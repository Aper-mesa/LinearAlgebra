import java.util.*;

////此类用于解线性方程组
public class LinearEquation extends Console {
    private LinearEquation() {
    }

    private static final Scanner input = new Scanner(System.in);
    private static int numberOfEquations = 0;

    ///求解的初始步骤：输入和判断等
    protected static void compute(int numberOfVariables) {
        if (numberOfVariables == 0) return;
        System.out.println(text.getString("inputAugmentedRow"));
        ArrayList<String> equations = new ArrayList<>();
        while (true) {
            String line = input.nextLine().strip().toLowerCase();
            if(line.equals("return")) return;
            String nextLine = line.strip().replaceAll("\\s+", " ");
            if (nextLine.equals("0")) break;
            loop:
            while (true) {
                String[] tempRowArr = nextLine.split(" ");
                if (tempRowArr.length != numberOfVariables) {
                    System.out.println(text.getString("invalidCommand"));
                    continue;
                }
                for (String s : tempRowArr) {
                    if (notNumber(s)) {
                        System.out.println(text.getString("invalidCommand"));
                        continue loop;
                    }
                }
                break;
            }
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
            Real[][] augmentedMatrixCopy = Tool.deepCopy(augmentedMatrix);
            Mat.rankAlgorithm(augmentedMatrixCopy);
            augmentedEchelon = augmentedMatrixCopy;
            if (Tool.hasZeroRowOrColumn(augmentedEchelon)) {
                augmentedEchelon = Tool.deepCopy(augmentedEchelon, augmentedEchelon.length - 1, augmentedEchelon[0].length);
                numberOfEquations--;
            }
        } while (Tool.hasZeroRowOrColumn(augmentedEchelon));
        int augmentedRank = Mat.rankAlgorithm(augmentedEchelon);
        int coefficientRank = Mat.rankAlgorithm(coefficientMatrix);
        if (augmentedRank != coefficientRank) {
            System.out.println(text.getString("noSolution"));
            numberOfEquations = 0;
            return;
        }
        coefficientMatrix = Tool.deepCopy(augmentedMatrix, numberOfVariables);
        coefficientRank = Mat.rankAlgorithm(coefficientMatrix);
        //判断系数矩阵来确定方的程解的数量
        if (coefficientRank == numberOfVariables) uniqueSolution(augmentedEchelon);
        else infiniteSolution(augmentedEchelon, numberOfVariables - augmentedRank, numberOfVariables);
    }

    ///求唯一解
    private static void uniqueSolution(Real[][] augmentedEchelon) {
        System.out.println(text.getString("oneSolution"));
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
    }

    ///求基础解系η
    private static void infiniteSolution(Real[][] augmentedEchelon, int numberOfFreeVariables, int numberOfVariables) {
        System.out.println(text.getString("infinitelyManySolutions"));
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
                            temp = temp.add(specificFreeVariables[freeVariables.indexOf(j + 1)].multiply(coefficientEchelon[k - 1][j]));
                        else
                            temp = temp.add(specificBasicVariables[basicVariables.indexOf(j + 1)].multiply(coefficientEchelon[k - 1][j]));
                    }
                }
                specificBasicVariables[k - 1] = constantVector[k - 1].subtract(temp);
                fundamentalSolution[i][basicVariables.get(k - 1) - 1] = constantVector[k - 1].subtract(temp);
            }
        }
        System.out.println(text.getString("fundamentalSolution"));
        Tool.print(fundamentalSolution);
        numberOfEquations = 0;
    }
}
