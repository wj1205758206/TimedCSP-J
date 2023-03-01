package module.tcj.lts;

import common.ParsingException;
import common.classes.expressions.Valuation;
import common.classes.expressions.expressionclass.Expression;
import common.classes.moduleinterface.SpecificationBase;
import common.classes.ultility.Ultility;
import module.tcj.assertions.TCJDataStore;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public final class IndexChoice extends TCJProcess {
    public List<TCJProcess> Processes;


    public IndexedProcess IndexedProcessDefinition;

    public IndexChoice(IndexedProcess definition) {
        this.IndexedProcessDefinition = definition;
    }

    public IndexChoice(List<TCJProcess> processes) {
        this.Processes = processes;
        StringBuilder stringBuilder = new StringBuilder();
        for (TCJProcess TCJProcess : this.Processes) {
            stringBuilder.append("#");
            stringBuilder.append(TCJProcess.ProcessID);
        }
        this.ProcessID = TCJDataStore.DataManager.InitializeProcessID(stringBuilder.toString());
    }

    @Override
    public List<ZoneConfiguration> MoveOneStep(ZoneConfiguration eStep) throws CloneNotSupportedException {
        List<ZoneConfiguration> list = new ArrayList<>();
        for (int i = 0; i < this.Processes.size(); i++) {
            list.addAll(this.Processes.get(i).MoveOneStep(eStep));
        }
        return list;
    }

    @Override
    public void GetDiscretEventTransitions(TickConfiguration eStep, List<TickConfiguration> toReturn) throws CloneNotSupportedException {
        for (int i = 0; i < this.Processes.size(); i++) {
            List<TickConfiguration> list = new ArrayList<>();
            this.Processes.get(i).GetDiscretEventTransitions(eStep, list);
            toReturn.addAll(list);
        }
    }

    @Override
    public TCJProcess GetProcessAfterDelay(TickConfiguration eStep) {
        List<TCJProcess> list = new ArrayList<>();
        for (int i = 0; i < this.Processes.size(); i++) {
            TCJProcess processAfterDelay = this.Processes.get(i).GetProcessAfterDelay(eStep);
            if (processAfterDelay == null) {
                return null;
            }
            list.add(processAfterDelay);
        }
        return new IndexChoice(list);
    }

    @Override
    public List<ZoneConfigurationWithChannelData> SyncOutput(ZoneConfiguration eStep) {
        List<ZoneConfigurationWithChannelData> list = new ArrayList<>();
        for (int i = 0; i < this.Processes.size(); i++) {
            list.addAll(this.Processes.get(i).SyncOutput(eStep));
        }
        return list;
    }

    @Override
    public List<ZoneConfiguration> SyncInput(ZoneConfigurationWithChannelData eStep) {
        List<ZoneConfiguration> list = new ArrayList<>();
        for (int i = 0; i < this.Processes.size(); i++) {
            list.addAll(this.Processes.get(i).SyncInput(eStep));
        }
        return list;
    }

    @Override
    public void SyncOutput(TickConfiguration eStep, List<TickConfigurationWithChannelData> toReturn) {
        List<TickConfigurationWithChannelData> list = new ArrayList<>();
        for (TCJProcess TCJProcess : this.Processes) {
            TCJProcess.SyncOutput(eStep, list);
        }
        toReturn.addAll(list);
    }

    @Override
    public void SyncInput(TickConfigurationWithChannelData eStep, List<TickConfiguration> toReturn) {
        for (TCJProcess TCJProcess : this.Processes) {
            List<TickConfiguration> list = new ArrayList<>();
            TCJProcess.SyncInput(eStep, list);
            toReturn.addAll(list);
        }
    }

    @Override
    public String toString() {
        if (this.Processes == null) {
            return "[]" + this.IndexedProcessDefinition;
        }
        String str = "(" + this.Processes.get(0);
        for (int i = 1; i < this.Processes.size(); i++) {
            str += "[]";
            str += this.Processes.get(i).toString();
        }
        return str + ")";
    }

    @Override
    public HashSet<String> GetAlphabets(Map<String, String> visitedDefinitionRefs) {
        if (this.Processes == null) {
            return this.IndexedProcessDefinition.Process.GetAlphabets(visitedDefinitionRefs);
        }
        HashSet<String> hashSet = new HashSet<>();
        for (int i = 0; i < this.Processes.size(); i++) {
            hashSet.addAll(this.Processes.get(i).GetAlphabets(visitedDefinitionRefs));
        }
        return hashSet;
    }

    @Override
    public List<String> GetGlobalVariables() {
        if (this.Processes == null) {
            return this.IndexedProcessDefinition.GetGlobalVariables();
        }
        List<String> list = new ArrayList<>();
        for (int i = 0; i < this.Processes.size(); i++) {
            List<String> globalVariables = this.Processes.get(i).GetGlobalVariables();
            Ultility.addList(list, globalVariables);
        }
        return list;
    }

    @Override
    public List<String> GetChannels() {
        if (this.Processes == null) {
            return this.IndexedProcessDefinition.Process.GetChannels();
        }
        List<String> list = new ArrayList<>();
        for (int i = 0; i < this.Processes.size(); i++) {
            List<String> channels = this.Processes.get(i).GetChannels();
            Ultility.addList(list, channels);
        }
        return list;
    }

    @Override
    public TCJProcess ClearConstant(Map<String, Expression> constMapping) throws ParsingException {
        List<TCJProcess> list = this.Processes;
        if (this.Processes == null) {
            if (SpecificationBase.isParsing) {
                return new IndexChoice(this.IndexedProcessDefinition.ClearConstant(constMapping));
            }
            list = this.IndexedProcessDefinition.GetIndexedProcesses(constMapping);
        }
        List<TCJProcess> list2 = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            TCJProcess TCJProcess = list.get(i).ClearConstant(constMapping);
            if (!(TCJProcess instanceof Stop)) {
                list2.add(TCJProcess);
            }
        }
        if (list2.size() == 0) {
            return new Stop();
        }
        return new IndexChoice(list2);
    }

    @Override
    public boolean MustBeAbstracted() {
        if (this.Processes == null) {
            return this.IndexedProcessDefinition.Process.MustBeAbstracted();
        }
        for (TCJProcess t : this.Processes) {
            if (t.MustBeAbstracted()) {
                return true;
            }
        }
        return false;
        //return this.Processes.Any((TCJProcess t) => t.MustBeAbstracted());
    }

    @Override
    public TCJProcess SetTimer(Valuation globalEnv, List<List<Integer>> clockBound, Map<Integer, Integer> mapping) {
        List<TCJProcess> list = new ArrayList<>();
        for (int i = 0; i < this.Processes.size(); i++) {
            list.add(this.Processes.get(i).SetTimer(globalEnv, clockBound, mapping));
        }
        return new IndexChoice(list);
    }

    @Override
    public boolean IsTimeImmediate() {
        if (this.Processes == null) {
            return this.IndexedProcessDefinition.Process.IsTimeImmediate();
        }
        for (int i = 0; i < this.Processes.size(); i++) {
            if (this.Processes.get(i).IsTimeImmediate()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean IsSkip() {
        if (this.Processes == null) {
            return this.IndexedProcessDefinition.Process.IsSkip();
        }
        for (int i = 0; i < this.Processes.size(); i++) {
            if (!this.Processes.get(i).IsSkip()) {
                return false;
            }
        }
        return true;
    }
}
