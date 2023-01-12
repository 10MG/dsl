package cn.tenmg.dsl.exception;

/**
 * 宏执行异常
 * 
 * @author June wjzhao@aliyun.com
 * 
 * @since 1.3.0
 *
 */
public class MacroException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5416303486009981590L;

	public MacroException(String massage) {
		super(massage);
	}

	public MacroException(String massage, Throwable cause) {
		super(massage, cause);
	}
}
