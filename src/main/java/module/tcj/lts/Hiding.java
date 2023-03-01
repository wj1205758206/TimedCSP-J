package module.tcj.lts;

import common.classes.expressions.expressionclass.Expression;
import common.classes.lts.Event;
import common.classes.lts.EventCollection;
import common.classes.ultility.Ultility;
import module.tcj.assertions.TCJDataStore;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public final class Hiding extends TCJProcess {

    public TCJProcess Process;


    public EventCollection HidingAlphabets;

    public Hiding(TCJProcess process, EventCollection alphabets) {
        if (process instanceof Hiding) {
            this.Process = ((Hiding) process).Process;
            List<Event> list = new ArrayList<>(((Hiding) process).HidingAlphabets);

            for (Event item : alphabets) {
                if (!list.contains(item)) {
                    list.add(item);
                }
            }
            this.HidingAlphabets = new EventCollection(list);
        } else {
            this.Process = process;
            this.HidingAlphabets = alphabets;
        }
        this.ProcessID = TCJDataStore.DataManager.InitializeProcessID("\\" + this.Process.ProcessID + "$" + this.HidingAlphabets.ProcessID);
    }

    @Override
    public List<ZoneConfiguration> MoveOneStep(ZoneConfiguration eStep) throws CloneNotSupportedException {
        List<ZoneConfiguration> list = this.Process.MoveOneStep(eStep);
        for (ZoneConfiguration zoneConfiguration : list) {
            zoneConfiguration.Process = new Hiding(zoneConfiguration.Process, this.HidingAlphabets);
            if (this.HidingAlphabets.ContainEventName(zoneConfiguration.event)) {
                zoneConfiguration.displayName = "[" + zoneConfiguration.event + "]";
                zoneConfiguration.event = "τ";
                zoneConfiguration.IsUrgent = true;
                zoneConfiguration.DBM = eStep.DBM.addUrgency();
            }
        }
        return list;
    }

    @Override
    public void GetDiscretEventTransitions(TickConfiguration eStep, List<TickConfiguration> toReturn) throws CloneNotSupportedException {
        this.Process.GetDiscretEventTransitions(eStep, toReturn);
        for (TickConfiguration tickConfiguration : toReturn) {
            if (this.HidingAlphabets.ContainEventName(tickConfiguration.event)) {
                tickConfiguration.displayName = "[" + tickConfiguration.event + "]";
                tickConfiguration.event = "τ";
                tickConfiguration.Process = new Hiding(tickConfiguration.Process, this.HidingAlphabets);
                tickConfiguration.IsUrgent = true;
            } else {
                tickConfiguration.Process = new Hiding(tickConfiguration.Process, this.HidingAlphabets);
            }
        }
    }

    @Override
    public TCJProcess GetProcessAfterDelay(TickConfiguration eStep) {
        return new Hiding(this.Process.GetProcessAfterDelay(eStep), this.HidingAlphabets);
    }

    @Override
    public List<ZoneConfigurationWithChannelData> SyncOutput(ZoneConfiguration eStep) {
        List<ZoneConfigurationWithChannelData> list = this.Process.SyncOutput(eStep);
        for (int i = 0; i < list.size(); i++) {
            list.get(i).Process = new Hiding(list.get(i).Process, this.HidingAlphabets);
        }
        return list;
    }

    @Override
    public List<ZoneConfiguration> SyncInput(ZoneConfigurationWithChannelData eStep) {
        List<ZoneConfiguration> list = this.Process.SyncInput(eStep);
        for (int i = 0; i < list.size(); i++) {
            list.get(i).Process = new Hiding(list.get(i).Process, this.HidingAlphabets);
        }
        return list;
    }

    @Override
    public void SyncOutput(TickConfiguration eStep, List<TickConfigurationWithChannelData> toReturn) {
        this.Process.SyncOutput(eStep, toReturn);
        for (TickConfigurationWithChannelData tickConfigurationWithChannelData : toReturn) {
            tickConfigurationWithChannelData.Process = new Hiding(tickConfigurationWithChannelData.Process, this.HidingAlphabets);
        }
    }

    @Override
    public void SyncInput(TickConfigurationWithChannelData eStep, List<TickConfiguration> toReturn) {
        this.Process.SyncInput(eStep, toReturn);
        for (TickConfiguration tickConfiguration : toReturn) {
            tickConfiguration.Process = new Hiding(tickConfiguration.Process, this.HidingAlphabets);
        }
    }

    @Override
    public String toString() {
        return "(" + this.Process + " \\ {" + Ultility.PPStringList(this.HidingAlphabets) + "})";
    }

    @Override
    public HashSet<String> GetAlphabets(Map<String, String> visitedDefinitionRefs) {
        HashSet<String> alphabets = this.Process.GetAlphabets(visitedDefinitionRefs);
        for (String item : this.HidingAlphabets.EventNames) {
            alphabets.remove(item);
        }
        return alphabets;
    }

    @Override
    public List<String> GetGlobalVariables() {
        return this.Process.GetGlobalVariables();
    }

    @Override
    public List<String> GetChannels() {
        return this.Process.GetChannels();
    }

    @Override
    public TCJProcess ClearConstant(Map<String, Expression> constMapping) {
        return new Hiding(this.Process.ClearConstant(constMapping), this.HidingAlphabets.ClearConstant(constMapping));
    }

    @Override
    public boolean MustBeAbstracted() {
        return this.Process.MustBeAbstracted();
    }
}
