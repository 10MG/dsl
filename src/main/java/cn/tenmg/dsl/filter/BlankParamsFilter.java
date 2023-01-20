package cn.tenmg.dsl.filter;

import cn.tenmg.dsl.ParamsFilter;
import cn.tenmg.dsl.utils.MatchUtils;
import cn.tenmg.dsl.utils.StringUtils;

/**
 * 空白字符串参数过滤器
 * 
 * @author June wjzhao@aliyun.com
 * 
 * @since 1.3.0
 */
public class BlankParamsFilter implements ParamsFilter {

	private String params = ParamsFilter.ALL;

	/**
	 * 获取参数名称表达式，默认值为“*”。
	 * 
	 * @return 参数名称表达式
	 */
	public String getParams() {
		return params;
	}

	/**
	 * 设置参数名称表达式，多个参数名称之间使用“,”分隔，可使用“*”作为通配符。
	 * 
	 * @param params
	 *            参数名称表达式
	 */
	public void setParams(String params) {
		this.params = params;
	}

	@Override
	public boolean determine(String name, Object value) {
		if (StringUtils.isBlank(params)) {
			return false;
		} else if (MatchUtils.matchesAny(params.split(","), name)) {
			if (value == null) {
				return true;
			}
			if ((value instanceof String && StringUtils.isBlank((String) value))) {
				return true;
			}
		}
		return false;
	}

}
