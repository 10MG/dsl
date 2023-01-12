package cn.tenmg.dsl.context;

import java.util.List;

import cn.tenmg.dsl.DSLContext;
import cn.tenmg.dsl.ParamsConverter;
import cn.tenmg.dsl.ParamsFilter;

/**
 * 默认动态脚本语言上下文
 * 
 * @author June wjzhao@aliyun.com
 * 
 * @since 1.3.0
 */
public class DefaultDSLContext implements DSLContext {

	private List<ParamsConverter<?>> paramsConverters;

	private List<ParamsFilter> paramsFilters;

	@Override
	public List<ParamsConverter<?>> getParamsConverters() {
		return paramsConverters;
	}

	public void setParamsConverters(List<ParamsConverter<?>> paramsConverters) {
		this.paramsConverters = paramsConverters;
	}

	@Override
	public List<ParamsFilter> getParamsFilters() {
		return paramsFilters;
	}

	public void setParamsFilters(List<ParamsFilter> paramsFilters) {
		this.paramsFilters = paramsFilters;
	}

	public DefaultDSLContext() {
		super();
	}

	public DefaultDSLContext(List<ParamsConverter<?>> paramsConverters, List<ParamsFilter> paramsFilters) {
		super();
		this.paramsConverters = paramsConverters;
		this.paramsFilters = paramsFilters;
	}

}
