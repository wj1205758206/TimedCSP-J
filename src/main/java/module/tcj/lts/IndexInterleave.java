package module.tcj.lts;

import common.ParsingException;
import common.classes.datastructure.DBM;
import common.classes.expressions.Valuation;
import common.classes.expressions.expressionclass.Expression;
import common.classes.moduleinterface.AssertionBase;
import common.classes.moduleinterface.SpecificationBase;
import common.classes.ultility.Ultility;
import module.tcj.assertions.TCJDataStore;

import java.util.*;

public final class IndexInterleave extends TCJProcess {
    public List<TCJProcess> Processes;
    public IndexedProcess IndexedProcessDefinition;

    public IndexInterleave(IndexedProcess definition) {
        this.IndexedProcessDefinition = definition;
    }

    public IndexInterleave(List<TCJProcess> processes) {
        this.Processes = processes;
        String text = this.Processes.get(0).ProcessID;
        for (int i = 1; i < this.Processes.size(); i++) {
            text = text + "|" + this.Processes.get(i).ProcessID;
        }
        this.ProcessID = TCJDataStore.DataManager.InitializeProcessID(text);
    }

    @Override
    public List<ZoneConfiguration> MoveOneStep(ZoneConfiguration eStep) throws CloneNotSupportedException, ParsingException {
        List<ZoneConfiguration> list = new ArrayList<>();
        List<List<ZoneConfiguration>> list2 = new ArrayList<>();
        for (int i = 0; i < this.Processes.size(); i++) {
            TCJProcess TCJProcess = this.Processes.get(i);
            List<ZoneConfiguration> list3 = TCJProcess.MoveOneStep(eStep);
            list2.add(new ArrayList<>());
            for (int j = 0; j < list3.size(); j++) {
                ZoneConfiguration zoneConfiguration = list3.get(j);
                if (zoneConfiguration.event != "terminate") {
                    if (AssertionBase.calculateParticipatingProcess) {
                        zoneConfiguration.participatingProcesses = new String[]{String.valueOf(i)};
                    }
                    List<TCJProcess> list4 = new ArrayList<>(this.Processes);
                    list4.add(i, zoneConfiguration.Process);
                    IndexInterleave process = new IndexInterleave(list4);
                    zoneConfiguration.Process = process;
                    list.add(zoneConfiguration);
                } else {
                    list2.get(i).add(zoneConfiguration);
                }
            }
            if (SpecificationBase.hasSyncrhonousChannel) {
                this.SynchronousChannelInputOutput(list, i, eStep, null);
            }
        }
        this.TerminationSteps(eStep, list2, list);
        return list;
    }

    private void SynchronousChannelInputOutput(List<ZoneConfiguration> returnList, int i, ZoneConfiguration step, String evt) throws ParsingException {
        List<ZoneConfigurationWithChannelData> list = this.Processes.get(i).SyncOutput(step);
        for (ZoneConfigurationWithChannelData zoneConfigurationWithChannelData : list) {
            if (evt == null || !(zoneConfigurationWithChannelData.event != evt)) {
                TCJProcess process = zoneConfigurationWithChannelData.Process;
                for (int j = 0; j < this.Processes.size(); j++) {
                    if (j != i) {
                        List<ZoneConfiguration> list2 = this.Processes.get(j).SyncInput(zoneConfigurationWithChannelData);
                        for (ZoneConfiguration zoneConfiguration : list2) {
                            List<TCJProcess> list3 = new ArrayList<>(this.Processes);
                            list3.add(i, process);
                            list3.add(j, zoneConfiguration.Process);
                            IndexInterleave p = new IndexInterleave(list3);
                            ZoneConfiguration zoneConfiguration2 = new ZoneConfiguration(p, zoneConfigurationWithChannelData.event, zoneConfigurationWithChannelData.displayName, zoneConfigurationWithChannelData.globalEnv, false, DBM.returnUrgentDBM(zoneConfigurationWithChannelData.DBM, zoneConfiguration.DBM));
                            zoneConfiguration2.isAtomic = (zoneConfigurationWithChannelData.isAtomic || zoneConfiguration.isAtomic);
                            if (AssertionBase.calculateParticipatingProcess) {
                                zoneConfiguration2.participatingProcesses = new String[]{String.valueOf(i), String.valueOf(j)};
                            }
                            returnList.add(zoneConfiguration2);
                        }
                    }
                }
            }
        }
    }

    private void TerminationSteps(ZoneConfiguration eStep, List<List<ZoneConfiguration>> terminateSteps, List<ZoneConfiguration> toReturn) {
        boolean isAtomic = false;
        for (List<ZoneConfiguration> list : terminateSteps) {
            if (list.size() == 0) {
                return;
            }
            for (ZoneConfiguration zoneConfiguration : list) {
                if (zoneConfiguration.isAtomic) {
                    isAtomic = true;
                }
            }
        }
        ZoneConfiguration zoneConfiguration2 = new ZoneConfiguration(new Stop(), "terminate", null, eStep.globalEnv, false, eStep.DBM);
        zoneConfiguration2.isAtomic = isAtomic;
        if (AssertionBase.calculateParticipatingProcess) {
            zoneConfiguration2.participatingProcesses = new String[this.Processes.size()];
            for (int i = 0; i < this.Processes.size(); i++) {
                zoneConfiguration2.participatingProcesses[i] = String.valueOf(i);
            }
        }
        toReturn.add(zoneConfiguration2);
    }

    @Override
    public List<ZoneConfigurationWithChannelData> SyncOutput(ZoneConfiguration eStep) throws ParsingException {
        List<ZoneConfigurationWithChannelData> list = new ArrayList<>();
        for (int i = 0; i < this.Processes.size(); i++) {
            TCJProcess TCJProcess = this.Processes.get(i);
            List<ZoneConfigurationWithChannelData> list2 = TCJProcess.SyncOutput(eStep);
            for (int j = 0; j < list2.size(); j++) {
                ZoneConfiguration zoneConfiguration = list2.get(j);
                List<TCJProcess> list3 = new ArrayList<>(this.Processes.size());
                list3.addAll(this.Processes);
                list3.add(i, zoneConfiguration.Process);
                zoneConfiguration.Process = new IndexInterleave(list3);
                list.add(list2.get(j));
            }
        }
        return list;
    }

    @Override
    public List<ZoneConfiguration> SyncInput(ZoneConfigurationWithChannelData eStep) throws ParsingException {
        List<ZoneConfiguration> list = new ArrayList<>();
        for (int i = 0; i < this.Processes.size(); i++) {
            TCJProcess TCJProcess = this.Processes.get(i);
            List<ZoneConfiguration> list2 = TCJProcess.SyncInput(eStep);
            if (list2.size() > 0) {
                for (int j = 0; j < list2.size(); j++) {
                    ZoneConfiguration zoneConfiguration = list2.get(j);
                    List<TCJProcess> list3 = new ArrayList<>(this.Processes.size());
                    list3.addAll(this.Processes);
                    list3.add(i, zoneConfiguration.Process);
                    IndexInterleave process = new IndexInterleave(list3);
                    zoneConfiguration.Process = process;
                    list.add(zoneConfiguration);
                }
            }
        }
        return list;
    }

    @Override
    public void GetDiscretEventTransitions(TickConfiguration eStep, List<TickConfiguration> toReturn) throws CloneNotSupportedException, ParsingException {
        boolean flag = true;
        boolean flag2 = false;
        for (int i = 0; i < this.Processes.size(); i++) {
            TCJProcess TCJProcess = this.Processes.get(i);
            List<TickConfiguration> list = new ArrayList<>();
            TCJProcess.GetDiscretEventTransitions(eStep, list);
            boolean flag3 = false;
            for (int j = 0; j < list.size(); j++) {
                TickConfiguration tickConfiguration = list.get(j);
                if (tickConfiguration.event != "terminate") {
                    if (AssertionBase.calculateParticipatingProcess) {
                        tickConfiguration.participatingProcesses = new String[]{String.valueOf(i)};
                    }
                    List<TCJProcess> list2 = new ArrayList<>(this.Processes);
                    list2.add(i, tickConfiguration.Process);
                    IndexInterleave process = new IndexInterleave(list2);
                    tickConfiguration.Process = process;
                    toReturn.add(tickConfiguration);
                } else {
                    if (tickConfiguration.isAtomic) {
                        flag2 = true;
                    }
                    flag3 = true;
                }
            }
            if (SpecificationBase.hasSyncrhonousChannel) {
                this.SynchronousChannelInputOutput(toReturn, i, eStep, null);
            }
            if (!flag3) {
                flag = false;
            }
        }
        if (flag) {
            TickConfiguration tickConfiguration2 = new TickConfiguration(new Stop(), "terminate", null, eStep.globalEnv, false, false);
            if (flag2) {
                tickConfiguration2.isAtomic = true;
            }
            if (AssertionBase.calculateParticipatingProcess) {
                tickConfiguration2.participatingProcesses = new String[this.Processes.size()];
                for (int k = 0; k < this.Processes.size(); k++) {
                    tickConfiguration2.participatingProcesses[k] = String.valueOf(k);
                }
            }
            toReturn.add(tickConfiguration2);
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
        return new IndexInterleave(list);
    }

    private void SynchronousChannelInputOutput(List<TickConfiguration> returnList, int i, TickConfiguration step, String evt) throws ParsingException {
        List<TickConfigurationWithChannelData> list = new ArrayList<>();
        this.Processes.get(i).SyncOutput(step, list);
        for (TickConfigurationWithChannelData tickConfigurationWithChannelData : list) {
            if (evt == null || !(tickConfigurationWithChannelData.event != evt)) {
                TCJProcess process = tickConfigurationWithChannelData.Process;
                for (int j = 0; j < this.Processes.size(); j++) {
                    if (j != i) {
                        List<TickConfiguration> list2 = new ArrayList<>();
                        this.Processes.get(j).SyncInput(tickConfigurationWithChannelData, list2);
                        for (TickConfiguration tickConfiguration : list2) {
                            List<TCJProcess> list3 = new ArrayList<>(this.Processes);
                            list3.add(i, process);
                            list3.add(j, tickConfiguration.Process);
                            IndexInterleave p = new IndexInterleave(list3);
                            TickConfiguration tickConfiguration2 = new TickConfiguration(p, tickConfigurationWithChannelData.event, tickConfigurationWithChannelData.displayName, tickConfigurationWithChannelData.globalEnv, false, tickConfiguration.IsUrgent || tickConfigurationWithChannelData.IsUrgent);
                            tickConfiguration2.isAtomic = (tickConfigurationWithChannelData.isAtomic || tickConfiguration.isAtomic);
                            if (AssertionBase.calculateParticipatingProcess) {
                                tickConfiguration2.participatingProcesses = new String[]{String.valueOf(i), String.valueOf(j)};
                            }
                            returnList.add(tickConfiguration2);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void SyncOutput(TickConfiguration eStep, List<TickConfigurationWithChannelData> toReturn) throws ParsingException {
        for (int i = 0; i < this.Processes.size(); i++) {
            TCJProcess TCJProcess = this.Processes.get(i);
            List<TickConfigurationWithChannelData> list = new ArrayList<>();
            TCJProcess.SyncOutput(eStep, list);
            for (TickConfigurationWithChannelData tickConfigurationWithChannelData : list) {
                List<TCJProcess> list2 = new ArrayList<>(this.Processes.size());
                list2.addAll(this.Processes);
                list2.add(i, tickConfigurationWithChannelData.Process);
                tickConfigurationWithChannelData.Process = new IndexInterleave(list2);
                toReturn.add(tickConfigurationWithChannelData);
            }
        }
    }

    @Override
    public void SyncInput(TickConfigurationWithChannelData eStep, List<TickConfiguration> toReturn) throws ParsingException {
        for (int i = 0; i < this.Processes.size(); i++) {
            TCJProcess TCJProcess = this.Processes.get(i);
            List<TickConfiguration> list = new ArrayList<>();
            TCJProcess.SyncInput(eStep, list);
            for (TickConfiguration tickConfiguration : list) {
                List<TCJProcess> list2 = new ArrayList<>(this.Processes.size());
                list2.addAll(this.Processes);
                list2.add(i, tickConfiguration.Process);
                IndexInterleave process = new IndexInterleave(list2);
                tickConfiguration.Process = process;
                toReturn.add(tickConfiguration);
            }
        }
    }

    @Override
    public HashSet<String> GetAlphabets(Map<String, String> visitedDefinitionRefs) throws ParsingException {
        if (this.Processes == null) {
            return this.IndexedProcessDefinition.Process.GetAlphabets(visitedDefinitionRefs);
        }
        HashSet<String> hashSet = new HashSet<>();
        for (int i = 0; i < this.Processes.size(); i++) {
            TCJProcess TCJProcess = this.Processes.get(i);
            hashSet.addAll(TCJProcess.GetAlphabets(visitedDefinitionRefs));
        }
        return hashSet;
    }

    @Override
    public List<String> GetGlobalVariables() {
        if (this.Processes == null) {
            return this.IndexedProcessDefinition.GetGlobalVariables();
        }
        List<String> list = new ArrayList<>();
        for (TCJProcess TCJProcess : this.Processes) {
            Ultility.addList(list, TCJProcess.GetGlobalVariables());
        }
        return list;
    }

    @Override
    public List<String> GetChannels() {
        if (this.Processes == null) {
            return this.IndexedProcessDefinition.Process.GetChannels();
        }
        List<String> list = new ArrayList<>();
        for (TCJProcess TCJProcess : this.Processes) {
            Ultility.addList(list, TCJProcess.GetChannels());
        }
        return list;
    }

    @Override
    public String toString() {
        if (this.Processes == null) {
            return "|||" + this.IndexedProcessDefinition;
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < this.Processes.size(); i++) {
            TCJProcess arg = this.Processes.get(i);
            stringBuilder.append(arg + "\r\n|||");
        }
        return "(" + stringBuilder.toString().substring(0, stringBuilder.toString().lastIndexOf("|")) + ")";
    }

    @Override
    public TCJProcess ClearConstant(Map<String, Expression> constMapping) throws ParsingException {
        List<TCJProcess> list = new ArrayList<>();
        if (!SpecificationBase.isParsing) {
            List<TCJProcess> list2 = this.Processes;
            if (this.Processes == null) {
                list2 = this.IndexedProcessDefinition.GetIndexedProcesses(constMapping);
            }
            int count = list2.size();
            Map<String, Integer> dictionary = new HashMap<>(count);
            int i = 0;
            while (i < count) {
                TCJProcess TCJProcess = list2.get(i).ClearConstant(constMapping);
                if (TCJProcess instanceof IndexInterleave) {
                    List<TCJProcess> processes = ((IndexInterleave) TCJProcess).Processes;
                    for (TCJProcess tcjProcess : processes) {
                        String processID = tcjProcess.ProcessID;
                        if (!dictionary.containsKey(processID)) {
                            list.add(tcjProcess);
                            dictionary.put(processID, 1);
                        } else {
                            dictionary.put(processID, dictionary.get(processID) + 1);
                        }
                    }
                    i++;
                    continue;
//                    using (List<TCJProcess>.Enumerator enumerator = processes.GetEnumerator())
//                    {
//                        while (enumerator.MoveNext())
//                        {
//                            TCJProcess TCJProcess2 = enumerator.Current;
//                            string processID = TCJProcess2.ProcessID;
//                            if (!dictionary.ContainsKey(processID))
//                            {
//                                list.Add(TCJProcess2);
//                                dictionary.Add(processID, 1);
//                            }
//                            else
//                            {
//                                dictionary[processID]++;
//                            }
//                        }
//                            //goto IL_215;
//                    }
                    //goto IL_131;
                }
                // goto IL_131;
                //IL_215:
                //i++;
                //continue;
                //IL_131:
                else if (TCJProcess instanceof IndexInterleaveAbstract) {
                    IndexInterleaveAbstract indexInterleaveAbstract = (IndexInterleaveAbstract) TCJProcess;
                    for (TCJProcess tcjProcess : indexInterleaveAbstract.Processes) {
                        String processID2 = tcjProcess.ProcessID;
                        if (!dictionary.containsKey(processID2)) {
                            list.add(tcjProcess);
                            dictionary.put(processID2, indexInterleaveAbstract.ProcessesActualSize.get(processID2));
                        } else {
                            dictionary.put(processID2, dictionary.get(processID2) + indexInterleaveAbstract.ProcessesCounter.get(processID2));
                        }
                    }
                    i++;
                    continue;

//                    using (List<TCJProcess>.Enumerator enumerator2 = indexInterleaveAbstract.Processes.GetEnumerator())
//                    {
//                        while (enumerator2.MoveNext())
//                        {
//                            TCJProcess TCJProcess3 = enumerator2.Current;
//                            string processID2 = TCJProcess3.ProcessID;
//                            if (!dictionary.ContainsKey(processID2))
//                            {
//                                list.Add(TCJProcess3);
//                                dictionary.Add(processID2, indexInterleaveAbstract.ProcessesActualSize[processID2]);
//                            }
//                            else
//                            {
//                                dictionary[processID2] += indexInterleaveAbstract.ProcessesCounter[processID2];
//                            }
//                        }
//                            //goto IL_215;
//                    }
                }
                if (!dictionary.containsKey(TCJProcess.ProcessID)) {
                    list.add(TCJProcess);
                    dictionary.put(TCJProcess.ProcessID, 1);
                    //goto IL_215;
                    i++;
                    continue;
                }
                dictionary.put(TCJProcess.ProcessID, dictionary.get(TCJProcess.ProcessID) + 1);
                //dictionary[TCJProcess.ProcessID] = dictionary[TCJProcess.ProcessID] + 1;
                //goto IL_215;
                i++;
                continue;
            }
            for (Map.Entry<String, Integer> keyValuePair : dictionary.entrySet()) {
                if (keyValuePair.getValue() > 1 || keyValuePair.getValue() == -1) {
                    IndexInterleaveAbstract indexInterleaveAbstract = new IndexInterleaveAbstract(list, dictionary);
                    indexInterleaveAbstract.ProcessesActualSize = new HashMap<>(dictionary);
                    return indexInterleaveAbstract;
                }
            }
//            foreach (KeyValuePair<string, int> keyValuePair in dictionary)
//            {
//                if (keyValuePair.Value > 1 || keyValuePair.Value == -1)
//                {
//                    return new IndexInterleaveAbstract(list, dictionary)
//                    {
//                        ProcessesActualSize = new Dictionary<string, int>(dictionary)
//                    };
//                }
//            }
            return new IndexInterleave(list);
        }
        if (this.Processes == null) {
            return new IndexInterleave(this.IndexedProcessDefinition.ClearConstant(constMapping));
        }
        for (TCJProcess TCJProcess4 : this.Processes) {
            list.add(TCJProcess4.ClearConstant(constMapping));
        }
        return new IndexInterleave(list);
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
    public TCJProcess GetTopLevelConcurrency(List<String> visitedDef) {
        return this;
    }

    @Override
    public TCJProcess SetTimer(Valuation globalEnv, List<List<Integer>> clockBound, Map<Integer, Integer> mapping) throws ParsingException {
        List<TCJProcess> list = new ArrayList<>();
        for (int i = 0; i < this.Processes.size(); i++) {
            list.add(this.Processes.get(i).SetTimer(globalEnv, clockBound, mapping));
        }
        return new IndexInterleave(list);
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
