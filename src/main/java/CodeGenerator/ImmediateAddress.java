package CodeGenerator;

public class ImmediateAddress extends Address{
    public ImmediateAddress(int num,varType varType) {
        this.num = num;
        this.varType = varType;
    }

    @Override
    public String toString() {
        return "#"+num;
    }
}
