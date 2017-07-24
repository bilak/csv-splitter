package com.github.bilak.spring.csvsplitter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
public class CsvSplitterApplication {

	public static void main(String[] args) {
		SpringApplication.run(CsvSplitterApplication.class, args);
	}
}
