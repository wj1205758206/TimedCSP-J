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

public final class GuardProcess extends TCJProcess {

    public TCJProcess FirstProcess;


    public Expression ConditionalExpression;


    public GuardProcess(TCJProcess firstProcess, Expression conditionExpression) {
        this.FirstProcess = firstProcess;
        this.ConditionalExpression = conditionExpression;
        this.ProcessID = TCJDataStore.DataManager.InitializeProcessID(this.FirstProcess.ProcessID + "@" + conditionExpression.expressionID);
    }

    @Override
    public List<ZoneConfiguration> MoveOneStep(ZoneConfiguration eStep) throws CloneNotSupportedException {
        ExpressionValue expressionValue = EvaluatorDenotational.evaluate(this.ConditionalExpression, eStep.globalEnv);
        if (((BoolConstant) expressionValue)._value) {
            return this.FirstProcess.MoveOneStep(eStep);
        }
        return new ArrayList<>();
    }

    @Override
    public void GetDiscretEventTransitions(TickConfiguration eStep, List<TickConfiguration> toReturn) throws CloneNotSupportedException {
        ExpressionValue expressionValue = EvaluatorDenotational.evaluate(this.ConditionalExpression, eStep.globalEnv);
        if (((BoolConstant) expressionValue)._value) {
            this.FirstProcess.GetDiscretEventTransitions(eStep, toReturn);
        }
    }

    @Override
    public void SyncOutput(TickConfiguration eStep, List<TickConfigurationWithChannelData> toReturn) {
        ExpressionValue expressionValue = EvaluatorDenotational.evaluate(this.ConditionalExpression, eStep.globalEnv);
        if (((BoolConstant) expressionValue)._value) {
            this.FirstProcess.SyncOutput(eStep, toReturn);
        }
    }

    @Override
    public void SyncInput(TickConfigurationWithChannelData eStep, List<TickConfiguration> toReturn) {
        ExpressionValue expressionValue = EvaluatorDenotational.evaluate(this.ConditionalExpression, eStep.globalEnv);
        if (((BoolConstant) expressionValue)._value) {
            this.FirstProcess.SyncInput(eStep, toReturn);
        }

    }

    @Override
    public List<ZoneConfigurationWithChannelData> SyncOutput(ZoneConfiguration eStep) {
        ExpressionValue expressionValue = EvaluatorDenotational.evaluate(this.ConditionalExpression, eStep.globalEnv);
        if (((BoolConstant) expressionValue)._value) {
            return this.FirstProcess.SyncOutput(eStep);
        }
        return new ArrayList<>();
    }

    @Override
    public List<ZoneConfiguration> SyncInput(ZoneConfigurationWithChannelData eStep) {
        ExpressionValue expressionValue = EvaluatorDenotational.evaluate(this.ConditionalExpression, eStep.globalEnv);
        if (((BoolConstant) expressionValue)._value) {
            return this.FirstProcess.SyncInput(eStep);
        }
        return new ArrayList<>();
    }

    @Override
    public String toString() {
        return "([" + this.ConditionalExpression + "]" + this.FirstProcess + ")";
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
        return new GuardProcess(firstProcess, conditionExpression);
    }

    @Override
    public boolean MustBeAbstracted() {
        return this.FirstProcess.MustBeAbstracted();
    }

    @Override
    public boolean IsTimeImmediate() {
        return this.FirstProcess.IsTimeImmediate();
    }
}
