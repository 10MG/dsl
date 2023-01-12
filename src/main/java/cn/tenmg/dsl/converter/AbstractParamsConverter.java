package cn.tenmg.dsl.converter;

import cn.tenmg.dsl.ParamsConverter;
import cn.tenmg.dsl.utils.MatchUtils;
import cn.tenmg.dsl.utils.StringUtils;

/**
 * 参数转换器抽象实现类
 * 
 * @param <T>
 *            结果类型
 * 
 * @author June wjzhao@aliyun.com
 *
 * @since 1.3.0
 */
public abstract class AbstractParamsConverter<T> implements ParamsConverter<T> {

	private String params;

	public String getParams() {
		return params;
	}

	public void setParams(String params) {
		this.params = params;
	}

	@Override
	public boolean determine(String name) {
		if (StringUtils.isBlank(params)) {
			return false;
		}
		return MatchUtils.matchesAny(params.split(","), name);
	}

}
