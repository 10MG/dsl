# DSL

# 一、简介

<p style="text-indent:2em">
DSL的全称是动态脚本语言（Dynamic Script Language），他是一种对脚本语言的一种扩展。DSL使用特殊字符#[]标记动态片段，当解析时，判断实际传入参数值是否为空（null）决定是否保留该片段，从而达到动态执行不同脚本目的。以此来避免程序员手动拼接繁杂的脚本，使得程序员能从繁杂的业务逻辑中解脱出来。此外，DSL脚本支持宏，来增强脚本的动态逻辑处理能力。
</p>

# 二、动态片段

<p style="text-indent:2em">
DSL使用特殊字符#[]标记动态片段，动态片段可以是任意脚本片段，参数使用冒号加参数名表示（例如，:staffName）。
</p>

## 例子

<p style="text-indent:2em">
例如，可以对SQL脚本进行动态化解析。假设有一张员工信息表STAFF_INFO，表结构详见如下建表语句：
</p>

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

<p style="text-indent:2em">
通常，我们经常需要按员工编号或者按员工姓名查询员工信息。这就需要我们对查询条件进行排列组合，一共会存在

```math
2^2=4
```

种可能的SQL。如果使用SQL拼接的技术实现，显然是比较低效的。如果查询条件数量更多，则拼接SQL会成为难以想象的难题。为此，我们必须有一种技术帮我们来完成这样的事情，动态片段应运而生。有了动态片段，我们对上述问题就能够轻松解决了。
</p>

```
SELECT
  *
FROM STAFF_INFO S
WHERE 1=1
  #[AND S.STAFF_ID = :staffId]
  #[AND S.STAFF_NAME LIKE :staffName]
```
<p style="text-indent:2em">有了上述带动态片段的SQL，可以自动根据实际情况生成需要执行的SQL。例如：</p>
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

# 三、宏

<p style="text-indent:2em">宏是动态脚本语言（DSL）的重要组成部分，通过宏可以实现一些简单的逻辑处理。宏是基于Java内置的JavaScript引擎实现的，因此其语法是JavaScript语法，而不是Java。目前已实现的宏包括：

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
</p>

其中：

```
#[AND STAFF_ID = :staffId]
```

等价于：

```
#[if(:staffId != null) AND STAFF_ID = :staffId]
```
但遇到这种情况，极力推荐使用前者。因为前者代码更简洁，同时不需要运行JavaScript引擎而运行更快。

<p style="text-indent:2em">
接上文，假如有如下表数据，以下再给两个运用宏的例子：
</p>

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

# 四、特别注意

<p style="text-indent:2em">
并非所有参数相关的脚本片段都需要使用动态片段，应该根据需要确定是否使用动态片段。例如，只允许查询本部门员工信息的情况下，当前部门（curDepartmentId）这个参数是必须的，该片段就应该静态化表示：
</p>

```
SELECT
  *
FROM STAFF_INFO S
WHERE S.DEPARTMENT_ID = :curDepartmentId
  #[AND S.STATUS = :status]
  #[AND S.STAFF_ID = :staffId]
  #[AND S.STAFF_NAME LIKE :staffName]
```

