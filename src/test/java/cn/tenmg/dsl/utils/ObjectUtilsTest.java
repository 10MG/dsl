package cn.tenmg.dsl.utils;

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
		Assertions.assertEquals(emperor, ObjectUtils.getValue(prince, "parent"));
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
		ObjectUtils.setValue(prince, "parent", emperor);
		Assertions.assertEquals(prince.getName(), ObjectUtils.getValue(prince, "name"));
		Assertions.assertNull(ObjectUtils.getValue(prince, "yearOfbirth"));
		Assertions.assertEquals(prince.getAge(), (int) ObjectUtils.getValue(prince, "age"));
		Assertions.assertEquals(emperor, ObjectUtils.getValue(prince, "parent"));

		ObjectUtils.setValue(prince, "yearOfbirth", "-128");
		Assertions.assertEquals(-128, (int) ObjectUtils.getValue(prince, "yearOfbirth"));
	}
}
