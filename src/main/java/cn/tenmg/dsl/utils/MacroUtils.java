package cn.tenmg.dsl.utils;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

import cn.tenmg.dsl.DSLContext;
import cn.tenmg.dsl.Macro;
import cn.tenmg.dsl.exception.MacroException;

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
		Macro macro;
		ServiceLoader<Macro> loader = ServiceLoader.load(Macro.class);
		for (Iterator<Macro> it = loader.iterator(); it.hasNext();) {
			macro = it.next();
			MACROS.put(getMacroName(macro.getClass()), macro);
		}
		try {
			int suffixLen = CLASS_SUFFIX.length();
			String scanPackages = ConfigUtils.getProperty(SCAN_PACKAGES_KEY);
			if (scanPackages != null) {
				String[] basePackages = scanPackages.split(",");
				for (int i = 0; i < basePackages.length; i++) {
					scanMacros(basePackages[i].trim(), suffixLen);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private MacroUtils() {
	}

	public static final Dslf execute(DSLContext context, Map<String, Object> attributes, StringBuilder dsl,
			Map<String, Object> params, boolean emptyWhenNoMacro) {
		Macro macro;
		String macroName, paramName;
		int len = dsl.length(), i = 0, backslashes = 0;
		char a = DSLUtils.BLANK_SPACE, b = DSLUtils.BLANK_SPACE;
		StringBuilder macroNameBuilder = new StringBuilder(), paramNameBuilder = null;
		Map<String, Object> usedParams = new HashMap<String, Object>();
		while (i < len) {
			char c = dsl.charAt(i);
			if (c == MACRO_LOGIC_START) {// 宏逻辑开始
				if (macroNameBuilder.length() > 0) {
					macroName = macroNameBuilder.toString();
					macro = getMacro(macroName);
					if (macro == null) {// 找不到对应的宏
						if (emptyWhenNoMacro) {
							dsl.setLength(0);
						}
						return newDslf(dsl, false);
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
									if (isParam) {// 参数放在后面，宏逻辑随着参数名结束而结束
										isParam = false;
										paramName = paramNameBuilder.toString();
										usedParams.put(paramName, params.get(paramName));
									}

									if (deep == 0) {
										if (logic.length() > 0) {
											try {
												return newDslf(dsl, macro.execute(context, attributes, logic.toString(),
														dsl.delete(0, i + 1), usedParams));
											} catch (Exception e) {
												throw new MacroException(
														"Exception occurred when executing macro ".concat(macroName),
														e);
											}
										} else {
											return newDslf(dsl, false);
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
											paramNameBuilder.append(c);
										} else {
											isParam = false;
											paramName = paramNameBuilder.toString();
											usedParams.put(paramName, params.get(paramName));
										}
									} else {
										if (DSLUtils.isParamBegin(a, b, c)) {
											isParam = true;
											paramNameBuilder = new StringBuilder().append(c);
										}
									}
									logic.append(c);
								}
							}
						}
					}
					return newDslf(dsl, false);
				} else {
					if (emptyWhenNoMacro) {
						dsl.setLength(0);
					}
					return newDslf(dsl, false);
				}
			} else if (c <= DSLUtils.BLANK_SPACE) {
				if (macroNameBuilder.length() > 0) {
					macroName = macroNameBuilder.toString();
					macro = getMacro(macroName);
					if (macro == null) {// 找不到对应的宏
						if (emptyWhenNoMacro) {
							dsl.setLength(0);
						}
						return newDslf(dsl, false);
					} else {// 当前字符为空白字符，则宏名称结束应该在前一个位置
						try {
							return newDslf(dsl, macro.execute(context, attributes, null, dsl.delete(0, i), usedParams));
						} catch (Exception e) {
							throw new MacroException("An exception occurred when executing macro ".concat(macroName),
									e);
						}
					}
				} else {
					if (emptyWhenNoMacro) {
						dsl.setLength(0);
					}
					return newDslf(dsl, false);
				}
			} else {
				macroNameBuilder.append(c);
			}
			a = b;
			b = c;
			i++;
		}
		if (emptyWhenNoMacro) {
			dsl.setLength(0);
		}
		return newDslf(dsl, false);
	}

	/**
	 * 动态脚本语言片段
	 * 
	 * @author June wjzhao@aliyun.com
	 * 
	 * @since 1.3.0
	 */
	public static class Dslf {

		private StringBuilder value;

		private boolean dslfAsScript;

		public StringBuilder getValue() {
			return value;
		}

		public boolean isDslfAsScript() {
			return dslfAsScript;
		}

		public Dslf(StringBuilder value, boolean dslfAsScript) {
			super();
			this.value = value;
			this.dslfAsScript = dslfAsScript;
		}

	}

	private static final Dslf newDslf(StringBuilder value, boolean dslfAsScript) {
		return new Dslf(value, dslfAsScript);
	}

	private static String getMacroName(Class<?> type) {
		cn.tenmg.dsl.annotion.Macro macro = type.getAnnotation(cn.tenmg.dsl.annotion.Macro.class);
		if (macro != null) {
			String name = macro.name();
			if (StringUtils.isBlank(name)) {
				name = macro.value();
			}
			return StringUtils.isBlank(name) ? type.getSimpleName().toLowerCase() : name;
		}
		return type.getSimpleName().toLowerCase();
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

	private static Macro getMacro(String name) {
		Macro macro = MACROS.get(name);
		if (macro == null) {
			synchronized (MACROS) {
				macro = MACROS.get(name);
				if (macro == null) {
					String className = macros.containsKey(name) ? macros.get(name)
							: ConfigUtils.getProperty(MACRO_KEY_PREFIX + name);
					if (StringUtils.isNotBlank(className)) {
						try {
							macro = (Macro) Class.forName(className).getDeclaredConstructor().newInstance();
							MACROS.put(name, macro);
						} catch (InstantiationException | IllegalArgumentException | InvocationTargetException
								| NoSuchMethodException | SecurityException | IllegalAccessException e) {
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

}
