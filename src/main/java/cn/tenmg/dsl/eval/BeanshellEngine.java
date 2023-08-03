package cn.tenmg.dsl.eval;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import bsh.Interpreter;
import cn.tenmg.dsl.EvalEngine;

/**
 * 基于 Beanshell 的 Java 代码执行引擎
 * 
 * @author June wjzhao@aliyun.com
 * 
 * @since 1.4.0
 */
public class BeanshellEngine implements EvalEngine {

	private ThreadLocal<Interpreter> interpreterHolder = new ThreadLocal<Interpreter>();

	@Override
	public void open() {
		interpreterHolder.set(new Interpreter());
	}

	@Override
	public void put(Map<String, Object> params) throws Exception {
		Entry<String, Object> entry;
		Interpreter interpreter = interpreterHolder.get();
		for (Iterator<Entry<String, Object>> it = params.entrySet().iterator(); it.hasNext();) {
			entry = it.next();
			interpreter.set(entry.getKey(), entry.getValue());
		}
	}

	@Override
	public Object eval(String code) throws Exception {
		return interpreterHolder.get().eval(code);
	}

	@Override
	public void close() {
		interpreterHolder.remove();
	}

}
