import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Scanner;

public class Console {
    //线性代数计算器的综合启动器，整合所有功能，支持多参数单行输入，极大提升效率
    static ResourceBundle text = ResourceBundle.getBundle("Lang_zh", Locale.CHINA);

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        System.out.println("""
                -zh   切换至中文（默认） change to Chinese (default)
                -en   切换至英语 change to English
                -help 获取帮助   get help
                -exit 退出程序   exit program
                """);
        while (true) {
            String inputs = input.nextLine().strip();
            if (inputs.contains("-exit")) System.exit(0);
            else if (inputs.contains("-en")) text = ResourceBundle.getBundle("Lang_en");
            else if (inputs.contains("-zh")) text = ResourceBundle.getBundle("Lang_zh", Locale.CHINA);
            if (inputs.contains("-help")) showHelp();
            if (inputs.contains("-m")) mat(inputs.split("-m")[1].strip());
        }
    }

    private static void mat(String input) {

    }

    //展示帮助
    private static void showHelp() {
        System.out.println(text.getString("help"));
    }
}
