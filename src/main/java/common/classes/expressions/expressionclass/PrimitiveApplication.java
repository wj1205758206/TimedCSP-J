package common.classes.expressions.expressionclass;

import common.classes.ultility.Ultility;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PrimitiveApplication extends Expression {

    public final String AND = "&&";


    public final
    String OR = "||";


    public final
    String NOT = "!";


    public final
    String IMPLIES = "=>";


    public final
    String PLUS = "+";


    public final
    String MINUS = "-";


    public final
    String TIMES = "*";


    public final
    String DIVIDE = "/";


    public final
    String MOD = "mod";

    public final
    String NEGATIVE = "~";

    public final
    String EQUAL = "==";

    public final
    String NOT_EQUAL = "!=";

    public final
    String GREATER = ">";

    public final
    String GREATER_EQUAL = ">=";


    public final
    String LESS = "<";

    public final
    String LESS_EQUAL = "<=";

    public final
    String ARRAY = ".";

    public final
    String ARRAYPRIME = ".'";

    public final
    String MIN = "Min";

    public final String MAX = "Max";

    public String Operator;


    public Expression Argument1;

    public Expression Argument2;

    public PrimitiveApplication(String op, Expression a1) {
        this.expressionType = ExpressionType.PrimitiveApplication;
        this.Operator = op;
        this.Argument1 = a1;
        this.hasVar = a1.hasVar;
        this.expressionID = this.Operator + this.Argument1.expressionID;
    }

    public PrimitiveApplication(String op, Expression a1, Expression a2) {
        this.expressionType = ExpressionType.PrimitiveApplication;
        this.Operator = op;
        this.Argument1 = a1;
        this.Argument2 = a2;
        this.hasVar = (a1.hasVar || a2.hasVar);
        if (this.Operator == "mod") {
            this.expressionID = this.Argument1.expressionID + "%" + this.Argument2.expressionID;
            return;
        }
        this.expressionID = this.Argument1.expressionID + this.Operator + this.Argument2.expressionID;
    }

    @Override
    public String toString() {
        if (this.Argument2 == null) {
            if (this.Operator == "~") {
                return "-(" + this.Argument1 + ")";
            }
            return "(" + this.Argument1 + " % " + this.Argument2 + ")";
        } else {
            if (this.Operator == ".") {
                return this.Argument1 + "[" + this.Argument2 + "]";
            }
            if (this.Operator == "mod") {
                return "(" + this.Argument1 + " % " + this.Argument2 + ")";

            }
            return "(" + this.Argument1 + " " + this.Operator + " " + this.Argument2 + ")";
        }
    }

    @Override
    public List<String> getVars() {
        if (this.Operator == ".") {
            List<String> list = new ArrayList<>(16);
            list.add(this.Argument1.toString());
            if (this.Argument2 != null) {
                Ultility.addList(list, this.Argument2.getVars());
            }
            return list;
        }
        List<String> vars = this.Argument1.getVars();
        if (this.Argument2 != null) {
            Ultility.addList(vars, this.Argument2.getVars());
        }
        return vars;
    }

    @Override
    public Expression clearConstant(Map<String, Expression> constMapping) {
        if (this.Argument2 != null) {
            return new PrimitiveApplication(this.Operator, this.Argument1.clearConstant(constMapping), this.Argument2.clearConstant(constMapping));
        }
        return new PrimitiveApplication(this.Operator, this.Argument1.clearConstant(constMapping));
    }

    public boolean isBinaryExpression() {
        return this.Argument2 != null;
    }

    public boolean isBinaryLogicalExpression() {
        return this.Operator == "&&" || this.Operator == "||";
    }

    public boolean isBinaryNumericalExpression() {
        return this.Operator == "+" || this.Operator == "-" || this.Operator == "*" || this.Operator == "/" || this.Operator == "mod" || this.Operator == "Min" || this.Operator == "Max";
    }

    public boolean isRelationalExpression() {
        return this.Operator == "==" || this.Operator == "!=" || this.Operator == ">" || this.Operator == ">=" || this.Operator == "<" || this.Operator == "<=";
    }

    public boolean isArrayExpression() {
        return this.Operator == "." || this.Operator == ".'";
    }


    public boolean isBooleanExpression() {
        return this.isBinaryLogicalExpression() || this.isRelationalExpression();
    }

    @Override
    public boolean hasExternalLibraryCall() {
        if (this.isBinaryExpression()) {
            return this.Argument1.hasExternalLibraryCall() || this.Argument2.hasExternalLibraryCall();
        }
        return this.Argument1.hasExternalLibraryCall();
    }

}
