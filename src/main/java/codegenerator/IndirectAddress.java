package codegenerator;

public class IndirectAddress extends Address{
    public IndirectAddress(int num, VarType varType) {
        this.num = num;
        this.varType = varType;
    }

    @Override
    public String toString() {
        return "@"+num;
    }
}
