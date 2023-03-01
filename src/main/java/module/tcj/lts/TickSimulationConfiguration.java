package module.tcj.lts;

import common.ParsingException;
import common.classes.expressions.Valuation;
import common.classes.moduleinterface.ConfigurationBase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TickSimulationConfiguration extends TickConfiguration {

    public TickSimulationConfiguration(TickConfiguration config) {
        super(config.Process, config.event, config.displayName, config.globalEnv, config.isDataOperation, config.IsUrgent);
    }

    public TickSimulationConfiguration(TCJProcess p, String e, String hiddenEvent, Valuation globalEnv, boolean isDataOperation, boolean isUrgent) {
        super(p, e, hiddenEvent, globalEnv, isDataOperation, isUrgent);
    }

    @Override
    public List<ConfigurationBase> makeOneMove() throws ParsingException, CloneNotSupportedException {
        List<TickConfiguration> list = new ArrayList<>();
        this.Process.GetDiscretEventTransitions(this, list);
        List<TickConfiguration> list2 = new ArrayList<>();
        boolean flag = false;
        for (TickConfiguration tickConfiguration : list) {
            if (tickConfiguration.isAtomic) {
                list2.add(new TickSimulationConfiguration(tickConfiguration));
            }
            if (tickConfiguration.IsUrgent) {
                flag = true;
            }
        }
        if (list2.size() > 0) {
            ConfigurationBase[] array = new ConfigurationBase[list2.size()];
            for (int i = 0; i < array.length; i++) {
                array[i] = list2.get(i);
            }
            return Arrays.asList(array);
        }
        if (!flag) {
            TCJProcess processAfterDelay = this.Process.GetProcessAfterDelay(this);
            if (processAfterDelay != null) {
                list.add(new TickConfiguration(processAfterDelay, "tock", "tock", this.globalEnv, false, false));
                this.isDeadLock = (list.size() == 1 && processAfterDelay.ProcessID == this.Process.ProcessID);
            } else {
                this.isDeadLock = (list.size() == 0);
            }
        } else {
            this.isDeadLock = (list.size() == 0);
        }
        ConfigurationBase[] array = new ConfigurationBase[list.size()];
        for (int i = 0; i < list.size(); i++) {
            array[i] = new TickSimulationConfiguration(list.get(i));
        }
        return Arrays.asList(array);
    }
}
