import java.util.*;

public class Test {
    public static void main(String[] args) {
        Real r1 = new Real("2^");
        Real r2 = new Real("8^");
        Fraction f1 = new Fraction("1/5");
        System.out.println(r1.divide(r2));
    }
}
