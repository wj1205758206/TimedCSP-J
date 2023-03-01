package module.tcj.lts;

import common.ParsingException;
import common.classes.expressions.Valuation;
import common.classes.expressions.expressionclass.Expression;
import common.classes.ultility.Ultility;
import module.tcj.assertions.TCJDataStore;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

public final class Sequence extends TCJProcess {

    public TCJProcess FirstProcess;

    public TCJProcess SecondProcess;

    public Sequence(TCJProcess firstProcess, TCJProcess secondProcess) {
        this.FirstProcess = firstProcess;
        this.SecondProcess = secondProcess;
        if (firstProcess.IsSkip()) {
            this.ProcessID = this.SecondProcess.ProcessID;
            return;
        }
        if (this.FirstProcess instanceof Stop || (this.FirstProcess instanceof AtomicProcess
                && (((AtomicProcess) this.FirstProcess).Process instanceof Stop))) {
            this.ProcessID = "!";
            return;
        }
        this.ProcessID = TCJDataStore.DataManager.InitializeProcessID(";" + this.FirstProcess.ProcessID + "$" + this.SecondProcess.ProcessID);
    }

    @Override
    public List<ZoneConfiguration> MoveOneStep(ZoneConfiguration eStep) throws CloneNotSupportedException, ParsingException {
        if (this.FirstProcess.IsSkip()) {
            return this.SecondProcess.MoveOneStep(eStep);
        }
        List<ZoneConfiguration> list = this.FirstProcess.MoveOneStep(eStep);
        for (int i = 0; i < list.size(); i++) {
            ZoneConfiguration zoneConfiguration = list.get(i);
            if (zoneConfiguration.event == "terminate") {
                zoneConfiguration.event = "τ";
                zoneConfiguration.IsUrgent = true;
                zoneConfiguration.DBM = zoneConfiguration.DBM.addUrgency();
                zoneConfiguration.Process = this.SecondProcess;
            } else {
                zoneConfiguration.Process = new Sequence(zoneConfiguration.Process, this.SecondProcess);
            }
        }
        return list;
    }

    @Override
    public void GetDiscretEventTransitions(TickConfiguration eStep, List<TickConfiguration> toReturn) throws CloneNotSupportedException, ParsingException {
        if (this.FirstProcess.IsSkip()) {
            this.SecondProcess.GetDiscretEventTransitions(eStep, toReturn);
            return;
        }
        this.FirstProcess.GetDiscretEventTransitions(eStep, toReturn);
        for (TickConfiguration tickConfiguration : toReturn) {
            if (tickConfiguration.event == "terminate") {
                tickConfiguration.event = "τ";
                tickConfiguration.Process = this.SecondProcess;
                tickConfiguration.IsUrgent = true;
            } else {
                tickConfiguration.Process = new Sequence(tickConfiguration.Process, this.SecondProcess);
            }
        }
    }

    @Override
    public TCJProcess GetProcessAfterDelay(TickConfiguration eStep) throws ParsingException {
        if (this.FirstProcess.IsSkip()) {
            return this.SecondProcess.GetProcessAfterDelay(eStep);
        }
        TCJProcess processAfterDelay = this.FirstProcess.GetProcessAfterDelay(eStep);
        if (processAfterDelay == null) {
            return processAfterDelay;
        }
        return new Sequence(processAfterDelay, this.SecondProcess);
    }

    @Override
    public List<ZoneConfigurationWithChannelData> SyncOutput(ZoneConfiguration eStep) throws ParsingException {
        if (this.FirstProcess.IsSkip()) {
            return this.SecondProcess.SyncOutput(eStep);
        }
        List<ZoneConfigurationWithChannelData> list = this.FirstProcess.SyncOutput(eStep);
        for (int i = 0; i < list.size(); i++) {
            ZoneConfiguration zoneConfiguration = list.get(i);
            zoneConfiguration.Process = new Sequence(zoneConfiguration.Process, this.SecondProcess);
        }
        return list;
    }

    @Override
    public List<ZoneConfiguration> SyncInput(ZoneConfigurationWithChannelData eStep) throws ParsingException {
        if (this.FirstProcess.IsSkip()) {
            return this.SecondProcess.SyncInput(eStep);
        }
        List<ZoneConfiguration> list = this.FirstProcess.SyncInput(eStep);
        for (int i = 0; i < list.size(); i++) {
            ZoneConfiguration zoneConfiguration = list.get(i);
            zoneConfiguration.Process = new Sequence(zoneConfiguration.Process, this.SecondProcess);
        }
        return list;
    }

    @Override
    public void SyncOutput(TickConfiguration eStep, List<TickConfigurationWithChannelData> toReturn) throws ParsingException {
        if (this.FirstProcess.IsSkip()) {
            this.SecondProcess.SyncOutput(eStep, toReturn);
            return;
        }
        this.FirstProcess.SyncOutput(eStep, toReturn);
        for (TickConfigurationWithChannelData tickConfigurationWithChannelData : toReturn) {
            tickConfigurationWithChannelData.Process = new Sequence(tickConfigurationWithChannelData.Process, this.SecondProcess);
        }
    }

    @Override
    public void SyncInput(TickConfigurationWithChannelData eStep, List<TickConfiguration> toReturn) throws ParsingException {
        if (this.FirstProcess.IsSkip()) {
            this.SecondProcess.SyncInput(eStep, toReturn);
            return;
        }
        this.FirstProcess.SyncInput(eStep, toReturn);
        for (TickConfiguration tickConfiguration : toReturn) {
            tickConfiguration.Process = new Sequence(tickConfiguration.Process, this.SecondProcess);
        }
    }

    @Override
    public List<String> GetGlobalVariables() {
        List<String> globalVariables = this.FirstProcess.GetGlobalVariables();
        Ultility.addList(globalVariables, this.SecondProcess.GetGlobalVariables());
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
        return "(" + this.FirstProcess + ";" + this.SecondProcess + ")";
    }

    @Override
    public HashSet<String> GetAlphabets(Map<String, String> visitedDefinitionRefs) throws ParsingException {
        if (this.FirstProcess.IsSkip()) {
            return this.SecondProcess.GetAlphabets(visitedDefinitionRefs);
        }
        HashSet<String> alphabets = this.SecondProcess.GetAlphabets(visitedDefinitionRefs);
        alphabets.addAll(this.FirstProcess.GetAlphabets(visitedDefinitionRefs));
        return alphabets;
    }

    @Override
    public TCJProcess ClearConstant(Map<String, Expression> constMapping) throws ParsingException {
        if (this.FirstProcess.IsSkip()) {
            return this.SecondProcess.ClearConstant(constMapping);
        }
        return new Sequence(this.FirstProcess.ClearConstant(constMapping), this.SecondProcess.ClearConstant(constMapping));
    }

    @Override
    public boolean MustBeAbstracted() {
        return this.FirstProcess.MustBeAbstracted() || this.SecondProcess.MustBeAbstracted();
    }

    @Override
    public TCJProcess SetTimer(Valuation globalEnv, List<List<Integer>> clockBound, Map<Integer, Integer> mapping) throws ParsingException {
        if (this.FirstProcess.IsSkip()) {
            return this.SecondProcess.SetTimer(globalEnv, clockBound, mapping);
        }
        return new Sequence(this.FirstProcess.SetTimer(globalEnv, clockBound, mapping), this.SecondProcess);
    }

    @Override
    public boolean IsTimeImmediate() {
        if (this.FirstProcess.IsSkip()) {
            return this.SecondProcess.IsTimeImmediate();
        }
        return this.FirstProcess.IsTimeImmediate();
    }

    @Override
    public boolean IsSkip() {
        return this.FirstProcess.IsSkip() && this.SecondProcess.IsSkip();
    }
}
