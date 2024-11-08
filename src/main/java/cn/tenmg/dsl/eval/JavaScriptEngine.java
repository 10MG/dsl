package cn.tenmg.dsl.eval;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import cn.tenmg.dsl.EvalEngine;
import cn.tenmg.dsl.exception.MacroException;
import cn.tenmg.dsl.utils.MapUtils;
import cn.tenmg.dsl.utils.StringUtils;

/**
 * JavaScript 代码执行引擎
 * 
 * @author June wjzhao@aliyun.com
 * 
 * @since 1.4.0
 */
public class JavaScriptEngine implements EvalEngine {
	/**
	 * 脚本引擎管理器
	 */
	private static final ScriptEngineManager SCRIPT_ENGINE_MANAGER = new ScriptEngineManager();

	private ThreadLocal<ScriptEngine> scriptEngineHolder = new ThreadLocal<ScriptEngine>();

	@Override
	public void open() {
		ScriptEngine scriptEngine = SCRIPT_ENGINE_MANAGER.getEngineByName("JavaScript");
		if (scriptEngine == null) {
			throw new RuntimeException(
					"Unable to find JavaScript engine, please import relevant components, such as nashorn-core");
		}
		scriptEngineHolder.set(scriptEngine);
	}

	@Override
	public void put(Map<String, Object> params) {
		String name, decode;
		Key keys[], key;
		Entry<String, Object> entry;
		ScriptEngine scriptEngine = scriptEngineHolder.get();
		for (Iterator<Entry<String, Object>> it = params.entrySet().iterator(); it.hasNext();) {
			entry = it.next();
			name = entry.getKey();
			keys = toKeys(entry.getKey());
			if (keys.length == 1) {
				scriptEngine.put(name, entry.getValue());
			} else {
				Map<String, Object> value = MapUtils.newHashMap(), child;
				key = keys[0];
				checkName(params, key.fullName, name);
				scriptEngine.put(key.name, value);
				int last = keys.length - 1;
				for (int i = 1; i < last; i++) {
					key = keys[i];
					checkName(params, key.fullName, name);
					decode = getEncodeName(params, keys, key, i, name);
					scriptEngine.put(decode, decode);
					child = MapUtils.newHashMap();
					value.put(decode, child);
					value = child;
				}
				key = keys[last];
				decode = getEncodeName(params, keys, key, last, name);
				scriptEngine.put(decode, decode);
				value.put(decode, entry.getValue());
			}
		}
	}

	@Override
	public Object eval(String code) throws Exception {
		return scriptEngineHolder.get().eval(code);
	}

	@Override
	public void close() {
		scriptEngineHolder.remove();
	}

	private static void checkName(Map<String, Object> params, String fullName, String name) {
		if (params.containsKey(fullName)) {
			throw new MacroException(String.format(
					"The parameter name '%s' is a part of another parameter name '%s' in the same macro, please change one of them to another name",
					fullName, name));
		}
	}

	private static String getEncodeName(Map<String, Object> params, Key[] keys, Key key, int index, String name) {
		String decode = StringUtils.decode(key.name);
		if (key.name.equals(decode) && params.containsKey(key.name)) {
			String prefix = keys[index - 1].fullName;
			throw new MacroException(String.format(
					"The parameter name '%s' contains another parameter name '%s' in the same macro, please change to '%s['%s']%s' or '%s[\"%s\"]%s'",
					name, key.name, prefix, key.name, key.leftName, prefix, key.name, key.leftName));
		}
		return decode;
	}

	private static Key[] toKeys(String key) {
		char c;
		boolean inSquare = false;
		List<Key> keys = new ArrayList<Key>();
		StringBuilder keyBuilder = new StringBuilder(key.substring(0, 1));
		for (int i = 1, len = key.length(), last = len - 1; i < len; i++) {
			c = key.charAt(i);
			if (c == '.') {
				if (inSquare) {
					keyBuilder.append(c);
					while (++i < len && (c = key.charAt(i)) != ']') {
						keyBuilder.append(c);
					}
					if (c == ']') {
						inSquare = false;
						keys.add(new Key(keyBuilder.substring(1), key.substring(0, i), key.substring(i)));
						keyBuilder.setLength(0);
					}
				} else if (i < last) {
					if (keyBuilder.length() > 0) {
						keys.add(new Key(keyBuilder.toString(), key.substring(0, i), key.substring(i)));
						keyBuilder.setLength(0);
					}
				} else {
					keys.add(new Key(keyBuilder.append(c).toString(), key.substring(0, i), key.substring(i)));
				}
			} else if (c == '[') {
				if (keyBuilder.length() > 0) {
					if (inSquare) {
						keys.add(new Key(keyBuilder.substring(1), key.substring(0, i), key.substring(i)));
						keyBuilder.setLength(0);
					} else {
						keys.add(new Key(keyBuilder.toString(), key.substring(0, i), key.substring(i)));
						keyBuilder.setLength(0);
					}
				}
				inSquare = true;
				keyBuilder.append(c);
			} else if (c == ']') {
				if (inSquare) {
					inSquare = false;
					keys.add(new Key(keyBuilder.substring(1), key.substring(0, i + 1), key.substring(i + 1)));
					keyBuilder.setLength(0);
				} else {
					keyBuilder.append(c);
				}
			} else {
				keyBuilder.append(c);
			}
		}
		if (keyBuilder.length() > 0) {
			keys.add(new Key(keyBuilder.toString(), key, ""));
		}
		return keys.toArray(new Key[0]);
	}

	private static class Key {

		private String name;

		private String fullName;

		private String leftName;

		public Key(String name, String fullName, String leftName) {
			super();
			this.name = name;
			this.fullName = fullName;
			this.leftName = leftName;
		}
	}
}
