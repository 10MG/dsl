package cn.tenmg.dsl.filter;

import cn.tenmg.dsl.ParamsFilter;
import cn.tenmg.dsl.utils.MatchUtils;
import cn.tenmg.dsl.utils.StringUtils;

/**
 * 可比较的参数过滤器
 * 
 * @author June wjzhao@aliyun.com
 *
 * @since 1.3.0
 */
public abstract class CompareableParamsFilter implements ParamsFilter {

	/**
	 * 参数名表达式
	 */
	protected String params;

	/**
	 * 比较值
	 */
	protected String value;

	public String getParams() {
		return params;
	}

	public void setParams(String params) {
		this.params = params;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * 对参数值和比较值进行比较确定是否需要过滤
	 * 
	 * @param value
	 *            参数值
	 * @param compared
	 *            比较值
	 * @return 如果参数需要过滤则返回{@code true}，否则返回{@code false}
	 */
	abstract <T extends Comparable<T>> boolean decide(T value, T compared);

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public boolean determine(String name, Object value) {
		if (StringUtils.isBlank(params) || value == null) {
			return false;
		}
		if (MatchUtils.matchesAny(params.split(","), name)) {
			Class<?> type = value.getClass();
			if (String.class.isAssignableFrom(type)) {
				return decide((String) value, this.value);
			} else if (Comparable.class.isAssignableFrom(type)) {
				try {
					return decide((Comparable) value,
							(Comparable) type.getMethod("valueOf", String.class).invoke(null, this.value));
				} catch (Exception e) {
					return false;
				}
			} else {
				return false;
			}
		}
		return false;
	}

}
