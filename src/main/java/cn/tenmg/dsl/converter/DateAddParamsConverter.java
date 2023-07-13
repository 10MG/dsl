package cn.tenmg.dsl.converter;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import cn.tenmg.dsl.utils.DateUtils;
import cn.tenmg.dsl.utils.MapUtils;

/**
 * 对 {@code java.util.Date} 类型的参数做加法运算的转换器
 * 
 * @author June wjzhao@aliyun.com
 * 
 * @since 1.3.0
 */
public class DateAddParamsConverter extends AbstractParamsConverter<Object> {

	private static final Map<String, Integer> fields = MapUtils.newHashMapBuilder(String.class, Integer.class, 7)
			.put(Unit.MILLISECOND, Calendar.MILLISECOND).put(Unit.SECOND, Calendar.SECOND)
			.put(Unit.MINUTE, Calendar.MINUTE).put(Unit.HOUR, Calendar.HOUR).put(Unit.DAY, Calendar.DATE)
			.put(Unit.MONTH, Calendar.MONTH).build(Unit.YEAR, Calendar.YEAR);

	private int amount;

	private String unit = "day";

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	@Override
	public Object convert(Object value) {
		if (value != null && value instanceof Date) {
			Integer field = fields.get(unit);
			if (field != null) {
				return DateUtils.add((Date) value, field, amount);
			}
		}
		return value;
	}

	public static final class Unit {

		public static final String MILLISECOND = "millisecond", SECOND = "second", MINUTE = "minute", HOUR = "hour",
				DAY = "day", MONTH = "month", YEAR = "year";

	}

}
