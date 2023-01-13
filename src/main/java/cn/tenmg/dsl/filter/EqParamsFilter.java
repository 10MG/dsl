package cn.tenmg.dsl.filter;

/**
 * 等值参数过滤器
 * 
 * @author June wjzhao@aliyun.com
 * 
 * @since 1.3.0
 */
public class EqParamsFilter extends CompareableParamsFilter {

	@Override
	boolean decideNull() {
		return true;
	}

	@Override
	<T extends Comparable<T>> boolean decide(T value, T compared) {
		return value.compareTo(compared) == 0;
	}

}