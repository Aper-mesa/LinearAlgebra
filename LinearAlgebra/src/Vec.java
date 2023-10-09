import java.util.*;

////此程序用于向量的相关计算
/*
1. 加减法
2. 数乘
3. 内积
4. 外积
5. 模长
6. 夹角
7. 混合积
 */
public class Vec {
    // 向量维数，自初始化起不可改变
    public final int dimension;
    public Real[] data;

    // 传入
    public Vec(int dimension, Real[] data) {
        this.dimension = dimension;
        this.data = data;
    }

    // 向量加
    public static Vec add(Vec a, Vec b) {
        // 判断向量维数是否相同
        if (a.dimension != b.dimension) throw new IllegalArgumentException("向量维数不同");
        Real[] data = new Real[a.dimension];
        for (int i = 0; i < a.dimension; i++) {
            data[i] = a.data[i].add(b.data[i]);
        }
        return new Vec(a.dimension, data);
    }

    public Vec add(Vec b) {
        return add(this, b);
    }

    // 向量减
    public static Vec subtract(Vec a, Vec b) {
        // 判断向量维数是否相同
        if (a.dimension != b.dimension) throw new IllegalArgumentException("向量维数不同");
        Real[] data = new Real[a.dimension];
        for (int i = 0; i < a.dimension; i++) {
            data[i] = a.data[i].subtract(b.data[i]);
        }
        return new Vec(a.dimension, data);
    }

    public Vec subtract(Vec b) {
        return subtract(this, b);
    }

    // 数乘
    public static Vec multiply(Real a, Vec b) {
        Real[] data = new Real[b.dimension];
        for (int i = 0; i < b.dimension; i++) {
            data[i] = a.multiply(b.data[i]);
        }
        return new Vec(b.dimension, data);
    }

    public Vec multiply(Real a) {
        return multiply(a, this);
    }

    // 内积
    public static Real dot(Vec a, Vec b) {
        // 判断向量维数是否相同
        if (a.dimension != b.dimension) throw new IllegalArgumentException("向量维数不同");
        Real result = Real.ZERO;
        for (int i = 0; i < a.dimension; i++) {
            result = result.add(a.data[i].multiply(b.data[i]));
        }
        return result;
    }

    public Real dot(Vec b) {
        return dot(this, b);
    }

    // 外积
    public static Vec cross(Vec a, Vec b) {
        // 判断向量维数是否相同
        if (a.dimension != b.dimension) throw new IllegalArgumentException("向量维数不同");
        if (a.dimension != 3) throw new IllegalArgumentException("外积只能用于三维向量");
        Real[] data = new Real[3];
        data[0] = a.data[1].multiply(b.data[2]).subtract(a.data[2].multiply(b.data[1]));
        data[1] = a.data[2].multiply(b.data[0]).subtract(a.data[0].multiply(b.data[2]));
        data[2] = a.data[0].multiply(b.data[1]).subtract(a.data[1].multiply(b.data[0]));
        return new Vec(3, data);
    }

    public Vec cross(Vec b) {
        return cross(this, b);
    }

    // 模长
    public static Real length(Vec a) {
        Real result = Real.ZERO;
        for (int i = 0; i < a.dimension; i++) {
            result = result.add(a.data[i].multiply(a.data[i]));
        }
        // TODO: 临时用法
        return result.sqrt();
    }

    public Real length() {
        return length(this);
    }

    // 夹角
    public static Real angle(Vec a, Vec b) {
        // 判断向量维数是否相同
        if (a.dimension != b.dimension) throw new IllegalArgumentException("向量维数不同");
        return dot(a, b).divide(length(a).multiply(length(b)));
    }

    public Real angle(Vec b) {
        return angle(this, b);
    }

    // 混合积
    public static Real mixed(Vec a, Vec b, Vec c) {
        // 判断向量维数是否相同
        if (a.dimension != b.dimension || a.dimension != c.dimension)
            throw new IllegalArgumentException("向量维数不同");
        return dot(a, cross(b, c));
    }

    public Real mixed(Vec b, Vec c) {
        return mixed(this, b, c);
    }

    // 打印向量信息
    public String toString() {
        return """
                向量维数： %d
                向量数据： %s
                """.formatted(dimension, _out(data));
    }

    protected static String _out(Real[] data) {
        StringBuilder result = new StringBuilder();
        for (Real datum : data) {
            result.append(datum).append(" ");
        }
        return result.toString();
    }

    // 复制向量
    public Vec copy() {
        return new Vec(dimension, data);
    }

    // 获得向量维数
    public int getDimension() {
        return dimension;
    }

    // 获得向量数据
    public Real[] getData() {
        return data;
    }

    // 控制台
    public static void main(Real[] src) {
        Dictionary<String, Vec> var = new Hashtable<>();
        Con console = new Con();
        Scanner scanner = new Scanner(System.in);
        Con.help();
        System.out.print(">>> ");
        while (console.deal(scanner.nextLine().replace(" ", ""), var)) {
            System.out.print(">>> ");
        }
    }
}

// 控制台相关逻辑
class Con {
    public static void help() {
        System.out.println("""
                ↓ 优先级 高->低 ↓ 不能嵌套
                help: 查看帮助
                exit: 退出
                C: 清空变量及输出
                =: 赋值 (变量名=[0,0,0])
                +-: 加减
                ^: 外积
                *: 数乘
                .: 内积
                |: 模长
                Angle(变量名): 夹角
                Mix(变量名): 混合积
                """);
    }

    public boolean deal(String src, Dictionary<String, Vec> var) {
        if (Objects.equals(src, "exit")) return false;
        if (Objects.equals(src, "C")) {
            System.out.flush();
            var = new Hashtable<>();
        }
        if (Objects.equals(src, "帮助")) {
            help();
        }
        return analyze(src, var);
    }

    private boolean analyze(String src, Dictionary<String, Vec> var) {
        try {
            if (src.contains("=")) {
                if (src.contains("+") || src.contains("-") || src.contains("*") || src.contains("^")) {
                    String r = src.substring(src.indexOf("=") + 1);
                    if (src.contains("*")) {
                        Real a = new Real(r.substring(0, r.indexOf("*")));
                        String b = r.substring(r.indexOf("*") + 1);
                        if (var.get(b) == null) throw new IllegalArgumentException("变量不存在");
                        Vec result = Vec.multiply(a, var.get(b));
                        var.put(src.substring(0, src.indexOf("=")), result);
                        System.out.println(src.substring(0, src.indexOf("=")) + " = " + result);
                    }
                    if (src.contains("+")) {
                        String a = r.substring(0, r.indexOf("+"));
                        String b = r.substring(r.indexOf("+") + 1);
                        if (var.get(a) == null || var.get(b) == null) throw new IllegalArgumentException("变量不存在");
                        Vec result = Vec.add(var.get(a), var.get(b));
                        var.put(src.substring(0, src.indexOf("=")), result);
                        System.out.println(src.substring(0, src.indexOf("=")) + " = " + result);
                    }
                    if (src.contains("-")) {
                        String a = r.substring(0, r.indexOf("-"));
                        String b = r.substring(r.indexOf("-") + 1);
                        if (var.get(a) == null || var.get(b) == null) throw new IllegalArgumentException("变量不存在");
                        Vec result = Vec.subtract(var.get(a), var.get(b));
                        var.put(src.substring(0, src.indexOf("=")), result);
                        System.out.println(src.substring(0, src.indexOf("=")) + " = " + result);
                    }
                    if (src.contains("^")) {
                        String a = r.substring(0, r.indexOf("^"));
                        String b = r.substring(r.indexOf("^") + 1);
                        if (var.get(a) == null || var.get(b) == null) throw new IllegalArgumentException("变量不存在");
                        Real result = Vec.angle(var.get(a), var.get(b));
                        System.out.println(src.substring(0, src.indexOf("=")) + " = " + result);
                    }
                    return true;
                }
                String[] data = src.split("=");
                String name = data[0];
                String[] value = data[1].substring(1, data[1].length() - 1).split(",");
                Real[] result = new Real[value.length];
                for (int i = 0; i < value.length; i++) {
                    result[i] = new Real(value[i]);
                }
                var.put(name, new Vec(result.length, result));
                System.out.println(name + " = " + var.get(name));
            } else {
                if (!(src.contains("+") || src.contains("-") || src.contains("*") || src.contains("^") || src.contains(".") || src.contains("|") || src.contains("Angle") || src.contains("Mix"))) {
                    System.out.println(var.get(src));
                } else {
                    if (src.contains("*")) {
                        Real a = new Real(src.substring(0, src.indexOf("*")));
                        String b = src.substring(src.indexOf("*") + 1);
                        if (var.get(b) == null) throw new IllegalArgumentException("变量不存在");
                        Vec result = Vec.multiply(a, var.get(b));
                        System.out.println(result);
                    }
                    if (src.contains("+")) {
                        String a = src.substring(0, src.indexOf("+"));
                        String b = src.substring(src.indexOf("+") + 1);
                        if (var.get(a) == null || var.get(b) == null) throw new IllegalArgumentException("变量不存在");
                        Vec result = Vec.add(var.get(a), var.get(b));
                        System.out.println(result);
                    }
                    if (src.contains("-")) {
                        String a = src.substring(0, src.indexOf("-"));
                        String b = src.substring(src.indexOf("-") + 1);
                        if (var.get(a) == null || var.get(b) == null) throw new IllegalArgumentException("变量不存在");
                        Vec result = Vec.subtract(var.get(a), var.get(b));
                        System.out.println(result);
                    }
                    if (src.contains("^")) {
                        String a = src.substring(0, src.indexOf("^"));
                        String b = src.substring(src.indexOf("^") + 1);
                        if (var.get(a) == null || var.get(b) == null) throw new IllegalArgumentException("变量不存在");
                        Real result = Vec.angle(var.get(a), var.get(b));
                        System.out.println(result);
                    }
                    if (src.contains(".")) {
                        String a = src.substring(0, src.indexOf("."));
                        String b = src.substring(src.indexOf(".") + 1);
                        if (var.get(a) == null || var.get(b) == null) throw new IllegalArgumentException("变量不存在");
                        Real result = Vec.dot(var.get(a), var.get(b));
                        System.out.println(result);
                    }
                    if (src.contains("|")) {
                        String a = src.substring(1);
                        if (var.get(a) == null) throw new IllegalArgumentException("变量不存在");
                        Real result = Vec.length(var.get(a));
                        System.out.println(result);
                    }
                    if (src.contains("Angle")) {
                        String a = src.substring(src.indexOf("Angle(") + 6, src.indexOf(","));
                        String b = src.substring(src.indexOf(",") + 1, src.indexOf(")"));
                        if (var.get(a) == null || var.get(b) == null) throw new IllegalArgumentException("变量不存在");
                        Real result = Vec.angle(var.get(a), var.get(b));
                        System.out.println(result);
                    }
                    if (src.contains("Mix")) {
                        String[] s = src.split(",");
                        String a = s[0].substring(s[0].indexOf("(") + 1);
                        String b = s[1];
                        String c = s[2].substring(0, s[2].indexOf(")"));
                        if (var.get(a) == null || var.get(b) == null || var.get(c) == null)
                            throw new IllegalArgumentException("变量不存在");
                        Real result = Vec.mixed(var.get(a), var.get(b), var.get(c));
                        System.out.println(result);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return true;
    }

}