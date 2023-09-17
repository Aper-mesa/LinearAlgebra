import java.util.Scanner;

//此程序用于计算行列式
public class Det {

    private Det() {
    }

    public static Object[] getValue(Real[][] det) {
        Object[] result = new Object[2];
        result[1] = det;
        if (Tool.hasZeroRowOrColumn(det)) {
            result[0] = Real.ZERO;
            return result;
        }
        int order = det.length;
        //行列式的结果
        result[0] = Real.ONE;
        //记录行列式是否因交换而需要变为原相反数
        boolean switched = false;
        boolean zero = true;
        for (int dia = 0; dia < order; dia++) {
            Real[] tempRow;
            //判断对角线是否为0，为0则找下面的第一个非0行进行交换
            if (det[dia][dia].equals(Real.ZERO)) {
                int i;
                for (i = dia + 1; i < order; i++) {
                    if (!det[i][dia].equals(Real.ZERO)) {
                        zero = false;
                        break;
                    }
                }
                if (zero) {
                    result[0] = Real.ZERO;
                    return result;
                }
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
                    if (!det[i][dia].equals(Real.ZERO)) {
                        has = true;
                        break;
                    }
                }
                if (has) {
                    //计算两行之间的比例
                    Real ratio = det[i][dia].negate().divide(det[dia][dia]);
                    //用临时数组存储乘以比例之后的对角线行
                    Real[] tempArr = new Real[order];
                    for (int j = 0; j < order; j++) {
                        tempArr[j] = det[dia][j].multiply(ratio);
                    }
                    //将临时数组中的数据依次加到要变成0的那一行中
                    for (int j = 0; j < order; j++) det[i][j] = det[i][j].add(tempArr[j]);
                }
            }
        }
        //计算正对角线上的所有元素之积
        for (int i = 0; i < order; i++) result[0] = ((Real) result[0]).multiply(det[i][i]);
        if (switched) ((Real) result[0]).negate();
        return result;
    }

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        //行列式的阶数
        int order;
        //二维数组存储行列式
        Real[][] det;
        //临时数组存储用户输入的行列式的某一行的所有元素
        System.out.println("输入行列式的阶数");
        order = Integer.parseInt(input.nextLine());
        det = Tool.input(order, order);
        System.out.println("结果为" + Det.getValue(det)[0]);
    }
}