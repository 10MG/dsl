package cn.tenmg.dsl.utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import cn.tenmg.dsl.DSLContext;
import cn.tenmg.dsl.NamedScript;
import cn.tenmg.dsl.ParamsConverter;
import cn.tenmg.dsl.ParamsFilter;
import cn.tenmg.dsl.ParamsParser;
import cn.tenmg.dsl.Script;
import cn.tenmg.dsl.context.DefaultDSLContext;
import cn.tenmg.dsl.utils.MacroUtils.Dslf;

/**
 * 动态脚本语言(DSL)工具类
 * 
 * @author June wjzhao@aliyun.com
 * 
 * @since 1.0.0
 */
public abstract class DSLUtils {

	public static final char SINGLE_QUOTATION_MARK = '\'', BACKSLASH = '\\', BLANK_SPACE = '\u0020', COMMA = ',',
			PARAM_MARK = '?', DYNAMIC_PREFIX[], DYNAMIC_SUFFIX, PARAM_PREFIX, EMBED_PREFIX, LINE_BREAK = '\n',
			SINGLELINE_COMMENT_PREFIXES[][], MILTILINE_COMMENT_PREFIXES[][], MILTILINE_COMMENT_SUFFIXES[][];

	private static final char LEFT_SQUARE_BRACKET = '[', RIGHT_SQUARE_BRACKET = ']';

	private static DefaultDSLContext defaultDSLContext = new DefaultDSLContext();

	private static final Set<Character> LINE_TAIL = SetUtils.newHashSet('\r', LINE_BREAK), PARAM_CHARS = SetUtils
			.newHashSet('.', LEFT_SQUARE_BRACKET, RIGHT_SQUARE_BRACKET, '_', SINGLE_QUOTATION_MARK, '"');

	static {
		DYNAMIC_PREFIX = ConfigUtils.getProperty("dynamic.prefix", "#[").toCharArray();
		DYNAMIC_SUFFIX = ConfigUtils.getProperty("dynamic.suffix", "]").charAt(0);
		PARAM_PREFIX = ConfigUtils.getProperty("param.prefix", ":").charAt(0);
		EMBED_PREFIX = ConfigUtils.getProperty("embed.prefix", "#").charAt(0);
		String[] singlelineCommentPrefixes = ConfigUtils.getProperty("comment.singleline", "--").split(","),
				miltilineComments = ConfigUtils.getProperty("comment.multiline", "/*,*/").split(";");
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
	}

	private DSLUtils() {
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
	public static NamedScript parse(String dsl, Object... params) {
		return parse(defaultDSLContext, dsl, params);
	}

	/**
	 * 将指定的源动态脚本语言（DSL）及参数转换为NamedScript对象。NamedScript对象含有带命名参数的脚本（script），及实际使用的参数查找表（params）。转换的过程中含有有效参数（参数值非null）的动态片段将被保留并去除前缀和后缀（默认以“#[”作为前缀，以“]”作为后缀。），否则动态片段将被去除。另外，使用单引号“''”包裹的字符串将被完整保留
	 * 
	 * @param dsl
	 *            源DSL脚本
	 * @param params
	 *            参数查找表
	 * @return 返回NamedScript对象
	 */
	public static NamedScript parse(String dsl, Object params) {
		return parse(defaultDSLContext, dsl, params);
	}

	/**
	 * 将指定的源动态脚本语言（DSL）及参数转换为NamedScript对象。NamedScript对象含有带命名参数的脚本（script），及实际使用的参数查找表（params）。动态脚本的动态片段以“#[”作为前缀，以“]”作为后缀。转换的过程中含有有效参数（参数值非null）的动态片段将被保留并去除“#[”前缀和后缀“]”，否则动态片段将被去除。另外，使用单引号“''”包裹的字符串将被完整保留
	 * 
	 * @param context
	 *            上下文
	 * @param dsl
	 *            源DSL脚本
	 * @param params
	 *            查询参数列表
	 * @return 返回NamedScript对象
	 */
	public static NamedScript parse(DSLContext context, String dsl, Object... params) {
		Map<String, Object> paramsMap;
		if (params != null) {
			if (params.length % 2 == 0) {
				paramsMap = new HashMap<String, Object>(params.length / 2);
				for (int i = 0; i < params.length; i++) {
					paramsMap.put((String) params[i], params[++i]);
				}
			} else {
				throw new IllegalArgumentException("The number of parameters must be a multiple of 2");
			}
		} else {
			paramsMap = new HashMap<String, Object>();
		}
		return parse(context, dsl, paramsMap);
	}

	/**
	 * 将指定的源动态脚本语言（DSL）及参数转换为NamedScript对象。NamedScript对象含有带命名参数的脚本（script），及实际使用的参数查找表（params）。转换的过程中含有有效参数（参数值非null）的动态片段将被保留并去除前缀和后缀（默认以“#[”作为前缀，以“]”作为后缀。），否则动态片段将被去除。另外，使用单引号“''”包裹的字符串将被完整保留
	 * 
	 * @param context
	 *            上下文
	 * @param dsl
	 *            源DSL脚本
	 * @param params
	 *            参数查找表
	 * @return 返回NamedScript对象
	 */
	public static NamedScript parse(DSLContext context, String dsl, Object params) {
		Map<String, Object> usedParams = new HashMap<String, Object>();
		if (StringUtils.isBlank(dsl)) {
			return new NamedScript(dsl, usedParams);
		}
		dsl = deleteStartBlankLines(StringUtils.stripEnd(dsl, null));// 删除前面空白行和末尾空白字符
		int len = dsl.length();
		if (len < 3) {// 长度太小（小于字符串“#[]”的长度）无法构成动态，直接返回
			return new NamedScript(dsl, usedParams);
		}
		if (params == null) {
			params = new HashMap<String, Object>();
		}
		ParamGetter paramGetter = getParamGetter(context);
		String paramName;
		Object value;
		int i = 0, deep = 0, backslashes = 0;// 连续反斜杠数
		char a = BLANK_SPACE, b = BLANK_SPACE, c;
		boolean isString = false, // 是否在字符串区域
				isSinglelineComment = false, // 是否在单行注释区域
				isMiltilineComment = false, // 是否在多行注释区域
				isDynamic = false, // 是否在动态脚本区域
				isParam = false, // 是否在参数区域
				isEmbed = false, // 是否在嵌入参数区域
				notParamAccessor = true; // 不在参数访问符“[]”内
		StringBuilder scriptBuilder = new StringBuilder(), paramNameBuilder = new StringBuilder(), dslfBuilder;
		HashMap<Integer, Boolean> inValidParams = new HashMap<Integer, Boolean>();
		HashMap<Integer, Map<String, Object>> validParams = new HashMap<Integer, Map<String, Object>>(),
				embedParams = new HashMap<Integer, Map<String, Object>>();
		HashMap<Integer, StringBuilder> dslfBuilders = new HashMap<Integer, StringBuilder>();
		HashMap<Integer, Map<String, Object>> contexts = new HashMap<Integer, Map<String, Object>>();
		while (i < len) {
			c = dsl.charAt(i);
			if (isString) {// 字符串内
				if (c == BACKSLASH) {
					backslashes++;
				} else {
					if (isStringEnd(a, b, c, backslashes)) {// 字符串区域结束
						isString = false;
					}
					backslashes = 0;
				}
				if (deep > 0) {
					dslfBuilders.get(deep).append(c);
				} else {
					scriptBuilder.append(c);
				}
			} else if (isSinglelineComment) {// 单行注释内
				if (isDynamic && isDynamicEnd(c) && notParamAccessor) {// 当前字符为动态脚本结束字符
					isSinglelineComment = false;
					if (inValidParams.get(deep) == null) {// 不含无效参数
						if (processDSL(context, scriptBuilder, dslfBuilders, params, paramGetter, usedParams,
								inValidParams, validParams, embedParams, contexts, deep, false)) {// DSL片段解析为主脚本
							deleteRedundantBlank(scriptBuilder);// 删除多余空白字符
							break;
						}
						deep--;
					} else if (deep > 0) {
						if (processDSL(context, scriptBuilder, dslfBuilders, params, paramGetter, usedParams,
								inValidParams, validParams, embedParams, contexts, deep, true)) {// DSL片段解析为主脚本
							deleteRedundantBlank(scriptBuilder);// 删除多余空白字符
							break;
						}
						deep--;
					}
					if (deep < 1) {// 已离开动态脚本区域
						isDynamic = false;
						deleteRedundantBlank(scriptBuilder);// 删除多余空白字符
					} else {
						deleteRedundantBlank(dslfBuilders.get(deep));// 删除多余空白字符
					}
				} else {
					if (c == LINE_BREAK) {
						isSinglelineComment = false;
					}
					if (deep > 0) {
						dslfBuilders.get(deep).append(c);
					} else {
						scriptBuilder.append(c);
					}
				}
			} else if (isMiltilineComment) {// 多行注释内
				if (isMiltilineCommentEnd(b, c)) {
					isMiltilineComment = false;
				}
				if (deep > 0) {
					dslfBuilders.get(deep).append(c);
				} else {
					scriptBuilder.append(c);
				}
			} else if (c == SINGLE_QUOTATION_MARK && notParamAccessor) {// 字符串区域开始
				if (deep > 0) {
					dslfBuilders.get(deep).append(c);
				} else {
					scriptBuilder.append(c);
				}
			} else if (isSinglelineCommentBegin(b, c)) {// 单行注释开始
				isSinglelineComment = true;
				if (deep > 0) {
					dslfBuilders.get(deep).append(c);
				} else {
					scriptBuilder.append(c);
				}
			} else if (isMiltilineCommentBegin(b, c)) {// 多行注释开始
				isMiltilineComment = true;
				if (deep > 0) {
					dslfBuilders.get(deep).append(c);
				} else {
					scriptBuilder.append(c);
				}
			} else if (isDynamic) {// 当前字符处于动态脚本区域
				if (isDynamicEnd(c) && notParamAccessor) {// 结束当前动态脚本区域
					if (isParam) {// 处于动态参数区域
						isParam = false;// 结束动态参数区域
						paramName = paramNameBuilder.toString();
						value = paramGetter.getValue(params, paramName);
						if (value != null) {
							validParams.get(deep).put(paramName, value);
							paramNameBuilder.setLength(0);
						} else if (deep > 0) {
							inValidParams.put(deep, Boolean.TRUE);// 含有无效参数标记
						}
					} else if (isEmbed) {
						isEmbed = false;// 结束动态嵌入参数区域
						paramName = paramNameBuilder.toString();
						value = paramGetter.getValue(params, paramName);
						if (value != null) {
							embedParams.get(deep).put(paramName, value);
							paramNameBuilder.setLength(0);
						} else if (deep > 0) {
							inValidParams.put(deep, Boolean.TRUE);// 含有无效参数标记
						}
					}
					if (inValidParams.get(deep) == null) {// 不含无效参数
						if (processDSL(context, scriptBuilder, dslfBuilders, params, paramGetter, usedParams,
								inValidParams, validParams, embedParams, contexts, deep, false)) {// DSL片段解析为主脚本
							deleteRedundantBlank(scriptBuilder);// 删除多余空白字符
							break;
						}
						deep--;
					} else if (deep > 0) {
						if (processDSL(context, scriptBuilder, dslfBuilders, params, paramGetter, usedParams,
								inValidParams, validParams, embedParams, contexts, deep, true)) {// DSL片段解析为主脚本
							deleteRedundantBlank(scriptBuilder);// 删除多余空白字符
							break;
						}
						deep--;
					}
					if (deep < 1) {// 已离开动态脚本区域
						isDynamic = false;
						deleteRedundantBlank(scriptBuilder);// 删除多余空白字符
					} else {
						deleteRedundantBlank(dslfBuilders.get(deep));// 删除多余空白字符
					}
				} else {
					if (isParam) {// 处于动态参数区域
						if (c == LEFT_SQUARE_BRACKET) {
							notParamAccessor = false;
						} else if (c == RIGHT_SQUARE_BRACKET) {
							notParamAccessor = true;
						}
						if (isParamChar(c)) {
							paramNameBuilder.append(c);
							dslfBuilder = dslfBuilders.get(deep);
							if (dslfBuilder == null) {
								dslfBuilder = new StringBuilder();
								dslfBuilders.put(deep, dslfBuilder);
							}
							dslfBuilder.append(c);
						} else {// 离开动态参数区域
							isParam = false;
							paramName = paramNameBuilder.toString();
							value = paramGetter.getValue(params, paramName);
							if (value != null) {
								validParams.get(deep).put(paramName, value);
							} else if (deep >= 0) {
								inValidParams.put(deep, Boolean.TRUE);// 含有无效参数标记
							}
							paramNameBuilder.setLength(0);

							if (isDynamicBegin(b, c)) {// 嵌套的新的动态脚本区域
								dslfBuilder = dslfBuilders.get(deep);
								dslfBuilder.deleteCharAt(dslfBuilder.length() - 1);// 删除#号
								deep++;
								dslfBuilders.put(deep, new StringBuilder());
								validParams.put(deep, new HashMap<String, Object>());
								embedParams.put(deep, new HashMap<String, Object>());
							} else {
								dslfBuilder = dslfBuilders.get(deep);
								if (dslfBuilder == null) {
									dslfBuilder = new StringBuilder();
									dslfBuilders.put(deep, dslfBuilder);
								}
								dslfBuilder.append(c);
							}
						}
					} else if (isEmbed) {// 处于动态嵌入参数区域
						if (c == LEFT_SQUARE_BRACKET) {
							notParamAccessor = false;
						} else if (c == RIGHT_SQUARE_BRACKET) {
							notParamAccessor = true;
						}
						if (isParamChar(c)) {
							paramNameBuilder.append(c);
							dslfBuilder = dslfBuilders.get(deep);
							if (dslfBuilder == null) {
								dslfBuilder = new StringBuilder();
								dslfBuilders.put(deep, dslfBuilder);
							}
							dslfBuilder.append(c);
						} else {// 离开动态嵌入参数区域
							isEmbed = false;
							paramName = paramNameBuilder.toString();
							value = paramGetter.getValue(params, paramName);
							if (value != null) {
								embedParams.get(deep).put(paramName, value);
							} else if (deep >= 0) {
								inValidParams.put(deep, Boolean.TRUE);// 含有无效参数标记
							}
							paramNameBuilder.setLength(0);

							if (isDynamicBegin(b, c)) {// 嵌套的新的动态脚本区域
								dslfBuilder = dslfBuilders.get(deep);
								dslfBuilder.deleteCharAt(dslfBuilder.length() - 1);// 删除#号
								deep++;
								dslfBuilders.put(deep, new StringBuilder());
								validParams.put(deep, new HashMap<String, Object>());
								embedParams.put(deep, new HashMap<String, Object>());
							} else {
								dslfBuilder = dslfBuilders.get(deep);
								if (dslfBuilder == null) {
									dslfBuilder = new StringBuilder();
									dslfBuilders.put(deep, dslfBuilder);
								}
								dslfBuilder.append(c);
							}
						}
					} else {// 未处于动态参数区域
						if (isDynamicBegin(b, c)) {// 嵌套的新的动态脚本区域
							dslfBuilder = dslfBuilders.get(deep);
							dslfBuilder.deleteCharAt(dslfBuilder.length() - 1);// 删除#号
							deep++;
							dslfBuilders.put(deep, new StringBuilder());
							validParams.put(deep, new HashMap<String, Object>());
							embedParams.put(deep, new HashMap<String, Object>());
						} else {
							if (isParamBegin(a, b, c)) {
								isParam = true;
								paramNameBuilder.setLength(0);
								paramNameBuilder.append(c);
							} else if (isEmbedBegin(a, b, c)) {
								isEmbed = true;
								paramNameBuilder.setLength(0);
								paramNameBuilder.append(c);
							}

							dslfBuilder = dslfBuilders.get(deep);
							if (dslfBuilder == null) {
								dslfBuilder = new StringBuilder();
								dslfBuilders.put(deep, dslfBuilder);
							}
							dslfBuilder.append(c);
						}
					}
				}
			} else {// 当前字符未处于动态脚本区域
				if (isDynamicBegin(b, c)) {
					isDynamic = true;
					scriptBuilder.deleteCharAt(scriptBuilder.length() - 1);
					deep++;
					dslfBuilders.put(deep, new StringBuilder());
					validParams.put(deep, new HashMap<String, Object>());
					embedParams.put(deep, new HashMap<String, Object>());
				} else {
					if (isParam) {// 处于参数区域
						if (isParamChar(c)) {
							paramNameBuilder.append(c);
							if (i == len - 1) {
								paramName = paramNameBuilder.toString();
								usedParams.put(paramName, paramGetter.getValue(params, paramName));
							}
						} else {// 离开参数区域
							isParam = false;
							paramName = paramNameBuilder.toString();
							usedParams.put(paramName, paramGetter.getValue(params, paramName));
							if (i < len - 1) {
								paramNameBuilder.setLength(0);
							}
						}
					} else if (isEmbed) {// 处于嵌入参数区域
						if (isParamChar(c)) {
							paramNameBuilder.append(c);
							if (i == len - 1) {// 最后一个参数字符
								scriptBuilder.setLength(scriptBuilder.length() - paramNameBuilder.length());
								value = paramGetter.getValue(params, paramNameBuilder.toString());
								scriptBuilder.append(value == null ? "null" : value.toString());
								break;
							}
						} else {// 离开嵌入参数区域
							isEmbed = false;
							scriptBuilder.setLength(scriptBuilder.length() - paramNameBuilder.length() - 1);// 需要将#号也删除
							value = paramGetter.getValue(params, paramNameBuilder.toString());
							scriptBuilder.append(value == null ? "null" : value.toString());
							if (i < len - 1) {
								paramNameBuilder.setLength(0);
							}
						}
					} else {// 未处于参数区域
						if (isParamBegin(a, b, c)) {
							isParam = true;
							paramNameBuilder.setLength(0);
							paramNameBuilder.append(c);
						} else if (isEmbedBegin(a, b, c)) {
							isEmbed = true;
							paramNameBuilder.setLength(0);
							paramNameBuilder.append(c);
						}
					}
					scriptBuilder.append(c);
				}
			}
			a = b;
			b = c;
			i++;
		}
		return new NamedScript(scriptBuilder.toString(), usedParams);
	}

	/**
	 * 将指定的含有命名参数的脚本及其参数对照表转换为可执行的脚本对象（含可执行脚本及对应的参数列表）
	 * 
	 * @param namedScript
	 *            使用命名参数的脚本对象模型
	 * @param parser
	 *            参数解析器
	 * @return 返回可执行的脚本对象
	 */
	public static <T> Script<T> toScript(NamedScript namedScript, ParamsParser<T> parser) {
		return toScript(namedScript.getScript(), namedScript.getParams(), parser);
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
				isParam = false, // 是否在参数区域
				notParamAccessor = true; // 不在参数访问符“[]”内

		StringBuilder scriptBuilder = new StringBuilder(), commentBuilder = new StringBuilder(),
				paramName = new StringBuilder();
		while (i < len) {
			char c = namedscript.charAt(i);
			if (isString) {// 字符串内
				if (c == BACKSLASH) {
					backslashes++;
				} else {
					if (isStringEnd(a, b, c, backslashes)) {// 字符串区域结束
						isString = false;
					}
					backslashes = 0;
				}
				scriptBuilder.append(c);
			} else if (isSinglelineComment) {// 单行注释内
				commentBuilder.append(c);
				if (c == LINE_BREAK) {
					isSinglelineComment = false;
					scriptBuilder.append(commentBuilder);
					commentBuilder.setLength(0);
				}
			} else if (isMiltilineComment) {// 多行注释内
				commentBuilder.append(c);
				if (isMiltilineCommentEnd(b, c)) {
					isMiltilineComment = false;
					scriptBuilder.append(commentBuilder);
					commentBuilder.setLength(0);
				}
			} else if (c == SINGLE_QUOTATION_MARK && notParamAccessor) {// 字符串区域开始
				isString = true;
				scriptBuilder.append(c);
			} else if (isSinglelineCommentBegin(b, c)) {// 单行注释开始
				isSinglelineComment = true;
				commentBuilder.append(c);
			} else if (isMiltilineCommentBegin(b, c)) {// 多行注释开始
				isMiltilineComment = true;
				commentBuilder.append(c);
			} else if (isParam) {// 处于参数区域
				if (c == LEFT_SQUARE_BRACKET) {
					notParamAccessor = false;
				} else if (c == RIGHT_SQUARE_BRACKET) {
					notParamAccessor = true;
				}
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
	 * 获取含命名参数的脚本中使用的参数集
	 * 
	 * @param paramGetter
	 *            参数获取器
	 * @param namedscript
	 *            含命名参数的脚本
	 * @param params
	 *            参数集
	 * @return 含命名参数的脚本中使用的参数集
	 */
	public static Map<String, Object> getUsedParams(ParamGetter paramGetter, CharSequence namedscript, Object params) {
		Map<String, Object> usedParams = new HashMap<String, Object>();
		String paramName;
		int i = 0, len = namedscript.length(), last = len - 1, backslashes = 0;// 连续反斜杠数
		char a = BLANK_SPACE, b = a, c;
		boolean isString = false, // 是否在字符串区域
				isSinglelineComment = false, // 是否在单行注释区域
				isMiltilineComment = false, // 是否在多行注释区域
				isParam = false, // 是否在参数区域
				isEmbed = false, // 是否在嵌入参数区域
				notParamAccessor = true; // 不在参数访问符“[]”内
		StringBuilder paramNameBuilder = new StringBuilder();
		while (i < len) {
			c = namedscript.charAt(i);
			if (isString) {// 字符串内
				if (c == BACKSLASH) {
					backslashes++;
				} else {
					if (isStringEnd(a, b, c, backslashes)) {// 字符串区域结束
						isString = false;
					}
					backslashes = 0;
				}
			} else if (isSinglelineComment) {// 单行注释内
				if (c == LINE_BREAK) {
					isSinglelineComment = false;
				}
			} else if (isMiltilineComment) {// 多行注释内
				if (isMiltilineCommentEnd(b, c)) {
					isMiltilineComment = false;
				}
			} else if (c == SINGLE_QUOTATION_MARK && notParamAccessor) {// 字符串区域开始
				isString = true;
			} else if (isSinglelineCommentBegin(b, c)) {// 单行注释开始
				isSinglelineComment = true;
			} else if (isMiltilineCommentBegin(b, c)) {// 多行注释开始
				isMiltilineComment = true;
			} else {
				if (isParam) {// 处于参数区域
					if (c == LEFT_SQUARE_BRACKET) {
						notParamAccessor = false;
					} else if (c == RIGHT_SQUARE_BRACKET) {
						notParamAccessor = true;
					}
					if (isParamChar(c)) {
						paramNameBuilder.append(c);
						if (i == last) {
							paramName = paramNameBuilder.toString();
							usedParams.put(paramName, paramGetter.getValue(params, paramName));
							break;
						}
					} else {// 离开参数区域
						isParam = false;
						paramName = paramNameBuilder.toString();
						usedParams.put(paramName, paramGetter.getValue(params, paramName));
						if (i < last) {
							paramNameBuilder.setLength(0);
						}
					}
				} else if (isEmbed) {// 处于嵌入参数区域
					if (c == LEFT_SQUARE_BRACKET) {
						notParamAccessor = false;
					} else if (c == RIGHT_SQUARE_BRACKET) {
						notParamAccessor = true;
					}
					if (isParamChar(c)) {
						paramNameBuilder.append(c);
						if (i == last) {// 最后一个参数字符
							paramName = paramNameBuilder.toString();
							usedParams.put(paramName, paramGetter.getValue(params, paramName));
							break;
						}
					} else {// 离开嵌入参数区域
						isEmbed = false;
						paramName = paramNameBuilder.toString();
						usedParams.put(paramName, paramGetter.getValue(params, paramName));
						if (i < last) {
							paramNameBuilder.setLength(0);
						}
					}
				} else {// 未处于参数区域
					if (isParamBegin(a, b, c)) {
						isParam = true;
						paramNameBuilder.setLength(0);
						paramNameBuilder.append(c);
					} else if (isEmbedBegin(a, b, c)) {
						isEmbed = true;
						paramNameBuilder.setLength(0);
						paramNameBuilder.append(c);
					}
				}
			}
			a = b;
			b = c;
			i++;
		}
		return usedParams;
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
		return b == PARAM_PREFIX && a != PARAM_PREFIX && is26LettersIgnoreCase(c);
	}

	/**
	 * 根据指定的三个前后相邻字符b和c，判断其是否为嵌入参数脚本参数的开始位置
	 * 
	 * @param a
	 *            前第二个字符
	 * @param b
	 *            前第一个字符
	 * @param c
	 *            当前字符
	 * @return 如果字符a不为“:”、字符b为“:”且字符c为26个英文字母（大小写均可）则返回true，否则返回false
	 */
	public static boolean isEmbedBegin(char a, char b, char c) {
		return b == EMBED_PREFIX && a != EMBED_PREFIX && is26LettersIgnoreCase(c);
	}

	/**
	 * 根据指定的字符c，判断是否是参数字符（即大小写字母、数字、英文句号、方括号、下划线）
	 * 
	 * @param c
	 *            指定字符
	 * @return 如果字符c为26个字母（大小写均可）、“0-9”、“.”、“[”、“]”或者“_”，返回 {@code true}，否则返回
	 *         {@code false}
	 */
	public static boolean isParamChar(char c) {
		return is26LettersIgnoreCase(c) || (c >= '0' && c <= '9') || PARAM_CHARS.contains(c);
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
	 * 根据当前字符 {@code c} 和前一字符 {@code b} 判断当前是否处于脚本的单行注释的开始位置
	 * 
	 * @param b
	 *            前一字符
	 * @param c
	 *            当前字符
	 * @return 如果当前为脚本的单行注释的开始位置，则返回 {@code true}，否则返回 {@code false}
	 */
	public static boolean isSinglelineCommentBegin(char b, char c) {
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

	/**
	 * 根据当前字符 {@code c} 和前一字符 {@code b} 判断当前是否处于脚本的多行注释的开始位置
	 * 
	 * @param b
	 *            前一字符
	 * @param c
	 *            当前字符
	 * @return 如果当前为脚本的多行注释的开始位置，则返回 {@code true}，否则返回 {@code false}
	 */
	public static boolean isMiltilineCommentBegin(char b, char c) {
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

	/**
	 * 根据当前字符 {@code c} 和前一字符 {@code b} 判断当前是否处于脚本的多行注释的结束位置
	 * 
	 * @param b
	 *            前一字符
	 * @param c
	 *            当前字符
	 * @return 如果当前为脚本的多行注释的结束位置，则返回 {@code true}，否则返回 {@code false}
	 */
	public static boolean isMiltilineCommentEnd(char b, char c) {
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
	 * 使用参数过转换器对参数表进行浅层转换。浅层转换是指，仅转换表层参数，但不会转换嵌在表层参数内部的属性。
	 * 
	 * @param paramsConverters
	 *            参数转换器
	 * @param params
	 *            参数集
	 */
	@SuppressWarnings("unchecked")
	public static void convert(List<ParamsConverter<?>> paramsConverters, Map<String, Object> params) {
		Object value;
		String paramName;
		Entry<String, ?> entry;
		for (Iterator<?> it = params.entrySet().iterator(); it.hasNext();) {
			entry = (Entry<String, ?>) it.next();
			paramName = entry.getKey();
			value = convert(paramsConverters, paramName, entry.getValue());
			params.put(paramName, convert(paramsConverters, paramName, value));
		}
	}

	/**
	 * 使用参数过滤器对参数表进行浅层过滤。浅层过滤是指，仅过滤表层参数，但不会过滤嵌在表层参数内部的属性。
	 * 
	 * @param paramsFilters
	 *            参数过滤器
	 * @param params
	 *            参数表
	 */
	@SuppressWarnings("unchecked")
	public static void filter(List<ParamsFilter> paramsFilters, Map<String, Object> params) {
		Object value;
		String paramName;
		Entry<String, ?> entry;
		Set<String> filteredParams = new HashSet<String>();
		for (Iterator<?> it = params.entrySet().iterator(); it.hasNext();) {
			entry = (Entry<String, ?>) it.next();
			paramName = entry.getKey();
			value = entry.getValue();
			if (filtered(paramsFilters, filteredParams, paramName, value)) {
				it.remove();
				filteredParams.add(paramName);
				break;
			}
		}
	}

	/**
	 * 删除前面的空白行
	 * 
	 * @param dsl
	 *            动态脚本语言
	 */
	private static String deleteStartBlankLines(String dsl) {
		int lastLineTailIndex = -1;
		for (int len = dsl.length(), i = 0; i < len; i++) {
			char c = dsl.charAt(i);
			if (Character.isWhitespace(c)) {
				if (LINE_TAIL.contains(c)) {
					lastLineTailIndex = i;
				}
			} else {
				break;
			}
		}
		if (lastLineTailIndex >= 0) {
			return dsl.substring(lastLineTailIndex + 1);
		}
		return dsl;
	}

	/**
	 * 处理DSL片段
	 * 
	 * @param params
	 *            查询参数
	 * @param paramGetter
	 *            参数获取器
	 * @param scriptBuilder
	 *            目标脚本字符串构建器
	 * @param dslfBuilders
	 *            DSL片段（带深度）缓存表
	 * @param usedParams
	 *            使用到的参数
	 * @param inValidParams
	 *            含有无效参数标记
	 * @param validParams
	 *            有效参数表
	 * @param embedParams
	 *            嵌入参数表
	 * @param globalContext
	 *            全局上下文
	 * @param attributesMap
	 *            各层级属性表。各层属性表由当本层已运行的宏所存储，供本层后续执行的宏使用
	 * @param deep
	 *            当前动态脚本深度
	 * @param emptyWhenNoMacro
	 *            DSL片段没有宏时目标脚本是否为空白字符串
	 */
	private static final boolean processDSL(DSLContext context, StringBuilder scriptBuilder,
			HashMap<Integer, StringBuilder> dslfBuilders, Object params, ParamGetter paramGetter,
			Map<String, Object> usedParams, HashMap<Integer, Boolean> inValidParams,
			HashMap<Integer, Map<String, Object>> validParamses, HashMap<Integer, Map<String, Object>> embedParams,
			HashMap<Integer, Map<String, Object>> attributesMap, int deep, boolean emptyWhenNoMacro) {
		Map<String, Object> validParams = validParamses.get(deep);
		usedParams.putAll(validParams);
		Map<String, Object> attributes = attributesMap.get(deep);
		if (attributes == null) {
			attributes = new HashMap<String, Object>();
			attributesMap.put(deep, attributes);
		}
		Dslf dslf = MacroUtils.execute(context, attributes, dslfBuilders.get(deep), validParams, emptyWhenNoMacro);
		boolean dslfAsScript = dslf.isDslfAsScript();
		if (dslfAsScript || deep == 1) {
			replaceEmbedParams(params, scriptBuilder, dslf, usedParams, embedParams, deep);
		} else {
			dslfBuilders.get(deep - 1).append(dslf.getValue());
		}
		dslfBuilders.remove(deep);
		validParamses.remove(deep);
		inValidParams.remove(deep);
		return dslfAsScript;
	}

	private static void replaceEmbedParams(Object params, StringBuilder scriptBuilder, Dslf dslf,
			Map<String, Object> usedParams, HashMap<Integer, Map<String, Object>> embedParams, int deep) {
		if (dslf.isDslfAsScript()) {
			scriptBuilder.setLength(0);
		}
		if (embedParams.isEmpty()) {
			scriptBuilder.append(dslf.getValue());
		} else {
			String namedScript = dslf.getValue().toString();
			Object value;
			Entry<String, Object> entry;
			for (Iterator<Entry<String, Object>> it = embedParams.get(deep).entrySet().iterator(); it.hasNext();) {
				entry = it.next();
				value = entry.getValue();
				namedScript = namedScript.replaceAll(EMBED_PREFIX + entry.getKey(),
						value == null ? "null" : value.toString());
			}
			scriptBuilder.append(namedScript);
		}
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
		return b == DYNAMIC_PREFIX[0] && c == DYNAMIC_PREFIX[1];
	}

	/**
	 * 根据指定字符c，判断其是否为动态脚本区的结束位置
	 * 
	 * @param c
	 *            指定字符
	 * @return
	 */
	private static boolean isDynamicEnd(char c) {
		return c == DYNAMIC_SUFFIX;
	}

	/**
	 * 删除将指定字符串缓冲区末尾多余的空白字符（包括回车符、换行符、制表符、空格）
	 * 
	 * @param sb
	 *            指定字符串缓冲区
	 */
	private static void deleteRedundantBlank(StringBuilder sb) {
		for (int len = sb.length(), i = len - 1; i >= 0; i--) {
			char c = sb.charAt(i);
			if (!Character.isWhitespace(c)) {
				sb.setLength(i + 1);
				return;
			}
		}
		sb.setLength(0);
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

	/**
	 * 将指定参数进行转换
	 * 
	 * @param paramsConverters
	 *            参数转换器
	 * @param paramName
	 *            参数名
	 * @param value
	 *            参数值
	 * @return 转换后的参数值
	 */
	private static Object convert(List<ParamsConverter<?>> paramsConverters, String paramName, Object value) {
		ParamsConverter<?> paramsConverter;
		for (int i = 0, size = paramsConverters.size(); i < size; i++) {
			paramsConverter = paramsConverters.get(i);
			if (paramsConverter.determine(paramName)) {
				value = paramsConverter.convert(value);
			}
		}
		return value;
	}

	/**
	 * 将指定参数进行转换
	 * 
	 * @param paramsConverters
	 *            参数转换器
	 * @param convertedParams
	 *            已转换参数集
	 * @param paramName
	 *            参数名
	 * @param value
	 *            参数值
	 * @return 转换后的参数值
	 */
	private static Object convert(List<ParamsConverter<?>> paramsConverters, Map<String, Object> convertedParams,
			String paramName, Object value) {
		value = convert(paramsConverters, paramName, value);
		convertedParams.put(paramName, value);
		return value;
	}

	/**
	 * 确定参数是否需要过滤掉
	 * 
	 * @param paramsFilters
	 *            参数过滤器
	 * @param filteredParams
	 *            已过滤参数
	 * @param paramName
	 *            参数名
	 * @param value
	 *            参数值
	 * @return 如果该参数需要被过滤掉则返回{@code true}，否则返回{@code false}
	 */
	private static boolean filtered(List<ParamsFilter> paramsFilters, Set<String> filteredParams, String paramName,
			Object value) {
		ParamsFilter paramsFilter;
		for (int i = 0, size = paramsFilters.size(); i < size; i++) {
			paramsFilter = paramsFilters.get(i);
			if (paramsFilter.determine(paramName, value)) {
				filteredParams.add(paramName);
				return true;
			}
		}
		return false;
	}

	private static ParamGetter getParamGetter(DSLContext context) {
		List<ParamsConverter<?>> paramsConverters = context.getParamsConverters();
		List<ParamsFilter> paramsFilters = context.getParamsFilters();
		if (CollectionUtils.isEmpty(paramsConverters)) {
			if (CollectionUtils.isEmpty(paramsFilters)) {
				return SimpleParamGetter.getInstance();
			} else {
				return new FilterAbleParamGetter(paramsFilters);
			}
		} else {
			if (CollectionUtils.isEmpty(paramsFilters)) {
				return new ConvertAbleParamGetter(paramsConverters);
			} else {
				return new FullFeaturesParamGetter(paramsConverters, paramsFilters);
			}
		}
	}

	/**
	 * 简单参数获取器
	 * 
	 * @author June wjzhao@aliyun.com
	 * 
	 * @since 1.3.0
	 */
	public static class SimpleParamGetter implements ParamGetter {

		private static final SimpleParamGetter INSTANCE = new SimpleParamGetter();

		private SimpleParamGetter() {
			super();
		}

		public static SimpleParamGetter getInstance() {
			return INSTANCE;
		}

		@Override
		public Object getValue(Object params, String paramName) {
			return ObjectUtils.getValueIgnoreException(params, paramName);
		}

	}

	/**
	 * 支持转换的参数获取器
	 * 
	 * @author June wjzhao@aliyun.com
	 * 
	 * @since 1.3.0
	 *
	 */
	public static class ConvertAbleParamGetter implements ParamGetter {

		private final Map<String, Object> convertedParams = new HashMap<String, Object>();

		private final List<ParamsConverter<?>> converters;

		private ConvertAbleParamGetter(List<ParamsConverter<?>> converters) {
			super();
			this.converters = converters;
		}

		public static ConvertAbleParamGetter newInstance(List<ParamsConverter<?>> converters) {
			return new ConvertAbleParamGetter(converters);
		}

		@Override
		public Object getValue(Object params, String paramName) {
			if (convertedParams.containsKey(paramName)) {
				return convertedParams.get(paramName);
			}
			return convert(converters, convertedParams, paramName,
					ObjectUtils.getValueIgnoreException(params, paramName));
		}

	}

	/**
	 * 支持过滤的参数获取器
	 * 
	 * @author June wjzhao@aliyun.com
	 * 
	 * @since 1.3.0
	 *
	 */
	public static class FilterAbleParamGetter implements ParamGetter {

		private final Set<String> filteredParams = new HashSet<String>();

		private final List<ParamsFilter> filters;

		private FilterAbleParamGetter(List<ParamsFilter> filters) {
			super();
			this.filters = filters;
		}

		public static FilterAbleParamGetter newInstance(List<ParamsFilter> filters) {
			return new FilterAbleParamGetter(filters);
		}

		@Override
		public Object getValue(Object params, String paramName) {
			Object value = ObjectUtils.getValueIgnoreException(params, paramName);
			if (filteredParams.contains(paramName) || filtered(filters, filteredParams, paramName, value)) {
				return null;
			}
			return value;
		}

	}

	/**
	 * 具有完整特性（支持参数转换和过滤）的参数获取器
	 * 
	 * @author June wjzhao@aliyun.com
	 * 
	 * @since 1.3.0
	 *
	 */
	public static class FullFeaturesParamGetter implements ParamGetter {

		private final Map<String, Object> convertedParams = new HashMap<String, Object>();

		private final Set<String> filteredParams = new HashSet<String>();

		private final List<ParamsConverter<?>> converters;

		private final List<ParamsFilter> filters;

		private FullFeaturesParamGetter(List<ParamsConverter<?>> converters, List<ParamsFilter> filters) {
			super();
			this.converters = converters;
			this.filters = filters;
		}

		public static FullFeaturesParamGetter newInstance(List<ParamsConverter<?>> converters,
				List<ParamsFilter> filters) {
			return new FullFeaturesParamGetter(converters, filters);
		}

		@Override
		public Object getValue(Object params, String paramName) {
			if (convertedParams.containsKey(paramName)) {
				return convertedParams.get(paramName);
			}
			if (filteredParams.contains(paramName)) {
				return null;
			}
			Object value = convert(converters, convertedParams, paramName,
					ObjectUtils.getValueIgnoreException(params, paramName));
			return filtered(filters, filteredParams, paramName, value) ? null : value;
		}

	}

	/**
	 * 参数获取器
	 * 
	 * @author wjzhao@aliyun.com
	 * 
	 * @since 1.3.0
	 */
	protected static interface ParamGetter {
		/**
		 * 获取参数值
		 * 
		 * @param params
		 *            参数集
		 * @param paramName
		 *            参数名称
		 * @return 参数值
		 */
		Object getValue(Object params, String paramName);
	}

}