package cn.tenmg.dsl.filter;

import cn.tenmg.dsl.ParamsFilter;
import cn.tenmg.dsl.exception.TypeConvertException;
import cn.tenmg.dsl.utils.ConfigUtils;
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
	 * 将比较值转换为参数值的类型时是否抛出异常
	 */
	private static final boolean TYPE_CONVERT_EXCEPTION = Boolean
			.valueOf(ConfigUtils.getProperty("filter.type_convert_exception", "false"));

	private static final String TYPE_CONVERT_METHOD = "valueOf";

	/**
	 * 参数名表达式
	 */
	private String params;

	/**
	 * 比较值
	 */
	private Object value;

	public String getParams() {
		return params;
	}

	public void setParams(String params) {
		this.params = params;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	/**
	 * 当参数值和比较值均为 {@code null} 时，确定是否需要过滤
	 * 
	 * @return 如果参数需要过滤则返回 {@code true}，否则返回 {@code false}
	 */
	abstract boolean decideNull();

	/**
	 * 对参数值和比较值进行比较确定是否需要过滤
	 * 
	 * @param value
	 *            参数值
	 * @param compared
	 *            比较值
	 * @return 如果参数需要过滤则返回 {@code true}，否则返回 {@code false}
	 */
	abstract <T extends Comparable<T>> boolean decide(T value, T compared);

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public boolean determine(String name, Object value) {
		if (StringUtils.isBlank(params)) {
			return false;
		}
		Object compared = getValue();
		if (compared == null) {
			if (value == null) {
				return decideNull();
			}
			return false;
		}
		if (MatchUtils.matchesAny(params.split(","), name)) {
			Class<?> type = value.getClass(), comparedType = compared.getClass();
			if (Comparable.class.isAssignableFrom(type)) {
				if (!type.equals(comparedType)) {
					try {
						compared = type.getMethod(TYPE_CONVERT_METHOD, comparedType).invoke(null, compared);
					} catch (Exception e) {
						if (TYPE_CONVERT_EXCEPTION) {
							throw new TypeConvertException(StringUtils.concat(
									"An exception occured when converting compared value to the type of the parameter value by the static method ",
									TYPE_CONVERT_METHOD, "(", comparedType.getName(), ")"), e);
						}
						return false;
					}
				}
				return decide((Comparable) value, (Comparable) compared);
			} else {
				return false;
			}
		}
		return false;
	}

}
