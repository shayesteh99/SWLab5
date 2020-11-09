package codegenerator;

public class DirectAddress extends Address{
    public DirectAddress(int num, VarType varType) {
        this.num = num;
        this.varType = varType;
    }

    @Override
    public String toString() {
        return num+"";
    }
}
