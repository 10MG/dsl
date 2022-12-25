package cn.tenmg.dsl.utils;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import cn.tenmg.dsl.model.Emperor;
import cn.tenmg.dsl.model.People;

/**
 * 对象工具类测试类
 * 
 * @author June wjzhao@aliyun.com
 *
 * @since 1.2.10
 */
public class ObjectUtilsTest {

	@Test
	public void testGetValue() throws Exception {
		Emperor emperor = new Emperor("嬴政", 49, "秦始皇");
		People prince = new People("扶苏", 31, emperor);

		Assertions.assertEquals(emperor.getName(), ObjectUtils.getValue(emperor, "name"));
		Assertions.assertNull(ObjectUtils.getValue(emperor, "yearOfbirth"));
		Assertions.assertEquals(emperor.getAge(), (int) ObjectUtils.getValue(emperor, "age"));
		Assertions.assertEquals(emperor.getAlias(), ObjectUtils.getValue(emperor, "alias"));

		Assertions.assertEquals(prince.getName(), ObjectUtils.getValue(prince, "name"));
		Assertions.assertNull(ObjectUtils.getValue(prince, "yearOfbirth"));
		Assertions.assertEquals(prince.getAge(), (int) ObjectUtils.getValue(prince, "age"));
		Assertions.assertEquals(emperor, ObjectUtils.getValue(prince, "father"));
	}

	@Test
	public void testGetValueFromMap() throws Exception {
		Map<String, People> params = new HashMap<String, People>(2);
		Emperor emperor = new Emperor("刘彻", 69, "汉武帝");
		params.put("emperor", emperor);
		People prince = new People("刘据", 37, emperor);
		params.put("prince", prince);

		Assertions.assertEquals(emperor.getName(), ObjectUtils.getValue(params, "emperor.name"));
		Assertions.assertEquals(emperor.getAge(), (int) ObjectUtils.getValue(params, "emperor.age"));
		Assertions.assertEquals(emperor.getAlias(), ObjectUtils.getValue(params, "emperor.alias"));

		Assertions.assertEquals(prince.getName(), ObjectUtils.getValue(params, "prince.name"));
		Assertions.assertEquals(prince.getAge(), (int) ObjectUtils.getValue(params, "prince.age"));
		Assertions.assertEquals(emperor, ObjectUtils.getValue(params, "prince.father"));

		Assertions.assertEquals(emperor.getName(), ObjectUtils.getValue(params, "prince.father.name"));
		Assertions.assertEquals(emperor.getAge(), (int) ObjectUtils.getValue(params, "prince.father.age"));
		Assertions.assertEquals(emperor.getAlias(), ObjectUtils.getValue(params, "prince.father.alias"));
	}

	@Test
	public void testSetValue() throws Exception {
		Emperor emperor = new Emperor();
		ObjectUtils.setValue(emperor, "name", "刘彻");
		ObjectUtils.setValue(emperor, "age", 69);
		ObjectUtils.setValue(emperor, "yearOfbirth", -156);
		ObjectUtils.setValue(emperor, "alias", "汉武帝");
		Assertions.assertEquals(emperor.getName(), ObjectUtils.getValue(emperor, "name"));
		Assertions.assertEquals(emperor.getAge(), (int) ObjectUtils.getValue(emperor, "age"));
		Assertions.assertEquals(emperor.getYearOfbirth(), (int) ObjectUtils.getValue(emperor, "yearOfbirth"));
		Assertions.assertEquals(emperor.getAlias(), ObjectUtils.getValue(emperor, "alias"));

		People prince = new People();
		ObjectUtils.setValue(prince, "name", "刘据");
		ObjectUtils.setValue(prince, "age", 37);
		ObjectUtils.setValue(prince, "father", emperor);
		Assertions.assertEquals(prince.getName(), ObjectUtils.getValue(prince, "name"));
		Assertions.assertNull(ObjectUtils.getValue(prince, "yearOfbirth"));
		Assertions.assertEquals(prince.getAge(), (int) ObjectUtils.getValue(prince, "age"));
		Assertions.assertEquals(emperor, ObjectUtils.getValue(prince, "father"));

		ObjectUtils.setValue(prince, "yearOfbirth", "-128");
		Assertions.assertEquals(-128, (int) ObjectUtils.getValue(prince, "yearOfbirth"));

		ObjectUtils.setValue(prince, "father.father.name", "刘启");
		ObjectUtils.setValue(prince, "father.father.age", 47);
		ObjectUtils.setValue(prince, "father.father.yearOfbirth", -188);
		People grandpa = prince.getFather().getFather();
		Assertions.assertEquals(grandpa.getName(), ObjectUtils.getValue(prince, "father.father.name"));
		Assertions.assertEquals(grandpa.getAge(), (int) ObjectUtils.getValue(prince, "father.father.age"));
		Assertions.assertEquals(grandpa.getYearOfbirth(),
				(int) ObjectUtils.getValue(prince, "father.father.yearOfbirth"));
		Assertions.assertEquals(1, (int) ObjectUtils.getValue((new int[] {1}), "[0]"));
		Assertions.assertEquals(1, (int) ObjectUtils.getValue((new int[] {1}), "0"));
	}
}
