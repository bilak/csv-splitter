package com.github.bilak.spring.csvsplitter.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.FileSystemResource;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * @author lvasek.
 */
@ConfigurationProperties(prefix = "app.split")
public class SplitterProperties {

	public static final String DEFAULT_IMPORT_TABLE = "T_IMPORT";

	public enum OrderByType {
		COLUMN_NAME,
		COLUMN_INDEX;
	}

	public enum SplitByType {
		COLUMN_NAME,
		COLUMN_INDEX;
	}

	/**
	 * Name of table to which csv will be imported
	 */
	private String importTableName = DEFAULT_IMPORT_TABLE;

	/**
	 * Input csv file
	 */
	private FileSystemResource inputFile;
	/**
	 * Output directory where files will be generated
	 */
	private Path outputDirectory = Paths.get("/tmp");
	/**
	 * Column names or column indexes by which will be csv importFile
	 */
	private List<String> splitBy;
	/**
	 * Choose whether importFile by column name or column index (if using index first is 1)
	 */
	private SplitByType splitByType = SplitByType.COLUMN_NAME;
	/**
	 * Column names or column indexes by which will be csv importFile ordered
	 */
	private List<String> orderBy;
	/**
	 * Choose whether order by column name or column index (if using index first is 1)
	 */
	private OrderByType orderByType = OrderByType.COLUMN_NAME;

	/**
	 * Input csv configuration
	 */
	private CsvProperties inputCsv;
	/**
	 * Output csv configuration
	 */
	private CsvProperties outputCsv;

	/*
		GET/SET
	 */
	public FileSystemResource getInputFile() {
		return inputFile;
	}

	public String getImportTableName() {
		return importTableName;
	}

	public void setImportTableName(String importTableName) {
		this.importTableName = importTableName;
	}

	public void setInputFile(FileSystemResource inputFile) {
		this.inputFile = inputFile;
	}

	public Path getOutputDirectory() {
		return outputDirectory;
	}

	public void setOutputDirectory(Path outputDirectory) {
		this.outputDirectory = outputDirectory;
	}

	public List<String> getSplitBy() {
		return splitBy;
	}

	public void setSplitBy(List<String> splitBy) {
		this.splitBy = splitBy;
	}

	public SplitByType getSplitByType() {
		return splitByType;
	}

	public void setSplitByType(SplitByType splitByType) {
		this.splitByType = splitByType;
	}

	public List<String> getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(List<String> orderBy) {
		this.orderBy = orderBy;
	}

	public OrderByType getOrderByType() {
		return orderByType;
	}

	public void setOrderByType(OrderByType orderByType) {
		this.orderByType = orderByType;
	}

	public CsvProperties getInputCsv() {
		return inputCsv;
	}

	public void setInputCsv(CsvProperties inputCsv) {
		this.inputCsv = inputCsv;
	}

	public CsvProperties getOutputCsv() {
		return outputCsv;
	}

	public void setOutputCsv(CsvProperties outputCsv) {
		this.outputCsv = outputCsv;
	}

	@Override
	public String toString() {
		return "SplitterProperties{" +
				"inputFile=" + inputFile +
				", outputDirectory=" + outputDirectory +
				", splitBy=" + splitBy +
				", splitByType=" + splitByType +
				", orderBy=" + orderBy +
				", orderByType=" + orderByType +
				", inputCsv=" + inputCsv +
				", outputCsv=" + outputCsv +
				'}';
	}
}
