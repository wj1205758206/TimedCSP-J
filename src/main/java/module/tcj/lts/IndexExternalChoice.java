package module.tcj.lts;

import common.ParsingException;
import common.classes.expressions.Valuation;
import common.classes.expressions.expressionclass.Expression;
import common.classes.moduleinterface.SpecificationBase;
import common.classes.ultility.Ultility;
import module.tcj.assertions.TCJDataStore;

import java.util.*;

public final class IndexExternalChoice extends TCJProcess {

    public List<TCJProcess> Processes;

    public IndexedProcess IndexedProcessDefinition;

    public IndexExternalChoice(IndexedProcess definition) {
        this.IndexedProcessDefinition = definition;
    }

    public IndexExternalChoice(List<TCJProcess> processes) {
        this.Processes = new ArrayList<>();
        Map<String, Boolean> sortedDictionary = new TreeMap<>();
        for (TCJProcess TCJProcess : processes) {
            if (TCJProcess instanceof IndexExternalChoice && ((IndexExternalChoice) TCJProcess).Processes != null) {
                List<TCJProcess> processes2 = ((IndexExternalChoice) TCJProcess).Processes;
                for (TCJProcess process : processes2) {
                    if (!sortedDictionary.containsKey(process.ProcessID)) {
                        this.Processes.add(process);
                        sortedDictionary.put(process.ProcessID, false);
                    }
                }
//                using (List<TCJProcess>.Enumerator enumerator2 = processes2.GetEnumerator())
//                {
//                    while (enumerator2.MoveNext())
//                    {
//                        TCJProcess TCJProcess2 = enumerator2.Current;
//                        if (!sortedDictionary.ContainsKey(TCJProcess2.ProcessID))
//                        {
//                            this.Processes.Add(TCJProcess2);
//                            sortedDictionary.Add(TCJProcess2.ProcessID, false);
//                        }
//                    }
//                    continue;
//                }
            }
            if (!sortedDictionary.containsKey(TCJProcess.ProcessID)) {
                this.Processes.add(TCJProcess);
                sortedDictionary.put(TCJProcess.ProcessID, false);
            }
        }
        if (this.Processes.size() > 1) {
            StringBuilder stringBuilder = new StringBuilder();
            for (String value : sortedDictionary.keySet()) {
                stringBuilder.append("*");
                stringBuilder.append(value);
            }
            this.ProcessID = TCJDataStore.DataManager.InitializeProcessID(stringBuilder.toString());
            return;
        }
        this.ProcessID = this.Processes.get(0).ProcessID;
    }

    @Override
    public List<ZoneConfiguration> MoveOneStep(ZoneConfiguration eStep) throws CloneNotSupportedException, ParsingException {
        List<ZoneConfiguration> list = new ArrayList<>();
        for (int i = 0; i < this.Processes.size(); i++) {
            List<ZoneConfiguration> list2 = this.Processes.get(i).MoveOneStep(eStep);
            for (int j = 0; j < list2.size(); j++) {
                ZoneConfiguration zoneConfiguration = list2.get(j);
                if (zoneConfiguration.event == "τ") {
                    List<TCJProcess> list3 = new ArrayList<>(this.Processes);
                    list3.add(i, zoneConfiguration.Process);
                    IndexExternalChoice process = new IndexExternalChoice(list3);
                    zoneConfiguration.Process = process;
                }
                list.add(zoneConfiguration);
            }
        }
        return list;
    }

    @Override
    public void GetDiscretEventTransitions(TickConfiguration eStep, List<TickConfiguration> toReturn) throws CloneNotSupportedException, ParsingException {
        for (int i = 0; i < this.Processes.size(); i++) {
            List<TickConfiguration> list = new ArrayList<>();
            this.Processes.get(i).GetDiscretEventTransitions(eStep, list);
            for (int j = 0; j < list.size(); j++) {
                TickConfiguration tickConfiguration = list.get(j);
                if (tickConfiguration.event == "τ") {
                    List<TCJProcess> list2 = new ArrayList<>(this.Processes);
                    list2.add(i, tickConfiguration.Process);
                    IndexExternalChoice process = new IndexExternalChoice(list2);
                    tickConfiguration.Process = process;
                    toReturn.add(tickConfiguration);
                } else {
                    toReturn.add(tickConfiguration);
                }
            }
        }
    }

    @Override
    public TCJProcess GetProcessAfterDelay(TickConfiguration eStep) throws ParsingException {
        List<TCJProcess> list = new ArrayList<>();
        for (int i = 0; i < this.Processes.size(); i++) {
            TCJProcess processAfterDelay = this.Processes.get(i).GetProcessAfterDelay(eStep);
            if (processAfterDelay == null) {
                return null;
            }
            list.add(processAfterDelay);
        }
        return new IndexExternalChoice(list);
    }

    @Override
    public List<ZoneConfigurationWithChannelData> SyncOutput(ZoneConfiguration eStep) throws ParsingException {
        List<ZoneConfigurationWithChannelData> list = new ArrayList<>();
        for (int i = 0; i < this.Processes.size(); i++) {
            list.addAll(this.Processes.get(i).SyncOutput(eStep));
        }
        return list;
    }

    @Override
    public List<ZoneConfiguration> SyncInput(ZoneConfigurationWithChannelData eStep) throws ParsingException {
        List<ZoneConfiguration> list = new ArrayList<>();
        for (int i = 0; i < this.Processes.size(); i++) {
            list.addAll(this.Processes.get(i).SyncInput(eStep));
        }
        return list;
    }

    @Override
    public void SyncOutput(TickConfiguration eStep, List<TickConfigurationWithChannelData> toReturn) throws ParsingException {
        for (TCJProcess TCJProcess : this.Processes) {
            TCJProcess.SyncOutput(eStep, toReturn);
        }
    }

    @Override
    public void SyncInput(TickConfigurationWithChannelData eStep, List<TickConfiguration> toReturn) throws ParsingException {
        for (TCJProcess TCJProcess : this.Processes) {
            TCJProcess.SyncInput(eStep, toReturn);
        }
    }

    @Override
    public String toString() {
        if (this.Processes == null) {
            return "[*]" + this.IndexedProcessDefinition;
        }
        String str = "(" + this.Processes.get(0);
        for (int i = 1; i < this.Processes.size(); i++) {
            str += "[*]";
            str += this.Processes.get(i).toString();
        }
        return str + ")";
    }

    @Override
    public HashSet<String> GetAlphabets(Map<String, String> visitedDefinitionRefs) throws ParsingException {
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
                return new IndexExternalChoice(this.IndexedProcessDefinition.ClearConstant(constMapping));
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
        return new IndexExternalChoice(list2);
    }

    @Override
    public boolean MustBeAbstracted() {
        if (this.Processes == null) {
            return this.IndexedProcessDefinition.Process.MustBeAbstracted();
        }
        for (int i = 0; i < this.Processes.size(); i++) {
            if (this.Processes.get(i).MustBeAbstracted()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public TCJProcess SetTimer(Valuation globalEnv, List<List<Integer>> clockBound, Map<Integer, Integer> mapping) throws ParsingException {
        List<TCJProcess> list = new ArrayList<>();
        for (int i = 0; i < this.Processes.size(); i++) {
            list.add(this.Processes.get(i).SetTimer(globalEnv, clockBound, mapping));
        }
        return new IndexExternalChoice(list);
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
