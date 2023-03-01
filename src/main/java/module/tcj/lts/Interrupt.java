package module.tcj.lts;

import common.ParsingException;
import common.classes.expressions.Valuation;
import common.classes.expressions.expressionclass.Expression;
import common.classes.ultility.Ultility;
import module.tcj.assertions.TCJDataStore;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public final class Interrupt extends TCJProcess {
    public TCJProcess FirstProcess;

    public TCJProcess SecondProcess;

    public Interrupt(TCJProcess firstProcess, TCJProcess secondProcess) {
        this.FirstProcess = firstProcess;
        this.SecondProcess = secondProcess;
        this.ProcessID = TCJDataStore.DataManager.InitializeProcessID(this.FirstProcess.ProcessID + "^" + this.SecondProcess.ProcessID);
    }

    @Override
    public List<ZoneConfiguration> MoveOneStep(ZoneConfiguration eStep) throws CloneNotSupportedException, ParsingException {
        List<ZoneConfiguration> list = this.FirstProcess.MoveOneStep(eStep);
        for (int i = 0; i < list.size(); i++) {
            ZoneConfiguration zoneConfiguration = list.get(i);
            if (zoneConfiguration.event != "terminate") {
                Interrupt process = new Interrupt(zoneConfiguration.Process, this.SecondProcess);
                zoneConfiguration.Process = process;
            }
        }
        List<ZoneConfiguration> list2 = this.SecondProcess.MoveOneStep(eStep);
        for (int j = 0; j < list2.size(); j++) {
            ZoneConfiguration zoneConfiguration2 = list2.get(j);
            if (zoneConfiguration2.event == "τ") {
                Interrupt process2 = new Interrupt(this.FirstProcess, zoneConfiguration2.Process);
                zoneConfiguration2.Process = process2;
            }
            list.add(zoneConfiguration2);
        }
        return list;
    }

    @Override
    public void GetDiscretEventTransitions(TickConfiguration eStep, List<TickConfiguration> toReturn) throws CloneNotSupportedException, ParsingException {
        List<TickConfiguration> list = new ArrayList<>();
        this.FirstProcess.GetDiscretEventTransitions(eStep, list);
        for (int i = 0; i < list.size(); i++) {
            TickConfiguration tickConfiguration = list.get(i);
            if (tickConfiguration.event != "terminate") {
                Interrupt process = new Interrupt(tickConfiguration.Process, this.SecondProcess);
                tickConfiguration.Process = process;
                toReturn.add(tickConfiguration);
            }
        }
        List<TickConfiguration> list2 = new ArrayList<>();
        this.SecondProcess.GetDiscretEventTransitions(eStep, list2);
        for (int j = 0; j < list2.size(); j++) {
            TickConfiguration tickConfiguration2 = list2.get(j);
            if (tickConfiguration2.event == "τ") {
                Interrupt process2 = new Interrupt(this.FirstProcess, tickConfiguration2.Process);
                tickConfiguration2.Process = process2;
                toReturn.add(tickConfiguration2);
            } else {
                toReturn.add(tickConfiguration2);
            }
        }
    }

    @Override
    public TCJProcess GetProcessAfterDelay(TickConfiguration eStep) throws ParsingException {
        TCJProcess processAfterDelay = this.FirstProcess.GetProcessAfterDelay(eStep);
        if (processAfterDelay == null) {
            return null;
        }
        TCJProcess processAfterDelay2 = this.SecondProcess.GetProcessAfterDelay(eStep);
        if (processAfterDelay2 == null) {
            return null;
        }
        return new Interrupt(processAfterDelay, processAfterDelay2);
    }

    @Override
    public List<ZoneConfigurationWithChannelData> SyncOutput(ZoneConfiguration eStep) throws ParsingException {
        List<ZoneConfigurationWithChannelData> list = this.FirstProcess.SyncOutput(eStep);
        for (int i = 0; i < list.size(); i++) {
            ZoneConfiguration zoneConfiguration = list.get(i);
            if (zoneConfiguration.event != "terminate") {
                Interrupt process = new Interrupt(zoneConfiguration.Process, this.SecondProcess);
                zoneConfiguration.Process = process;
            }
        }
        list.addAll(this.SecondProcess.SyncOutput(eStep));
        return list;
    }

    @Override
    public List<ZoneConfiguration> SyncInput(ZoneConfigurationWithChannelData eStep) throws ParsingException {
        List<ZoneConfiguration> list = this.FirstProcess.SyncInput(eStep);
        for (int i = 0; i < list.size(); i++) {
            ZoneConfiguration zoneConfiguration = list.get(i);
            if (zoneConfiguration.event != "terminate") {
                Interrupt process = new Interrupt(zoneConfiguration.Process, this.SecondProcess);
                zoneConfiguration.Process = process;
            }
        }
        list.addAll(this.SecondProcess.SyncInput(eStep));
        return list;
    }

    @Override
    public void SyncOutput(TickConfiguration eStep, List<TickConfigurationWithChannelData> toReturn) throws ParsingException {
        this.FirstProcess.SyncOutput(eStep, toReturn);
        for (int i = 0; i < toReturn.size(); i++) {
            TickConfiguration tickConfiguration = toReturn.get(i);
            if (tickConfiguration.event != "terminate") {
                Interrupt process = new Interrupt(tickConfiguration.Process, this.SecondProcess);
                tickConfiguration.Process = process;
            }
        }
        List<TickConfigurationWithChannelData> list = new ArrayList<>();
        this.SecondProcess.SyncOutput(eStep, list);
        toReturn.addAll(list);
    }

    @Override
    public void SyncInput(TickConfigurationWithChannelData eStep, List<TickConfiguration> toReturn) throws ParsingException {
        for (int i = 0; i < toReturn.size(); i++) {
            TickConfiguration tickConfiguration = toReturn.get(i);
            if (tickConfiguration.event != "terminate") {
                Interrupt process = new Interrupt(tickConfiguration.Process, this.SecondProcess);
                tickConfiguration.Process = process;
            }
        }
        List<TickConfiguration> list = new ArrayList<>();
        this.SecondProcess.SyncInput(eStep, list);
        toReturn.addAll(list);
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
        return "(" + this.FirstProcess + "interrupt" + this.SecondProcess + ")";
    }

    @Override
    public HashSet<String> GetAlphabets(Map<String, String> visitedDefinitionRefs) throws ParsingException {
        HashSet<String> alphabets = this.SecondProcess.GetAlphabets(visitedDefinitionRefs);
        alphabets.addAll(this.FirstProcess.GetAlphabets(visitedDefinitionRefs));
        return alphabets;
    }

    @Override
    public TCJProcess ClearConstant(Map<String, Expression> constMapping) throws ParsingException {
        return new Interrupt(this.FirstProcess.ClearConstant(constMapping), this.SecondProcess.ClearConstant(constMapping));
    }

    @Override
    public boolean MustBeAbstracted() {
        return this.FirstProcess.MustBeAbstracted() || this.SecondProcess.MustBeAbstracted();
    }

    @Override
    public TCJProcess SetTimer(Valuation globalEnv, List<List<Integer>> clockBound, Map<Integer, Integer> mapping) throws ParsingException {
        return new Interrupt(this.FirstProcess.SetTimer(globalEnv, clockBound, mapping), this.SecondProcess.SetTimer(globalEnv, clockBound, mapping));
    }

    @Override
    public boolean IsTimeImmediate() {
        return this.FirstProcess.IsTimeImmediate() || this.SecondProcess.IsTimeImmediate();
    }
}
