import java.math.BigDecimal;

public class Test {
    public static void main(String[] args) {
        Fraction f1 = new Fraction("0.666");
        Fraction f2 = new Fraction("0.333");
        System.out.println(f1.divide(f2));
    }
}
