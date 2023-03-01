package common.classes.semanticmodels.lts.assertion;

import common.classes.ba.BuchiAutomata;
import common.classes.moduleinterface.ConfigurationBase;
import common.classes.ultility.FairnessType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public final class LocalPair {

    public ConfigurationBase configuration;

    public String state;

    public List<String> Enabled;

    public LocalPair(ConfigurationBase e, String s) {
        this.configuration = e;
        this.state = s;
        this.Enabled = new ArrayList<>();
    }

    public String GetCompressedState() {
        return this.configuration.getIDWithEvent() + "$" + this.state;
    }

    public String GetCompressedState(FairnessType fairness) {
        if (fairness != FairnessType.PROCESS_LEVEL_STRONG_FAIRNESS && fairness != FairnessType.PROCESS_LEVEL_WEAK_FAIRNESS) {
            return this.configuration.getIDWithEvent() + "$" + this.state;
        }
        if (this.configuration.participatingProcesses == null) {
            return this.configuration.getIDWithEvent() + "$" + this.state;
        }
        String text = "";
        for (String str : this.configuration.participatingProcesses) {
            text = text + str + "$";
        }
        return this.configuration.getIDWithEvent() + "$" + text + this.state;
    }

    public static List<LocalPair> GetInitialPairsLocal(BuchiAutomata BA, ConfigurationBase initialStep) {
        List<LocalPair> list = new ArrayList<>();
        HashSet<String> hashSet = new HashSet<>();
        for (String text : BA.InitialStates) {
            List<String> list2 = BA.MakeOneMove(text, initialStep);
            for (String text2 : list2) {
                if (hashSet.add(text2)) {
                    list.add(new LocalPair(initialStep, text2));
                }
            }
        }
        return list;
    }

    public static List<LocalPair> NextLocal(BuchiAutomata BA, List<ConfigurationBase> steps, String BAState) {
        List<LocalPair> list = new ArrayList<>(steps.size() * BA.States.length);
        //List<LocalPair> list = new List<LocalPair>(steps.Count<ConfigurationBase>() * BA.States.Length);
        for (ConfigurationBase configurationBase : steps) {
            List<String> list2 = BA.MakeOneMove(BAState, configurationBase);
            for (int i = 0; i < list2.size(); i++) {
                list.add(new LocalPair(configurationBase, list2.get(i)));
            }
        }
        return list;
    }

    public void SetEnabled(List<ConfigurationBase> steps, FairnessType fairness) {
        switch (fairness) {
            case EVENT_LEVEL_WEAK_FAIRNESS:
            case EVENT_LEVEL_STRONG_FAIRNESS:
                for (ConfigurationBase configurationBase : steps) {
                    this.Enabled.add(configurationBase.event);
                }
                return;

            /*    using (IEnumerator<ConfigurationBase> enumerator = steps.GetEnumerator())
            {
                while (enumerator.MoveNext())
                {
                    ConfigurationBase configurationBase = enumerator.Current;
                    this.Enabled.Add(configurationBase.Event);
                }
                return;
            }
            break;*/
            case PROCESS_LEVEL_WEAK_FAIRNESS:
            case PROCESS_LEVEL_STRONG_FAIRNESS:
                break;
            case GLOBAL_FAIRNESS:
                //goto IL_C2;
                for (ConfigurationBase configurationBase3 : steps) {
                    this.Enabled.add(configurationBase3.getIDWithEvent());
                }
                return;
            default:
                return;
        }
        for (ConfigurationBase configurationBase2 : steps) {
            for (String item : configurationBase2.participatingProcesses) {
                if (!this.Enabled.contains(item)) {
                    this.Enabled.add(item);
                }
            }
        }
        return;

        /*using(IEnumerator < ConfigurationBase > enumerator2 = steps.GetEnumerator())
        {
            while (enumerator2.MoveNext()) {
                ConfigurationBase configurationBase2 = enumerator2.Current;
                foreach(string item in configurationBase2.ParticipatingProcesses)
                {
                    if (!this.Enabled.Contains(item)) {
                        this.Enabled.Add(item);
                    }
                }
            }
            return;
        }*/
        /*IL_C2:
        foreach(ConfigurationBase configurationBase3 in steps)
        {
            this.Enabled.Add(configurationBase3.GetIDWithEvent());
        }*/
    }
}
