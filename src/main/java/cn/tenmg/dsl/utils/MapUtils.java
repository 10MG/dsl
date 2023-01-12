package cn.tenmg.dsl.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Map工具类
 * 
 * @author June wjzhao@aliyun.com
 * 
 * @since 1.3.0
 */
public abstract class MapUtils {

	public static <K, V> HashMap<K, V> newHashMap(K key, V value) {
		HashMap<K, V> map = new HashMap<K, V>();
		map.put(key, value);
		return map;
	}

	public static <K, V> HashMap<K, V> newHashMap(int initialCapacity, K key, V value) {
		HashMap<K, V> map = new HashMap<K, V>(initialCapacity);
		map.put(key, value);
		return map;
	}

	public static <K, V> MapBuilder<K, V> newMapBuilder(Map<K, V> map) {
		return new MapBuilder<K, V>(map);
	}

	public static <K, V> MapBuilder<K, V> newHashMapBuilder(K key, V value) {
		return new MapBuilder<K, V>(newHashMap(key, value));
	}

	public static <K, V> MapBuilder<K, V> newHashMapBuilder(int initialCapacity, K key, V value) {
		return new MapBuilder<K, V>(newHashMap(initialCapacity, key, value));
	}

	/**
	 * Map构建器
	 * 
	 * @author June wjzhao@aliyun.com
	 * 
	 * @since 1.3.0
	 * 
	 * @param <K>
	 *            键的类型
	 * @param <V>
	 *            值的类型
	 */
	public static class MapBuilder<K, V> {

		private final Map<K, V> map;

		public MapBuilder(Map<K, V> map) {
			super();
			this.map = map;
		}

		/**
		 * 将键、值存入希查找表中
		 * 
		 * @param map
		 *            指定查找表
		 * @return 返回HashMapKit对象
		 */
		public MapBuilder<K, V> putAll(Map<K, V> map) {
			if (map != null) {
				map.putAll(map);
			}
			return this;
		}

		/**
		 * 将指定查找表中的元素全部放入哈希表中
		 * 
		 * @param key
		 *            键
		 * @param value
		 *            值
		 * @return 返回HashMapKit对象
		 */
		public MapBuilder<K, V> put(K key, V value) {
			map.put(key, value);
			return this;
		}

		/**
		 * 返回构建的Map对象
		 * 
		 * @return 返回构建的Map对象
		 */
		public Map<K, V> build() {
			return map;
		}

		/**
		 * 返回构建的Map对象
		 * 
		 * @return 返回构建的Map对象
		 */
		public Map<K, V> build(K key, V value) {
			map.put(key, value);
			return map;
		}

	}

}
