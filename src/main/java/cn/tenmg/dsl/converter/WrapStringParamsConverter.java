package cn.tenmg.dsl.converter;

/**
 * 将字符串参数使用模板进行装饰的转换器。包装模板可使用 <code> ${value}</code> 占位符代表参数值
 * 
 * @author June wjzhao@aliyun.com
 *
 * @since 1.3.0
 */
public class WrapStringParamsConverter extends FormatableParamsConverter<Object> {

	private static final String VALUE_REGEX = "\\$\\{value\\}";

	@Override
	public Object convert(Object value) {
		String formatter = getFormatter();
		if (value != null && formatter != null && value instanceof String) {
			return formatter.replaceAll(VALUE_REGEX, (String) value);
		}
		return value;
	}

}
