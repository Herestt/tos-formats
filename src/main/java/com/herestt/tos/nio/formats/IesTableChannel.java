package com.herestt.tos.nio.formats;

import java.io.IOException;
import java.nio.ByteOrder;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.herestt.common.io.FileContent;
import com.herestt.tos.nio.formats.IesFileInfo.ColumnInfo;

/**
 * An {@link IesTable} implementation.
 * 
 * <p>This class deal with {@code .ies} files by connecting a channel to it. This means
 * that every actions on the {@link IesTable} object are directly affected to the file.</p> 
 * 
 * @author Herestt
 *
 */
public class IesTableChannel implements IesTable<Integer, String, String> {
	
	/** The {@code .ies} file content descriptor. */
	private IesFileInfo fileInfo;
	
	/** The {@code .ies} file columns descriptors. */
	private List<ColumnInfo> columnsInfo;
	
	/** The channel connected to the {@code .ies} file. */
	private SeekableByteChannel sbc;
	
	protected IesTableChannel() {}
	
	@Override
	public void init(Path path, IesFileInfo fileInfo,
			List<ColumnInfo> columnsInfo) throws IOException {
		this.fileInfo = fileInfo;
		this.columnsInfo = columnsInfo;
		sbc = Files.newByteChannel(path);
	}

	@Override
	public void close() throws IOException {
		if(sbc != null && sbc.isOpen())
			sbc.close();
	}

	@Override
	public Iterator<Map<Integer, Map<String, String>>> iterator() {
		return new IteratorImpl();
	}
	
	@Override
	public void clear() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Map<Integer, String> column(String columnKey) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> columnKeySet() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean contains(Integer rowKey, String columnKey) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean containsColumn(String columnsKey) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean containsRow(Integer rowKey) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean containsValue(String value) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String get(Integer rowKey, String columnKey) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IesDataType dataType(String columnKey) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String put(Integer rowKey, String columnKey, String value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void putAll(
			IesTable<? extends Integer, ? extends String, ? extends String> table) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void remove(Integer rowKey) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Map<String, String> row(Integer rowKey) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Integer> rowKeySet() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long size() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	private class IteratorImpl implements Iterator<Map<Integer, Map<String, String>>> {

		private long currentPosition;
		private long currentRow;
		private Map<Integer, Map<String, String>> next;
		
		IteratorImpl() {
			currentPosition = fileInfo.getFileSize() - fileInfo.getContenteSize();
			currentRow = 0;
		}
		
		private Map<Integer, Map<String, String>> process() throws IOException {
			FileContent.access(sbc, currentPosition);
			FileContent.order(ByteOrder.LITTLE_ENDIAN);
			Map<Integer, Map<String, String>> row = new HashMap<>();
			Map<String, String> columns = new HashMap<>();
			long rowKey = FileContent.read().asUnsignedInt();
			for(ColumnInfo ci : columnsInfo) {
				String value;
				if(ci.getDataType() == IesDataType.NUMERIC)
					value = Float.toString(FileContent.read().asFloat());
				else if(ci.getDataType() == IesDataType.STRING) {
					int stringLength = FileContent.read().asUnsignedShort();
					value = FileContent.read().asXorString(stringLength, XOR_STRING_KEY);
					value = new String(value.getBytes("ISO-8859-1"), "UTF-8");			//TODO - Herestt : optimization.
				} 
				else
					throw new IllegalArgumentException("Unknown data type.");
				columns.put(ci.getName(), value);
			}
			FileContent.skip(fileInfo.getStringColumnCount());
			currentPosition = sbc.position();
			row.put((int) rowKey, columns);
			return row;
		}
		
		@Override
		public boolean hasNext() {
			if(currentRow == fileInfo.getRowCount())
				return false;
			try {
				if((next = process()) == null)
					return false;
			} catch (IOException e) {
				return false;
			}
			currentRow++;
			return true;
		}

		@Override
		public Map<Integer, Map<String, String>> next() {
			return next;
		}
	}
}