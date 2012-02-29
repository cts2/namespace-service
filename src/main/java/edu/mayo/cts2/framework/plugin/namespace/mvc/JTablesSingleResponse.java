package edu.mayo.cts2.framework.plugin.namespace.mvc;


public class JTablesSingleResponse extends JTablesResponse {
		
	private Object Record;
	
	public JTablesSingleResponse() {
		super();
	}

	public JTablesSingleResponse(Object record) {
		super();
		Record = record;
	}

	public Object getRecord() {
		return Record;
	}

	public void setRecord(Object record) {
		Record = record;
	}

}