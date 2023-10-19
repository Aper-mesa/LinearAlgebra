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

2的11次方是2048
*/

public class Console {
    ////线性代数计算器的综合启动器，整合所有功能，支持多参数单行输入，极大提升效率
    static ResourceBundle text = ResourceBundle.getBundle("Lang_zh", Locale.CHINA);

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        System.out.println("\u001B[31m" + "           水银线代计算器\nApermesa's Linear Algebra Calculator" + "\u001B[0m");
        System.out.println("""
                输入 “help” 获取帮助
                    Enter "help" to get help
                输入 “exit” 退出程序
                    Enter "exit" to exit the program""");
        while (true) {
            System.out.print("> ");
            String inputs = input.nextLine().strip().toLowerCase();
            if (inputs.equals("exit") || inputs.equals("shutdown") ||
                    inputs.contains("stop") || inputs.contains("爬") || inputs.contains("滚")) {
                System.out.println("\u001B[38;2;245;144;33m" + "HλLF-LIFE 3 CONFIRMED");
                System.exit(0);
            } else if (inputs.contains("en") || inputs.contains("zh") || inputs.contains("help")) {
                if (inputs.contains("en")) {
                    text = ResourceBundle.getBundle("Lang_en", Locale.ENGLISH);
                    System.out.println("Language changed to English");
                } else if (inputs.contains("zh")) {
                    text = ResourceBundle.getBundle("Lang_zh", Locale.CHINA);
                    System.out.println("语言切换至中文");
                }
                if (inputs.contains("help") || inputs.contains("?") || inputs.contains("？")) {
                    System.out.println("\u001B[36m" + text.getString("help") + "\u001B[0m");
                    System.out.println("\u001B[33m" + text.getString("example") + "\u001B[0m");
                }
                continue;
            }
            //矩阵
            if (inputs.startsWith("-m")) mat(inputs.split("-m")[1].strip());
                //线性方程组
            else if (inputs.startsWith("-e"))
                LinearEquation.compute(Integer.parseInt(inputs.split("-e")[1].strip()));
                //行列式
            else if (inputs.startsWith("-d"))
                System.out.println(Det.getValue(Integer.parseInt(inputs.split("-d")[1].strip())));
                //向量
            else if (inputs.startsWith("-v")) vec(inputs.split("-v")[1].strip());
                //彩蛋，2048游戏
            else if (inputs.equals("2048")) Game2048.play();
            else System.out.println(text.getString("invalidCommand"));
        }
    }

    ///矩阵命令处理
    private static void mat(String input) {
        //para即为用户输入的指令，包含运算类型和矩阵的信息
        String[] para = input.split(" ");
        //此数组仅存储矩阵信息
        Real[] info = new Real[para.length - 1];
        for (int i = 1; i < para.length; i++)
            info[i - 1] = new Real(para[i]);
        Real[][] mat = null;
        int rank = 0;
        ArrayList<Real> eigenValue = null;
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
        }
        if (mat == null && eigenValue == null) System.out.println(rank);
        else if (mat == null) System.out.println(eigenValue);
        else Tool.print(mat);
    }

    ///向量命令处理
    private static void vec(String input) {
        //para即为用户输入的指令，包含运算类型和矩阵的信息
        String[] para = input.split(" ");
        //此数组仅存储矩阵信息
        Real[] info = new Real[para.length - 1];
        for (int i = 1; i < para.length; i++)
            info[i - 1] = new Real(para[i]);
        Real[] vec = null;
        Real result = null;
        switch (para[0]) {
            case "a" -> vec = Vec.add(1, info);
            case "s" -> vec = Vec.add(-1, info);
            case "c" -> vec = Vec.scalarMultiply(info);
            case "i" -> result = Vec.innerProduct(info);
            case "o2" -> result = Vec.outerProduct2D();
            case "o3" -> vec = Vec.outerProduct3D();
            case "l" -> result = Vec.length(info);
            case "as" -> result = Vec.angleSin(info);
            case "ac" -> result = Vec.angleCos(info);
            case "t" -> result = Vec.mixedProduct();
        }
        if (vec == null) System.out.println(result);
        else System.out.println(Arrays.toString(vec));
    }
}
