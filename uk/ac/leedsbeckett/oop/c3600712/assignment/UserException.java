package uk.ac.leedsbeckett.oop.c3600712.assignment;

public class UserException extends Exception {
	private static final long serialVersionUID = 1L;
	private String message;
	public UserException(String exceptionMessage) {
		message = exceptionMessage.length() > 1 ? exceptionMessage : "No error provided";
	}
	
	public String getMessage() {
		return message;
	}
}
