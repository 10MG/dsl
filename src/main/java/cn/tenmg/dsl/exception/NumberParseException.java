package cn.tenmg.dsl.exception;

/**
 * 数字解析异常
 * 
 * @author June wjzhao@aliyun.com
 * 
 * @since 1.3.0
 */
public class NumberParseException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1549629736417575932L;

	public NumberParseException(String massage) {
		super(massage);
	}

	public NumberParseException(String massage, Throwable cause) {
		super(massage, cause);
	}
}
