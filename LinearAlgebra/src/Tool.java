import java.util.Arrays;

//此类提供各种工具和操作供各类使用
class Tool {
    ///二维数组深复制，仅需提供起点，自动新建相同大小的终点
    protected static Fraction[][] deepCopy(Fraction[][] origin) {
        Fraction[][] destination = new Fraction[origin.length][origin[0].length];
        for (int i = 0; i < destination.length; i++) destination[i] = Arrays.copyOf(origin[i], destination[i].length);
        return destination;
    }

    ///深复制的重载方法，舍弃最后若干列
    protected static Fraction[][] deepCopy(Fraction[][] origin, int newColumn) {
        Fraction[][] destination = new Fraction[origin.length][newColumn];
        for (int i = 0; i < destination.length; i++) destination[i] = Arrays.copyOf(origin[i], newColumn);
        return destination;
    }

    ///深复制的重载方法，舍弃最后若干行和若干列
    protected static Fraction[][] deepCopy(Fraction[][] origin, int newRow, int newColumn) {
        Fraction[][] destination = new Fraction[newRow][newColumn];
        for (int i = 0; i < destination.length; i++) destination[i] = Arrays.copyOf(origin[i], newColumn);
        return destination;
    }

    ///矩阵加K倍操作
    protected static void addK(Fraction[][] identity, boolean has, int i, int dia, Fraction[][] mat) {
        if (has) {
            //计算两行之间的比例
            Fraction ratio = mat[i][dia].negate().divide(mat[dia][dia]);
            //用临时数组存储乘以比例之后的对角线行
            Fraction[] temp = new Fraction[mat.length], temp2 = new Fraction[mat.length];
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
    protected static void addK(Fraction[] vector, boolean has, int i, int dia, Fraction[][] mat) {
        if (has) {
            //计算两行之间的比例
            Fraction ratio = mat[i][dia].negate().divide(mat[dia][dia]);
            //用临时数组存储乘以比例之后的对角线行
            Fraction[] tempRow = new Fraction[mat.length];
            Fraction tempElement = null;
            for (int j = 0; j < mat.length; j++) {
                tempRow[j] = ratio.multiply(mat[dia][j]);
                tempElement = ratio.multiply(vector[j]);
            }
            //将临时数组中的数据依次加到要变成0的那一行中
            for (int j = 0; j < mat.length; j++) {
                mat[i][j] = mat[i][j].add(tempRow[j]);
                vector[j] = vector[j].add(tempElement);
            }
        }
    }

    ///判断二维数组是否存在元素全为0的某一行或某一列
    protected static boolean hasZeroRowOrColumn(Fraction[][] input) {
        boolean badOne = false;
        for (Fraction[] row : input) {
            badOne = false;
            for (Fraction e : row) {
                if (!e.equals(Fraction.ZERO)) {
                    badOne = true;
                    break;
                }
            }
            if (!badOne) return true;
        }
        for (int i = 0; i < input.length; i++) {
            badOne = false;
            for (Fraction[] col : input) {
                if (!col[i].equals(Fraction.ZERO)) {
                    badOne = true;
                    break;
                }
            }
            if (!badOne) return true;
        }
        return !badOne;
    }
}
