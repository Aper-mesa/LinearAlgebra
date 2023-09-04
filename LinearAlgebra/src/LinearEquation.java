import java.util.*;

//此类用于解线性方程组
//让用户输入增广矩阵。获得系数矩阵。
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
        System.out.println("按行输入增广矩阵，元素间用一个空格隔开，输入0结束");
        ArrayList<String> equations = new ArrayList<>();
        while (true) {
            String nextLine = input.nextLine();
            if (nextLine.equals("0")) break;
            equations.add(nextLine);
            numberOfEquations++;
        }
        Fraction[][] augmentedMatrix = new Fraction[numberOfEquations][numberOfUnknowns + 1];
        Fraction[][] coefficientMatrix = new Fraction[numberOfEquations][numberOfUnknowns];
        for (int i = 0; i < augmentedMatrix.length; i++)
            for (int j = 0; j < augmentedMatrix[0].length; j++)
                augmentedMatrix[i][j] = new Fraction(equations.get(i).split(" ")[j]);
        System.out.println("增广矩阵：");
        for (Fraction[] row : augmentedMatrix) System.out.println(Arrays.toString(row));
        for (int i = 0; i < augmentedMatrix.length; i++) {
            coefficientMatrix[i] = Arrays.copyOf(augmentedMatrix[i], numberOfUnknowns);
        }
        int augmentedRank = (int) Mat.getRank(augmentedMatrix)[0];
        int coefficientRank = (int) Mat.getRank(coefficientMatrix)[0];
        if (augmentedRank != coefficientRank) {
            System.out.println("方程组无解");
            System.exit(0);
        }
        if (coefficientRank == numberOfEquations) System.out.println("方程组有唯一解");
        Fraction[][] echelonMatrix = (Fraction[][]) Mat.getRank(augmentedMatrix)[1];
        System.out.println("增广阶梯矩阵：");
        for (Fraction[] row : echelonMatrix) System.out.println(Arrays.toString(row));
    }
}
