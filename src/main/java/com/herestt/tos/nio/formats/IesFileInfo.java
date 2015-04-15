package com.herestt.tos.nio.formats;

public class IesFileInfo {

	private String name;
	private long headerSize;
	private long contenteSize;
	private long fileSize;
	private int rowCount;
	private int columnCount;
	private int numericColumnCount;
	private int stringColumnCount;
	
	protected IesFileInfo(String name, long headerSize, long contenteSize,
			long fileSize, int rowCount, int columnCount,
			int numericColumnCount, int stringColumnCount) {
		super();
		this.name = name;
		this.headerSize = headerSize;
		this.contenteSize = contenteSize;
		this.fileSize = fileSize;
		this.rowCount = rowCount;
		this.columnCount = columnCount;
		this.numericColumnCount = numericColumnCount;
		this.stringColumnCount = stringColumnCount;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getHeaderSize() {
		return headerSize;
	}

	public void setHeaderSize(long headerSize) {
		this.headerSize = headerSize;
	}

	public long getContenteSize() {
		return contenteSize;
	}

	public void setContenteSize(long contenteSize) {
		this.contenteSize = contenteSize;
	}

	public long getFileSize() {
		return fileSize;
	}

	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}

	public int getRowCount() {
		return rowCount;
	}

	public void setRowCount(int rowCount) {
		this.rowCount = rowCount;
	}

	public int getColumnCount() {
		return columnCount;
	}

	public void setColumnCount(int columnCount) {
		this.columnCount = columnCount;
	}

	public int getNumericColumnCount() {
		return numericColumnCount;
	}

	public void setNumericColumnCount(int numericColumnCount) {
		this.numericColumnCount = numericColumnCount;
	}

	public int getStringColumnCount() {
		return stringColumnCount;
	}

	public void setStringColumnCount(int stringColumnCount) {
		this.stringColumnCount = stringColumnCount;
	}
	
	protected static class ColumnInfo implements Comparable<ColumnInfo> {
		
		private String name;
		private String alternativeName;
		private IesDataType dataType;
		private int unknownA;
		private int unknownB;
		private int order;
		
		public ColumnInfo(String name, String alternativeName,
				IesDataType dataType, int unknownA, int unknownB, int order) {
			super();
			this.name = name;
			this.alternativeName = alternativeName;
			this.dataType = dataType;
			this.unknownA = unknownA;
			this.unknownB = unknownB;
			this.order = order;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getAlternativeName() {
			return alternativeName;
		}

		public void setAlternativeName(String alternativeName) {
			this.alternativeName = alternativeName;
		}

		public IesDataType getDataType() {
			return dataType;
		}

		public void setDataType(IesDataType dataType) {
			this.dataType = dataType;
		}

		public int getUnknownA() {
			return unknownA;
		}

		public void setUnknownA(int unknownA) {
			this.unknownA = unknownA;
		}

		public int getUnknownB() {
			return unknownB;
		}

		public void setUnknownB(int unknownB) {
			this.unknownB = unknownB;
		}

		public int getOrder() {
			return order;
		}

		public void setOrder(int order) {
			this.order = order;
		}

		@Override
		public int compareTo(ColumnInfo o) {
			if(dataType != o.dataType)
				throw new IllegalArgumentException("Only data of the same type can be compared.");
			return Integer.compare(order, o.order);
		}

		@Override
		public String toString() {
			return "ColumnInfo [dataType=" + dataType + ", unknownA="
					+ unknownA + ", unknownB=" + unknownB + ", order=" + order
					+ ", name=" + name + ", alternativeName=" + alternativeName
					+ "]";
		}
	}
}
