package cn.tenmg.dsl.converter;

import cn.tenmg.dsl.utils.DecimalUtils;

/**
 * 将参数转换为数字 {@code java.lang.Number} 类型的转换器
 * 
 * @author June wjzhao@aliyun.com
 *
 * @since 1.3.0
 */
public class ToNumberParamsConverter extends FormatableParamsConverter<Number> {

	@Override
	public Number convert(Object value) {
		if (value == null) {
			return null;
		}
		return DecimalUtils.parse(value, getFormatter());
	}

}
