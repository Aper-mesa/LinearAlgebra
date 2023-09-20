import java.util.*;

//此类用于解线性方程组
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
        //控制台文字颜色转义符
        String blue = "\u001B[34m";
        String reset = "\u001B[0m";
        System.out.println(blue + "注意根式格式：a*b^/c*d^，只写^默认表示二次根式，目前仅支持二次根式！" + reset);
        ArrayList<String> equations = new ArrayList<>();
        while (true) {
            String nextLine = input.nextLine();
            if (nextLine.equals("0")) break;
            equations.add(nextLine);
            numberOfEquations++;
        }
        Real[][] augmentedMatrix = new Real[numberOfEquations][numberOfVariables + 1];
        Real[][] coefficientMatrix;
        for (int i = 0; i < augmentedMatrix.length; i++)
            for (int j = 0; j < augmentedMatrix[0].length; j++)
                augmentedMatrix[i][j] = new Real(equations.get(i).split(" ")[j]);
        coefficientMatrix = Tool.deepCopy(augmentedMatrix, numberOfVariables);
        Real[][] augmentedEchelon;
        //此循环去掉阶梯增广矩阵的0行
        do {
            augmentedEchelon = (Real[][]) Mat.getRank(augmentedMatrix)[1];
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
    private static void uniqueSolution(Real[][] augmentedEchelon) {
        System.out.println("方程组有唯一解");
        //与方阵一起参与运算的单位矩阵，操作结束后即为结果
        Real[] constantVector = new Real[augmentedEchelon.length];
        Real[][] coefficientEchelon = Tool.deepCopy(augmentedEchelon, augmentedEchelon[0].length - 1);
        for (int i = 0; i < constantVector.length; i++)
            constantVector[i] = augmentedEchelon[i][augmentedEchelon[0].length - 1];
        //此循环将系数矩阵变为上三角
        for (int dia = 0; dia < coefficientEchelon.length; dia++) {
            Real[] tempRow;
            Real tempElement;
            //判断对角线是否为0，为0则找下面的第一个非0行进行交换
            if (coefficientEchelon[dia][dia].equals(Real.ZERO)) {
                int i;
                for (i = dia + 1; i < coefficientEchelon.length; i++)
                    if (!coefficientEchelon[i][dia].equals(Real.ZERO)) break;
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
                    if (!coefficientEchelon[i][dia].equals(Real.ZERO)) {
                        has = true;
                        break;
                    }
                }
                Tool.addK(constantVector, has, i, dia, coefficientEchelon);
            }
        }
        Tool.eliminateUpper(coefficientEchelon, constantVector);
        //最后将对角线数都化为1
        for (int i = 0; i < coefficientEchelon.length; i++) {
            if (!coefficientEchelon[i][i].equals(Real.ONE)) {
                Real ratio = Real.ONE.divide(coefficientEchelon[i][i]);
                for (int j = 0; j < coefficientEchelon.length; j++)
                    coefficientEchelon[i][j] = coefficientEchelon[i][j].multiply(ratio);
                constantVector[i] = constantVector[i].multiply(ratio);
            }
        }
        //展示结果
        System.out.println(Arrays.toString(constantVector) + "\n");
        numberOfEquations = 0;
        main(null);
    }

    ///求基础解系η
    private static void infiniteSolution(Real[][] augmentedEchelon, int numberOfFreeVariables) {
        System.out.println("方程组有无穷解");
        //建一个集合存储所有未知量的索引
        ArrayList<Integer> freeVariables = new ArrayList<>();
        for (int i = 0; i < numberOfVariables; i++) freeVariables.add(i + 1);
        Real[] constantVector = new Real[augmentedEchelon.length];
        for (int i = 0; i < constantVector.length; i++)
            constantVector[i] = augmentedEchelon[i][augmentedEchelon[0].length - 1];
        Real[][] coefficientEchelon = Tool.deepCopy(augmentedEchelon, augmentedEchelon[0].length - 1);
        //将系数矩阵每一行的pivot都变成1，找pivot并将其从集合中去除，全部完成后剩余元素即为自由未知量
        for (int j = 0; j < coefficientEchelon.length; j++) {
            for (int i = 0; i < coefficientEchelon[0].length; i++) {
                if (!coefficientEchelon[j][i].equals(Real.ZERO)) {
                    freeVariables.remove((Integer) (i + 1));
                    if (!coefficientEchelon[j][i].equals(Real.ONE)) {
                        Real ratio = Real.ONE.divide(coefficientEchelon[j][i]);
                        for (int k = 0; k < coefficientEchelon[j].length; k++)
                            coefficientEchelon[j][k] = coefficientEchelon[j][k].multiply(ratio);
                        constantVector[j] = constantVector[j].multiply(ratio);
                    }
                    break;
                }
            }
        }
        //将所有基础未知量的索引存入一个数组中
        ArrayList<Integer> basicVariables = new ArrayList<>();
        for (int i = 0; i < numberOfVariables; i++) if (!freeVariables.contains(i + 1)) basicVariables.add(i + 1);
        //基础解系
        Real[][] fundamentalSolution = new Real[numberOfFreeVariables][numberOfVariables];
        //这里要循环n-r次，每次都给自由未知量赋不同的值，但所有情况组合成一个标准基
        for (int i = 0; i < numberOfFreeVariables; i++) {
            Real[] specificFreeVariables = new Real[numberOfFreeVariables];
            Real[] specificBasicVariables = new Real[basicVariables.size()];
            Arrays.fill(specificFreeVariables, Real.ZERO);
            Arrays.fill(specificBasicVariables, Real.ZERO);
            specificFreeVariables[i] = Real.ONE;
            //先把基础解系的自由未知量位置填好
            for (int k = 0; k < numberOfFreeVariables; k++)
                fundamentalSolution[i][freeVariables.get(k) - 1] = specificFreeVariables[k];
            //这里循环解出基础未知量。从底下往上解
            for (int k = basicVariables.size(); k > 0; k--) {
                boolean pivot = false;
                int pivotIndex = 0;
                Real temp = Real.ZERO;
                for (int j = 0; j < numberOfVariables; j++) {
                    if (coefficientEchelon[k - 1][j].equals(Real.ZERO)) continue;
                    if (!pivot && !coefficientEchelon[k - 1][j].equals(Real.ZERO)) {
                        pivot = true;
                        pivotIndex = j;
                        continue;
                    }
                    if (j > pivotIndex) {
                        if (freeVariables.contains(j + 1))
                            temp = temp.add(specificFreeVariables[freeVariables.indexOf(j+1)].multiply(coefficientEchelon[k - 1][j]));
                        else
                            temp = temp.add(specificBasicVariables[basicVariables.indexOf(j+1)].multiply(coefficientEchelon[k - 1][j]));
                    }
                }
                specificBasicVariables[k - 1] = constantVector[k - 1].subtract(temp);
                fundamentalSolution[i][basicVariables.get(k - 1) - 1] = constantVector[k - 1].subtract(temp);
            }
        }
        System.out.println("一个基础解系：");
        Tool.print(fundamentalSolution);
        numberOfEquations = 0;
        main(null);
    }
}
