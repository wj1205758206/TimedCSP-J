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

public final class IndexParallel extends TCJProcess {
    public List<TCJProcess> Processes;

    private HashSet<String>[] Alphabets;

    public IndexedProcess IndexedProcessDefinition;

    public IndexParallel(IndexedProcess indexedProcessDefinition) {
        this.IndexedProcessDefinition = indexedProcessDefinition;
    }

    public IndexParallel(List<TCJProcess> processes) {
        this.Processes = processes;
        this.ProcessID = "&" + this.Processes.get(0).ProcessID;
        for (int i = 1; i < this.Processes.size(); i++) {
            this.ProcessID = this.ProcessID + "$" + this.Processes.get(i).ProcessID;
        }
        this.ProcessID = TCJDataStore.DataManager.InitializeProcessID(this.ProcessID);
    }

    private IndexParallel(List<TCJProcess> processes, HashSet<String>[] alphabets) {
        this.Processes = processes;
        this.Alphabets = alphabets;
        this.ProcessID = "&" + this.Processes.get(0).ProcessID;
        for (int i = 1; i < this.Processes.size(); i++) {
            this.ProcessID = this.ProcessID + "$" + this.Processes.get(i).ProcessID;
        }
        this.ProcessID = TCJDataStore.DataManager.InitializeProcessID(this.ProcessID);
    }

    private void IdentifySharedEventsAndVariables() {
        try {
            Map<String, Integer> dictionary = new HashMap<>();
            this.Alphabets = new HashSet[this.Processes.size()];
            for (int i = 0; i < this.Processes.size(); i++) {
                HashSet<String> alphabets = this.Processes.get(i).GetAlphabets(new HashMap<>());
                alphabets.add("terminate");
                this.Alphabets[i] = alphabets;
                for (String key : alphabets) {
                    if (!dictionary.containsKey(key)) {
                        dictionary.put(key, 1);
                    } else {
                        dictionary.put(key, dictionary.get(key) + 1);
                    }
                }
            }
            for (int j = 0; j < this.Processes.size(); j++) {
                HashSet<String> hashSet = this.Alphabets[j];
                for (Map.Entry<String, Integer> keyValuePair : dictionary.entrySet()) {
                    if (keyValuePair.getValue() == 1 && hashSet.contains(keyValuePair.getKey())) {
                        hashSet.remove(keyValuePair.getKey());
                    }
                }
            }
        } catch (Exception exception) {
            this.Alphabets = null;
            throw new RuntimeException("IdentifySharedEventsAndVariables: " + exception.getMessage());
        }
    }

    @Override
    public List<ZoneConfiguration> MoveOneStep(ZoneConfiguration eStep) throws CloneNotSupportedException, ParsingException {
        if (this.Alphabets == null) {
            this.IdentifySharedEventsAndVariables();
        }
        List<ZoneConfiguration> list = new ArrayList<>();
        List<String> list2 = new ArrayList<>();
        List<String> list3 = new ArrayList<>();
        Map<String, List<ZoneConfiguration>> dictionary = new HashMap<>();
        for (int i = 0; i < this.Processes.size(); i++) {
            List<ZoneConfiguration> list4 = this.Processes.get(i).MoveOneStep(eStep);
            List<String> list5 = new ArrayList<>(list4.size());
            for (int j = 0; j < list4.size(); j++) {
                ZoneConfiguration zoneConfiguration = list4.get(j);
                String event = zoneConfiguration.event;
                if (!this.Alphabets[i].contains(event) || zoneConfiguration.isDataOperation) {
                    if (AssertionBase.calculateParticipatingProcess) {
                        zoneConfiguration.participatingProcesses = new String[]{String.valueOf(i)};
                    }
                    List<TCJProcess> list6 = new ArrayList<>(this.Processes);
                    list6.set(i, zoneConfiguration.Process);
                    zoneConfiguration.Process = new IndexParallel(list6, this.Alphabets);
                    list.add(zoneConfiguration);
                } else {
                    list5.add(event);
                    String key = event + "$" + i;
                    if (!dictionary.containsKey(key)) {
                        dictionary.put(key, new ArrayList<>());
                    }
                    dictionary.get(key).add(zoneConfiguration);
                    if (!list2.contains(event)) {
                        list2.add(event);
                    }
                }
            }
            for (String item : this.Alphabets[i]) {
                if (!list5.contains(item) && !list3.contains(item)) {
                    list3.add(item);
                }
            }
            if (SpecificationBase.hasSyncrhonousChannel) {
                this.SynchronousChannelInputOutput(list, i, eStep, this.Processes);
            }
        }
        int count = list3.size();
        for (int k = 0; k < count; k++) {
            list2.remove(list3.get(k));
        }
        List<Boolean> list7 = null;
        for (String text : list2) {
            List<List<TCJProcess>> list8 = new ArrayList<>();
            list8.add(new ArrayList<>());
            if (SpecificationBase.hasAtomicEvent) {
                list7 = new ArrayList<>();
                list7.add(false);
            }
            List<Boolean> list9 = new ArrayList<>();
            list9.add(false);
            List<String> list10 = new ArrayList<>();
            for (int l = 0; l < this.Processes.size(); l++) {
                if (this.Alphabets[l].contains(text)) {
                    list10.add(String.valueOf(l));
                    List<ZoneConfiguration> list11 = dictionary.get(text + "$" + l);
                    List<List<TCJProcess>> list12 = new ArrayList<>(list8.size());
                    for (ZoneConfiguration zoneConfiguration2 : list11) {
                        if (list8.get(0).size() == l) {
                            for (int m = 0; m < list8.size(); m++) {
                                list8.get(m).add(zoneConfiguration2.Process);
                                if (zoneConfiguration2.isAtomic) {
                                    for (int n = 0; n < list7.size(); n++) {
                                        list7.set(n, true);
                                    }
                                }
                                if (zoneConfiguration2.DBM.isUrgent()) {
                                    for (int num = 0; num < list9.size(); num++) {
                                        list9.set(num, true);
                                    }
                                }
                            }
                        } else {
                            for (int num2 = 0; num2 < list8.size(); num2++) {
                                List<TCJProcess> list13 = new ArrayList<>();
                                List<TCJProcess> list14 = list8.get(num2);
                                for (int num3 = 0; num3 < list14.size() - 1; num3++) {
                                    list13.add(list14.get(num3));
                                }
                                list13.add(zoneConfiguration2.Process);
                                list12.add(list13);
                                if (SpecificationBase.hasAtomicEvent) {
                                    if (!list7.get(num2)) {
                                        list7.add(zoneConfiguration2.isAtomic);
                                    } else {
                                        list7.add(true);
                                    }
                                }
                                if (!list9.get(num2)) {
                                    list9.add(zoneConfiguration2.DBM.isUrgent());
                                } else {
                                    list9.add(true);
                                }
                            }
                        }
                    }
                    list8.addAll(list12);
                } else {
                    for (int num4 = 0; num4 < list8.size(); num4++) {
                        list8.get(num4).add(this.Processes.get(l));
                    }
                }
            }
            for (int num5 = 0; num5 < list8.size(); num5++) {
                List<TCJProcess> processes = list8.get(num5);
                IndexParallel p = new IndexParallel(processes, this.Alphabets);
                ZoneConfiguration zoneConfiguration3 = new ZoneConfiguration(p, text, null, eStep.globalEnv, false, list9.get(num5) ? eStep.DBM.addUrgency() : eStep.DBM);
                if (SpecificationBase.hasAtomicEvent) {
                    zoneConfiguration3.isAtomic = list7.get(num5);
                }
                if (AssertionBase.calculateParticipatingProcess) {
                    zoneConfiguration3.participatingProcesses = (String[]) list10.toArray();
                }
                list.add(zoneConfiguration3);
            }
        }
        return list;
    }

    private void SynchronousChannelInputOutput(List<ZoneConfiguration> returnList, int i, ZoneConfiguration step, List<TCJProcess> clockSetProcesses) throws ParsingException {
        List<ZoneConfigurationWithChannelData> list = clockSetProcesses.get(i).SyncOutput(step);
        for (ZoneConfigurationWithChannelData zoneConfigurationWithChannelData : list) {
            TCJProcess process = zoneConfigurationWithChannelData.Process;
            for (int j = 0; j < clockSetProcesses.size(); j++) {
                if (j != i) {
                    List<ZoneConfiguration> list2 = clockSetProcesses.get(j).SyncInput(zoneConfigurationWithChannelData);
                    for (ZoneConfiguration zoneConfiguration : list2) {
                        List<TCJProcess> list3 = new ArrayList<>(clockSetProcesses);
                        list3.set(i, process);
                        list3.set(j, zoneConfiguration.Process);
                        IndexParallel p = new IndexParallel(list3, this.Alphabets);
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

    @Override
    public void GetDiscretEventTransitions(TickConfiguration eStep, List<TickConfiguration> toReturn) throws CloneNotSupportedException, ParsingException {
        if (this.Alphabets == null) {
            this.IdentifySharedEventsAndVariables();
        }
        List<String> list = new ArrayList<>();
        List<String> list2 = new ArrayList<>();
        Map<String, List<TickConfiguration>> dictionary = new HashMap<>();
        for (int i = 0; i < this.Processes.size(); i++) {
            List<TickConfiguration> list3 = new ArrayList<>();
            this.Processes.get(i).GetDiscretEventTransitions(eStep, list3);
            List<String> list4 = new ArrayList<>(list3.size());
            for (int j = 0; j < list3.size(); j++) {
                TickConfiguration tickConfiguration = list3.get(j);
                String event = tickConfiguration.event;
                if (!this.Alphabets[i].contains(event) || tickConfiguration.isDataOperation) {
                    if (AssertionBase.calculateParticipatingProcess) {
                        tickConfiguration.participatingProcesses = new String[]{String.valueOf(i)};
                    }
                    List<TCJProcess> list5 = new ArrayList<>(this.Processes);
                    list5.set(i, tickConfiguration.Process);
                    tickConfiguration.Process = new IndexParallel(list5, this.Alphabets);
                    toReturn.add(tickConfiguration);
                } else {
                    list4.add(event);
                    String key = event + "$" + i;
                    if (!dictionary.containsKey(key)) {
                        dictionary.put(key, new ArrayList<>());
                    }
                    dictionary.get(key).add(tickConfiguration);
                    if (!list.contains(event)) {
                        list.add(event);
                    }
                }
            }
            for (String item : this.Alphabets[i]) {
                if (!list4.contains(item) && !list2.contains(item)) {
                    list2.add(item);
                }
            }
            if (SpecificationBase.hasSyncrhonousChannel) {
                this.SynchronousChannelInputOutput(toReturn, i, eStep, null, this.Processes);
            }
        }
        int count = list2.size();
        for (int k = 0; k < count; k++) {
            list.remove(list2.get(k));
        }
        List<Boolean> list6 = null;
        for (String text : list) {
            List<List<TCJProcess>> list7 = new ArrayList<>();
            list7.add(new ArrayList<>());
            if (SpecificationBase.hasAtomicEvent) {
                list6 = new ArrayList<>();
                list6.add(false);
            }
            List<Boolean> list8 = new ArrayList<>();
            list8.add(false);
            List<String> list9 = new ArrayList<>();
            for (int l = 0; l < this.Processes.size(); l++) {
                if (this.Alphabets[l].contains(text)) {
                    list9.add(String.valueOf(l));
                    List<TickConfiguration> list10 = dictionary.get(text + "$" + l);
                    List<List<TCJProcess>> list11 = new ArrayList<>(list7.size());
                    for (TickConfiguration tickConfiguration2 : list10) {
                        if (list7.get(0).size() == l) {
                            for (int m = 0; m < list7.size(); m++) {
                                list7.get(m).add(tickConfiguration2.Process);
                                if (tickConfiguration2.isDataOperation) {
                                    for (int n = 0; n < list6.size(); n++) {
                                        list6.set(n, true);
                                    }
                                }
                                if (tickConfiguration2.IsUrgent) {
                                    for (int num = 0; num < list8.size(); num++) {
                                        list8.set(num, true);
                                    }
                                }
                            }
                        } else {
                            for (int num2 = 0; num2 < list7.size(); num2++) {
                                List<TCJProcess> list12 = new ArrayList<>();
                                List<TCJProcess> list13 = list7.get(num2);
                                for (int num3 = 0; num3 < list13.size() - 1; num3++) {
                                    list12.add(list13.get(num3));
                                }
                                list12.add(tickConfiguration2.Process);
                                list11.add(list12);
                                if (SpecificationBase.hasAtomicEvent) {
                                    if (!list6.get(num2)) {
                                        list6.add(tickConfiguration2.isAtomic);
                                    } else {
                                        list6.add(true);
                                    }
                                }
                                if (!list8.get(num2)) {
                                    list8.add(tickConfiguration2.IsUrgent);
                                } else {
                                    list8.add(true);
                                }
                            }
                        }
                    }
                    list7.addAll(list11);
                } else {
                    for (int num4 = 0; num4 < list7.size(); num4++) {
                        list7.get(num4).add(this.Processes.get(l));
                    }
                }
            }
            for (int num5 = 0; num5 < list7.size(); num5++) {
                List<TCJProcess> processes = list7.get(num5);
                IndexParallel p = new IndexParallel(processes, this.Alphabets);
                TickConfiguration tickConfiguration3 = new TickConfiguration(p, text, null, eStep.globalEnv, false, list8.get(num5));
                if (SpecificationBase.hasAtomicEvent) {
                    tickConfiguration3.isAtomic = list6.get(num5);
                }
                if (AssertionBase.calculateParticipatingProcess) {
                    tickConfiguration3.participatingProcesses = (String[]) list9.toArray();
                }
                toReturn.add(tickConfiguration3);
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
        return new IndexParallel(list, this.Alphabets);
    }

    private void SynchronousChannelInputOutput(List<TickConfiguration> returnList, int i, TickConfiguration step, String evt, List<TCJProcess> clockSetProcesses) throws ParsingException {
        List<TickConfigurationWithChannelData> list = new ArrayList<>();
        clockSetProcesses.get(i).SyncOutput(step, list);
        for (TickConfigurationWithChannelData tickConfigurationWithChannelData : list) {
            if (evt == null || !(tickConfigurationWithChannelData.event != evt)) {
                TCJProcess process = tickConfigurationWithChannelData.Process;
                for (int j = 0; j < clockSetProcesses.size(); j++) {
                    if (j != i) {
                        List<TickConfiguration> list2 = new ArrayList<>();
                        clockSetProcesses.get(j).SyncInput(tickConfigurationWithChannelData, list2);
                        for (TickConfiguration tickConfiguration : list2) {
                            List<TCJProcess> list3 = new ArrayList<>(clockSetProcesses);
                            list3.set(i, process);
                            list3.set(j, tickConfiguration.Process);
                            IndexParallel p = new IndexParallel(list3, this.Alphabets);
                            TickConfiguration tickConfiguration2 = new TickConfiguration(p, tickConfigurationWithChannelData.event,
                                    tickConfigurationWithChannelData.displayName,
                                    tickConfigurationWithChannelData.globalEnv,
                                    false, tickConfigurationWithChannelData.IsUrgent || tickConfiguration.IsUrgent);
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
    public List<ZoneConfigurationWithChannelData> SyncOutput(ZoneConfiguration eStep) throws ParsingException {
        List<ZoneConfigurationWithChannelData> list = new ArrayList<>();
        for (int i = 0; i < this.Processes.size(); i++) {
            TCJProcess TCJProcess = this.Processes.get(i);
            List<ZoneConfigurationWithChannelData> list2 = TCJProcess.SyncOutput(eStep);
            for (int j = 0; j < list2.size(); j++) {
                ZoneConfiguration zoneConfiguration = list2.get(j);
                List<TCJProcess> list3 = new ArrayList<>(this.Processes.size());
                list3.addAll(this.Processes);
                list3.set(i, zoneConfiguration.Process);
                zoneConfiguration.Process = new IndexParallel(list3, this.Alphabets);
                list.add(list2.get(j));
            }
        }
        return list;
    }

    @Override
    public void SyncInput(TickConfigurationWithChannelData eStep, List<TickConfiguration> toReturn) throws ParsingException {
        for (int i = 0; i < this.Processes.size(); i++) {
            TCJProcess TCJProcess = this.Processes.get(i);
            List<TickConfiguration> list = new ArrayList<>();
            TCJProcess.SyncInput(eStep, list);
            for (int j = 0; j < list.size(); j++) {
                TickConfiguration tickConfiguration = list.get(j);
                List<TCJProcess> list2 = new ArrayList<>(this.Processes.size());
                list2.addAll(this.Processes);
                list2.set(i, tickConfiguration.Process);
                tickConfiguration.Process = new IndexParallel(list2, this.Alphabets);
                toReturn.add(tickConfiguration);
            }
        }
    }

    @Override
    public void SyncOutput(TickConfiguration eStep, List<TickConfigurationWithChannelData> toReturn) throws ParsingException {
        for (int i = 0; i < this.Processes.size(); i++) {
            TCJProcess TCJProcess = this.Processes.get(i);
            List<TickConfigurationWithChannelData> list = new ArrayList<>();
            TCJProcess.SyncOutput(eStep, list);
            for (int j = 0; j < list.size(); j++) {
                TickConfiguration tickConfiguration = list.get(j);
                List<TCJProcess> list2 = new ArrayList<>(this.Processes.size());
                list2.addAll(this.Processes);
                list2.set(i, tickConfiguration.Process);
                tickConfiguration.Process = new IndexParallel(list2, this.Alphabets);
                toReturn.add(list.get(j));
            }
        }
    }

    @Override
    public List<ZoneConfiguration> SyncInput(ZoneConfigurationWithChannelData eStep) throws ParsingException {
        List<ZoneConfiguration> list = new ArrayList<>();
        for (int i = 0; i < this.Processes.size(); i++) {
            TCJProcess TCJProcess = this.Processes.get(i);
            List<ZoneConfiguration> list2 = TCJProcess.SyncInput(eStep);
            for (int j = 0; j < list2.size(); j++) {
                ZoneConfiguration zoneConfiguration = list2.get(j);
                List<TCJProcess> list3 = new ArrayList<>(this.Processes.size());
                list3.addAll(this.Processes);
                list3.set(i, zoneConfiguration.Process);
                zoneConfiguration.Process = new IndexParallel(list3, this.Alphabets);
                list.add(zoneConfiguration);
            }
        }
        return list;
    }

    @Override
    public HashSet<String> GetAlphabets(Map<String, String> visitedDefinitionRefs) throws ParsingException {
        if (this.Processes == null) {
            TCJProcess TCJProcess = this.ClearConstant(new HashMap<>());
            return TCJProcess.GetAlphabets(visitedDefinitionRefs);
        }
        HashSet<String> hashSet = new HashSet<String>();
        for (int i = 0; i < this.Processes.size(); i++) {
            TCJProcess TCJProcess2 = this.Processes.get(i);
            hashSet.addAll(TCJProcess2.GetAlphabets(visitedDefinitionRefs));
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
            return "||" + this.IndexedProcessDefinition;
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < this.Processes.size(); i++) {
            TCJProcess TCJProcess = this.Processes.get(i);
            stringBuilder.append(TCJProcess.toString() + "\r\n||");
        }
        String str = stringBuilder.toString();
        return "(" + str.substring(0, str.length() - 1) + ")";
    }

    @Override
    public TCJProcess ClearConstant(Map<String, Expression> constMapping) throws ParsingException {
        List<TCJProcess> list = this.Processes;
        int count;
        List<TCJProcess> list2;
        if (SpecificationBase.isParsing) {
            if (this.Processes == null) {
                return new IndexParallel(this.IndexedProcessDefinition.ClearConstant(constMapping));
            }
            count = this.Processes.size();
            list2 = new ArrayList<>(count);
            for (int i = 0; i < count; i++) {
                list2.add(this.Processes.get(i).ClearConstant(constMapping));
            }
        }
        if (this.Processes == null) {
            list = this.IndexedProcessDefinition.GetIndexedProcesses(constMapping);
        }
        count = list.size();
        list2 = new ArrayList<>(count);
        for (int j = 0; j < count; j++) {
            TCJProcess TCJProcess = list.get(j).ClearConstant(constMapping);
            if (TCJProcess instanceof IndexParallel && ((IndexParallel) TCJProcess).Processes != null) {
                List<TCJProcess> processes = ((IndexParallel) TCJProcess).Processes;
                list2.addAll(processes);
            } else {
                list2.add(TCJProcess);
            }
        }
        return new IndexParallel(list2);
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
        return new IndexParallel(list, this.Alphabets);
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
