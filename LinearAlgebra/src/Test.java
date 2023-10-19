public class Test {
    public static void main(String[] args) {
        double d1 = 0.1;
        double d2 =0.2;
        System.out.println(d1+d2);
        Fraction f1 = new Fraction("0.1");
        Fraction f2 = new Fraction("0.2");
        System.out.println(f1.add(f2));
        Real r1 = new Real("8^");
        System.out.println(r1);
    }
}
