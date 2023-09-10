import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashSet;

/*分数类，支持用户输入整数、小数以及指定格式的分数
        支持整数、小数以及分数之间的四则运算和幂运算
        支持数据无损无精度问题，支持小数以分数或小数形式输出。*/

public class Fraction {
    //分子
    private BigDecimal numerator;
    //分母
    private BigDecimal denominator;
    //符号
    private int sign;

    protected BigDecimal getNumerator() {
        return numerator;
    }

    protected BigDecimal getDenominator() {
        return denominator;
    }

    private void setNumerator(BigDecimal numerator) {
        this.numerator = numerator;
    }

    private void setDenominator(BigDecimal denominator) {
        this.denominator = denominator;
    }

    private void setSign(int sign) {
        this.sign = sign;
    }

    ///全参构造
    public Fraction(BigDecimal numerator, BigDecimal denominator, int sign) {
        this.numerator = new BigDecimal(numerator.toPlainString());
        this.denominator = new BigDecimal(denominator.toPlainString());
        this.sign = sign;
        integerize(this);
    }

    ///直接输入字符串的简单粗暴构造方法
    public Fraction(String fraction) {
        boolean sign = true;
        if (fraction.startsWith("-")) {
            sign = false;
            fraction = fraction.substring(1);
        }
        if (fraction.contains("/")) {
            String[] temp = fraction.split("/");
            numerator = new BigDecimal(temp[0]);
            denominator = new BigDecimal(temp[1]);
        } else {
            numerator = new BigDecimal(fraction);
            denominator = BigDecimal.ONE;
        }
        if (denominator.compareTo(BigDecimal.ZERO) < 0) {
            denominator = denominator.negate();
            sign = !sign;
        }
        this.sign = sign ? 1 : -1;
        integerize(this);
    }

    ///不输入符号的构造方法，符号默认为正
    public Fraction(BigDecimal numerator, BigDecimal denominator) {
        this(numerator, denominator, 1);
    }

    ///不输入分母的构造方法，分母默认为1
    public Fraction(BigDecimal numerator, int sign) {
        this(numerator, new BigDecimal(1), sign);
    }

    ///不输入分母和符号的构造方法，分母默认为1，符号默认为正
    public Fraction(BigDecimal numerator) {
        this(numerator, new BigDecimal(1));
    }

    ///静态的数值为0的分数
    public static final Fraction ZERO = new Fraction("0");

    ///静态的数值为1的分数
    public static final Fraction ONE = new Fraction("1");

    ///整数化
    private void integerize(Fraction fraction) {
        //nu和de分别表示分子和分母的小数位数
        int nu = 0;
        int de = 0;
        String numeratorStr = fraction.numerator.toString();
        String denominatorStr = fraction.denominator.toPlainString();
        //不管分子还是分母，必须是小数且小数部分不是全为0才执行整数化操作
        if (numeratorStr.contains(".") && !numeratorStr.split("\\.")[1].matches("0+"))
            nu = numeratorStr.split("\\.")[1].length();
        if (denominatorStr.contains(".") && !denominatorStr.split("\\.")[1].matches("0+"))
            de = denominatorStr.split("\\.")[1].length();
        BigDecimal multiple = BigDecimal.valueOf(Math.pow(10, Math.max(nu, de)));
        fraction.numerator = new BigDecimal(fraction.numerator.multiply(multiple).toPlainString());
        fraction.denominator = new BigDecimal(fraction.denominator.multiply(multiple).toPlainString());
        simplify(fraction);
    }

    ///约分
    protected static void simplify(Fraction fraction) {
        BigDecimal numerator = fraction.numerator;
        BigDecimal denominator = fraction.denominator;
        //两个集合分别存储分子和分母的除1以外的所有因数
        if (numerator.compareTo(BigDecimal.ZERO) == 0) {
            fraction.denominator = BigDecimal.ONE;
            return;
        } else if (numerator.compareTo(denominator) == 0) {
            fraction.numerator = BigDecimal.ONE;
            fraction.denominator = BigDecimal.ONE;
            return;
        }
        boolean loop = true;
        while (loop) {
            ArrayList<BigDecimal> nuArr = new ArrayList<>();
            ArrayList<BigDecimal> deArr = new ArrayList<>();
            findFactor(numerator, nuArr);
            findFactor(denominator, deArr);
            //用第三个集合存储公因数
            HashSet<BigDecimal> common = new HashSet<>();
            for (BigDecimal nu : nuArr) for (BigDecimal de : deArr) if (nu.compareTo(de) == 0) common.add(de);
            if (common.isEmpty()) {
                loop = false;
                continue;
            }
            //接着找到最大的公因数
            BigDecimal max = common.iterator().next();
            for (BigDecimal divisor : common) if (divisor.compareTo(max) > 0) max = divisor;
            //分子分母同时除以最大公因数
            numerator = numerator.divide(max, RoundingMode.HALF_DOWN);
            denominator = denominator.divide(max, RoundingMode.HALF_DOWN);
        }
        fraction.setNumerator(new BigDecimal(numerator.stripTrailingZeros().toPlainString()));
        fraction.setDenominator(new BigDecimal(denominator.stripTrailingZeros().toPlainString()));
    }

    ///获取一个数字的除1以外的所有因数
    private static void findFactor(BigDecimal num, ArrayList<BigDecimal> arr) {
        for (int i = 1; i <= Math.sqrt(num.doubleValue()); i++) {
            if (num.remainder(new BigDecimal(i)).compareTo(BigDecimal.ZERO) == 0) {
                arr.add(new BigDecimal(i));
                arr.add(num.divide(new BigDecimal(i), RoundingMode.HALF_DOWN));
            }
        }
        if (!arr.isEmpty()) arr.remove(0);
    }

    ///通分
    private void commonize(Fraction fraction) {
        //如果两个数的分母相同则无需通分
        if (denominator.compareTo(fraction.denominator) == 0) return;
        BigDecimal oldDenominator = denominator;
        numerator = numerator.multiply(fraction.denominator);
        denominator = denominator.multiply(fraction.denominator);
        fraction.setNumerator(fraction.numerator.multiply(oldDenominator));
        fraction.setDenominator(fraction.denominator.multiply(oldDenominator));
    }

    ///加法
    public Fraction add(Fraction fraction) {
        Fraction addend = new Fraction(numerator, denominator, sign);
        Fraction augend = new Fraction(fraction.toString());
        augend.setNumerator(new BigDecimal(augend.numerator.toPlainString()));
        augend.setDenominator(new BigDecimal(augend.denominator.toPlainString()));
        //如果拿同一个对象进行加法操作，需要将除数分配到新的内存空间，否则结果出错
        if (equals(augend)) augend = new Fraction(augend.numerator, augend.denominator, augend.sign);
        addend.commonize(augend);
        //先统一把符号放到分子上再进行计算
        if (addend.sign < 0) {
            addend.sign *= -1;
            addend.numerator = addend.numerator.negate();
        }
        if (augend.sign < 0) {
            augend.sign *= -1;
            augend.numerator = augend.numerator.negate();
        }
        addend.numerator = addend.numerator.add(augend.numerator);
        //如果算出来分子小于0，则符号要移至sign
        if (addend.numerator.compareTo(BigDecimal.ZERO) < 0) {
            addend.numerator = addend.numerator.negate();
            addend.sign *= -1;
        }
        simplify(addend);
        return addend;
    }

    ///减法
    public Fraction subtract(Fraction fraction) {
        Fraction subtrahend = new Fraction(fraction.toString());
        if (equals(subtrahend))
            subtrahend = new Fraction(subtrahend.numerator, subtrahend.denominator, subtrahend.sign);
        subtrahend.setSign(-1 * subtrahend.sign);
        return add(subtrahend);
    }

    ///乘法
    public Fraction multiply(Fraction fraction) {
        Fraction multiplicand = new Fraction(toString());
        Fraction multiplier = new Fraction(fraction.toString());
        //如果拿同一个对象进行乘法操作，需要将除数分配到新的内存空间，否则结果出错
        if (equals(multiplier))
            multiplier = new Fraction(multiplier.numerator, multiplier.denominator, multiplier.sign);
        multiplicand.numerator = numerator.multiply(multiplier.numerator);
        multiplicand.denominator = denominator.multiply(multiplier.denominator);
        multiplicand.sign = sign == multiplier.sign ? 1 : -1;
        simplify(multiplicand);
        return multiplicand;
    }

    ///除法
    public Fraction divide(Fraction fraction) {
        Fraction dividend = new Fraction(toString());
        Fraction divisor = new Fraction(fraction.toString());
        divisor.setNumerator(new BigDecimal(divisor.numerator.toPlainString()));
        divisor.setDenominator(new BigDecimal(divisor.denominator.toPlainString()));
        //如果拿同一个对象进行除法操作，需要将除数分配到新的内存空间，否则结果出错
        if (equals(divisor))
            divisor = new Fraction(divisor.numerator, new BigDecimal(divisor.denominator.toPlainString()), divisor.sign);
        if (divisor.numerator.compareTo(BigDecimal.ZERO) == 0) {
            System.out.println("0不可作除数");
            System.exit(0);
        }
        dividend.numerator = numerator.multiply(divisor.denominator);
        dividend.denominator = denominator.multiply(divisor.numerator);
        dividend.sign = dividend.sign == divisor.sign ? 1 : -1;
        simplify(dividend);
        return dividend;
    }

    ///幂运算
    //FIXME BigDecimal类只提供整数指数的幂运算，根本不够用。需要写一个根式类。
    public Fraction exponentiate(int exponent) {
        BigDecimal numerator = this.numerator;
        BigDecimal denominator = this.denominator;
        if (sign < 0) {
            sign *= -1;
            numerator = numerator.negate();
        }
        numerator = numerator.pow(exponent, new MathContext(3));
        denominator = denominator.pow(exponent, new MathContext(3));
        if (numerator.compareTo(BigDecimal.ZERO) < 0) {
            sign *= -1;
            numerator = numerator.negate();
        }
        Fraction result = new Fraction(numerator, denominator, sign);
        simplify(result);
        return result;
    }

    ///开根号
    //FIXME 此方法为根式类写好前的临时方法，仅用于临时向量模长计算。根式类写好后此类将会与幂运算方法合并。
    public Fraction squareRoot() {
        if (sign < 0) {
            System.out.println("负数无实数平方根");
            return this;
        }
        BigDecimal numerator = this.numerator;
        BigDecimal denominator = this.denominator;
        numerator = numerator.sqrt(new MathContext(3));
        denominator = denominator.sqrt(new MathContext(3));
        Fraction result = new Fraction(numerator, denominator);
        simplify(result);
        return result;
    }

    ///变为相反数的方法
    public Fraction negate() {
        return new Fraction(numerator, denominator, sign * -1);
    }

    ///取余操作，仅限整数间运算
    public Fraction remainder(Fraction fraction) {
        Fraction dividend = new Fraction(this.toString());
        Fraction divisor = new Fraction(fraction.toString());
        if (dividend.notInteger() || divisor.notInteger()) return dividend;
        else return new Fraction(numerator.remainder(divisor.numerator));
    }

    ///输出分数形式的字符串
    @Override
    public String toString() {
        return (numerator.compareTo(BigDecimal.ZERO) == 0 ? "" : (sign > 0 ? "" : "-")) + numerator.stripTrailingZeros().toPlainString() + (denominator.equals(BigDecimal.ONE) ? "" : "/") + (denominator.equals(BigDecimal.ONE) ? "" : denominator.stripTrailingZeros().toPlainString());
    }

    ///输出小数形式的字符串
    public String toDecimalString() {
        return (sign > 0 ? "" : "-") + numerator.divide(denominator, RoundingMode.HALF_DOWN);
    }

    ///输出小数
    public double toDecimal() {
        return (sign > 0 ? 1 : -1) * numerator.divide(denominator, RoundingMode.HALF_DOWN).doubleValue();
    }

    ///判断是否为负数
    public boolean isNegative() {
        return sign < 0;
    }

    ///判断是否为整数
    public boolean notInteger() {
        return denominator.compareTo(BigDecimal.ONE) != 0;
    }

    ///判断两个分数是否在数值上相同。无法使用subtract方法，否则产生递归调用问题导致栈溢出
    @Override
    public boolean equals(Object fraction) {
        if (!(fraction instanceof Fraction)) return false;
        else if (numerator.compareTo(BigDecimal.ZERO) == 0 && ((Fraction) fraction).numerator.compareTo(BigDecimal.ZERO) == 0)
            return true;
        else if (sign - ((Fraction) fraction).sign != 0) return false;
        Fraction copiedFraction = new Fraction(fraction.toString());
        Fraction thisFraction = new Fraction(this.toString());
        thisFraction.commonize(copiedFraction);
        return thisFraction.numerator.subtract(copiedFraction.numerator).compareTo(BigDecimal.ZERO) == 0;
    }
}
