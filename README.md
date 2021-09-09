# DSL

## 简介

DSL的全称是动态脚本语言（Dynamic Script Language），它是一种对脚本语言的一种扩展。DSL使用特殊字符`#[]`标记动态片段，当解析时，判断实际传入参数值是否为空（null）决定是否保留该片段，从而达到动态执行不同脚本目的。以此来避免程序员手动拼接繁杂的脚本，使得程序员能从繁杂的业务逻辑中解脱出来。此外，DSL脚本支持宏，来增强脚本的动态逻辑处理能力。

## 动态片段

DSL使用特殊字符`#[]`标记动态片段，动态片段可以是任意脚本片段，参数使用冒号加参数名表示（例如，:staffName）。

### 例子

例如，可以对SQL脚本进行动态化解析。假设有一张员工信息表STAFF_INFO，表结构详见如下建表语句：

```
CREATE TABLE STAFF_INFO (
  STAFF_ID VARCHAR(20) NOT NULL,          /*员工编号*/
  STAFF_NAME VARCHAR(30) DEFAULT NULL,    /*员工姓名*/
  DEPARTMENT_ID VARCHAR(10) DEFAULT NULL, /*部门编号*/
  POSITION VARCHAR(30) DEFAULT NULL,      /*所任职位*/
  STATUS VARCHAR(20) DEFAULT 'IN_SERVICE',/*在职状态*/
  PRIMARY KEY (`STAFF_ID`)
);
```

通常，我们经常需要按员工编号或者按员工姓名查询员工信息。这就需要我们对查询条件进行排列组合，一共会存在

```math
2^2=4
```

种可能的SQL。如果使用SQL拼接的技术实现，显然是比较低效的。如果查询条件数量更多，则拼接SQL会成为难以想象的难题。为此，我们必须有一种技术帮我们来完成这样的事情，动态片段应运而生。有了动态片段，我们对上述问题就能够轻松解决了。

```
SELECT
  *
FROM STAFF_INFO S
WHERE 1=1
  #[AND S.STAFF_ID = :staffId]
  #[AND S.STAFF_NAME LIKE :staffName]
```
有了上述带动态片段的SQL，可以自动根据实际情况生成需要执行的SQL。例如：

1. 参数staffId为空（null），而staffName为非空（非null）时，实际执行的语句为：

```
SELECT
   *
 FROM STAFF_INFO S
 WHERE 1=1
   AND S.STAFF_NAME LIKE :staffName
```

2. 相反，参数staffName为空（null），而staffId为非空（非null）时，实际执行的语句为：

```
SELECT
   *
 FROM STAFF_INFO S
 WHERE 1=1
   AND S.STAFF_ID = :staffId
```

3. 或者，参数staffId、staffName均为空（null）时，实际执行的语句为：

```
SELECT
   *
 FROM STAFF_INFO S
 WHERE 1=1
```

4. 最后，参数staffId、staffName均为非空（非null）时，实际执行的语句为：

```
SELECT
   *
 FROM STAFF_INFO S
 WHERE 1=1
   AND S.STAFF_ID = :staffId
   AND S.STAFF_NAME LIKE :staffName
```

## 使用宏

宏是动态脚本语言（DSL）的重要组成部分，通过宏可以实现一些简单的逻辑处理。宏是基于Java内置的JavaScript引擎实现的，因此其语法是JavaScript语法，而不是Java。目前已实现的宏包括：

```
#[if(……)]
```

```
#[if(……)]
#[else]
```

```
#[if(……)]
#[elseif(……)]
#[else]
```

其中：

```
#[AND STAFF_ID = :staffId]
```

等价于：

```
#[if(:staffId != null) AND STAFF_ID = :staffId]
```
但遇到这种情况，极力推荐使用前者。因为前者代码更简洁，同时不需要运行JavaScript引擎而运行更快。

接上文，假如有如下表数据，以下再给两个运用宏的例子：

1. 部门编号为01的员工可以查看所有员工信息，其他员工仅可以查看自己所在部门的员工信息。假设当前员工所在部门参数为curDepartmentId，那么DSL可以这样编写：

```
SELECT
  *
FROM STAFF_INFO S
WHERE #[if(:curDepartmentId == '01') 1=1]
  #[else S.DEPARTMENT_ID = :curDepartmentId]
  #[AND S.STAFF_ID = :staffId]
  #[AND S.STAFF_NAME LIKE :staffName]
```

2. 部门编号为01的员工可以查看所有员工信息，部门编号为02和03的员工可以查看本部门员工的信息，其他员工仅可以查看本部门跟自己职位一样的员工信息。假设当前员工职位参数为curPosition，所在部门参数为curDepartmentId，那么DSL可以这样编写：

```
SELECT
  *
FROM STAFF_INFO S
WHERE #[if(:curDepartmentId == '01') 1=1]
  #[elseif(:curDepartmentId == '02' || :curDepartmentId == '03') S.DEPARTMENT_ID = :curDepartmentId]
  #[else S.DEPARTMENT_ID = :curDepartmentId AND S.POSITION = :curPosition]
  #[AND S.STAFF_ID = :staffId]
  #[AND S.STAFF_NAME LIKE :staffName]
```

## 使用注释

1.2.0版本以后注释中的命名参数表达式（如`:paramName`）不再被认为是参数，而是会被原样保留，同样，动态片段也会被原样保留。也就是说，注释内的所有内容都会被原封不动地保留。

注释可以在动态片段内部，动态片段内部的注释会跟随动态片段保留而保留，去除而去除；注释也可以在动态片段外部，动态片段外部的注释会被完整保留在脚本中。单行注释的前缀和多行注释的前、后缀都可以在`dsl.properties`[配置文件](https://gitee.com/tenmg/dsl/tree/master#%E9%85%8D%E7%BD%AE%E6%96%87%E4%BB%B6)中自定义，最多支持两个字符。

对于单行注释前缀和多行注释前缀，使用一个字符时，不能使用字符`#`；使用两个字符时，不能使用字符`#[`。对于多行注释后缀，使用一个字符时，不能使用字符`]`。

单行注释的前缀默认为`--`或`//`，例子：

```
-- 这是单行注释……
// 这是单行注释……
```

多行注释的前缀默认为`/*`后缀默认为*/，例子：

```
/* 这是多行
   注释…… */
```

## 配置文件

通过`dsl.properties`配置文件可以调整注释的配置。单行注释前缀通过`comment.singleline`指定，多个前缀之间使用`,`隔开。多行注释前、后缀通过`comment.multiline`指定，成对配置，使用`,`隔开前缀和后缀，多对多行注释前后缀之间使用`;`隔开。`dsl.properties`默认的配置内容为：

```
comment.singleline=--,//
comment.multiline=/*,*/
```

## 特别注意

并非所有参数相关的脚本片段都需要使用动态片段，应该根据需要确定是否使用动态片段。例如，只允许查询本部门员工信息的情况下，当前部门（curDepartmentId）这个参数是必须的，该片段就应该静态化表示：

```
SELECT
  *
FROM STAFF_INFO S
WHERE S.DEPARTMENT_ID = :curDepartmentId
  #[AND S.STATUS = :status]
  #[AND S.STAFF_ID = :staffId]
  #[AND S.STAFF_NAME LIKE :staffName]
```

## 使用说明
以基于Maven项目为例

pom.xml添加依赖，${dsl.version}为版本号，可定义属性或直接使用版本号替换

```
<!-- https://mvnrepository.com/artifact/cn.tenmg/flink-jobs -->
<dependency>
    <groupId>cn.tenmg</groupId>
    <artifactId>dsl</artifactId>
    <version>${dsl.version}</version>
</dependency>
```

调用DSLUtils.parse方法，传入动态脚本和参数执行解析

```
public class DslApp {

    public static void main(String[] args) {
		NamedScript namedScript = DSLUtils.parse("SELECT\r\n" + "  *\r\n" + "FROM STAFF_INFO S\r\n"
				+ "WHERE #[if(:curDepartmentId == '01') 1=1 -- 添加恒等条件， 使得后面的动态条件可以统一，而不需要去除“AND”（注：这里是单行注释）]\r\n"
				+ "  #[elseif(:curDepartmentId == '02' || :curDepartmentId == '03') S.DEPARTMENT_ID = :curDepartmentId]\r\n"
				+ "  #[else S.DEPARTMENT_ID = :curDepartmentId AND S.POSITION = :curPosition]\r\n"
				+ "  /* 注释可以在动态片段内部，动态片段内部的注释会跟随动态片段保留而保留，去除而去除；\r\n"
				+ "  注释也可以在动态片段外部，动态片段外部的注释会被完整保留在脚本中。\r\n"
				+ "  单行注释的前缀、多行注释的前后缀都可以在dsl.properties配置文件中自定义，最多支持两个字符。\r\n"
				+ "  对于单行注释前缀和多行注释前缀，使用一个字符时，不能使用字符“#”；使用两个字符时，不能使用字符“#[”。 */\r\n"
				+ "  对于多行注释后缀，第一个字符不能使用字符“]”。 */\r\n"
				+ "  #[AND S.STAFF_ID = :staffId]\r\n" + "  #[AND S.STAFF_NAME LIKE :staffName]", "staffName", "June");

		Script<List<Object>> script = DSLUtils.toScript(namedScript.getScript(), namedScript.getParams(),
				JDBCParamsParser.getInstance());
		String sql = script.getValue();
		List<Object> params = script.getParams();
		// Use SQL and parameters to execute JDBC

		// Plain script, such as plain SQL
		sql = DSLUtils.toScript(namedScript.getScript(), namedScript.getParams(), new PlaintextParamsParser() {

			@Override
			protected String convert(Object value) {
				if (value instanceof Date) {
					return parse((Date) value);
				} else if (value instanceof Calendar) {
					Date date = ((Calendar) value).getTime();
					if (date == null) {
						return "null";
					} else {
						return parse(date);
					}
				} else {
					return value.toString();
				}
			}

			private String parse(Date date) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
				return "'" + sdf.format(date) + "'";
			}

		}).getValue();
		// Use SQL to execute JDBC
    }

}
```

本例控制台输出为

```
SELECT
  *
FROM STAFF_INFO S
WHERE
   S.DEPARTMENT_ID = :curDepartmentId AND S.POSITION = :curPosition
  AND S.STAFF_NAME LIKE :staffName


```
