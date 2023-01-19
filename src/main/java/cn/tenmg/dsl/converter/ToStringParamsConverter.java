package cn.tenmg.dsl.converter;

/**
 * 将参数转换为 {@code java.lang.String} 类型的转换器
 * 
 * @author June wjzhao@aliyun.com
 * 
 * @since 1.3.0
 */
public class ToStringParamsConverter extends AbstractParamsConverter<String> {

	@Override
	public String convert(Object value) {
		return value == null ? null : value.toString();
	}

}
