import java.util.Arrays;
import java.util.Scanner;

//此程序用于计算行列式
//2023.08.24-此类已过时，新行列式计算器已接入分数类。
public class DetLegacy {
    static Scanner input = new Scanner(System.in);
    //行列式的阶数
    static int order;
    //二维数组存储行列式
    static double[][] det;
    //行列式的结果
    static double result = 1;

    public static void main(String[] args) {
        //临时数组存储用户输入的行列式的某一行的所有元素
        String[] tempRowArr;
        System.out.println("输入行列式的阶数");
        order = Integer.parseInt(input.nextLine());
        det = new double[order][order];
        for (int i = 0; i < order; i++) {
            System.out.println("输入行列式第" + (i + 1) + "行的所有元素，元素之间用一个空格隔开");
            tempRowArr = input.nextLine().split(" ");
            for (int j = 0; j < order; j++) det[i][j] = Double.parseDouble(tempRowArr[j]);
        }
        if (zero()) result(0);
        compute();
    }

    ///打印行列式
    public static void print() {
        for (double[] row : det) System.out.println(Arrays.toString(row));
    }

    ///展示行列式计算结果，整数手动去掉小数点
    public static void result(double result) {
        if ((int) result == result) System.out.println("该" + order + "阶行列式的结果为" + (int) result);
        else System.out.println("该" + order + "阶行列式的结果为" + result);
        System.exit(0);
    }

    ///判断行列式是否存在元素全为0的某一行或某一列
    public static boolean zero() {
        boolean badOne = false;
        for (double[] row : det) {
            badOne = false;
            for (double e : row) {
                if (e != 0) {
                    badOne = true;
                    break;
                }
            }
            if (!badOne) return true;
        }
        for (int i = 0; i < det.length; i++) {
            badOne = false;
            for (double[] col : det) {
                if (col[i] != 0) {
                    badOne = true;
                    break;
                }
            }
            if (!badOne) return true;
        }
        return !badOne;
    }

    ///判断当前对角线数是否为零，为零则执行交换；若对角线数以下全为零，则行列式结果为0
    public static void compute() {
        //记录行列式是否因交换而需要变为原来的相反数
        boolean switched = false;
        boolean zero = true;
        for (int dia = 0; dia < order; dia++) {
            double[] tempRow;
            //判断对角线是否为0，为0则找下面的第一个非0行进行交换
            if (det[dia][dia] == 0) {
                int i;
                for (i = dia + 1; i < order; i++) {
                    if (det[i][dia] != 0) {
                        zero = false;
                        break;
                    }
                }
                if (zero) result(0);
                tempRow = det[dia];
                det[dia] = det[i];
                det[i] = tempRow;
                switched = !switched;
            }
            int i;
            //将对角线数以下的数字变为0
            for (int k = 0; k < order - dia - 1; k++) {
                boolean has = false;
                //找到第一个不是0的数字的行的索引
                for (i = dia + 1; i < order; i++) {
                    if (det[i][dia] != 0) {
                        has = true;
                        break;
                    }
                }
                if (has) {
                    //计算两行之间的比例
                    double ratio = (-det[i][dia] / det[dia][dia]);
                    //用临时数组存储乘以比例之后的对角线行
                    double[] temp = new double[order];
                    for (int j = 0; j < order; j++) temp[j] = ratio * det[dia][j];
                    //将临时数组中的数据依次加到要变成0的那一行中
                    for (int j = 0; j < order; j++) det[i][j] += temp[j];
                }
            }
        }
        print();
        //计算正对角线上的所有元素之积
        for (int i = 0; i < order; i++) result *= det[i][i];
        if (switched) result *= -1;
        result(result);
    }
}