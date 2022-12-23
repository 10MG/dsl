package cn.tenmg.dsl.utils;

import java.util.Collections;
import java.util.HashSet;

/**
 * 集合工具类
 * 
 * @author June wjzhao@aliyun.com
 * 
 * @since 1.2.10
 */
public abstract class SetUtils {

	/**
	 * 将元素组装成一个哈希集合对象
	 * 
	 * @param elements
	 *            元素
	 * @return 哈希集合对象
	 */
	@SafeVarargs
	public static <E> HashSet<E> newHashSet(final E... elements) {
		final HashSet<E> set = new HashSet<>(elements.length);
		Collections.addAll(set, elements);
		return set;
	}

}
