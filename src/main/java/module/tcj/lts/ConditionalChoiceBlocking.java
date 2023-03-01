package module.tcj.lts;

import common.classes.expressions.EvaluatorDenotational;
import common.classes.expressions.expressionclass.BoolConstant;
import common.classes.expressions.expressionclass.Expression;
import common.classes.expressions.expressionclass.ExpressionValue;
import common.classes.ultility.Ultility;
import module.tcj.assertions.TCJDataStore;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public final class ConditionalChoiceBlocking extends TCJProcess {

    public TCJProcess FirstProcess;

    public Expression ConditionalExpression;

    public ConditionalChoiceBlocking(TCJProcess firstProcess, Expression conditionExpression) {
        this.FirstProcess = firstProcess;
        this.ConditionalExpression = conditionExpression;
        this.ProcessID = TCJDataStore.DataManager.InitializeProcessID(this.FirstProcess.ProcessID + "@" + conditionExpression.expressionID);
    }

    @Override
    public List<ZoneConfiguration> MoveOneStep(ZoneConfiguration eStep) {
        List<ZoneConfiguration> list = new ArrayList<>();
        ExpressionValue expressionValue = EvaluatorDenotational.evaluate(this.ConditionalExpression, eStep.globalEnv);
        if (((BoolConstant) expressionValue)._value) {
            list.add(new ZoneConfiguration(this.FirstProcess, "τ", "[ifb(" + this.ConditionalExpression + ")]", eStep.globalEnv, false, eStep.DBM.addUrgency()));
        }
        return list;
    }

    @Override
    public void GetDiscretEventTransitions(TickConfiguration eStep, List<TickConfiguration> toReturn) {
        ExpressionValue expressionValue = EvaluatorDenotational.evaluate(this.ConditionalExpression, eStep.globalEnv);
        if (((BoolConstant) expressionValue)._value) {
            toReturn.add(new TickConfiguration(this.FirstProcess, "τ", "[ifb(" + this.ConditionalExpression + ")]", eStep.globalEnv, false, true));
        }
    }

    @Override
    public String toString() {
        return "ifb " + this.ConditionalExpression + " {" + this.FirstProcess.toString() + "}";
    }

    @Override
    public HashSet<String> GetAlphabets(Map<String, String> visitedDefinitionRefs) {
        return this.FirstProcess.GetAlphabets(visitedDefinitionRefs);
    }

    @Override
    public List<String> GetGlobalVariables() {
        List<String> globalVariables = this.FirstProcess.GetGlobalVariables();
        Ultility.addList(globalVariables, this.ConditionalExpression.getVars());
        return globalVariables;
    }

    @Override
    public List<String> GetChannels() {
        return this.FirstProcess.GetChannels();
    }

    @Override
    public TCJProcess ClearConstant(Map<String, Expression> constMapping) {
        Expression conditionExpression = this.ConditionalExpression.clearConstant(constMapping);
        TCJProcess firstProcess = this.FirstProcess.ClearConstant(constMapping);
        return new ConditionalChoiceBlocking(firstProcess, conditionExpression);
    }

    @Override
    public boolean MustBeAbstracted() {
        return this.FirstProcess.MustBeAbstracted();
    }
}
