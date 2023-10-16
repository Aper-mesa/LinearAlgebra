import java.util.ArrayList;
import java.util.Arrays;

////此程序用于进行矩阵的相关运算

public class Mat {

    private Mat() {
    }

    private static Real[][] mat1;
    private static Real[][] mat2;

    ///矩阵加减法，参数决定加减，1为加，-1为减
    public static Real[][] add(int sign, Real[] info) {
        if (!canAddOrSubtract(info)) return null;
        mat1 = Tool.input(info[0].toInt(), info[1].toInt());
        mat2 = Tool.input(info[2].toInt(), info[3].toInt());
        Real[][] left = Tool.deepCopy(mat1);
        Real[][] right = Tool.deepCopy(mat2);
        Real[][] result = new Real[mat1.length][mat1[0].length];
        for (int i = 0; i < result.length; i++)
            for (int j = 0; j < result.length; j++)
                result[i][j] = left[i][j].add(right[i][j].multiply(new Real(sign + "")));
        return result;
    }

    ///判断矩阵是否可以相加或相减，即是否同型
    protected static boolean canAddOrSubtract(Real[] info) {
        return info[0] == info[2] && info[1] == info[3];
    }

    ///乘法框架
    public static Real[][] multiply(Real[] info) {
        if (!canMultiply(info)) return null;
        mat1 = Tool.input(info[0].toInt(), info[1].toInt());
        mat2 = Tool.input(info[2].toInt(), info[3].toInt());
        return multiplicationAlgorithm(mat1, mat2);
    }

    ///判断矩阵是否可以相乘
    public static boolean canMultiply(Real[] info) {
        return info[1].equals(info[2]);
    }

    ///乘法算法核心
    private static Real[][] multiplicationAlgorithm(Real[][] left, Real[][] right) {
        Real[][] multiplicand = Tool.deepCopy(left);
        Real[][] multiplier = Tool.deepCopy(right);
        Real[][] result = new Real[multiplicand.length][multiplier[0].length];
        //临时记录积的某个元素的值
        Real temp;
        int leftRow, leftCol, rightCol;
        for (leftRow = 0; leftRow < multiplicand.length; leftRow++) {
            for (rightCol = 0; rightCol < multiplier[0].length; rightCol++) {
                temp = Real.ZERO;
                for (leftCol = 0; leftCol < multiplicand[0].length; leftCol++)
                    temp = temp.add(multiplicand[leftRow][leftCol].multiply(multiplier[leftCol][rightCol]));
                result[leftRow][rightCol] = temp;
            }
        }
        return result;
    }

    ///方幂
    public static Real[][] power(Real[] info) {
        int exponent = info[0].toInt();
        int order = info[1].toInt();
        mat1 = Tool.input(order, order);
        Real[][] result = Tool.deepCopy(mat1);
        //指数为0，结果为同阶单位矩阵
        if (exponent == 0) {
            for (Real[] row : mat1) Arrays.fill(row, Real.ZERO);
            for (int i = 0; i < mat1.length; i++) result[i][i] = Real.ONE;
            return result;
        }
        for (int i = 1; i < exponent; i++) result = multiplicationAlgorithm(result, mat1);
        return result;
    }

    ///数乘
    public static Real[][] scalarMultiply(Real[] info) {
        mat1 = Tool.input(info[1].toInt(), info[2].toInt());
        Real k = new Real(info[0]);
        Real[][] result = Tool.deepCopy(mat1);
        for (int i = 0; i < mat1.length; i++)
            for (int j = 0; j < mat1[0].length; j++) result[i][j] = k.multiply(result[i][j]);
        return result;
    }

    ///转置
    public static Real[][] transpose(Real[] info) {
        mat1 = Tool.input(info[0].toInt(), info[1].toInt());
        Real[][] result = new Real[mat1[0].length][mat1.length];
        for (int row = 0; row < mat1.length; row++)
            for (int col = 0; col < mat1[0].length; col++) result[col][row] = mat1[row][col];
        return result;
    }

    ///求秩框架
    public static int getRank(Real[] info) {
        mat1 = Tool.input(info[0].toInt(), info[1].toInt());
        return rankAlgorithm(mat1);
    }

    ///求阶梯形矩阵
    public static Real[][] getEchelon(Real[] info) {
        mat1 = Tool.input(info[0].toInt(), info[1].toInt());
        rankAlgorithm(mat1);
        return mat1;
    }

    ///求秩算法核心
    protected static int rankAlgorithm(Real[][] mat) {
        int firstNonZeroRow;
        //找到第一个非零行
        outer:
        for (firstNonZeroRow = 0; firstNonZeroRow < mat.length; firstNonZeroRow++)
            for (Real e : mat[firstNonZeroRow]) if (!e.equals(Real.ZERO)) break outer;
        int row;
        while (true) {
            //找到第一个非零行的第一个非零元素下面的第一个非零行并执行行变换
            //先将左边的所有第一个数字为0的非零行进行交换。先找到第一个非零列
            for (int l = 0; l < Math.min(mat.length, mat[0].length); l++) {
                if (mat[l][l].equals(Real.ZERO)) {
                    for (int i = l; i < mat.length - 1; i++) {
                        if (!mat[i + 1][l].equals(Real.ZERO)) {
                            Real[] temp = mat[i + 1];
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
                    if (!mat[row][firstNonZeroElement].equals(Real.ZERO)) break;
                //找到第一个非零元素之后，检查它左边的每一列是否全部非零
                for (int i = 0; i < firstNonZeroElement; i++)
                    for (Real[] fractions : mat) if (!fractions[i].equals(Real.ZERO)) break flag;
                //接着检查每一行的第一个非零元素下面是否有非零元素
                for (int col = 0; col < mat[0].length; col++) {
                    inner:
                    for (int firstNonZero = 0; firstNonZero < mat.length; firstNonZero++) {
                        for (int i = 0; i < col; i++) if (!mat[firstNonZero][i].equals(Real.ZERO)) continue inner;
                        if (!mat[firstNonZero][col].equals(Real.ZERO))
                            for (int bottom = firstNonZero + 1; bottom < mat.length; bottom++)
                                if (!mat[bottom][col].equals(Real.ZERO)) break flag;
                    }
                }
                echelon = true;
            }
            //遍历矩阵后，若为阶梯形矩阵，则直接计算秩；否则执行行变换
            if (echelon) {
                int rank = 0;
                for (int i = firstNonZeroRow; i < mat.length; i++) {
                    for (int e = 0; e < mat[i].length; e++) {
                        if (!mat[i][e].equals(Real.ZERO)) {
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
                    for (int i = 0; i < col; i++) if (!mat[firstNonZero][i].equals(Real.ZERO)) continue inner;
                    if (!mat[firstNonZero][col].equals(Real.ZERO)) {
                        for (bottom = firstNonZero + 1; bottom < mat.length; bottom++) {
                            if (!mat[bottom][col].equals(Real.ZERO)) {
                                changer = bottom;
                                break outer;
                            }
                        }
                    }
                }
            }
            //计算两行之间的比例
            Real ratio = (mat[changer][col].negate().divide(mat[firstNonZero][col]));
            //用临时数组存储乘以比例之后的行
            Real[] temp = new Real[mat[0].length];
            for (int j = 0; j < mat[0].length; j++) temp[j] = ratio.multiply(mat[firstNonZero][j]);
            //将临时数组中的数据依次加到要变成0的那一行中
            for (int j = 0; j < mat[0].length; j++) mat[changer][j] = mat[changer][j].add(temp[j]);
        }
    }

    ///求逆
    public static Real[][] inverse(Real[] info) {
        mat1 = Tool.input(info[0].toInt(), info[0].toInt());
        //不能使用原矩阵判断是否满秩，因为不能让原矩阵发生变化。因此先进行深复制
        Real[][] test = Tool.deepCopy(mat1);
        //调用求秩方法判断方阵是否满秩
        if (mat1.length != rankAlgorithm(test)) {
            System.out.println("方阵不满秩\n");
            return null;
        }
        //与方阵一起参与运算的单位矩阵，操作结束后即为结果
        Real[][] identity = Tool.getIdentity(mat1.length);
        boolean zero = true;
        //此循环将方阵变为上三角
        for (int dia = 0; dia < mat1.length; dia++) {
            Real[] tempRow, tempRow2;
            //判断对角线是否为0，为0则找下面的第一个非0行进行交换
            if (mat1[dia][dia].equals(Real.ZERO)) {
                int i;
                for (i = dia + 1; i < mat1.length; i++) {
                    if (!mat1[i][dia].equals(Real.ZERO)) {
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
            Tool.eliminateUpper(mat1, identity);
        }
        //最后将对角线数都化为1
        for (int i = 0; i < mat1.length; i++) {
            if (!mat1[i][i].equals(Real.ONE)) {
                Real ratio = Real.ONE.divide(mat1[i][i]);
                for (int j = 0; j < mat1.length; j++) {
                    mat1[i][j] = mat1[i][j].multiply(ratio);
                    identity[i][j] = identity[i][j].multiply(ratio);
                }
            }
        }
        return identity;
    }

    ///求特征值
    public static ArrayList<Real> eigenvalue(Real[][] mat) {
        if (mat.length != mat[0].length) throw new ArithmeticException("\n非方阵");
        int order = mat.length;
        if (order > 4) throw new ArithmeticException("\n最高支持4阶方阵，当前" + order + "阶");
        ArrayList<Real> result = new ArrayList<>();
        switch (order) {
            case 1 -> {
                result.add(mat[0][0]);
                return result;
            }
            case 2 -> {
                Real a = Real.ONE;
                Real b = mat[0][0].negate().subtract(mat[1][1]);
                Real c = (mat[0][0].multiply(mat[1][1])).subtract(mat[0][1].multiply(mat[1][0]));
                Real delta = b.power(2).subtract(new Real(4).multiply(a).multiply(c));
                Real x1 = (b.negate().add(delta.sqrt())).divide(new Real(2).multiply(a));
                Real x2 = (b.negate().subtract(delta.sqrt())).divide(new Real(2).multiply(a));
                result.add(x1);
                result.add(x2);
                return result;
            }
            case 3 -> //FIXME 没有立方根方法，写个锤子的三阶特征值
                    System.out.println("WIP");
            case 4 -> System.out.println("Work In Progress");
        }
        return null;
    }
}
