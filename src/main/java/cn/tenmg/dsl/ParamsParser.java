package cn.tenmg.dsl;

import java.util.Map;

/**
 * 参数解析器
 * 
 * @author 赵伟均 wjzhao@aliyun.com
 * 
 * @since 1.2.0
 *
 * @param <T>
 *            目标参数类型
 */
public interface ParamsParser<T> {

	/**
	 * 实例化目标参数对象
	 * 
	 * @return 返回目标参数对象
	 */
	T newParams();

	/**
	 * 解析参数并组织脚本
	 * 
	 * @param scriptBuilder
	 *            脚本构建器
	 * @param params
	 *            参数查找表
	 * @param paramName
	 *            当前解析参数名
	 * @param targetParams
	 *            目标参数对象
	 */
	void parse(StringBuilder scriptBuilder, Map<String, ?> params, String paramName, T targetParams);
}
