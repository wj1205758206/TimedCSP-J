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

public final class WaitunitProcess extends TCJProcess {

    public TCJProcess Process;

    public Expression DelayTime;

    public int TimerID;

    public WaitunitProcess(TCJProcess process, Expression cond, int timerID) {
        this.Process = process;
        this.DelayTime = cond;
        this.TimerID = timerID;
        StringBuilder stringBuilder = new StringBuilder(this.Process.ProcessID);
        stringBuilder.append("U[" + cond.expressionID + "]");
        this.ProcessID = TCJDataStore.DataManager.InitializeProcessID(stringBuilder.toString());
    }

    @Override
    public List<String> GetGlobalVariables() {
        List<String> globalVariables = this.Process.GetGlobalVariables();
        Ultility.addList(globalVariables, this.DelayTime.getVars());
        return globalVariables;
    }

    @Override
    public List<String> GetChannels() {
        return this.Process.GetChannels();
    }

    @Override
    public List<ZoneConfiguration> MoveOneStep(ZoneConfiguration eStep) throws CloneNotSupportedException, ParsingException {
        List<ZoneConfiguration> list = new ArrayList<>();
        List<ZoneConfiguration> list2 = this.Process.MoveOneStep(eStep);
        int value = ((IntConstant) EvaluatorDenotational.evaluate(this.DelayTime, eStep.globalEnv))._value;
        for (ZoneConfiguration zoneConfiguration : list2) {
            if (zoneConfiguration.event == "terminate") {
                DBM dbm = zoneConfiguration.DBM.clone();
                dbm.addConstraint(this.TimerID, TimerOperationType.GreaterThanOrEqualTo, value);
                zoneConfiguration.DBM = dbm;
                list.add(zoneConfiguration);
                dbm = zoneConfiguration.DBM.clone();
                dbm.addConstraint(this.TimerID, TimerOperationType.LessThanOrEqualTo, value);
                ZoneConfiguration temp = new ZoneConfiguration(new WaitProcess(this.DelayTime, this.TimerID), "τ", "[waituntil[" + value + "]]", zoneConfiguration.globalEnv, false, dbm.addUrgency());
                temp.isAtomic = zoneConfiguration.isAtomic;
                list.add(temp);
            } else {
                list.add(zoneConfiguration);
            }
        }
        return list;
    }

    @Override
    public void GetDiscretEventTransitions(TickConfiguration eStep, List<TickConfiguration> toReturn) throws CloneNotSupportedException, ParsingException {
        List<TickConfiguration> list = new ArrayList<>();
        this.Process.GetDiscretEventTransitions(eStep, list);
        int value = ((IntConstant) EvaluatorDenotational.evaluate(this.DelayTime, eStep.globalEnv))._value;
        for (TickConfiguration tickConfiguration : list) {
            if (tickConfiguration.event == "terminate") {
                TickConfiguration temp = new TickConfiguration(new WaitProcess(this.DelayTime, this.TimerID), "τ", "[waituntil[" + value + "]]", tickConfiguration.globalEnv, false, true);
                temp.isAtomic = tickConfiguration.isAtomic;
                toReturn.add(temp);
            } else {
                tickConfiguration.Process = new WaitunitProcess(tickConfiguration.Process, this.DelayTime, this.TimerID);
                toReturn.add(tickConfiguration);
            }
        }
    }

    @Override
    public TCJProcess GetProcessAfterDelay(TickConfiguration eStep) throws ParsingException {
        int value = ((IntConstant) EvaluatorDenotational.evaluate(this.DelayTime, eStep.globalEnv))._value;
        if (value <= 1) {
            return this.Process.GetProcessAfterDelay(eStep);
        }
        TCJProcess processAfterDelay = this.Process.GetProcessAfterDelay(eStep);
        if (processAfterDelay == null) {
            return null;
        }
        return new WaitunitProcess(processAfterDelay, new IntConstant(value - 1), this.TimerID);
    }

    @Override
    public List<ZoneConfigurationWithChannelData> SyncOutput(ZoneConfiguration eStep) throws ParsingException {
        List<ZoneConfigurationWithChannelData> list = this.Process.SyncOutput(eStep);
        for (ZoneConfigurationWithChannelData zoneConfigurationWithChannelData : list) {
            zoneConfigurationWithChannelData.Process = new WaitunitProcess(zoneConfigurationWithChannelData.Process, this.DelayTime, this.TimerID);
        }
        return list;
    }

    @Override
    public List<ZoneConfiguration> SyncInput(ZoneConfigurationWithChannelData eStep) throws ParsingException {
        List<ZoneConfiguration> list = this.Process.SyncInput(eStep);
        for (ZoneConfiguration zoneConfiguration : list) {
            zoneConfiguration.Process = new WaitunitProcess(zoneConfiguration.Process, this.DelayTime, this.TimerID);
        }
        return list;
    }

    @Override
    public void SyncOutput(TickConfiguration eStep, List<TickConfigurationWithChannelData> toReturn) throws ParsingException {
        List<TickConfigurationWithChannelData> list = new ArrayList<>();
        this.Process.SyncOutput(eStep, list);
        for (TickConfigurationWithChannelData tickConfigurationWithChannelData : list) {
            tickConfigurationWithChannelData.Process = new WaitunitProcess(tickConfigurationWithChannelData.Process, this.DelayTime, this.TimerID);
        }
        toReturn.addAll(list);
    }

    @Override
    public void SyncInput(TickConfigurationWithChannelData eStep, List<TickConfiguration> toReturn) throws ParsingException {
        List<TickConfiguration> list = new ArrayList<>();
        this.Process.SyncInput(eStep, list);
        for (TickConfiguration tickConfiguration : list) {
            tickConfiguration.Process = new WaitunitProcess(tickConfiguration.Process, this.DelayTime, this.TimerID);
        }
        toReturn.addAll(list);
    }

    @Override
    public String toString() {
        if (this.TimerID == 0) {
            return "("
                    + this.Process
                    + " waituntil["
                    + this.DelayTime
                    + "])";
        }
        return "("
                + this.Process
                + " waituntil["
                + this.DelayTime
                + "]c"
                + this.TimerID
                + ")";
    }

    @Override
    public HashSet<String> GetAlphabets(Map<String, String> visitedDefinitionRefs) throws ParsingException {
        return this.Process.GetAlphabets(visitedDefinitionRefs);
    }

    @Override
    public TCJProcess ClearConstant(Map<String, Expression> constMapping) throws ParsingException {
        return new WaitunitProcess(this.Process.ClearConstant(constMapping), this.DelayTime.clearConstant(constMapping), this.TimerID);
    }

    @Override
    public boolean MustBeAbstracted() {
        return this.Process.MustBeAbstracted();
    }

    @Override
    public TCJProcess SetTimer(Valuation globalEnv, List<List<Integer>> clockBound, Map<Integer, Integer> mapping) throws ParsingException {
        TCJProcess process = this.Process.SetTimer(globalEnv, clockBound, mapping);
        clockBound.add(Arrays.asList(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE));
        if (this.TimerID > 0) {
            mapping.put(this.TimerID, clockBound.size());
        }
        return new WaitunitProcess(process, this.DelayTime, clockBound.size());
    }

    @Override
    public TCJProcess GetTopLevelConcurrency(List<String> visitedDef) {
        return this.Process.GetTopLevelConcurrency(visitedDef);
    }

    @Override
    public boolean IsTimeImmediate() {
        return true;
    }
}
