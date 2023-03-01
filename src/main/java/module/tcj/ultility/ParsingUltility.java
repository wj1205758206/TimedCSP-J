package module.tcj.ultility;

import common.ParsingException;
import common.classes.expressions.Valuation;
import common.classes.expressions.expressionclass.*;
import org.antlr.v4.runtime.Token;

import java.lang.reflect.Method;
import java.util.Map;

public class ParsingUltility {
    public static void TestIsBooleanExpression(Expression p, Token ID1, String expression, Valuation valuation, Map<String, Expression> ConstantDatabase) throws ParsingException, NoSuchMethodException {
        if (ConstantDatabase.size() > 0) {
            p = p.clearConstant(ConstantDatabase);
        }
        if (p instanceof PrimitiveApplication) {
            String operator = ((PrimitiveApplication) p).Operator;
            if (operator == "+" || operator == "-" || operator == "*" || operator == "/" || operator == "~" || operator == "." || operator == "mod") {
                throw new ParsingException(
                        "The expression "
                                + p
                                + " "
                                + expression
                                + "must be a boolean expression!", ID1);
            }
            if (operator == "||" || operator == "&&" || operator == "!") {
                ParsingUltility.TestIsBooleanExpression(((PrimitiveApplication) p).Argument1, ID1, expression, valuation, ConstantDatabase);
                if (((PrimitiveApplication) p).Argument2 != null) {
                    ParsingUltility.TestIsBooleanExpression(((PrimitiveApplication) p).Argument2, ID1, expression, valuation, ConstantDatabase);
                    return;
                }
            } else if (operator == ">=" || operator == "<=" || operator == ">" || operator == "<") {
                ParsingUltility.TestIsIntExpression(((PrimitiveApplication) p).Argument1, ID1, expression, valuation, ConstantDatabase);
                if (((PrimitiveApplication) p).Argument2 != null) {
                    ParsingUltility.TestIsIntExpression(((PrimitiveApplication) p).Argument2, ID1, expression, valuation, ConstantDatabase);
                    return;
                }
            }
        } else if (p instanceof Variable) {
            if (valuation != null && valuation.variables != null && valuation.variables.containsKey(p.expressionID)
                    && !(valuation.variables.getContainsKey(p.expressionID) instanceof BoolConstant)) {
                throw new ParsingException(
                        String.format("The_static_method_call__0__must_return_a_boolean_value_", p + " " + expression), ID1);
            }
        } else if (p instanceof StaticMethodCall) {
            StaticMethodCall staticMethodCall = (StaticMethodCall) p;
            String methodName;
            if ((methodName = staticMethodCall.methodName) != null && (methodName == "cfull" || methodName == "cempty")) {
                return;
            }
            String key = staticMethodCall.methodName + staticMethodCall.arguments.length;
            if (!Ultility.JavaMethods.containsKey(key)) {
                throw new ParsingException(
                        String.format("The call {0} is not defined.", staticMethodCall.methodName), ID1);
            }
            if (Ultility.JavaMethods.get(key).getReturnType().getName() == "boolean") {
                return;
            }
            throw new ParsingException(
                    String.format("The_static_method_call__0__must_return_a_boolean_value_", p), ID1);
        } else if (p instanceof ClassMethodCall) {
            ClassMethodCall classMethodCall = (ClassMethodCall) p;
            if (!valuation.variables.containsKey(classMethodCall.variable)) {
                throw new ParsingException(
                        String.format("The call {0} may not be defined for variable {1}.", classMethodCall.methodName, classMethodCall.variable), ID1);
            }
            Method method = valuation.variables.getContainsKey(classMethodCall.variable).getClass().getMethod(classMethodCall.methodName);
            if (method != null && method.getReturnType().getName() == "Boolean") {
                return;
            }
            throw new ParsingException(
                    String.format("The_static_method_call__0__must_return_a_boolean_value_", p), ID1);
        } else {
            if (p instanceof Assignment) {
                Assignment assignment = (Assignment) p;
                ParsingUltility.TestIsBooleanExpression(assignment.rightHandSide, ID1, expression, valuation, ConstantDatabase);
                return;
            }
            if (p instanceof PropertyAssignment) {
                PropertyAssignment propertyAssignment = (PropertyAssignment) p;
                ParsingUltility.TestIsBooleanExpression(propertyAssignment.rightHandExpression, ID1, expression, valuation, ConstantDatabase);
                return;
            }
            if (!(p instanceof BoolConstant) && !(p instanceof If) && !(p instanceof While)) {
                throw new ParsingException(
                        String.format("The_expression__0__must_be_a_boolean_expression_", p + " " + expression), ID1);
            }
        }
    }

    public static void TestIsIntExpression(Expression p, Token ID1, String expression, Valuation valuation, Map<String, Expression> ConstantDatabase) throws ParsingException, NoSuchMethodException {
        if (ConstantDatabase.size() > 0) {
            p = p.clearConstant(ConstantDatabase);
        }
        if (p instanceof PrimitiveApplication) {
            String operator = ((PrimitiveApplication) p).Operator;
            if (operator == "||" || operator == "&&" || operator == "==" || operator == "!=" || operator == ">" ||
                    operator == "<" || operator == "!" || operator == ">=" || operator == "<=") {
                throw new ParsingException(
                        String.format("The_expression__0__must_be_an_integer_expression_", p + " " + expression), ID1);
            }
        } else if (p instanceof Variable) {
            if (valuation != null && valuation.variables != null && valuation.variables.containsKey(p.expressionID)
                    && !(valuation.variables.getContainsKey(p.expressionID)
                    instanceof IntConstant)) {
                throw new ParsingException(
                        String.format("The_variable__0__must_be_an_integer_variable_", p + " " + expression), ID1);
            }
        } else if (p instanceof StaticMethodCall) {
            StaticMethodCall staticMethodCall = (StaticMethodCall) p;
            String methodName;
            if ((methodName = staticMethodCall.methodName) != null && (methodName == "ccount" || methodName == "csize")) {
                return;
            }
            String key = staticMethodCall.methodName + staticMethodCall.arguments.length;
            if (!Ultility.JavaMethods.containsKey(key)) {
                throw new ParsingException(
                        String.format("The call {0} is not defined.", staticMethodCall.methodName), ID1);
            }
            if (Ultility.JavaMethods.get(key).getReturnType().getName() == "int") {
                return;
            }
            throw new ParsingException(
                    String.format("The_static_method_call__0__must_return_an_integer_value_", ID1.getText()), ID1);
        } else if (p instanceof ClassMethodCall) {
            ClassMethodCall classMethodCall = (ClassMethodCall) p;
            if (!valuation.variables.containsKey(classMethodCall.variable)) {
                throw new ParsingException(
                        String.format("The call {0} may not be defined for variable {1}.", classMethodCall.methodName, classMethodCall.variable), ID1);
            }
            Method method = valuation.variables.getContainsKey(classMethodCall.variable).getClass().getMethod(classMethodCall.methodName);
            if (method != null && method.getReturnType().getName() == "int") {
                return;
            }
            throw new ParsingException(
                    String.format("The_method_call__0__must_return_an_integer_value_", p), ID1);
        } else {
            if (p instanceof Assignment) {
                Assignment assignment = (Assignment) p;
                ParsingUltility.TestIsIntExpression(assignment.rightHandSide, ID1, expression, valuation, ConstantDatabase);
                return;
            }
            if (p instanceof PropertyAssignment) {
                PropertyAssignment propertyAssignment = (PropertyAssignment) p;
                ParsingUltility.TestIsIntExpression(propertyAssignment.rightHandExpression, ID1, expression, valuation, ConstantDatabase);
                return;
            }
            if (!(p instanceof IntConstant) && !(p instanceof ClassMethodCall) && !(p instanceof If) && !(p instanceof While)) {
                throw new ParsingException(
                        String.format("The_expression__0__must_be_an_integer_expression_", p + " " + expression), ID1);
            }
        }
    }

}
