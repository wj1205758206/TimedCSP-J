package module.tcj.lts;

import common.ParsingException;
import common.classes.datastructure.DBM;
import common.classes.expressions.EvaluatorDenotational;
import common.classes.expressions.Valuation;
import common.classes.expressions.expressionclass.Expression;
import common.classes.expressions.expressionclass.IntConstant;
import common.classes.moduleinterface.AssertionBase;
import common.classes.moduleinterface.SpecificationBase;
import common.classes.ultility.Ultility;
import module.tcj.assertions.TCJDataStore;

import java.util.*;

public final class IndexInterleaveAbstract extends TCJProcess {

    public List<TCJProcess> Processes;


    public Map<String, Integer> ProcessesCounter;


    public Map<String, Integer> ProcessesActualSize;


    public Expression RangeExpression;


    private boolean MustAbstract;

    public IndexInterleaveAbstract(TCJProcess processBase, Expression range) {
        this.Processes = new ArrayList<>();
        this.Processes.add(processBase);
        this.RangeExpression = range;
    }

    public IndexInterleaveAbstract(TCJProcess processBase, int size) {
        this.Processes = new ArrayList<>();
        this.Processes.add(processBase);
        this.ProcessesCounter = new HashMap<>();
        this.ProcessesCounter.put(processBase.ProcessID, size);
        this.ProcessesActualSize = new HashMap<>();
        this.ProcessesActualSize.put(processBase.ProcessID, size);
        this.MustAbstract = (size == -1);
    }

    public IndexInterleaveAbstract(List<TCJProcess> processes, Map<String, Integer> counters) {
        this.Processes = new ArrayList<>();
        this.ProcessesCounter = new HashMap<>();
        List<String> list = new ArrayList<>(processes.size());
        for (int i = 0; i < processes.size(); i++) {
            String processID = processes.get(i).ProcessID;
            if (counters.get(processID) != 0) {
                list.add(processID);
                this.Processes.add(processes.get(i));
                this.ProcessesCounter.put(processID, counters.get(processID));
            }
        }
        Collections.sort(list);
        this.ProcessID = "|" + list.get(0) + "*" + this.ProcessesCounter.get(list.get(0));
        for (int j = 1; j < this.Processes.size(); j++) {
            Object processID2 = this.ProcessID;
            this.ProcessID = processID2 + "$" + list.get(j) + "*" + this.ProcessesCounter.get(list.get(j));
        }
        this.ProcessID = TCJDataStore.DataManager.InitializeProcessID(this.ProcessID);
    }

    @Override
    public List<ZoneConfiguration> MoveOneStep(ZoneConfiguration eStep) throws CloneNotSupportedException, ParsingException {
        List<ZoneConfiguration> list = new ArrayList<>();
        List<Map<String, Integer>> list2 = null;
        List<List<ZoneConfiguration>> list3 = new ArrayList<>();
        for (int i = 0; i < this.Processes.size(); i++) {
            list3.add(new ArrayList<>());
            TCJProcess TCJProcess = this.Processes.get(i);
            List<ZoneConfiguration> list4 = TCJProcess.MoveOneStep(eStep);
            if (list4.size() > 0) {
                list2 = Ultility.ProcessCounterDecrement(this.MustAbstract ? Ultility.CutNumber : -1, this.ProcessesCounter, TCJProcess.ProcessID, 1);
            }
            int j = 0;
            while (j < list4.size()) {
                ZoneConfiguration zoneConfiguration = list4.get(j);
                if (zoneConfiguration.event != "terminate") {
                    for (Map<String, Integer> dictionary : list2) {
                        Map<String, Integer> dictionary2 = new HashMap<>(dictionary);
                        List<TCJProcess> processes = new ArrayList<>(this.Processes);
                        this.AddOneProcess(processes, zoneConfiguration.Process, dictionary2);
                        IndexInterleaveAbstract p = new IndexInterleaveAbstract(processes, dictionary2);
                        ZoneConfiguration zoneConfiguration2 = new ZoneConfiguration(p, zoneConfiguration.event, zoneConfiguration.displayName, zoneConfiguration.globalEnv, zoneConfiguration.isDataOperation, zoneConfiguration.DBM);
                        zoneConfiguration2.isAtomic = zoneConfiguration.isAtomic;
                        if (AssertionBase.calculateParticipatingProcess) {
                            zoneConfiguration2.participatingProcesses = new String[]{TCJProcess.ProcessID};
                        }
                        list.add(zoneConfiguration2);
                    }
                    j++;
                    continue;

                    /*using (List<Dictionary<string, int>>.Enumerator enumerator = list2.GetEnumerator())
                    {
                        while (enumerator.MoveNext())
                        {
                            Dictionary<string, int> dictionary = enumerator.Current;
                            Dictionary<string, int> dictionary2 = new Dictionary<string, int>(dictionary);
                            List<TCJProcess> processes = new List<TCJProcess>(this.Processes);
                            this.AddOneProcess(processes, zoneConfiguration.Process, dictionary2);
                            IndexInterleaveAbstract p = new IndexInterleaveAbstract(processes, dictionary2);
                            ZoneConfiguration zoneConfiguration2 = new ZoneConfiguration(p, zoneConfiguration.Event, zoneConfiguration.DisplayName, zoneConfiguration.GlobalEnv, zoneConfiguration.IsDataOperation, zoneConfiguration.DBM);
                            zoneConfiguration2.IsAtomic = zoneConfiguration.IsAtomic;
                            if (AssertionBase.CalculateParticipatingProcess)
                            {
                                zoneConfiguration2.ParticipatingProcesses = new string[]
                                        {
                                                TCJProcess.ProcessID
                                        };
                            }
                            list.Add(zoneConfiguration2);
                        }
                            goto IL_166;
                    }
                        goto IL_158;*/
                } else {
                    list3.get(i).add(zoneConfiguration);
                    j++;
                    continue;
                }
                //goto IL_158;
                //IL_166:
                //j++;
                //continue;
                //IL_158:
                //list3[i].Add(zoneConfiguration);
                //goto IL_166;
            }
            if (SpecificationBase.hasSyncrhonousChannel) {
                this.SynchronousChannelInputOutput(list, i, eStep, null);
            }
        }
        boolean flag = true;
        for (List<ZoneConfiguration> list5 : list3) {
            if (list5.size() == 0) {
                flag = false;
                break;
            }
        }
        if (flag) {
            list.addAll(this.TerminationSteps(eStep, list3));
        }
        return list;
    }

    private List<ZoneConfiguration> TerminationSteps(ZoneConfiguration eStep, List<List<ZoneConfiguration>> terminiateSteps) {
        List<ZoneConfiguration> list = new ArrayList<>();
        boolean flag = false;
        for (int i = 0; i < this.Processes.size(); i++) {
            List<ZoneConfiguration> list2 = terminiateSteps.get(i);
            if (list2.size() == 0) {
                return list;
            }
            if (!flag) {
                for (ZoneConfiguration zoneConfiguration : list2) {
                    if (zoneConfiguration.isAtomic) {
                        flag = true;
                        break;
                    }
                }
            }
        }
        ZoneConfiguration zoneConfiguration2 = new ZoneConfiguration(new Stop(), "terminate", null, eStep.globalEnv, false, eStep.DBM);
        zoneConfiguration2.isAtomic = flag;
        if (AssertionBase.calculateParticipatingProcess) {
            zoneConfiguration2.participatingProcesses = new String[this.Processes.size()];
            for (int j = 0; j < this.Processes.size(); j++) {
                zoneConfiguration2.participatingProcesses[j] = this.Processes.get(j).ProcessID;
            }
        }
        list.add(zoneConfiguration2);
        return list;
    }

    @Override
    public List<ZoneConfigurationWithChannelData> SyncOutput(ZoneConfiguration eStep) throws ParsingException {
        List<ZoneConfigurationWithChannelData> list = new ArrayList<>();
        for (int i = 0; i < this.Processes.size(); i++) {
            TCJProcess TCJProcess = this.Processes.get(i);
            List<ZoneConfigurationWithChannelData> list2 = TCJProcess.SyncOutput(eStep);
            if (list2.size() > 0) {
                List<Map<String, Integer>> list3 = Ultility.ProcessCounterDecrement(this.MustAbstract ? Ultility.CutNumber : -1, this.ProcessesCounter, TCJProcess.ProcessID, 1);
                for (int j = 0; j < list2.size(); j++) {
                    ZoneConfiguration zoneConfiguration = list2.get(j);
                    for (Map<String, Integer> dictionary : list3) {
                        List<TCJProcess> processes = new ArrayList<>(this.Processes);
                        Map<String, Integer> dictionary2 = new HashMap<>(dictionary);
                        this.AddOneProcess(processes, zoneConfiguration.Process, dictionary2);
                        IndexInterleaveAbstract p = new IndexInterleaveAbstract(processes, dictionary2);
                        ZoneConfigurationWithChannelData temp = new ZoneConfigurationWithChannelData(p, zoneConfiguration.event,
                                zoneConfiguration.displayName, zoneConfiguration.globalEnv, zoneConfiguration.isDataOperation, list2.get(j).ChannelName, list2.get(j).Expressions, list2.get(j).DBM);
                        temp.isAtomic = zoneConfiguration.isAtomic;
                        list.add(temp);
                    }
                }
            }
        }
        return list;
    }

    @Override
    public List<ZoneConfiguration> SyncInput(ZoneConfigurationWithChannelData eStep) throws ParsingException {
        List<ZoneConfiguration> list = new ArrayList<>();
        List<Map<String, Integer>> list2 = null;
        for (int i = 0; i < this.Processes.size(); i++) {
            TCJProcess TCJProcess = this.Processes.get(i);
            List<ZoneConfiguration> list3 = TCJProcess.SyncInput(eStep);
            if (list3.size() > 0) {
                list2 = Ultility.ProcessCounterDecrement(this.MustAbstract ? Ultility.CutNumber : -1, this.ProcessesCounter, TCJProcess.ProcessID, 1);
            }
            for (int j = 0; j < list3.size(); j++) {
                ZoneConfiguration zoneConfiguration = list3.get(j);
                for (Map<String, Integer> dictionary : list2) {
                    Map<String, Integer> dictionary2 = new HashMap<>(dictionary);
                    List<TCJProcess> processes = new ArrayList<>(this.Processes);
                    this.AddOneProcess(processes, zoneConfiguration.Process, dictionary2);
                    IndexInterleaveAbstract p = new IndexInterleaveAbstract(processes, dictionary2);
                    ZoneConfiguration zoneConfiguration2 = new ZoneConfiguration(p, zoneConfiguration.event, zoneConfiguration.displayName, zoneConfiguration.globalEnv, zoneConfiguration.isDataOperation, zoneConfiguration.DBM);
                    zoneConfiguration2.isAtomic = zoneConfiguration.isAtomic;
                    if (AssertionBase.calculateParticipatingProcess) {
                        zoneConfiguration2.participatingProcesses = new String[]{TCJProcess.ProcessID};
                    }
                    list.add(zoneConfiguration2);
                }
            }
        }
        return list;
    }

    private void SynchronousChannelInputOutput(List<ZoneConfiguration> returnList, int i, ZoneConfiguration step, String evt) throws ParsingException {
        List<ZoneConfigurationWithChannelData> list = this.Processes.get(i).SyncOutput(step);
        List<Map<String, Integer>> list2 = Ultility.ProcessCounterDecrement(this.MustAbstract ? Ultility.CutNumber : -1, this.ProcessesCounter, this.Processes.get(i).ProcessID, 1);
        for (ZoneConfigurationWithChannelData zoneConfigurationWithChannelData : list) {
            if (!(evt != null & zoneConfigurationWithChannelData.event != evt)) {
                for (int j = 0; j < this.Processes.size(); j++) {
                    String processID = this.Processes.get(j).ProcessID;
                    if (j != i || (j == i && (this.ProcessesCounter.get(processID) > 1 || this.ProcessesCounter.get(processID) == -1))) {
                        List<ZoneConfiguration> list3 = this.Processes.get(j).SyncInput(zoneConfigurationWithChannelData);
                        if (list3.size() > 0) {
                            if (j == i) {
                                List<Map<String, Integer>> list4 = Ultility.ProcessCounterDecrement(this.MustAbstract ? Ultility.CutNumber : -1, this.ProcessesCounter, this.Processes.get(i).ProcessID, 2);
                                for (ZoneConfiguration zoneConfiguration : list3) {
                                    for (Map<String, Integer> dictionary : list4) {
                                        Map<String, Integer> dictionary2 = new HashMap<>(dictionary);
                                        List<TCJProcess> list5 = new ArrayList<>(this.Processes);
                                        this.AddOneProcess(list5, zoneConfiguration.Process, dictionary2);
                                        this.AddOneProcess(list5, zoneConfigurationWithChannelData.Process, dictionary2);
                                        ZoneConfiguration zoneConfiguration2 = new ZoneConfiguration(new IndexInterleaveAbstract(list5, dictionary2), zoneConfigurationWithChannelData.event, zoneConfigurationWithChannelData.displayName, zoneConfigurationWithChannelData.globalEnv, false, DBM.returnUrgentDBM(zoneConfigurationWithChannelData.DBM, zoneConfiguration.DBM));
                                        zoneConfiguration2.isAtomic = (zoneConfigurationWithChannelData.isAtomic || zoneConfiguration.isAtomic);
                                        if (AssertionBase.calculateParticipatingProcess) {
                                            zoneConfiguration2.participatingProcesses = new String[]{list5.get(i).ProcessID, list5.get(j).ProcessID};
                                        }
                                        returnList.add(zoneConfiguration2);
                                    }
                                }

                                /*using (List<ZoneConfiguration>.Enumerator enumerator2 = list3.GetEnumerator())
                                {
                                    while (enumerator2.MoveNext())
                                    {
                                        ZoneConfiguration zoneConfiguration = enumerator2.Current;
                                        foreach (Dictionary<string, int> dictionary in list4)
                                        {
                                            Dictionary<string, int> dictionary2 = new Dictionary<string, int>(dictionary);
                                            List<TCJProcess> list5 = new List<TCJProcess>(this.Processes);
                                            this.AddOneProcess(list5, zoneConfiguration.Process, dictionary2);
                                            this.AddOneProcess(list5, zoneConfigurationWithChannelData.Process, dictionary2);
                                            ZoneConfiguration zoneConfiguration2 = new ZoneConfiguration(new IndexInterleaveAbstract(list5, dictionary2), zoneConfigurationWithChannelData.Event, zoneConfigurationWithChannelData.DisplayName, zoneConfigurationWithChannelData.GlobalEnv, false, DBM.ReturnUrgentDBM(zoneConfigurationWithChannelData.DBM, zoneConfiguration.DBM));
                                            zoneConfiguration2.IsAtomic = (zoneConfigurationWithChannelData.IsAtomic || zoneConfiguration.IsAtomic);
                                            if (AssertionBase.CalculateParticipatingProcess)
                                            {
                                                zoneConfiguration2.ParticipatingProcesses = new string[]
                                                        {
                                                                list5[i].ProcessID,
                                                                list5[j].ProcessID
                                                        };
                                            }
                                            returnList.add(zoneConfiguration2);
                                        }
                                    }
                                        goto IL_3BA;
                                }*/
                            } else {
                                for (Map<String, Integer> counters : list2) {
                                    List<Map<String, Integer>> list6 = Ultility.ProcessCounterDecrement(this.MustAbstract ? Ultility.CutNumber : -1, counters, processID, 1);
                                    for (ZoneConfiguration zoneConfiguration3 : list3) {
                                        for (Map<String, Integer> dictionary3 : list6) {
                                            List<TCJProcess> processes = new ArrayList<>(this.Processes);
                                            this.AddOneProcess(processes, zoneConfiguration3.Process, dictionary3);
                                            this.AddOneProcess(processes, zoneConfigurationWithChannelData.Process, dictionary3);
                                            ZoneConfiguration zoneConfiguration4 = new ZoneConfiguration(new IndexInterleaveAbstract(processes, dictionary3), zoneConfigurationWithChannelData.event, zoneConfigurationWithChannelData.displayName, zoneConfigurationWithChannelData.globalEnv, false, DBM.returnUrgentDBM(zoneConfigurationWithChannelData.DBM, zoneConfiguration3.DBM));
                                            zoneConfiguration4.isAtomic = (zoneConfigurationWithChannelData.isAtomic || zoneConfiguration3.isAtomic);
                                            if (AssertionBase.calculateParticipatingProcess) {
                                                zoneConfiguration4.participatingProcesses = new String[]{this.Processes.get(i).ProcessID, this.Processes.get(j).ProcessID};
                                            }
                                            returnList.add(zoneConfiguration4);
                                        }
                                    }
                                }
                            }

                        }
                    }
                    //IL_3BA: ;
                }
            }
        }
    }

    @Override
    public void GetDiscretEventTransitions(TickConfiguration eStep, List<TickConfiguration> toReturn) throws CloneNotSupportedException, ParsingException {
        List<Map<String, Integer>> list = null;
        boolean flag = true;
        boolean flag2 = false;
        for (int i = 0; i < this.Processes.size(); i++) {
            TCJProcess TCJProcess = this.Processes.get(i);
            List<TickConfiguration> list2 = new ArrayList<>();
            TCJProcess.GetDiscretEventTransitions(eStep, list2);
            boolean flag3 = false;
            if (list2.size() > 0) {
                list = Ultility.ProcessCounterDecrement(this.MustAbstract ? Ultility.CutNumber : -1, this.ProcessesCounter, TCJProcess.ProcessID, 1);
            }
            int j = 0;
            while (j < list2.size()) {
                TickConfiguration tickConfiguration = list2.get(j);
                if (tickConfiguration.event != "terminate") {
                    for (Map<String, Integer> dictionary : list) {
                        Map<String, Integer> dictionary2 = new HashMap<>(dictionary);
                        List<TCJProcess> processes = new ArrayList<>(this.Processes);
                        this.AddOneProcess(processes, tickConfiguration.Process, dictionary2);
                        IndexInterleaveAbstract p = new IndexInterleaveAbstract(processes, dictionary2);
                        TickConfiguration tickConfiguration2 = new TickConfiguration(p, tickConfiguration.event, tickConfiguration.displayName, tickConfiguration.globalEnv, tickConfiguration.isDataOperation, tickConfiguration.IsUrgent);
                        tickConfiguration2.isAtomic = tickConfiguration.isAtomic;
                        if (AssertionBase.calculateParticipatingProcess) {
                            tickConfiguration2.participatingProcesses = new String[]{TCJProcess.ProcessID};
                        }
                        toReturn.add(tickConfiguration2);
                    }
                    j++;
                    continue;

                    /*using (List<Dictionary<string, int>>.Enumerator enumerator = list.GetEnumerator())
                    {
                        while (enumerator.MoveNext())
                        {
                            Dictionary<string, int> dictionary = enumerator.Current;
                            Dictionary<string, int> dictionary2 = new Dictionary<string, int>(dictionary);
                            List<TCJProcess> processes = new List<TCJProcess>(this.Processes);
                            this.AddOneProcess(processes, tickConfiguration.Process, dictionary2);
                            IndexInterleaveAbstract p = new IndexInterleaveAbstract(processes, dictionary2);
                            TickConfiguration tickConfiguration2 = new TickConfiguration(p, tickConfiguration.Event, tickConfiguration.DisplayName, tickConfiguration.GlobalEnv, tickConfiguration.IsDataOperation, tickConfiguration.IsUrgent);
                            tickConfiguration2.IsAtomic = tickConfiguration.IsAtomic;
                            if (AssertionBase.CalculateParticipatingProcess)
                            {
                                tickConfiguration2.ParticipatingProcesses = new string[]
                                        {
                                                TCJProcess.ProcessID
                                        };
                            }
                            toReturn.Add(tickConfiguration2);
                        }
                            goto IL_15D;
                    }*/
                    //goto IL_14F;
                }
                //goto IL_14F;
                //IL_15D:
                //j++;
                //continue;
                //IL_14F:
                if (tickConfiguration.isAtomic) {
                    flag2 = true;
                }
                flag3 = true;
                //goto IL_15D;
                j++;
                continue;
            }
            if (SpecificationBase.hasSyncrhonousChannel) {
                this.SynchronousChannelInputOutput(toReturn, i, eStep, null);
            }
            if (!flag3) {
                flag = false;
            }
        }
        if (flag) {
            TickConfiguration tickConfiguration3 = new TickConfiguration(new Stop(), "terminate", null, eStep.globalEnv, false, false);
            if (flag2) {
                tickConfiguration3.isAtomic = true;
            }
            if (AssertionBase.calculateParticipatingProcess) {
                tickConfiguration3.participatingProcesses = new String[this.Processes.size()];
                for (int k = 0; k < this.Processes.size(); k++) {
                    tickConfiguration3.participatingProcesses[k] = String.valueOf(k);
                }
            }
            toReturn.add(tickConfiguration3);
        }
    }

    @Override
    public TCJProcess GetProcessAfterDelay(TickConfiguration eStep) throws ParsingException {
        List<TCJProcess> list = new ArrayList<>();
        Map<String, Integer> dictionary = new HashMap<>();
        for (int i = 0; i < this.Processes.size(); i++) {
            TCJProcess processAfterDelay = this.Processes.get(i).GetProcessAfterDelay(eStep);
            if (processAfterDelay == null) {
                return null;
            }
            list.add(processAfterDelay);
            dictionary.put(list.get(i).ProcessID, this.ProcessesCounter.get(this.Processes.get(i).ProcessID));
        }
        return new IndexInterleaveAbstract(list, dictionary);
    }

    private void SynchronousChannelInputOutput(List<TickConfiguration> returnList, int i, TickConfiguration step, String evt) throws ParsingException {
        List<TickConfigurationWithChannelData> list = new ArrayList<>();
        this.Processes.get(i).SyncOutput(step, list);
        List<Map<String, Integer>> list2 = Ultility.ProcessCounterDecrement(this.MustAbstract ? Ultility.CutNumber : -1, this.ProcessesCounter, this.Processes.get(i).ProcessID, 1);
        for (TickConfigurationWithChannelData tickConfigurationWithChannelData : list) {
            if (!(evt != null & tickConfigurationWithChannelData.event != evt)) {
                for (int j = 0; j < this.Processes.size(); j++) {
                    String processID = this.Processes.get(j).ProcessID;
                    if (j != i || (j == i && (this.ProcessesCounter.get(processID) > 1 || this.ProcessesCounter.get(processID) == -1))) {
                        List<TickConfiguration> list3 = new ArrayList<>();
                        this.Processes.get(j).SyncInput(tickConfigurationWithChannelData, list3);
                        if (list3.size() > 0) {
                            if (j == i) {
                                List<Map<String, Integer>> list4 = Ultility.ProcessCounterDecrement(this.MustAbstract ? Ultility.CutNumber : -1, this.ProcessesCounter, this.Processes.get(i).ProcessID, 2);
                                for (TickConfiguration tickConfiguration : list3) {
                                    for (Map<String, Integer> dictionary : list4) {
                                        Map<String, Integer> dictionary2 = new HashMap<>(dictionary);
                                        List<TCJProcess> list5 = new ArrayList<>(this.Processes);
                                        this.AddOneProcess(list5, tickConfiguration.Process, dictionary2);
                                        this.AddOneProcess(list5, tickConfigurationWithChannelData.Process, dictionary2);
                                        TickConfiguration tickConfiguration2 = new TickConfiguration(new IndexInterleaveAbstract(list5, dictionary2), tickConfigurationWithChannelData.event, tickConfigurationWithChannelData.displayName, tickConfigurationWithChannelData.globalEnv, false, tickConfigurationWithChannelData.IsUrgent || tickConfiguration.IsUrgent);
                                        tickConfiguration2.isAtomic = (tickConfigurationWithChannelData.isAtomic || tickConfiguration.isAtomic);
                                        if (AssertionBase.calculateParticipatingProcess) {
                                            tickConfiguration2.participatingProcesses = new String[]{list5.get(i).ProcessID, list5.get(j).ProcessID};
                                        }
                                        returnList.add(tickConfiguration2);
                                    }
                                }

                                /*using (List<TickConfiguration>.Enumerator enumerator2 = list3.GetEnumerator())
                                {
                                    while (enumerator2.MoveNext())
                                    {
                                        TickConfiguration tickConfiguration = enumerator2.Current;
                                        foreach (Dictionary<string, int> dictionary in list4)
                                        {
                                            Dictionary<string, int> dictionary2 = new Dictionary<string, int>(dictionary);
                                            List<TCJProcess> list5 = new List<TCJProcess>(this.Processes);
                                            this.AddOneProcess(list5, tickConfiguration.Process, dictionary2);
                                            this.AddOneProcess(list5, tickConfigurationWithChannelData.Process, dictionary2);
                                            TickConfiguration tickConfiguration2 = new TickConfiguration(new IndexInterleaveAbstract(list5, dictionary2), tickConfigurationWithChannelData.Event, tickConfigurationWithChannelData.DisplayName, tickConfigurationWithChannelData.GlobalEnv, false, tickConfigurationWithChannelData.IsUrgent || tickConfiguration.IsUrgent);
                                            tickConfiguration2.IsAtomic = (tickConfigurationWithChannelData.IsAtomic || tickConfiguration.IsAtomic);
                                            if (AssertionBase.CalculateParticipatingProcess)
                                            {
                                                tickConfiguration2.ParticipatingProcesses = new string[]
                                                        {
                                                                list5[i].ProcessID,
                                                                list5[j].ProcessID
                                                        };
                                            }
                                            returnList.Add(tickConfiguration2);
                                        }
                                    }
                                        goto IL_3C7;
                                }*/
                            } else {
                                for (Map<String, Integer> counters : list2) {
                                    List<Map<String, Integer>> list6 = Ultility.ProcessCounterDecrement(this.MustAbstract ? Ultility.CutNumber : -1, counters, processID, 1);
                                    for (TickConfiguration tickConfiguration3 : list3) {
                                        for (Map<String, Integer> dictionary3 : list6) {
                                            List<TCJProcess> processes = new ArrayList<>(this.Processes);
                                            this.AddOneProcess(processes, tickConfiguration3.Process, dictionary3);
                                            this.AddOneProcess(processes, tickConfigurationWithChannelData.Process, dictionary3);
                                            TickConfiguration tickConfiguration4 = new TickConfiguration(new IndexInterleaveAbstract(processes, dictionary3), tickConfigurationWithChannelData.event, tickConfigurationWithChannelData.displayName, tickConfigurationWithChannelData.globalEnv, false, tickConfigurationWithChannelData.IsUrgent || tickConfiguration3.IsUrgent);
                                            tickConfiguration4.isAtomic = (tickConfigurationWithChannelData.isAtomic || tickConfiguration3.isAtomic);
                                            if (AssertionBase.calculateParticipatingProcess) {
                                                tickConfiguration4.participatingProcesses = new String[]{this.Processes.get(i).ProcessID, this.Processes.get(j).ProcessID};
                                            }
                                            returnList.add(tickConfiguration4);
                                        }
                                    }
                                }
                            }

                        }
                    }
                    //IL_3C7: ;
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
            if (list.size() > 0) {
                List<Map<String, Integer>> list2 = Ultility.ProcessCounterDecrement(this.MustAbstract ? Ultility.CutNumber : -1, this.ProcessesCounter, TCJProcess.ProcessID, 1);
                for (int j = 0; j < list.size(); j++) {
                    TickConfiguration tickConfiguration = list.get(j);
                    for (Map<String, Integer> dictionary : list2) {
                        List<TCJProcess> processes = new ArrayList<>(this.Processes);
                        Map<String, Integer> dictionary2 = new HashMap<>(dictionary);
                        this.AddOneProcess(processes, tickConfiguration.Process, dictionary2);
                        IndexInterleaveAbstract p = new IndexInterleaveAbstract(processes, dictionary2);
                        TickConfigurationWithChannelData temp = new TickConfigurationWithChannelData(p, tickConfiguration.event, tickConfiguration.displayName, tickConfiguration.globalEnv, tickConfiguration.isDataOperation, tickConfiguration.IsUrgent, list.get(j).ChannelName, list.get(j).Expressions);
                        temp.isAtomic = tickConfiguration.isAtomic;
                        toReturn.add(temp);
                    }
                }
            }
        }
    }

    @Override
    public void SyncInput(TickConfigurationWithChannelData eStep, List<TickConfiguration> toReturn) throws ParsingException {
        List<Map<String, Integer>> list = null;
        for (int i = 0; i < this.Processes.size(); i++) {
            TCJProcess TCJProcess = this.Processes.get(i);
            List<TickConfiguration> list2 = new ArrayList<>();
            TCJProcess.SyncInput(eStep, list2);
            if (list2.size() > 0) {
                list = Ultility.ProcessCounterDecrement(this.MustAbstract ? Ultility.CutNumber : -1, this.ProcessesCounter, TCJProcess.ProcessID, 1);
            }
            for (int j = 0; j < list2.size(); j++) {
                TickConfiguration tickConfiguration = list2.get(j);
                for (Map<String, Integer> dictionary : list) {
                    Map<String, Integer> dictionary2 = new HashMap<>(dictionary);
                    List<TCJProcess> processes = new ArrayList<>(this.Processes);
                    this.AddOneProcess(processes, tickConfiguration.Process, dictionary2);
                    IndexInterleaveAbstract p = new IndexInterleaveAbstract(processes, dictionary2);
                    TickConfiguration tickConfiguration2 = new TickConfiguration(p, tickConfiguration.event, tickConfiguration.displayName, tickConfiguration.globalEnv, tickConfiguration.isDataOperation, tickConfiguration.IsUrgent);
                    tickConfiguration2.isAtomic = tickConfiguration.isAtomic;
                    if (AssertionBase.calculateParticipatingProcess) {
                        tickConfiguration2.participatingProcesses = new String[]{TCJProcess.ProcessID};
                    }
                    toReturn.add(tickConfiguration2);
                }
            }
        }
    }

    private void AddOneProcess(List<TCJProcess> processes, TCJProcess newProcess, Map<String, Integer> counter) {
        if (newProcess instanceof IndexInterleave) {
            IndexInterleave indexInterleave = (IndexInterleave) newProcess;
            for (TCJProcess tcjProcess : indexInterleave.Processes) {
                String processID = tcjProcess.ProcessID;
                if (!counter.containsKey(processID)) {
                    counter.put(processID, 1);
                    processes.add(tcjProcess);
                } else {
                    counter.put(processID, Ultility.ProcessCounterIncrement(this.MustAbstract ? Ultility.CutNumber : -1, counter.get(processID), 1));
                }
            }
            return;
            /*using (List<TCJProcess>.Enumerator enumerator = indexInterleave.Processes.GetEnumerator())
            {
                while (enumerator.MoveNext())
                {
                    TCJProcess TCJProcess = enumerator.Current;
                    string processID = TCJProcess.ProcessID;
                    if (!counter.ContainsKey(processID))
                    {
                        counter.Add(processID, 1);
                        processes.Add(TCJProcess);
                    }
                    else
                    {
                        counter[processID] = Common.Classes.Ultility.Ultility.ProcessCounterIncrement(this.MustAbstract ? Common.Classes.Ultility.Ultility.CutNumber : -1, counter[processID], 1);
                    }
                }
                return;
            }*/
        }
        if (newProcess instanceof IndexInterleaveAbstract) {
            IndexInterleaveAbstract indexInterleaveAbstract = (IndexInterleaveAbstract) newProcess;
            for (TCJProcess tcjProcess2 : indexInterleaveAbstract.Processes) {
                String processID2 = tcjProcess2.ProcessID;
                if (!counter.containsKey(processID2)) {
                    counter.put(processID2, indexInterleaveAbstract.ProcessesCounter.get(processID2));
                    processes.add(tcjProcess2);
                } else {
                    counter.put(processID2, Ultility.ProcessCounterIncrement(this.MustAbstract ? Ultility.CutNumber : -1, counter.get(processID2), indexInterleaveAbstract.ProcessesCounter.get(processID2)));
                }
            }
            return;

            /*using (List<TCJProcess>.Enumerator enumerator2 = indexInterleaveAbstract.Processes.GetEnumerator())
            {
                while (enumerator2.MoveNext())
                {
                    TCJProcess TCJProcess2 = enumerator2.Current;
                    string processID2 = TCJProcess2.ProcessID;
                    if (!counter.ContainsKey(processID2))
                    {
                        counter.Add(processID2, indexInterleaveAbstract.ProcessesCounter[processID2]);
                        processes.Add(TCJProcess2);
                    }
                    else
                    {
                        counter[processID2] = Common.Classes.Ultility.Ultility.ProcessCounterIncrement(this.MustAbstract ? Common.Classes.Ultility.Ultility.CutNumber : -1, counter[processID2], indexInterleaveAbstract.ProcessesCounter[processID2]);
                    }
                }
                return;
            }*/
        }
        String processID3 = newProcess.ProcessID;
        if (!counter.containsKey(processID3)) {
            counter.put(processID3, 1);
            processes.add(newProcess);
            return;
        }
        counter.put(processID3, Ultility.ProcessCounterIncrement(this.MustAbstract ? Ultility.CutNumber : -1, counter.get(processID3), 1));
    }

    @Override
    public HashSet<String> GetAlphabets(Map<String, String> visitedDefinitionRefs) throws ParsingException {
        HashSet<String> hashSet = new HashSet<>();
        for (int i = 0; i < this.Processes.size(); i++) {
            TCJProcess TCJProcess = this.Processes.get(i);
            hashSet.addAll(TCJProcess.GetAlphabets(visitedDefinitionRefs));
        }
        return hashSet;
    }

    @Override
    public List<String> GetGlobalVariables() {
        if (this.ProcessesCounter == null) {
            return this.Processes.get(0).GetGlobalVariables();
        }
        List<String> list = new ArrayList<>();
        for (TCJProcess TCJProcess : this.Processes) {
            Ultility.addList(list, TCJProcess.GetGlobalVariables());
        }
        return list;
    }

    @Override
    public List<String> GetChannels() {
        if (this.ProcessesCounter == null) {
            return this.Processes.get(0).GetChannels();
        }
        List<String> list = new ArrayList<>();
        for (TCJProcess TCJProcess : this.Processes) {
            Ultility.addList(list, TCJProcess.GetChannels());
        }
        return list;
    }

    @Override
    public String toString() {
        if (this.ProcessesCounter == null) {
            return "(||| {" + this.RangeExpression.toString()
                    + "} @" + this.Processes.get(0) + ")";
        }
        String text = "";
        for (TCJProcess TCJProcess : this.Processes) {
            if (this.ProcessesCounter.get(TCJProcess.ProcessID) == -1) {
                Object obj = text;
                text = obj + "(||| {..} @" + TCJProcess + ")|||";
            } else {
                Object obj2 = text;
                text = obj2 + "(||| {" + this.ProcessesCounter.get(TCJProcess.ProcessID) + "} @" + TCJProcess + ")|||";
            }
        }
        return text.substring(0, text.length() - 1);
    }

    @Override
    public TCJProcess ClearConstant(Map<String, Expression> constMapping) throws ParsingException {
        int num = this.Processes.size();
        List<TCJProcess> list = new ArrayList<>(num);
        Map<String, Integer> dictionary = new HashMap<>(num);
        if (!SpecificationBase.isParsing) {
            Map<String, Integer> dictionary2 = this.ProcessesCounter;
            if (this.ProcessesCounter == null) {
                Expression exp = this.RangeExpression.clearConstant(constMapping);
                num = ((IntConstant) EvaluatorDenotational.evaluate(exp, null))._value;
                if (num == 0) {
                    return new Skip();
                }
                if (num < 0) {
                    throw new ParsingException("Negative range "
                            + num
                            + " for parameterized interleave: "
                            + this.RangeExpression
                            , 0, 0, this.RangeExpression.toString());
                }
                dictionary2 = new HashMap<>();
                dictionary2.put(this.Processes.get(0).ProcessID, num);
            }
            for (int i = 0; i < this.Processes.size(); i++) {
                int num2 = dictionary2.get(this.Processes.get(i).ProcessID);
                TCJProcess TCJProcess = this.Processes.get(i).ClearConstant(constMapping);
                if (TCJProcess instanceof IndexInterleave) {
                    List<TCJProcess> processes = ((IndexInterleave) TCJProcess).Processes;
                    for (int j = 0; j < processes.size(); j++) {
                        String processID = processes.get(j).ProcessID;
                        if (!dictionary.containsKey(processID)) {
                            dictionary.put(processID, num2);
                            list.add(processes.get(j));
                        } else if (dictionary.get(processID) != -1) {
                            dictionary.put(processID, dictionary.get(processID) + num2);
                        }
                    }
                } else if (TCJProcess instanceof IndexInterleaveAbstract) {
                    IndexInterleaveAbstract indexInterleaveAbstract = (IndexInterleaveAbstract) TCJProcess;
                    for (int k = 0; k < indexInterleaveAbstract.Processes.size(); k++) {
                        String processID2 = indexInterleaveAbstract.Processes.get(k).ProcessID;
                        int num3 = indexInterleaveAbstract.ProcessesCounter.get(processID2);
                        if (!dictionary.containsKey(processID2)) {
                            list.add(indexInterleaveAbstract.Processes.get(k));
                            if (num2 != -1 && num3 != -1) {
                                dictionary.put(processID2, num3 * num2);
                            } else {
                                dictionary.put(processID2, -1);
                            }
                        } else if (dictionary.get(processID2) != -1) {
                            if (num3 == -1 || num2 == -1) {
                                dictionary.put(processID2, -1);
                            } else {
                                dictionary.put(processID2, dictionary.get(processID2) + (num2 * num3));
                            }
                        }
                    }
                } else {
                    String processID3 = TCJProcess.ProcessID;
                    if (!dictionary.containsKey(processID3)) {
                        list.add(TCJProcess);
                        dictionary.put(processID3, num2);
                    } else if (dictionary.get(processID3) != -1) {
                        dictionary.put(processID3, dictionary.get(processID3) + num2);
                    }
                }
            }
            IndexInterleaveAbstract interleaveAbstract = new IndexInterleaveAbstract(list, dictionary);
            interleaveAbstract.ProcessesActualSize = new HashMap<>(dictionary);
            return interleaveAbstract;
        }
        if (this.ProcessesCounter == null) {
            return new IndexInterleaveAbstract(this.Processes.get(0).ClearConstant(constMapping), this.RangeExpression.clearConstant(constMapping));
        }
        for (int l = 0; l < this.Processes.size(); l++) {
            int num4 = this.ProcessesCounter.get(this.Processes.get(l).ProcessID);
            TCJProcess TCJProcess2 = this.Processes.get(l).ClearConstant(constMapping);
            String processID4 = TCJProcess2.ProcessID;
            if (!dictionary.containsKey(processID4)) {
                list.add(TCJProcess2);
                dictionary.put(processID4, num4);
            } else if (dictionary.get(processID4) != -1) {
                dictionary.put(processID4, dictionary.get(processID4) + num4);
            }
        }
        IndexInterleaveAbstract interleaveAbstract = new IndexInterleaveAbstract(list, dictionary);
        interleaveAbstract.ProcessesActualSize = new HashMap<>(dictionary);
        return interleaveAbstract;
    }

    @Override
    public boolean MustBeAbstracted() {
        if (this.ProcessesCounter == null) {
            return this.Processes.get(0).MustBeAbstracted();
        }
        for (int i = 0; i < this.Processes.size(); i++) {
            String processID = this.Processes.get(i).ProcessID;
            if (this.ProcessesActualSize.get(processID) == -1) {
                return true;
            }
            boolean flag = this.Processes.get(i).MustBeAbstracted();
            if (flag) {
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
        Map<String, Integer> dictionary = new HashMap<>();
        List<TCJProcess> list = new ArrayList<>();
        for (int i = 0; i < this.Processes.size(); i++) {
            TCJProcess TCJProcess = this.Processes.get(i).SetTimer(globalEnv, clockBound, mapping);
            int num = this.ProcessesCounter.get(this.Processes.get(i).ProcessID);
            if (dictionary.containsKey(TCJProcess.ProcessID)) {
                dictionary.put(TCJProcess.ProcessID, dictionary.get(TCJProcess.ProcessID) + num);
            } else {
                list.add(TCJProcess);
                dictionary.put(TCJProcess.ProcessID, num);
            }
        }
        return new IndexInterleaveAbstract(list, dictionary);
    }

    @Override
    public boolean IsTimeImmediate() {
        if (this.ProcessesCounter == null) {
            return this.Processes.get(0).IsTimeImmediate();
        }
        for (int i = 0; i < this.Processes.size(); i++) {
            boolean flag = this.Processes.get(i).IsTimeImmediate();
            if (flag) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean IsSkip() {
        return this.Processes.size() == 1 && this.Processes.get(0).IsSkip();
    }
}
