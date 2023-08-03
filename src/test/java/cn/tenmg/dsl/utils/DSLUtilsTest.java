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

	private static final String expectedSQL = "SELECT STAFF_ID, STAFF_NAME, POSITION, STATE, CREATE_TIME FROM STAFF_INFO WHERE enabled = :enabled AND STATE = :state AND CREATE_TIME >= :beginDate AND CREATE_TIME < :endDate AND POSITION in (:positions) AND STAFF_NAME LIKE :staffName AND STAFF_ID = :staff.staffId AND STAFF_ID = :map.staffId AND STAFF_NAME = :map[staffName]-- 单行注释 AND STAFF_ID = :array[0] AND 0 != :noteq /*多行 注释*/ AND 0 <= :notgt AND 0 < :notgte AND 0 >= :notlt AND 0 > :notlte ORDER BY CASE STAFF_ID WHEN :map.excellent[0] THEN 0 WHEN :map[excellent][1] THEN 1 WHEN :map.excellent[2] THEN 2 ELSE 3 END, STAFF_NAME";

	@Test
	public void test() throws IOException {
		String enabled = "1", beginDate = "2021-07-02", endDate = "2023-01-19", staffName = "June",
				positions = "Chairman,CEO,COO,CFO,CIO,OD,MD,OM,PM,Staff", regex = ",", nullValue = null,
				emptyString = "", blankSpace = " ", expectedAfterWrap = StringUtils.concat("%", staffName, "%");
		int state = 1, eq = 0, noteq = 1, gt = 1, notgt = 0, gte = 0, notgte = -1, lt = -1, notlt = 0, lte = 0,
				notlte = 1, others = 9, limit = 500;
		Map<String, Object> params = MapUtils.newHashMapBuilder(String.class).put("enabled", enabled)
				.put("state", state).put("beginDate", beginDate).put("endDate", endDate).put("positions", positions)
				.put("staffName", staffName).put("null", nullValue).put("emptyString", emptyString)
				.put("blankSpace", blankSpace).put("eq", eq).put("noteq", noteq).put("gt", gt).put("notgt", notgt)
				.put("gte", gte).put("notgte", notgte).put("lt", lt).put("notlt", notlt).put("lte", lte)
				.put("notlte", notlte).put("others", others).put("staff", new Staff(1))
				.put("map",
						MapUtils.newHashMapBuilder().put("staffId", 1).put("staffName", "June")
								.put("excellent", Arrays.asList(1, 2, 3)).build())
				.put("array", Arrays.asList(100)).build();

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

		InputStream in = null;
		try {
			in = ClassUtils.getDefaultClassLoader().getResourceAsStream("full-features.dsl");
			byte[] bytes = new byte[in.available()];
			in.read(bytes);
			NamedScript namedScript = DSLUtils.parse(new DefaultDSLContext(converters, filters),
					new String(bytes, StandardCharsets.UTF_8), params);

			// 测试解析脚本正确性
			Assertions.assertEquals(expectedSQL, namedScript.getScript().trim().replaceAll("[\\s]+", " "));

			// 测试解析参数正确性
			Map<String, Object> usedParams = namedScript.getParams();
			Assertions.assertNotEquals(enabled, usedParams.get("enabled"));
			Assertions.assertEquals(DateUtils.parse(beginDate, "yyyy-MM-dd"), usedParams.get("beginDate"));
			Assertions.assertEquals(DateUtils.addDays(DateUtils.parse(endDate, "yyyy-MM-dd"), 1),
					usedParams.get("endDate"));
			Assertions.assertArrayEquals(positions.split(regex, limit), (String[]) usedParams.get("positions"));
			Assertions.assertEquals(expectedAfterWrap, usedParams.get("staffName"));
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
		} finally {
			if (in != null) {
				in.close();
			}
		}
	}

}
