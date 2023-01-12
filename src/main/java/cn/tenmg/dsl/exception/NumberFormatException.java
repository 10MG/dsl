package cn.tenmg.dsl.exception;

/**
 * 数字格式化异常
 * 
 * @author June wjzhao@aliyun.com
 * 
 * @since 1.3.0
 *
 */
public class NumberFormatException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1572430933891193170L;

	public NumberFormatException(String massage) {
		super(massage);
	}

	public NumberFormatException(String massage, Throwable cause) {
		super(massage, cause);
	}

}
