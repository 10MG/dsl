package cn.tenmg.dsl.exception;

/**
 * 类型转换异常
 * 
 * @author June wjzhao@aliyun.com
 * 
 * @since 1.3.1
 */
public class TypeConvertException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -287583545030856360L;

	public TypeConvertException(String massage) {
		super(massage);
	}

	public TypeConvertException(String massage, Throwable cause) {
		super(massage, cause);
	}
}
