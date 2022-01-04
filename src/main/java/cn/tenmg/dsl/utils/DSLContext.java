package cn.tenmg.dsl.utils;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

import cn.tenmg.dsl.Macro;

/**
 * DSL上下文
 * 
 * @author June wjzhao@aliyun.com
 * 
 * @since 1.2.3
 */
public abstract class DSLContext {

	private static final String DEFAULT_STRATEGIES_PATH = "dsl-context-loader.properties",
			CONFIG_LOCATION_KEY = "config.location", CLASS_SUFFIX = ".class", SCAN_PACKAGES_KEY = "scan.packages";

	private static Properties defaultProperties, configProperties;

	static {
		try {
			defaultProperties = PropertiesLoaderUtils.loadFromClassPath(DEFAULT_STRATEGIES_PATH);
		} catch (Exception e) {
			defaultProperties = new Properties();
		}
		try {
			defaultProperties = PropertiesLoaderUtils.loadFromClassPath("dsl-default.properties");
		} catch (Exception e) {
		}
		try {
			String configLocation = defaultProperties.getProperty(CONFIG_LOCATION_KEY, "dsl.properties");
			configProperties = PropertiesLoaderUtils.loadFromClassPath(configLocation);
		} catch (Exception e) {
			configProperties = new Properties();
		}
		try {
			int suffixLen = CLASS_SUFFIX.length();
			scanMacros(defaultProperties, "cn.tenmg.dsl.macro", suffixLen);
			String scanPackages = getProperty(SCAN_PACKAGES_KEY);
			if (scanPackages != null) {
				String[] basePackages = scanPackages.split(",");
				for (int i = 0; i < basePackages.length; i++) {
					scanMacros(configProperties, basePackages[i].trim(), suffixLen);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
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
	 * 根据键获取配置的属性。优先查找用户配置属性，如果用户配置属性不存在从上下文配置中查找
	 * 
	 * @param key
	 *            键
	 * @return 配置属性值或null
	 */
	public static String getProperty(String key) {
		return configProperties.containsKey(key) ? configProperties.getProperty(key)
				: defaultProperties.getProperty(key);
	}

	/**
	 * 根据键获取配置的属性。优先查找用户配置属性，如果用户配置属性不存在从上下文配置中查找，如果均未找到则返回默认值
	 * 
	 * @param key
	 *            键
	 * @param defaultValue
	 *            默认值
	 * @return 配置属性值或默认值
	 */
	public static String getProperty(String key, String defaultValue) {
		return configProperties.containsKey(key) ? configProperties.getProperty(key)
				: (defaultProperties.containsKey(key) ? defaultProperties.getProperty(key) : defaultValue);
	}

	private static void scanMacros(Properties properties, String basePackage, int suffixLen)
			throws IOException, ClassNotFoundException {
		List<String> paths = FileUtils.scanPackage(basePackage, CLASS_SUFFIX);
		if (paths != null) {
			String className, name;
			for (int i = 0, size = paths.size(); i < size; i++) {
				className = paths.get(i);
				className = className.substring(0, className.length() - suffixLen).replaceAll("/", ".");
				Class<?> c = Class.forName(className);
				if (Macro.class.isAssignableFrom(c)) {
					cn.tenmg.dsl.annotion.Macro macro = c.getAnnotation(cn.tenmg.dsl.annotion.Macro.class);
					if (macro != null) {
						name = macro.name();
						properties.put(StringUtils.isBlank(name) ? c.getSimpleName().toLowerCase() : name, className);
					}
				}

			}
		}
	}

	public static void main(String[] args) {

	}

}
