package module.tcj.lts;

import common.classes.expressions.EvaluatorDenotational;
import common.classes.expressions.Valuation;
import common.classes.expressions.expressionclass.Expression;
import common.classes.expressions.expressionclass.IntConstant;
import common.classes.ultility.Ultility;
import module.tcj.assertions.TCJDataStore;

import java.util.*;

public final class DeadlineProcess extends TCJProcess {

    public TCJProcess Process;


    public Expression DelayTime;


    public int TimerID;

    public DeadlineProcess(TCJProcess process, Expression cond, int timerID) {
        this.Process = process;
        this.DelayTime = cond;
        this.TimerID = timerID;
        StringBuilder stringBuilder = new StringBuilder(this.Process.ProcessID);
        stringBuilder.append("D[" + cond.expressionID + "]");
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
    public List<ZoneConfiguration> MoveOneStep(ZoneConfiguration eStep) throws CloneNotSupportedException {
        List<ZoneConfiguration> list = this.Process.MoveOneStep(eStep);
        for (ZoneConfiguration zoneConfiguration : list) {
            if (zoneConfiguration.event != "terminate") {
                zoneConfiguration.Process = new DeadlineProcess(zoneConfiguration.Process, this.DelayTime, this.TimerID);
            }
        }
        return list;
    }

    @Override
    public void GetDiscretEventTransitions(TickConfiguration eStep, List<TickConfiguration> toReturn) throws CloneNotSupportedException {
        this.Process.GetDiscretEventTransitions(eStep, toReturn);
        int value = ((IntConstant) EvaluatorDenotational.evaluate(this.DelayTime, eStep.globalEnv))._value;
        for (TickConfiguration tickConfiguration : toReturn) {
            if (tickConfiguration.event != "terminate") {
                tickConfiguration.Process = new DeadlineProcess(tickConfiguration.Process, this.DelayTime, this.TimerID);
            }
            if (value == 0) {
                tickConfiguration.IsUrgent = true;
            }
        }
    }

    @Override
    public TCJProcess GetProcessAfterDelay(TickConfiguration eStep) {
        int value = ((IntConstant) EvaluatorDenotational.evaluate(this.DelayTime, eStep.globalEnv))._value;
        if (value == 0) {
            return null;
        }
        TCJProcess processAfterDelay = this.Process.GetProcessAfterDelay(eStep);
        if (processAfterDelay == null) {
            return null;
        }
        return new DeadlineProcess(processAfterDelay, new IntConstant(value - 1), this.TimerID);
    }

    @Override
    public List<ZoneConfigurationWithChannelData> SyncOutput(ZoneConfiguration eStep) {
        List<ZoneConfigurationWithChannelData> list = this.Process.SyncOutput(eStep);
        for (ZoneConfigurationWithChannelData zoneConfigurationWithChannelData : list) {
            zoneConfigurationWithChannelData.Process = new DeadlineProcess(zoneConfigurationWithChannelData.Process, this.DelayTime, this.TimerID);
        }
        return list;
    }

    @Override
    public List<ZoneConfiguration> SyncInput(ZoneConfigurationWithChannelData eStep) {
        List<ZoneConfiguration> list = this.Process.SyncInput(eStep);
        for (ZoneConfiguration zoneConfiguration : list) {
            zoneConfiguration.Process = new DeadlineProcess(zoneConfiguration.Process, this.DelayTime, this.TimerID);
        }
        return list;
    }

    @Override
    public void SyncOutput(TickConfiguration eStep, List<TickConfigurationWithChannelData> toReturn) {
        this.Process.SyncOutput(eStep, toReturn);
        for (TickConfigurationWithChannelData tickConfigurationWithChannelData : toReturn) {
            tickConfigurationWithChannelData.Process = new DeadlineProcess(tickConfigurationWithChannelData.Process, this.DelayTime, this.TimerID);
        }
    }

    @Override
    public void SyncInput(TickConfigurationWithChannelData eStep, List<TickConfiguration> toReturn) {
        this.Process.SyncInput(eStep, toReturn);
        for (TickConfiguration tickConfiguration : toReturn) {
            tickConfiguration.Process = new DeadlineProcess(tickConfiguration.Process, this.DelayTime, this.TimerID);
        }
    }

    @Override
    public String toString() {
        if (this.TimerID == 0) {
            return "(" + this.Process + " deadline[" + this.DelayTime + "])";
        }
        return "(" + this.Process + "deadline[" + this.DelayTime + "]c" + this.TimerID + ")";
    }

    @Override
    public HashSet<String> GetAlphabets(Map<String, String> visitedDefinitionRefs) {
        return this.Process.GetAlphabets(visitedDefinitionRefs);
    }

    @Override
    public TCJProcess ClearConstant(Map<String, Expression> constMapping) {
        return new DeadlineProcess(this.Process.ClearConstant(constMapping), this.DelayTime.clearConstant(constMapping), this.TimerID);
    }

    @Override
    public boolean MustBeAbstracted() {
        return this.Process.MustBeAbstracted();
    }

    @Override
    public TCJProcess SetTimer(Valuation globalEnv, List<List<Integer>> clockBound, Map<Integer, Integer> mapping) {
        TCJProcess process = this.Process.SetTimer(globalEnv, clockBound, mapping);
        int value = ((IntConstant) EvaluatorDenotational.evaluate(this.DelayTime, globalEnv))._value;
        clockBound.add(Arrays.asList(value, -2147483647, value));
        if (this.TimerID > 0) {
            mapping.put(this.TimerID, clockBound.size());
        }
        return new DeadlineProcess(process, this.DelayTime, clockBound.size());
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
