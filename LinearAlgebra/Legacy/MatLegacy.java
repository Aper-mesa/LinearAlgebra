import java.util.Arrays;
import java.util.Scanner;

//此程序用于进行矩阵的相关运算
//2023.08.24-此类已过时，新矩阵计算器已接入分数类。此类采用的伪精度解决方案是愚蠢而有意义的。

public class MatLegacy {
    private static final Scanner input = new Scanner(System.in);
    //矩阵的行数和列数
    private static int row1;
    private static int col1;
    private static int row2;
    private static int col2;
    //计算时用到的矩阵
    private static double[][] mat1;
    private static double[][] mat2;
    private static double[][] result;

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
                case "1" -> result = addSub(1);
                case "2" -> result = addSub(-1);
                case "3" -> result = multiplication();
                case "4" -> result = exponentiation();
                case "5" -> result = scalar();
                case "6" -> result = transpose();
                case "7" -> result = rank();
                case "8" -> result = inverse();
                case "0" -> System.exit(0);
            }
            print(result);
        }
    }

    ///打印矩阵
    private static void print(double[][] mat) {
        for (double[] row : mat) System.out.println(Arrays.toString(row));
    }

    ///解决精度问题：若元素绝对值小于0.000000000000001，则此元素看作是0
    private static void format(double[][] mat) {
        for (double[] row : mat) {
            for (int i = 0; i < row.length; i++) {
                if (Math.abs(row[i]) < 0.00000000000001) row[i] = 0;
                else if (Math.abs(row[i] - Math.floor(row[i])) < 0.00000000000001) row[i] = Math.floor(row[i]);
                else if (row[i] == -0) row[i] = 0;
            }
        }
    }

    ///传递一个二维数组变量用于存储一个矩阵,参数记录矩阵需要记录的次数，加减乘为1和2，幂运算为0
    public static double[][] input(int order) {
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
        double[][] mat = new double[actualRow][actualCol];
        for (int i = 0; i < actualRow; i++) {
            System.out.println("输入矩阵第" + (i + 1) + "行的所有元素，元素之间用一个空格隔开");
            tempRowArr = input.nextLine().split(" ");
            for (int j = 0; j < actualCol; j++) mat[i][j] = Double.parseDouble(tempRowArr[j]);
        }
        return mat;
    }

    ///矩阵加减法，参数决定加减，1为加，-1为减
    public static double[][] addSub(int sign) {
        mat1 = input(1);
        mat2 = input(2);
        if (row1 != row2 || col1 != col2) {
            System.out.println("两个矩阵不同型，无法计算\n");
            main(null);
        }
        double[][] result = new double[mat1.length][mat1[0].length];
        for (int i = 0; i < result.length; i++)
            for (int j = 0; j < result.length; j++) result[i][j] = mat1[i][j] + sign * mat2[i][j];
        return result;
    }

    ///乘法框架
    public static double[][] multiplication() {
        mat1 = input(1);
        mat2 = input(2);
        if (col1 != row2) {
            System.out.println("左矩阵的列数不等于右矩阵的行数，无法计算\n");
            main(null);
        }
        return multiplicationAlgorithm(mat1, mat2);
    }

    ///乘法算法核心
    private static double[][] multiplicationAlgorithm(double[][] left, double[][] right) {
        double[][] result = new double[left.length][right[0].length];
        //临时记录积的某个元素的值
        double temp;
        int leftRow, leftCol, rightCol;
        for (leftRow = 0; leftRow < left.length; leftRow++) {
            for (rightCol = 0; rightCol < right[0].length; rightCol++) {
                temp = 0;
                for (leftCol = 0; leftCol < left[0].length; leftCol++)
                    temp = temp + left[leftRow][leftCol] * right[leftCol][rightCol];
                result[leftRow][rightCol] = temp;
            }
        }
        return result;
    }

    ///方幂
    public static double[][] exponentiation() {
        mat1 = input(1);
        System.out.println("输入指数");
        int exponent = Integer.parseInt(input.nextLine());
        //指数为0，结果为同阶单位矩阵
        if (exponent == 0) {
            for (double[] row : mat1) Arrays.fill(row, 0);
            for (int i = 0; i < mat1.length; i++) mat1[i][i] = 1;
            return mat1;
        }
        result = mat1;
        for (int i = 0; i < exponent; i++) result = multiplicationAlgorithm(mat1, result);
        return result;
    }

    ///数乘
    public static double[][] scalar() {
        mat1 = input(1);
        System.out.println("输入所乘数字");
        double k = Double.parseDouble(input.nextLine());
        result = mat1;
        for (int i = 0; i < mat1.length; i++) for (int j = 0; j < mat1[0].length; j++) result[i][j] *= k;
        return result;
    }

    ///转置
    public static double[][] transpose() {
        mat1 = input(1);
        double[][] result = new double[mat1[0].length][mat1.length];
        for (int row = 0; row < mat1.length; row++)
            for (int col = 0; col < mat1[0].length; col++) result[col][row] = mat1[row][col];
        return result;
    }

    ///求秩框架
    public static double[][] rank() {
        mat1 = input(1);
        System.out.println("秩为" + rankAlgorithm(mat1));
        return mat1;
    }

    ///求秩算法核心
    private static int rankAlgorithm(double[][] mat) {
        int firstNonZeroRow;
        //找到第一个非零行
        outer:
        for (firstNonZeroRow = 0; firstNonZeroRow < mat.length; firstNonZeroRow++)
            for (double e : mat[firstNonZeroRow]) if (e != 0) break outer;
        int row;
        while (true) {
            //找到第一个非零行的第一个非零元素下面的第一个非零行并执行行变换
            //先将左边的所有第一个数字为0的非零行进行交换。先找到第一个非零列
            for (int l = 0; l < Math.min(mat.length, mat[0].length); l++) {
                if (mat[l][l] == 0) {
                    for (int i = l; i < mat.length - 1; i++) {
                        if (mat[i + 1][l] != 0) {
                            double[] temp = mat[i + 1];
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
                    if (mat[row][firstNonZeroElement] != 0) break;
                //找到第一个非零元素之后，检查它左边的每一列是否全部非零
                for (int i = 0; i < firstNonZeroElement; i++)
                    for (double[] doubles : mat) if (doubles[i] != 0) break flag;
                //接着检查每一行的第一个非零元素下面是否有非零元素
                for (int col = 0; col < mat[0].length; col++) {
                    inner:
                    for (int firstNonZero = 0; firstNonZero < mat.length; firstNonZero++) {
                        for (int i = 0; i < col; i++) if (mat[firstNonZero][i] != 0) continue inner;
                        if (mat[firstNonZero][col] != 0)
                            for (int bottom = firstNonZero + 1; bottom < mat.length; bottom++)
                                if (mat[bottom][col] != 0) break flag;
                    }
                }
                echelon = true;
            }
            //遍历矩阵后，若为阶梯形矩阵，则直接计算秩；否则执行行变换
            if (echelon) {
                int rank = 0;
                for (int i = firstNonZeroRow; i < mat.length; i++) {
                    for (int e = 0; e < mat[i].length; e++) {
                        if (mat[i][e] != 0) {
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
                    for (int i = 0; i < col; i++) if (mat[firstNonZero][i] != 0) continue inner;
                    if (mat[firstNonZero][col] != 0) {
                        for (bottom = firstNonZero + 1; bottom < mat.length; bottom++) {
                            if (mat[bottom][col] != 0) {
                                changer = bottom;
                                break outer;
                            }
                        }
                    }
                }
            }
            //计算两行之间的比例
            double ratio = (-mat[changer][col] / mat[firstNonZero][col]);
            //用临时数组存储乘以比例之后的行
            double[] temp = new double[mat[0].length];
            for (int j = 0; j < mat[0].length; j++) temp[j] = ratio * mat[firstNonZero][j];
            //将临时数组中的数据依次加到要变成0的那一行中
            for (int j = 0; j < mat[0].length; j++) mat[changer][j] += temp[j];
            format(mat);
        }
    }

    ///求逆
    public static double[][] inverse() {
        mat1 = input(1);
        if (row1 != col1) {
            System.out.println("只有方阵才有逆矩阵\n");
            main(null);
        }
        //不能使用原矩阵判断是否满秩，因为不能让原矩阵发生变化。因此先进行深复制
        double[][] test = new double[mat1.length][mat1.length];
        for (int i = 0; i < mat1.length; i++) test[i] = Arrays.copyOf(mat1[i], mat1.length);
        //调用求秩方法判断方阵是否满秩
        if (mat1.length != rankAlgorithm(test)) {
            System.out.println("方阵不满秩\n");
            main(null);
        }
        //与方阵一起参与运算的单位矩阵，操作结束后即为结果
        double[][] identity = new double[mat1.length][mat1.length];
        for (int i = 0; i < identity.length; i++) identity[i][i] = 1;
        boolean zero = true;
        //此循环将方阵变为上三角
        for (int dia = 0; dia < mat1.length; dia++) {
            double[] tempRow, tempRow2;
            //判断对角线是否为0，为0则找下面的第一个非0行进行交换
            if (mat1[dia][dia] == 0) {
                int i;
                for (i = dia + 1; i < mat1.length; i++) {
                    if (mat1[i][dia] != 0) {
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
                    if (mat1[i][dia] != 0) {
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
                //找到对角线数上第一个非0数字
                for (i = dia - 1; i >= 0; i--) {
                    if (mat1[i][dia] != 0) {
                        has = true;
                        break;
                    }
                }
                addK(identity, has, i, dia);
            }
        }
        //最后将对角线数都化为1
        for (int i = 0; i < mat1.length; i++) {
            if (mat1[i][i] != 1) {
                double ratio = 1 / mat1[i][i];
                for (int j = 0; j < mat1.length; j++) {
                    mat1[i][j] *= ratio;
                    identity[i][j] *= ratio;
                }
            }
        }
        return identity;
    }

    ///矩阵加K倍操作
    private static void addK(double[][] identity, boolean has, int i, int dia) {
        if (has) {
            //计算两行之间的比例
            double ratio = (-mat1[i][dia] / mat1[dia][dia]);
            //用临时数组存储乘以比例之后的对角线行
            double[] temp = new double[mat1.length], temp2 = new double[mat1.length];
            for (int j = 0; j < mat1.length; j++) {
                temp[j] = ratio * mat1[dia][j];
                temp2[j] = ratio * identity[dia][j];
            }
            //将临时数组中的数据依次加到要变成0的那一行中
            for (int j = 0; j < mat1.length; j++) {
                mat1[i][j] += temp[j];
                identity[i][j] += temp2[j];
            }
        }
    }
}
