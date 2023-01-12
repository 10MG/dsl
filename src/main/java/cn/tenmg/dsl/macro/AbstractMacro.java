package cn.tenmg.dsl.macro;

import cn.tenmg.dsl.Macro;

/**
 * 抽象宏。已废弃，即将在下一版本移除
 * 
 * @author June wjzhao@aliyun.com
 * 
 * @since 1.0.0
 */
@Deprecated
public abstract class AbstractMacro implements Macro {

	protected static final StringBuilder emptyStringBuilder() {
		return new StringBuilder();
	}

}
