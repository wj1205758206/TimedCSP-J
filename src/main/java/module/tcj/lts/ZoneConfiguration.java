package module.tcj.lts;

import common.ParsingException;
import common.classes.datastructure.TimerOperationType;
import common.classes.expressions.Valuation;
import common.classes.moduleinterface.ConfigurationBase;
import common.classes.datastructure.DBM;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ZoneConfiguration extends ConfigurationBase {

    public TCJProcess Process;


    public boolean IsUrgent;


    protected String ConfigID;


    public DBM DBM;


    public List<List<Integer>> clocksBound = new ArrayList<>();


    public Map<Integer, Integer> mapping = new HashMap<>();


    private String ConfigIDWithoutDBM;


    public ZoneConfiguration(TCJProcess p, String e, String hiddenEvent, Valuation globalEnv, boolean isDataOperation, DBM dbm) {
        this.Process = p;
        this.event = e;
        this.globalEnv = globalEnv;
        this.displayName = hiddenEvent;
        this.isDataOperation = isDataOperation;
        this.DBM = dbm;
    }

    @Override
    public List<ConfigurationBase> makeOneMove() throws ParsingException, CloneNotSupportedException {
        List<ZoneConfiguration> zoneConfigurations = this.MakeOneMoveLocal();
        List<ConfigurationBase> configurationBaseList =  new ArrayList<>(zoneConfigurations);
        return configurationBaseList;
    }


    public void SetTimer() throws ParsingException {
        this.Process = this.Process.SetTimer(this.globalEnv, this.clocksBound, this.mapping);
        this.DBM = this.DBM.cleanAndRename(this.mapping, this.clocksBound.size());
    }


    public List<ZoneConfiguration> MakeOneMoveLocal() throws ParsingException, CloneNotSupportedException {
        DBM dbm = this.DBM;
        this.DBM = this.DBM.clone();
        int num = this.clocksBound.size() + 1;
        this.DBM.addTimer(num);
        this.DBM.up();
        for (int i = 0; i < this.clocksBound.size(); i++) {
            this.DBM.addConstraint(i + 1, TimerOperationType.LessThanOrEqualTo, this.clocksBound.get(i).get(0));
        }
        List<ZoneConfiguration> list = this.Process.MoveOneStep(this);
        List<ZoneConfiguration> list2 = new ArrayList<>();
        List<ZoneConfiguration> list3 = new ArrayList<>();
        List<ZoneConfiguration> list4 = new ArrayList<>();
        boolean flag = false;
        boolean flag2 = false;
        for (ZoneConfiguration zoneConfiguration : list) {
            if (!zoneConfiguration.DBM.isEmpty()) {
                if (zoneConfiguration.isAtomic) {
                    if (zoneConfiguration.DBM == this.DBM) {
                        zoneConfiguration.IsUrgent = true;
                        zoneConfiguration.DBM = zoneConfiguration.DBM.clone();
                        zoneConfiguration.DBM.addConstraint(num, TimerOperationType.LessThanOrEqualTo, 0);
                        zoneConfiguration.SetTimer();
                        list2.add(zoneConfiguration);
                    } else if (zoneConfiguration.DBM.getTimerUpper(num) == 0) {
                        zoneConfiguration.SetTimer();
                        list2.add(zoneConfiguration);
                    } else {
                        list3.add(zoneConfiguration);
                        flag2 = true;
                    }
                } else if (zoneConfiguration.DBM.getTimerUpper(num) == 0) {
                    flag = true;
                    zoneConfiguration.IsUrgent = true;
                    list3.add(zoneConfiguration);
                } else if (zoneConfiguration.DBM == this.DBM) {
                    list3.add(zoneConfiguration);
                } else {
                    list4.add(zoneConfiguration);
                }
            }
        }
        this.DBM = dbm;
        if (list2.size() > 0) {
            return list2;
        }
        if (flag) {
            List<ZoneConfiguration> list5 = new ArrayList<>();
            for (ZoneConfiguration zoneConfiguration2 : list3) {
                zoneConfiguration2.DBM = zoneConfiguration2.DBM.clone();
                zoneConfiguration2.DBM.addConstraint(num, TimerOperationType.LessThanOrEqualTo, 0);
                if (!zoneConfiguration2.DBM.isEmpty()) {
                    zoneConfiguration2.SetTimer();
                    zoneConfiguration2.IsUrgent = true;
                    list5.add(zoneConfiguration2);
                }
            }
            return list5;
        }
        if (!flag2) {
            list3.addAll(list4);
        }
        for (ZoneConfiguration zoneConfiguration3 : list3) {
            zoneConfiguration3.SetTimer();
        }
        this.isDeadLock = (list3.size() == 0);
        return list3;
    }

    @Override
    public String getID() {
        if (this.ConfigID == null) {
            this.ConfigID = this.GetIDNoDBM() + "$" + this.DBM.getID();
        }
        return this.ConfigID;
    }

    public String GetIDNoDBM() {
        if (this.ConfigIDWithoutDBM == null) {
            if (this.globalEnv.isEmpty()) {
                this.ConfigIDWithoutDBM = this.Process.ProcessID;
            } else {
                this.ConfigIDWithoutDBM = this.globalEnv.getID(this.Process.ProcessID);
            }
        }
        return this.ConfigIDWithoutDBM;
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
        if (this.DBM.getDim() > 1) {
            stringBuilder.append("\n");
            stringBuilder.append("The clock zone is:" + "\n");
            stringBuilder.append(this.DBM.toString() + "\n");
        }
        return stringBuilder.toString();
    }


    @Override
    public String getDisplayEvent() {
        String displayEvent = this.getDisplayEvent();
        if (displayEvent == "init" || !this.IsUrgent) {
            return displayEvent;
        }
        return displayEvent + "*";
    }
}
