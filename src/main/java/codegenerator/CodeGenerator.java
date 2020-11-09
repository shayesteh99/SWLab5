package codegenerator;

import logger.LogHandler;
import errorhandler.ErrorHandler;
import scanner.token.Token;
import semantic.symbol.Symbol;
import semantic.symbol.SymbolTable;
import semantic.symbol.SymbolType;

import java.util.Stack;

/**
 * Created by Alireza on 6/27/2015.
 */
public class CodeGenerator {
    private Memory memory = new Memory();
    private Stack<Address> ss = new Stack<Address>();
    private Stack<String> symbolStack = new Stack<>();
    private Stack<String> callStack = new Stack<>();
    private SymbolTable symbolTable;

    public CodeGenerator() {
        symbolTable = new SymbolTable(memory);
        //TODO
    }
    public void printMemory()
    {
        memory.pintCodeBlock();
    }
    public void semanticFunction(int func, Token next) {
        LogHandler.print("codegenerator : " + func);
        switch (func) {
            case 0:
                return;
            case 1:
                checkID();
                break;
            case 2:
                pid(next);
                break;
            case 3:
                fpid();
                break;
            case 4:
                kpid(next);
                break;
            case 5:
                intpid(next);
                break;
            case 6:
                startCall();
                break;
            case 7:
                call();
                break;
            case 8:
                arg();
                break;
            case 9:
                assign();
                break;
            case 10:
                primaryOpp(Operation.ADD);
                break;
            case 11:
                primaryOpp(Operation.SUB);
                break;
            case 12:
                primaryOpp(Operation.MULT);
                break;
            case 13:
                label();
                break;
            case 14:
                save();
                break;
            case 15:
                cgwhile();
                break;
            case 16:
                jpfSave();
                break;
            case 17:
                jpHere();
                break;
            case 18:
                print();
                break;
            case 19:
                equal();
                break;
            case 20:
                lessThan();
                break;
            case 21:
                and();
                break;
            case 22:
                not();
                break;
            case 23:
                defClass();
                break;
            case 24:
                defMethod();
                break;
            case 25:
                popClass();
                break;
            case 26:
                extend();
                break;
            case 27:
                defField();
                break;
            case 28:
                defVar();
                break;
            case 29:
                methodReturn();
                break;
            case 30:
                defParam();
                break;
            case 31:
                lastTypeBool();
                break;
            case 32:
                lastTypeInt();
                break;
            case 33:
                defMain();
                break;
        }
    }

    private void defMain() {
        //ss.pop();
        memory.add3AddressCode(ss.pop().num, Operation.JP, new DirectAddress(memory.getCurrentCodeBlockAddress(), VarType.Address), null, null);
        String methodName = "main";
        String className = symbolStack.pop();

        symbolTable.addMethod(className, methodName, memory.getCurrentCodeBlockAddress());

        symbolStack.push(className);
        symbolStack.push(methodName);
    }

    //    public void spid(Token next){
//        symbolStack.push(next.value);
//    }
    public void checkID() {
        symbolStack.pop();
        if (ss.peek().varType == VarType.Non) {
            //TODO : error
        }
    }

    public void pid(Token next) {
        if (symbolStack.size() > 1) {
            String methodName = symbolStack.pop();
            String className = symbolStack.pop();
            try {

                Symbol s = symbolTable.get(className, methodName, next.value);
                VarType t = VarType.valueOf(s.type.name());
                ss.push(new DirectAddress(s.address, t));


            } catch (Exception e) {
                ss.push(new DirectAddress(0, VarType.Non));
            }
            symbolStack.push(className);
            symbolStack.push(methodName);
        } else {
            ss.push(new DirectAddress(0, VarType.Non));
        }
        symbolStack.push(next.value);
    }

    public void fpid() {
        ss.pop();
        ss.pop();

        Symbol s = symbolTable.get(symbolStack.pop(), symbolStack.pop());
        VarType t = VarType.valueOf(s.type.name());
        ss.push(new DirectAddress(s.address, t));

    }

    public void kpid(Token next) {
        ss.push(symbolTable.get(next.value));
    }

    public void intpid(Token next) {
        ss.push(new ImmediateAddress(Integer.parseInt(next.value), VarType.Int));
    }

    public void startCall() {
        //TODO: method ok
        ss.pop();
        ss.pop();
        String methodName = symbolStack.pop();
        String className = symbolStack.pop();
        symbolTable.startCall(className, methodName);
        callStack.push(className);
        callStack.push(methodName);

        //symbolStack.push(methodName);
    }

    public void call() {
        //TODO: method ok
        String methodName = callStack.pop();
        String className = callStack.pop();
        try {
            symbolTable.getNextParam(className, methodName);
            ErrorHandler.getInstance().printError("The few argument pass for method");
        } catch (IndexOutOfBoundsException e) {
            System.out.println(e.getStackTrace());
        }
            SymbolType s = symbolTable.getMethodReturnType(className, methodName);
            VarType t = VarType.valueOf(s.name());
            Address temp = new DirectAddress(memory.getTemp(),t);
            ss.push(temp);
            memory.add3AddressCode(Operation.ASSIGN, new ImmediateAddress(temp.num, VarType.Address), new DirectAddress(symbolTable.getMethodReturnAddress(className, methodName), VarType.Address), null);
            memory.add3AddressCode(Operation.ASSIGN, new ImmediateAddress(memory.getCurrentCodeBlockAddress() + 2, VarType.Address), new DirectAddress(symbolTable.getMethodCallerAddress(className, methodName), VarType.Address), null);
            memory.add3AddressCode(Operation.JP, new DirectAddress(symbolTable.getMethodAddress(className, methodName), VarType.Address), null, null);

            //symbolStack.pop();


    }

    public void arg() {
        //TODO: method ok

        String methodName = callStack.pop();
//        String className = symbolStack.pop();
        try {
            Symbol s = symbolTable.getNextParam(callStack.peek(), methodName);
            VarType t = VarType.valueOf(s.type.name());
            Address param = ss.pop();
            if (param.varType != t) {
                ErrorHandler.getInstance().printError("The argument type isn't match");
            }
            memory.add3AddressCode(Operation.ASSIGN, param, new DirectAddress(s.address, t), null);

//        symbolStack.push(className);

        } catch (IndexOutOfBoundsException e) {
            ErrorHandler.getInstance().printError("Too many arguments pass for method");
        }
        callStack.push(methodName);

    }

    public void assign() {

            Address s1 = ss.pop();
            Address s2 = ss.pop();
//        try {
            if (s1.varType != s2.varType) {
                ErrorHandler.getInstance().printError("The type of operands in assign is different ");
            }
//        }catch (NullPointerException d)
//        {
//            d.printStackTrace();
//        }
            memory.add3AddressCode(Operation.ASSIGN, s1, s2, null);

    }
    public void primaryOpp(Operation opp) {
        Address temp = new DirectAddress(memory.getTemp(), VarType.Int);
        Address s2 = ss.pop();
        Address s1 = ss.pop();

        if (s1.varType != VarType.Int || s2.varType != VarType.Int) {
            ErrorHandler.getInstance().printError("In " + opp.name().toLowerCase() +  " two operands must be integer");
        }
        memory.add3AddressCode(opp, s1, s2, temp);
        ss.push(temp);
    }

    public void label() {
        ss.push(new DirectAddress(memory.getCurrentCodeBlockAddress(), VarType.Address));
    }

    public void save() {
        ss.push(new DirectAddress(memory.saveMemory(), VarType.Address));
    }

    public void cgwhile() {
        memory.add3AddressCode(ss.pop().num, Operation.JPF, ss.pop(), new DirectAddress(memory.getCurrentCodeBlockAddress() + 1, VarType.Address), null);
        memory.add3AddressCode(Operation.JP, ss.pop(), null, null);
    }

    public void jpfSave() {
        Address save = new DirectAddress(memory.saveMemory(), VarType.Address);
        memory.add3AddressCode(ss.pop().num, Operation.JPF, ss.pop(), new DirectAddress(memory.getCurrentCodeBlockAddress(), VarType.Address), null);
        ss.push(save);
    }

    public void jpHere() {
        memory.add3AddressCode(ss.pop().num, Operation.JP, new DirectAddress(memory.getCurrentCodeBlockAddress(), VarType.Address), null, null);
    }

    public void print() {
        memory.add3AddressCode(Operation.PRINT, ss.pop(), null, null);
    }

    public void equal() {
        Address temp = new DirectAddress(memory.getTemp(), VarType.Bool);
        Address s2 = ss.pop();
        Address s1 = ss.pop();
        if (s1.varType != s2.varType) {
            ErrorHandler.getInstance().printError("The type of operands in equal operator is different");
        }
        memory.add3AddressCode(Operation.EQ, s1, s2, temp);
        ss.push(temp);
    }

    public void lessThan() {
        Address temp = new DirectAddress(memory.getTemp(), VarType.Bool);
        Address s2 = ss.pop();
        Address s1 = ss.pop();
        if (s1.varType != VarType.Int || s2.varType != VarType.Int) {
            ErrorHandler.getInstance().printError("The type of operands in less than operator is different");
        }
        memory.add3AddressCode(Operation.LT, s1, s2, temp);
        ss.push(temp);
    }

    public void and() {
        Address temp = new DirectAddress(memory.getTemp(), VarType.Bool);
        Address s2 = ss.pop();
        Address s1 = ss.pop();
        if (s1.varType != VarType.Bool || s2.varType != VarType.Bool) {
            ErrorHandler.getInstance().printError("In and operator the operands must be boolean");
        }
        memory.add3AddressCode(Operation.AND, s1, s2, temp);
        ss.push(temp);

    }

    public void not() {
        Address temp = new DirectAddress(memory.getTemp(), VarType.Bool);
        Address s2 = ss.pop();
        Address s1 = ss.pop();
        if (s1.varType != VarType.Bool) {
            ErrorHandler.getInstance().printError("In not operator the operand must be boolean");
        }
        memory.add3AddressCode(Operation.NOT, s1, s2, temp);
        ss.push(temp);

    }

    public void defClass() {
        ss.pop();
        symbolTable.addClass(symbolStack.peek());
    }

    public void defMethod() {
        ss.pop();
        String methodName = symbolStack.pop();
        String className = symbolStack.pop();

        symbolTable.addMethod(className, methodName, memory.getCurrentCodeBlockAddress());

        symbolStack.push(className);
        symbolStack.push(methodName);

    }

    public void popClass() {
        symbolStack.pop();
    }

    public void extend() {
        ss.pop();
        symbolTable.setSuperClass(symbolStack.pop(), symbolStack.peek());
    }

    public void defField() {
        ss.pop();
        symbolTable.addField(symbolStack.pop(), symbolStack.peek());
    }

    public void defVar() {
        ss.pop();

        String var = symbolStack.pop();
        String methodName = symbolStack.pop();
        String className = symbolStack.pop();

        symbolTable.addMethodLocalVariable(className, methodName, var);

        symbolStack.push(className);
        symbolStack.push(methodName);
    }

    public void methodReturn() {
        //TODO : call ok

        String methodName = symbolStack.pop();
        Address s = ss.pop();
        SymbolType t = symbolTable.getMethodReturnType(symbolStack.peek(), methodName);
        VarType temp = VarType.valueOf(t.name());
        if (s.varType != temp) {
            ErrorHandler.getInstance().printError("The type of method and return address was not match");
        }
        memory.add3AddressCode(Operation.ASSIGN, s, new IndirectAddress(symbolTable.getMethodReturnAddress(symbolStack.peek(), methodName), VarType.Address), null);
        memory.add3AddressCode(Operation.JP, new DirectAddress(symbolTable.getMethodCallerAddress(symbolStack.peek(), methodName), VarType.Address), null, null);

        //symbolStack.pop();

    }

    public void defParam() {
        //TODO : call Ok
        ss.pop();
        String param = symbolStack.pop();
        String methodName = symbolStack.pop();
        String className = symbolStack.pop();

        symbolTable.addMethodParameter(className, methodName, param);

        symbolStack.push(className);
        symbolStack.push(methodName);
    }

    public void lastTypeBool() {
        symbolTable.setLastType(SymbolType.Bool);
    }

    public void lastTypeInt() {
        symbolTable.setLastType(SymbolType.Int);
    }

    public void main() {

    }

}
