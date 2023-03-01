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

public final class ConditionalChoice extends TCJProcess {
    public TCJProcess FirstProcess;

    public TCJProcess SecondProcess;

    public Expression ConditionalExpression;

    public ConditionalChoice(TCJProcess firstProcess, TCJProcess secondProcess, Expression conditionExpression) {
        this.FirstProcess = firstProcess;
        this.SecondProcess = secondProcess;
        this.ConditionalExpression = conditionExpression;
        this.ProcessID = TCJDataStore.DataManager.InitializeProcessID(
                this.FirstProcess.ProcessID + "@" + conditionExpression.expressionID + "@" + this.SecondProcess.ProcessID);
    }

    @Override
    public List<ZoneConfiguration> MoveOneStep(ZoneConfiguration eStep) {
        List<ZoneConfiguration> list = new ArrayList<>();
        ExpressionValue expressionValue = EvaluatorDenotational.evaluate(this.ConditionalExpression, eStep.globalEnv);

        if (((BoolConstant) expressionValue)._value) {
            list.add(new ZoneConfiguration(this.FirstProcess, "τ", "[if(" + this.ConditionalExpression + ")]", eStep.globalEnv, false, eStep.DBM));
        } else {
            list.add(new ZoneConfiguration(this.SecondProcess, "τ", "[if!(" + this.ConditionalExpression + ")]", eStep.globalEnv, false, eStep.DBM));
        }
        return list;
    }

    @Override
    public void GetDiscretEventTransitions(TickConfiguration eStep, List<TickConfiguration> toReturn) {

        ExpressionValue expressionValue = EvaluatorDenotational.evaluate(this.ConditionalExpression, eStep.globalEnv);

        if (((BoolConstant) expressionValue)._value) {
            toReturn.add(new TickConfiguration(this.FirstProcess, "τ", "[if(" + this.ConditionalExpression + ")]", eStep.globalEnv, false, false));
            return;
        }
        toReturn.add(new TickConfiguration(this.SecondProcess, "τ", "[if!(" + this.ConditionalExpression + ")]", eStep.globalEnv, false, false));
    }

    @Override
    public String toString() {
        return "if " + this.ConditionalExpression + " {" + this.FirstProcess + "} else {" + this.SecondProcess + "}";
    }

    @Override
    public HashSet<String> GetAlphabets(Map<String, String> visitedDefinitionRefs) {
        HashSet<String> alphabets = this.SecondProcess.GetAlphabets(visitedDefinitionRefs);
        alphabets.addAll(this.FirstProcess.GetAlphabets(visitedDefinitionRefs));
        return alphabets;
    }

    @Override
    public List<String> GetGlobalVariables() {
        List<String> globalVariables = this.SecondProcess.GetGlobalVariables();
        Ultility.addList(globalVariables, this.FirstProcess.GetGlobalVariables());
        Ultility.addList(globalVariables, this.ConditionalExpression.getVars());
        return globalVariables;
    }

    @Override
    public List<String> GetChannels() {
        List<String> channels = this.SecondProcess.GetChannels();
        Ultility.addList(channels, this.FirstProcess.GetChannels());
        return channels;
    }

    @Override
    public TCJProcess ClearConstant(Map<String, Expression> constMapping) {
        Expression conditionExpression = this.ConditionalExpression.clearConstant(constMapping);
        return new ConditionalChoice(this.FirstProcess.ClearConstant(constMapping), this.SecondProcess.ClearConstant(constMapping), conditionExpression);
    }

    @Override
    public boolean MustBeAbstracted() {
        return this.FirstProcess.MustBeAbstracted() || this.SecondProcess.MustBeAbstracted();
    }
}
