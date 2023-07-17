package cn.tenmg.dsl.eval;

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

	Interpreter interpreter = new Interpreter();

	@Override
	public void put(String key, Object value) throws Exception {
		interpreter.set(key, value);
	}

	@Override
	public Object eval(String code) throws Exception {
		return interpreter.eval(code);
	}

}
