package cn.tenmg.dsl.exception;

/**
 * 时间格式化异常
 * 
 * @author June wjzhao@aliyun.com
 * 
 * @since 1.3.0
 *
 */
public class DateFormatException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1572430933891193170L;

	public DateFormatException(String massage) {
		super(massage);
	}

	public DateFormatException(String massage, Throwable cause) {
		super(massage, cause);
	}

}
