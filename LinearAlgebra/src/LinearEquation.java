import java.util.*;

//此类用于解线性方程组
//让用户输入增广矩阵。获得系数矩阵。
//分别求系数矩阵和增广矩阵的逆，不同则方程组无解。接着看系数矩阵是否满秩，满秩则有唯一解，降秩则有无穷解。
//用阶梯化的增广矩阵继续求解。若有唯一解，则从下往上依次迭代得出所有解。
//若有无穷解，则找出自由未知量，将其移至等式右侧，并用自由未知量依次表示出剩余未知量。
public class LinearEquation {
    private static final Scanner input = new Scanner(System.in);
    private static int numberOfVariables;
    private static int numberOfEquations = 0;

    public static void main(String[] args) {
        compute();
    }

    ///求解的初始步骤：输入和判断等
    private static void compute() {
        System.out.println("输入线性方程组元的个数，输入0退出");
        numberOfVariables = Integer.parseInt(input.nextLine());
        if (numberOfVariables == 0) System.exit(0);
        System.out.println("按行输入增广矩阵，元素间用一个空格隔开，输入0结束");
        ArrayList<String> equations = new ArrayList<>();
        while (true) {
            String nextLine = input.nextLine();
            if (nextLine.equals("0")) break;
            equations.add(nextLine);
            numberOfEquations++;
        }
        Fraction[][] augmentedMatrix = new Fraction[numberOfEquations][numberOfVariables + 1];
        Fraction[][] coefficientMatrix;
        for (int i = 0; i < augmentedMatrix.length; i++)
            for (int j = 0; j < augmentedMatrix[0].length; j++)
                augmentedMatrix[i][j] = new Fraction(equations.get(i).split(" ")[j]);
        coefficientMatrix = Tool.deepCopy(augmentedMatrix, numberOfVariables);
        Fraction[][] augmentedEchelon;
        //此循环去掉阶梯增广矩阵的0行
        do {
            augmentedEchelon = (Fraction[][]) Mat.getRank(augmentedMatrix)[1];
            if (Tool.hasZeroRowOrColumn(augmentedEchelon)) {
                augmentedEchelon = Tool.deepCopy(augmentedEchelon, augmentedEchelon.length - 1, augmentedEchelon[0].length);
                numberOfEquations--;
            }
        } while (Tool.hasZeroRowOrColumn(augmentedEchelon));
        int augmentedRank = (int) Mat.getRank(augmentedEchelon)[0];
        int coefficientRank = (int) Mat.getRank(coefficientMatrix)[0];
        if (augmentedRank != coefficientRank) {
            System.out.println("方程组无解\n");
            numberOfEquations = 0;
            main(null);
            return;
        }
        coefficientMatrix = Tool.deepCopy(augmentedMatrix, numberOfVariables);
        coefficientRank = (int) Mat.getRank(coefficientMatrix)[0];
        //判断系数矩阵来确定方的程解的数量
        if (coefficientRank == numberOfVariables) uniqueSolution(augmentedEchelon);
        else infiniteSolution(augmentedEchelon, numberOfVariables - augmentedRank);
    }

    ///求唯一解
    private static void uniqueSolution(Fraction[][] augmentedEchelon) {
        System.out.println("方程组有唯一解\n");
        //与方阵一起参与运算的单位矩阵，操作结束后即为结果
        Fraction[] constantVector = new Fraction[augmentedEchelon.length];
        Fraction[][] coefficientEchelon = Tool.deepCopy(augmentedEchelon, augmentedEchelon[0].length - 1);
        for (int i = 0; i < constantVector.length; i++)
            constantVector[i] = augmentedEchelon[i][augmentedEchelon[0].length - 1];
        //此循环将系数矩阵变为上三角
        for (int dia = 0; dia < coefficientEchelon.length; dia++) {
            Fraction[] tempRow;
            Fraction tempElement;
            //判断对角线是否为0，为0则找下面的第一个非0行进行交换
            if (coefficientEchelon[dia][dia].equals(Fraction.ZERO)) {
                int i;
                for (i = dia + 1; i < coefficientEchelon.length; i++)
                    if (!coefficientEchelon[i][dia].equals(Fraction.ZERO)) break;
                tempRow = coefficientEchelon[dia];
                coefficientEchelon[dia] = coefficientEchelon[i];
                coefficientEchelon[i] = tempRow;
                tempElement = constantVector[dia];
                constantVector[dia] = constantVector[i];
                constantVector[i] = tempElement;
            }
            int i;
            //将对角线数以下的数字变为0
            for (int k = 0; k < coefficientEchelon.length - dia - 1; k++) {
                boolean has = false;
                //找到第一个不是0的数字的行的索引
                for (i = dia + 1; i < coefficientEchelon.length; i++) {
                    if (!coefficientEchelon[i][dia].equals(Fraction.ZERO)) {
                        has = true;
                        break;
                    }
                }
                Tool.addK(constantVector, has, i, dia, coefficientEchelon);
            }
        }
        //接下来将对角线上的数字变为0：从下往上加
        for (int dia = coefficientEchelon.length - 1; dia >= 0; dia--) {
            int i;
            //将对角线数以上的数字变为0
            for (int k = 0; k < dia + 1; k++) {
                boolean has = false;
                //找到对角线数上第一个非零数字
                for (i = dia - 1; i >= 0; i--) {
                    if (!coefficientEchelon[i][dia].equals(Fraction.ZERO)) {
                        has = true;
                        break;
                    }
                }
                Tool.addK(constantVector, has, i, dia, coefficientEchelon);
            }
        }
        //最后将对角线数都化为1
        for (int i = 0; i < coefficientEchelon.length; i++) {
            if (!coefficientEchelon[i][i].equals(Fraction.ONE)) {
                Fraction ratio = Fraction.ONE.divide(coefficientEchelon[i][i]);
                for (int j = 0; j < coefficientEchelon.length; j++)
                    coefficientEchelon[i][j] = coefficientEchelon[i][j].multiply(ratio);
                constantVector[i] = constantVector[i].multiply(ratio);
            }
        }
        //展示结果
        System.out.println("解：");
        for (int i = 0; i < numberOfVariables; i++) System.out.println("x" + (i + 1) + " = " + constantVector[i]);
        System.out.println();
        numberOfEquations = 0;
        main(null);
    }

    ///求通解
    private static void infiniteSolution(Fraction[][] augmentedEchelon, int numberOfFreeVariables) {
        System.out.println("方程组有无穷解");
        //确定自由未知量n-r，将常数向量左边的n-r列单独保存
        Fraction[][] freeColumns = new Fraction[numberOfFreeVariables][numberOfEquations];
        for (int i = numberOfFreeVariables; i > 0; i--)
            for (int j = 0; j < freeColumns[0].length; j++)
                freeColumns[numberOfFreeVariables - i][j] = augmentedEchelon[j][augmentedEchelon[0].length - i - 1];
        //将自由列所有元素取相反数
        for (Fraction[] column : freeColumns) for (int i = 0; i < column.length; i++) column[i] = column[i].negate();
        int numberOfBasicVariables = numberOfVariables - numberOfFreeVariables;
        //展示结果
        System.out.println("通解：");
        for (int i = 0; i < numberOfEquations; i++) {
            StringBuilder tempFree = new StringBuilder();
            for (int j = 0; j < freeColumns.length - 1; j++)
                tempFree.append(freeColumns[j][i]).append("X").append(numberOfBasicVariables + j + 1).append(freeColumns[j + 1][i].isNegative() ? " " : " +");
            tempFree.append(freeColumns[freeColumns.length - 1][i]).append("X").append(numberOfVariables).append(" +");
            System.out.println("X" + (i + 1) + " = " + tempFree + augmentedEchelon[i][augmentedEchelon[0].length - 1]);
        }
    }
}
