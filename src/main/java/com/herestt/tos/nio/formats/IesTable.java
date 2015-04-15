package com.herestt.tos.nio.formats;

import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteOrder;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import com.herestt.common.io.FileContent;
import com.herestt.tos.nio.formats.IesFileInfo.ColumnInfo;

/**
 * An interface class that wraps an {@code .ies} file so that it can be handled as a
 * collection.
 * 
 * <p>As data is stored within a file, the access must first be done by calling the
 * {@link IesTable#open(Path)} method.
 * This one is in charge of creating an {@link IesFileInfo} object, and a {@link Set} of
 * {@link IesFileInfo.ColumnInfo} that describe the format of the file.</p>
 * 
 * <p>So, an implementation of an {@link IesTable} has to use the {@link IesFileInfo},
 * and a {@link Set} of {@link IesFileInfo.ColumnInfo} that will be served through the
 * {@link IesTable#init(IesFileInfo, Set)} method so as to let the other specified
 * ones take place.</p> 
 * 
 * @author Herestt
 *
 * @param <R> - the type of the table row keys.
 * @param <C> - the type of the table column keys.
 * @param <V> - the type of the mapped values.
 */
public interface IesTable<R,C,V> extends Closeable, Iterable<Map<R, Map<C,V>>> {
	
	static final int TABLE_NAME_SIZE = 128;
	static final int HEADER_SEPARATOR_SIZE = 2;
	static final int FILE_DESCRIPTION_SIZE = 156;
	static final int COLUMN_NAME_SIZE = 64;
	static final int XOR_STRING_KEY = 1;
	
	/**
	 * Opens a {@code .ies} file.
	 * 
	 * Once the file is parsed this method will serve an {@link IesFileInfo},
	 * and a {@link Set} of {@link IesFileInfo.ColumnInfo} that describes the
	 * opened file content to the {@link IesTable#init(Path, IesFileInfo, Set)}
	 * method.
	 * 
	 * @param path - the {@code .ies} file path.
	 * @return the table connected to the file.
	 * @throws IOException - if an I/O error occurs.
	 */
	@SuppressWarnings("unchecked")
	public static <R,C,V,T extends IesTable<R,C,V>> T open(Path path, Class<T> type) throws IOException, InstantiationException, IllegalAccessException {
		IesTable<R, C, V> table = null;		
		try(SeekableByteChannel sbc = Files.newByteChannel(path)) {
			IesFileInfo fileInfo = createFileInfo(sbc);
			List<IesFileInfo.ColumnInfo> columnsInfo = createColumnInfosList(fileInfo, sbc);
			table = type.newInstance();
			table.init(path, fileInfo, columnsInfo);
		}
		return (T) table;
	}
	
	static IesFileInfo createFileInfo(SeekableByteChannel sbc) throws IOException {
		FileContent.access(sbc, 0);
		FileContent.order(ByteOrder.LITTLE_ENDIAN);
		IesFileInfo fileInfo = new IesFileInfo(
				FileContent.read().asString(TABLE_NAME_SIZE),
				FileContent.skip(HEADER_SEPARATOR_SIZE * 2).read().asUnsignedInt(),
				FileContent.read().asUnsignedInt(),
				FileContent.read().asUnsignedInt(),
				FileContent.skip(HEADER_SEPARATOR_SIZE).read().asUnsignedShort(),
				FileContent.read().asUnsignedShort(),
				FileContent.read().asUnsignedShort(),
				FileContent.read().asUnsignedShort());
		FileContent.skip(HEADER_SEPARATOR_SIZE);
		return fileInfo;
	}
	
	static List<IesFileInfo.ColumnInfo> createColumnInfosList(IesFileInfo fileInfo, SeekableByteChannel sbc) throws IOException {
		List<IesFileInfo.ColumnInfo> numericColumnsInfo = new ArrayList<>();
		List<IesFileInfo.ColumnInfo> stringColumnsInfo = new ArrayList<>();
		List<IesFileInfo.ColumnInfo> columnsInfo = new ArrayList<>();
		FileContent.access(sbc, FILE_DESCRIPTION_SIZE);
		FileContent.order(ByteOrder.LITTLE_ENDIAN);
		for(int i = 0; i < fileInfo.getColumnCount(); i++) {
			IesFileInfo.ColumnInfo ci = new IesFileInfo.ColumnInfo(
					FileContent.read().asXorString(COLUMN_NAME_SIZE, XOR_STRING_KEY),
					FileContent.read().asXorString(COLUMN_NAME_SIZE, XOR_STRING_KEY),
					toDataType(FileContent.read().asUnsignedShort()),
					FileContent.read().asUnsignedShort(),
					FileContent.read().asUnsignedShort(),
					FileContent.read().asUnsignedShort());
			if(ci.getDataType() == IesDataType.NUMERIC)
				numericColumnsInfo.add(ci);
			if(ci.getDataType() == IesDataType.STRING)
				stringColumnsInfo.add(ci);
		}
		Collections.sort(numericColumnsInfo);
		Collections.sort(stringColumnsInfo);
		columnsInfo.add(stringColumnsInfo.get(0));	// Add the 'ClassName' column.
		columnsInfo.addAll(numericColumnsInfo);		// Add numeric columns.
		columnsInfo.addAll(stringColumnsInfo);		// Add string columns.
		return columnsInfo;
	}
	
	static IesDataType toDataType(int type) {
		if(type == 0)
			return IesDataType.NUMERIC;
		else if(type == 1 
				|| type == 2)
			return IesDataType.STRING;
		else
			throw new IllegalArgumentException("This data type is not recognize.");
	}
	
	/**
	 * Initializes the table object.
	 * 
	 * @param path - the {@code .ies} file path.
	 * @param fileInfo - the description of file content.
	 * @param columnsInfo - the description of the each column of the table.
	 */
	public void init(Path path, IesFileInfo fileInfo,
			List<IesFileInfo.ColumnInfo> columnsInfo) throws IOException;
	
	/**
	 * Empties the table.
	 */
	public void clear();
	
	/**
	 * Gets all mapped values according to a column key.
	 * 
	 * @param columnKey - the key of the column.
	 * @return the corresponding map.
	 */
	public Map<R,V> column(C columnKey);
	
	/**
	 * Build a {@link Set} that will contain table column keys.
	 * 
	 * @return a set of column keys.
	 */
	public Set<C> columnKeySet();
	
	/**
	 * Checks whether a row key and a column key map a value.
	 * 
	 * @param rowKey - the key of the row.
	 * @param columnKey - the key of the column.
	 * 
	 * @return {@code true} if the value is mapped, and {@code true} otherwise.
	 */
	public boolean contains(R rowKey, C columnKey);
	
	/**
	 * Checks whether a column key exists.
	 * 
	 * @param columnsKey - the column key the check.
	 * @return {@code true} if the column key exists, and {@code true} otherwise.
	 */
	public boolean containsColumn(C columnsKey);
	
	/**
	 * Checks whether a row key exists.
	 * 
	 * @param rowKey - the row key to check.
	 * @return {@code true} if the row key exists, and {@code true} otherwise.
	 */
	public boolean containsRow(R rowKey);
	
	/**
	 * Checks whether a value is mapped in the table.
	 * 
	 * @param value - the value to checks.
	 * @return {@code true} if the value is mapped, and {@code true} otherwise.
	 */
	public boolean containsValue(V value);
	
	/**
	 * Get the value mapped by the given row/column key pair.
	 * 
	 * @param rowKey - the key of the row.
	 * @param columnKey - the key of the column.
	 * @return the mapped value.
	 */
	public V get(R rowKey, C columnKey);
	
	/**
	 * Gets the {@link IesDataType} of a column.
	 * 
	 * @param columnKey - the key of the column.
	 * @return the {@link IesDataType} of the column.
	 */
	public IesDataType dataType(C columnKey);
	
	/**
	 * Checks whether the table is empty.
	 * 
	 * @return {@code true} if the table is empty, and {@code true} otherwise.
	 */
	public boolean isEmpty();
	
	/**
	 * Maps a value into the table according to a row/column key pair.
	 * 
	 * @param rowKey - the key of the row.
	 * @param columnKey - the key of the column.
	 * @param value - the value to map.
	 * @return the mapped value.
	 */
	public V put(R rowKey, C columnKey, V value);	
	
	/**
	 * Maps a whole table a the end of this one.
	 * 
	 * @param table - the table to map.
	 */
	public void putAll(IesTable<? extends R, ? extends C, ? extends V> table);
	
	/**
	 * Removes a row from the table.
	 * 
	 * @param rowKey - the key of the column to remove.
	 */
	public void remove(R rowKey);

	/**
	 * Gets a row.
	 * 
	 * @param rowKey - the key of the row.
	 * @return the row.
	 */
	public Map<C,V> row(R rowKey);
	
	/**
	 * Build a {@link Set} that will contains table row keys.
	 * 
	 * @return a {@link Set} of row keys.
	 */
	public Set<R> rowKeySet();
	
	/**
	 * Gets the size of the table.
	 * 
	 * @return the size of the table.
	 */
	public long size();
}