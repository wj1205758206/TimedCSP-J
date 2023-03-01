package module.tcj.lts;

import common.ParsingException;
import common.classes.expressions.Valuation;
import common.classes.moduleinterface.ConfigurationBase;

import java.util.ArrayList;
import java.util.List;

public class TickConfiguration extends ConfigurationBase {


    public TCJProcess Process;


    protected String ConfigID;


    public boolean IsUrgent;

    public TickConfiguration(TCJProcess p, String e, String hiddenEvent, Valuation globalEnv, boolean isDataOperation, boolean isUrgent) {
        this.Process = p;
        this.event = e;
        this.globalEnv = globalEnv;
        this.displayName = hiddenEvent;
        this.isDataOperation = isDataOperation;
        this.IsUrgent = isUrgent;
    }

    @Override
    public List<ConfigurationBase> makeOneMove() throws ParsingException, CloneNotSupportedException {
        List<TickConfiguration> list = new ArrayList<>();
        boolean eventTransitions = this.GetEventTransitions(list);
        TCJProcess TCJProcess = this.Process;
        while (!eventTransitions) {
            String processID = TCJProcess.ProcessID;
            TCJProcess = TCJProcess.GetProcessAfterDelay(this);
            if (TCJProcess == null || !(TCJProcess.ProcessID != processID)) {
                break;
            }
            TickConfiguration tickConfiguration = new TickConfiguration(TCJProcess, "τ", "tock", this.globalEnv, false, false);
            List<TickConfiguration> list2 = new ArrayList<>();
            eventTransitions = tickConfiguration.GetEventTransitions(list2);
            list.addAll(list2);
        }
        this.isDeadLock = (list.size() == 0);
        // List<TickConfiguration>  -->  List<ConfigurationBase>
        List<ConfigurationBase> configurationBaseList = new ArrayList<>(list);
        return configurationBaseList;
    }


    private boolean GetEventTransitions(List<TickConfiguration> toReturn) throws ParsingException, CloneNotSupportedException {
        this.Process.GetDiscretEventTransitions(this, toReturn);
        List<TickConfiguration> list = new ArrayList<>();
        boolean result = false;
        for (TickConfiguration tickConfiguration : toReturn) {
            if (tickConfiguration.isAtomic) {
                list.add(tickConfiguration);
            }
            if (tickConfiguration.IsUrgent) {
                result = true;
            }
        }
        if (list.size() > 0) {
            toReturn.clear();
            toReturn.addAll(list);
            return true;
        }
        return result;
    }


    public List<TickConfiguration> MakeOneMoveWithTockTransitions() throws ParsingException, CloneNotSupportedException {
        List<TickConfiguration> list = new ArrayList<>();
        this.Process.GetDiscretEventTransitions(this, list);
        List<TickConfiguration> list2 = new ArrayList<>();
        boolean flag = false;
        for (TickConfiguration tickConfiguration : list) {
            if (tickConfiguration.isAtomic) {
                list2.add(tickConfiguration);
            }
            if (tickConfiguration.IsUrgent) {
                flag = true;
            }
        }
        if (list2.size() > 0) {
            return list2;
        }
        if (!flag) {
            TCJProcess processAfterDelay = this.Process.GetProcessAfterDelay(this);
            if (processAfterDelay != null) {
                TickConfiguration item = new TickConfiguration(processAfterDelay, "tock", "tock", this.globalEnv, false, false);
                list.add(item);
                this.isDeadLock = (list.size() == 1 && processAfterDelay.ProcessID == this.Process.ProcessID);
            }
        }
        return list;
    }


    public boolean IsTock() {
        return this.event == "tock";
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("The process is:" + "\n");
        stringBuilder.append(this.Process.toString() + "\n");
        if (!this.globalEnv.isEmpty()) {
            stringBuilder.append("\n");
            stringBuilder.append("The environment is:" + "\n");
            stringBuilder.append(this.globalEnv.toString() + "\n");
        }
        return stringBuilder.toString();
    }


    @Override
    public String getID() {
        if (this.ConfigID == null) {
            if (this.globalEnv.isEmpty()) {
                this.ConfigID = this.Process.ProcessID;
            } else {
                this.ConfigID = this.globalEnv.getID(this.Process.ProcessID);
            }
        }
        return this.ConfigID;
    }

}
