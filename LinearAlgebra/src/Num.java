//数域扩展到复数范围，现已支持负数的平方根计算
public class Num {
    //实部
    public Real real = Real.ZERO;
    //虚部，默认为0
    public Real imaginary = Real.ZERO;

    ///输入复数的构造
    public Num(Num num) {
        real = num.real;
        imaginary = num.imaginary;
    }

    ///分别输入实部和虚部的构造方法
    public Num(Real real, Real imaginary) {
        this.real = real;
        this.imaginary = imaginary;
    }

    ///输入字符串的构造方法
    //FIXME 不支持在根号中输入负数
    public Num(String inputStr) {
        //若字符串不包含i则说明虚部为0，直接使用实数构造方法即可
        if (!inputStr.contains("i")) {
            real = new Real(inputStr);
            return;
        }
        //开头有负号
        if (inputStr.startsWith("-")) {
            //把开头负号去掉还有负号，说明虚部为负，实部也为负
            if (inputStr.substring(1).contains("-")) {
                real = new Real(inputStr.split("-")[1]).negate();
                imaginary = new Real(inputStr.split("-")[2].split("i")[0]).negate();
            }
            //里面还有个正号，说明实部为负，虚部为正
            else if (inputStr.contains("+")) {
                real = new Real(inputStr.split("\\+")[0]);
                imaginary = new Real(inputStr.split("\\+")[1].split("i")[0]);
            }
            //剩下的情况就只有纯负虚数了
            else {
                imaginary = new Real(inputStr.split("i")[0]);
            }
        }
        //开头不是负号
        else {
            //如果开头不是负号但是有负号，说明实部为正，虚部为负
            if (inputStr.contains("-")) {
                real = new Real(inputStr.split("-")[0]);
                imaginary = new Real(inputStr.split("-")[1].split("i")[0]).negate();
            }
            //里面还有个正号，说明实部和虚部都是正数
            else if (inputStr.contains("+")) {
                real = new Real(inputStr.split("\\+")[0]);
                imaginary = new Real(inputStr.split("\\+")[1].split("i")[0]);
            }
            //剩下的情况为正纯虚数
            else {
                imaginary = new Real(inputStr.split("i")[0]);
            }
        }
    }

    ///加法
    public Num add(Num num) {
        Num addend = new Num(this);
        Num augend = new Num(num);
        addend.real = addend.real.add(augend.real);
        addend.imaginary = addend.imaginary.add(augend.imaginary);
        return addend;
    }

    ///减法
    public Num subtract(Num num) {
        return add(new Num(num).negate());
    }

    ///乘法：(a+bi)*(c+di)=(ac-bd)i+(ad+bc)i
    public Num multiply(Num num) {
        Num result = new Num(this);
        Num multiplicand = new Num(this);
        Num multiplier = new Num(num);
        result.real = multiplicand.real.multiply(multiplier.real).subtract(multiplicand.imaginary.multiply(multiplier.imaginary));
        Real ad = multiplicand.real.multiply(multiplier.imaginary);
        Real bc = multiplicand.imaginary.multiply(multiplier.real);
        result.imaginary = ad.add(bc);
        return result;
    }

    ///变为相反数
    public Num negate() {
        return new Num(real.negate(), imaginary.negate());
    }

    @Override
    public String toString() {
        return imaginary.equals(Real.ZERO) ? real.toString() : (real.equals(Real.ZERO) ? "" : real) + (imaginary.isNegative() ? "" : (real.equals(Real.ZERO) ? "" : "+")) + imaginary + "i";
    }
}
