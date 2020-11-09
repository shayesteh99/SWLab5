package codegenerator;

public class ImmediateAddress extends Address{
    public ImmediateAddress(int num, VarType varType) {
        this.num = num;
        this.varType = varType;
    }

    @Override
    public String toString() {
        return "#"+num;
    }
}
