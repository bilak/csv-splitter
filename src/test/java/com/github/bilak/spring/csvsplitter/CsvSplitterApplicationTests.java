package com.github.bilak.spring.csvsplitter;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;

import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(properties = {
		"app.split.inputFile=src/test/resources//FL_insurance_sample.csv",
		"app.split.splitBy=3,16",
		"app.split.splitByType=COLUMN_INDEX",
		"app.split.orderBy=3,16",
		"app.split.orderByType=COLUMN_INDEX"
})
public class CsvSplitterApplicationTests {

	@Autowired
	private NamedParameterJdbcOperations jdbcTemplate;

	@Test
	public void testImportHasCorrectNumberOfRecords() {
		Long count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM T_IMPORT", (Map) null, Long.class);
		assertTrue("database should contain 36634 records", 36634L == count);
	}

}
