package com.herestt.tos.nio.formats;

/**
 * An enumeration of possible data type that can be mapped by an
 * {@code .ies} table.
 * 
 * @author Herestt
 *
 */
public enum IesDataType {
	/** Numeric values are represent by {@code float}s. */
	NUMERIC,
	/** Strings value are represented by a {@code length} then by many {@code chars}. */
	STRING
}
