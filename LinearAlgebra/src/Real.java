/*  实数类
    1. 以根号的形式展示所有指数不是1的数字
    2. 支持分母有理化和最简根式
    3. 支持根式之间的四则运算
    4. 支持数据的无损保存、传递和输出*/

import java.math.BigDecimal;

//FIXME 暂时先只写二次根式
public class Real {
    //符号
    private int sign = 1;
    //分子的指数
    private Fraction nExponent = Fraction.ONE;
    //分子的底数
    private Fraction nBase;
    //分子根式的系数
    private Fraction nCoefficient = Fraction.ONE;
    //分母根式的系数
    private Fraction dCoefficient = Fraction.ONE;

    //分母的底数
    private Fraction dBase = Fraction.ONE;
    //分母的指数
    private Fraction dExponent = Fraction.ONE;
    //静态0
    public static final Real ZERO = new Real("0");
    //静态1
    public static final Real ONE = new Real("1");

    ///字符串全参构造，格式：a*b^/c*d^，只写^默认表示二次根式，目前仅支持二次根式！
    public Real(String inputStr) {
        String numerator, denominator;
        if (inputStr.startsWith("-")) {
            sign *= -1;
            inputStr = inputStr.substring(1);
        }
        if (inputStr.contains("/")) {
            denominator = inputStr.split("/")[1];
            if (denominator.startsWith("-")) {
                sign *= -1;
                denominator = denominator.substring(1);
            }
            if (denominator.contains("*") && denominator.contains("^")) {
                dBase = new Fraction(denominator.split("\\*")[1].substring(0, denominator.split("\\*")[1].length() - 1));
                dCoefficient = new Fraction(denominator.split("\\*")[0]);
                dBase = new Fraction("0.5");
            } else if (denominator.contains("^")) {
                dBase = new Fraction(denominator.split("\\^")[0]);
                dExponent = new Fraction("0.5");
            } else dBase = new Fraction(denominator);
        }
        numerator = inputStr.split("/")[0];
        if (numerator.contains("*") && numerator.contains("^")) {
            nBase = new Fraction(numerator.split("\\*")[1].substring(0, numerator.split("\\*")[1].length() - 1));
            nCoefficient = new Fraction(numerator.split("\\*")[0]);
            nExponent = new Fraction("0.5");
        } else if (numerator.contains("^")) {
            nBase = new Fraction(numerator.split("\\^")[0]);
            nExponent = new Fraction("0.5");
        } else nBase = new Fraction(numerator);
        simplify();
    }

    ///用实数的构造方法
    public Real(Real real) {
        sign = real.sign;
        nCoefficient = real.nCoefficient;
        nBase = real.nBase;
        nExponent = real.nExponent;
        dCoefficient = real.dCoefficient;
        dBase = real.dBase;
        dExponent = real.dExponent;
    }

    ///分别输入每一个值的构造方法
    public Real(int sign, Fraction nCoefficient, Fraction nBase, Fraction nExponent, Fraction dCoefficient, Fraction dBase, Fraction dExponent) {
        this.sign = sign;
        this.nCoefficient = nCoefficient;
        this.nBase = nBase;
        this.nExponent = nExponent;
        this.dCoefficient = dCoefficient;
        this.dBase = dBase;
        this.dExponent = dExponent;
    }

    ///分母有理化
    private void rationalize() {
        dExponent = Fraction.ONE;
        nExponent = new Fraction("0.5");
        nBase = nBase.multiply(dBase);
    }

    ///整数化
    private void integerize() {
        if (!nBase.getDenominator().equals(BigDecimal.ONE)) {
            Fraction temp = new Fraction(1, nBase.getDenominator(), BigDecimal.ONE);
            nBase = nBase.multiply(temp);
            dBase = dBase.multiply(temp);
            dExponent = new Fraction("0.5");
        }
        if (!dBase.getDenominator().equals(BigDecimal.ONE)) {
            Fraction temp = new Fraction(1, dBase.getDenominator(), BigDecimal.ONE);
            nBase = nBase.multiply(temp);
            dBase = dBase.multiply(temp);
            nExponent = new Fraction("0.5");
        }
        //系数整数化
        if (!nCoefficient.getDenominator().equals(BigDecimal.ONE)) {
            Fraction temp = new Fraction(1, nCoefficient.getDenominator(), BigDecimal.ONE);
            nCoefficient = nCoefficient.multiply(temp);
            dCoefficient = dCoefficient.multiply(temp);
        }
        if (!dCoefficient.getDenominator().equals(BigDecimal.ONE)) {
            Fraction temp = new Fraction(1, dCoefficient.getDenominator(), BigDecimal.ONE);
            nCoefficient = nCoefficient.multiply(temp);
            dCoefficient = dCoefficient.multiply(temp);
        }
    }

    ///化简。先整数化，再分母有理化。化简时，先化简根式，再化简系数
    private void simplify() {
        transfer();
        if (dCoefficient.notInteger() || dBase.notInteger() || nCoefficient.notInteger() || nBase.notInteger())
            integerize();
        if (dExponent.notInteger()) rationalize();
        //先化简根式。找平方数
        for (int i = 2; i <= Math.sqrt(Integer.parseInt(nBase.toDecimalString())); i++) {
            if (nBase.remainder(new Fraction(1, new BigDecimal(i * i), BigDecimal.ONE)).equals(Fraction.ZERO)) {
                nCoefficient = nCoefficient.multiply(new Fraction(1, new BigDecimal(i), BigDecimal.ONE));
                nBase = nBase.divide(new Fraction(1, new BigDecimal(i * i), BigDecimal.ONE));
                i--;
            }
        }
        transfer();
        //再化简系数。直接用分数类
        Fraction coefficientFraction = new Fraction(1, new BigDecimal(nCoefficient.toDecimalString()), new BigDecimal(dCoefficient.toString()));
        Fraction.simplify(coefficientFraction);
        nCoefficient = new Fraction(1, coefficientFraction.getNumerator(), BigDecimal.ONE);
        dCoefficient = new Fraction(1, coefficientFraction.getDenominator(), BigDecimal.ONE);
    }

    //根式底数为1时，将指数改为1；指数为1但底数不为1时，将底数转为系数。
    private void transfer() {
        if (nBase.equals(Fraction.ONE)) nExponent = Fraction.ONE;
        if (dBase.equals(Fraction.ONE)) dExponent = Fraction.ONE;
        if (nExponent.equals(Fraction.ONE) && !nBase.equals(Fraction.ONE)) {
            nCoefficient = nCoefficient.multiply(nBase);
            nBase = Fraction.ONE;
        }
        if (dExponent.equals(Fraction.ONE) && !dBase.equals(Fraction.ONE)) {
            dCoefficient = dCoefficient.multiply(dBase);
            dBase = Fraction.ONE;
        }
    }

    ///通分
    private void commonize(Real real) {
        if (dExponent.equals(real.dExponent) && dBase.equals(real.dBase) && dCoefficient.equals(real.dCoefficient))
            return;
        //由于创建对象时已经进行分母有理化，所以分母一定没有根号
        Fraction temp = dCoefficient;
        dCoefficient = dCoefficient.multiply(real.dCoefficient);
        nCoefficient = nCoefficient.multiply(real.dCoefficient);
        real.dCoefficient = real.dCoefficient.multiply(temp);
        real.nCoefficient = real.nCoefficient.multiply(temp);
    }

    ///加法，与分数类基本一致
    public Real add(Real real) {
        Real addend = new Real(this);
        Real augend = new Real(real);
        commonize(real);
        if (addend.sign < 0) {
            addend.sign *= -1;
            addend.nCoefficient = addend.nCoefficient.negate();
        }
        if (augend.sign < 0) {
            augend.sign *= -1;
            augend.nCoefficient = augend.nCoefficient.negate();
        }
        addend.nCoefficient = addend.nCoefficient.add(augend.nCoefficient);
        if (addend.nCoefficient.isNegative()) {
            addend.nCoefficient = addend.nCoefficient.negate();
            addend.sign *= -1;
        }
        addend.simplify();
        //若计算完成后结果分子系数为0，则底数和指数都可以初始化了
        if (addend.nCoefficient.equals(Fraction.ZERO)) {
            addend.nBase = Fraction.ZERO;
            addend.nExponent = Fraction.ONE;
        }
        return addend;
    }

    ///减法
    public Real subtract(Real real) {
        Real subtrahend = new Real(real);
        subtrahend.sign *= -1;
        return add(subtrahend);
    }

    ///乘法
    public Real multiply(Real real) {
        Real multiplicand = new Real(this);
        Real multiplier = new Real(real);
        multiplicand.nCoefficient = multiplicand.nCoefficient.multiply(multiplier.nCoefficient);
        if (!multiplicand.nExponent.equals(Fraction.ONE) && multiplicand.nExponent.equals(multiplier.nExponent))
            multiplicand.nBase = multiplicand.nBase.multiply(multiplier.nBase);
        multiplicand.dCoefficient = multiplicand.dCoefficient.multiply(multiplier.dCoefficient);
        if (multiplicand.dExponent.equals(Fraction.ONE) && !multiplier.dExponent.equals(Fraction.ONE)) {
            multiplicand.dExponent = multiplier.dExponent;
            multiplicand.dBase = multiplicand.dBase.multiply(multiplier.dBase);
        } else if (!multiplicand.dExponent.equals(Fraction.ONE) && multiplicand.dExponent.equals(multiplier.dExponent))
            multiplicand.dBase = multiplicand.dBase.multiply(multiplier.dBase);
        multiplicand.sign = sign == multiplier.sign ? 1 : -1;
        multiplicand.simplify();
        return multiplicand;
    }

    ///除法
    public Real divide(Real real) {
        Real dividend = new Real(this);
        Real divisorReciprocal = new Real(sign, real.dCoefficient, real.dBase, real.dExponent, real.nCoefficient, real.nBase, real.nExponent);
        dividend = dividend.multiply(divisorReciprocal);
        dividend.sign = dividend.sign == divisorReciprocal.sign ? 1 : -1;
        dividend.simplify();
        return dividend;
    }

    ///开根号，前提是原式没有根号，且是正数
    public Real sqrt() {
        if (sign == -1 || !this.nExponent.equals(Fraction.ONE) || !this.dExponent.equals(Fraction.ONE))
            throw new ArithmeticException("负数开根号或原式已存在根号");
        Real copy = new Real(this);
        //将分子分母的系数转移到底数中来
        copy.nBase = copy.nBase.multiply(copy.nCoefficient);
        copy.nCoefficient = Fraction.ONE;
        copy.dBase = copy.dBase.multiply(copy.dCoefficient);
        copy.dCoefficient = Fraction.ONE;
        copy.nExponent = new Fraction("0.5");
        copy.dExponent = new Fraction("0.5");
        copy.simplify();
        return copy;
    }

    ///变为相反数
    public Real negate() {
        return new Real(sign * -1, nCoefficient, nBase, nExponent, dCoefficient, dBase, dExponent);
    }

    ///取余运算，仅限整数
    public Real remainder(Real real) {
        Real dividend = new Real(this);
        Real divisor = new Real(real);
        if (dividend.notInteger() || divisor.notInteger()) throw new ArithmeticException("小数取余");
        else {
            if (dividend.sign < 0) {
                dividend.nCoefficient = dividend.nCoefficient.negate();
                dividend.sign *= -1;
            }
            if (divisor.sign < 0) {
                divisor.nCoefficient = divisor.nCoefficient.negate();
                divisor.sign *= -1;
            }
        }
        return new Real(1, dividend.nCoefficient.remainder(divisor.nCoefficient), Fraction.ONE, Fraction.ONE, Fraction.ONE, Fraction.ONE, Fraction.ONE);
    }

    ///判断是否为整数
    public boolean notInteger() {
        return nExponent.notInteger() || dExponent.notInteger() || nCoefficient.notInteger() || !dCoefficient.equals(Fraction.ONE) || nBase.notInteger() || !dBase.equals(Fraction.ONE);
    }

    ///判断是否为负数
    public boolean isNegative() {
        return sign < 0;
    }

    ///判断两个实数是否在数值上相同
    @Override
    public boolean equals(Object real) {
        if (!(real instanceof Real)) return false;
        Real thisCopy = new Real(this);
        Real realCopy = new Real((Real) real);
        return thisCopy.subtract(realCopy).nCoefficient.equals(Fraction.ZERO);
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        StringBuilder numerator = new StringBuilder();
        StringBuilder denominator = new StringBuilder();
        if (sign < 0) result.append("-");
        if (!nCoefficient.equals(Fraction.ONE)) numerator.append(nCoefficient);
        if (nExponent.equals(new Fraction("0.5"))) numerator.append("√").append(nBase);
        if (!dCoefficient.equals(Fraction.ONE) || dExponent.equals(new Fraction("0.5"))) denominator.append("/");
        if (!dCoefficient.equals(Fraction.ONE)) denominator.append(dCoefficient);
        if (dExponent.equals(new Fraction("0.5"))) denominator.append("√").append(dBase);
        result.append(numerator).append(denominator);
        return result.toString();
    }
}
