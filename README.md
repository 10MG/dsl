# DSL

## 简介

DSL的全称是动态脚本语言（Dynamic Script Language），它是对脚本语言的一种扩展。DSL使用`:`加参数名表示普通参数，使用`#`加参数名表示嵌入式参数，并使用特殊字符`#[]`标记动态片段，当解析时，判断实际传入参数值是否为空（null）或不存在决定是否保留该动态片段，从而达到动态执行不同脚本目的。以此来避免程序员手动拼接繁杂的脚本，使得程序员能从繁杂的业务逻辑中解脱出来。此外，DSL脚本支持宏，来增强脚本的动态逻辑处理能力。

## 参数

### 普通参数

使用`:`加参数名表示普通参数，例如，:staffName。

### 嵌入式参数

使用`#`加参数名表示（例如，#staffName）嵌入式参数，嵌入式参数会被以字符串的形式嵌入到脚本中。 **值得注意的是：如果在SQL脚本中使用嵌入式参数，会有SQL注入风险，一定注意不要使用前端传参直接作为嵌入式参数使用** 。1.2.2版本开始支持嵌入式参数。

### 动态参数

动态参数是指，根据具体情况确定是否在动态脚本中生效的参数，动态参数是动态片段的组成部分。动态参数既可以是普通参数，也可以嵌入式参数。

### 静态参数

静态参数是相对动态参数而言的，它永远会在动态脚本中生效。在动态片段之外使用的参数就是静态参数。静态参数既可以是普通参数，也可以嵌入式参数。

### 参数访问符

参数访问符包括两种，即`.`和`[]`, 使用`Map`传参时，优先获取键相等的值，只有键不存在时才会将键降级拆分一一访问对象，直到找到参数并返回，或未找到返回`null`。其中`.`用来访问对象的属性，例如`:staff.name`、`#staff.age`；`[]`用来访问数组、集合的元素，例如`:array[0]`、`#map[key]`。理论上，支持任意级嵌套使用，例如`:list[0][1].name`、`#map[key][1].staff.name`。1.2.2版本开始支持参数访问符。

## 动态片段

DSL使用特殊字符`#[]`标记动态片段，并连同动态参数一起构成动态片段，动态片段可以是任意脚本片段。

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

## 扩展宏

可通过实现`cn.tenmg.dsl.Macro`接口来扩展宏。接口源码：

```
public interface Macro {
	/**
	 * 执行宏并返回计算结果
	 * 
	 * @param logic
	 *            宏逻辑代码
	 * @param dslf
	 *            DSL动态片段
	 * @param context
	 *            宏运行的上下文
	 * @param params
	 *            宏运行的参数
	 * @return 返回可执行脚本语言的片段
	 */
	StringBuilder excute(String logic, StringBuilder dslf, Map<String, Object> context, Map<String, Object> params)
			throws Exception;
}
```

接口参数说明：

参数      | 含义           | 说明
----------|---------------|--------------------------------
`logic`   | 宏逻辑代码     | 宏名称之后`(`和`)`之间的部分代码，如`if`/`elseif`宏的判断逻辑
`dslf`    | DSL动态片段    | 宏包裹的部分代码（除去宏名称、括号和宏逻辑代码的部分）
`context` | 宏运行的上下文 | 可以用于存储宏的上下文环境，以辅助后续的宏处理。例如`if`[`elseif`]`else`宏就需要存储上一个判断的结果，以辅助后续的判断。
`params`  | 宏运行的参数   | 传入DSL解析的操作查找表。

1.2.4版本开始，支持两种方式注入宏：一种是使用`@Macro`注解和配置文件中的扫描包名配置实现的注解扫描模式，另一种是直接通过配置文件指定宏的实现类名的配置类名模式。推荐使用注解扫描模式，因为使用起来更加灵活，避免反复修改配置文件。

### 注解扫描模式

1. 配置扫描的包

在配置文件中，配置`scan.packages`指定扫描的包，如果不配置该值则默认仅扫描`cn.tenmg.dsl.macro`包。

```
scan.packages=mypackage
```

2. 编写宏的实现类

```
package mypackage;

import java.util.Map;

import cn.tenmg.dsl.annotion.Macro;

@Macro(name = "MySimpleMacro")
public class MySimpleMacro implements cn.tenmg.dsl.Macro {

	@Override
	public StringBuilder excute(String logic, StringBuilder dslf, Map<String, Object> context,
			Map<String, Object> params) throws Exception {
		// TODO Your logic to process dslf or generate a new script fragment used for the actual excute
		return dslf;// Returns the script fragment used for the actual excute
	}

}
```

### 配置类名模式

除了可以通过注解扫描来注入宏之外，还可以通过配置文件中直接指定宏的实现类名来扩展或重写宏，例如有如下没有注解的宏实现类，可以在配置文件中直接配置`macro.myLogicalMacro=mypackage.MyLogicalMacro`使之生效：

```
package mypackage;

import java.util.Map;

import cn.tenmg.dsl.annotion.Macro;

public class MySimpleMacro implements cn.tenmg.dsl.Macro {

	@Override
	public StringBuilder excute(String logic, StringBuilder dslf, Map<String, Object> context,
			Map<String, Object> params) throws Exception {
		// TODO Your logic to process dslf or generate a new script fragment used for the actual excute
		return dslf;// Returns the script fragment used for the actual excute
	}

}
```

## 使用注释

1.2.0版本以后注释中的命名参数表达式（如`:paramName`）不再被认为是参数，而是会被原样保留，同样，动态片段也会被原样保留。也就是说，注释内的所有内容都会被原封不动地保留。

注释可以在动态片段内部，动态片段内部的注释会跟随动态片段保留而保留，去除而去除；注释也可以在动态片段外部，动态片段外部的注释会被完整保留在脚本中。单行注释的前缀和多行注释的前、后缀都可以在`dsl.properties`[配置文件](#%E9%85%8D%E7%BD%AE%E6%96%87%E4%BB%B6)中自定义，最多支持两个字符。

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

通过`dsl.properties`配置文件可以调整DSL的配置，各配置项如下：


配置项                | 默认值   | 说明
---------------------|----------|--------------------------------
`dynamic.prefix`     |  `#[`    | 动态脚本片段前缀，仅支持2个字符。
`dynamic.suffix`     |  `]`     | 动态脚本片段后缀，仅支持1个字符。
`param.prefix`       |  `:`     | 普通参数前缀，仅支持1个字符。
`embed.prefix`       |  `#`     | 嵌入参数前缀，仅支持1个字符。
`comment.singleline` |  `--,//` | 嵌入参数前缀，仅支持1个字符。
`comment.multiline`  |  `/*,*/` | 嵌入参数前缀，仅支持1个字符。
`macro.*`            | `macro.if=cn.tenmg.dsl.macro.If`<br>`macro.elseif=cn.tenmg.dsl.macro.ElseIf`<br>`macro.else=cn.tenmg.dsl.macro.Else` | 宏实现类配置。默认的配置分别对应`#[if]`、`#[elseif]`和`#[else]`三个内置的逻辑判断宏。

如果用户想变更实际使用的配置文件，则需要在`dsl-context-loader.properties`中给配置文件相对classpath的具体位置，例如：

 **dsl-context-loader.properties** 

```
config.location=custom-dsl.properties
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
<!-- https://mvnrepository.com/artifact/cn.tenmg/dsl -->
<dependency>
    <groupId>cn.tenmg</groupId>
    <artifactId>dsl</artifactId>
    <version>${dsl.version}</version>
</dependency>
```

调用DSLUtils.parse方法，传入动态脚本和参数执行动态解析，然后再调用DSLUtils.toScript方法将含命名参数的脚本解析为实际可执行的脚本（和参数）。

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
				+ "  #[AND S.STAFF_ID = :staffId]\r\n"
				+ "  #[AND S.STAFF_NAME LIKE :staffName]", "staffName", "June");

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

## 参与贡献

1.  Fork 本仓库
2.  新建 Feat_xxx 分支
3.  提交代码
4.  新建 Pull Request

## 友情链接

DSQL开源地址：https://gitee.com/tenmg/dsql