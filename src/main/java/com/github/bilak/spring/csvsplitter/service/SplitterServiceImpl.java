package com.github.bilak.spring.csvsplitter.service;

import com.github.bilak.spring.csvsplitter.configuration.CsvProperties;
import com.github.bilak.spring.csvsplitter.configuration.SplitterProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.lang.String.format;
import static java.util.stream.Collectors.toMap;

/**
 * @author lvasek.
 */
public class SplitterServiceImpl implements SplitterService {

	private static final Logger logger = LoggerFactory.getLogger(SplitterServiceImpl.class);

	private String IMPORT_TABLE;

	private static final String emptySpace = " ";

	private SplitterProperties splitterProperties;
	private NamedParameterJdbcOperations jdbcTemplate;

	public SplitterServiceImpl(SplitterProperties splitterProperties, NamedParameterJdbcOperations jdbcTemplate) {
		this.splitterProperties = splitterProperties;
		this.jdbcTemplate = jdbcTemplate;
		this.IMPORT_TABLE = StringUtils.isEmpty(splitterProperties.getImportTableName()) ?
				SplitterProperties.DEFAULT_IMPORT_TABLE :
				splitterProperties.getImportTableName();
	}

	@Override
	public void importFile() {
		if (splitterProperties.getInputFile().exists()) {
			logger.debug("Going to import resource [{}]", splitterProperties.getInputFile().getFile().getAbsolutePath());
			importResource(splitterProperties.getInputFile());
			//getSplitDefinitions();
		} else {
			logger.error("Resource [{}] does not exists", splitterProperties.getInputFile());
		}
	}

	private void importResource(Resource resource) {
		String resourcePath = getResourcePath(resource);
		String query = format("CREATE TABLE %s AS SELECT * FROM CSVREAD('", IMPORT_TABLE)
				.concat(resourcePath).concat("', NULL, ")
				.concat(getInputCsvStringDecode())
				.concat(")");
		logger.debug("Using query [{}]", query);
		jdbcTemplate.update(query, (Map) null);

		if (logger.isDebugEnabled()) {
			Integer count = jdbcTemplate.queryForObject(format("select count(*) from %s ", IMPORT_TABLE), (Map) null, Integer.class);
			logger.debug("Imported {} records", count);
		}
	}

	@Override
	public List<SplitDefinition> getSplitDefinitions() {
		List<String> tableColumns = getTableColumns();
		logger.debug("Table columns {}", tableColumns.toString());
		List<String> splitColumns = getSplitColumns(tableColumns);
		List<String> orderColumns = getOrderColumns(tableColumns);
		if (!splitColumns.isEmpty()) {
			return getSplitDefinitions(splitColumns, orderColumns);
		} else {
			throw new RuntimeException("No importFile columns defined");
		}
	}

	@Override
	public void executeSplit(List<SplitDefinition> definitions) {
		IntStream.range(0, definitions.size())
				.forEach(idx -> {
					jdbcTemplate.execute(definitions.get(idx).getSplitQuery(), (PreparedStatementCallback<Object>) ps -> ps.execute());
				});
	}

	private List<SplitDefinition> getSplitDefinitions(List<String> splitColumns, List<String> orderColumns) {
		String splitBy = splitColumns.stream().collect(Collectors.joining(","));
		String orderBy = Optional.ofNullable(orderColumns.stream().collect(Collectors.joining(",")))
				.filter(ob -> !StringUtils.isEmpty(ob))
				.map(ob -> " ORDER BY ".concat(ob))
				.orElse(null);
		String sql = "SELECT DISTINCT ".concat(splitBy).concat(" FROM %s ");
		sql = format(sql, IMPORT_TABLE);
		sql = StringUtils.isEmpty(orderBy) ? sql : sql.concat(orderBy);

		List<Map<String, Object>> result = jdbcTemplate.queryForList(sql, (Map) null);
		return IntStream
				.range(0, result.size())
				.mapToObj(idx -> {
					String query = "CALL CSVWRITE('"
							.concat(createSplitFileName(result.get(idx), idx, result.size()))
							.concat("', '")
							.concat(createWhereQuery(result.get(idx), orderBy))
							.concat("', ")
							.concat(getOutputCsvStringDecode())
							.concat(")");
					logger.debug("Output query [{}]", query);
					return new SplitDefinition(result.get(idx), query);
				})
				.collect(Collectors.toList());

	}

	private String createSplitFileName(Map<String, Object> rowData, Integer index, Integer filesCount) {
		// TODO play with SPEL and add option to configure output file name with it
		String fileName =
				rowData
						.keySet()
						.stream()
						.collect(Collectors.joining("_"))
						.concat("_")
						.concat(format("%0" + String.valueOf(filesCount).length() + "d", index))
						.concat(".csv");
		if (!splitterProperties.getOutputDirectory().toFile().exists()) {
			if (!splitterProperties.getOutputDirectory().toFile().mkdirs()) {
				throw new RuntimeException(format("Output directory %s does not exists", splitterProperties.getOutputDirectory().toFile().getAbsolutePath()));
			}
		}
		return Paths.get(splitterProperties.getOutputDirectory().toString(), fileName).toString();
	}

	private String createWhereQuery(Map<String, Object> properties, String orderBy) {
		String sql = format("SELECT * FROM %s WHERE 1 = 1 ", IMPORT_TABLE);
		String where = properties
				.keySet()
				.stream()
				.map(key -> key.concat(" = ''")
						.concat(properties.get(key)
								.toString()
								.replaceAll("'", "''")
								.replaceAll("\"", "\\\"")) // todo replace also double quotes "
						.concat("''"))
				.collect(Collectors.joining(" AND "));
		String query =
				(StringUtils.isEmpty(where) ?
						sql :
						sql.concat(" AND ").concat(where));
		return StringUtils.isEmpty(orderBy) ? query : query.concat(" ").concat(orderBy);
	}

	private String getResourcePath(Resource resource) {
		try {
			return resource.getFile().getAbsolutePath();
		} catch (IOException e) {
			throw new RuntimeException("Unable to retrieve resource's path", e);
		}
	}

	private List<String> getSplitColumns(List<String> tableColumns) {
		if (splitterProperties.getSplitByType() != null && splitterProperties.getSplitBy() != null) {
			switch (splitterProperties.getSplitByType()) {
				case COLUMN_NAME:
					return splitterProperties
							.getSplitBy()
							.stream()
							.map(prop -> tableColumns
									.stream()
									.filter(col -> col.equalsIgnoreCase(prop))
									.findFirst()
									.orElseThrow(() -> new RuntimeException(format("Property [%s] is not defined", prop))))
							.collect(Collectors.toList());
				case COLUMN_INDEX:
					return splitterProperties
							.getSplitBy()
							.stream()
							.map(prop -> Integer.valueOf(prop))
							.map(idx -> {
								if (idx + 1 <= tableColumns.size() - 1)
									return tableColumns.get(idx - 1);
								else
									throw new RuntimeException(format("Property index [%d] does not exists", idx));
							})
							.collect(Collectors.toList());
			}
		}
		return Collections.emptyList();
	}

	private List<String> getOrderColumns(List<String> tableColumns) {
		if (splitterProperties.getOrderByType() != null && splitterProperties.getOrderBy() != null) {
			switch (splitterProperties.getOrderByType()) {
				case COLUMN_NAME:
					return
							splitterProperties
									.getOrderBy()
									.stream()
									.map(prop -> tableColumns
											.stream()
											.filter(col -> col.equalsIgnoreCase(prop))
											.findFirst()
											.orElseThrow(() -> new RuntimeException(format("Property [%s] is not defined", prop))))
									.collect(Collectors.toList());
				case COLUMN_INDEX:
					return splitterProperties
							.getOrderBy()
							.stream()
							.map(prop -> Integer.valueOf(prop))
							.map(idx -> {
								if (idx + 1 <= tableColumns.size() - 1)
									return tableColumns.get(idx - 1);
								else
									throw new RuntimeException(format("Property index [%d] does not exists", idx));
							})
							.collect(Collectors.toList());
			}
		}
		return Collections.emptyList();
	}

	private String getInputCsvStringDecode() {
		return "STRINGDECODE('".concat(getCsvStringDecode(splitterProperties.getInputCsv())).concat("')");
	}

	private String getOutputCsvStringDecode() {
		return "STRINGDECODE('".concat(getCsvStringDecode(splitterProperties.getOutputCsv())).concat("')");
	}

	private String getCsvStringDecode(CsvProperties props) {
		StringBuilder sb = new StringBuilder();
		sb.append("caseSensitiveColumnNames=").append(props.isCaseSensitiveColumnNames()).append(emptySpace);
		if (!StringUtils.isEmpty(props.getCharSet()))
			sb.append("charset=").append(props.getCharSet()).append(emptySpace);
		if (!StringUtils.isEmpty(props.getEscape()))
			sb.append("escape=").append(props.getEscape()).append(emptySpace);
		if (!StringUtils.isEmpty(props.getFieldDelimiter()))
			sb.append("fieldDelimiter=").append(props.getFieldDelimiter()).append(emptySpace);
		if (!StringUtils.isEmpty(props.getFieldSeparator()))
			sb.append("fieldSeparator=").append(props.getFieldSeparator()).append(emptySpace);
		if (!StringUtils.isEmpty(props.getLineSeparator()))
			sb.append("lineSeparator=").append(props.getLineSeparator()).append(emptySpace);
		sb.append("preserveWhitespace=").append(props.isPreserveWhitespace()).append(emptySpace);
		sb.append("writeColumnHeader=").append(props.isWriteColumnHeader()).append(emptySpace);

		return sb.toString()
				//.replaceAll("'", "''")
				//.replaceAll("\"", "\\\"") // todo escape here or provide correct in input?
				;
	}

	private List<String> getTableColumns() {
		return jdbcTemplate.queryForList(
				"SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = :TABLE_NAME ORDER BY ORDINAL_POSITION",
				tableNameParams(),
				String.class);
	}

	private Map<String, Object> queryParams(Map.Entry<String, Object>... entries) {
		return Stream
				.of(entries)
				.collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
	}

	private Map<String, Object> tableNameParams() {
		return queryParams(new AbstractMap.SimpleEntry("TABLE_NAME", IMPORT_TABLE));
	}


	// TODO remove this when SPEL is implemented
	public static void main(String[] args) throws NoSuchMethodException {
		StandardEvaluationContext context = new StandardEvaluationContext();

		context.setVariable("VAR1", "this is my variable ");
		context.setVariable("VAR2", Integer.valueOf(1));
		context.setVariable("VAR3", Integer.valueOf(10));
		context.setVariable("index", 0);

		ReflectionUtils.doWithMethods(StringUtils.class, method -> {
			if (Modifier.isPublic(method.getModifiers()))
				context.registerFunction(method.getName(), method);
		});

		ExpressionParser parser = new SpelExpressionParser();
		Expression exp = parser.parseExpression("#index + '_' + #VAR2 + '_' + #VAR1");
		System.out.println(exp.getValue(context).toString());
		System.out.println(parser.parseExpression("#trimAllWhitespace(#VAR1) + ' '+ #index").getValue(context).toString());

		System.out.println(StringUtils.trimAllWhitespace("this is test"));

		System.out.println(String.format("%04d", 10));

	}
}
