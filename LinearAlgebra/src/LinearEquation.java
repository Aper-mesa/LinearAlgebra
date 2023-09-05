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
        Fraction[][] coefficientMatrix;
        for (int i = 0; i < augmentedMatrix.length; i++)
            for (int j = 0; j < augmentedMatrix[0].length; j++)
                augmentedMatrix[i][j] = new Fraction(equations.get(i).split(" ")[j]);
        coefficientMatrix = Tool.deepCopy(augmentedMatrix, numberOfUnknowns);
        int augmentedRank = (int) Mat.getRank(augmentedMatrix)[0];
        int coefficientRank = (int) Mat.getRank(coefficientMatrix)[0];
        if (augmentedRank != coefficientRank) {
            System.out.println("方程组无解");
            System.exit(0);
        }
        boolean uniqueSolution = false;
        Fraction[][] augmentedEchelon;
        //此循环去掉阶梯增广矩阵的0行
        do {
            augmentedEchelon = (Fraction[][]) Mat.getRank(augmentedMatrix)[1];
            if (Tool.hasZeroRowOrColumn(augmentedEchelon)) {
                augmentedEchelon = Tool.deepCopy(augmentedEchelon, augmentedEchelon.length - 1, augmentedEchelon[0].length);
                numberOfEquations--;
            }
        } while (Tool.hasZeroRowOrColumn(augmentedEchelon));
        coefficientMatrix = Tool.deepCopy(augmentedMatrix, numberOfUnknowns);
        coefficientRank = (int) Mat.getRank(coefficientMatrix)[0];
        if (coefficientRank == numberOfEquations) {
            uniqueSolution = true;
            System.out.println("方程有唯一解");
        } else System.out.println("方程有无数解");
        System.out.println("阶梯增广矩阵：");
        for (Fraction[] row : augmentedEchelon) System.out.println(Arrays.toString(row));
        if (uniqueSolution) uniqueSolution(augmentedEchelon);
    }

    ///求唯一解
    private static Fraction[][] uniqueSolution(Fraction[][] augmentedEchelon) {
        //与方阵一起参与运算的单位矩阵，操作结束后即为结果
        Fraction[] constantVector = new Fraction[augmentedEchelon.length];
        Fraction[][] coefficientEchelon = Tool.deepCopy(augmentedEchelon, augmentedEchelon[0].length - 1);
        for (int i = 0; i < constantVector.length; i++)
            constantVector[i] = augmentedEchelon[i][augmentedEchelon[0].length - 1];
        System.out.println("常数向量：");
        System.out.println(Arrays.toString(constantVector));
        boolean zero = true;
       /* //此循环将系数矩阵变为上三角
        for (int dia = 0; dia < augmentedEchelon.length; dia++) {
            Fraction[] tempRow, tempRow2;
            //判断对角线是否为0，为0则找下面的第一个非0行进行交换
            if (augmentedEchelon[dia][dia].equals(Fraction.ZERO)) {
                int i;
                for (i = dia + 1; i < augmentedEchelon.length; i++) {
                    if (!augmentedEchelon[i][dia].equals(Fraction.ZERO)) {
                        zero = false;
                        break;
                    }
                }
                if (zero) System.out.println("对角线上有0");
                tempRow = augmentedEchelon[dia];
                augmentedEchelon[dia] = augmentedEchelon[i];
                augmentedEchelon[i] = tempRow;
                tempRow2 = constantVector[dia];
                constantVector[dia] = constantVector[i];
                constantVector[i] = tempRow2;
            }
            int i;
            //将对角线数以下的数字变为0
            for (int k = 0; k < augmentedEchelon.length - dia - 1; k++) {
                boolean has = false;
                //找到第一个不是0的数字的行的索引
                for (i = dia + 1; i < augmentedEchelon.length; i++) {
                    if (!augmentedEchelon[i][dia].equals(Fraction.ZERO)) {
                        has = true;
                        break;
                    }
                }
                Tool.addK(constantVector, has, i, dia, augmentedEchelon);
            }
        }
        //接下来将对角线上的数字变为0：从下往上加
        for (int dia = augmentedEchelon.length - 1; dia >= 0; dia--) {
            int i;
            //将对角线数以上的数字变为0
            for (int k = 0; k < dia + 1; k++) {
                boolean has = false;
                //找到对角线数上第一个非零数字
                for (i = dia - 1; i >= 0; i--) {
                    if (!augmentedEchelon[i][dia].equals(Fraction.ZERO)) {
                        has = true;
                        break;
                    }
                }
                Tool.addK(constantVector, has, i, dia, augmentedEchelon);
            }
        }
        //最后将对角线数都化为1
        for (int i = 0; i < augmentedEchelon.length; i++) {
            if (!augmentedEchelon[i][i].equals(Fraction.ONE)) {
                Fraction ratio = Fraction.ONE.divide(augmentedEchelon[i][i]);
                for (int j = 0; j < augmentedEchelon.length; j++) {
                    augmentedEchelon[i][j] = augmentedEchelon[i][j].multiply(ratio);
                    constantVector[i][j] = constantVector[i][j].multiply(ratio);
                }
            }
        }*/
        return augmentedEchelon;
    }

    private static Fraction[][] infiniteSolution(Fraction[][] echelon) {
        return null;
    }
}
