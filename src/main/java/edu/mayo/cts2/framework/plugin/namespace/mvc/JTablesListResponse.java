package edu.mayo.cts2.framework.plugin.namespace.mvc;

import java.util.ArrayList;
import java.util.List;

public class JTablesListResponse extends JTablesResponse {
	
	private List<Object> Records = new ArrayList<Object>();
	
	private long TotalRecordCount;
	
	public JTablesListResponse() {
		super();
	}
	
	public JTablesListResponse(long totalNumOfRecords, List<Object> records) {
		super();
		Records = records;
		TotalRecordCount = totalNumOfRecords;
	}

	public List<?> getRecords() {
		return Records;
	}

	public void setRecords(List<Object> records) {
		Records = records;
	}

	public long getTotalRecordCount() {
		return TotalRecordCount;
	}

	public void setTotalRecordCount(long totalRecordCount) {
		TotalRecordCount = totalRecordCount;
	}
}