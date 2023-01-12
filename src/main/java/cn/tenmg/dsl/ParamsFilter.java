package cn.tenmg.dsl;

/**
 * 参数过滤器
 * 
 * @author June wjzhao@aliyun.com
 * 
 * @since 1.3.0
 */
public interface ParamsFilter {

	public static final String ALL = "*";

	/**
	 * 对参数名和参数值进行判断，确定参数是否需要过滤掉
	 * 
	 * @param name
	 *            参数名
	 * @param value
	 *            参数值
	 * @return 参数需要过滤返回true，否则返回false
	 */
	boolean determine(String name, Object value);

}
