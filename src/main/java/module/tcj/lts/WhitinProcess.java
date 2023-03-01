package module.tcj.lts;

import common.ParsingException;
import common.classes.expressions.EvaluatorDenotational;
import common.classes.expressions.Valuation;
import common.classes.expressions.expressionclass.Expression;
import common.classes.expressions.expressionclass.IntConstant;
import common.classes.ultility.Ultility;
import module.tcj.assertions.TCJDataStore;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public final class WhitinProcess extends TCJProcess {

    public int TimerID;
    public TCJProcess FirstProcess;

    public Expression DelayTime;

    public WhitinProcess(TCJProcess firstProcess, Expression delay, int timerID) {
        this.FirstProcess = firstProcess;
        this.DelayTime = delay;
        this.TimerID = timerID;
        this.ProcessID = TCJDataStore.DataManager.InitializeProcessID(this.FirstProcess.ProcessID + "I[" + this.DelayTime.expressionID + "]");
    }

    @Override
    public List<ZoneConfiguration> MoveOneStep(ZoneConfiguration eStep) throws CloneNotSupportedException, ParsingException {
        List<ZoneConfiguration> list = this.FirstProcess.MoveOneStep(eStep);
        for (ZoneConfiguration zoneConfiguration : list) {
            if (zoneConfiguration.event == "τ") {
                zoneConfiguration.Process = new WhitinProcess(zoneConfiguration.Process, this.DelayTime, this.TimerID);
            }
        }
        return list;
    }

    @Override
    public void GetDiscretEventTransitions(TickConfiguration eStep, List<TickConfiguration> toReturn) throws CloneNotSupportedException, ParsingException {
        int value = ((IntConstant) EvaluatorDenotational.evaluate(this.DelayTime, eStep.globalEnv))._value;
        this.FirstProcess.GetDiscretEventTransitions(eStep, toReturn);
        for (TickConfiguration tickConfiguration : toReturn) {
            if (tickConfiguration.event == "τ") {
                tickConfiguration.Process = new WhitinProcess(tickConfiguration.Process, this.DelayTime, this.TimerID);
            }
            if (value == 0) {
                tickConfiguration.IsUrgent = true;
            }
        }
    }

    @Override
    public TCJProcess GetProcessAfterDelay(TickConfiguration eStep) throws ParsingException {
        int value = ((IntConstant) EvaluatorDenotational.evaluate(this.DelayTime, eStep.globalEnv))._value;
        if (value == 0) {
            return null;
        }
        TCJProcess processAfterDelay = this.FirstProcess.GetProcessAfterDelay(eStep);
        if (processAfterDelay == null) {
            return null;
        }
        return new WhitinProcess(processAfterDelay, new IntConstant(value - 1), this.TimerID);
    }

    @Override
    public List<ZoneConfigurationWithChannelData> SyncOutput(ZoneConfiguration eStep) throws ParsingException {
        return this.FirstProcess.SyncOutput(eStep);
    }

    @Override
    public List<ZoneConfiguration> SyncInput(ZoneConfigurationWithChannelData eStep) throws ParsingException {
        return this.FirstProcess.SyncInput(eStep);
    }

    @Override
    public void SyncOutput(TickConfiguration eStep, List<TickConfigurationWithChannelData> toReturn) throws ParsingException {
        this.FirstProcess.SyncOutput(eStep, toReturn);
    }

    @Override
    public void SyncInput(TickConfigurationWithChannelData eStep, List<TickConfiguration> toReturn) throws ParsingException {
        this.FirstProcess.SyncInput(eStep, toReturn);
    }

    @Override
    public List<String> GetGlobalVariables() {
        List<String> globalVariables = this.FirstProcess.GetGlobalVariables();
        Ultility.addList(globalVariables, this.DelayTime.getVars());
        return globalVariables;
    }

    @Override
    public List<String> GetChannels() {
        return this.FirstProcess.GetChannels();
    }

    @Override
    public String toString() {
        if (this.TimerID == 0) {
            return "("
                    + this.FirstProcess.toString()
                    + " within["
                    + this.DelayTime
                    + "])";
        }
        return "("
                + this.FirstProcess.toString()
                + " within["
                + this.DelayTime
                + "]c"
                + this.TimerID
                + ")";
    }

    @Override
    public HashSet<String> GetAlphabets(Map<String, String> visitedDefinitionRefs) throws ParsingException {
        return this.FirstProcess.GetAlphabets(visitedDefinitionRefs);
    }

    @Override
    public TCJProcess ClearConstant(Map<String, Expression> constMapping) throws ParsingException {
        return new WhitinProcess(this.FirstProcess.ClearConstant(constMapping), this.DelayTime.clearConstant(constMapping), this.TimerID);
    }

    @Override
    public boolean MustBeAbstracted() {
        return this.FirstProcess.MustBeAbstracted();
    }

    @Override
    public TCJProcess SetTimer(Valuation globalEnv, List<List<Integer>> clockBound, Map<Integer, Integer> mapping) throws ParsingException {
        TCJProcess firstProcess = this.FirstProcess.SetTimer(globalEnv, clockBound, mapping);
        int value = ((IntConstant) EvaluatorDenotational.evaluate(this.DelayTime, globalEnv))._value;
        clockBound.add(Arrays.asList(value, -2147483647, value));
        if (this.TimerID > 0) {
            mapping.put(this.TimerID, clockBound.size());
        }
        return new WhitinProcess(firstProcess, this.DelayTime, clockBound.size());
    }

    @Override
    public TCJProcess GetTopLevelConcurrency(List<String> visitedDef) {
        return this.FirstProcess.GetTopLevelConcurrency(visitedDef);
    }

    @Override
    public boolean IsTimeImmediate() {
        return true;
    }
}
