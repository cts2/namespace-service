package edu.mayo.cts2.framework.plugin.namespace.mvc;


public class JTablesErrorResponse extends JTablesResponse {
	
	private static String ERROR_RESPONSE = "ERROR";
	
	private String Message;

	public JTablesErrorResponse(String errorMessage) {
		super();
		this.setResult(ERROR_RESPONSE);
		this.Message = errorMessage;
	}

	public String getMessage() {
		return Message;
	}

	public void setMessage(String message) {
		Message = message;
	}
	
}