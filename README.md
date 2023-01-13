# DSL

<p align="left">
    <a href="https://mvnrepository.com/artifact/cn.tenmg/dsl">
        <img alt="maven" src="https://img.shields.io/maven-central/v/cn.tenmg/dsl.svg?style=flat-square">
    </a>
    <a target="_blank" href="LICENSE"><img src="https://img.shields.io/:license-Apache%202.0-blue.svg"></a>
    <a target="_blank" href='https://gitee.com/tenmg/dsl'>
        <img src="https://gitee.com/tenmg/dsl/badge/star.svg?theme=white" />
    </a>
</p>

## 简介

DSL的全称是动态脚本语言（Dynamic Script Language），它是对脚本语言的一种扩展。DSL使用`:`和参数名表示普通参数，使用`#`和参数名表示嵌入参数，并使用特殊字符`#[]`标记动态片段，当解析时，判断实际传入参数值是否为空（`null`）或不存在决定是否保留该动态片段，从而达到动态执行不同脚本目的。以此来避免程序员手动拼接繁杂的脚本，使得程序员能从繁杂的业务逻辑中解脱出来。此外，DSL脚本支持宏，来增强脚本的动态逻辑处理能力。由于具有很强的动态处理能力，目前DSL最成功的应用领域是动态结构化查询语言（DSQL）。

## 参数

### 普通参数

使用`:`和参数名表示普通参数。例如，:staffName。

### 嵌入参数

使用`#`和参数名表示嵌入参数（例如，#staffName）。1.2.2版本开始支持嵌入参数，嵌入参数会被以字符串的形式嵌入到脚本中。**值得注意的是：如果在SQL脚本中使用嵌入参数，会有SQL注入风险，一定注意不要将前端传参直接作为嵌入参数使用，如果使用必需进行合法性检验**。

### 动态参数

动态参数是指，根据具体情况确定是否在动态脚本中生效的参数，动态参数是动态片段的组成部分。动态参数既可以是普通参数，也可以嵌入参数。

### 静态参数

静态参数是相对动态参数而言的，它永远会在动态脚本中生效。在动态片段之外使用的参数就是静态参数。静态参数既可以是普通参数，也可以嵌入参数。

### 参数访问符

参数访问符包括两种，即`.`和`[]`, 使用`Map`传参时，优先获取键相等的值，只有键不存在时才会将键降级拆分一一访问对象，直到找到参数并返回，或未找到返回`null`。其中`.`用来访问对象的属性，例如`:staff.name`、`#staff.age`；`[]`用来访问数组、集合的元素，例如`:array[0]`、`#map[key]`。理论上，支持任意级嵌套使用，例如`:list[0][1].name`、`#map[key][1].staff.name`。1.2.2版本开始支持参数访问符。

## 参数转换器

参数转换器用于对参数值进行转换，主要应用场景是统一获取用户输入参数后可能对应值的类型或者内容需要转换后才适合在动态脚本中使用。例如，从`ServletRequest`中获取的参数均为字符串类型（`java.lang.String`），有可能部分参数需要转换为数字类型（`java.lang.Number`）或者时间类型（`java.util.Date`）等。


### ToNumberParamsConverter

将参数转换为数字 `java.lang.Number` 类型的转换器。

属性        | 含义         | 说明
------------|-------------|--------------------------------
`params`    | 参数名表达式 | 可使用 `*` 作为通配符，多个参数表达式之间使用逗号分隔。
`formatter` | 格式模板     | 如 `#,###.00` 等。


### ToDateParamsConverter

将参数转换为 `java.util.Date` 类型的转换器。

属性        | 含义         | 说明
------------|-------------|--------------------------------
`params`    | 参数名表达式 | 可使用 `*` 作为通配符，多个参数表达式之间使用逗号分隔。
`formatter` | 格式模板     | 如 `yyyy-MM-dd HH:mm:ss.S` 等。


### DateAddParamsConverter

时间（`java.util.Date`）参数加法运算转换器。

属性      | 含义         | 说明
----------|-------------|--------------------------------
`params`  | 参数名表达式 | 可使用 `*` 作为通配符，多个参数表达式之间使用逗号分隔。
`amount`  | 时间量       | 整数，为负值时，相当于减法。
`unit`    | 时间单位     | 可选值：`year/month/day/hour/minute/second/millisecond`，分别对应：年/月/日/时/分/秒/毫秒。


### ToStringParamsConverter

将参数转换为 `java.lang.String` 类型的转换器。

属性        | 含义         | 说明
------------|-------------|--------------------------------
`params`    | 参数名表达式 | 可使用 `*` 作为通配符，多个参数表达式之间使用逗号分隔。


### WrapStringParamsConverter

对 `java.lang.String` 类型的非 `null` 参数值进行包装的转换器。

属性        | 含义         | 说明
------------|-------------|--------------------------------
`params`    | 参数名表达式 | 可使用 `*` 作为通配符，多个参数表达式之间使用逗号分隔。
`formatter` | 包装模板     | 使用占位符 `${value}` 表示参数值，可根据需要添加其他内容。


### SplitParamsConverter

将类型为 java.lang.String 的非 null 参数值进行分割的转换器

属性     | 含义              | 说明
---------|------------------|--------------------------------
`params` | 参数名表达式      | 可使用 `*` 作为通配符，多个参数表达式之间使用逗号分隔。
`regex`  | 分割正则表达式    | 如 “,” 等。
`limit`  | 最大分割子字符串数 | 可缺省，指定时，必须为大于1的正整数。


## 参数过滤器

### BlankParamsFilter

空白字符串参数过滤器，作用是过滤掉值为空白字符串参数。

属性        | 含义         | 说明
------------|-------------|--------------------------------
`params`    | 参数名表达式 | 可使用 `*` 作为通配符，多个参数表达式之间使用逗号分隔。默认为 `*`。


### EqParamsFilter

等值参数过滤器，作用是过滤掉与指定的比较值相等的参数，比较之前会将两个值进行类型转换以确保类型一致。优先将比较值转换为参数值的类型。

属性        | 含义         | 说明
------------|-------------|--------------------------------
`params`    | 参数名表达式 | 可使用 `*` 作为通配符，多个参数表达式之间使用逗号分隔。默认为 `*`。
`value `    | 供比较的值   | 


### GtParamsFilter

大值参数过滤器，作用是过滤掉比指定的比较值大的参数，比较之前会将两个值进行类型转换以确保类型一致。优先将比较值转换为参数值的类型。

属性        | 含义         | 说明
------------|-------------|--------------------------------
`params`    | 参数名表达式 | 可使用 `*` 作为通配符，多个参数表达式之间使用逗号分隔。默认为 `*`。
`value `    | 供比较的值   | 


### GteParamsFilter

大于等于参数过滤器，作用是过滤掉与指定的比较值相等或者比指定的比较值大的参数，比较之前会将两个值进行类型转换以确保类型一致。优先将比较值转换为参数值的类型。

属性        | 含义         | 说明
------------|-------------|--------------------------------
`params`    | 参数名表达式 | 可使用 `*` 作为通配符，多个参数表达式之间使用逗号分隔。默认为 `*`。
`value `    | 供比较的值   | 


### LtParamsFilter

小值参数过滤器，作用是过滤掉比指定的比较值小的参数，比较之前会将两个值进行类型转换以确保类型一致。优先将比较值转换为参数值的类型。

属性        | 含义         | 说明
------------|-------------|--------------------------------
`params`    | 参数名表达式 | 可使用 `*` 作为通配符，多个参数表达式之间使用逗号分隔。默认为 `*`。
`value `    | 供比较的值   | 


### LteParamsFilter

小于等于参数过滤器，作用是过滤掉与指定的比较值相等或者比指定的比较值小的参数，比较之前会将两个值进行类型转换以确保类型一致。优先将比较值转换为参数值的类型。

属性        | 含义         | 说明
------------|-------------|--------------------------------
`params`    | 参数名表达式 | 可使用 `*` 作为通配符，多个参数表达式之间使用逗号分隔。默认为 `*`。
`value `    | 供比较的值   | 


## 参数解析器

将动态脚本解析完成以后，在执行之前需要使用参数解析器将参数代入脚本中形成可执行脚本。目前已内置了两种参数解析器供选择。

### JDBCParamsParser

JDBC参数解析器。将脚本中的命名参数替换为 `?` ，并将参数以此放入 `ArrayList` 中。


### PlaintextParamsParser

明文参数解析器抽象类。将脚本中的命名参数替换为参数值，其中字符串参数将在替换的参数值上添加单引号。由于不同场景下，时间类型参数的转换差异很大，因此这部分还需用户自行实现。


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

1. 参数staffId为空（`null`），而staffName为非空（非`null`）时，实际执行的语句为：

```
SELECT
   *
 FROM STAFF_INFO S
 WHERE 1=1
   AND S.STAFF_NAME LIKE :staffName
```

2. 相反，参数staffName为空（`null`），而staffId为非空（非`null`）时，实际执行的语句为：

```
SELECT
   *
 FROM STAFF_INFO S
 WHERE 1=1
   AND S.STAFF_ID = :staffId
```

3. 或者，参数staffId、staffName均为空（`null`）时，实际执行的语句为：

```
SELECT
   *
 FROM STAFF_INFO S
 WHERE 1=1
```

4. 最后，参数staffId、staffName均为非空（非`null`）时，实际执行的语句为：

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
	 * 执行宏并解析DSL动态片段。如果返回结果为{@code true}，则DSL解析立即终止，并以当前宏解析DSL动态片段的结果为DSL解析的最终结果；否则，将当前宏解析的DSL片段结果拼接到DSL的主解析结果中，并继续后续解析。
	 * 
	 * @param context
	 *            DSL上下文
	 * @param attributes
	 *            属性表。由当前层已运行的宏所存储，供本层后续执行的宏使用
	 * @param logic
	 *            逻辑代码
	 * @param dslf
	 *            DSL动态片段
	 * @param params
	 *            宏运行的参数
	 * @return 如果返回结果为{@code true}，则DSL解析立即终止，并以当前宏解析DSL动态片段的结果为DSL解析的最终结果；否则，将当前宏解析的DSL片段结果拼接到DSL的主解析结果中，并继续后续解析。
	 */
	boolean execute(DSLContext context, Map<String, Object> attributes, String logic, StringBuilder dslf,
			Map<String, Object> params) throws Exception;
}
```

接口参数说明：

参数         | 含义           | 说明
-------------|---------------|--------------------------------
`context`    | DSL上下文      | DSL解析运行的上下文，默认为一个空的 `DefaultDSLContext` ，可在解析阶段传入自己的上下文。
`attributes` | 属性表         | 由当前层已运行的宏所存储，供本层后续执行的宏使用。例如`if`[`elseif`]`else`宏就需要存储上一个判断的结果，以辅助后续的判断。
`logic`      | 宏逻辑代码     | 宏名称之后`(`和`)`之间的部分代码，如`if`/`elseif`宏的判断逻辑
`dslf`       | DSL动态片段    | 除去宏名称、括号和宏逻辑代码的部分，宏包裹的剩余部分代码
`params`     | 宏运行的参数   | 当前宏所用到的参数查找表。

1.2.4版本开始，支持两种方式注入宏：一种是使用`@Macro`注解和配置文件中的扫描包名配置实现的注解扫描模式，另一种是直接通过配置文件指定宏的实现类名的配置类名模式。推荐使用注解扫描模式，因为使用起来更加灵活，避免反复修改配置文件。1.3.0版本开始，增加 Java 原生服务加载方式扩展宏的实现。

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

@Macro(name = "MyMacroName")
public class MyMacro implements cn.tenmg.dsl.Macro {

	@Override
	boolean execute(DSLContext context, Map<String, Object> attributes, String logic, StringBuilder dslf,
			Map<String, Object> params) throws Exception
		// TODO Your logic to process dslf to an actual running script
		return false;// Returns false, indicating that the processed script is only part of the actual running script
	}

}
```

### 配置类名模式

除了可以通过注解扫描来注入宏之外，还可以通过配置文件中直接指定宏的实现类名来扩展或重写宏。例如，可以在配置文件中直接配置`macro.MyMacroName=mypackage.MyMacro`使以下宏生效：

```
package mypackage;

import java.util.Map;

import cn.tenmg.dsl.annotion.Macro;

public class MyMacro implements cn.tenmg.dsl.Macro {

	@Override
	boolean execute(DSLContext context, Map<String, Object> attributes, String logic, StringBuilder dslf,
			Map<String, Object> params) throws Exception
		// TODO Your logic to process dslf to an actual running script
		return false;// Returns false, indicating that the processed script is only part of the actual running script
	}

}
```

### 服务加载模式

此外，还可以通过 Java 原生服务加载方式扩展宏的实现，且同样支持使用注解自定义宏名称。仅需在类路径（classpath）下创建 META-INF/services/cn.tenmg.dsl.Macro文件并在文件内填写类名即可，多个类使用换行分隔。 例如，Maven 项目的结构如下：

```
resources
    └─META-INF
        └─services
             └─cn.tenmg.dsl.Macro
```

```
mypackage.MyMacro
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
`comment.singleline` |  `--,//` | 单行注释前缀，最多2个字符。
`comment.multiline`  |  `/*,*/` | 多行注释前缀、后缀，前后缀之间使用“,”分隔，均最多支持2个字符。
`macro.*`            |          | 宏实现类配置。宏的配置也可Java原生服务加载模式或者使用注解扫描模式来配置，个性化的宏名称可以在注解上配置。

如果用户想变更实际使用的配置文件，则需要在 `dsl-config-loader.properties` （1.2及以前版本为dsl-context-loader.properties）中给出配置文件相对classpath的具体位置，例如：

 **dsl-config-loader.properties** 

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