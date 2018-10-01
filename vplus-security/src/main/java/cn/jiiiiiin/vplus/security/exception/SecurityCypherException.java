package cn.jiiiiiin.vplus.security.exception;

/**
 * @author jiiiiiin
 */
public class SecurityCypherException extends Exception {

	private String code;

	public String getCode() {
		return code;
	}

	public SecurityCypherException() {
	}

	public SecurityCypherException(String message) {
		super(message);
	}

	public SecurityCypherException(String message, Throwable cause) {
		super(message, cause);
	}

	public SecurityCypherException(Throwable cause) {
		super(cause);
	}

	public SecurityCypherException(String message, String code) {
		super(message);
		this.code = code;
	}

}
