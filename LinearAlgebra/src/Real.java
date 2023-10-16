/*  实数类-封装分数类
    1. 以根号的形式展示所有指数不是1的数字
    2. 支持分母有理化和最简根式
    3. 支持根式之间的四则运算
    4. 支持数据的无损保存、传递和输出*/

import java.math.BigDecimal;

//FIXME 暂时先只写二次根式，够用
public class Real {
    //符号
    private int sign = 1;
    //分子的指数
    private Fraction nExponent = Fraction.ONE;
    //分子的底数
    private Fraction nBase = Fraction.ONE;
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
                dBase = Fraction.HALF;
            } else if (denominator.contains("^")) {
                dBase = new Fraction(denominator.split("\\^")[0]);
                dExponent = Fraction.HALF;
            } else dBase = new Fraction(denominator);
        }
        numerator = inputStr.split("/")[0];
        if (numerator.contains("*") && numerator.contains("^")) {
            nBase = new Fraction(numerator.split("\\*")[1].substring(0, numerator.split("\\*")[1].length() - 1));
            nCoefficient = new Fraction(numerator.split("\\*")[0]);
            nExponent = Fraction.HALF;
        } else if (numerator.contains("^")) {
            nBase = new Fraction(numerator.split("\\^")[0]);
            nExponent = Fraction.HALF;
        } else nCoefficient = new Fraction(numerator);
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

    ///用double的构造方法
    public Real(double number) {
        this(number + "");
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
        nExponent = Fraction.HALF;
        nBase = nBase.multiply(dBase);
    }

    ///整数化
    private void integerize() {
        if (!nBase.getDenominator().equals(BigDecimal.ONE)) {
            Fraction temp = new Fraction(1, nBase.getDenominator(), BigDecimal.ONE);
            nBase = nBase.multiply(temp);
            dBase = dBase.multiply(temp);
            dExponent = Fraction.HALF;
        }
        if (!dBase.getDenominator().equals(BigDecimal.ONE)) {
            Fraction temp = new Fraction(1, dBase.getDenominator(), BigDecimal.ONE);
            nBase = nBase.multiply(temp);
            dBase = dBase.multiply(temp);
            nExponent = Fraction.HALF;
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
        if (equals(ZERO) && sign < 0) sign *= -1;
        transfer();
        if (dCoefficient.notInteger() || dBase.notInteger() || nCoefficient.notInteger() || nBase.notInteger())
            integerize();
        if (dExponent.notInteger()) rationalize();
        //先化简根式。找平方数
        for (int i = 2; i <= Math.sqrt(Integer.parseInt(nBase.toString())); i++) {
            if (nBase.remainder(new Fraction(1, new BigDecimal(i * i), BigDecimal.ONE)).equals(Fraction.ZERO)) {
                nCoefficient = nCoefficient.multiply(new Fraction(1, new BigDecimal(i), BigDecimal.ONE));
                nBase = nBase.divide(new Fraction(1, new BigDecimal(i * i), BigDecimal.ONE));
                i--;
            }
        }
        transfer();
        //再化简系数。直接用分数类
        Fraction coefficientFraction = new Fraction(1, new BigDecimal(nCoefficient.toString()), new BigDecimal(dCoefficient.toString()));
        Fraction.simplify(coefficientFraction);
        nCoefficient = new Fraction(1, coefficientFraction.getNumerator(), BigDecimal.ONE);
        dCoefficient = new Fraction(1, coefficientFraction.getDenominator(), BigDecimal.ONE);
    }

    //根式底数为1时，将指数改为1；指数为1但底数不为1时，将底数转为系数。
    private void transfer() {
        if (nBase.equals(Fraction.ONE)) nExponent = Fraction.ONE;
        if (dBase.equals(Fraction.ONE)) dExponent = Fraction.ONE;
        if (!nCoefficient.equals(Fraction.ZERO) && nBase.equals(Fraction.ZERO)) nBase = Fraction.ONE;
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

    ///加法，先判断是否为同类项
    public Real add(Real real) {
        if (real.nCoefficient.equals(Fraction.ZERO) || real.nBase.equals(Fraction.ZERO)) return new Real(this);
        transfer();
        real.transfer();
        //若式子里还有根号则比较底数是否相同，一个有根号一个没根号则无法计算
        if (!nExponent.equals(real.nExponent) || (nExponent.equals(real.nExponent) && !nBase.equals(real.nBase)))
            throw new ArithmeticException("\n非同类项");
        Real addend = new Real(this);
        Real augend = new Real(real);
        addend.commonize(augend);
        if (addend.sign < 0) {
            addend.sign *= -1;
            addend.nCoefficient = addend.nCoefficient.negate();
        }
        if (augend.sign < 0) {
            augend.sign *= -1;
            augend.nCoefficient = augend.nCoefficient.negate();
        }
        addend.nCoefficient = addend.nCoefficient.add(augend.nCoefficient);
        if (addend.nCoefficient.sign < 0) {
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
        return add(new Real(real).negate());
    }

    ///乘法
    public Real multiply(Real real) {
        Real multiplicand = new Real(this);
        Real multiplier = new Real(real);
        multiplicand.nCoefficient = multiplicand.nCoefficient.multiply(multiplier.nCoefficient);
        multiplicand.dCoefficient = multiplicand.dCoefficient.multiply(multiplier.dCoefficient);
        if (multiplicand.nExponent.equals(Fraction.ONE) && !multiplier.nExponent.equals(Fraction.ONE)) {
            multiplicand.nExponent = multiplier.nExponent;
            multiplicand.nBase = multiplier.nBase;
        } else {
            multiplicand.nBase = multiplicand.nBase.multiply(multiplier.nBase);
        }
        multiplicand.sign = sign == multiplier.sign ? 1 : -1;
        multiplicand.simplify();
        return multiplicand;
    }

    ///除法
    public Real divide(Real real) {
        Real dividend = new Real(this);
        Real divisorReciprocal = new Real(real.sign, real.dCoefficient, real.dBase, real.dExponent, real.nCoefficient, real.nBase, real.nExponent);
        dividend = dividend.multiply(divisorReciprocal);
        dividend.simplify();
        return dividend;
    }

    ///开根号，前提是原式没有根号，且是正数
    public Real sqrt() {
        if (sign == -1 || !this.nExponent.equals(Fraction.ONE) || !this.dExponent.equals(Fraction.ONE))
            throw new ArithmeticException("\n负数开根号或原式已存在根号");
        Real copy = new Real(this);
        //将分子分母的系数转移到底数中来
        copy.nBase = copy.nBase.multiply(copy.nCoefficient);
        copy.nCoefficient = Fraction.ONE;
        copy.dBase = copy.dBase.multiply(copy.dCoefficient);
        copy.dCoefficient = Fraction.ONE;
        copy.nExponent = Fraction.HALF;
        copy.dExponent = Fraction.HALF;
        copy.simplify();
        return copy;
    }

    ///幂运算，仅支持非负整数指数
    public Real power(int exponent) {
        Real copy = new Real(this);
        if (exponent < 0) throw new ArithmeticException("\n仅支持非负整数指数");
        if (exponent == 0 && copy.equals(ZERO)) throw new ArithmeticException("\n0的0次方未定义");
        else if (exponent == 0) return ONE;
        Real result = new Real(copy);
        for (int i = 1; i < exponent; i++) {
            result = result.multiply(copy);
        }
        return result;
    }

    ///变为相反数
    public Real negate() {
        return new Real(sign * -1, nCoefficient, nBase, nExponent, dCoefficient, dBase, dExponent);
    }

    ///取余运算，仅限整数
    public Real remainder(Real real) {
        Real dividend = new Real(this);
        Real divisor = new Real(real);
        if (dividend.notInteger() || divisor.notInteger()) throw new ArithmeticException("\n小数取余");
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
        else if (nCoefficient.equals(Fraction.ZERO) && ((Real) real).nCoefficient.equals(Fraction.ZERO)) return true;
        else if (sign - (((Real) real).sign) != 0) return false;
        else if (!nExponent.equals(((Real) real).nExponent)) return false;
        else if (!nBase.equals(((Real) real).nBase)) return false;
        Real thisCopy = new Real(this);
        Real realCopy = new Real((Real) real);
        thisCopy.commonize(realCopy);
        return thisCopy.nCoefficient.subtract(realCopy.nCoefficient).equals(Fraction.ZERO);
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        StringBuilder numerator = new StringBuilder();
        StringBuilder denominator = new StringBuilder();
        if (nCoefficient.equals(Fraction.ONE) && nBase.equals(Fraction.ONE)) numerator.append("1");
        if (sign < 0) result.append("-");
        if (!nCoefficient.equals(Fraction.ONE)) numerator.append(nCoefficient);
        if (nExponent.equals(Fraction.HALF)) numerator.append("√").append(nBase);
        if (!dCoefficient.equals(Fraction.ONE) || dExponent.equals(Fraction.HALF)) denominator.append("/");
        if (!dCoefficient.equals(Fraction.ONE)) denominator.append(dCoefficient);
        if (dExponent.equals(Fraction.HALF)) denominator.append("√").append(dBase);
        return result.append(numerator).append(denominator).toString();
    }
}
