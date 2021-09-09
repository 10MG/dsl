package cn.tenmg.dsl.utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import cn.tenmg.dsl.NamedScript;
import cn.tenmg.dsl.ParamsParser;
import cn.tenmg.dsl.Script;

/**
 * 动态脚本语言(DSL)工具类
 * 
 * @author 赵伟均 wjzhao@aliyun.com
 * 
 * @since 1.0.0
 */
public abstract class DSLUtils {

	public static final char BACKSLASH = '\\', BLANK_SPACE = '\u0020', PARAM_BEGIN = ':', COMMA = ',', PARAM_MARK = '?';

	private static final char SINGLE_QUOTATION_MARK = '\'', LINE_BREAK = '\n', DYNAMIC_BEGIN[] = { '#', '[' },
			DYNAMIC_END = ']';

	private static char SINGLELINE_COMMENT_PREFIXES[][], MILTILINE_COMMENT_PREFIXES[][] = { { '/', '*' } },
			MILTILINE_COMMENT_SUFFIXES[][] = { { '*', '/' } };

	private static final String LINE_SPLITOR = "\r\n", EMPTY_CHARS = LINE_SPLITOR + "\t ";

	static {
		try {
			Properties config = PropertiesLoaderUtils.loadFromClassPath("dsl.properties");
			String[] singlelineCommentPrefixes = config.getProperty("comment.singleline", "--").split(","),
					miltilineComments = config.getProperty("comment.multiline", "/*,*/").split(";");
			SINGLELINE_COMMENT_PREFIXES = new char[singlelineCommentPrefixes.length][];
			MILTILINE_COMMENT_PREFIXES = new char[miltilineComments.length][];
			MILTILINE_COMMENT_SUFFIXES = new char[miltilineComments.length][];
			for (int i = 0; i < singlelineCommentPrefixes.length; i++) {
				SINGLELINE_COMMENT_PREFIXES[i] = singlelineCommentPrefixes[i].toCharArray();
			}
			String[] miltilineComment;
			for (int i = 0; i < miltilineComments.length; i++) {
				miltilineComment = miltilineComments[i].split(",");
				MILTILINE_COMMENT_PREFIXES[i] = miltilineComment[0].toCharArray();
				MILTILINE_COMMENT_SUFFIXES[i] = miltilineComment[1].toCharArray();
			}
		} catch (Exception e) {
		}
	}

	/**
	 * 将指定的源动态脚本语言（DSL）及参数转换为NamedScript对象。NamedScript对象含有带命名参数的脚本（script），及实际使用的参数查找表（params）。动态脚本的动态片段以“#[”作为前缀，以“]”作为后缀。转换的过程中含有有效参数（参数值非null）的动态片段将被保留并去除“#[”前缀和后缀“]”，否则动态片段将被去除。另外，使用单引号“''”包裹的字符串将被完整保留
	 * 
	 * @param dsl
	 *            源DSL脚本
	 * @param params
	 *            参数查找表
	 * @return 返回NamedScript对象
	 */
	public static NamedScript parse(String dsl, Map<String, Object> params) {
		if (params == null) {
			params = new HashMap<String, Object>();
		}
		if (StringUtils.isBlank(dsl)) {
			return new NamedScript(dsl, params);
		}
		dsl = StringUtils.stripStart(dsl, LINE_SPLITOR);// 去除仅含换行符的行
		dsl = StringUtils.stripEnd(dsl, EMPTY_CHARS);// 去除空白字符
		int len = dsl.length();
		if (len < 3) {// 长度小于最小动态脚本单元 “#[]”的长度直接返回
			return new NamedScript(dsl, params);
		}
		int i = 0, deep = 0, backslashes = 0;// 连续反斜杠数
		char a = BLANK_SPACE, b = BLANK_SPACE, c;
		boolean isString = false, // 是否在字符串区域
				isSinglelineComment = false, // 是否在单行注释区域
				isMiltilineComment = false, // 是否在多行注释区域
				isDynamic = false, // 是否在动态脚本区域
				isParam = false;// 是否在参数区域
		StringBuilder script = new StringBuilder(), paramName = new StringBuilder();
		Map<String, Object> usedParams = new HashMap<String, Object>();
		HashMap<Integer, Boolean> inValidMap = new HashMap<Integer, Boolean>();
		HashMap<Integer, Set<String>> validMap = new HashMap<Integer, Set<String>>();
		HashMap<Integer, StringBuilder> dslMap = new HashMap<Integer, StringBuilder>();
		HashMap<Integer, Map<String, Object>> contexts = new HashMap<Integer, Map<String, Object>>();
		while (i < len) {
			c = dsl.charAt(i);
			if (isString) {
				if (c == BACKSLASH) {
					backslashes++;
				} else {
					if (isStringEnd(a, b, c, backslashes)) {// 字符串区域结束
						isString = false;
					}
					backslashes = 0;
				}
				if (deep > 0) {
					dslMap.get(deep).append(c);
				} else {
					script.append(c);
				}
			} else if (c == SINGLE_QUOTATION_MARK) {// 字符串区域开始
				isString = true;
				if (deep > 0) {
					dslMap.get(deep).append(c);
				} else {
					script.append(c);
				}
			} else if (isSinglelineComment) {
				if (isDynamic && isDynamicEnd(c)) {// 当前字符为动态脚本结束字符
					isSinglelineComment = false;
					if (inValidMap.get(deep) == null) {// 不含无效参数
						processDSL(params, script, dslMap, usedParams, inValidMap, validMap, contexts, deep, false);
						deep--;
					} else if (deep > 0) {
						processDSL(params, script, dslMap, usedParams, inValidMap, validMap, contexts, deep, true);
						deep--;
					}
					if (deep < 1) {// 已离开动态脚本区域
						isDynamic = false;
						deleteRedundantBlank(script);// 删除多余空白字符
					} else {
						deleteRedundantBlank(dslMap.get(deep));// 删除多余空白字符
					}
				} else {
					if (c == LINE_BREAK) {
						isSinglelineComment = false;
					}
					if (deep > 0) {
						dslMap.get(deep).append(c);
					} else {
						script.append(c);
					}
				}
			} else if (isMiltilineComment) {
				if (isMiltilineCommentEnd(b, c)) {
					isMiltilineComment = false;
				}
				if (deep > 0) {
					dslMap.get(deep).append(c);
				} else {
					script.append(c);
				}
			} else if (isSinglelineCommentBegin(b, c)) {
				isSinglelineComment = true;
				if (deep > 0) {
					dslMap.get(deep).append(c);
				} else {
					script.append(c);
				}
			} else if (isMiltilineCommentBegin(b, c)) {
				isMiltilineComment = true;
				if (deep > 0) {
					dslMap.get(deep).append(c);
				} else {
					script.append(c);
				}
			} else if (isDynamic) {// 当前字符处于动态脚本区域
				if (isDynamicEnd(c)) {// 结束当前动态脚本区域
					if (isParam) {// 处于动态参数区域
						isParam = false;// 结束动态参数区域
						String name = paramName.toString();
						Object value = params.get(name);
						if (value != null) {
							validMap.get(deep).add(name);
							paramName.setLength(0);
						} else if (deep > 0) {
							inValidMap.put(deep, Boolean.TRUE);// 含有无效参数标记
						}
					}
					if (inValidMap.get(deep) == null) {// 不含无效参数
						processDSL(params, script, dslMap, usedParams, inValidMap, validMap, contexts, deep, false);
						deep--;
					} else if (deep > 0) {
						processDSL(params, script, dslMap, usedParams, inValidMap, validMap, contexts, deep, true);
						deep--;
					}
					if (deep < 1) {// 已离开动态脚本区域
						isDynamic = false;
						deleteRedundantBlank(script);// 删除多余空白字符
					} else {
						deleteRedundantBlank(dslMap.get(deep));// 删除多余空白字符
					}
				} else {
					if (isParam) {// 处于动态参数区域
						if (isParamChar(c)) {
							paramName.append(c);
							StringBuilder dslBuilder = dslMap.get(deep);
							if (dslBuilder == null) {
								dslBuilder = new StringBuilder();
								dslMap.put(deep, dslBuilder);
							}
							dslBuilder.append(c);
						} else {// 离开动态参数区域
							isParam = false;
							String name = paramName.toString();
							Object value = params.get(name);
							if (value != null) {
								validMap.get(deep).add(name);
							} else if (deep >= 0) {
								inValidMap.put(deep, Boolean.TRUE);// 含有无效参数标记
							}
							paramName.setLength(0);

							if (isDynamicBegin(b, c)) {// 嵌套的新的动态脚本区域
								StringBuilder dslBuilder = dslMap.get(deep);
								dslBuilder.deleteCharAt(dslBuilder.length() - 1);// 删除#号
								deep++;
								dslMap.put(deep, new StringBuilder());
								validMap.put(deep, new HashSet<String>());
							} else {
								StringBuilder dslBuilder = dslMap.get(deep);
								if (dslBuilder == null) {
									dslBuilder = new StringBuilder();
									dslMap.put(deep, dslBuilder);
								}
								dslBuilder.append(c);
							}
						}
					} else {// 未处于动态参数区域
						if (isDynamicBegin(b, c)) {// 嵌套的新的动态脚本区域
							StringBuilder dslBuilder = dslMap.get(deep);
							dslBuilder.deleteCharAt(dslBuilder.length() - 1);// 删除#号
							deep++;
							dslMap.put(deep, new StringBuilder());
							validMap.put(deep, new HashSet<String>());
						} else {
							if (isParamBegin(a, b, c)) {
								isParam = true;
								paramName.setLength(0);
								paramName.append(c);
							}

							StringBuilder dslBuilder = dslMap.get(deep);
							if (dslBuilder == null) {
								dslBuilder = new StringBuilder();
								dslMap.put(deep, dslBuilder);
							}
							dslBuilder.append(c);
						}
					}
				}
			} else {// 当前字符未处于动态脚本区域
				if (isDynamicBegin(b, c)) {
					isDynamic = true;
					script.deleteCharAt(script.length() - 1);
					deep++;
					dslMap.put(deep, new StringBuilder());
					validMap.put(deep, new HashSet<String>());
				} else {
					if (isParam) {// 处于参数区域
						if (isParamChar(c)) {
							paramName.append(c);
							if (i == len - 1) {
								String name = paramName.toString();
								usedParams.put(name, params.get(name));
							}
						} else {// 离开参数区域
							isParam = false;
							String name = paramName.toString();
							usedParams.put(name, params.get(name));
							if (i < len - 1) {
								paramName.setLength(0);
							}
						}
					} else {// 未处于参数区域
						if (isParamBegin(a, b, c)) {
							isParam = true;
							paramName.setLength(0);
							paramName.append(c);
						}
					}
					script.append(c);
				}
			}
			a = b;
			b = c;
			i++;
		}
		return new NamedScript(script.toString(), usedParams);
	}

	/**
	 * 将指定的源动态脚本语言（DSL）及参数转换为NamedScript对象。NamedScript对象含有带命名参数的脚本（script），及实际使用的参数查找表（params）。动态脚本的动态片段以“#[”作为前缀，以“]”作为后缀。转换的过程中含有有效参数（参数值非null）的动态片段将被保留并去除“#[”前缀和后缀“]”，否则动态片段将被去除。另外，使用单引号“''”包裹的字符串将被完整保留
	 * 
	 * @param dsl
	 *            源DSL脚本
	 * @param params
	 *            查询参数列表
	 * @return 返回NamedScript对象
	 */
	@SuppressWarnings("unchecked")
	public static NamedScript parse(String dsl, Object... params) {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		if (params != null) {
			if (params.length == 1 && params[0] instanceof Map) {
				paramsMap = (Map<String, Object>) params[0];
			} else {
				for (int i = 0; i < params.length; i++) {
					paramsMap.put((String) params[i], params[++i]);
				}
			}
		}
		return parse(dsl, paramsMap);
	}

	/**
	 * 将指定的含有命名参数的脚本及其参数对照表转换为可执行的脚本对象（含可执行脚本及对应的参数列表）
	 * 
	 * @param namedscript
	 *            含有命名参数的脚本
	 * @param params
	 *            查询对照表
	 * @param parser
	 *            参数解析器
	 * @return 返回可执行的脚本对象
	 */
	public static <T> Script<T> toScript(String namedscript, Map<String, ?> params, ParamsParser<T> parser) {
		if (params == null) {
			params = new HashMap<String, Object>();
		}
		T targetParams = parser.newParams();
		if (StringUtils.isBlank(namedscript)) {
			return new Script<T>(namedscript, targetParams);
		}
		int len = namedscript.length(), i = 0, backslashes = 0;
		char a = BLANK_SPACE, b = BLANK_SPACE;
		boolean isString = false, // 是否在字符串区域
				isSinglelineComment = false, // 是否在单行注释区域
				isMiltilineComment = false, // 是否在多行注释区域
				isParam = false;// 是否在参数区域

		StringBuilder scriptBuilder = new StringBuilder(), commentBuilder = new StringBuilder(),
				paramName = new StringBuilder();
		while (i < len) {
			char c = namedscript.charAt(i);
			if (isString) {
				if (c == BACKSLASH) {
					backslashes++;
				} else {
					if (isStringEnd(a, b, c, backslashes)) {// 字符串区域结束
						isString = false;
					}
					backslashes = 0;
				}
				scriptBuilder.append(c);
			} else if (c == SINGLE_QUOTATION_MARK) {// 字符串区域开始
				isString = true;
				scriptBuilder.append(c);
			} else if (isSinglelineComment) {
				commentBuilder.append(c);
				if (c == LINE_BREAK) {
					isSinglelineComment = false;
					scriptBuilder.append(commentBuilder);
					commentBuilder.setLength(0);
				}
			} else if (isMiltilineComment) {
				commentBuilder.append(c);
				if (isMiltilineCommentEnd(b, c)) {
					isSinglelineComment = false;
					scriptBuilder.append(commentBuilder);
					commentBuilder.setLength(0);
				}
			} else if (isSinglelineCommentBegin(b, c)) {
				isSinglelineComment = true;
				commentBuilder.append(c);
			} else if (isMiltilineCommentBegin(b, c)) {
				isMiltilineComment = true;
				commentBuilder.append(c);
			} else if (isParam) {// 处于参数区域
				if (isParamChar(c)) {
					paramName.append(c);
				} else {
					isParam = false;// 参数区域结束
					parser.parse(scriptBuilder, params, paramName.toString(), targetParams);
					scriptBuilder.append(c);
				}
			} else {
				if (isParamBegin(a, b, c)) {
					isParam = true;// 参数区域开始
					paramName.setLength(0);
					paramName.append(c);
					scriptBuilder.deleteCharAt(scriptBuilder.length() - 1);// 删除表示参数开始的字符
				} else {
					scriptBuilder.append(c);
				}
			}
			a = b;
			b = c;
			i++;
		}
		if (isParam) {
			parser.parse(scriptBuilder, params, paramName.toString(), targetParams);
		}
		if (commentBuilder.length() > 0) {
			scriptBuilder.append(commentBuilder);
		}
		return new Script<T>(scriptBuilder.toString(), targetParams);
	}

	/**
	 * 根据指定的三个前后相邻字符b和c，判断其是否为命名参数脚本参数的开始位置
	 * 
	 * @param a
	 *            前第二个字符
	 * @param b
	 *            前第一个字符
	 * @param c
	 *            当前字符
	 * @return 如果字符a不为“:”、字符b为“:”且字符c为26个英文字母（大小写均可）则返回true，否则返回false
	 */
	public static boolean isParamBegin(char a, char b, char c) {
		return b == PARAM_BEGIN && a != PARAM_BEGIN && is26LettersIgnoreCase(c);
	}

	/**
	 * 根据指定的字符c，判断是否是参数字符（即大小写字母、数字、下划线、短横线）
	 * 
	 * @param c
	 *            指定字符
	 * @return 如果字符c为26个字母（大小写均可）、“0-9”或者“_”，返回true，否则返回false
	 */
	public static boolean isParamChar(char c) {
		return is26LettersIgnoreCase(c) || (c >= '0' && c <= '9') || c == '_';
	}

	/**
	 * 
	 * 根据指定的三个前后相邻字符a、b和c及当前字符c之前的连续反斜杠数量，判断其是否为命名参数脚本字符串区的结束位置
	 * 
	 * @param a
	 *            前第二个字符a
	 * @param b
	 *            前一个字符b
	 * @param c
	 *            当前字符c
	 * @param backslashes
	 *            当前字符c之前的连续反斜杠数量
	 * @return 是动态脚本字符串区域结束位置返回true，否则返回false
	 */
	public static boolean isStringEnd(char a, char b, char c, int backslashes) {
		if (c == SINGLE_QUOTATION_MARK) {
			if (b == BACKSLASH) {
				return backslashes % 2 == 0;
			} else {
				return true;
			}
		} else {
			return false;
		}
	}

	/**
	 * 处理DSL片段
	 * 
	 * @param params
	 *            查询参数
	 * @param script
	 *            目标脚本字符串构建器
	 * @param dslMap
	 *            DSL片段（带深度）缓存表
	 * @param usedParams
	 *            使用到的参数
	 * @param inValidMap
	 *            含有无效参数标记
	 * @param validMap
	 *            有效参数表
	 * @param contexts
	 *            宏运行上下文
	 * @param deep
	 *            当前动态脚本深度
	 * @param emptyWhenNoMacro
	 *            DSL片段没有宏时目标脚本是否为空白字符串
	 */
	private static final void processDSL(Map<String, Object> params, StringBuilder script,
			HashMap<Integer, StringBuilder> dslMap, Map<String, Object> usedParams,
			HashMap<Integer, Boolean> inValidMap, HashMap<Integer, Set<String>> validMap,
			HashMap<Integer, Map<String, Object>> contexts, int deep, boolean emptyWhenNoMacro) {
		Map<String, Object> context = contexts.get(deep);
		if (context == null) {
			context = new HashMap<String, Object>();
			contexts.put(deep, context);
		}
		StringBuilder dslBuilder = MacroUtils.execute(dslMap.get(deep), context, params, emptyWhenNoMacro);
		if (deep == 1) {
			script.append(dslBuilder);
			for (String name : validMap.get(deep)) {
				if (!usedParams.containsKey(name) && dslBuilder.indexOf(PARAM_BEGIN + name) >= 0) {
					usedParams.put(name, params.get(name));
				}
			}
		} else {
			dslMap.get(deep - 1).append(dslBuilder);
		}
		dslMap.remove(deep);
		validMap.remove(deep);
		inValidMap.remove(deep);
	}

	private static boolean isSinglelineCommentBegin(char b, char c) {
		for (int i = 0; i < SINGLELINE_COMMENT_PREFIXES.length; i++) {
			char[] singlelineCommentPrefix = SINGLELINE_COMMENT_PREFIXES[i];
			if (singlelineCommentPrefix.length > 1) {
				if (b == singlelineCommentPrefix[0] && c == singlelineCommentPrefix[1]) {
					return true;
				}
			} else {
				if (c == singlelineCommentPrefix[0]) {
					return true;
				}
			}
		}
		return false;
	}

	private static boolean isMiltilineCommentBegin(char b, char c) {
		for (int i = 0; i < MILTILINE_COMMENT_PREFIXES.length; i++) {
			char[] multilineCommentPrefix = MILTILINE_COMMENT_PREFIXES[i];
			if (multilineCommentPrefix.length > 1) {
				if (b == multilineCommentPrefix[0] && c == multilineCommentPrefix[1]) {
					return true;
				}
			} else {
				if (c == multilineCommentPrefix[0]) {
					return true;
				}
			}
		}
		return false;
	}

	private static boolean isMiltilineCommentEnd(char b, char c) {
		for (int i = 0; i < MILTILINE_COMMENT_SUFFIXES.length; i++) {
			char[] multilineCommentSuffix = MILTILINE_COMMENT_SUFFIXES[i];
			if (multilineCommentSuffix.length > 1) {
				if (b == multilineCommentSuffix[0] && c == multilineCommentSuffix[1]) {
					return true;
				}
			} else {
				if (c == multilineCommentSuffix[0]) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 根据指定的两个前后相邻字符b和c，判断其是否为动态脚本区的开始位置
	 * 
	 * @param b
	 *            前一个字符b
	 * @param c
	 *            当前字符c
	 * @return
	 */
	private static boolean isDynamicBegin(char b, char c) {
		return b == DYNAMIC_BEGIN[0] && c == DYNAMIC_BEGIN[1];
	}

	/**
	 * 根据指定字符c，判断其是否为动态脚本区的结束位置
	 * 
	 * @param c
	 *            指定字符
	 * @return
	 */
	private static boolean isDynamicEnd(char c) {
		return c == DYNAMIC_END;
	}

	/**
	 * 删除将指定字符串缓冲区末尾多余的空白字符（包括回车符、换行符、制表符、空格）
	 * 
	 * @param target
	 *            指定字符串缓冲区
	 */
	private static void deleteRedundantBlank(StringBuilder target) {
		int length = target.length(), i = length;
		while (i > 0) {
			char ch = target.charAt(--i);
			if (ch > BLANK_SPACE) {
				target.delete(i + 1, length);
				break;
			}
		}
	}

	/**
	 * 根据指定的字符c，判断是否是26个字母（大小写均可）
	 * 
	 * @param c
	 *            指定字符
	 * @return 是26个字母返回true，否则返回false
	 */
	private static boolean is26LettersIgnoreCase(char c) {
		return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
	}

}
