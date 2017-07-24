package com.github.bilak.spring.csvsplitter.service;

import java.util.List;

/**
 * @author lvasek.
 */
public interface SplitterService {

	void importFile();

	List<SplitDefinition> getSplitDefinitions();

	void executeSplit(List<SplitDefinition> definitions);
}
