package cn.tenmg.dsl.converter;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
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

	private static final Map<String, Integer> fields = MapUtils.newMapBuilder(new HashMap<String, Integer>(7))
			.put("millisecond", Calendar.MILLISECOND).put("second", Calendar.SECOND).put("minute", Calendar.MINUTE)
			.put("hour", Calendar.HOUR).put("day", Calendar.DATE).put("month", Calendar.MONTH)
			.build("year", Calendar.YEAR);

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

}
