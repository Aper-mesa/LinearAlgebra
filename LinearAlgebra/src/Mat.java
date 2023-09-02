import java.util.Arrays;
import java.util.Scanner;

//此程序用于进行矩阵的相关运算

public class Mat {
    private static final Scanner input = new Scanner(System.in);
    //矩阵的行数和列数
    private static int row1;
    private static int col1;
    private static int row2;
    private static int col2;
    //计算时用到的矩阵
    private static Fraction[][] mat1;
    private static Fraction[][] mat2;
    private static Fraction[][] result;

    public static void main(String[] args) {
        while (true) {
            System.out.println("""
                    选择运算
                    1. 加法  2. 减法
                    3. 乘法  4. 方幂
                    5. 数乘  6. 转置
                    7. 求秩  8. 求逆
                        0. 退出""");
            switch (input.nextLine()) {
                case "1" -> result = add(1);
                case "2" -> result = add(-1);
                case "3" -> result = multiply();
                case "4" -> result = exponentiate();
                case "5" -> result = scalarMultiply();
                case "6" -> result = transpose();
                case "7" -> result = getRank();
                case "8" -> result = inverse();
                case "0" -> System.exit(0);
            }
            print(result);
        }
    }

    ///打印矩阵
    private static void print(Fraction[][] mat) {
        for (Fraction[] row : mat) System.out.println(Arrays.toString(row));
    }

    ///传递一个二维数组变量用于存储一个矩阵,参数记录矩阵需要记录的次数，加减乘为1和2，幂运算为0
    public static Fraction[][] input(int order) {
        System.out.println("输入矩阵的行数和列数，数字之间用一个空格隔开");
        String[] shape = input.nextLine().split(" ");
        int actualRow, actualCol;
        if (order == 1) {
            row1 = Integer.parseInt(shape[0]);
            col1 = Integer.parseInt(shape[1]);
            actualRow = row1;
            actualCol = col1;
        } else {
            row2 = Integer.parseInt(shape[0]);
            col2 = Integer.parseInt(shape[1]);
            actualRow = row2;
            actualCol = col2;
        }
        //临时数组存储用户输入的行列式的某一行的所有元素
        String[] tempRowArr;
        Fraction[][] mat = new Fraction[actualRow][actualCol];
        for (int i = 0; i < actualRow; i++) {
            System.out.println("输入矩阵第" + (i + 1) + "行的所有元素，元素之间用一个空格隔开");
            tempRowArr = input.nextLine().split(" ");
            for (int j = 0; j < actualCol; j++) mat[i][j] = new Fraction(tempRowArr[j]);
        }
        return mat;
    }

    ///矩阵加减法，参数决定加减，1为加，-1为减
    public static Fraction[][] add(int sign) {
        mat1 = input(1);
        mat2 = input(2);
        if (row1 != row2 || col1 != col2) {
            System.out.println("两个矩阵不同型，无法计算\n");
            main(null);
        }
        Fraction[][] result = new Fraction[mat1.length][mat1[0].length];
        for (int i = 0; i < result.length; i++)
            for (int j = 0; j < result.length; j++)
                //result[i][j] = mat1[i][j].multiply(mat2[i][j].multiply(new Fraction(sign + "")));
                result[i][j] = mat1[i][j].add(mat2[i][j].multiply(new Fraction(sign + "")));
        return result;
    }

    ///乘法框架
    public static Fraction[][] multiply() {
        mat1 = input(1);
        mat2 = input(2);
        if (col1 != row2) {
            System.out.println("左矩阵的列数不等于右矩阵的行数，无法计算\n");
            main(null);
        }
        return multiplicationAlgorithm(mat1, mat2);
    }

    ///乘法算法核心
    private static Fraction[][] multiplicationAlgorithm(Fraction[][] multiplicand, Fraction[][] multiplier) {
        Fraction[][] result = new Fraction[multiplicand.length][multiplier[0].length];
        //临时记录积的某个元素的值
        Fraction temp;
        int leftRow, leftCol, rightCol;
        for (leftRow = 0; leftRow < multiplicand.length; leftRow++) {
            for (rightCol = 0; rightCol < multiplier[0].length; rightCol++) {
                temp = Fraction.ZERO;
                for (leftCol = 0; leftCol < multiplicand[0].length; leftCol++)
                    temp = temp.add(multiplicand[leftRow][leftCol].multiply(multiplier[leftCol][rightCol]));
                result[leftRow][rightCol] = temp;
            }
        }
        return result;
    }

    ///方幂
    public static Fraction[][] exponentiate() {
        mat1 = input(1);
        Fraction[][] fraction = new Fraction[mat1.length][mat1[0].length];
        for (int i = 0; i < fraction.length; i++) fraction[i] = Arrays.copyOf(mat1[i], mat1[0].length);
        System.out.println("输入指数");
        int exponent = Integer.parseInt(input.nextLine());
        //指数为0，结果为同阶单位矩阵
        if (exponent == 0) {
            for (Fraction[] row : mat1) Arrays.fill(row, Fraction.ZERO);
            for (int i = 0; i < mat1.length; i++) fraction[i][i] = Fraction.ONE;
            return fraction;
        }
        for (int i = 1; i < exponent; i++) fraction = multiplicationAlgorithm(fraction, mat1);
        return fraction;
    }

    ///数乘
    public static Fraction[][] scalarMultiply() {
        mat1 = input(1);
        System.out.println("输入所乘数字");
        Fraction k = new Fraction(input.nextLine());
        result = mat1;
        for (int i = 0; i < mat1.length; i++)
            for (int j = 0; j < mat1[0].length; j++) result[i][j] = k.multiply(result[i][j]);
        return result;
    }

    ///转置
    public static Fraction[][] transpose() {
        mat1 = input(1);
        Fraction[][] result = new Fraction[mat1[0].length][mat1.length];
        for (int row = 0; row < mat1.length; row++)
            for (int col = 0; col < mat1[0].length; col++) result[col][row] = mat1[row][col];
        return result;
    }

    ///求秩框架
    public static Fraction[][] getRank() {
        mat1 = input(1);
        System.out.println("秩为" + rankAlgorithm(mat1));
        return mat1;
    }

    ///求秩算法核心
    private static int rankAlgorithm(Fraction[][] mat) {
        int firstNonZeroRow;
        //找到第一个非零行
        outer:
        for (firstNonZeroRow = 0; firstNonZeroRow < mat.length; firstNonZeroRow++)
            for (Fraction e : mat[firstNonZeroRow]) if (!e.equals(Fraction.ZERO)) break outer;
        int row;
        while (true) {
            //找到第一个非零行的第一个非零元素下面的第一个非零行并执行行变换
            //先将左边的所有第一个数字为0的非零行进行交换。先找到第一个非零列
            for (int l = 0; l < Math.min(mat.length, mat[0].length); l++) {
                if (mat[l][l].equals(Fraction.ZERO)) {
                    for (int i = l; i < mat.length - 1; i++) {
                        if (!mat[i + 1][l].equals(Fraction.ZERO)) {
                            Fraction[] temp = mat[i + 1];
                            mat[i + 1] = mat[l];
                            mat[l] = temp;
                            break;
                        }
                    }
                }
            }
            //记录矩阵是否为阶梯型
            boolean echelon = false;
            int firstNonZeroElement;
            flag:
            for (row = firstNonZeroRow; row < mat.length; row++) {
                //先判断此矩阵是否已经为阶梯矩阵：找到每一行的第一个非零元素
                for (firstNonZeroElement = 0; firstNonZeroElement < mat[row].length; firstNonZeroElement++)
                    if (!mat[row][firstNonZeroElement].equals(Fraction.ZERO)) break;
                //找到第一个非零元素之后，检查它左边的每一列是否全部非零
                for (int i = 0; i < firstNonZeroElement; i++)
                    for (Fraction[] fractions : mat) if (!fractions[i].equals(Fraction.ZERO)) break flag;
                //接着检查每一行的第一个非零元素下面是否有非零元素
                for (int col = 0; col < mat[0].length; col++) {
                    inner:
                    for (int firstNonZero = 0; firstNonZero < mat.length; firstNonZero++) {
                        for (int i = 0; i < col; i++) if (!mat[firstNonZero][i].equals(Fraction.ZERO)) continue inner;
                        if (!mat[firstNonZero][col].equals(Fraction.ZERO))
                            for (int bottom = firstNonZero + 1; bottom < mat.length; bottom++)
                                if (!mat[bottom][col].equals(Fraction.ZERO)) break flag;
                    }
                }
                echelon = true;
            }
            //遍历矩阵后，若为阶梯形矩阵，则直接计算秩；否则执行行变换
            if (echelon) {
                int rank = 0;
                for (int i = firstNonZeroRow; i < mat.length; i++) {
                    for (int e = 0; e < mat[i].length; e++) {
                        if (!mat[i][e].equals(Fraction.ZERO)) {
                            rank++;
                            break;
                        }
                    }
                }
                return rank;
            }
            //以下为加K倍操作
            int changer = 0;
            int col, firstNonZero = 0, bottom;
            outer:
            for (col = 0; col < mat[0].length; col++) {
                inner:
                for (firstNonZero = 0; firstNonZero < mat.length; firstNonZero++) {
                    for (int i = 0; i < col; i++) if (!mat[firstNonZero][i].equals(Fraction.ZERO)) continue inner;
                    if (!mat[firstNonZero][col].equals(Fraction.ZERO)) {
                        for (bottom = firstNonZero + 1; bottom < mat.length; bottom++) {
                            if (!mat[bottom][col].equals(Fraction.ZERO)) {
                                changer = bottom;
                                break outer;
                            }
                        }
                    }
                }
            }
            //计算两行之间的比例
            Fraction ratio = (mat[changer][col].negate().divide(mat[firstNonZero][col]));
            //用临时数组存储乘以比例之后的行
            Fraction[] temp = new Fraction[mat[0].length];
            for (int j = 0; j < mat[0].length; j++) temp[j] = ratio.multiply(mat[firstNonZero][j]);
            //将临时数组中的数据依次加到要变成0的那一行中
            for (int j = 0; j < mat[0].length; j++) mat[changer][j] = mat[changer][j].add(temp[j]);
        }
    }

    ///求逆
    public static Fraction[][] inverse() {
        mat1 = input(1);
        if (row1 != col1) {
            System.out.println("只有方阵才有逆矩阵\n");
            main(null);
        }
        //不能使用原矩阵判断是否满秩，因为不能让原矩阵发生变化。因此先进行深复制
        Fraction[][] test = new Fraction[mat1.length][mat1.length];
        for (int i = 0; i < mat1.length; i++) test[i] = Arrays.copyOf(mat1[i], mat1[0].length);
        //调用求秩方法判断方阵是否满秩
        if (mat1.length != rankAlgorithm(test)) {
            System.out.println("方阵不满秩\n");
            main(null);
        }
        //与方阵一起参与运算的单位矩阵，操作结束后即为结果
        Fraction[][] identity = new Fraction[mat1.length][mat1.length];
        for (int i = 0; i < identity.length; i++) identity[i][i] = Fraction.ONE;
        boolean zero = true;
        //此循环将方阵变为上三角
        for (int dia = 0; dia < mat1.length; dia++) {
            Fraction[] tempRow, tempRow2;
            //判断对角线是否为0，为0则找下面的第一个非0行进行交换
            if (mat1[dia][dia].equals(Fraction.ZERO)) {
                int i;
                for (i = dia + 1; i < mat1.length; i++) {
                    if (!mat1[i][dia].equals(Fraction.ZERO)) {
                        zero = false;
                        break;
                    }
                }
                if (zero) System.out.println("对角线上有0");
                tempRow = mat1[dia];
                mat1[dia] = mat1[i];
                mat1[i] = tempRow;
                tempRow2 = identity[dia];
                identity[dia] = identity[i];
                identity[i] = tempRow2;
            }
            int i;
            //将对角线数以下的数字变为0
            for (int k = 0; k < mat1.length - dia - 1; k++) {
                boolean has = false;
                //找到第一个不是0的数字的行的索引
                for (i = dia + 1; i < mat1.length; i++) {
                    if (!mat1[i][dia].equals(Fraction.ZERO)) {
                        has = true;
                        break;
                    }
                }
                addK(identity, has, i, dia);
            }
        }
        //接下来将对角线上的数字变为0：从下往上加
        for (int dia = mat1.length - 1; dia >= 0; dia--) {
            int i;
            //将对角线数以上的数字变为0
            for (int k = 0; k < dia + 1; k++) {
                boolean has = false;
                //找到对角线数上第一个非零数字
                for (i = dia - 1; i >= 0; i--) {
                    if (!mat1[i][dia].equals(Fraction.ZERO)) {
                        has = true;
                        break;
                    }
                }
                addK(identity, has, i, dia);
            }
        }
        //最后将对角线数都化为1
        for (int i = 0; i < mat1.length; i++) {
            if (!mat1[i][i].equals(Fraction.ONE)) {
                Fraction ratio = Fraction.ONE.divide(mat1[i][i]);
                for (int j = 0; j < mat1.length; j++) {
                    mat1[i][j] = mat1[i][j].multiply(ratio);
                    identity[i][j] = identity[i][j].multiply(ratio);
                }
            }
        }
        return identity;
    }

    ///矩阵加K倍操作
    private static void addK(Fraction[][] identity, boolean has, int i, int dia) {
        if (has) {
            //计算两行之间的比例
            Fraction ratio = mat1[i][dia].negate().divide(mat1[dia][dia]);
            //用临时数组存储乘以比例之后的对角线行
            Fraction[] temp = new Fraction[mat1.length], temp2 = new Fraction[mat1.length];
            for (int j = 0; j < mat1.length; j++) {
                temp[j] = ratio.multiply(mat1[dia][j]);
                temp2[j] = ratio.multiply(identity[dia][j]);
            }
            //将临时数组中的数据依次加到要变成0的那一行中
            for (int j = 0; j < mat1.length; j++) {
                mat1[i][j] = mat1[i][j].add(temp[j]);
                identity[i][j] = identity[i][j].add(temp2[j]);
            }
        }
    }
}
