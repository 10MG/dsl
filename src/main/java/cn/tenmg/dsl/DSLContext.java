package cn.tenmg.dsl;

import java.util.List;

/**
 * 动态脚本语言上下文
 * 
 * @author June wjzhao@aliyun.com
 * 
 * @since 1.3.0
 */
public interface DSLContext {

	/**
	 * 获取参数转换器列表
	 * 
	 * @return 参数转换器列表
	 */
	List<ParamsConverter<?>> getParamsConverters();

	/**
	 * 获取参数过滤器列表
	 * 
	 * @return 参数过滤器列表
	 */
	List<ParamsFilter> getParamsFilters();

}
