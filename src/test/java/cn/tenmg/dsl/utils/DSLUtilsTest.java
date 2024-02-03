package cn.tenmg.dsl.utils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import cn.tenmg.dsl.DSLContext;
import cn.tenmg.dsl.NamedScript;
import cn.tenmg.dsl.ParamsConverter;
import cn.tenmg.dsl.ParamsFilter;
import cn.tenmg.dsl.context.DefaultDSLContext;
import cn.tenmg.dsl.converter.DateAddParamsConverter;
import cn.tenmg.dsl.converter.DateAddParamsConverter.Unit;
import cn.tenmg.dsl.converter.SplitParamsConverter;
import cn.tenmg.dsl.converter.ToDateParamsConverter;
import cn.tenmg.dsl.converter.ToNumberParamsConverter;
import cn.tenmg.dsl.converter.ToStringParamsConverter;
import cn.tenmg.dsl.converter.WrapStringParamsConverter;
import cn.tenmg.dsl.filter.BlankParamsFilter;
import cn.tenmg.dsl.filter.EqParamsFilter;
import cn.tenmg.dsl.filter.GtParamsFilter;
import cn.tenmg.dsl.filter.GteParamsFilter;
import cn.tenmg.dsl.filter.LtParamsFilter;
import cn.tenmg.dsl.filter.LteParamsFilter;
import cn.tenmg.dsl.model.Staff;

/**
 * 动态脚本语言(DSL)工具类测试类
 * 
 * @author June wjzhao@aliyun.com
 *
 * @since 1.3.2
 */
public class DSLUtilsTest {

	@Test
	public void test() throws IOException {
		String enabled = "1", beginDate = "2021-07-02", endDate = "2023-01-19", staffName = "June",
				positions = "Chairman,CEO,COO,CFO,CIO,OD,MD,OM,PM,Staff", regex = ",", nullValue = null,
				emptyString = "", blankSpace = " ", expectedAfterWrap = StringUtils.concat("%", staffName, "%");
		int state = 1, staffId = 1, array0 = 100, eq = 0, noteq = 1, gt = 1, notgt = 0, gte = 0, notgte = -1, lt = -1,
				notlt = 0, lte = 0, notlte = 1, excellent0 = 1, excellent1 = 2, excellent2 = 3, others = 9, limit = 500;
		Map<String, Object> map = MapUtils.newHashMapBuilder(String.class).put("staffId", staffId)
				.put("staffName", "June").build("excellent", Arrays.asList(excellent0, excellent1, excellent2));
		Map<String, Object> params = MapUtils.newHashMapBuilder(String.class).put("enabled", enabled)
				.put("state", state).put("beginDate", beginDate).put("endDate", endDate).put("positions", positions)
				.put("staffName", staffName).put("null", nullValue).put("emptyString", emptyString)
				.put("blankSpace", blankSpace).put("eq", eq).put("noteq", noteq).put("gt", gt).put("notgt", notgt)
				.put("gte", gte).put("notgte", notgte).put("lt", lt).put("notlt", notlt).put("lte", lte)
				.put("notlte", notlte).put("others", others).put("staff", new Staff(1)).put("map", map)
				.put("array", Arrays.asList(array0)).build();

		// 参数转换器
		List<ParamsConverter<?>> converters = new ArrayList<ParamsConverter<?>>();

		ToNumberParamsConverter toNumber = new ToNumberParamsConverter();
		toNumber.setParams("enabled");
		toNumber.setFormatter("");
		converters.add(toNumber);

		ToStringParamsConverter toString = new ToStringParamsConverter();
		toString.setParams("state");
		toString.setFormatter("#");
		converters.add(toString);

		ToDateParamsConverter toDate = new ToDateParamsConverter();
		toDate.setParams("beginDate,endDate");
		toDate.setFormatter("yyyy-MM-dd");
		converters.add(toDate);

		DateAddParamsConverter dateAdd = new DateAddParamsConverter();
		dateAdd.setParams("endDate");
		dateAdd.setAmount(1);
		dateAdd.setUnit(Unit.DAY);
		converters.add(dateAdd);

		SplitParamsConverter split = new SplitParamsConverter();
		split.setParams("positions");
		split.setRegex(regex);
		split.setLimit(limit);
		converters.add(split);

		WrapStringParamsConverter wrapString = new WrapStringParamsConverter();
		wrapString.setParams("staffName");
		wrapString.setFormatter("%${value}%");
		converters.add(wrapString);

		// 参数过滤器
		List<ParamsFilter> filters = new ArrayList<ParamsFilter>();

		BlankParamsFilter blank = new BlankParamsFilter();
		blank.setParams("null,emptyString,blankSpace");
		filters.add(blank);

		EqParamsFilter eqf = new EqParamsFilter();
		eqf.setParams("eq,noteq");
		eqf.setValue(0);
		filters.add(eqf);

		GtParamsFilter gtf = new GtParamsFilter();
		gtf.setParams("gt,notgt");
		gtf.setValue(0);
		filters.add(gtf);

		GteParamsFilter gtef = new GteParamsFilter();
		gtef.setParams("gte,notgte");
		gtef.setValue(0);
		filters.add(gtef);

		LtParamsFilter ltf = new LtParamsFilter();
		ltf.setParams("lt,notlt");
		ltf.setValue(0);
		filters.add(ltf);

		LteParamsFilter ltef = new LteParamsFilter();
		ltef.setParams("lte,notlte");
		ltef.setValue(0);
		filters.add(ltef);

		DSLContext context = new DefaultDSLContext(converters, filters);
		String dsl = readString("full-features.dsl");
		// 三个参数的动态片段的三个参数分别为 excellent0, excellent1, excellent2
		NamedScript namedScript = DSLUtils.parse(context, dsl, params);
		// 测试解析脚本正确性
		Assertions.assertEquals(readString("expected1.dsl"), namedScript.getScript());
		// 测试解析参数正确性
		Map<String, Object> usedParams = namedScript.getParams();
		commonAsserts(enabled, state, beginDate, endDate, positions, regex, limit, expectedAfterWrap, staffId, array0,
				noteq, notgt, notgte, notlt, notlte, usedParams);
		Assertions.assertEquals(excellent0, usedParams.get("map.excellent[0]"));
		Assertions.assertEquals(excellent1, usedParams.get("map[excellent][1]"));
		Assertions.assertEquals(excellent2, usedParams.get("map.excellent[2]"));

		// 三个参数的动态片段的三个参数分别为 excellent0, excellent1, null
		map.put("excellent", Arrays.asList(excellent0, excellent1));
		namedScript = DSLUtils.parse(context, dsl, params);
		// 测试解析脚本正确性
		Assertions.assertEquals(readString("expected2.dsl"), namedScript.getScript());
		// 测试解析参数正确性
		usedParams = namedScript.getParams();
		commonAsserts(enabled, state, beginDate, endDate, positions, regex, limit, expectedAfterWrap, staffId, array0,
				noteq, notgt, notgte, notlt, notlte, usedParams);
		Assertions.assertEquals(excellent0, usedParams.get("map.excellent[0]"));
		Assertions.assertEquals(excellent1, usedParams.get("map[excellent][1]"));
		Assertions.assertFalse(usedParams.containsKey("map.excellent[2]"));

		// 三个参数的动态片段的三个参数分别为 excellent0, excellent1, none
		map.put("excellent", MapUtils.newHashMapBuilder("0", excellent0).build("1", excellent1));
		namedScript = DSLUtils.parse(context, dsl, params);
		// 测试解析脚本正确性
		Assertions.assertEquals(readString("expected2.dsl"), namedScript.getScript());
		// 测试解析参数正确性
		usedParams = namedScript.getParams();
		commonAsserts(enabled, state, beginDate, endDate, positions, regex, limit, expectedAfterWrap, staffId, array0,
				noteq, notgt, notgte, notlt, notlte, usedParams);
		Assertions.assertEquals(excellent0, usedParams.get("map.excellent[0]"));
		Assertions.assertEquals(excellent1, usedParams.get("map[excellent][1]"));
		Assertions.assertFalse(usedParams.containsKey("map.excellent[2]"));

		// 三个参数的动态片段的三个参数分别为 excellent0, null, excellent2
		map.put("excellent", Arrays.asList(excellent0, null, excellent2));
		namedScript = DSLUtils.parse(context, dsl, params);
		// 测试解析脚本正确性
		Assertions.assertEquals(readString("expected3.dsl"), namedScript.getScript());
		// 测试解析参数正确性
		usedParams = namedScript.getParams();
		commonAsserts(enabled, state, beginDate, endDate, positions, regex, limit, expectedAfterWrap, staffId, array0,
				noteq, notgt, notgte, notlt, notlte, usedParams);
		Assertions.assertEquals(excellent0, usedParams.get("map.excellent[0]"));
		Assertions.assertFalse(usedParams.containsKey("map[excellent][1]"));
		Assertions.assertFalse(usedParams.containsKey("map.excellent[2]"));

		// 三个参数的动态片段的三个参数分别为 excellent0, none, excellent2
		map.put("excellent", MapUtils.newHashMapBuilder("0", excellent0).build("2", excellent2));
		namedScript = DSLUtils.parse(context, dsl, params);
		// 测试解析脚本正确性
		Assertions.assertEquals(readString("expected3.dsl"), namedScript.getScript());
		// 测试解析参数正确性
		usedParams = namedScript.getParams();
		commonAsserts(enabled, state, beginDate, endDate, positions, regex, limit, expectedAfterWrap, staffId, array0,
				noteq, notgt, notgte, notlt, notlte, usedParams);
		Assertions.assertEquals(excellent0, usedParams.get("map.excellent[0]"));
		Assertions.assertFalse(usedParams.containsKey("map[excellent][1]"));
		Assertions.assertFalse(usedParams.containsKey("map.excellent[2]"));

		// 三个参数的动态片段的三个参数分别为 null, excellent1, excellent2
		map.put("excellent", Arrays.asList(null, excellent1, excellent2));
		namedScript = DSLUtils.parse(context, dsl, params);
		// 测试解析脚本正确性
		Assertions.assertEquals(readString("expected4.dsl"), namedScript.getScript());
		// 测试解析参数正确性
		usedParams = namedScript.getParams();
		commonAsserts(enabled, state, beginDate, endDate, positions, regex, limit, expectedAfterWrap, staffId, array0,
				noteq, notgt, notgte, notlt, notlte, usedParams);
		Assertions.assertFalse(usedParams.containsKey("map.excellent[0]"));
		Assertions.assertFalse(usedParams.containsKey("map[excellent][1]"));
		Assertions.assertFalse(usedParams.containsKey("map.excellent[2]"));

		// 三个参数的动态片段的三个参数分别为 none, excellent1, excellent2
		map.put("excellent", MapUtils.newHashMapBuilder("1", excellent1).build("2", excellent2));
		namedScript = DSLUtils.parse(context, dsl, params);
		// 测试解析脚本正确性
		Assertions.assertEquals(readString("expected4.dsl"), namedScript.getScript());
		// 测试解析参数正确性
		usedParams = namedScript.getParams();
		commonAsserts(enabled, state, beginDate, endDate, positions, regex, limit, expectedAfterWrap, staffId, array0,
				noteq, notgt, notgte, notlt, notlte, usedParams);
		Assertions.assertFalse(usedParams.containsKey("map.excellent[0]"));
		Assertions.assertFalse(usedParams.containsKey("map[excellent][1]"));
		Assertions.assertFalse(usedParams.containsKey("map.excellent[2]"));

		// 三个参数的动态片段的三个参数分别为 excellent0, null, null
		map.put("excellent", Arrays.asList(excellent0, null, null));
		namedScript = DSLUtils.parse(context, dsl, params);
		// 测试解析脚本正确性
		Assertions.assertEquals(readString("expected5.dsl"), namedScript.getScript());
		// 测试解析参数正确性
		usedParams = namedScript.getParams();
		commonAsserts(enabled, state, beginDate, endDate, positions, regex, limit, expectedAfterWrap, staffId, array0,
				noteq, notgt, notgte, notlt, notlte, usedParams);
		Assertions.assertEquals(excellent0, usedParams.get("map.excellent[0]"));
		Assertions.assertFalse(usedParams.containsKey("map[excellent][1]"));
		Assertions.assertFalse(usedParams.containsKey("map.excellent[2]"));

		// 三个参数的动态片段的三个参数分别为 excellent0, none, none
		map.put("excellent", Arrays.asList(excellent0));
		namedScript = DSLUtils.parse(context, dsl, params);
		// 测试解析脚本正确性
		Assertions.assertEquals(readString("expected5.dsl"), namedScript.getScript());
		// 测试解析参数正确性
		usedParams = namedScript.getParams();
		commonAsserts(enabled, state, beginDate, endDate, positions, regex, limit, expectedAfterWrap, staffId, array0,
				noteq, notgt, notgte, notlt, notlte, usedParams);
		Assertions.assertEquals(excellent0, usedParams.get("map.excellent[0]"));
		Assertions.assertFalse(usedParams.containsKey("map[excellent][1]"));
		Assertions.assertFalse(usedParams.containsKey("map.excellent[2]"));

		// 三个参数的动态片段的三个参数分别为 null, excellent1, null
		map.put("excellent", Arrays.asList(null, excellent1, null));
		namedScript = DSLUtils.parse(context, dsl, params);
		// 测试解析脚本正确性
		Assertions.assertEquals(readString("expected4.dsl"), namedScript.getScript());
		// 测试解析参数正确性
		usedParams = namedScript.getParams();
		commonAsserts(enabled, state, beginDate, endDate, positions, regex, limit, expectedAfterWrap, staffId, array0,
				noteq, notgt, notgte, notlt, notlte, usedParams);
		Assertions.assertFalse(usedParams.containsKey("map.excellent[0]"));
		Assertions.assertFalse(usedParams.containsKey("map[excellent][1]"));
		Assertions.assertFalse(usedParams.containsKey("map.excellent[2]"));

		// 三个参数的动态片段的三个参数分别为 none, excellent1, none
		map.put("excellent", MapUtils.newHashMap("1", excellent1));
		namedScript = DSLUtils.parse(context, dsl, params);
		// 测试解析脚本正确性
		Assertions.assertEquals(readString("expected4.dsl"), namedScript.getScript());
		// 测试解析参数正确性
		usedParams = namedScript.getParams();
		commonAsserts(enabled, state, beginDate, endDate, positions, regex, limit, expectedAfterWrap, staffId, array0,
				noteq, notgt, notgte, notlt, notlte, usedParams);
		Assertions.assertFalse(usedParams.containsKey("map.excellent[0]"));
		Assertions.assertFalse(usedParams.containsKey("map[excellent][1]"));
		Assertions.assertFalse(usedParams.containsKey("map.excellent[2]"));

		// 三个参数的动态片段的三个参数分别为 null, null, excellent2
		map.put("excellent", Arrays.asList(null, null, excellent2));
		namedScript = DSLUtils.parse(context, dsl, params);
		// 测试解析脚本正确性
		Assertions.assertEquals(readString("expected4.dsl"), namedScript.getScript());
		// 测试解析参数正确性
		usedParams = namedScript.getParams();
		commonAsserts(enabled, state, beginDate, endDate, positions, regex, limit, expectedAfterWrap, staffId, array0,
				noteq, notgt, notgte, notlt, notlte, usedParams);
		Assertions.assertFalse(usedParams.containsKey("map.excellent[0]"));
		Assertions.assertFalse(usedParams.containsKey("map[excellent][1]"));
		Assertions.assertFalse(usedParams.containsKey("map.excellent[2]"));

		// 三个参数的动态片段的三个参数分别为 none, none, excellent2
		map.put("excellent", MapUtils.newHashMap("2", excellent1));
		namedScript = DSLUtils.parse(context, dsl, params);
		// 测试解析脚本正确性
		Assertions.assertEquals(readString("expected4.dsl"), namedScript.getScript());
		// 测试解析参数正确性
		usedParams = namedScript.getParams();
		commonAsserts(enabled, state, beginDate, endDate, positions, regex, limit, expectedAfterWrap, staffId, array0,
				noteq, notgt, notgte, notlt, notlte, usedParams);
		Assertions.assertFalse(usedParams.containsKey("map.excellent[0]"));
		Assertions.assertFalse(usedParams.containsKey("map[excellent][1]"));
		Assertions.assertFalse(usedParams.containsKey("map.excellent[2]"));

		// 三个参数的动态片段的三个参数分别为 null, null, null
		map.put("excellent", Arrays.asList(null, null, null));
		namedScript = DSLUtils.parse(context, dsl, params);
		// 测试解析脚本正确性
		Assertions.assertEquals(readString("expected4.dsl"), namedScript.getScript());
		// 测试解析参数正确性
		usedParams = namedScript.getParams();
		commonAsserts(enabled, state, beginDate, endDate, positions, regex, limit, expectedAfterWrap, staffId, array0,
				noteq, notgt, notgte, notlt, notlte, usedParams);
		Assertions.assertFalse(usedParams.containsKey("map.excellent[0]"));
		Assertions.assertFalse(usedParams.containsKey("map[excellent][1]"));
		Assertions.assertFalse(usedParams.containsKey("map.excellent[2]"));

		// 三个参数的动态片段没有参数
		map.remove("excellent");
		namedScript = DSLUtils.parse(context, dsl, params);
		// 测试解析脚本正确性
		Assertions.assertEquals(readString("expected4.dsl"), namedScript.getScript());
		// 测试解析参数正确性
		usedParams = namedScript.getParams();
		commonAsserts(enabled, state, beginDate, endDate, positions, regex, limit, expectedAfterWrap, staffId, array0,
				noteq, notgt, notgte, notlt, notlte, usedParams);
		Assertions.assertFalse(usedParams.containsKey("map.excellent[0]"));
		Assertions.assertFalse(usedParams.containsKey("map[excellent][1]"));
		Assertions.assertFalse(usedParams.containsKey("map.excellent[2]"));
	}

	private static String readString(String fileName) throws IOException {
		InputStream in = null;
		try {
			in = ClassUtils.getDefaultClassLoader().getResourceAsStream(fileName);
			byte[] bytes = new byte[in.available()];
			in.read(bytes);
			return new String(bytes, StandardCharsets.UTF_8);
		} finally {
			if (in != null) {
				in.close();
			}
		}
	}

	private static void commonAsserts(String enabled, int state, String beginDate, String endDate, String positions,
			String regex, int limit, String expectedAfterWrap, int staffId, int array0, int noteq, int notgt,
			int notgte, int notlt, int notlte, Map<String, Object> usedParams) {
		Assertions.assertEquals(DecimalUtils.parse(enabled, ""), usedParams.get("enabled"));
		Assertions.assertEquals(String.valueOf(state), usedParams.get("state"));
		Assertions.assertEquals(DateUtils.parse(beginDate, "yyyy-MM-dd"), usedParams.get("beginDate"));
		Assertions.assertEquals(DateUtils.addDays(DateUtils.parse(endDate, "yyyy-MM-dd"), 1),
				usedParams.get("endDate"));
		Assertions.assertArrayEquals(positions.split(regex, limit), (String[]) usedParams.get("positions"));
		Assertions.assertEquals(expectedAfterWrap, usedParams.get("staffName"));
		Assertions.assertEquals(array0, usedParams.get("array[0]"));
		Assertions.assertEquals(staffId, usedParams.get("staff.staffId"));
		Assertions.assertEquals(staffId, usedParams.get("map.staffId"));
		Assertions.assertFalse(usedParams.containsKey("valid"));
		Assertions.assertFalse(usedParams.containsKey("roleId"));
		Assertions.assertFalse(usedParams.containsKey("null"));
		Assertions.assertFalse(usedParams.containsKey("emptyString"));
		Assertions.assertFalse(usedParams.containsKey("blankSpace"));
		Assertions.assertFalse(usedParams.containsKey("eq"));
		Assertions.assertEquals(noteq, usedParams.get("noteq"));
		Assertions.assertFalse(usedParams.containsKey("gt"));
		Assertions.assertEquals(notgt, usedParams.get("notgt"));
		Assertions.assertFalse(usedParams.containsKey("gte"));
		Assertions.assertEquals(notgte, usedParams.get("notgte"));
		Assertions.assertFalse(usedParams.containsKey("lt"));
		Assertions.assertEquals(notlt, usedParams.get("notlt"));
		Assertions.assertFalse(usedParams.containsKey("lte"));
		Assertions.assertEquals(notlte, usedParams.get("notlte"));
		Assertions.assertFalse(usedParams.containsKey("others"));
	}

}
