import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
//2023.09.04此类已废弃，我花了4小时写出了判断线性方程组格式的代码和将方程组转换为增广矩阵的代码，
//结果胡小程一句话让我绷不住了：都已经是标准格式了不直接输入增广矩阵就可以了吗
//所以这里的代码都没有必要了，用户直接输入增广矩阵就行。

//此类用于解线性方程组
//先使用正则表达式判断用户输入的内容是否是标准格式，接着获取每一个未知量前的参数，得到系数矩阵。接着得到增广矩阵。
//分别求系数矩阵和增广矩阵的逆，不同则方程组无解。接着看系数矩阵是否满秩，满秩则有唯一解，降秩则有无穷解。
//用阶梯化的增广矩阵继续求解。若有唯一解，则从下往上依次迭代得出所有解。
//若有无穷解，则找出自由未知量，将其移至等式右侧，并用自由未知量一次表示出剩余未知量。
public class LinearEquation {
    private static final Scanner input = new Scanner(System.in);
    private static int numberOfUnknowns;
    private static int numberOfEquations = 0;

    public static void main(String[] args) {
        System.out.println("输入线性方程组元的个数");
        numberOfUnknowns = Integer.parseInt(input.nextLine());
        System.out.println("按行输入每一个方程的标准格式，输入0结束");
        ArrayList<String> equations = new ArrayList<>();
        while (true) {
            String nextLine = input.nextLine();
            if (nextLine.equals("0")) break;
            if (!check(nextLine)) {
                System.out.println("格式不正确，重新输入此行");
                continue;
            }
            equations.add(nextLine);
            numberOfEquations++;
        }
        Fraction[][] augmentedMatrix = new Fraction[numberOfEquations][numberOfUnknowns + 1];
        //先把增广矩阵填满0，再用方程组去修改
        for (Fraction[] row : augmentedMatrix) Arrays.fill(row, Fraction.ZERO);
        //临时字符串数组存储每一个项，为提取系数和常数做准备
        String[] terms;
        for (int i = 0; i < equations.size(); i++) {
            terms = equations.get(i).split("=")[0].split("(?<=\\d)(?=[+-])");
            for (String term : terms) {
                if(term.matches("-(?i)x.+"))
                    augmentedMatrix[i][Integer.parseInt(term.split("(?i)x")[1]) - 1] = Fraction.ONE.negate();
                else if (term.matches("[+-]?(\\d+([./]\\d+)*)(?i)x.+")) {
                    augmentedMatrix[i][Integer.parseInt(term.split("(?i)x")[1]) - 1] = new Fraction(term.split("(?i)x")[0]);
                } else augmentedMatrix[i][Integer.parseInt(term.split("(?i)x")[1]) - 1] = Fraction.ONE;
            }
            augmentedMatrix[i][numberOfUnknowns] = new Fraction(equations.get(i).split("=")[1]);
        }
        Fraction[][] coefficientMatrix = new Fraction[numberOfEquations][numberOfUnknowns];
        for (int i = 0; i < coefficientMatrix.length; i++) coefficientMatrix[i] = Arrays.copyOf(augmentedMatrix[i], numberOfUnknowns);
        System.out.println("系数矩阵：");
        for (Fraction[] row : coefficientMatrix) {
            System.out.println(Arrays.toString(row));
        }
        System.out.println("增广矩阵：");
        for (Fraction[] row : augmentedMatrix) {
            System.out.println(Arrays.toString(row));
        }
    }

    ///检查用户输入的方程是否符合格式
    private static boolean check(String equation) {
        return equation.matches("-?(\\d+([./]\\d+)*)?(?i)x\\d+([+-](-?\\d+([./]\\d+)*)?(?i)x\\d+)*=-?\\d+([./]\\d+)?");
    }
}
