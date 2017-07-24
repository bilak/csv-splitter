package com.github.bilak.spring.csvsplitter.service;

import java.util.Map;
import java.util.Objects;

/**
 * @author lvasek.
 */
public class SplitDefinition {
	private Map<String, Object> splitParameters;
	private String splitQuery;

	public SplitDefinition(Map<String, Object> splitParameters, String splitQuery) {
		this.splitParameters = splitParameters;
		this.splitQuery = splitQuery;
	}

	public Map<String, Object> getSplitParameters() {
		return splitParameters;
	}

	public String getSplitQuery() {
		return splitQuery;
	}

	@Override
	public int hashCode() {
		return Objects.hash(splitParameters, splitQuery);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		final SplitDefinition other = (SplitDefinition) obj;
		return Objects.equals(this.splitParameters, other.splitParameters)
				&& Objects.equals(this.splitQuery, other.splitQuery);
	}

	@Override
	public String toString() {
		return "SplitDefinition{" +
				"splitParameters=" + splitParameters +
				", splitQuery='" + splitQuery + '\'' +
				'}';
	}
}
