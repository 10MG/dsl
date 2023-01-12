package cn.tenmg.dsl.utils;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Properties;

/**
 * 动态脚本语言配置工具类
 * 
 * @author June wjzhao@aliyun.com
 * 
 * @since 1.3.0
 */
public abstract class ConfigUtils {

	private static final String DEFAULT_STRATEGIES_PATH = "dsl-config-loader.properties",
			CONFIG_LOCATION_KEY = "config.location";

	private static Properties configProperties = new Properties();

	static {
		loadConfig();
		replacePlaceHolder();
	}

	private ConfigUtils() {
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
		PropertiesLoaderUtils.loadIgnoreException(configProperties, DEFAULT_STRATEGIES_PATH);
		PropertiesLoaderUtils.loadIgnoreException(configProperties, "dsl-default.properties");
		PropertiesLoaderUtils.loadIgnoreException(configProperties,
				configProperties.getProperty(CONFIG_LOCATION_KEY, "dsl.properties"));
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
