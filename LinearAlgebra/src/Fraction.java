import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashSet;

/*分数类，支持用户输入整数、小数以及指定格式的分数
        支持整数、小数以及分数之间的四则运算
        支持数据无损无精度问题，支持小数以分数或小数形式输出。*/

public class Fraction {
    //分子
    private BigDecimal numerator;
    //分母
    private BigDecimal denominator;
    //符号
    private int sign;

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
        integerize();
    }

    ///直接输入字符串的简单粗暴构造方法
    public Fraction(String fraction) {
        boolean positive = true;
        if (fraction.startsWith("-")) {
            positive = false;
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
        this.sign = positive ? 1 : -1;
        integerize();
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
        this(numerator, new BigDecimal(1), 1);
    }

    ///静态的数值为0的分数
    public static final Fraction ZERO = new Fraction("0");

    ///静态的数值为1的分数
    public static final Fraction ONE = new Fraction("1");

    ///整数化
    private void integerize() {
        //nu和de分别表示分子和分母的小数位数
        int nu = 0;
        int de = 0;
        String numeratorStr = numerator.toString();
        String denominatorStr = denominator.toPlainString();
        //不管分子还是分母，必须是小数且小数部分不是全为0才执行整数化操作
        if (numeratorStr.contains(".") && !numeratorStr.split("\\.")[1].matches("0+"))
            nu = numeratorStr.split("\\.")[1].length();
        if (denominatorStr.contains(".") && !denominatorStr.split("\\.")[1].matches("0+"))
            de = denominatorStr.split("\\.")[1].length();
        BigDecimal multiple = BigDecimal.valueOf(Math.pow(10, Math.max(nu, de)));
        numerator = new BigDecimal(numerator.multiply(multiple).toPlainString());
        denominator = new BigDecimal(denominator.multiply(multiple).toPlainString());
        simplify();
    }

    ///约分
    private void simplify() {
        BigDecimal numerator = this.numerator;
        BigDecimal denominator = this.denominator;
        //两个集合分别存储分子和分母的除1以外的所有因数
        if (numerator.compareTo(BigDecimal.ZERO) == 0) {
            this.denominator = BigDecimal.ONE;
            return;
        } else if (numerator.compareTo(denominator) == 0) {
            this.numerator = BigDecimal.ONE;
            this.denominator = BigDecimal.ONE;
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
        this.setNumerator(new BigDecimal(numerator.stripTrailingZeros().toPlainString()));
        this.setDenominator(new BigDecimal(denominator.stripTrailingZeros().toPlainString()));
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
        fraction.setNumerator(new BigDecimal(fraction.numerator.toPlainString()));
        fraction.setDenominator(new BigDecimal(fraction.denominator.toPlainString()));
        //如果拿同一个对象进行加法操作，需要将除数分配到新的内存空间，否则结果出错
        if (this.equals(fraction)) fraction = new Fraction(fraction.numerator, fraction.denominator, fraction.sign);
        commonize(fraction);
        BigDecimal newNumerator = (sign > 0 ? numerator : numerator.negate()).add(fraction.sign > 0 ? fraction.numerator : fraction.numerator.negate());
        numerator = newNumerator;
        sign = newNumerator.compareTo(BigDecimal.ZERO) > 0 ? 1 : -1;
        simplify();
        return this;
    }

    ///减法
    public Fraction subtract(Fraction fraction) {
        if (this.equals(fraction)) fraction = new Fraction(fraction.numerator, fraction.denominator, fraction.sign);
        fraction.setSign(-1 * fraction.sign);
        return add(fraction);
    }

    ///乘法
    public Fraction multiply(Fraction fraction) {
        //如果拿同一个对象进行乘法操作，需要将除数分配到新的内存空间，否则结果出错
        if (this.equals(fraction)) fraction = new Fraction(fraction.numerator, fraction.denominator, fraction.sign);
        numerator = numerator.multiply(fraction.numerator);
        denominator = denominator.multiply(fraction.denominator);
        sign = sign == fraction.sign ? 1 : -1;
        simplify();
        return this;
    }

    ///除法
    public Fraction divide(Fraction fraction) {
        fraction.setNumerator(new BigDecimal(fraction.numerator.toPlainString()));
        fraction.setDenominator(new BigDecimal(fraction.denominator.toPlainString()));
        //如果拿同一个对象进行除法操作，需要将除数分配到新的内存空间，否则结果出错
        if (this.equals(fraction))
            fraction = new Fraction(fraction.numerator, new BigDecimal(fraction.denominator.toPlainString()), fraction.sign);
        if (fraction.numerator.compareTo(BigDecimal.ZERO) == 0) {
            System.out.println("0不可作除数");
            System.exit(0);
        }
        numerator = numerator.multiply(fraction.denominator);
        denominator = denominator.multiply(fraction.numerator);
        sign = sign == fraction.sign ? 1 : -1;
        simplify();
        return this;
    }

    ///变为相反数的方法
    public Fraction negate() {
        sign *= -1;
        return this;
    }

    ///输出分数形式的字符串
    @Override
    public String toString() {
        return (numerator.compareTo(BigDecimal.ZERO) == 0 ? "" : (sign > 0 ? "" : "-")) + numerator.toPlainString() + (denominator.equals(BigDecimal.ONE) ? "" : "/") + (denominator.equals(BigDecimal.ONE) ? "" : denominator.toPlainString());
    }

    ///输出小数形式的字符串
    public String toDecimalString() {
        return (sign > 0 ? "" : "-") + numerator.divide(denominator, RoundingMode.HALF_DOWN);
    }

    ///输出小数
    public double toDecimal() {
        return (sign > 0 ? 1 : -1) * numerator.divide(denominator, RoundingMode.HALF_DOWN).doubleValue();
    }

    ///判断两个分数是否在数值上相同。无法使用subtract方法，否则导致递归调用问题
    @Override
    public boolean equals(Object fraction) {
        if (!(fraction instanceof Fraction)) return false;
        else if (sign - ((Fraction) fraction).sign != 0) return false;
        commonize((Fraction) fraction);
        return numerator.subtract(((Fraction) fraction).numerator).compareTo(BigDecimal.ZERO)==0;
    }
}
