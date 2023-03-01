package module.tcj.lts;

import common.classes.expressions.EvaluatorDenotational;
import common.classes.expressions.expressionclass.BoolConstant;
import common.classes.expressions.expressionclass.Expression;
import common.classes.expressions.expressionclass.ExpressionValue;
import common.classes.ultility.Ultility;
import module.tcj.assertions.TCJDataStore;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

public final class ConditionalChoiceAtomic extends TCJProcess {
    public TCJProcess FirstProcess;

    public TCJProcess SecondProcess;

    public Expression ConditionalExpression;

    public ConditionalChoiceAtomic(TCJProcess firstProcess, TCJProcess secondProcess, Expression conditionExpression) {
        this.FirstProcess = firstProcess;
        this.SecondProcess = secondProcess;
        this.ConditionalExpression = conditionExpression;
        this.ProcessID = TCJDataStore.DataManager.InitializeProcessID(
                this.FirstProcess.ProcessID + " @" + conditionExpression.expressionID + "@" + this.SecondProcess.ProcessID);
    }

    @Override
    public List<ZoneConfiguration> MoveOneStep(ZoneConfiguration eStep) {
        ExpressionValue expressionValue = EvaluatorDenotational.evaluate(this.ConditionalExpression, eStep.globalEnv);

        if (((BoolConstant) expressionValue)._value) {
            return this.FirstProcess.MoveOneStep(eStep);
        }
        return this.SecondProcess.MoveOneStep(eStep);
    }

    @Override
    public void GetDiscretEventTransitions(TickConfiguration eStep, List<TickConfiguration> toReturn) {
        ExpressionValue expressionValue = EvaluatorDenotational.evaluate(this.ConditionalExpression, eStep.globalEnv);
        if (((BoolConstant) expressionValue)._value) {
            this.FirstProcess.GetDiscretEventTransitions(eStep, toReturn);
            return;
        }
        this.SecondProcess.GetDiscretEventTransitions(eStep, toReturn);
    }

    @Override
    public void SyncOutput(TickConfiguration eStep, List<TickConfigurationWithChannelData> toReturn) {
        ExpressionValue expressionValue = EvaluatorDenotational.evaluate(this.ConditionalExpression, eStep.globalEnv);
        if (((BoolConstant) expressionValue)._value) {
            this.FirstProcess.SyncOutput(eStep, toReturn);
            return;
        }
        this.SecondProcess.SyncOutput(eStep, toReturn);
    }

    @Override
    public void SyncInput(TickConfigurationWithChannelData eStep, List<TickConfiguration> toReturn) {
        ExpressionValue expressionValue = EvaluatorDenotational.evaluate(this.ConditionalExpression, eStep.globalEnv);
        if (((BoolConstant) expressionValue)._value) {
            this.FirstProcess.SyncInput(eStep, toReturn);
            return;
        }
        this.SecondProcess.SyncInput(eStep, toReturn);
    }

    @Override
    public List<ZoneConfigurationWithChannelData> SyncOutput(ZoneConfiguration eStep) {
        ExpressionValue expressionValue = EvaluatorDenotational.evaluate(this.ConditionalExpression, eStep.globalEnv);
        if (((BoolConstant) expressionValue)._value) {
            return this.FirstProcess.SyncOutput(eStep);
        }
        return this.SecondProcess.SyncOutput(eStep);
    }

    @Override
    public List<ZoneConfiguration> SyncInput(ZoneConfigurationWithChannelData eStep) {
        ExpressionValue expressionValue = EvaluatorDenotational.evaluate(this.ConditionalExpression, eStep.globalEnv);
        if (((BoolConstant) expressionValue)._value) {
            return this.FirstProcess.SyncInput(eStep);
        }
        return this.SecondProcess.SyncInput(eStep);
    }

    @Override
    public String toString() {
        return "ifa " + this.ConditionalExpression + " {" + this.FirstProcess.toString() + "} else {" + this.SecondProcess.toString() + "}";
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
        return new ConditionalChoiceAtomic(this.FirstProcess.ClearConstant(constMapping), this.SecondProcess.ClearConstant(constMapping), conditionExpression);

    }

    @Override
    public boolean MustBeAbstracted() {
        return this.FirstProcess.MustBeAbstracted() || this.SecondProcess.MustBeAbstracted();
    }

    @Override
    public boolean IsTimeImmediate() {
        return this.FirstProcess.IsTimeImmediate() || this.SecondProcess.IsTimeImmediate();
    }
}
