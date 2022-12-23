package cn.tenmg.dsl.utils;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Properties;

/**
 * DSL上下文
 * 
 * @author June wjzhao@aliyun.com
 * 
 * @since 1.2.3
 */
public abstract class DSLContext {

	private static final String DEFAULT_STRATEGIES_PATH = "dsl-context-loader.properties",
			CONFIG_LOCATION_KEY = "config.location";

	private static Properties configProperties = new Properties();

	static {
		loadConfig();
		replacePlaceHolder();
	}

	private DSLContext() {
	}

	/**
	 * 获取配置文件所在位置
	 * 
	 * @return 配置文件所在位置
	 */
	public static String getConfigLocation() {
		return getProperty(DSLContext.CONFIG_LOCATION_KEY);
	}

	/**
	 * 根据键获取配置的属性
	 * 
	 * @param key
	 *            键
	 * @return 配置属性值或null
	 */
	public static String getProperty(String key) {
		return configProperties.getProperty(key);
	}

	/**
	 * 根据键获取配置的属性
	 * 
	 * @param key
	 *            键
	 * @param defaultValue
	 *            默认值
	 * @return 配置属性值或默认值
	 */
	public static String getProperty(String key, String defaultValue) {
		return configProperties.getProperty(key, defaultValue);
	}

	/**
	 * 加载配置
	 */
	private static void loadConfig() {
		try {
			configProperties.putAll(PropertiesLoaderUtils.loadFromClassPath(DEFAULT_STRATEGIES_PATH));
		} catch (Exception e) {
		}
		try {
			configProperties.putAll(PropertiesLoaderUtils.loadFromClassPath("dsl-default.properties"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			configProperties.putAll(PropertiesLoaderUtils
					.loadFromClassPath(configProperties.getProperty(CONFIG_LOCATION_KEY, "dsl.properties")));
		} catch (Exception e) {
		}
	}

	/**
	 * 替换占位符
	 */
	private static void replacePlaceHolder() {
		Object value;
		Entry<Object, Object> entry;
		for (Iterator<Entry<Object, Object>> it = configProperties.entrySet().iterator(); it.hasNext();) {
			entry = it.next();
			value = entry.getValue();
			configProperties.put(entry.getKey(),
					value == null ? null : PlaceHolderUtils.replace(value.toString(), configProperties));
		}
	}
}
