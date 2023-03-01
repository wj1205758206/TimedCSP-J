package module.tcj.lts;

import common.ParsingException;
import common.classes.datastructure.DBM;
import common.classes.datastructure.TimerOperationType;
import common.classes.expressions.EvaluatorDenotational;
import common.classes.expressions.Valuation;
import common.classes.expressions.expressionclass.Expression;
import common.classes.expressions.expressionclass.IntConstant;
import common.classes.ultility.Ultility;
import module.tcj.assertions.TCJDataStore;

import java.util.*;

public final class TimedInterrupt extends TCJProcess {
    public TCJProcess FirstProcess;

    public TCJProcess SecondProcess;

    public Expression DelayTime;

    public int TimerID;

    public TimedInterrupt(TCJProcess firstProcess, TCJProcess secondProcess, Expression delay, int timerID) {
        this.FirstProcess = firstProcess;
        this.SecondProcess = secondProcess;
        this.DelayTime = delay;
        this.TimerID = timerID;
        this.ProcessID = TCJDataStore.DataManager.InitializeProcessID(
                this.FirstProcess.ProcessID + "^[" + this.DelayTime.expressionID + "]" + this.SecondProcess.ProcessID);
    }

    @Override
    public List<ZoneConfiguration> MoveOneStep(ZoneConfiguration eStep) throws CloneNotSupportedException, ParsingException {
        int value = ((IntConstant) EvaluatorDenotational.evaluate(this.DelayTime, eStep.globalEnv))._value;
        List<ZoneConfiguration> list = this.FirstProcess.MoveOneStep(eStep);
        for (ZoneConfiguration zoneConfiguration : list) {
            zoneConfiguration.Process = new TimedInterrupt(zoneConfiguration.Process, this.SecondProcess, this.DelayTime, this.TimerID);
        }
        DBM dbm = eStep.DBM.clone();
        dbm.addConstraint(this.TimerID, TimerOperationType.Equals, value);
        list.add(new ZoneConfiguration(this.SecondProcess, "τ", "[interrupt[" + value + "]]", eStep.globalEnv, false, dbm));
        return list;
    }

    @Override
    public void GetDiscretEventTransitions(TickConfiguration eStep, List<TickConfiguration> toReturn) throws CloneNotSupportedException, ParsingException {
        int value = ((IntConstant) EvaluatorDenotational.evaluate(this.DelayTime, eStep.globalEnv))._value;
        this.FirstProcess.GetDiscretEventTransitions(eStep, toReturn);
        for (TickConfiguration tickConfiguration : toReturn) {
            tickConfiguration.Process = new TimedInterrupt(tickConfiguration.Process, this.SecondProcess, this.DelayTime, this.TimerID);
            if (value == 0) {
                tickConfiguration.IsUrgent = true;
            }
        }
        if (value == 0) {
            toReturn.add(new TickConfiguration(this.SecondProcess, "τ", "[interrupt[" + value + "]]", eStep.globalEnv, false, true));
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
        return new TimedInterrupt(processAfterDelay, this.SecondProcess, new IntConstant(value - 1), this.TimerID);
    }

    @Override
    public List<ZoneConfigurationWithChannelData> SyncOutput(ZoneConfiguration eStep) throws ParsingException {
        List<ZoneConfigurationWithChannelData> list = this.FirstProcess.SyncOutput(eStep);
        for (ZoneConfigurationWithChannelData zoneConfigurationWithChannelData : list) {
            zoneConfigurationWithChannelData.Process = new TimedInterrupt(zoneConfigurationWithChannelData.Process, this.SecondProcess, this.DelayTime, this.TimerID);
        }
        return list;
    }

    @Override
    public List<ZoneConfiguration> SyncInput(ZoneConfigurationWithChannelData eStep) throws ParsingException {
        List<ZoneConfiguration> list = this.FirstProcess.SyncInput(eStep);
        for (ZoneConfiguration zoneConfiguration : list) {
            zoneConfiguration.Process = new TimedInterrupt(zoneConfiguration.Process, this.SecondProcess, this.DelayTime, this.TimerID);
        }
        return list;
    }

    @Override
    public void SyncOutput(TickConfiguration eStep, List<TickConfigurationWithChannelData> toReturn) throws ParsingException {
        List<TickConfigurationWithChannelData> list = new ArrayList<>();
        this.FirstProcess.SyncOutput(eStep, list);
        for (TickConfigurationWithChannelData tickConfigurationWithChannelData : list) {
            tickConfigurationWithChannelData.Process = new TimedInterrupt(tickConfigurationWithChannelData.Process, this.SecondProcess, this.DelayTime, this.TimerID);
        }
        toReturn.addAll(list);
    }

    @Override
    public void SyncInput(TickConfigurationWithChannelData eStep, List<TickConfiguration> toReturn) throws ParsingException {
        List<TickConfiguration> list = new ArrayList<>();
        this.FirstProcess.SyncInput(eStep, list);
        for (TickConfiguration tickConfiguration : list) {
            tickConfiguration.Process = new TimedInterrupt(tickConfiguration.Process, this.SecondProcess, this.DelayTime, this.TimerID);
        }
        toReturn.addAll(list);
    }

    @Override
    public List<String> GetGlobalVariables() {
        List<String> globalVariables = this.FirstProcess.GetGlobalVariables();
        Ultility.addList(globalVariables, this.SecondProcess.GetGlobalVariables());
        Ultility.addList(globalVariables, this.DelayTime.getVars());
        return globalVariables;
    }

    @Override
    public List<String> GetChannels() {
        List<String> channels = this.FirstProcess.GetChannels();
        Ultility.addList(channels, this.SecondProcess.GetChannels());
        return channels;
    }

    @Override
    public String toString() {
        if (this.TimerID == 0) {
            return "(" + this.FirstProcess.toString() + " interrupt[" + this.DelayTime + "] " + this.SecondProcess.toString() + ")";
        }
        return "(" + this.FirstProcess.toString() + " interrupt[" + this.DelayTime + "]c" + this.TimerID + " " + this.SecondProcess.toString() + ")";
    }

    @Override
    public HashSet<String> GetAlphabets(Map<String, String> visitedDefinitionRefs) throws ParsingException {
        HashSet<String> alphabets = this.SecondProcess.GetAlphabets(visitedDefinitionRefs);
        alphabets.addAll(this.FirstProcess.GetAlphabets(visitedDefinitionRefs));
        return alphabets;
    }

    @Override
    public TCJProcess ClearConstant(Map<String, Expression> constMapping) throws ParsingException {
        return new TimedInterrupt(this.FirstProcess.ClearConstant(constMapping), this.SecondProcess.ClearConstant(constMapping), this.DelayTime.clearConstant(constMapping), this.TimerID);
    }

    @Override
    public boolean MustBeAbstracted() {
        return this.FirstProcess.MustBeAbstracted() || this.SecondProcess.MustBeAbstracted();
    }

    @Override
    public TCJProcess SetTimer(Valuation globalEnv, List<List<Integer>> clockBound, Map<Integer, Integer> mapping) throws ParsingException {
        TCJProcess firstProcess = this.FirstProcess.SetTimer(globalEnv, clockBound, mapping);
        int value = ((IntConstant) EvaluatorDenotational.evaluate(this.DelayTime, globalEnv))._value;
        clockBound.add(Arrays.asList(value, value, value));
        if (this.TimerID > 0) {
            mapping.put(this.TimerID, clockBound.size());
        }
        return new TimedInterrupt(firstProcess, this.SecondProcess, this.DelayTime, clockBound.size());
    }

    @Override
    public boolean IsTimeImmediate() {
        return true;
    }
}
