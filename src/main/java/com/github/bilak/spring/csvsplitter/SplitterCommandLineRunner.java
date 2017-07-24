package com.github.bilak.spring.csvsplitter;

import com.github.bilak.spring.csvsplitter.service.SplitDefinition;
import com.github.bilak.spring.csvsplitter.service.SplitterService;
import org.springframework.boot.CommandLineRunner;

import java.util.List;

/**
 * @author lvasek.
 */
public class SplitterCommandLineRunner implements CommandLineRunner {

	private SplitterService splitterService;

	public SplitterCommandLineRunner(SplitterService splitterService) {
		this.splitterService = splitterService;
	}

	@Override
	public void run(String... strings) throws Exception {
		splitterService.importFile();
		List<SplitDefinition> splitDefinitions = splitterService.getSplitDefinitions();
		splitterService.executeSplit(splitDefinitions);
	}
}
