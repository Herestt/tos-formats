package com.herestt.tos.nio.formats;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

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

	protected IesTableChannel() {}
	
	@Override
	public void init(Path path, IesFileInfo fileInfo,
			SortedSet<ColumnInfo> columnsInfo) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void close() throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Iterator<Map<Integer, Map<String, String>>> iterator() {
		// TODO Auto-generated method stub
		return null;
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

		@Override
		public boolean hasNext() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public Map<Integer, Map<String, String>> next() {
			// TODO Auto-generated method stub
			return null;
		}
	}
}