package com.github.bilak.spring.csvsplitter.configuration;

import com.github.bilak.spring.csvsplitter.SplitterCommandLineRunner;
import com.github.bilak.spring.csvsplitter.service.SplitterService;
import com.github.bilak.spring.csvsplitter.service.SplitterServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;

/**
 * @author lvasek.
 */
@Configuration
public class CsvSplitterConfiguration {

	private NamedParameterJdbcOperations jdbcTemplate;

	@Autowired
	public CsvSplitterConfiguration(NamedParameterJdbcOperations jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Bean
	@ConfigurationProperties(prefix = "app.split")
	SplitterProperties splitterProperties() {
		return new SplitterProperties();
	}


	@Bean
	CommandLineRunner splitterCommandLineRunner() {
		return new SplitterCommandLineRunner(splitterService());
	}

	@Bean
	SplitterService splitterService() {
		return new SplitterServiceImpl(splitterProperties(), jdbcTemplate);
	}

}
