package cn.tenmg.dsl.parser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cn.tenmg.dsl.ParamsParser;
import cn.tenmg.dsl.utils.DSLUtils;

/**
 * JDBC参数解析器
 * 
 * @author June wjzhao@aliyun.com
 * 
 * @since 1.2.0
 */
public class JDBCParamsParser implements ParamsParser<List<Object>> {

	private static final JDBCParamsParser INSTANCE = new JDBCParamsParser();

	private JDBCParamsParser() {
		super();
	}

	public static JDBCParamsParser getInstance() {
		return INSTANCE;
	}

	@Override
	public List<Object> newParams() {
		return new ArrayList<Object>();
	}

	@Override
	public void parse(StringBuilder scriptBuilder, Map<String, ?> params, String paramName,
			List<Object> targetParams) {
		scriptBuilder.append(DSLUtils.PARAM_MARK);
		Object value = params.get(paramName);
		if (value != null) {
			if (value instanceof Collection<?>) {
				Collection<?> collection = (Collection<?>) value;
				if (collection == null || collection.isEmpty()) {
					targetParams.add(null);
				} else {
					Iterator<?> it = collection.iterator();
					targetParams.add(it.next());
					while (it.hasNext()) {
						scriptBuilder.append(DSLUtils.COMMA).append(DSLUtils.BLANK_SPACE).append(DSLUtils.PARAM_MARK);
						targetParams.add(it.next());
					}
				}
			} else if (value instanceof Object[]) {
				Object[] objects = (Object[]) value;
				if (objects.length == 0) {
					targetParams.add(null);
				} else {
					targetParams.add(objects[0]);
					for (int i = 1; i < objects.length; i++) {
						scriptBuilder.append(DSLUtils.COMMA).append(DSLUtils.BLANK_SPACE).append(DSLUtils.PARAM_MARK);
						targetParams.add(objects[i]);
					}
				}
			} else {
				targetParams.add(value);
			}
		} else {
			targetParams.add(value);
		}
	}

}
