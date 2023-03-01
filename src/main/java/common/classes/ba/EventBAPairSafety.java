package common.classes.ba;

import common.classes.moduleinterface.ConfigurationBase;
import common.classes.ultility.Ultility;

import java.util.ArrayList;
import java.util.List;

public final class EventBAPairSafety {
    public ConfigurationBase configuration;

    public List<String> States;

    public EventBAPairSafety(ConfigurationBase e, List<String> s) {
        this.configuration = e;
        this.States = s;
    }

    public String GetCompressedState() {
        return this.configuration.getIDWithEvent() + "*" + Ultility.PPStringList(this.States);
    }

    public static EventBAPairSafety GetInitialPairs(BuchiAutomata BA, ConfigurationBase initialStep) {
        List<String> list = new ArrayList<>();
        for (String state : BA.InitialStates) {
            List<String> list2 = BA.MakeOneMove(state, initialStep);
            for (String item : list2) {
                if (!list.contains(item)) {
                    list.add(item);
                }
            }
        }
        return new EventBAPairSafety(initialStep, list);
    }

    public EventBAPairSafety[] Next(BuchiAutomata BA, ConfigurationBase[] steps) {
        EventBAPairSafety[] array = new EventBAPairSafety[steps.length];
        for (int i = 0; i < steps.length; i++) {
            List<String> list = new ArrayList<>();
            for (String state : this.States) {
                List<String> list2 = BA.MakeOneMove(state, steps[i]);
                Ultility.Union(list, list2);
            }
            array[i] = new EventBAPairSafety(steps[i], list);
        }
        return array;
    }
}
