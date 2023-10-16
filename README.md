# LinearAlgebra：线性代数的Java实现
## 作者：工水 (Apermesa)
学了线代，就想着用代码实现它们。一方面可以巩固线代的知识，思考如何将计算算法化，
    另一方面也可以训练代码能力。

开发时间：2023.7.23-2023.10
### 学术指导：胡旭程
胡旭程没有参与代码编写，但是向我传授了绝大部分的线代知识，也与我讨论了一些算法思想，
    同时他也承担了程序主要的测试工作，在此表示感谢。
---
## 🚀项目特点🚀
### 1. 支持二次根式和分数的输入输出
### 2. 数据无损，没有精度问题
### 3. 双语命令行控制台

---
## 项目介绍
### 本程序可实现线性代数相关计算，支持解线性方程组、矩阵、行列式及向量等运算

### 1. 实数类

- 此类用于封装所有数据，以根号的形式展示所有指数不是1的数字

- 支持用户输入整数、小数、分数及二次根式

- 支持分母有理化和最简根式化简

- 支持根式之间的四则运算

- 支持数据的无损保存、传递和输出

- 封装了分数类（见下文）

### 2. 分数类

此类是实数类的前身，本身已经可以实现绝大多数工作，
但向量的计算会涉及二次根式

### 3. 控制台

集成所有功能，方便用户使用，具体命令见后文

### 4. 行列式计算器

输入一个行列式并计算其具体数值

### 5. 矩阵计算器

- 加法
- 减法
- 乘法
- 方幂
- 数乘
- 转置
- 求秩
- 求逆
- 阶梯型
- 特征值（最高支持二阶）

### 6. 向量计算器

- 加法
- 减法
- 数乘
- 内积
- 外积（二维和三维）
- 模长
- 夹角正弦和余弦值
- 混合积

### 7. 线性方程组计算器

程序会给出解的情况；唯一解提供解向量，无穷解则提供一个基础解系


---
## 控制台使用说明

输入 “zh” 可将语言切换至中文（默认），输入 “en” 可将语言切换至英语

按照提示输入指令以运行相应功能

（你可以随便输入点东西试试，比如2的11次方，但这显然不是一个指令……）

### 矩阵：

加法   -ma 行 列，           减法   -ms 行 列

乘法   -mm 行 列 行 列，     方幂   -mp 指数 阶数

数乘   -mc 数 行 列，        转置   -mt 行 列

求秩   -mr 行 列，           求逆   -mi 阶数

阶梯型 -me 行 列，           特征值 -mg 阶数

### 线性方程组： 

-e 元的个数

### 行列式：

-d 阶数

### 向量：

加法     -va  维，      减法     -vs 维

数乘     -vc  数 维，   内积     -vi 维

二维外积 -vo2，         三维外积 -vo3

模长     -vl  维 ，     夹角正弦 -vas 维

夹角余弦 -vac 维  ，    混合积   -vt

---


## 联系我
QQ 2455358098