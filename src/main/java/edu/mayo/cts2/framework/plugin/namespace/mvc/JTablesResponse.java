package edu.mayo.cts2.framework.plugin.namespace.mvc;


public class JTablesResponse {
	
	private static String OK_RESPONSE = "OK";
	
	private String Result = OK_RESPONSE;

	public JTablesResponse() {
		super();
	}

	public String getResult() {
		return Result;
	}

	public void setResult(String result) {
		Result = result;
	}
}