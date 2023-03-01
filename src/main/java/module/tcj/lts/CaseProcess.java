package module.tcj.lts;

import common.classes.expressions.EvaluatorDenotational;
import common.classes.expressions.expressionclass.BoolConstant;
import common.classes.expressions.expressionclass.Expression;
import common.classes.ultility.Ultility;
import module.tcj.assertions.TCJDataStore;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public final class CaseProcess extends TCJProcess {
    public TCJProcess[] Processes;


    public Expression[] Conditions;

    public CaseProcess(TCJProcess[] processes, Expression[] conds) {
        this.Processes = processes;
        this.Conditions = conds;
        StringBuilder stringBuilder = new StringBuilder("?");
        for (int i = 0; i < this.Processes.length; i++) {
            stringBuilder.append(this.Conditions[i].expressionID);
            stringBuilder.append(":");
            stringBuilder.append(this.Processes[i].ProcessID);
            stringBuilder.append(";");
        }
        this.ProcessID = TCJDataStore.DataManager.InitializeProcessID(stringBuilder.toString());
    }

    @Override
    public List<String> GetGlobalVariables() {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < this.Conditions.length; i++) {
            Ultility.addList(list, this.Processes[i].GetGlobalVariables());
            ;
            Ultility.addList(list, this.Conditions[i].getVars());
            ;
        }
        return list;
    }

    @Override
    public List<String> GetChannels() {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < this.Conditions.length; i++) {
            List<String> channels = this.Processes[i].GetChannels();
            for (String item : channels) {
                if (!list.contains(item)) {
                    list.add(item);
                }
            }
        }
        return list;
    }

    @Override
    public List<ZoneConfiguration> MoveOneStep(ZoneConfiguration eStep) {
        List<ZoneConfiguration> list = new ArrayList<>();
        for (int i = 0; i < this.Processes.length; i++) {
            if (((BoolConstant) (EvaluatorDenotational.evaluate(this.Conditions[i], eStep.globalEnv)))._value) {
                list.add(new ZoneConfiguration(this.Processes[i], "τ", "[" + this.Conditions[i] + "]", eStep.globalEnv, false, eStep.DBM));
                return list;
            }
        }
        list.add(new ZoneConfiguration(new Skip(), "τ", "[default]", eStep.globalEnv, false, eStep.DBM));
        return list;
    }

    @Override
    public void GetDiscretEventTransitions(TickConfiguration eStep, List<TickConfiguration> toReturn) {
        for (int i = 0; i < this.Processes.length; i++) {
            if (((BoolConstant) (EvaluatorDenotational.evaluate(this.Conditions[i], eStep.globalEnv)))._value) {
                toReturn.add(new TickConfiguration(this.Processes[i], "τ", "[" + this.Conditions[i] + "]", eStep.globalEnv, false, false));
            }
        }
        toReturn.add(new TickConfiguration(new Skip(), "τ", "[default]", eStep.globalEnv, false, false));
    }

    @Override
    public String toString() {
        if (this.Processes.length == 1) {
            return "if" + this.Conditions[0] + "{" + this.Processes[0] + "}";
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("case {" + "\n");
        for (int i = 0; i < this.Processes.length; i++) {
            Expression expression = this.Conditions[i];
            TCJProcess TCJProcess = this.Processes[i];
            stringBuilder.append(expression.toString() + ":" + TCJProcess.toString());
        }
        stringBuilder.append("}" + "\n");
        return stringBuilder.toString();
    }

    @Override
    public HashSet<String> GetAlphabets(Map<String, String> visitedDefinitionRefs) {

        HashSet<String> hashSet = new HashSet<>();
        for (int i = 0; i < this.Processes.length; i++) {
            TCJProcess TCJProcess = this.Processes[i];
            hashSet.addAll(TCJProcess.GetAlphabets(visitedDefinitionRefs));
        }
        return hashSet;
    }

    @Override
    public TCJProcess ClearConstant(Map<String, Expression> constMapping) {
        TCJProcess[] array = new TCJProcess[this.Processes.length];
        Expression[] array2 = new Expression[this.Processes.length];
        for (int i = 0; i < this.Processes.length; i++) {
            array2[i] = this.Conditions[i].clearConstant(constMapping);
            array[i] = this.Processes[i].ClearConstant(constMapping);
        }
        return new CaseProcess(array, array2);
    }

    @Override
    public boolean MustBeAbstracted() {
        for (int i = 0; i < this.Processes.length; i++) {
            if (this.Processes[i].MustBeAbstracted()) {
                return true;
            }
        }
        return false;
    }
}
