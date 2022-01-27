package cn.tenmg.dsl.utils;

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

	private static Properties configProperties;

	static {
		try {
			configProperties = PropertiesLoaderUtils.loadFromClassPath(DEFAULT_STRATEGIES_PATH);
		} catch (Exception e) {
			e.printStackTrace();
			configProperties = new Properties();
		}
		try {
			configProperties.putAll(PropertiesLoaderUtils.loadFromClassPath("dsl-default.properties"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			String configLocation = configProperties.getProperty(CONFIG_LOCATION_KEY, "dsl.properties");
			configProperties = PropertiesLoaderUtils.loadFromClassPath(configLocation);
		} catch (Exception e) {
			configProperties = new Properties();
		}
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
		return configProperties.containsKey(key) ? configProperties.getProperty(key) : defaultValue;
	}

}
