public class Vec extends Console {
    ////向量计算器
    private Vec() {
    }

    ///加减法
    protected static Real[] add(int sign, Real[] info) {
        Real[][] vec1 = Tool.input(1, info[0].toInt());
        Real[][] vec2 = Tool.input(1, info[0].toInt());
        Real[] result = new Real[vec1[0].length];
        for (int i = 0; i < vec1.length; i++) result[i] = vec1[0][i].add(vec2[0][i].multiply(new Real(sign)));
        return result;
    }

    ///数乘
    protected static Real[] scalarMultiply(Real[] info) {
        Real[][] vec = Tool.input(1, info[1].toInt());
        Real k = info[0];
        Real[] result = new Real[vec[0].length];
        for (int i = 0; i < vec.length; i++) result[i] = vec[0][i].multiply(k);
        return result;
    }

    ///内积
    protected static Real innerProduct(Real[] info) {
        Real[][] vec1 = Tool.input(1, info[0].toInt());
        Real[][] vec2 = Tool.input(1, info[0].toInt());
        return inner(vec1, vec2);
    }

    ///内积算法核心
    private static Real inner(Real[][] vec1, Real[][] vec2) {
        Real result = Real.ZERO;
        for (int i = 0; i < vec1.length; i++) result = result.add(vec1[0][i].multiply(vec2[0][i]));
        return result;
    }

    ///模长
    protected static Real length(Real[] info) {
        Real[][] vec = Tool.input(1, info[0].toInt());
        return lengthAlgo(vec);
    }

    ///模长算法核心
    private static Real lengthAlgo(Real[][] vec) {
        return inner(vec, vec).sqrt();
    }

    ///夹角余弦值
    protected static Real angleCos(Real[] info) {
        Real[][] vec1 = Tool.input(1, info[0].toInt());
        Real[][] vec2 = Tool.input(1, info[0].toInt());
        return cos(vec1, vec2);
    }

    ///夹角余弦值算法核心
    private static Real cos(Real[][] vec1, Real[][] vec2) {
        return inner(vec1, vec2).divide(lengthAlgo(vec1).multiply(lengthAlgo(vec2)));
    }

    ///夹角正弦值
    protected static Real angleSin(Real[] info) {
        Real[][] vec1 = Tool.input(1, info[0].toInt());
        Real[][] vec2 = Tool.input(1, info[0].toInt());
        return sin(vec1, vec2);
    }

    ///夹角正弦值算法核心
    private static Real sin(Real[][] vec1, Real[][] vec2) {
        return Real.ONE.subtract(cos(vec1, vec2)).sqrt();
    }

    ///二维外积
    protected static Real outerProduct2D() {
        Real[][] vec1 = Tool.input(1, 2);
        Real[][] vec2 = Tool.input(1, 2);
        return outer2(vec1, vec2);
    }

    ///二维外积算法核心
    private static Real outer2(Real[][] vec1, Real[][] vec2) {
        return vec1[0][0].multiply(vec2[0][1]).subtract(vec1[0][1].multiply(vec2[0][0]));
    }

    ///三维外积
    protected static Real[] outerProduct3D() {
        Real[][] vec1 = Tool.input(1, 3);
        Real[][] vec2 = Tool.input(1, 3);
        return outer3(vec1, vec2);
    }

    ///三维外积算法核心
    private static Real[] outer3(Real[][] vec1, Real[][] vec2) {
        Real[] result = new Real[3];
        result[0] = vec1[0][1].multiply(vec2[0][2]).subtract(vec1[0][2].multiply(vec2[0][1]));
        result[1] = vec1[0][2].multiply(vec2[0][1]).subtract(vec1[0][0].multiply(vec2[0][2]));
        result[2] = vec1[0][0].multiply(vec2[0][1]).subtract(vec1[0][1].multiply(vec2[0][0]));
        return result;
    }

    ///混合积
    protected static Real mixedProduct() {
        Real[][] vec1 = Tool.input(1, 3);
        Real[][] vec2 = Tool.input(1, 3);
        Real[][] vec3 = Tool.input(1, 3);
        Real[][] cross = new Real[1][];
        cross[0] = outer3(vec1, vec2);
        return inner(cross, vec3);
    }
}
