package cn.tenmg.dsl.converter;

/**
 * 支持格式化的参数转换器
 * 
 * @param <T>
 *            结果类型
 * 
 * @author June wjzhao@aliyun.com
 * 
 * @since 1.3.0
 */
public abstract class FormatableParamsConverter<T> extends AbstractParamsConverter<T> {

	private String formatter;

	public String getFormatter() {
		return formatter;
	}

	public void setFormatter(String formatter) {
		this.formatter = formatter;
	}

}
