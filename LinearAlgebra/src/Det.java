import java.util.Arrays;
import java.util.Scanner;

//此程序用于计算行列式
public class Det {

    private Det() {
    }

    public static Fraction getValue(Fraction[][] det) {
        if (zero(det)) return Fraction.ZERO;
        int order = det.length;
        //行列式的结果
        Fraction result = Fraction.ONE;
        //记录行列式是否因交换而需要变为原来的相反数
        boolean switched = false;
        boolean zero = true;
        for (int dia = 0; dia < order; dia++) {
            Fraction[] tempRow;
            //判断对角线是否为0，为0则找下面的第一个非0行进行交换
            if (det[dia][dia].equals(Fraction.ZERO)) {
                int i;
                for (i = dia + 1; i < order; i++) {
                    if (!det[i][dia].equals(Fraction.ZERO)) {
                        zero = false;
                        break;
                    }
                }
                if (zero) return Fraction.ZERO;
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
                    if (!det[i][dia].equals(Fraction.ZERO)) {
                        has = true;
                        break;
                    }
                }
                if (has) {
                    //计算两行之间的比例
                    Fraction tempFraction = new Fraction(det[i][dia].toDecimalString());
                    Fraction ratio = tempFraction.negate().divide(det[dia][dia]);
                    //用临时数组存储乘以比例之后的对角线行
                    Fraction[] tempArr = new Fraction[order];
                    for (int j = 0; j < order; j++) {
                        Fraction temp = new Fraction(det[dia][j].toDecimalString());
                        tempArr[j] = temp.multiply(ratio);
                    }
                    //将临时数组中的数据依次加到要变成0的那一行中
                    for (int j = 0; j < order; j++) det[i][j] = det[i][j].add(tempArr[j]);
                }
            }
        }
        //计算正对角线上的所有元素之积
        for (int i = 0; i < order; i++) result = result.multiply(det[i][i]);
        if (switched) result.negate();
        return result;
    }

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        //行列式的阶数
        int order;
        //二维数组存储行列式
        Fraction[][] det;
        //临时数组存储用户输入的行列式的某一行的所有元素
        String[] tempRowArr;
        System.out.println("输入行列式的阶数");
        order = Integer.parseInt(input.nextLine());
        det = new Fraction[order][order];
        for (int i = 0; i < order; i++) {
            System.out.println("输入行列式第" + (i + 1) + "行的所有元素，元素之间用一个空格隔开");
            tempRowArr = input.nextLine().split(" ");
            for (int j = 0; j < order; j++) det[i][j] = new Fraction(tempRowArr[j]);
        }
        System.out.println("该" + order + "阶行列式的结果为" + Det.getValue(det));
    }

    ///判断行列式是否存在元素全为0的某一行或某一列
    public static boolean zero(Fraction[][] det) {
        boolean badOne = false;
        for (Fraction[] row : det) {
            badOne = false;
            for (Fraction e : row) {
                if (!e.equals(Fraction.ZERO)) {
                    badOne = true;
                    break;
                }
            }
            if (!badOne) return true;
        }
        for (int i = 0; i < det.length; i++) {
            badOne = false;
            for (Fraction[] col : det) {
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