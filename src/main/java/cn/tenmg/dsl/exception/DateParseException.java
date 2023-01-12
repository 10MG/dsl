package cn.tenmg.dsl.exception;

/**
 * 时间解析异常
 * 
 * @author June wjzhao@aliyun.com
 * 
 * @since 1.3.0
 *
 */
public class DateParseException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7813646639495607803L;

	public DateParseException(String massage) {
		super(massage);
	}

	public DateParseException(String massage, Throwable cause) {
		super(massage, cause);
	}
}
