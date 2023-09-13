import java.math.BigDecimal;
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
    ///静态的数值为0的分数
    public static final Fraction ZERO = new Fraction("0");

    ///静态的数值为1的分数
    public static final Fraction ONE = new Fraction("1");

    protected BigDecimal getNumerator() {
        return numerator;
    }

    protected BigDecimal getDenominator() {
        return denominator;
    }

    ///全参构造
    public Fraction(int sign, BigDecimal numerator, BigDecimal denominator) {
        this.numerator = new BigDecimal(numerator.toPlainString());
        this.denominator = new BigDecimal(denominator.toPlainString());
        this.sign = sign;
        integerize(this);
    }

    ///输入分数的构造方法
    public Fraction(Fraction fraction) {
        this.sign = fraction.sign;
        this.numerator = fraction.numerator;
        this.denominator = fraction.denominator;
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
        fraction.numerator = new BigDecimal(numerator.stripTrailingZeros().toPlainString());
        fraction.denominator = new BigDecimal(denominator.stripTrailingZeros().toPlainString());
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
        fraction.numerator = fraction.numerator.multiply(oldDenominator);
        fraction.denominator = fraction.denominator.multiply(oldDenominator);
    }

    ///加法
    public Fraction add(Fraction fraction) {
        Fraction addend = new Fraction(sign, numerator, denominator);
        Fraction augend = new Fraction(fraction);
        augend.numerator = new BigDecimal(augend.numerator.toPlainString());
        augend.denominator = new BigDecimal(augend.denominator.toPlainString());
        //如果拿同一个对象进行加法操作，需要将除数分配到新的内存空间，否则结果出错
        if (equals(augend)) augend = new Fraction(augend.sign, augend.numerator, augend.denominator);
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
        Fraction subtrahend = new Fraction(fraction);
        subtrahend.sign *= -1;
        return add(subtrahend);
    }

    ///乘法
    public Fraction multiply(Fraction fraction) {
        Fraction multiplicand = new Fraction(this);
        Fraction multiplier = new Fraction(fraction);
        //如果拿同一个对象进行乘法操作，需要将除数分配到新的内存空间，否则结果出错
        if (equals(multiplier))
            multiplier = new Fraction(multiplier.sign, multiplier.numerator, multiplier.denominator);
        multiplicand.numerator = numerator.multiply(multiplier.numerator);
        multiplicand.denominator = denominator.multiply(multiplier.denominator);
        multiplicand.sign = sign == multiplier.sign ? 1 : -1;
        simplify(multiplicand);
        return multiplicand;
    }

    ///除法
    public Fraction divide(Fraction fraction) {
        Fraction dividend = new Fraction(this);
        Fraction divisor = new Fraction(fraction);
        divisor.numerator = new BigDecimal(divisor.numerator.toPlainString());
        divisor.denominator = new BigDecimal(divisor.denominator.toPlainString());
        //如果拿同一个对象进行除法操作，需要将除数分配到新的内存空间，否则结果出错
        if (equals(divisor))
            divisor = new Fraction(divisor.sign, divisor.numerator, new BigDecimal(divisor.denominator.toPlainString()));
        if (divisor.numerator.compareTo(BigDecimal.ZERO) == 0) throw new ArithmeticException("0作除数");
        dividend.numerator = numerator.multiply(divisor.denominator);
        dividend.denominator = denominator.multiply(divisor.numerator);
        dividend.sign = dividend.sign == divisor.sign ? 1 : -1;
        simplify(dividend);
        return dividend;
    }

    ///变为相反数的方法
    public Fraction negate() {
        return new Fraction(sign * -1, numerator, denominator);
    }

    ///取余操作，仅限整数间运算
    public Fraction remainder(Fraction fraction) {
        Fraction dividend = new Fraction(this);
        Fraction divisor = new Fraction(fraction);
        if (dividend.notInteger() || divisor.notInteger()) return dividend;
            //考虑负数，将符号提至分子的位置
        else {
            if (dividend.sign < 0) {
                dividend.numerator = dividend.numerator.negate();
                dividend.sign *= -1;
            }
            if (divisor.sign < 0) {
                divisor.numerator = divisor.numerator.negate();
                divisor.sign *= -1;
            }
            return new Fraction(1, dividend.numerator.remainder(divisor.numerator), BigDecimal.ONE);
        }

    }

    ///输出分数形式的字符串
    @Override
    public String toString() {
        return (numerator.compareTo(BigDecimal.ZERO) == 0 ? "" : (sign > 0 ? "" : "-")) + numerator.stripTrailingZeros().toPlainString() + (denominator.equals(BigDecimal.ONE) ? "" : "/") + (denominator.equals(BigDecimal.ONE) ? "" : denominator.stripTrailingZeros().toPlainString());
    }

    ///输出小数形式的字符串
    public String toDecimalString() {
        return (sign > 0 ? "" : "-") + numerator.divide(denominator, 100, RoundingMode.HALF_UP).stripTrailingZeros();
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
        Fraction copiedFraction = new Fraction((Fraction) fraction);
        Fraction thisFraction = new Fraction(this);
        thisFraction.commonize(copiedFraction);
        return thisFraction.numerator.subtract(copiedFraction.numerator).compareTo(BigDecimal.ZERO) == 0;
    }
}
