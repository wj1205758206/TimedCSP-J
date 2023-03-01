package common.classes.lts;

import common.classes.expressions.EvaluatorDenotational;
import common.classes.expressions.Valuation;
import common.classes.expressions.expressionclass.Expression;
import common.classes.expressions.expressionclass.ExpressionValue;
import common.classes.ultility.Ultility;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Event {

    public String BaseName;


    public Expression[] ExpressionList;


    public String EventID;


    public String EventName;

    public Event(String name) {
        this.BaseName = name;
    }

    public boolean ContainsVariable() {
        if (this.EventID == null) {
            for (Expression expression : this.ExpressionList) {
                if (expression.hasVar) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public String toString() {
        if (this.EventID == null) {
            return this.BaseName + Ultility.PPStringListDot(this.ExpressionList);
        }
        return this.EventID;
    }

    public boolean hasExternalLibraryCall() {
        if (this.ExpressionList != null) {
            for (int i = 0; i < this.ExpressionList.length; i++) {
                if (this.ExpressionList[i].hasExternalLibraryCall()) {
                    return true;
                }
            }
        }
        return false;
    }

    public String GetID() {
        if (this.EventID == null) {
            return this.BaseName + Ultility.pPIDListDot(this.ExpressionList);
        }
        return this.EventID;
    }

    public String GetEventID(Valuation global) {
        if (this.EventID != null) {
            return this.EventID;
        }
        if (this.ExpressionList != null && this.ExpressionList.length > 0) {
            String text = this.BaseName;
            for (int i = 0; i < this.ExpressionList.length; i++) {
                ExpressionValue expressionValue = EvaluatorDenotational.evaluate(this.ExpressionList[i], global);
                text = text + "." + expressionValue.expressionID;
            }
            return text;
        }
        return this.BaseName;
    }

    public String GetEventName(Valuation global) {
        if (this.EventName != null) {
            return this.EventName;
        }
        if (this.ExpressionList != null && this.ExpressionList.length > 0) {
            String text = this.BaseName;
            for (int i = 0; i < this.ExpressionList.length; i++) {
                ExpressionValue expressionValue = EvaluatorDenotational.evaluate(this.ExpressionList[i], global);
                text = text + "." + expressionValue.toString();
            }
            return text;
        }
        return this.BaseName;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Event)) {
            return false;
        }
        return this.GetID() == ((Event) obj).GetID();
    }

    @Override
    public int hashCode() {
        return this.GetID().hashCode();
    }

    public Event ClearConstant(Map<String, Expression> constMapping) {
        if (this.EventID != null) {
            return this;
        }
        String text = this.BaseName;
        String text2 = this.BaseName;
        int num = (this.ExpressionList == null) ? 0 : this.ExpressionList.length;
        List<Expression> list = new ArrayList<>(num);
        boolean flag = false;
        for (int i = 0; i < num; i++) {
            Expression expression = this.ExpressionList[i].clearConstant(constMapping);
            if (!expression.hasVar) {
                ExpressionValue expressionValue = EvaluatorDenotational.evaluate(expression, null);
                if (expressionValue == null) {
                    throw new RuntimeException("Expression " + expression + " has no value! Please make sure it has a value when used in event!");
                }
                text = text + "." + expressionValue.expressionID;
                text2 = text2 + "." + expressionValue.toString();
                list.add(expressionValue);
            } else {
                flag = true;
                list.add(expression);
            }
        }
        if (flag) {
            Event event = new Event(this.BaseName);
            event.ExpressionList = (Expression[]) list.toArray();
            return event;
        }
        Event event = new Event(this.BaseName);
        event.EventID = text;
        EventName = text2;
        return event;
    }
}
