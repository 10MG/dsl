package cn.tenmg.dsl;

/**
 * 参数类型转换器
 * 
 * @param <T>
 *            结果类型
 * 
 * @author June wjzhao@aliyun.com
 * 
 * @since 1.3.0
 */
public interface ParamsConverter<T> {

	/**
	 * 使用给定的参数名，确定该参数是否需要进行转换
	 * 
	 * @param name
	 *            参数名
	 * @return 返回{@code true}，则表示该参数需要进行转换；否则，表示该参数不需要进行转换
	 */
	boolean determine(String name);

	/**
	 * 
	 * 将参数值转换为指定类型
	 * 
	 * @param value
	 *            参数值
	 * @return 转换后的参数值
	 */
	T convert(Object value);

}
