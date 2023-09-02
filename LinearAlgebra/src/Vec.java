//此程序用于向量的相关计算
/*
1. 加减法
2. 数乘
3. 内积
4. 外积
5. 模长
6. 夹角
7. 混合积
 */
public class Vec {
    // 向量维数，自初始化起不可改变
    public final int dimension;
    public Fraction[] data;

    // 传入
    public Vec(int dimension, Fraction[] data) {
        this.dimension = dimension;
        this.data = data;
    }

    // 向量加
    public static Vec add(Vec a, Vec b){
        // 判断向量维数是否相同
        if(a.dimension != b.dimension) throw new IllegalArgumentException("向量维数不同");
        Fraction[] data = new Fraction[a.dimension];
        for(int i = 0; i < a.dimension; i++){
            data[i] = a.data[i].add(b.data[i]);
        }
        return new Vec(a.dimension, data);
    }

    public Vec add(Vec b){
        return add(this, b);
    }

    // 向量减
    public static Vec subtract(Vec a, Vec b){
        // 判断向量维数是否相同
        if(a.dimension != b.dimension) throw new IllegalArgumentException("向量维数不同");
        Fraction[] data = new Fraction[a.dimension];
        for(int i = 0; i < a.dimension; i++){
            data[i] = a.data[i].subtract(b.data[i]);
        }
        return new Vec(a.dimension, data);
    }

    public Vec subtract(Vec b){
        return subtract(this, b);
    }

    // 数乘
    public static Vec multiply(Fraction a, Vec b){
        Fraction[] data = new Fraction[b.dimension];
        for(int i = 0; i < b.dimension; i++){
            data[i] = a.multiply(b.data[i]);
        }
        return new Vec(b.dimension, data);
    }

    public Vec multiply(Fraction a){
        return multiply(a, this);
    }

    // 内积
    public static Fraction dot(Vec a, Vec b){
        // 判断向量维数是否相同
        if(a.dimension != b.dimension) throw new IllegalArgumentException("向量维数不同");
        Fraction result = Fraction.ZERO;
        for(int i = 0; i < a.dimension; i++){
            result = result.add(a.data[i].multiply(b.data[i]));
        }
        return result;
    }

    public Fraction dot(Vec b){
        return dot(this, b);
    }

    // 外积
    public static Vec cross(Vec a, Vec b){
        // 判断向量维数是否相同
        if(a.dimension != b.dimension) throw new IllegalArgumentException("向量维数不同");
        if(a.dimension != 3) throw new IllegalArgumentException("外积只能用于三维向量");
        Fraction[] data = new Fraction[3];
        data[0] = a.data[1].multiply(b.data[2]).subtract(a.data[2].multiply(b.data[1]));
        data[1] = a.data[2].multiply(b.data[0]).subtract(a.data[0].multiply(b.data[2]));
        data[2] = a.data[0].multiply(b.data[1]).subtract(a.data[1].multiply(b.data[0]));
        return new Vec(3, data);
    }

    public Vec cross(Vec b){
        return cross(this, b);
    }

    // 模长
    public static Fraction length(Vec a){
        Fraction result = Fraction.ZERO;
        for(int i = 0; i < a.dimension; i++){
            result = result.add(a.data[i].multiply(a.data[i]));
        }
        // TODO: 等待Fraction 乘方
        return result;
    }

    public Fraction length(){
        return length(this);
    }

    // 夹角
    public static Fraction angle(Vec a, Vec b){
        // 判断向量维数是否相同
        if(a.dimension != b.dimension) throw new IllegalArgumentException("向量维数不同");
        return dot(a, b).divide(length(a).multiply(length(b)));
    }

    public Fraction angle(Vec b){
        return angle(this, b);
    }

    // 混合积
    public static Fraction mixed(Vec a, Vec b, Vec c){
        // 判断向量维数是否相同
        if(a.dimension != b.dimension || a.dimension != c.dimension) throw new IllegalArgumentException("向量维数不同");
        return dot(a, cross(b, c));
    }

    public Fraction mixed(Vec b, Vec c){
        return mixed(this, b, c);
    }

    // 打印向量信息
    public String toString(){
        return """
                向量维数： %d
                向量数据： %s
                """.formatted(dimension, data);
    }

    // 复制向量
    public Vec copy(){
        return new Vec(dimension, data);
    }

    // 获得向量维数
    public int getDimension(){
        return dimension;
    }

    // 获得向量数据
    public Fraction[] getData(){
        return data;
    }
}
