package cn.tenmg.dsl.utils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Map工具类
 * 
 * @author June wjzhao@aliyun.com
 * 
 * @since 1.3.0
 */
public abstract class MapUtils {

	/**
	 * 判断 {@code Map} 对象是否为 {@code null} 或者不包含任何元素。
	 * 
	 * @param m
	 *            {@code Map} 对象
	 * @return 如果 {@code Map} 对象 {@code m} 为 {@code null} 或者非 {@code null}
	 *         但不包含任何元素，则返回 {@code true}；否则，返回 {@code false}。
	 */
	public static boolean isEmpty(Map<?, ?> m) {
		return m == null || m.isEmpty();
	}

	/**
	 * 判断 {@code Map} 对象是否非 {@code null} 且至少包含一个元素。
	 * 
	 * @param m
	 *            {@code Map} 对象
	 * @return 如果 {@code Map} 对象非 {@code null} 且至少包含一个元素，则返回 {@code true}；否则，返回
	 *         {@code false}。
	 */
	public static boolean isNotEmpty(Map<?, ?> m) {
		return m != null && !m.isEmpty();
	}

	/**
	 * 使用键集合移除指定查找表中的元素。
	 * 
	 * @param map
	 *            指定查找表
	 * @param keys
	 *            键集合
	 */
	public static <K> void removeAll(Map<K, ?> map, Set<K> keys) {
		for (Iterator<?> it = keys.iterator(); it.hasNext();) {
			map.remove(it.next());
		}
	}

	/**
	 * 创建一个 {@code HashMap<K, V>} 对象。
	 * 
	 * @return 新建的 {@code HashMap<K, V>} 对象。
	 */
	public static <K, V> HashMap<K, V> newHashMap() {
		return new HashMap<K, V>();
	}

	/**
	 * 创建一个初始容量为 {@code initialCapacity} 的 {@code HashMap<K, V>} 对象。
	 * 
	 * @return 新建的 {@code HashMap<K, V>} 对象。
	 */
	public static <K, V> HashMap<K, V> newHashMap(int initialCapacity) {
		return new HashMap<K, V>(initialCapacity);
	}

	/**
	 * 创建一个含有一个键为 {@code key}、值为 {@code value} 元素的 {@code HashMap<K, V>} 对象 。
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @return 新建的 {@code HashMap<K, V>} 对象。
	 */
	public static <K, V> HashMap<K, V> newHashMap(K key, V value) {
		HashMap<K, V> map = new HashMap<K, V>();
		map.put(key, value);
		return map;
	}

	/**
	 * 创建一个初始容量为 {@code initialCapacity}，且含有一个键为 {@code key}、值为 {@code value} 元素的
	 * {@code HashMap<K, V>} 对象。
	 * 
	 * @param initialCapacity
	 *            初始容量
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @return 新建的 {@code HashMap<K, V>} 对象。
	 */
	public static <K, V> HashMap<K, V> newHashMap(int initialCapacity, K key, V value) {
		HashMap<K, V> map = new HashMap<K, V>(initialCapacity);
		map.put(key, value);
		return map;
	}

	/**
	 * 创建一个 {@code ConcurrentHashMap<K, V>} 对象。
	 * 
	 * @return 新建的 {@code ConcurrentHashMap<K, V>} 对象。
	 */
	public static <K, V> ConcurrentHashMap<K, V> newConcurrentHashMap() {
		return new ConcurrentHashMap<K, V>();
	}

	/**
	 * 创建一个初始容量为 {@code initialCapacity} 的 {@code ConcurrentHashMap<K, V>} 对象。
	 * 
	 * @return 新建的 {@code ConcurrentHashMap<K, V>} 对象。
	 */
	public static <K, V> ConcurrentHashMap<K, V> newConcurrentHashMap(int initialCapacity) {
		return new ConcurrentHashMap<K, V>(initialCapacity);
	}

	/**
	 * 创建一个含有一个键为 {@code key}、值为 {@code value} 元素的 {@code ConcurrentHashMap<K, V>}
	 * 对象 。
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @return 新建的 {@code ConcurrentHashMap<K, V>} 对象。
	 */
	public static <K, V> ConcurrentHashMap<K, V> newConcurrentHashMap(K key, V value) {
		ConcurrentHashMap<K, V> map = new ConcurrentHashMap<K, V>();
		map.put(key, value);
		return map;
	}

	/**
	 * 创建一个初始容量为 {@code initialCapacity}，且含有一个键为 {@code key}、值为 {@code value} 元素的
	 * {@code ConcurrentHashMap<K, V>} 对象。
	 * 
	 * @param initialCapacity
	 *            初始容量
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @return 新建的 {@code HashMap<K, V>} 对象。
	 */
	public static <K, V> ConcurrentHashMap<K, V> newConcurrentHashMap(int initialCapacity, K key, V value) {
		ConcurrentHashMap<K, V> map = new ConcurrentHashMap<K, V>(initialCapacity);
		map.put(key, value);
		return map;
	}

	/**
	 * 将一个 {@code Map<K, V>} 对象转换为一个 {@code HashMap<K, V>} 对象。
	 * 
	 * @param map
	 *            {@code Map<K, V>} 对象
	 * @return 新建的 {@code HashMap<K, V>} 对象。
	 */
	public static <K, V> HashMap<K, V> toHashMap(Map<K, V> map) {
		HashMap<K, V> copy = new HashMap<K, V>(map.size());
		copy.putAll(map);
		return copy;
	}

	/**
	 * 将一个 {@code Map<K, V>} 对象转换为一个 {@code ConcurrentHashMap<K, V>} 对象。
	 * 
	 * @param map
	 *            {@code Map<K, V>} 对象
	 * @return 新建的 {@code ConcurrentHashMap<K, V>} 对象。
	 */
	public static <K, V> ConcurrentHashMap<K, V> toConcurrentHashMap(Map<K, V> map) {
		ConcurrentHashMap<K, V> copy = new ConcurrentHashMap<K, V>(map.size());
		copy.putAll(map);
		return copy;
	}

	/**
	 * 创建一个含有 {@code map} 所有元素的 {@code MapBuilder<M, K, V>} 对象。
	 * 
	 * @param map
	 *            {@code Map<K, V>} 对象
	 * @return 新建的 {@code MapBuilder<M, K, V>} 对象。
	 */
	public static <M extends Map<K, V>, K, V> MapBuilder<M, K, V> newMapBuilder(M map) {
		return new MapBuilder<M, K, V>(map);
	}

	/**
	 * 创建一个含有一个键为 {@code key}、值为 {@code value} 元素的
	 * {@code MapBuilder<HashMap<K, V>, K, V>} 对象。
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @return 新建的哈希查找表构建器，即 {@code MapBuilder<HashMap<K, V>, K, V>} 对象。
	 */
	public static <K, V> MapBuilder<HashMap<K, V>, K, V> newHashMapBuilder(K key, V value) {
		return new MapBuilder<HashMap<K, V>, K, V>(newHashMap(key, value));
	}

	/**
	 * 创建一个含有一个键为 {@code key}、值为 {@code value} 元素，且初始容量为 {@code initialCapacity} 的
	 * {@code MapBuilder<HashMap<K, V>, K, V>} 对象。
	 * 
	 * @param initialCapacity
	 *            初始容量
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @return 新建的 {@code MapBuilder<HashMap<K, V>, K, V>} 对象。
	 */
	public static <K, V> MapBuilder<HashMap<K, V>, K, V> newHashMapBuilder(int initialCapacity, K key, V value) {
		return new MapBuilder<HashMap<K, V>, K, V>(newHashMap(initialCapacity, key, value));
	}

	/**
	 * 创建一个 {@code MapBuilder<HashMap<K, V>, K, V>} 对象。
	 * 
	 * @return 新建的 {@code MapBuilder<HashMap<K, V>, K, V>} 对象。
	 */
	public static <K, V> MapBuilder<HashMap<K, V>, K, V> newHashMapBuilder() {
		return new MapBuilder<HashMap<K, V>, K, V>(newHashMap());
	}

	/**
	 * 创建一个初始容量为 {@code initialCapacity} 的 {@code MapBuilder<HashMap<K, V>, K, V>}
	 * 对象。
	 * 
	 * @return 新建的 {@code MapBuilder<HashMap<K, V>, K, V>} 对象。
	 */
	public static <K, V> MapBuilder<HashMap<K, V>, K, V> newHashMapBuilder(int initialCapacity) {
		return new MapBuilder<HashMap<K, V>, K, V>(newHashMap(initialCapacity));
	}

	/**
	 * 创建一个键类型为 {@code keyType} 的 {@code MapBuilder<HashMap<K, V>, K, V>} 对象。
	 * 
	 * @param keyType
	 *            键类型
	 * @return 新建的 {@code MapBuilder<HashMap<K, V>, K, V>} 对象。
	 */
	public static <K, V> MapBuilder<HashMap<K, V>, K, V> newHashMapBuilder(Class<K> keyType) {
		return new MapBuilder<HashMap<K, V>, K, V>(newHashMap());
	}

	/**
	 * 创建一个键类型为 {@code keyType}，初始容量为 {@code initialCapacity} 的
	 * {@code MapBuilder<HashMap<K, V>, K, V>} 对象。
	 * 
	 * @param initialCapacity
	 *            初始容量
	 * @param keyType
	 *            键类型
	 * @return 新建的 {@code MapBuilder<HashMap<K, V>, K, V>} 对象。
	 */
	public static <K, V> MapBuilder<HashMap<K, V>, K, V> newHashMapBuilder(int initialCapacity, Class<K> keyType) {
		return new MapBuilder<HashMap<K, V>, K, V>(newHashMap(initialCapacity));
	}

	/**
	 * 创建一个键类型为 {@code keyType}、值类型为 {@code valueType} 的
	 * {@code MapBuilder<HashMap<K, V>, K, V>} 对象。
	 * 
	 * @param keyType
	 *            键类型
	 * @param valueType
	 *            值类型
	 * @return 新建的 {@code MapBuilder<HashMap<K, V>, K, V>} 对象。
	 */
	public static <K, V> MapBuilder<HashMap<K, V>, K, V> newHashMapBuilder(Class<K> keyType, Class<V> valueType) {
		return new MapBuilder<HashMap<K, V>, K, V>(newHashMap());
	}

	/**
	 * 创建一个键类型为 {@code keyType}、值类型为 {@code valueType}，初始容量为 {@code initialCapacity}
	 * 的 {@code MapBuilder<HashMap<K, V>, K, V>} 对象。
	 * 
	 * @param initialCapacity
	 *            初始容量
	 * @param keyType
	 *            键类型
	 * @param valueType
	 *            值类型
	 * @return 新建的 {@code MapBuilder<HashMap<K, V>, K, V>} 对象。
	 */
	public static <K, V> MapBuilder<HashMap<K, V>, K, V> newHashMapBuilder(int initialCapacity, Class<K> keyType,
			Class<V> valueType) {
		return new MapBuilder<HashMap<K, V>, K, V>(newHashMap(initialCapacity));
	}

	/**
	 * 创建一个含有一个键为 {@code key}、值为 {@code value} 元素的
	 * {@code MapBuilder<ConcurrentHashMap<K, V>, K, V>} 对象。
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @return 新建的哈希查找表构建器，即 {@code MapBuilder<ConcurrentHashMap<K, V>, K, V>} 对象。
	 */
	public static <K, V> MapBuilder<ConcurrentHashMap<K, V>, K, V> newConcurrentHashMapBuilder(K key, V value) {
		return new MapBuilder<ConcurrentHashMap<K, V>, K, V>(newConcurrentHashMap(key, value));
	}

	/**
	 * 创建一个含有一个键为 {@code key}、值为 {@code value} 元素，且初始容量为 {@code initialCapacity} 的
	 * {@code MapBuilder<ConcurrentHashMap<K, V>, K, V>} 对象。
	 * 
	 * @param initialCapacity
	 *            初始容量
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @return 新建的 {@code MapBuilder<ConcurrentHashMap<K, V>, K, V>} 对象。
	 */
	public static <K, V> MapBuilder<ConcurrentHashMap<K, V>, K, V> newConcurrentHashMapBuilder(int initialCapacity,
			K key, V value) {
		return new MapBuilder<ConcurrentHashMap<K, V>, K, V>(newConcurrentHashMap(initialCapacity, key, value));
	}

	/**
	 * 创建一个 {@code MapBuilder<ConcurrentHashMap<K, V>, K, V>} 对象。
	 * 
	 * @return 新建的 {@code MapBuilder<ConcurrentHashMap<K, V>, K, V>} 对象。
	 */
	public static <K, V> MapBuilder<ConcurrentHashMap<K, V>, K, V> newConcurrentHashMapBuilder() {
		return new MapBuilder<ConcurrentHashMap<K, V>, K, V>(newConcurrentHashMap());
	}

	/**
	 * 创建一个初始容量为 {@code initialCapacity} 的
	 * {@code MapBuilder<ConcurrentHashMap<K, V>, K, V>} 对象。
	 * 
	 * @return 新建的 {@code MapBuilder<ConcurrentHashMap<K, V>, K, V>} 对象。
	 */
	public static <K, V> MapBuilder<ConcurrentHashMap<K, V>, K, V> newConcurrentHashMapBuilder(int initialCapacity) {
		return new MapBuilder<ConcurrentHashMap<K, V>, K, V>(newConcurrentHashMap(initialCapacity));
	}

	/**
	 * 创建一个键类型为 {@code keyType} 的 {@code MapBuilder<ConcurrentHashMap<K, V>, K, V>}
	 * 对象。
	 * 
	 * @param keyType
	 *            键类型
	 * @return 新建的 {@code MapBuilder<ConcurrentHashMap<K, V>, K, V>} 对象。
	 */
	public static <K, V> MapBuilder<ConcurrentHashMap<K, V>, K, V> newConcurrentHashMapBuilder(Class<K> keyType) {
		return new MapBuilder<ConcurrentHashMap<K, V>, K, V>(newConcurrentHashMap());
	}

	/**
	 * 创建一个键类型为 {@code keyType}，初始容量为 {@code initialCapacity} 的
	 * {@code MapBuilder<ConcurrentHashMap<K, V>, K, V>} 对象。
	 * 
	 * @param initialCapacity
	 *            初始容量
	 * @param keyType
	 *            键类型
	 * @return 新建的 {@code MapBuilder<ConcurrentHashMap<K, V>, K, V>} 对象。
	 */
	public static <K, V> MapBuilder<ConcurrentHashMap<K, V>, K, V> newConcurrentHashMapBuilder(int initialCapacity,
			Class<K> keyType) {
		return new MapBuilder<ConcurrentHashMap<K, V>, K, V>(newConcurrentHashMap(initialCapacity));
	}

	/**
	 * 创建一个键类型为 {@code keyType}、值类型为 {@code valueType} 的
	 * {@code MapBuilder<ConcurrentHashMap<K, V>, K, V>} 对象。
	 * 
	 * @param keyType
	 *            键类型
	 * @param valueType
	 *            值类型
	 * @return 新建的 {@code MapBuilder<ConcurrentHashMap<K, V>, K, V>} 对象。
	 */
	public static <K, V> MapBuilder<ConcurrentHashMap<K, V>, K, V> newConcurrentHashMapBuilder(Class<K> keyType,
			Class<V> valueType) {
		return new MapBuilder<ConcurrentHashMap<K, V>, K, V>(newConcurrentHashMap());
	}

	/**
	 * 创建一个键类型为 {@code keyType}、值类型为 {@code valueType}，初始容量为 {@code initialCapacity}
	 * 的 {@code MapBuilder<ConcurrentHashMap<K, V>, K, V>} 对象。
	 * 
	 * @param initialCapacity
	 *            初始容量
	 * @param keyType
	 *            键类型
	 * @param valueType
	 *            值类型
	 * @return 新建的 {@code MapBuilder<ConcurrentHashMap<K, V>, K, V>} 对象。
	 */
	public static <K, V> MapBuilder<ConcurrentHashMap<K, V>, K, V> newConcurrentHashMapBuilder(int initialCapacity,
			Class<K> keyType, Class<V> valueType) {
		return new MapBuilder<ConcurrentHashMap<K, V>, K, V>(newConcurrentHashMap(initialCapacity));
	}

	/**
	 * 创建一个 {@code MapBuilder<HashMap<K, V>, K, V>} 含有 {@code map} 所有元素的
	 * {@code MapBuilder<HashMap<K, V>, K, V>} 对象。
	 * 
	 * @param map
	 *            查找表对象
	 * @return 新建的 {@code MapBuilder<HashMap<K, V>, K, V>} 对象。
	 */
	public static <K, V> MapBuilder<HashMap<K, V>, K, V> toHashMapBuilder(Map<K, V> map) {
		HashMap<K, V> copy = new HashMap<K, V>(map.size());
		copy.putAll(map);
		return new MapBuilder<HashMap<K, V>, K, V>(copy);
	}

	/**
	 * 创建一个 {@code MapBuilder<ConcurrentHashMap<K, V>, K, V>} 含有 {@code map} 所有元素的
	 * {@code MapBuilder<ConcurrentHashMap<K, V>, K, V>} 对象。
	 * 
	 * @param map
	 *            查找表对象
	 * @return 新建的 {@code MapBuilder<ConcurrentHashMap<K, V>, K, V>} 对象。
	 */
	public static <K, V> MapBuilder<ConcurrentHashMap<K, V>, K, V> toConcurrentHashMapBuilder(Map<K, V> map) {
		ConcurrentHashMap<K, V> copy = new ConcurrentHashMap<K, V>(map.size());
		copy.putAll(map);
		return new MapBuilder<ConcurrentHashMap<K, V>, K, V>(copy);
	}

	/**
	 * 查找表构建器
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
	public static class MapBuilder<M extends Map<K, V>, K, V> {

		private final M map;

		public MapBuilder(M map) {
			super();
			this.map = map;
		}

		/**
		 * 如果查找表非 {@code null}，则将查找表中的所有元素存入查找表构建器中
		 * 
		 * @param map
		 *            查找表
		 * @return 查找表构建器
		 */
		public MapBuilder<M, K, V> putAll(Map<K, V> map) {
			if (map != null) {
				this.map.putAll(map);
			}
			return this;
		}

		/**
		 * 向查找表构建器存入一个键为 {@code key}，值为 {@code value} 的元素
		 * 
		 * @param key
		 *            键
		 * @param value
		 *            值
		 * @return 查找表构建器
		 */
		public MapBuilder<M, K, V> put(K key, V value) {
			map.put(key, value);
			return this;
		}

		/**
		 * 返回构建的查找表对象
		 * 
		 * @return 构建的查找表对象
		 */
		public M build() {
			return map;
		}

		/**
		 * 向查找表构建器存入一个键为 {@code key}，值为 {@code value} 的元素后返回构建的查找表对象
		 * 
		 * @param key
		 *            键
		 * @param value
		 *            值
		 * @return 构建的查找表对象
		 */
		public M build(K key, V value) {
			map.put(key, value);
			return map;
		}

		/**
		 * 返回构建的查找表对象
		 * 
		 * @return 返回构建的查找表对象
		 */
		public M build(Map<K, V> map) {
			this.map.putAll(map);
			return this.map;
		}

	}

}
