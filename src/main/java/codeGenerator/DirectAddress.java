package codeGenerator;

public class DirectAddress extends Address{
    public DirectAddress(int num,varType varType) {
        this.num = num;
        this.varType = varType;
    }

    @Override
    public String toString() {
        return num+"";
    }
}
