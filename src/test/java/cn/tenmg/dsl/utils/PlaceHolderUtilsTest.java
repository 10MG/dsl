package cn.tenmg.dsl.utils;

import java.util.HashMap;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * 占位符工具类测试类
 * 
 * @author June wjzhao@aliyun.com
 * @since 1.4.4
 */
public class PlaceHolderUtilsTest {

	/**
	 * 保持原样测试
	 */
	@Test
	public void leaveItAsItIsTest() {
		String splitor = " ", tml = "${name}";
		Assertions.assertTrue(PlaceHolderUtils.replace(tml).equals(tml));
		Assertions.assertTrue(PlaceHolderUtils.replace(splitor + tml).equals(splitor + tml));
		Assertions.assertTrue(PlaceHolderUtils.replace(tml + splitor).equals(tml + splitor));
		Assertions.assertTrue(PlaceHolderUtils.replace(splitor + tml + splitor).equals(splitor + tml + splitor));

		HashMap<String, String> params = MapUtils.newHashMap();
		Assertions.assertTrue(PlaceHolderUtils.replace(tml, params).equals(tml));
		Assertions.assertTrue(PlaceHolderUtils.replace(splitor + tml, params).equals(splitor + tml));
		Assertions.assertTrue(PlaceHolderUtils.replace(tml + splitor, params).equals(tml + splitor));
		Assertions
				.assertTrue(PlaceHolderUtils.replace(splitor + tml + splitor, params).equals(splitor + tml + splitor));
	}

	/**
	 * 简单模板替换测试
	 */
	@Test
	public void simpleReplaceTest() {
		String splitor = " ", tml = "${name}", name = "June";
		Assertions.assertTrue(PlaceHolderUtils.replace(tml, "name", name).equals(name));
		Assertions.assertTrue(PlaceHolderUtils.replace(splitor + tml, "name", name).equals(splitor + name));
		Assertions.assertTrue(PlaceHolderUtils.replace(tml + splitor, "name", name).equals(name + splitor));
		Assertions.assertTrue(
				PlaceHolderUtils.replace(splitor + tml + splitor, "name", name).equals(splitor + name + splitor));

		HashMap<String, String> params = MapUtils.newHashMap("name", name);
		Assertions.assertTrue(PlaceHolderUtils.replace(tml, params).equals(name));
		Assertions.assertTrue(PlaceHolderUtils.replace(splitor + tml, params).equals(splitor + name));
		Assertions.assertTrue(PlaceHolderUtils.replace(tml + splitor, params).equals(name + splitor));
		Assertions
				.assertTrue(PlaceHolderUtils.replace(splitor + tml + splitor, params).equals(splitor + name + splitor));
	}

	/**
	 * 拼接模板替换测试
	 */
	@Test
	public void splicingReplaceTest() {
		String splitor = " ", tml = "${id} ${name}", id = "000001", name = "June", expected = id + splitor + name;
		Assertions.assertTrue(PlaceHolderUtils.replace(tml, "id", id, "name", name).equals(expected));
		Assertions
				.assertTrue(PlaceHolderUtils.replace(splitor + tml, "id", id, "name", name).equals(splitor + expected));
		Assertions
				.assertTrue(PlaceHolderUtils.replace(tml + splitor, "id", id, "name", name).equals(expected + splitor));
		Assertions.assertTrue(PlaceHolderUtils.replace(splitor + tml + splitor, "id", id, "name", name)
				.equals(splitor + expected + splitor));

		HashMap<String, String> params = MapUtils.newHashMapBuilder("name", name).build("id", id);
		Assertions.assertTrue(PlaceHolderUtils.replace(tml, params).equals(expected));
		Assertions.assertTrue(PlaceHolderUtils.replace(splitor + tml, params).equals(splitor + expected));
		Assertions.assertTrue(PlaceHolderUtils.replace(tml + splitor, params).equals(expected + splitor));
		Assertions.assertTrue(
				PlaceHolderUtils.replace(splitor + tml + splitor, params).equals(splitor + expected + splitor));
	}

	/**
	 * 一层嵌套测试
	 */
	@Test
	public void oneLayerOfNestingTest() {
		String splitor = " ", tml = "${${id}}", id = "000001", name = "June";
		Assertions.assertTrue(PlaceHolderUtils.replace(tml, "id", id, id, name).equals(name));
		Assertions.assertTrue(PlaceHolderUtils.replace(splitor + tml, "id", id, id, name).equals(splitor + name));
		Assertions.assertTrue(PlaceHolderUtils.replace(tml + splitor, "id", id, id, name).equals(name + splitor));
		Assertions.assertTrue(
				PlaceHolderUtils.replace(splitor + tml + splitor, "id", id, id, name).equals(splitor + name + splitor));

		HashMap<String, String> params = MapUtils.newHashMapBuilder("id", id).build(id, name);
		Assertions.assertTrue(PlaceHolderUtils.replace(tml, params).equals(name));
		Assertions.assertTrue(PlaceHolderUtils.replace(splitor + tml, params).equals(splitor + name));
		Assertions.assertTrue(PlaceHolderUtils.replace(tml + splitor, params).equals(name + splitor));
		Assertions
				.assertTrue(PlaceHolderUtils.replace(splitor + tml + splitor, params).equals(splitor + name + splitor));
	}

	/**
	 * 两层嵌套测试
	 */
	@Test
	public void twoLayerOfNestingTest() {
		String splitor = " ", tml = "${${${id}}}", id = "000001", name = "June", abbr = "Jun.";
		Assertions.assertTrue(PlaceHolderUtils.replace(tml, "id", id, id, name, name, abbr).equals(abbr));
		Assertions.assertTrue(
				PlaceHolderUtils.replace(splitor + tml, "id", id, id, name, name, abbr).equals(splitor + abbr));
		Assertions.assertTrue(
				PlaceHolderUtils.replace(tml + splitor, "id", id, id, name, name, abbr).equals(abbr + splitor));
		Assertions.assertTrue(PlaceHolderUtils.replace(splitor + tml + splitor, "id", id, id, name, name, abbr)
				.equals(splitor + abbr + splitor));

		HashMap<String, String> params = MapUtils.newHashMapBuilder("id", id).put(id, name).build(name, abbr);
		Assertions.assertTrue(PlaceHolderUtils.replace(tml, params).equals(abbr));
		Assertions.assertTrue(PlaceHolderUtils.replace(splitor + tml, params).equals(splitor + abbr));
		Assertions.assertTrue(PlaceHolderUtils.replace(tml + splitor, params).equals(abbr + splitor));
		Assertions
				.assertTrue(PlaceHolderUtils.replace(splitor + tml + splitor, params).equals(splitor + abbr + splitor));
	}

}
