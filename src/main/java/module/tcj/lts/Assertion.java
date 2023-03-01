package module.tcj.lts;

import common.classes.expressions.EvaluatorDenotational;
import common.classes.expressions.expressionclass.BoolConstant;
import common.classes.expressions.expressionclass.Expression;
import common.classes.expressions.expressionclass.ExpressionValue;
import module.tcj.assertions.TCJDataStore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class Assertion extends TCJProcess {

    public Expression ConditionalExpression;

    public int LineNumber;

    public Assertion(Expression conditionExpression, int line) {
        this.ConditionalExpression = conditionExpression;
        this.LineNumber = line;
        this.ProcessID = TCJDataStore.DataManager.InitializeProcessID("A" + conditionExpression.expressionID);
    }

    @Override
    public List<ZoneConfiguration> MoveOneStep(ZoneConfiguration eStep) {
        List<ZoneConfiguration> list = new ArrayList<>();
        ExpressionValue expressionValue = EvaluatorDenotational.evaluate(this.ConditionalExpression, eStep.globalEnv);
        if (((BoolConstant) expressionValue)._value) {
            list.add(new ZoneConfiguration(new Stop(), "terminate", null, eStep.globalEnv, false, eStep.DBM.addUrgency()));
            return list;
        }
        throw new RuntimeException("Assertion at line " + this.LineNumber + " failed: " + this.ConditionalExpression);
    }

    @Override
    public void GetDiscretEventTransitions(TickConfiguration eStep, List<TickConfiguration> toReturn) {
        ExpressionValue expressionValue = EvaluatorDenotational.evaluate(this.ConditionalExpression, eStep.globalEnv);
        if (((BoolConstant) expressionValue)._value) {
            toReturn.add(new TickConfiguration(new Stop(), "terminate", null, eStep.globalEnv, false, true));
            return;
        }
        throw new RuntimeException("Assertion at line " + this.LineNumber + " failed: " + this.ConditionalExpression);
    }

    @Override
    public String toString() {
        return " assert(" + this.ConditionalExpression + ")";
    }

    @Override
    public List<String> GetGlobalVariables() {
        return this.ConditionalExpression.getVars();
    }

    @Override
    public TCJProcess ClearConstant(Map<String, Expression> constMapping) {
        Expression conditionExpression = this.ConditionalExpression.clearConstant(constMapping);
        return new Assertion(conditionExpression, this.LineNumber);
    }
}
