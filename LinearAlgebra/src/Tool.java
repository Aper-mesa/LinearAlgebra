import java.util.Arrays;
import java.util.Scanner;

////此类提供各种工具和操作供各类使用
class Tool extends Console {
    private Tool() {
    }

    private static final Scanner input = new Scanner(System.in);

    ///二维数组深复制，仅需提供起点，自动新建相同大小的终点
    protected static Real[][] deepCopy(Real[][] origin) {
        Real[][] destination = new Real[origin.length][origin[0].length];
        for (int i = 0; i < destination.length; i++) destination[i] = Arrays.copyOf(origin[i], destination[i].length);
        return destination;
    }

    ///深复制的重载方法，舍弃最后若干列
    protected static Real[][] deepCopy(Real[][] origin, int newColumn) {
        Real[][] destination = new Real[origin.length][newColumn];
        for (int i = 0; i < destination.length; i++) destination[i] = Arrays.copyOf(origin[i], newColumn);
        return destination;
    }

    ///深复制的重载方法，舍弃最后若干行和若干列
    protected static Real[][] deepCopy(Real[][] origin, int newRow, int newColumn) {
        Real[][] destination = new Real[newRow][newColumn];
        for (int i = 0; i < destination.length; i++) destination[i] = Arrays.copyOf(origin[i], newColumn);
        return destination;
    }

    ///矩阵向量的加K倍操作
    protected static void addK(Real[][] identity, boolean has, int i, int dia, Real[][] mat) {
        if (has) {
            //计算两行之间的比例
            Real ratio = mat[i][dia].negate().divide(mat[dia][dia]);
            //用临时数组存储乘以比例之后的对角线行
            Real[] temp = new Real[mat.length], temp2 = new Real[mat.length];
            for (int j = 0; j < mat.length; j++) {
                temp[j] = ratio.multiply(mat[dia][j]);
                temp2[j] = ratio.multiply(identity[dia][j]);
            }
            //将临时数组中的数据依次加到要变成0的那一行中
            for (int j = 0; j < mat.length; j++) {
                mat[i][j] = mat[i][j].add(temp[j]);
                identity[i][j] = identity[i][j].add(temp2[j]);
            }
        }
    }

    ///向量加K倍操作
    protected static void addK(Real[] vector, boolean has, int i, int dia, Real[][] mat) {
        if (has) {
            //计算两行之间的比例
            Real ratio = mat[i][dia].negate().divide(mat[dia][dia]);
            //用临时数组存储乘以比例之后的对角线行
            Real[] tempRow = new Real[mat.length];
            Real tempElement;
            for (int j = 0; j < mat.length; j++) tempRow[j] = ratio.multiply(mat[dia][j]);
            tempElement = ratio.multiply(vector[dia]);
            //将临时数组中的数据依次加到要变成0的那一行中
            for (int j = 0; j < mat.length; j++) mat[i][j] = mat[i][j].add(tempRow[j]);
            vector[i] = vector[i].add(tempElement);
        }
    }

    ///判断二维数组是否存在元素全为0的某一行或某一列
    protected static boolean hasZeroRowOrColumn(Real[][] input) {
        boolean badOne = false;
        for (Real[] row : input) {
            badOne = false;
            for (Real e : row) {
                if (!e.equals(Real.ZERO)) {
                    badOne = true;
                    break;
                }
            }
            if (!badOne) return true;
        }
        for (int i = 0; i < input.length; i++) {
            badOne = false;
            for (Real[] col : input) {
                if (!col[i].equals(Real.ZERO)) {
                    badOne = true;
                    break;
                }
            }
            if (!badOne) return true;
        }
        return !badOne;
    }

    ///输入二维数组
    protected static Real[][] input(int row, int column) {
        Real[][] result = new Real[row][column];
        String[] tempRowArr;
        System.out.println(text.getString("inputByRow"));
        for (int i = 0; i < row; i++) {
            outer:
            while (true) {
                tempRowArr = input.nextLine().strip().replaceAll("\\s+", " ").split(" ");
                if (tempRowArr.length != column) {
                    System.out.println(text.getString("invalidCommand"));
                    continue;
                }
                for (String s : tempRowArr) {
                    if (notNumber(s)) {
                        System.out.println(text.getString("invalidCommand"));
                        continue outer;
                    }
                }
                break;
            }
            for (int j = 0; j < column; j++) result[i][j] = new Real(tempRowArr[j]);
        }
        return result;
    }

    //将一个矩阵对角线上的数字变为0，从下往上加
    protected static <T> void eliminateUpper(Real[][] reals, T identity) {
        for (int dia = reals.length - 1; dia >= 0; dia--) {
            int i;
            //将对角线数以上的数字变为0
            for (int k = 0; k < dia + 1; k++) {
                boolean has = false;
                //找到对角线数上第一个非零数字
                for (i = dia - 1; i >= 0; i--) {
                    if (!reals[i][dia].equals(Real.ZERO)) {
                        has = true;
                        break;
                    }
                }
                if (identity instanceof Real[][]) Tool.addK((Real[][]) identity, has, i, dia, reals);
                else Tool.addK((Real[]) identity, has, i, dia, reals);
            }
        }
    }

    ///返回一个n阶单位矩阵
    protected static Real[][] getIdentity(int order) {
        Real[][] identity = new Real[order][order];
        for (Real[] row : identity) Arrays.fill(row, Real.ZERO);
        for (int i = 0; i < identity.length; i++) identity[i][i] = Real.ONE;
        return identity;
    }

    ///打印矩阵
    protected static void print(Real[][] mat) {
        for (Real[] row : mat) System.out.println(Arrays.toString(row));
    }
}
