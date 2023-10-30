import java.util.*;

/*默认颜色：
Reset: "\u001B[0m"
前景色（文本颜色）：

黑色："\u001B[30m"
红色："\u001B[31m"
绿色："\u001B[32m"
黄色："\u001B[33m"
蓝色："\u001B[34m"
洋红色（品红）："\u001B[35m"
青色："\u001B[36m"
白色："\u001B[37m"
背景色：

黑色："\u001B[40m"
红色："\u001B[41m"
绿色："\u001B[42m"
黄色："\u001B[43m"
蓝色："\u001B[44m"
洋红色（品红）："\u001B[45m"
青色："\u001B[46m"
白色："\u001B[47m"
*/

public class Console {
    ////线性代数计算器的综合启动器，整合所有功能，支持多参数单行输入，极大提升效率
    static ResourceBundle text = ResourceBundle.getBundle("Lang_zh", Locale.CHINA);

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        System.out.println("\u001B[31m" + "           工水线代计算器\nApermesa's Linear Algebra Calculator" + "\u001B[0m");
        System.out.println("""
                输入 “help” 获取帮助
                    Enter "help" to get help
                输入 “exit” 退出程序
                    Enter "exit" to exit the program""");
        while (true) {
            System.out.print("\u001B[34m" + "> " + "\u001B[0m");
            String inputs = input.nextLine().strip().toLowerCase().replaceAll("\\s+", " ");
            if (inputs.equals("exit") || inputs.equals("shutdown") ||
                    inputs.equals("stop") || inputs.equals("爬") || inputs.equals("滚")) {
                System.out.println("\u001B[38;2;245;144;33m" + "HλLF-LIFE 3 CONFIRMED");
                System.exit(0);
            } else if (inputs.equals("en") || inputs.equals("zh") || inputs.equals("help")
                    || inputs.equals("?") || inputs.equals("？") || inputs.equals("cn")) {
                if (inputs.equals("en")) {
                    text = ResourceBundle.getBundle("Lang_en", Locale.ENGLISH);
                    System.out.println("Language changed to English");
                } else if (inputs.equals("zh") || inputs.equals("cn")) {
                    text = ResourceBundle.getBundle("Lang_zh", Locale.CHINA);
                    System.out.println("语言切换至中文");
                }
                if (inputs.equals("help") || inputs.equals("?") || inputs.equals("？")) {
                    System.out.println("\u001B[36m" + text.getString("help") + "\u001B[0m");
                    System.out.println("\u001B[33m" + text.getString("example") + "\u001B[0m");
                }
                continue;
            }
            //矩阵
            if (inputs.startsWith("m")) mat(inputs.split("m")[1].strip());
                //线性方程组
            else if (inputs.startsWith("e")) {
                //判断参数格式，不对直接返回
                if (inputs.strip().equals("e") || notNumber(inputs.split("e")[1].strip())) {
                    System.out.println(text.getString("invalidCommand"));
                    continue;
                }
                LinearEquation.compute(Integer.parseInt(inputs.split("e")[1].strip()));
            }
            //行列式
            else if (inputs.startsWith("d")) {
                //判断参数格式，不对直接返回
                if (inputs.strip().equals("d") || notNumber(inputs.split("d")[1].strip())) {
                    System.out.println(text.getString("invalidCommand"));
                    continue;
                }
                System.out.println(Det.getValue(Integer.parseInt(inputs.split("d")[1].strip())));
            }
            //向量
            else if (inputs.startsWith("v")) vec(inputs.split("v")[1].strip());
                //彩蛋，2048游戏
            else if (inputs.equals("2048")) Game2048.play();
            else System.out.println(text.getString("invalidCommand"));
        }
    }

    ///矩阵命令处理
    private static void mat(String input) {
        //para即为用户输入的指令，包含运算类型和矩阵的信息
        String[] para = input.strip().replaceAll("\\s+", " ").split(" ");
        //判断用户输入的参数有没有非数字；有的话直接返回
        for (int i = 1; i < para.length; i++)
            if (notNumber(para[i])) {
                System.out.println(text.getString("invalidCommand"));
                return;
            }
        //此数组仅存储矩阵信息
        Real[] info = new Real[para.length - 1];
        for (int i = 1; i < para.length; i++)
            info[i - 1] = new Real(para[i]);
        Real[][] mat = null;
        int rank = -1;
        ArrayList<Real> eigenValue = null;
        //判断用户输入的参数是否符合格式；不符合直接返回
        if ((para[0].equals("i") || para[0].equals("g")) && badOneArg(info)) {
            System.out.println(text.getString("invalidCommand"));
            return;
        } else if ((para[0].equals("a") || para[0].equals("s") || para[0].equals("p") || para[0].equals("t") ||
                para[0].equals("r") | para[0].equals("e")) && !twoInt(info)) {
            System.out.println(text.getString("invalidCommand"));
            return;
        } else if (para[0].equals("c") && !threeArg(info)) {
            System.out.println(text.getString("invalidCommand"));
            return;
        } else if (para[0].equals("m") && !fourArg(info)) {
            System.out.println(text.getString("invalidCommand"));
            return;
        }
        switch (para[0]) {
            case "a" -> mat = Mat.add(1, info);
            case "s" -> mat = Mat.add(-1, info);
            case "m" -> mat = Mat.multiply(info);
            case "p" -> mat = Mat.power(info);
            case "c" -> mat = Mat.scalarMultiply(info);
            case "t" -> mat = Mat.transpose(info);
            case "r" -> rank = Mat.getRank(info);
            case "i" -> mat = Mat.inverse(info);
            case "e" -> mat = Mat.getEchelon(info);
            case "g" -> eigenValue = Mat.eigenvalue(info);
            default -> System.out.println(text.getString("invalidCommand"));
        }
        if (mat != null || eigenValue != null || rank != -1) {
            if (mat == null && eigenValue == null) System.out.println(rank);
            else if (mat == null) System.out.println(eigenValue);
            else Tool.print(mat);
        }
    }

    ///向量命令处理
    private static void vec(String input) {
        //para即为用户输入的指令，包含运算类型和矩阵的信息
        String[] para = input.split(" ");
        //判断用户输入的参数有没有非数字；有的话直接返回
        for (int i = 1; i < para.length; i++)
            if (notNumber(para[i])) {
                System.out.println(text.getString("invalidCommand"));
                return;
            }
        //此数组仅存储矩阵信息
        Real[] info = new Real[para.length - 1];
        for (int i = 1; i < para.length; i++)
            info[i - 1] = new Real(para[i]);
        Real[] vec = null;
        Real result = null;
        //判断用户输入的参数是否符合格式；不符合直接返回
        if ((para[0].equals("a") || para[0].equals("s") || para[0].equals("i") ||
                para[0].equals("e") || para[0].equals("as") || para[0].equals("ac")) && badOneArg(info)) {
            System.out.println(text.getString("invalidCommand"));
            return;
        } else if (para[0].equals("c") && !realAndInt(info)) {
            System.out.println(text.getString("invalidCommand"));
            return;
        } else if ((para[0].equals("o2") || para[0].equals("o3") || para[0].equals("t")) && !noArg(info)) {
            System.out.println(text.getString("invalidCommand"));
            return;
        }
        switch (para[0]) {
            case "a" -> vec = Vec.add(1, info);
            case "s" -> vec = Vec.add(-1, info);
            case "c" -> vec = Vec.scalarMultiply(info);
            case "i" -> result = Vec.innerProduct(info);
            case "o2" -> result = Vec.outerProduct2D();
            case "o3" -> vec = Vec.outerProduct3D();
            case "e" -> result = Vec.length(info);
            case "as" -> result = Vec.angleSin(info);
            case "ac" -> result = Vec.angleCos(info);
            case "t" -> result = Vec.mixedProduct();
        }
        if (vec == null) System.out.println(result);
        else System.out.println(Arrays.toString(vec));
    }

    ///判断输入的字符串是否符合实数格式
    protected static boolean notNumber(String input) {
        return !input.matches("-?\\d+(.\\d+)?\\^?(/-?\\d+(.\\d+)?\\^?)?");
    }

    ///无参数识别
    private static boolean noArg(Real[] info) {
        return info.length == 0;
    }

    ///单参数识别
    private static boolean badOneArg(Real[] info) {
        return info.length != 1 || info[0].notInteger();
    }

    ///双参数识别：两个整数
    private static boolean twoInt(Real[] info) {
        return info.length == 2 && !info[0].notInteger() && !info[1].notInteger();
    }

    ///双参数识别：一个实数一个整数
    private static boolean realAndInt(Real[] info) {
        return info.length == 2 && !info[1].notInteger();
    }

    ///三参数识别
    private static boolean threeArg(Real[] info) {
        return info.length == 3 && !info[1].notInteger() && !info[2].notInteger();
    }

    ///四参数识别
    private static boolean fourArg(Real[] info) {
        return info.length == 4 && !info[0].notInteger()
                && !info[1].notInteger() && !info[2].notInteger() && !info[3].notInteger();
    }
}
