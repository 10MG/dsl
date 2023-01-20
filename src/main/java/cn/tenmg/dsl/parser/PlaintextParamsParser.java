package cn.tenmg.dsl.parser;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import cn.tenmg.dsl.ParamsParser;
import cn.tenmg.dsl.utils.DSLUtils;

/**
 * 明文脚本参数解析器
 * 
 * @author June wjzhao@aliyun.com
 * 
 * @since 1.2.0
 *
 */
public abstract class PlaintextParamsParser implements ParamsParser<Void> {

	private static final String SINGLE_QUOTATION_MARK = "'", ENCODE_SINGLE_QUOTATION_MARK = "\\\\'";

	@Override
	public Void newParams() {
		return null;
	}

	@Override
	public void parse(StringBuilder scriptBuilder, Map<String, ?> params, String paramName, Void targetParams) {
		Object value = params.get(paramName);
		if (value == null) {
			appendNull(scriptBuilder);
		} else {
			if (value instanceof Collection<?>) {
				Collection<?> c = (Collection<?>) value;
				if (c.isEmpty()) {
					appendNull(scriptBuilder);
				} else {
					Iterator<?> it = c.iterator();
					append(scriptBuilder, it.next());
					while (it.hasNext()) {
						scriptBuilder.append(DSLUtils.COMMA).append(DSLUtils.BLANK_SPACE);
						append(scriptBuilder, it.next());
					}
				}
			} else if (value instanceof Object[]) {
				Object[] objects = (Object[]) value;
				if (objects.length == 0) {
					appendNull(scriptBuilder);
				} else {
					append(scriptBuilder, objects[0]);
					for (int i = 1; i < objects.length; i++) {
						scriptBuilder.append(DSLUtils.COMMA).append(DSLUtils.BLANK_SPACE);
						append(scriptBuilder, objects[i]);
					}
				}
			} else {
				append(scriptBuilder, value);
			}
		}
	}

	/**
	 * 将字符串“null”插入脚本替换脚本中的参数
	 * 
	 * @param scriptBuilder
	 *            脚本构建器
	 */
	private static final void appendNull(StringBuilder scriptBuilder) {
		scriptBuilder.append("null");
	}

	/**
	 * 将字符串插入脚本替换脚本中的参数
	 * 
	 * @param scriptBuilder
	 *            脚本构建器
	 * @param value
	 *            字符串参数值
	 */
	private static final void appendString(StringBuilder scriptBuilder, String value) {
		scriptBuilder.append(SINGLE_QUOTATION_MARK)
				.append(value.replaceAll(SINGLE_QUOTATION_MARK, ENCODE_SINGLE_QUOTATION_MARK))
				.append(SINGLE_QUOTATION_MARK);
	}

	/**
	 * 将对象转换为字符串插入脚本替换脚本中的参数
	 * 
	 * @param scriptBuilder
	 *            脚本构建器
	 * @param value
	 *            参数值
	 */
	private void append(StringBuilder scriptBuilder, Object value) {
		if (value == null) {
			appendNull(scriptBuilder);
		} else if (value instanceof String) {
			appendString(scriptBuilder, (String) value);
		} else if (value instanceof CharSequence || value instanceof Character || value instanceof char[]) {
			appendString(scriptBuilder, value.toString());
		} else {
			scriptBuilder.append(convert(value));
		}
	}

	/**
	 * 将参数值转换为字符串
	 * 
	 * @param value
	 *            参数值，不为<code>null</code>，且非<code>java.lang.String</code>、<code>java.lang.CharSequence</code>、<code>java.lang.Character</code>、<code>char[]</code>类型
	 * @return 返回参数值的字符串表示
	 */
	protected abstract String convert(Object value);

}
