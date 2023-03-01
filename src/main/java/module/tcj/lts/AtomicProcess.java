package module.tcj.lts;

import common.classes.expressions.Valuation;
import common.classes.expressions.expressionclass.Expression;
import module.tcj.assertions.TCJDataStore;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

public final class AtomicProcess extends TCJProcess {
    public TCJProcess Process;

    public AtomicProcess(TCJProcess process) {
        if (process instanceof AtomicProcess) {
            this.Process = ((AtomicProcess) process).Process;
        } else {
            this.Process = process;
        }
        this.ProcessID = TCJDataStore.DataManager.InitializeProcessID("<" + this.Process.ProcessID);
    }

    @Override
    public List<ZoneConfiguration> MoveOneStep(ZoneConfiguration eStep) {
        List<ZoneConfiguration> list = this.Process.MoveOneStep(eStep);
        for (ZoneConfiguration zoneConfiguration : list) {
            zoneConfiguration.Process = new AtomicProcess(zoneConfiguration.Process);
            zoneConfiguration.isAtomic = true;
        }
        return list;
    }

    @Override
    public void GetDiscretEventTransitions(TickConfiguration eStep, List<TickConfiguration> toReturn) {
        this.Process.GetDiscretEventTransitions(eStep, toReturn);
        for (TickConfiguration tickConfiguration : toReturn) {
            tickConfiguration.Process = new AtomicProcess(tickConfiguration.Process);
            tickConfiguration.isAtomic = true;
        }
    }

    @Override
    public TCJProcess GetProcessAfterDelay(TickConfiguration eStep) {
        TCJProcess processAfterDelay = this.Process.GetProcessAfterDelay(eStep);
        if (processAfterDelay != null) {
            return new AtomicProcess(processAfterDelay);
        }
        return null;
    }

    @Override
    public TCJProcess ClearConstant(Map<String, Expression> constMapping) {
        return new AtomicProcess(this.Process.ClearConstant(constMapping));
    }

    @Override
    public List<ZoneConfigurationWithChannelData> SyncOutput(ZoneConfiguration eStep) {
        List<ZoneConfigurationWithChannelData> list = this.Process.SyncOutput(eStep);
        for (ZoneConfigurationWithChannelData zoneConfigurationWithChannelData : list) {
            zoneConfigurationWithChannelData.Process = new AtomicProcess(zoneConfigurationWithChannelData.Process);
            zoneConfigurationWithChannelData.isAtomic = true;
        }
        return list;
    }

    @Override
    public List<ZoneConfiguration> SyncInput(ZoneConfigurationWithChannelData eStep) {
        List<ZoneConfiguration> list = this.Process.SyncInput(eStep);
        for (ZoneConfiguration zoneConfiguration : list) {
            zoneConfiguration.Process = new AtomicProcess(zoneConfiguration.Process);
            zoneConfiguration.isAtomic = true;
        }
        return list;
    }

    @Override
    public void SyncOutput(TickConfiguration eStep, List<TickConfigurationWithChannelData> toReturn) {
        this.Process.SyncOutput(eStep, toReturn);
        for (TickConfigurationWithChannelData tickConfigurationWithChannelData : toReturn) {
            tickConfigurationWithChannelData.Process = new AtomicProcess(tickConfigurationWithChannelData.Process);
            tickConfigurationWithChannelData.isAtomic = true;
        }
    }

    @Override
    public void SyncInput(TickConfigurationWithChannelData eStep, List<TickConfiguration> toReturn) {
        this.Process.SyncInput(eStep, toReturn);
        for (TickConfiguration tickConfiguration : toReturn) {
            tickConfiguration.Process = new AtomicProcess(tickConfiguration.Process);
            tickConfiguration.isAtomic = true;
        }
    }

    @Override
    public String toString() {
        return "atomic{" + this.Process + "}";
    }

    @Override
    public HashSet<String> GetAlphabets(Map<String, String> visitedDefinitionRefs) {
        return this.Process.GetAlphabets(visitedDefinitionRefs);
    }

    @Override
    public List<String> GetGlobalVariables() {
        return this.Process.GetGlobalVariables();
    }

    @Override
    public List<String> GetChannels() {
        return this.Process.GetChannels();
    }

    @Override
    public boolean MustBeAbstracted() {
        return this.Process.MustBeAbstracted();
    }

    @Override
    public boolean IsTimeImmediate() {
        return this.IsTimeImmediate();
    }

    @Override
    public TCJProcess GetTopLevelConcurrency(List<String> visitedDef) {
        return this.Process.GetTopLevelConcurrency(visitedDef);
    }

    @Override
    public TCJProcess SetTimer(Valuation globalEnv, List<List<Integer>> clockBound, Map<Integer, Integer> mapping) {
        return new AtomicProcess(this.Process.SetTimer(globalEnv, clockBound, mapping));
    }

    @Override
    public boolean IsSkip() {
        return this.Process.IsSkip();
    }
}
