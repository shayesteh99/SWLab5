package codeGenerator;

public class IndirectAddress extends Address{
    public IndirectAddress(int num,varType varType) {
        this.num = num;
        this.varType = varType;
    }

    @Override
    public String toString() {
        return "@"+num;
    }
}
