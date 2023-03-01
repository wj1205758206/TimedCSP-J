package common.classes.expressions;

import com.sun.prism.impl.Disposer;
import common.classes.expressions.expressionclass.*;
import common.classes.lts.ChannelQueue;
import common.ultility.Ultility;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.NoSuchElementException;

public final class EvaluatorDenotational {
    private static ExpressionValue evalPrimAppl(PrimitiveApplication application, ExpressionValue x1, Expression x2Exp, Valuation env) {
        try {
            String operator = application.Operator;
            switch (operator) {
                case "<": {
                    ExpressionValue expressionValue = EvaluatorDenotational.evaluate(x2Exp, env);
                    return new BoolConstant(((IntConstant) x1)._value < ((IntConstant) expressionValue)._value);
                }
                case "<=": {
                    ExpressionValue expressionValue = EvaluatorDenotational.evaluate(x2Exp, env);
                    return new BoolConstant(((IntConstant) x1)._value <= ((IntConstant) expressionValue)._value);
                }
                case ">": {
                    ExpressionValue expressionValue = EvaluatorDenotational.evaluate(x2Exp, env);
                    return new BoolConstant(((IntConstant) x1)._value > ((IntConstant) expressionValue)._value);
                }
                case ">=": {
                    ExpressionValue expressionValue = EvaluatorDenotational.evaluate(x2Exp, env);
                    return new BoolConstant(((IntConstant) x1)._value >= ((IntConstant) expressionValue)._value);
                }
                case "==": {
                    ExpressionValue expressionValue = EvaluatorDenotational.evaluate(x2Exp, env);
                    return new BoolConstant(x1.expressionID == expressionValue.expressionID);
                }
                case "!=": {
                    ExpressionValue expressionValue = EvaluatorDenotational.evaluate(x2Exp, env);
                    return new BoolConstant(x1.expressionID != expressionValue.expressionID);
                }
                case "&&":
                    if (((BoolConstant) x1)._value) {
                        ExpressionValue expressionValue = EvaluatorDenotational.evaluate(x2Exp, env);
                        return new BoolConstant(((BoolConstant) expressionValue)._value);
                    }
                    return new BoolConstant(false);
                case "||":
                    if (!((BoolConstant) x1)._value) {
                        ExpressionValue expressionValue = EvaluatorDenotational.evaluate(x2Exp, env);
                        return new BoolConstant(((BoolConstant) expressionValue)._value);
                    }
                    return new BoolConstant(true);
                case "xor": {
                    ExpressionValue expressionValue = EvaluatorDenotational.evaluate(x2Exp, env);
                    return new BoolConstant(((BoolConstant) x1)._value ^ ((BoolConstant) expressionValue)._value);
                }
                case "!":
                    return new BoolConstant(!((BoolConstant) x1)._value);
                case "+": {
                    ExpressionValue expressionValue = EvaluatorDenotational.evaluate(x2Exp, env);
                    return new IntConstant(((IntConstant) x1)._value + ((IntConstant) expressionValue)._value);
                }
                case "-": {
                    ExpressionValue expressionValue = EvaluatorDenotational.evaluate(x2Exp, env);
                    return new IntConstant(((IntConstant) x1)._value - ((IntConstant) expressionValue)._value);
                }
                case "*": {
                    ExpressionValue expressionValue = EvaluatorDenotational.evaluate(x2Exp, env);
                    return new IntConstant(((IntConstant) x1)._value * ((IntConstant) expressionValue)._value);
                }
                case "/": {
                    ExpressionValue expressionValue = EvaluatorDenotational.evaluate(x2Exp, env);
                    if (((IntConstant) expressionValue)._value == 0) {
                        throw new common.classes.expressions.expressionclass.ArithmeticException("Divide by Zero on " + application.toString());
                    }
                    return new IntConstant(((IntConstant) x1)._value / ((IntConstant) expressionValue)._value);
                }
                case "mod": {
                    ExpressionValue expressionValue = EvaluatorDenotational.evaluate(x2Exp, env);
                    if (((IntConstant) expressionValue)._value == 0) {
                        throw new common.classes.expressions.expressionclass.ArithmeticException("Modulo by Zero on " + application.toString());
                    }
                    int value = ((IntConstant) x1)._value;
                    int value2 = ((IntConstant) expressionValue)._value;
                    int num2 = value % value2;
                    return new IntConstant((num2 >= 0) ? num2 : (num2 + value2));
                }
                case ".": {
                    RecordValue recordValue = (RecordValue) x1;
                    ExpressionValue expressionValue = EvaluatorDenotational.evaluate(x2Exp, env);
                    int value3 = ((IntConstant) expressionValue)._value;
                    if (value3 < 0) {
                        throw new NegativeArraySizeException("Access negative index "
                                + value3
                                + " for variable "
                                + application.Argument1.toString()
                                + " in expression "
                                + application.toString());
                    }
                    if (value3 >= recordValue.associations.length) {
                        throw new IndexOutOfBoundsException("Index "
                                + value3
                                + " is out of range for variable "
                                + application.Argument1.toString()
                                + " in expression "
                                + application.toString());
                    }
                    return recordValue.associations[value3];
                }
                case "~":
                    return new IntConstant(-((IntConstant) x1)._value);
                case "<<": {
                    ExpressionValue expressionValue = EvaluatorDenotational.evaluate(x2Exp, env);
                    return new IntConstant(((IntConstant) x1)._value << ((IntConstant) expressionValue)._value);
                }
                case ">>": {
                    ExpressionValue expressionValue = EvaluatorDenotational.evaluate(x2Exp, env);
                    return new IntConstant(((IntConstant) x1)._value >> ((IntConstant) expressionValue)._value);
                }
                case "&": {
                    ExpressionValue expressionValue = EvaluatorDenotational.evaluate(x2Exp, env);
                    return new IntConstant(((IntConstant) x1)._value & ((IntConstant) expressionValue)._value);
                }
                case "^": {
                    ExpressionValue expressionValue = EvaluatorDenotational.evaluate(x2Exp, env);
                    return new IntConstant(((IntConstant) x1)._value ^ ((IntConstant) expressionValue)._value);
                }
                case "|": {
                    ExpressionValue expressionValue = EvaluatorDenotational.evaluate(x2Exp, env);
                    return new IntConstant(((IntConstant) x1)._value | ((IntConstant) expressionValue)._value);
                }
            }
        } catch (ClassCastException ex) {
            throw new RuntimeException("Invalid Cast Exception for " + application.toString() + ": " + ex.getMessage().replace("common.classes.expressions.expressionclass.", ""));
        }
        throw new RuntimeException("Invalid Primitive Operation: " + application.toString() + "!");
    }

    public static ExpressionValue evaluate(Expression exp, Valuation env) {
        switch (exp.expressionType) {
            case Constant:
                return (ExpressionValue) exp;
            case Variable:
                try {
                    return env.variables.getContainsKey(exp.expressionID);
                } catch (NoSuchElementException ex) {
                    throw new EvaluatingException("Access the non existing variable: " + exp.expressionID);
                } catch (Exception ex) {
                    throw new EvaluatingException("Variable evaluation exception for variable '" + exp.expressionID + "':" + ex.getMessage());
                }
                //break;
            case Record:
                break;
            case PrimitiveApplication: {
                PrimitiveApplication primitiveApplication = (PrimitiveApplication) exp;
                ExpressionValue expressionValue = EvaluatorDenotational.evaluate(primitiveApplication.Argument1, env);
                if (expressionValue == null) {
                    throw new RuntimeException("Invalid expression assignment: " + exp);
                }
                return EvaluatorDenotational.evalPrimAppl(primitiveApplication, expressionValue, primitiveApplication.Argument2, env);
            }
            case Assignment: {
                String leftHandSide = ((Assignment) exp).leftHandSide;
                Expression rightHandSide = ((Assignment) exp).rightHandSide;
                ExpressionValue expressionValue2 = EvaluatorDenotational.evaluate(rightHandSide, env);
                if (expressionValue2 == null) {
                    throw new RuntimeException("Invalid expression assignment: " + exp);
                }
                Valuation.checkVariableRange(leftHandSide, expressionValue2);
                env.variables.setValue(leftHandSide, expressionValue2);
                return expressionValue2;
            }
            case PropertyAssignment:
                try {
                    PropertyAssignment propertyAssignment = (PropertyAssignment) exp;
                    RecordValue recordValue = (RecordValue) EvaluatorDenotational.evaluate(propertyAssignment.recordExpression, env);
                    IntConstant intConstant = (IntConstant) EvaluatorDenotational.evaluate(propertyAssignment.propertyExpression, env);
                    ExpressionValue expressionValue3 = EvaluatorDenotational.evaluate(propertyAssignment.rightHandExpression, env);
                    int value = intConstant._value;
                    if (value < 0) {
                        throw new NegativeArraySizeException("Access negative index "
                                + value
                                + " for variable "
                                + propertyAssignment.recordExpression.toString());
                    }
                    if (value >= recordValue.associations.length) {
                        throw new IndexOutOfBoundsException("Index "
                                + value
                                + " is out of range for variable"
                                + propertyAssignment.recordExpression.toString());
                    }
                    if (expressionValue3 == null) {
                        throw new RuntimeException("Invalid expression assignment: " + exp);
                    }
                    Valuation.checkVariableRange(propertyAssignment.recordExpression.toString(), expressionValue3);
                    recordValue.associations[value] = expressionValue3;
                    recordValue.getID();
                    return expressionValue3;
                } catch (ClassCastException ex2) {
                    throw new RuntimeException("Invalid Cast Exception for "
                            + exp
                            + ":"
                            + ex2.getMessage().replace("common.classes.expression.expressionclass.", ""));
                }
                //goto IL_2F8;
                //goto_IL_2F8(exp, env);
            case If:
                //goto IL_2F8;
                goto_IL_2F8(exp, env);
            case Sequence: {
                Expression firstPart = ((Sequence) exp).firstPart;
                Expression secondPart = ((Sequence) exp).secondPart;
                EvaluatorDenotational.evaluate(firstPart, env);
                return EvaluatorDenotational.evaluate(secondPart, env);
            }
            case While: {
                Expression test = ((While) exp).test;
                Expression body = ((While) exp).body;
                if (((BoolConstant) EvaluatorDenotational.evaluate(test, env))._value) {
                    EvaluatorDenotational.evaluate(body, env);
                    return EvaluatorDenotational.evaluate(exp, env);
                }
                return null;
            }
            case Let:
                //goto IL_117F;
                goto_IL_117F(exp, env);
            case StaticMethodCall:
                try {
                    StaticMethodCall staticMethodCall = (StaticMethodCall) exp;
                    if (staticMethodCall.arguments.length > 0) {
                        String text = null;
                        if (staticMethodCall.arguments[0] instanceof Variable) {
                            text = ((Variable) staticMethodCall.arguments[0]).expressionID;
                        } else if (staticMethodCall.arguments[0] instanceof PrimitiveApplication) {
                            PrimitiveApplication primitiveApplication2 = (PrimitiveApplication) staticMethodCall.arguments[0];
                            ExpressionValue expressionValue4 = EvaluatorDenotational.evaluate(primitiveApplication2.Argument2, env);
                            text = primitiveApplication2.Argument1 + "[" + expressionValue4 + "]";
                        }
                        String methodName;
                        if ((methodName = staticMethodCall.methodName) != null) {
                            if (!(methodName == "cfull")) {
                                if (!(methodName == "cempty")) {
                                    if (!(methodName == "ccount")) {
                                        if (!(methodName == "csize")) {
                                            if (methodName == "cpeek") {
                                                ChannelQueue channelQueue = env.channels.getOrDefault(text, null);
                                                if (channelQueue == null) {
                                                    throw new RuntimeException("Channel "
                                                            + text
                                                            + " is not used in the model. Therefore it is meaningless to query channel information using "
                                                            + staticMethodCall
                                                            + ".");
                                                }
                                                if (channelQueue.size() == 0) {
                                                    throw new IndexOutOfBoundsException("Channel " + text + "'s buffer is empty!");
                                                }
                                                return new RecordValue(channelQueue.peek());
                                            }
                                        } else {
                                            ChannelQueue channelQueue = env.channels.getOrDefault(text, null);
                                            if (channelQueue != null) {
                                                return new IntConstant(channelQueue.size);
                                            }
                                            throw new RuntimeException("Channel "
                                                    + text
                                                    + " is not used in the model. Therefore it is meaningless to query channel information using "
                                                    + staticMethodCall
                                                    + ".");
                                        }
                                    } else {
                                        ChannelQueue channelQueue = env.channels.getOrDefault(text, null);
                                        if (channelQueue != null) {
                                            return new IntConstant(channelQueue.size());
                                        }
                                        throw new RuntimeException("Channel "
                                                + text
                                                + " is not used in the model. Therefore it is meaningless to query channel information using "
                                                + staticMethodCall
                                                + ".");
                                    }
                                } else {
                                    ChannelQueue channelQueue = env.channels.getOrDefault(text, null);
                                    if (channelQueue != null) {
                                        return new BoolConstant(channelQueue.isEmpty());
                                    }
                                    throw new RuntimeException("Channel "
                                            + text
                                            + " is not used in the model. Therefore it is meaningless to query channel information using "
                                            + staticMethodCall
                                            + ".");
                                }
                            } else {
                                ChannelQueue channelQueue = env.channels.getOrDefault(text, null);
                                if (channelQueue != null) {
                                    return new BoolConstant(channelQueue.isFull());
                                }
                                throw new RuntimeException("Channel "
                                        + text
                                        + " is not used in the model. Therefore it is meaningless to query channel information using "
                                        + staticMethodCall
                                        + ".");
                            }
                        }
                    }
                    Object[] array = new Object[staticMethodCall.arguments.length];
                    for (int i = 0; i < array.length; i++) {
                        ExpressionValue x = EvaluatorDenotational.evaluate(staticMethodCall.arguments[i], env);
                        array[i] = EvaluatorDenotational.getValueFromExpression(x);
                    }
                    String key = staticMethodCall.methodName + array.length;
                    if (!Ultility.javaMethods.containsKey(key)) {
                        throw new RuntimeException("Invalid Method Call: " + staticMethodCall + "! Make sure you have defined the method in the library.");
                    }
                    Object obj = Ultility.javaMethods.get(key).invoke(null, array);
                    if (Ultility.javaMethods.get(key).getReturnType().getName() == "void") {
                        return null;
                    }
                    if (obj instanceof Boolean) {
                        return new BoolConstant((Boolean) obj);
                    }
                    if (obj instanceof Integer || obj instanceof Short || obj instanceof Byte || obj instanceof Double) {
                        return new IntConstant(Integer.valueOf(obj.toString()));
                    }
                    if (obj instanceof int[]) {
                        int[] array2 = (int[]) obj;
                        ExpressionValue[] array3 = new ExpressionValue[array2.length];
                        for (int j = 0; j < array3.length; j++) {
                            array3[j] = new IntConstant(array2[j]);
                        }
                        return new RecordValue(array3);
                    }
                    if (obj instanceof ExpressionValue) {
                        return (ExpressionValue) obj;
                    }
                    return new NullConstant();
                } catch (InvocationTargetException ex3) {
                    if (ex3.getCause() != null) {
                        throw new RuntimeException("Exception happened at expression "
                                + exp
                                + ": "
                                + ex3.getCause().getMessage(), ex3.getCause());
                    }
                    throw new RuntimeException("Exception happened at expression "
                            + exp
                            + ": "
                            + ex3.getMessage());
                } catch (Exception ex4) {
                    throw new RuntimeException("Exception happened at expression "
                            + exp
                            + ": "
                            + ex4.getMessage());
                }
                //goto Block_12;
                //goto_Block_12(exp, env);
            case ClassMethodCall:
                //goto IL_914;
                goto_IL_914(exp, env);
            case NewObjectCreation:
                try {
                    NewObjectCreation newObjectCreation = (NewObjectCreation) exp;
                    Object[] array4 = new Object[newObjectCreation.arguments.length];
                    for (int k = 0; k < array4.length; k++) {
                        ExpressionValue x2 = EvaluatorDenotational.evaluate(newObjectCreation.arguments[k], env);
                        array4[k] = EvaluatorDenotational.getValueFromExpression(x2);
                    }
                    //Type type = Ultility.javaDataType.getOrDefault(newObjectCreation.className, null);
                    Class<?> type;
                    if (!Ultility.javaDataType.containsKey(newObjectCreation.className)) {
                        throw new RuntimeException("Invalid Object Creation: " + newObjectCreation + "! Make sure you have defined the class in the library.");
                    }
                    type = (Class<?>) Ultility.javaDataType.get(newObjectCreation.className);
                    Object obj2 = type.getConstructor().newInstance(array4);
                    //Object obj2 = Activator.CreateInstance(type, array4);
                    if (obj2 instanceof ExpressionValue) {
                        return (ExpressionValue) obj2;
                    }
                    throw new RuntimeException("Only object of class inheriting from ExpressionValue can be created. Please check your statement: " + newObjectCreation.toString() + ".");
                } catch (Exception ex5) {
                    throw new RuntimeException("Exception happened at expression "
                            + exp
                            + ": "
                            + ex5.getMessage());
                }
                //goto IL_1290;
                //goto_IL_1290(exp, env);
            case ClassMethodCallInstance:
                //goto IL_BD7;
                goto_IL_BD7(exp, env);
            case ClassProperty:
                //goto IL_E46;
                goto_IL_E46(exp, env);
            case ClassPropertyAssignment:
                //goto IL_1076;
                goto_IL_1076(exp, env);
            default:
                //goto IL_1290;
                goto_IL_1290(exp, env);
        }
        Expression[] associations = ((Record) exp).associations;
        ExpressionValue[] array5 = new ExpressionValue[associations.length];
        for (int l = 0; l < associations.length; l++) {
            array5[l] = EvaluatorDenotational.evaluate(associations[l], env);
            if (array5[l] == null) {
                throw new RuntimeException("Invalid expression assignment: " + exp);
            }
        }
        return new RecordValue(array5);
        /* ====================== goto IL_2f8 ======================
        IL_2F8:
        ExpressionValue expressionValue5 = EvaluatorDenotational.Evaluate(((If) exp).Condition, env);
        if (((BoolConstant) expressionValue5).Value) {
            return EvaluatorDenotational.Evaluate(((If) exp).ThenPart, env);
        }
        if (((If) exp).ElsePart != null) {
            return EvaluatorDenotational.Evaluate(((If) exp).ElsePart, env);
        }
        return null;
         */
        /* =========================== goto Block_12 ===============================
        Block_12:
        try {
            IL_914:
            ClassMethodCall classMethodCall = (ClassMethodCall) exp;
            ExpressionValue expressionValue6 = env.Variables[classMethodCall.Variable];
            if (expressionValue6 == null) {
                throw new RuntimeException(string.Concat(new object[]
                        {
                                "Exception happened at expression ",
                                exp,
                                ": variable ",
                                classMethodCall.Variable,
                                "'s value is null"
                        }));
            }
            object[] array6 = new object[classMethodCall.Arguments.Length];
            for (int m = 0; m < array6.Length; m++) {
                ExpressionValue x3 = EvaluatorDenotational.Evaluate(classMethodCall.Arguments[m], env);
                array6[m] = EvaluatorDenotational.GetValueFromExpression(x3);
            }
            MethodInfo method = expressionValue6.GetType().GetMethod(classMethodCall.MethodName);
            if (!(method != null)) {
                throw new RuntimeException("Invalid Method Call: " + classMethodCall + "! Make sure you have defined the method in the library.");
            }
            object obj3 = method.Invoke(expressionValue6, BindingFlags.InvokeMethod, null, array6, CultureInfo.InvariantCulture);
            if (method.ReturnType.Name == "Void") {
                return null;
            }
            if (obj3 is bool)
            {
                return new BoolConstant((bool) obj3);
            }
            if (obj3 is int ||obj3 is short ||obj3 is byte ||obj3 is double)
            {
                return new IntConstant(Convert.ToInt32(obj3));
            }
            if (obj3 is int[])
            {
                int[] array7 = obj3 as int[] ;
                ExpressionValue[] array8 = new ExpressionValue[array7.Length];
                for (int n = 0; n < array8.Length; n++) {
                    array8[n] = new IntConstant(array7[n]);
                }
                return new RecordValue(array8);
            }
            if (obj3 is ExpressionValue)
            {
                return obj3 as ExpressionValue;
            }
            if (obj3 == null) {
                return new NullConstant();
            }
            throw new RuntimeException("Call expression can only return int, short, byte, bool or int[] types. Please check your statement: " + classMethodCall.ToString() + ".");
        } catch (TargetInvocationException ex6) {
            if (ex6.InnerException != null) {
                throw new RuntimeException(string.Concat(new object[]
                        {
                                "Exception happened at expression ",
                                exp,
                                ": ",
                                ex6.InnerException.Message
                        })) {
                    InnerStackTrace =ex6.InnerException.StackTrace
                };
            }
            throw new RuntimeException(string.Concat(new object[]
                    {
                            "Exception happened at expression ",
                            exp,
                            ": ",
                            ex6.Message
                    }));
        } catch (Exception ex7) {
            throw new RuntimeException(string.Concat(new object[]
                    {
                            "Exception happened at expression ",
                            exp,
                            ": ",
                            ex7.Message
                    }));
        }
        try {
            IL_BD7:
            ClassMethodCallInstance classMethodCallInstance = (ClassMethodCallInstance) exp;
            ExpressionValue expressionValue7 = EvaluatorDenotational.Evaluate(classMethodCallInstance.Variable, env);
            object[] array9 = new object[classMethodCallInstance.Arguments.Length];
            for (int num = 0; num < array9.Length; num++) {
                ExpressionValue x4 = EvaluatorDenotational.Evaluate(classMethodCallInstance.Arguments[num], env);
                array9[num] = EvaluatorDenotational.GetValueFromExpression(x4);
            }
            MethodInfo method2 = expressionValue7.GetType().GetMethod(classMethodCallInstance.MethodName);
            if (!(method2 != null)) {
                throw new RuntimeException("Invalid Method Call: " + classMethodCallInstance + "! Make sure you have defined the method in the library.");
            }
            object obj4 = method2.Invoke(expressionValue7, array9);
            if (method2.ReturnType.Name == "Void") {
                return null;
            }
            if (obj4 is bool)
            {
                return new BoolConstant((bool) obj4);
            }
            if (obj4 is int ||obj4 is short ||obj4 is byte ||obj4 is double)
            {
                return new IntConstant(Convert.ToInt32(obj4));
            }
            if (obj4 is int[])
            {
                int[] array10 = obj4 as int[] ;
                ExpressionValue[] array11 = new ExpressionValue[array10.Length];
                for (int num2 = 0; num2 < array11.Length; num2++) {
                    array11[num2] = new IntConstant(array10[num2]);
                }
                return new RecordValue(array11);
            }
            if (obj4 is ExpressionValue)
            {
                return obj4 as ExpressionValue;
            }
            if (obj4 == null) {
                return new NullConstant();
            }
            throw new RuntimeException("Call expression can only return int, short, byte, bool or int[] types. Please check your statement: " + classMethodCallInstance.ToString() + ".");
        } catch (TargetInvocationException ex8) {
            if (ex8.InnerException != null) {
                throw new RuntimeException(string.Concat(new object[]
                        {
                                "Exception happened at expression ",
                                exp,
                                ": ",
                                ex8.InnerException.Message
                        })) {
                    InnerStackTrace =ex8.InnerException.StackTrace
                };
            }
            throw new RuntimeException(string.Concat(new object[]
                    {
                            "Exception happened at expression ",
                            exp,
                            ": ",
                            ex8.Message
                    }));
        } catch (Exception ex9) {
            throw new RuntimeException(string.Concat(new object[]
                    {
                            "Exception happened at expression ",
                            exp,
                            ": ",
                            ex9.Message
                    }));
        }
        try {
            IL_E46:
            ClassProperty classProperty = (ClassProperty) exp;
            ExpressionValue expressionValue8 = EvaluatorDenotational.Evaluate(classProperty.Variable, env);
            PropertyInfo property = expressionValue8.GetType().GetProperty(classProperty.PropertyName);
            object obj5 = null;
            if (property != null) {
                obj5 = property.GetValue(expressionValue8, null);
            } else {
                FieldInfo field = expressionValue8.GetType().GetField(classProperty.PropertyName);
                if (field != null) {
                    obj5 = field.GetValue(expressionValue8);
                }
            }
            if (obj5 == null) {
                throw new RuntimeException("Invalid Property Accessing: " + classProperty + "! Make sure you have defined the method in the library.");
            }
            if (obj5 is bool)
            {
                return new BoolConstant((bool) obj5);
            }
            if (obj5 is int ||obj5 is short ||obj5 is byte ||obj5 is double)
            {
                return new IntConstant(Convert.ToInt32(obj5));
            }
            if (obj5 is int[])
            {
                int[] array12 = obj5 as int[] ;
                ExpressionValue[] array13 = new ExpressionValue[array12.Length];
                for (int num3 = 0; num3 < array13.Length; num3++) {
                    array13[num3] = new IntConstant(array12[num3]);
                }
                return new RecordValue(array13);
            }
            if (obj5 is ExpressionValue)
            {
                return obj5 as ExpressionValue;
            }
            throw new RuntimeException("Call expression can only return int, short, byte, bool or int[] types. Please check your statement: " + classProperty.ToString() + ".");
        } catch (TargetInvocationException ex10) {
            if (ex10.InnerException != null) {
                throw new RuntimeException(string.Concat(new object[]
                        {
                                "Exception happened at expression ",
                                exp,
                                ": ",
                                ex10.InnerException.Message
                        })) {
                    InnerStackTrace =ex10.InnerException.StackTrace
                };
            }
            throw new RuntimeException(string.Concat(new object[]
                    {
                            "Exception happened at expression ",
                            exp,
                            ": ",
                            ex10.Message
                    }));
        } catch (Exception ex11) {
            throw new RuntimeException(string.Concat(new object[]
                    {
                            "Exception happened at expression ",
                            exp,
                            ": ",
                            ex11.Message
                    }));
        }
        try {
            IL_1076:
            ClassPropertyAssignment classPropertyAssignment = (ClassPropertyAssignment) exp;
            ExpressionValue expressionValue9 = EvaluatorDenotational.Evaluate(classPropertyAssignment.RightHandExpression, env);
            if (expressionValue9 == null) {
                throw new RuntimeException("Invalid expression assignment: " + exp);
            }
            ClassProperty classProperty2 = classPropertyAssignment.ClassProperty;
            ExpressionValue expressionValue10 = EvaluatorDenotational.Evaluate(classProperty2.Variable, env);
            PropertyInfo property2 = expressionValue10.GetType().GetProperty(classProperty2.PropertyName);
            if (property2 != null) {
                property2.SetValue(expressionValue10, EvaluatorDenotational.GetValueFromExpression(expressionValue9), null);
            } else {
                FieldInfo field2 = expressionValue10.GetType().GetField(classProperty2.PropertyName);
                if (!(field2 != null)) {
                    throw new RuntimeException("Invalid expression assignment: " + exp);
                }
                field2.SetValue(expressionValue10, EvaluatorDenotational.GetValueFromExpression(expressionValue9));
            }
            return expressionValue9;
        } catch (InvalidCastException ex12) {
            throw new RuntimeException(string.Concat(new object[]
                    {
                            "Invalid Cast Exception for ",
                            exp,
                            ": ",
                            ex12.Message.Replace("PAT.Common.Classes.Expressions.ExpressionClass.", "")
                    }));
        }
         */
        /* =========================== goto IL_117F ===============================
        IL_117F:
        LetDefinition letDefinition = exp as LetDefinition;
        ExpressionValue v = EvaluatorDenotational.Evaluate(letDefinition.RightHandExpression, env);
        env.ExtendDestructive(letDefinition.Variable, v);
        return null;
         */
        /*
        IL_1290:
        return new BoolConstant(true);
         */
    }

    public static Object getValueFromExpression(ExpressionValue x1) {
        if (x1 instanceof IntConstant) {
            return ((IntConstant) x1)._value;
        }
        if (x1 instanceof RecordValue) {
            ExpressionValue[] associations = ((RecordValue) x1).associations;
            int[] array = new int[associations.length];
            for (int i = 0; i < associations.length; i++) {
                array[i] = ((IntConstant) associations[i])._value;
            }
            return array;
        }
        if (x1 instanceof BoolConstant) {
            return ((BoolConstant) x1)._value;
        }
        if (x1 instanceof NullConstant) {
            return null;
        }
        return x1;
    }

    private static ExpressionValue goto_IL_1076(Expression exp, Valuation env) {
        try {
            //IL_1076:
            ClassPropertyAssignment classPropertyAssignment = (ClassPropertyAssignment) exp;
            ExpressionValue expressionValue9 = EvaluatorDenotational.evaluate(classPropertyAssignment.rightHandExpression, env);
            if (expressionValue9 == null) {
                throw new RuntimeException("Invalid expression assignment: " + exp);
            }
            ClassProperty classProperty2 = classPropertyAssignment.classProperty;
            ExpressionValue expressionValue10 = EvaluatorDenotational.evaluate(classProperty2.variable, env);
            //PropertyInfo property2 = expressionValue10.GetType().GetProperty(classProperty2.PropertyName);
            Field property2 = expressionValue10.getClass().getDeclaredField(classProperty2.propertyName);
            property2.setAccessible(true);
            if (property2 != null) {
                property2.set(expressionValue10, EvaluatorDenotational.getValueFromExpression(expressionValue9));
                //property2.SetValue(expressionValue10, EvaluatorDenotational.GetValueFromExpression(expressionValue9), null);
            } else {
                //FieldInfo field2 = expressionValue10.GetType().GetField(classProperty2.PropertyName);
                Field field2 = expressionValue10.getClass().getField(classProperty2.propertyName);
                if (!(field2 != null)) {
                    throw new RuntimeException("Invalid expression assignment: " + exp);
                }
                field2.set(expressionValue10, EvaluatorDenotational.getValueFromExpression(expressionValue9));
            }
            return expressionValue9;
        } catch (ClassCastException ex12) {
            throw new RuntimeException("Invalid Cast Exception for "
                    + exp
                    + ": "
                    + ex12.getMessage().replace("common.classes.expressions.expressionclass.", ""));
        } catch (NoSuchFieldException e) {
            throw new RuntimeException("NoSuchFieldException: " + e.getMessage());
        } catch (IllegalAccessException e) {
            throw new RuntimeException("IllegalAccessException: " + e.getMessage());
        }
    }

    private static ExpressionValue goto_IL_E46(Expression exp, Valuation env) {
        try {
            //IL_E46:
            ClassProperty classProperty = (ClassProperty) exp;
            ExpressionValue expressionValue8 = EvaluatorDenotational.evaluate(classProperty.variable, env);
            Field property = expressionValue8.getClass().getDeclaredField(classProperty.propertyName);
            property.setAccessible(true);
            Object obj5 = null;
            if (property != null) {
                obj5 = property.get(expressionValue8);
            } else {
                Field field = expressionValue8.getClass().getField(classProperty.propertyName);
                if (field != null) {
                    obj5 = field.get(expressionValue8);
                }
            }
            if (obj5 == null) {
                throw new RuntimeException("Invalid Property Accessing: " + classProperty + "! Make sure you have defined the method in the library.");
            }
            if (obj5 instanceof Boolean) {
                return new BoolConstant((Boolean) obj5);
            }
            if (obj5 instanceof Integer || obj5 instanceof Short || obj5 instanceof Byte || obj5 instanceof Double) {
                return new IntConstant(Integer.valueOf(obj5.toString()));
            }
            if (obj5 instanceof int[]) {
                int[] array12 = (int[]) obj5;
                ExpressionValue[] array13 = new ExpressionValue[array12.length];
                for (int num3 = 0; num3 < array13.length; num3++) {
                    array13[num3] = new IntConstant(array12[num3]);
                }
                return new RecordValue(array13);
            }
            if (obj5 instanceof ExpressionValue) {
                return (ExpressionValue) obj5;
            }
            throw new RuntimeException("Call expression can only return int, short, byte, bool or int[] types. Please check your statement: " + classProperty.toString() + ".");
        } catch (Exception ex11) {
            throw new RuntimeException("Exception happened at expression "
                    + exp
                    + ": "
                    + ex11.getMessage());
        }
    }

    private static ExpressionValue goto_IL_BD7(Expression exp, Valuation env) {
        try {
            //IL_BD7:
            ClassMethodCallInstance classMethodCallInstance = (ClassMethodCallInstance) exp;
            ExpressionValue expressionValue7 = EvaluatorDenotational.evaluate(classMethodCallInstance.variable, env);
            Object[] array9 = new Object[classMethodCallInstance.arguments.length];
            for (int num = 0; num < array9.length; num++) {
                ExpressionValue x4 = EvaluatorDenotational.evaluate(classMethodCallInstance.arguments[num], env);
                array9[num] = EvaluatorDenotational.getValueFromExpression(x4);
            }
            Method method2 = expressionValue7.getClass().getMethod(classMethodCallInstance.methodName);
            if (!(method2 != null)) {
                throw new RuntimeException("Invalid Method Call: " + classMethodCallInstance + "! Make sure you have defined the method in the library.");
            }
            Object obj4 = method2.invoke(expressionValue7, array9);
            if (method2.getReturnType().getName() == "void") {
                return null;
            }
            if (obj4 instanceof Boolean) {
                return new BoolConstant((Boolean) obj4);
            }
            if (obj4 instanceof Integer || obj4 instanceof Short || obj4 instanceof Byte || obj4 instanceof Double) {
                return new IntConstant(Integer.valueOf(obj4.toString()));
            }
            if (obj4 instanceof int[]) {
                int[] array10 = (int[]) obj4;
                ExpressionValue[] array11 = new ExpressionValue[array10.length];
                for (int num2 = 0; num2 < array11.length; num2++) {
                    array11[num2] = new IntConstant(array10[num2]);
                }
                return new RecordValue(array11);
            }
            if (obj4 instanceof ExpressionValue) {
                return (ExpressionValue) obj4;
            }
            if (obj4 == null) {
                return new NullConstant();
            }
            throw new RuntimeException("Call expression can only return int, short, byte, bool or int[] types. Please check your statement: " + classMethodCallInstance.toString() + ".");
        } catch (InvocationTargetException ex8) {
            if (ex8.getCause() != null) {
                throw new RuntimeException("Exception happened at expression "
                        + exp
                        + ": "
                        + ex8.getCause().getMessage(), ex8.getCause());
            }
            throw new RuntimeException("Exception happened at expression "
                    + exp
                    + ": "
                    + ex8.getMessage());
        } catch (Exception ex9) {
            throw new RuntimeException("Exception happened at expression "
                    + exp
                    + ": "
                    + ex9.getMessage());
        }
    }

    private static ExpressionValue goto_IL_1290(Expression exp, Valuation env) {
        return new BoolConstant(true);
    }

    /*
    private static ExpressionValue goto_Block_12(Expression exp, Valuation env) {
        //Block_12:
        try {
            //IL_914:
            ClassMethodCall classMethodCall = (ClassMethodCall) exp;
            ExpressionValue expressionValue6 = env.variables.getContainsKey(classMethodCall.variable);
            if (expressionValue6 == null) {
                throw new RuntimeException("Exception happened at expression "
                        + exp
                        + ": variable"
                        + classMethodCall.variable
                        + "'s value is null");
            }
            Object[] array6 = new Object[classMethodCall.arguments.length];
            for (int m = 0; m < array6.length; m++) {
                ExpressionValue x3 = EvaluatorDenotational.evaluate(classMethodCall.arguments[m], env);
                array6[m] = EvaluatorDenotational.getValueFromExpression(x3);
            }
            Method method = expressionValue6.getClass().getMethod(classMethodCall.methodName);
            if (!(method != null)) {
                throw new RuntimeException("Invalid Method Call: " + classMethodCall + "! Make sure you have defined the method in the library.");
            }
            Object obj3 = method.invoke(expressionValue6, array6);
            if (method.getReturnType().getName() == "void") {
                return null;
            }
            if (obj3 instanceof Boolean) {
                return new BoolConstant((Boolean) obj3);
            }
            if (obj3 instanceof Integer || obj3 instanceof Short || obj3 instanceof Byte || obj3 instanceof Double) {
                return new IntConstant(Integer.valueOf(obj3.toString()));
            }
            if (obj3 instanceof int[]) {
                int[] array7 = (int[]) obj3;
                ExpressionValue[] array8 = new ExpressionValue[array7.length];
                for (int n = 0; n < array8.length; n++) {
                    array8[n] = new IntConstant(array7[n]);
                }
                return new RecordValue(array8);
            }
            if (obj3 instanceof ExpressionValue) {
                return (ExpressionValue) obj3;
            }
            if (obj3 == null) {
                return new NullConstant();
            }
            throw new RuntimeException("Call expression can only return int, short, byte, bool or int[] types. Please check your statement: " + classMethodCall.toString() + ".");
        } catch (InvocationTargetException ex6) {
            if (ex6.getCause() != null) {
                throw new RuntimeException("Exception happened at expression "
                        + exp
                        + ": "
                        + ex6.getCause().getMessage(), ex6.getCause());
            }
            throw new RuntimeException("Exception happened at expression "
                    + exp
                    + ": "
                    + ex6.getMessage());
        } catch (Exception ex7) {
            throw new RuntimeException("Exception happened at expression "
                    + exp
                    + ": "
                    + ex7.getMessage());
        }
        try {
            //IL_BD7:
            ClassMethodCallInstance classMethodCallInstance = (ClassMethodCallInstance) exp;
            ExpressionValue expressionValue7 = EvaluatorDenotational.evaluate(classMethodCallInstance.variable, env);
            Object[] array9 = new Object[classMethodCallInstance.arguments.length];
            for (int num = 0; num < array9.length; num++) {
                ExpressionValue x4 = EvaluatorDenotational.evaluate(classMethodCallInstance.arguments[num], env);
                array9[num] = EvaluatorDenotational.getValueFromExpression(x4);
            }
            Method method2 = expressionValue7.getClass().getMethod(classMethodCallInstance.methodName);
            if (!(method2 != null)) {
                throw new RuntimeException("Invalid Method Call: " + classMethodCallInstance + "! Make sure you have defined the method in the library.");
            }
            Object obj4 = method2.invoke(expressionValue7, array9);
            if (method2.getReturnType().getName() == "void") {
                return null;
            }
            if (obj4 instanceof Boolean) {
                return new BoolConstant((Boolean) obj4);
            }
            if (obj4 instanceof Integer || obj4 instanceof Short || obj4 instanceof Byte || obj4 instanceof Double) {
                return new IntConstant(Integer.valueOf(obj4.toString()));
            }
            if (obj4 instanceof int[]) {
                int[] array10 = (int[]) obj4;
                ExpressionValue[] array11 = new ExpressionValue[array10.length];
                for (int num2 = 0; num2 < array11.length; num2++) {
                    array11[num2] = new IntConstant(array10[num2]);
                }
                return new RecordValue(array11);
            }
            if (obj4 instanceof ExpressionValue) {
                return (ExpressionValue) obj4;
            }
            if (obj4 == null) {
                return new NullConstant();
            }
            throw new RuntimeException("Call expression can only return int, short, byte, bool or int[] types. Please check your statement: " + classMethodCallInstance.toString() + ".");
        } catch (InvocationTargetException ex8) {
            if (ex8.getCause() != null) {
                throw new RuntimeException("Exception happened at expression "
                        + exp
                        + ": "
                        + ex8.getCause().getMessage(), ex8.getCause());
            }
            throw new RuntimeException("Exception happened at expression "
                    + exp
                    + ": "
                    + ex8.getMessage());
        } catch (Exception ex9) {
            throw new RuntimeException("Exception happened at expression "
                    + exp
                    + ": "
                    + ex9.getMessage());
        }
        try {
            //IL_E46:
            ClassProperty classProperty = (ClassProperty) exp;
            ExpressionValue expressionValue8 = EvaluatorDenotational.evaluate(classProperty.variable, env);
            Field property = expressionValue8.getClass().getDeclaredField(classProperty.propertyName);
            property.setAccessible(true);
            Object obj5 = null;
            if (property != null) {
                obj5 = property.get(expressionValue8);
            } else {
                Field field = expressionValue8.getClass().getField(classProperty.propertyName);
                if (field != null) {
                    obj5 = field.get(expressionValue8);
                }
            }
            if (obj5 == null) {
                throw new RuntimeException("Invalid Property Accessing: " + classProperty + "! Make sure you have defined the method in the library.");
            }
            if (obj5 instanceof Boolean) {
                return new BoolConstant((Boolean) obj5);
            }
            if (obj5 instanceof Integer || obj5 instanceof Short || obj5 instanceof Byte || obj5 instanceof Double) {
                return new IntConstant(Integer.valueOf(obj5.toString()));
            }
            if (obj5 instanceof int[]) {
                int[] array12 = (int[]) obj5;
                ExpressionValue[] array13 = new ExpressionValue[array12.length];
                for (int num3 = 0; num3 < array13.length; num3++) {
                    array13[num3] = new IntConstant(array12[num3]);
                }
                return new RecordValue(array13);
            }
            if (obj5 instanceof ExpressionValue) {
                return (ExpressionValue) obj5;
            }
            throw new RuntimeException("Call expression can only return int, short, byte, bool or int[] types. Please check your statement: " + classProperty.toString() + ".");
        } catch (Exception ex11) {
            throw new RuntimeException("Exception happened at expression "
                    + exp
                    + ": "
                    + ex11.getMessage());
        }
        try {
            //IL_1076:
            ClassPropertyAssignment classPropertyAssignment = (ClassPropertyAssignment) exp;
            ExpressionValue expressionValue9 = EvaluatorDenotational.evaluate(classPropertyAssignment.rightHandExpression, env);
            if (expressionValue9 == null) {
                throw new RuntimeException("Invalid expression assignment: " + exp);
            }
            ClassProperty classProperty2 = classPropertyAssignment.classProperty;
            ExpressionValue expressionValue10 = EvaluatorDenotational.evaluate(classProperty2.variable, env);
            //PropertyInfo property2 = expressionValue10.GetType().GetProperty(classProperty2.PropertyName);
            Field property2 = expressionValue10.getClass().getDeclaredField(classProperty2.propertyName);
            property2.setAccessible(true);
            if (property2 != null) {
                property2.set(expressionValue10, EvaluatorDenotational.getValueFromExpression(expressionValue9));
                //property2.SetValue(expressionValue10, EvaluatorDenotational.GetValueFromExpression(expressionValue9), null);
            } else {
                //FieldInfo field2 = expressionValue10.GetType().GetField(classProperty2.PropertyName);
                Field field2 = expressionValue10.getClass().getField(classProperty2.propertyName);
                if (!(field2 != null)) {
                    throw new RuntimeException("Invalid expression assignment: " + exp);
                }
                field2.set(expressionValue10, EvaluatorDenotational.getValueFromExpression(expressionValue9));
            }
            return expressionValue9;
        } catch (ClassCastException ex12) {
            throw new RuntimeException("Invalid Cast Exception for "
                    + exp
                    + ": "
                    + ex12.getMessage().replace("common.classes.expressions.expressionclass.", ""));
        } catch (NoSuchFieldException e) {
            throw new RuntimeException("NoSuchFieldException: " + e.getMessage());
        } catch (IllegalAccessException e) {
            throw new RuntimeException("AllegalAccessException: " + e.getMessage());
        }
    }

     */

    private static ExpressionValue goto_IL_117F(Expression exp, Valuation env) {
        LetDefinition letDefinition = (LetDefinition) exp;
        ExpressionValue v = EvaluatorDenotational.evaluate(letDefinition.rightHandExpression, env);
        env.extendDestructive(letDefinition.variable, v);
        return null;
    }

    // ============================ goto 转 function ============================
    private static ExpressionValue goto_IL_2F8(Expression exp, Valuation env) {
        ExpressionValue expressionValue5 = EvaluatorDenotational.evaluate(((If) exp).condition, env);
        if (((BoolConstant) expressionValue5)._value) {
            return EvaluatorDenotational.evaluate(((If) exp).thenPart, env);
        }
        if (((If) exp).elsePart != null) {
            return EvaluatorDenotational.evaluate(((If) exp).elsePart, env);
        }
        return null;
    }

    private static ExpressionValue goto_IL_914(Expression exp, Valuation env) {
        try {
            //IL_914:
            ClassMethodCall classMethodCall = (ClassMethodCall) exp;
            ExpressionValue expressionValue6 = env.variables.getContainsKey(classMethodCall.variable);
            if (expressionValue6 == null) {
                throw new RuntimeException("Exception happened at expression "
                        + exp
                        + ": variable"
                        + classMethodCall.variable
                        + "'s value is null");
            }
            Object[] array6 = new Object[classMethodCall.arguments.length];
            for (int m = 0; m < array6.length; m++) {
                ExpressionValue x3 = EvaluatorDenotational.evaluate(classMethodCall.arguments[m], env);
                array6[m] = EvaluatorDenotational.getValueFromExpression(x3);
            }
            Method method = expressionValue6.getClass().getMethod(classMethodCall.methodName);
            if (!(method != null)) {
                throw new RuntimeException("Invalid Method Call: " + classMethodCall + "! Make sure you have defined the method in the library.");
            }
            Object obj3 = method.invoke(expressionValue6, array6);
            if (method.getReturnType().getName() == "void") {
                return null;
            }
            if (obj3 instanceof Boolean) {
                return new BoolConstant((Boolean) obj3);
            }
            if (obj3 instanceof Integer || obj3 instanceof Short || obj3 instanceof Byte || obj3 instanceof Double) {
                return new IntConstant(Integer.valueOf(obj3.toString()));
            }
            if (obj3 instanceof int[]) {
                int[] array7 = (int[]) obj3;
                ExpressionValue[] array8 = new ExpressionValue[array7.length];
                for (int n = 0; n < array8.length; n++) {
                    array8[n] = new IntConstant(array7[n]);
                }
                return new RecordValue(array8);
            }
            if (obj3 instanceof ExpressionValue) {
                return (ExpressionValue) obj3;
            }
            if (obj3 == null) {
                return new NullConstant();
            }
            throw new RuntimeException("Call expression can only return int, short, byte, bool or int[] types. Please check your statement: " + classMethodCall.toString() + ".");
        } catch (InvocationTargetException ex6) {
            if (ex6.getCause() != null) {
                throw new RuntimeException("Exception happened at expression "
                        + exp
                        + ": "
                        + ex6.getCause().getMessage(), ex6.getCause());
            }
            throw new RuntimeException("Exception happened at expression "
                    + exp
                    + ": "
                    + ex6.getMessage());
        } catch (Exception ex7) {
            throw new RuntimeException("Exception happened at expression "
                    + exp
                    + ": "
                    + ex7.getMessage());
        }
    }
}
