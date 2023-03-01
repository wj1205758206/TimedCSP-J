package common.classes.assertion.parallel;

import common.classes.datastructure.StringHashTable;
import common.classes.moduleinterface.ConfigurationBase;
import common.classes.semanticmodels.lts.assertion.LocalPair;
import common.classes.ultility.FairnessType;
import common.classes.ultility.Ultility;
import org.omg.CORBA.WStringSeqHelper;

import java.util.*;

public final class FairThread {
    public boolean result;


    private Map<String, LocalPair> SCC;


    private Map<String, List<String>> OutgoingTransitionTable;


    public FairnessType FairnessType;


    public Map<String, LocalPair> FairSCC;


    public boolean CancelRequested;


    public Stack<LocalPair> TaskStack;

    //public event ReturnEvent ReturnAction;
    public ReturnEvent ReturnAction;

    public FairThread(Map<String, LocalPair> scc, Stack<LocalPair> tStack, FairnessType fairType, Map<String, List<String>> OutgoingTransitionTable) {
        this.SCC = scc;
        this.TaskStack = tStack;
        this.FairnessType = fairType;
        this.OutgoingTransitionTable = OutgoingTransitionTable;
    }

    public void InternalStart(Object o) throws Exception {
        this.FairSCC = this.IsFair(this.SCC, this.OutgoingTransitionTable);
        if (this.FairSCC != null) {
            this.result = true;
        } else {
            this.result = false;
        }
        synchronized (this) {
            //this.ReturnAction(this);
            this.ReturnAction.onReturnEvent(this);
        }
    }

    protected Map<String, LocalPair> IsFair(Map<String, LocalPair> StronglyConnectedComponents, Map<String, List<String>> OutgoingTransitionTable) throws Exception {
        switch (this.FairnessType) {
            case NO_FAIRNESS:
                return StronglyConnectedComponents;
            case EVENT_LEVEL_WEAK_FAIRNESS:
                return this.IsEventWeakFair(StronglyConnectedComponents);
            case EVENT_LEVEL_STRONG_FAIRNESS:
                return this.IsEventStrongFair(StronglyConnectedComponents, OutgoingTransitionTable);
            case PROCESS_LEVEL_WEAK_FAIRNESS:
                return this.IsProcesstWeakFair(StronglyConnectedComponents);
            case PROCESS_LEVEL_STRONG_FAIRNESS:
                return this.IsProcessStrongFair(StronglyConnectedComponents, OutgoingTransitionTable);
            case GLOBAL_FAIRNESS:
                return this.IsGlobalFair(StronglyConnectedComponents, OutgoingTransitionTable);
            default:
                return null;
        }
    }

    private Map<String, LocalPair> IsGlobalFair(Map<String, LocalPair> StronglyConnectedComponents,
                                                Map<String, List<String>> OutgoingTransitionTable) {
        for (Map.Entry<String, LocalPair> keyValuePair : StronglyConnectedComponents.entrySet()) {
            String key = keyValuePair.getKey();
            LocalPair value = keyValuePair.getValue();
            List<String> list = OutgoingTransitionTable.get(key);
            for (String b : value.Enabled) {
                boolean flag = false;
                for (String key2 : list) {
                    if (StronglyConnectedComponents.containsKey(key2) && StronglyConnectedComponents.get(key2).configuration.getIDWithEvent() == b) {
                        flag = true;
                        break;
                    }
                }
                if (!flag) {
                    return null;
                }
            }
        }
        return StronglyConnectedComponents;
    }

    private Map<String, LocalPair> IsEventStrongFair(Map<String, LocalPair> StronglyConnectedComponents, Map<String, List<String>> OutgoingTransitionTable) throws Exception {
        List<String> list = new ArrayList<>();
        list.add("τ");
        for (Map.Entry<String, LocalPair> keyValuePair : StronglyConnectedComponents.entrySet()) {
            ConfigurationBase configuration = keyValuePair.getValue().configuration;
            if (!list.contains(configuration.event) && (StronglyConnectedComponents.size() > 1 || !configuration.isDeadLock)) {
                list.add(configuration.event);
            }
        }
        List<String> list2 = new ArrayList<>();
        for (Map.Entry<String, LocalPair> keyValuePair2 : StronglyConnectedComponents.entrySet()) {
            String key = keyValuePair2.getKey();
            LocalPair value = keyValuePair2.getValue();
            for (String item : value.Enabled) {
                if (!list.contains(item)) {
                    list2.add(key);
                }
            }
        }
        if (list2.size() > 0) {
            for (String key2 : list2) {
                StronglyConnectedComponents.remove(key2);
            }
            if (StronglyConnectedComponents.size() > 0) {
                Map<String, LocalPair> dictionary = this.TarjanModelChecking2(StronglyConnectedComponents, OutgoingTransitionTable);
                if (dictionary != null) {
                    return dictionary;
                }
            }
            return null;
        }
        return StronglyConnectedComponents;
    }

    private Map<String, LocalPair> IsEventWeakFair(Map<String, LocalPair> StronglyConnectedComponents) {
        List<String> list = null;
        HashSet<String> hashSet = new HashSet<>();
        hashSet.add("τ");
        for (Map.Entry<String, LocalPair> keyValuePair : StronglyConnectedComponents.entrySet()) {
            LocalPair value = keyValuePair.getValue();
            if (!hashSet.contains(value.configuration.event) && (StronglyConnectedComponents.size() > 1 || !value.configuration.isDeadLock)) {
                hashSet.add(value.configuration.event);
            }
            if (list == null) {
                list = new ArrayList<>();
                list.addAll(value.Enabled);
            } else if (list.size() > 0) {
                list = Ultility.Intersect(list, value.Enabled);
            }
        }
        for (int i = 0; i < list.size(); i++) {
            String item = list.get(i);
            if (!hashSet.contains(item)) {
                return null;
            }
        }
        return StronglyConnectedComponents;
    }

    private Map<String, LocalPair> IsProcessStrongFair(Map<String, LocalPair> StronglyConnectedComponents, Map<String, List<String>> OutgoingTransitionTable) throws Exception {
        HashSet<String> hashSet = new HashSet<>();
        for (Map.Entry<String, LocalPair> keyValuePair : StronglyConnectedComponents.entrySet()) {
            ConfigurationBase configuration = keyValuePair.getValue().configuration;
            if (configuration.participatingProcesses != null) {
                String[] participatingProcesses = configuration.participatingProcesses;
                if (participatingProcesses != null) {
                    for (int i = 0; i < participatingProcesses.length; i++) {
                        if (!hashSet.contains(participatingProcesses[i])) {
                            hashSet.add(participatingProcesses[i]);
                        }
                    }
                }
            }
        }
        HashSet<String> hashSet2 = new HashSet<>();
        for (Map.Entry<String, LocalPair> keyValuePair2 : StronglyConnectedComponents.entrySet()) {
            String key = keyValuePair2.getKey();
            LocalPair value = keyValuePair2.getValue();
            for (int j = 0; j < value.Enabled.size(); j++) {
                if (!hashSet.contains(value.Enabled.get(j)) && !hashSet2.contains(key)) {
                    hashSet2.add(key);
                }
            }
        }
        if (hashSet2.size() > 0) {
            for (String key2 : hashSet2) {
                StronglyConnectedComponents.remove(key2);
            }
            if (StronglyConnectedComponents.size() > 0) {
                Map<String, LocalPair> dictionary = this.TarjanModelChecking2(StronglyConnectedComponents, OutgoingTransitionTable);
                if (dictionary != null) {
                    return dictionary;
                }
            }
            return null;
        }
        return StronglyConnectedComponents;
    }

    private Map<String, LocalPair> IsProcesstWeakFair(Map<String, LocalPair> StronglyConnectedComponents) {
        List<String> list = new ArrayList<>();
        HashSet<String> hashSet = new HashSet<>();
        for (Map.Entry<String, LocalPair> keyValuePair : StronglyConnectedComponents.entrySet()) {
            String key = keyValuePair.getKey();
            LocalPair value = keyValuePair.getValue();
            if (list.size() > 0) {
                list = Ultility.Intersect(list, value.Enabled);
            } else {
                list.addAll(value.Enabled);
            }
            if (value.configuration.participatingProcesses != null) {
                String[] participatingProcesses = value.configuration.participatingProcesses;
                for (String item : participatingProcesses) {
                    if (!hashSet.contains(item)) {
                        hashSet.add(item);
                    }
                }
            }
        }
        for (String item2 : list) {
            if (!hashSet.contains(item2)) {
                return null;
            }
        }
        return StronglyConnectedComponents;
    }

    protected Map<String, LocalPair> TarjanModelChecking2(Map<String, LocalPair> SCC, Map<String, List<String>> OutgoingTransitionTable) throws Exception {
        Map<String, LocalPair> dictionary = new HashMap<>(64);
        Map<String, Integer> dictionary2 = new HashMap<>(64);
        Map<String, Integer> dictionary3 = new HashMap<>(64);
        Stack<String> stack = new Stack<String>();
        StringHashTable stringHashTable = new StringHashTable(64);
        int num = 0;
        Stack<String> stack2 = new Stack<String>();
        //Dictionary<string, LocalPair>.KeyCollection.Enumerator enumerator = SCC.Keys.GetEnumerator();
        //enumerator.MoveNext();
        //stack2.Push(enumerator.Current);
        String current = SCC.keySet().iterator().next();
        stack2.push(current);
        Map<String, LocalPair> dictionary4;
        for (; ; ) {
            String text = stack2.peek();
            List<String> list = OutgoingTransitionTable.get(text);
            if (!dictionary2.containsKey(text)) {
                dictionary2.put(text, num);
                num++;
            }
            boolean flag = true;
            for (int i = 0; i < list.size(); i++) {
                String text2 = list.get(i);
                if (SCC.containsKey(text2) && !dictionary2.containsKey(text2)) {
                    stack2.push(text2);
                    flag = false;
                    break;
                }
            }
            if (flag) {
                int num2 = dictionary2.get(text);
                int num3 = num2;
                boolean flag2 = false;
                for (int j = 0; j < list.size(); j++) {
                    String text3 = list.get(j);
                    if (SCC.containsKey(text3)) {
                        if (text3 == text) {
                            flag2 = true;
                        }
                        if (!stringHashTable.containsKey(text3)) {
                            if (dictionary2.get(text3) > num3) {
                                num2 = Math.min(num2, dictionary3.get(text3));
                            } else {
                                num2 = Math.max(num2, dictionary2.get(text3));
                            }
                        }
                    }
                }
                dictionary3.put(text, num2);
                stack2.pop();
                if (num2 == num3) {
                    stringHashTable.add(text);
                    dictionary.put(text, SCC.get(text));
                    boolean flag3 = SCC.get(text).state.endsWith("-");
                    while (stack.size() > 0 && dictionary2.get(stack.peek()) > num3) {
                        String key = stack.pop();
                        dictionary.put(key, SCC.get(key));
                        stringHashTable.add(key);
                        if (!flag3 && SCC.get(key).state.endsWith("-")) {
                            flag3 = true;
                        }
                    }
                    if (flag3 && (list.size() == 0 || dictionary.size() > 1 || flag2)) {
                        dictionary4 = this.IsFair(dictionary, OutgoingTransitionTable);
                        if (dictionary4 != null) {
                            break;
                        }
                    }
                    dictionary.clear();
                } else {
                    stack.push(text);
                }
            }
            if (stack2.size() == 0 && stringHashTable.count != SCC.size()) {
                for (String text4 : SCC.keySet()) {
                    if (!stringHashTable.containsKey(text4)) {
                        stack2.push(text4);
                        break;
                    }
                }
            }
            if (stack2.size() <= 0) {
                //goto Block_21;
                return null;
            }
        }
        return dictionary4;
        //Block_21:
        //return null;
    }
}
