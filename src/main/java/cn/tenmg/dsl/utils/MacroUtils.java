package cn.tenmg.dsl.utils;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.tenmg.dsl.Macro;

/**
 * 宏工具类
 * 
 * @author June wjzhao@aliyun.com
 *
 * @since 1.0.0
 */
public abstract class MacroUtils {

	private static final String MACRO_KEY_PREFIX = "macro.", CLASS_SUFFIX = ".class",
			SCAN_PACKAGES_KEY = "scan.packages";

	private static final char

	/**
	 * 宏逻辑开始
	 */
	MACRO_LOGIC_START = '(',

			/**
			 * 宏逻辑结束
			 */
			MACRO_LOGIC_END = ')';

	/**
	 * 宏查找表
	 */
	private static final Map<String, String> macros = new HashMap<String, String>();
	private static final Map<String, Macro> MACROS = new HashMap<String, Macro>();

	static {
		try {
			int suffixLen = CLASS_SUFFIX.length();
			scanMacros("cn.tenmg.dsl.macro", suffixLen);
			String scanPackages = DSLContext.getProperty(SCAN_PACKAGES_KEY);
			if (scanPackages != null) {
				String[] basePackages = scanPackages.split(",");
				for (int i = 0; i < basePackages.length; i++) {
					scanMacros(basePackages[i].trim(), suffixLen);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	private static Macro getMacro(String name) {
		Macro macro = MACROS.get(name);
		if (macro == null) {
			synchronized (MACROS) {
				macro = MACROS.get(name);
				if (macro == null) {
					String className = macros.containsKey(name) ? macros.get(name)
							: DSLContext.getProperty(MACRO_KEY_PREFIX + name);
					if (StringUtils.isNotBlank(className)) {
						try {
							macro = (Macro) Class.forName(className).newInstance();
							MACROS.put(name, macro);
						} catch (InstantiationException | IllegalAccessException e) {
							throw new IllegalArgumentException("Cannot instantiate Macro for name '" + name + "'", e);
						} catch (ClassNotFoundException e) {
							throw new IllegalArgumentException("Wrong Macro configuration for name " + name + "'", e);
						}
					}
				}
			}
		}
		return macro;
	}

	public static final StringBuilder execute(StringBuilder dsl, Map<String, Object> context,
			Map<String, Object> params, boolean returnEmptyWhenNoMacro) {
		int len = dsl.length(), i = 0, backslashes = 0;
		char a = DSLUtils.BLANK_SPACE, b = DSLUtils.BLANK_SPACE;
		StringBuilder macroName = new StringBuilder(), paramName = null;
		Map<String, Object> usedParams = new HashMap<String, Object>();
		while (i < len) {
			char c = dsl.charAt(i);
			if (c == MACRO_LOGIC_START) {// 宏逻辑开始
				if (macroName.length() > 0) {
					Macro macro = getMacro(macroName.toString());
					if (macro == null) {// 找不到对应的宏
						if (returnEmptyWhenNoMacro) {
							return new StringBuilder();
						}
						return dsl;
					} else {
						StringBuilder logic = new StringBuilder();
						boolean isString = false;// 是否在字符串区域
						boolean isParam = false;// 是否在参数区域
						int deep = 0;// 逻辑深度
						while (++i < len) {
							a = b;
							b = c;
							c = dsl.charAt(i);
							if (isString) {
								if (c == DSLUtils.BACKSLASH) {
									backslashes++;
								} else {
									if (DSLUtils.isStringEnd(a, b, c, backslashes)) {// 字符串区域结束
										isString = false;
									}
									backslashes = 0;
								}
								logic.append(c);
							} else {
								if (c == MACRO_LOGIC_END) {// 宏逻辑结束
									if (deep == 0) {
										if (logic.length() > 0) {
											return execute(dsl, context, usedParams, macro, logic.toString(), i);
										} else {
											return dsl;
										}
									} else {
										logic.append(c);
										deep--;
									}
								} else if (c == MACRO_LOGIC_START) {
									logic.append(c);
									deep++;
								} else {
									if (isParam) {
										if (DSLUtils.isParamChar(c)) {
											paramName.append(c);
										} else {
											isParam = false;
											String name = paramName.toString();
											usedParams.put(name, params.get(name));
										}
										logic.append(c);
									} else {
										if (DSLUtils.isParamBegin(a, b, c)) {
											isParam = true;
											paramName = new StringBuilder();
											paramName.append(c);
											logic.setCharAt(logic.length() - 1, c);
										} else {
											logic.append(c);
										}
									}
								}
							}
						}
					}
					return dsl;
				} else {
					if (returnEmptyWhenNoMacro) {
						return new StringBuilder();
					}
					return dsl;
				}
			} else if (c <= DSLUtils.BLANK_SPACE) {
				if (macroName.length() > 0) {
					Macro macro = getMacro(macroName.toString());
					if (macro == null) {// 找不到对应的宏
						if (returnEmptyWhenNoMacro) {
							return new StringBuilder();
						}
						return dsl;
					} else {
						return execute(dsl, context, usedParams, macro, null, i - 1);// 当前字符为空白字符，则宏名称结束应该在前一个位置
					}
				} else {
					if (returnEmptyWhenNoMacro) {
						return new StringBuilder();
					}
					return dsl;
				}
			} else {
				macroName.append(c);
			}
			a = b;
			b = c;
			i++;
		}
		return dsl;
	}

	private static void scanMacros(String basePackage, int suffixLen) throws IOException, ClassNotFoundException {
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
						macros.put(StringUtils.isBlank(name) ? c.getSimpleName().toLowerCase() : name, className);
					}
				}
			}
		}
	}

	private static final StringBuilder execute(StringBuilder dsl, Map<String, Object> context,
			Map<String, Object> params, Macro macro, String logic, int macroEndIndex) {
		try {
			return macro.execute(logic, dsl.delete(0, macroEndIndex + 1), context, params);
		} catch (Exception e) {
			e.printStackTrace();
			return dsl;
		}
	}
}
