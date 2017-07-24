package com.github.bilak.spring.csvsplitter.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author lvasek.
 */
@ConfigurationProperties
public class CsvProperties {

	private boolean caseSensitiveColumnNames = false;
	private String charSet = "UTF-8";
	/**
	 * the character that escapes the field delimiter
	 */
	private String escape;
	private String fieldDelimiter = "\\\"";
	private String fieldSeparator = ",";
	private String lineComment;
	/**
	 * the line separator used for writing; ignored for reading
	 */
	private String lineSeparator;
	private boolean preserveWhitespace = false;
	private boolean writeColumnHeader = true;

	public boolean isCaseSensitiveColumnNames() {
		return caseSensitiveColumnNames;
	}

	public void setCaseSensitiveColumnNames(boolean caseSensitiveColumnNames) {
		this.caseSensitiveColumnNames = caseSensitiveColumnNames;
	}

	public String getCharSet() {
		return charSet;
	}

	public void setCharSet(String charSet) {
		this.charSet = charSet;
	}

	public String getEscape() {
		return escape;
	}

	public void setEscape(String escape) {
		this.escape = escape;
	}

	public String getFieldDelimiter() {
		return fieldDelimiter;
	}

	public void setFieldDelimiter(String fieldDelimiter) {
		this.fieldDelimiter = fieldDelimiter;
	}

	public String getFieldSeparator() {
		return fieldSeparator;
	}

	public void setFieldSeparator(String fieldSeparator) {
		this.fieldSeparator = fieldSeparator;
	}

	public String getLineComment() {
		return lineComment;
	}

	public void setLineComment(String lineComment) {
		this.lineComment = lineComment;
	}

	public String getLineSeparator() {
		return lineSeparator;
	}

	public void setLineSeparator(String lineSeparator) {
		this.lineSeparator = lineSeparator;
	}

	public boolean isPreserveWhitespace() {
		return preserveWhitespace;
	}

	public void setPreserveWhitespace(boolean preserveWhitespace) {
		this.preserveWhitespace = preserveWhitespace;
	}

	public boolean isWriteColumnHeader() {
		return writeColumnHeader;
	}

	public void setWriteColumnHeader(boolean writeColumnHeader) {
		this.writeColumnHeader = writeColumnHeader;
	}

	@Override
	public String toString() {
		return "CsvProperties{" +
				"caseSensitiveColumnNames=" + caseSensitiveColumnNames +
				", charSet='" + charSet + '\'' +
				", escape='" + escape + '\'' +
				", fieldDelimiter='" + fieldDelimiter + '\'' +
				", fieldSeparator='" + fieldSeparator + '\'' +
				", lineComment='" + lineComment + '\'' +
				", lineSeparator='" + lineSeparator + '\'' +
				", preserveWhitespace=" + preserveWhitespace +
				", writeColumnHeader=" + writeColumnHeader +
				'}';
	}
}
