import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Scanner;

public class Test {
    public static void main(String[] args) {
        System.out.println(factorial(7));
    }

    private static int factorial(int n) {
        return n == 1 ? 1 : n * factorial(n - 1);
    }
}
