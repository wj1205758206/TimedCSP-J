package common.classes.lts;

import common.ParsingException;
import common.classes.expressions.EvaluatorDenotational;
import common.classes.expressions.expressionclass.Expression;
import common.classes.expressions.expressionclass.ExpressionValue;
import common.classes.expressions.expressionclass.IntConstant;
import common.classes.ultility.Ultility;
import org.antlr.v4.runtime.Token;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ParallelDefinition {
    public String Parameter;


    public List<Expression> Domain;


    public Expression LowerBound;


    public Expression UpperBound;

    public List<Integer> DomainValues;


    public Token Token;

    public ParallelDefinition(String para, Token token) {
        this.Parameter = para;
        this.Domain = new ArrayList<>();
        this.DomainValues = new ArrayList<>();
        this.LowerBound = null;
        this.UpperBound = null;
        this.Token = token;
    }

    public boolean HasExternalLibraryAccess() {
        if (this.Domain != null) {
            for (int i = 0; i < this.Domain.size(); i++) {
                if (this.Domain.get(i).hasExternalLibraryCall()) {
                    return true;
                }
            }
        }
        return (this.LowerBound != null && this.LowerBound.hasExternalLibraryCall()) || (this.LowerBound != null && this.UpperBound.hasExternalLibraryCall());
    }

    @Override
    public String toString() {
        if (this.LowerBound == null) {
            String text = this.Parameter + ":{";
            for (int num : this.DomainValues) {
                text = text + num + ",";
            }
            text = text.substring(0, text.length() - 1);
            return text + "}";
        }
        return this.Parameter + ":{" + this.LowerBound + ".." + this.UpperBound + "}";
    }

    public ParallelDefinition ClearConstant(Map<String, Expression> constMapping) throws ParsingException {
        ParallelDefinition result = new ParallelDefinition(this.Parameter, this.Token);
        try {
            if (this.LowerBound == null) {
                for (Expression expression : this.Domain) {
                    Expression expression2 = expression.clearConstant(constMapping);
                    if (!expression2.hasVar) {
                        ExpressionValue expressionValue = EvaluatorDenotational.evaluate(expression2, null);
                        if (!(expressionValue instanceof IntConstant)) {
                            throw new ParsingException("An integer value is expected, but not " + expressionValue, this.Token);
                        }
                        result.Domain.add(expressionValue);
                        result.DomainValues.add(((IntConstant) expressionValue)._value);
                    } else {
                        result.Domain.add(expression2);
                    }
                }

//                using(List < Expression >.Enumerator enumerator = this.Domain.GetEnumerator())
//                {
//                    while (enumerator.MoveNext()) {
//                        Expression expression = enumerator.Current;
//                        Expression expression2 = expression.clearConstant(constMapping);
//                        if (!expression2.hasVar) {
//                            ExpressionValue expressionValue = EvaluatorDenotational.evaluate(expression2, null);
//                            if (!(expressionValue instanceof IntConstant)) {
//                                throw new ParsingException("An integer value is expected, but not " + expressionValue, this.Token);
//                            }
//                            result.Domain.add(expressionValue);
//                            result.DomainValues.add(((IntConstant) expressionValue)._value);
//                        } else {
//                            result.Domain.add(expression2);
//                        }
//                    }
//						goto IL_227;
//                }
            } else {
                result.LowerBound = this.LowerBound.clearConstant(constMapping);
                result.UpperBound = this.UpperBound.clearConstant(constMapping);
                if (!result.LowerBound.hasVar && !result.UpperBound.hasVar) {
                    ExpressionValue expressionValue2 = EvaluatorDenotational.evaluate(result.LowerBound, null);
                    ExpressionValue expressionValue3 = EvaluatorDenotational.evaluate(result.UpperBound, null);

                    if (!(expressionValue2 instanceof IntConstant) || !(expressionValue3 instanceof IntConstant)) {
                        throw new ParsingException("Integers value are expected, but not "
                                + expressionValue2
                                + " and "
                                + expressionValue3, this.Token);
                    }
                    int value = ((IntConstant) expressionValue2)._value;
                    int value2 = ((IntConstant) expressionValue3)._value;
                    if (value > value2) {
                        throw new ParsingException("The variable range's starting value "
                                + value
                                + " should be smaller than ending value"
                                + value2
                                + ".", this.Token);
                    }
                    result = new ParallelDefinition(this.Parameter, this.Token);
                    for (int i = value; i <= value2; i++) {
                        result.Domain.add(new IntConstant(i));
                        result.DomainValues.add(i);
                    }
                }
            }
            //  IL_227:
            //;
        } catch (Exception ex) {
            throw new ParsingException(ex.getMessage(), this.Token);
        }
        return result;
    }

    public List<String> GetGlobalVariables() {
        List<String> list = new ArrayList<>();
        if (this.LowerBound == null) {
            for (Expression expression : this.Domain) {
                Ultility.addList(list, expression.getVars());
            }
//            using (List<Expression>.Enumerator enumerator = this.Domain.GetEnumerator())
//            {
//                while (enumerator.MoveNext())
//                {
//                    Expression expression = enumerator.Current;
//                    Ultility.AddList<string>(list, expression.GetVars());
//                }
//                return list;
//            }
        }
        Ultility.addList(list, this.LowerBound.getVars());
        Ultility.addList(list, this.UpperBound.getVars());
        return list;
    }
}
