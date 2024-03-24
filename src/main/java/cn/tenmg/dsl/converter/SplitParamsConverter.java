package cn.tenmg.dsl.converter;

/**
 * 将类型为 {@code java.lang.String} 的非 {@code null} 参数值进行分割的转换器
 * 
 * @author June wjzhao@aliyun.com
 *
 */
public class SplitParamsConverter extends AbstractParamsConverter<Object> {

	private String regex;

	private Integer limit;

	public String getRegex() {
		return regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}

	public Integer getLimit() {
		return limit;
	}

	public void setLimit(Integer limit) {
		this.limit = limit;
	}

	@Override
	public Object convert(Object value) {
		if (value instanceof String) {
			if (limit == null) {
				return ((String) value).split(regex);
			}
			return ((String) value).split(regex, limit);
		}
		return value;
	}

}
