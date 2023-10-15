import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Scanner;

public class Console {
    ////线性代数计算器的综合启动器，整合所有功能，支持多参数单行输入，极大提升效率
    static ResourceBundle text = ResourceBundle.getBundle("Lang_zh", Locale.CHINA);

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        System.out.println("""
                -zh   切换至中文    change to Chinese (default)
                -en   切换至英语    change to English
                -help 获取帮助      get help
                -exit 退出程序      exit program
                """);
        while (true) {
            String inputs = input.nextLine().strip();
            if (inputs.contains("-exit")) System.exit(0);
            else if (inputs.contains("-en")) text = ResourceBundle.getBundle("Lang_en", Locale.ENGLISH);
            else if (inputs.contains("-zh")) text = ResourceBundle.getBundle("Lang_zh", Locale.CHINA);
            if (inputs.contains("-help")) showHelp();
            //矩阵
            if (inputs.contains("-m")) mat(inputs.split("-m")[1].strip());
                //线性方程组
            else if (!inputs.equals("-en") && inputs.contains("-e"))
                LinearEquation.compute(Integer.parseInt(inputs.split("-e")[1].strip()));
                //行列式
            else if (inputs.contains("-d"))
                System.out.println(Det.getValue(Integer.parseInt(inputs.split("-d")[1].strip())));
                //向量"
            else if (inputs.contains("-v")) vec(inputs.split("-v")[1].strip());
        }
    }

    ///矩阵命令处理
    private static void mat(String input) {
        //para即为用户输入的指令，包含运算类型和矩阵的信息
        String[] para = input.split(" ");
        //此数组仅存储矩阵信息
        Real[] info = new Real[para.length - 1];
        for (int i = 1; i < para.length; i++) {
            info[i - 1] = new Real(para[i]);
        }
        Real[][] mat = null;
        int rank = 0;
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
        }
        if (mat == null) System.out.println(rank);
        else Tool.print(mat);
    }

    ///向量命令处理
    private static void vec(String input) {

    }

    ///展示帮助
    private static void showHelp() {
        System.out.println(text.getString("帮助"));
    }
}
