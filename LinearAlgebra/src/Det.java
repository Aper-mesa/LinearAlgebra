////此程序用于计算行列式
public class Det {

    private Det() {
    }

    public static Real getValue(int order) {
        Real[][] det = Tool.input(order, order);
        //用户输入return返回时会是null
        if (det == null) return null;
        if (Tool.hasZeroRowOrColumn(det)) return Real.ZERO;
        //行列式的结果
        Real result = Real.ONE;
        //记录行列式是否因交换而需要变为原相反数
        boolean switched = false;
        boolean zero = true;
        for (int dia = 0; dia < order; dia++) {
            if (Tool.hasZeroRowOrColumn(det)) return Real.ZERO;
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
                    result = Real.ZERO;
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
        for (int i = 0; i < order; i++) result = result.multiply(det[i][i]);
        if (switched) result = result.negate();
        return result;
    }
}