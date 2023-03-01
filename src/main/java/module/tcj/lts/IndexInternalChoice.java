package module.tcj.lts;

import common.ParsingException;
import common.classes.expressions.expressionclass.Expression;
import common.classes.moduleinterface.SpecificationBase;
import common.classes.ultility.Ultility;
import module.tcj.assertions.TCJDataStore;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public final class IndexInternalChoice extends TCJProcess {

    public List<TCJProcess> Processes;

    public IndexedProcess IndexedProcessDefinition;

    public IndexInternalChoice(IndexedProcess definition) {
        this.IndexedProcessDefinition = definition;
    }

    public IndexInternalChoice(List<TCJProcess> processes) {
        this.Processes = new ArrayList<>();
        StringBuilder stringBuilder = new StringBuilder();
        for (TCJProcess TCJProcess : processes) {
            if (TCJProcess instanceof IndexInternalChoice && ((IndexInternalChoice) TCJProcess).Processes != null) {
                List<TCJProcess> processes2 = ((IndexInternalChoice) TCJProcess).Processes;
                this.Processes.addAll(processes2);
                for (TCJProcess tcjProcess : processes2) {
                    stringBuilder.append("%");
                    stringBuilder.append(tcjProcess.ProcessID);
                }

                /*using(List < TCJProcess >.Enumerator enumerator2 = processes2.GetEnumerator())
                {
                    while (enumerator2.MoveNext()) {
                        TCJProcess TCJProcess2 = enumerator2.Current;
                        stringBuilder.Append("%");
                        stringBuilder.Append(TCJProcess2.ProcessID);
                    }
                    continue;
                }*/
            }
            this.Processes.add(TCJProcess);
            stringBuilder.append("%");
            stringBuilder.append(TCJProcess.ProcessID);
        }
        this.ProcessID = TCJDataStore.DataManager.InitializeProcessID(stringBuilder.toString());
    }

    @Override
    public List<ZoneConfiguration> MoveOneStep(ZoneConfiguration eStep) throws CloneNotSupportedException, ParsingException {
        List<ZoneConfiguration> list = new ArrayList<>();
        for (int i = 0; i < this.Processes.size(); i++) {
            list.add(new ZoneConfiguration(this.Processes.get(i), "τ", "[int_choice]", eStep.globalEnv, false, eStep.DBM.addUrgency()));
        }
        return list;
    }

    @Override
    public void GetDiscretEventTransitions(TickConfiguration eStep, List<TickConfiguration> toReturn) throws CloneNotSupportedException, ParsingException {
        for (int i = 0; i < this.Processes.size(); i++) {
            toReturn.add(new TickConfiguration(this.Processes.get(i), "τ", "[int_choice]", eStep.globalEnv, false, true));
        }
    }

    @Override
    public TCJProcess GetProcessAfterDelay(TickConfiguration eStep) throws ParsingException {
        return null;
    }

    @Override
    public String toString() {
        if (this.Processes == null) {
            return "<>" + this.IndexedProcessDefinition;
        }
        String str = "(" + this.Processes.get(0);
        for (int i = 1; i < this.Processes.size(); i++) {
            str += "<>";
            str += this.Processes.get(i).toString();
        }
        return str + ")";
    }

    @Override
    public List<String> GetGlobalVariables() {
        if (this.Processes == null) {
            return this.IndexedProcessDefinition.GetGlobalVariables();
        }
        List<String> list = new ArrayList<>();
        for (int i = 0; i < this.Processes.size(); i++) {
            Ultility.addList(list, this.Processes.get(i).GetGlobalVariables());
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
            Ultility.addList(list, this.Processes.get(i).GetChannels());
        }
        return list;
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
    public TCJProcess ClearConstant(Map<String, Expression> constMapping) throws ParsingException {
        List<TCJProcess> list = this.Processes;
        if (this.Processes == null) {
            if (SpecificationBase.isParsing) {
                return new IndexInternalChoice(this.IndexedProcessDefinition.ClearConstant(constMapping));
            }
            list = this.IndexedProcessDefinition.GetIndexedProcesses(constMapping);
        }
        List<TCJProcess> list2 = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            TCJProcess item = list.get(i).ClearConstant(constMapping);
            list2.add(item);
        }
        return new IndexInternalChoice(list2);
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
}
