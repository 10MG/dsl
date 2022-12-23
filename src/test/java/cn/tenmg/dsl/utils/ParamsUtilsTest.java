package cn.tenmg.dsl.utils;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import cn.tenmg.dsl.model.Emperor;
import cn.tenmg.dsl.model.People;

/**
 * 参数工具类测试类
 * 
 * @author June wjzhao@aliyun.com
 *
 * @since 1.2.10
 */
public class ParamsUtilsTest {

	@Test
	public void test() throws Exception {
		Map<String, People> params = new HashMap<String, People>(2);
		Emperor emperor = new Emperor("刘彻", 69, "汉武帝");
		params.put("emperor", emperor);
		People prince = new People("刘据", 37, emperor);
		params.put("prince", prince);

		Assertions.assertEquals(emperor.getName(), ParamsUtils.getParam(params, "emperor.name"));
		Assertions.assertEquals(emperor.getAge(), (int) ParamsUtils.getParam(params, "emperor.age"));
		Assertions.assertEquals(emperor.getAlias(), ParamsUtils.getParam(params, "emperor.alias"));

		Assertions.assertEquals(prince.getName(), ParamsUtils.getParam(params, "prince.name"));
		Assertions.assertEquals(prince.getAge(), (int) ParamsUtils.getParam(params, "prince.age"));
		Assertions.assertEquals(emperor, ParamsUtils.getParam(params, "prince.parent"));

		Assertions.assertEquals(emperor.getName(), ParamsUtils.getParam(params, "prince.parent.name"));
		Assertions.assertEquals(emperor.getAge(), (int) ParamsUtils.getParam(params, "prince.parent.age"));
		Assertions.assertEquals(emperor.getAlias(), ParamsUtils.getParam(params, "prince.parent.alias"));
	}

}
