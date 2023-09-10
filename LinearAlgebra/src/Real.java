/*
*   实数类
*   1. 以根号的形式展示所有指数不是1的数字
    2. 支持分母有理化和最简根式
    3. 支持根式之间的四则运算
    4. 支持数据的无损保存、传递和输出*/
//TODO √

import java.math.BigDecimal;

//FIXME 暂时先只写二次根式
public class Real {
    //分子的指数
    private Fraction nExponent = Fraction.ONE;
    //分子的底数
    private Fraction nBase;
    //分子根式的系数
    private Fraction nCoefficient = Fraction.ONE;
    //分母的指数
    private Fraction dExponent = Fraction.ONE;
    //分母的底数
    private Fraction dBase = Fraction.ONE;
    //分母根式的系数
    private Fraction dCoefficient = Fraction.ONE;
    //符号
    private int sign = 1;

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

    ///分母有理化
    private void rationalize() {
        dExponent = Fraction.ONE;
        nBase = nBase.multiply(dBase);
    }

    ///整数化
    private void integerize() {
        String nBaseStr = nBase.toDecimalString();
        String dBaseStr = dBase.toDecimalString();
        if (nBaseStr.contains("\\.") || dBaseStr.contains("\\.")) {
            Fraction baseMultiple = new Fraction(Math.pow(10, Math.max(dBaseStr.split("\\.")[1].length(), nBaseStr.split("\\.")[1].length())) + "");
            dBase = dBase.multiply(baseMultiple);
            nBase = nBase.multiply(baseMultiple);
        }
        String nCoefficientStr = nCoefficient.toDecimalString();
        String dCoefficientStr = dCoefficient.toDecimalString();
        if (nCoefficientStr.contains("\\.") || dCoefficientStr.contains("\\.")) {
            Fraction coefficientMultiple = new Fraction(Math.pow(10, Math.max(dCoefficientStr.split("\\.")[1].length(), nCoefficientStr.split("\\.")[1].length())) + "");
            dCoefficient = dCoefficient.multiply(coefficientMultiple);
            nCoefficient = nCoefficient.multiply(coefficientMultiple);
        }
    }

    ///化简。先整数化，再分母有理化。化简时，先化简根式，再化简系数
    private void simplify() {
        if (dCoefficient.notInteger() || dBase.notInteger() || nCoefficient.notInteger() || nBase.notInteger())
            integerize();
        if (dExponent.notInteger()) rationalize();
        //先化简根式。找平方数
        for (int i = 2; i < Math.sqrt(Integer.parseInt(nBase.toDecimalString())); i++) {
            if (nBase.remainder(new Fraction(new BigDecimal(i * i))).equals(Fraction.ZERO)) {
                nCoefficient.multiply(new Fraction(new BigDecimal(i)));
                nBase.divide(new Fraction(new BigDecimal(i)));
                i--;
            }
        }
        //再化简系数。直接用分数类
        Fraction coefficientFraction = new Fraction(new BigDecimal(nCoefficient.toDecimalString()), new BigDecimal(dCoefficient.toString()));
        Fraction.simplify(coefficientFraction);
        nCoefficient = new Fraction(coefficientFraction.getNumerator());
        dCoefficient = new Fraction(coefficientFraction.getDenominator());
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        if (sign < 0) result.append("-");
        /*if (!nCoefficient.equals(Fraction.ONE)) result.append(nCoefficient);
        if (!nExponent.equals(Fraction.ONE) && nBase.equals(Fraction.ONE)) result.append("1");
        else if (!nExponent.equals(Fraction.ONE)) result.append("√").append(nBase);
        if(!dCoefficient.equals(Fraction.ONE) || !dBase.equals(Fraction.ONE)) result.append("/").append(dCoefficient);
        if(!dExponent.equals(Fraction.ONE)&&!dBase.equals(Fraction.ONE)) result.append("√").append(dBase);
        return result.toString();*/
        if (!nExponent.equals(Fraction.ONE)) result.append(nCoefficient).append("√").append(nBase);
        else result.append(nBase);
        if (!dExponent.equals(Fraction.ONE)) result.append("/").append(dCoefficient).append("√").append(dBase);
        else result.append("/").append(dBase);
        return result.toString();
    }
}
