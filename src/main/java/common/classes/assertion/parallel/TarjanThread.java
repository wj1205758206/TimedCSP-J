package common.classes.assertion.parallel;

import common.classes.ba.BuchiAutomata;
import common.classes.datastructure.StringDictionary;
import common.classes.datastructure.StringHashTable;
import common.classes.moduleinterface.ConfigurationBase;
import common.classes.moduleinterface.VerificationResultType;
import common.classes.semanticmodels.lts.assertion.LocalPair;
import common.classes.ultility.FairnessType;
import common.classes.ultility.Ultility;

import java.util.*;

public class TarjanThread {
    protected static final int SCC_MINIMUM_SIZE_BOUND_FOR_THREAD_FORKING = 100;

    protected static final int VISITED_NOPREORDER = -1;

    protected static final int SCC_FOUND = -2;

    public Map<String, List<String>> OutgoingTransitionTable;


    public VerificationResultType VerificationResult;

    public Stack<LocalPair> TaskStack;


    private BuchiAutomata BA;


    public int SearchedDepth;


    public long Transitions;


    private PATThreadPool ThreadPool;


    public boolean JobFinished;


    public boolean CancelRequested;


    private ConfigurationBase initialStep;


    private StringDictionary<int[]> DFSData;


    public Map<String, LocalPair> FairSCC;


    public int SCCCount;


    public int BigSCCCount;

    public long SCCTotalSize;

    public FairnessType FairnessType;

    public TarjanThread(ConfigurationBase initialStep, BuchiAutomata ba, PATThreadPool threadPool, FairnessType FairnessType) {
        this.initialStep = initialStep;
        this.VerificationResult = VerificationResultType.UNKNOWN;
        this.BA = ba;
        this.ThreadPool = threadPool;
        this.FairnessType = FairnessType;
        this.FairSCC = null;
        this.JobFinished = false;
    }

    public long GetNoOfStates() {
        return (long) this.DFSData.count;
    }

    public void TarjanModelChecking() throws Exception {
        this.OutgoingTransitionTable = new HashMap<>(Ultility.MC_INITIAL_SIZE);
        this.TaskStack = new Stack<LocalPair>();
        this.DFSData = new StringDictionary<int[]>(Ultility.MC_INITIAL_SIZE);
        List<LocalPair> initialPairsLocal = LocalPair.GetInitialPairsLocal(this.BA, this.initialStep);
        if (initialPairsLocal.size() == 0 || !this.BA.HasAcceptState) {
            this.VerificationResult = VerificationResultType.VALID;
            return;
        }
        for (int i = 0; i < initialPairsLocal.size(); i++) {
            LocalPair localPair = initialPairsLocal.get(i);
            this.TaskStack.push(localPair);
            String compressedState = localPair.GetCompressedState();
            StringDictionary<int[]> dfsdata = this.DFSData;
            String key = compressedState;
            int[] array = new int[2];
            array[0] = -1;
            dfsdata.add(key, array);
            this.OutgoingTransitionTable.put(compressedState, new ArrayList<>(8));
        }
        Map<String, LocalPair> dictionary = new HashMap<>(1024);
        Stack<LocalPair> stack = new Stack<LocalPair>();
        int num = 0;
        Map<String, List<LocalPair>> dictionary2 = new HashMap<>(1024);
        Map<String, LocalPair> dictionary3;
        for (; ; ) {
            if (this.SearchedDepth < this.TaskStack.size()) {
                this.SearchedDepth = this.TaskStack.size();
            }
            if (this.JobFinished) {
                break;
            }
            LocalPair localPair2 = this.TaskStack.peek();
            ConfigurationBase configuration = localPair2.configuration;
            String state = localPair2.state;
            String compressedState2 = localPair2.GetCompressedState();
            List<String> list = this.OutgoingTransitionTable.get(compressedState2);
            int[] containsKey = this.DFSData.getContainsKey(compressedState2);
            if (containsKey[0] == -1) {
                containsKey[0] = num;
                num++;
            }
            boolean flag = true;
            if (dictionary2.containsKey(compressedState2)) {
                List<LocalPair> list2 = dictionary2.get(compressedState2);
                if (list2.size() > 0) {
                    for (int j = list2.size() - 1; j >= 0; j--) {
                        LocalPair localPair3 = list2.get(j);
                        String compressedState3 = localPair3.GetCompressedState();
                        if (this.DFSData.getContainsKey(compressedState3)[0] == -1) {
                            if (flag) {
                                this.TaskStack.push(localPair3);
                                flag = false;
                                list2.remove(j);
                            }
                        } else {
                            list2.remove(j);
                        }
                    }
                }
            } else {
                List<ConfigurationBase> steps = configuration.makeOneMove();
                localPair2.SetEnabled(steps, this.FairnessType);
                List<LocalPair> list3 = LocalPair.NextLocal(this.BA, steps, state);
                this.Transitions += (long) list3.size();
                for (int k = list3.size() - 1; k >= 0; k--) {
                    LocalPair localPair4 = list3.get(k);
                    String compressedState4 = localPair4.GetCompressedState();
                    int[] containsKey2 = this.DFSData.getContainsKey(compressedState4);
                    if (containsKey2 != null) {
                        list.add(compressedState4);
                        if (containsKey2[0] == -1) {
                            if (flag) {
                                this.TaskStack.push(localPair4);
                                flag = false;
                                list3.remove(k);
                            } else {
                                list3.set(k, localPair4);
                            }
                        } else {
                            list3.remove(k);
                        }
                    } else {
                        StringDictionary<int[]> dfsdata2 = this.DFSData;
                        String key2 = compressedState4;
                        int[] array2 = new int[2];
                        array2[0] = -1;
                        dfsdata2.add(key2, array2);
                        this.OutgoingTransitionTable.put(compressedState4, new ArrayList<>(8));
                        list.add(compressedState4);
                        if (flag) {
                            this.TaskStack.push(localPair4);
                            flag = false;
                            list3.remove(k);
                        } else {
                            list3.set(k, localPair4);
                        }
                    }
                }
                dictionary2.put(compressedState2, list3);
            }
            if (flag) {
                int num2 = containsKey[0];
                int num3 = num2;
                boolean flag2 = false;
                for (int l = 0; l < list.size(); l++) {
                    String text = list.get(l);
                    if (text == compressedState2) {
                        flag2 = true;
                    }
                    int[] containsKey3 = this.DFSData.getContainsKey(text);
                    if (containsKey3[0] != -2) {
                        if (containsKey3[0] > num3) {
                            num2 = Math.min(num2, containsKey3[1]);
                        } else {
                            num2 = Math.min(num2, containsKey3[0]);
                        }
                    }
                }
                containsKey[1] = num2;
                this.TaskStack.pop();
                if (num2 == num3) {
                    dictionary.put(compressedState2, localPair2);
                    containsKey[0] = -2;
                    boolean flag3 = localPair2.state.endsWith("-");
                    while (stack.size() > 0 && this.DFSData.getContainsKey(stack.peek().GetCompressedState())[0] > num3) {
                        LocalPair localPair5 = stack.pop();
                        String compressedState5 = localPair5.GetCompressedState();
                        dictionary.put(compressedState5, localPair5);
                        this.DFSData.getContainsKey(compressedState5)[0] = -2;
                        if (!flag3 && localPair5.state.endsWith("-")) {
                            flag3 = true;
                        }
                    }
                    int count = dictionary.size();
                    if (flag3 && (configuration.isDeadLock || count > 1 || flag2)) {
                        this.SCCTotalSize += (long) count;
                        if (count >= 100 && this.ThreadPool.ThreadNumber < Ultility.PARALLEL_MODEL_CHECKIMG_BOUND) {
                            Map<String, LocalPair> stronglyConnectedComponents = new HashMap<>(dictionary);
                            this.StartFairThread(stronglyConnectedComponents);
                        } else {
                            dictionary3 = this.IsFair(dictionary, this.OutgoingTransitionTable);
                            if (dictionary3 != null) {
                                //goto Block_30;
                                this.JobFinished = true;
                                this.VerificationResult = VerificationResultType.INVALID;
                                this.FairSCC = dictionary3;
                                this.ThreadPool.StopAllThreads();
                                return;
                            }
                        }
                    }
                    for (String key3 : dictionary.keySet()) {
                        dictionary2.remove(key3);
                    }
                    dictionary = new HashMap<>(1024);
                } else {
                    stack.push(localPair2);
                }
            }
            if (this.TaskStack.size() <= 0) {
                //goto Block_32;
                this.JobFinished = true;
            }
        }
        return;
        /*Block_30:
        this.JobFinished = true;
        this.VerificationResult = VerificationResultType.INVALID;
        this.FairSCC = dictionary3;
        this.ThreadPool.StopAllThreads();
        return;
        Block_32:
        this.JobFinished = true;*/
    }

    private void StartFairThread(Map<String, LocalPair> StronglyConnectedComponents) {
        Stack<LocalPair> stack = new Stack<LocalPair>();
        LocalPair[] array = this.TaskStack.toArray(new LocalPair[0]);
        for (int i = array.length - 1; i >= 0; i--) {
            stack.push(array[i]);
        }
        Map<String, List<String>> dictionary = new HashMap<>(1024);
        for (Map.Entry<String, LocalPair> keyValuePair : StronglyConnectedComponents.entrySet()) {
            String key = keyValuePair.getKey();
            dictionary.put(key, this.OutgoingTransitionTable.get(key));
        }
        FairThread thread = new FairThread(StronglyConnectedComponents, stack, this.FairnessType, dictionary);
        this.ThreadPool.AddThread(thread);
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

    private Map<String, LocalPair> IsGlobalFair(Map<String, LocalPair> StronglyConnectedComponents, Map<String, List<String>> OutgoingTransitionTable) {
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
        HashSet<String> hashSet = new HashSet<String>();
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
        while (!this.JobFinished) {
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
                                num2 = Math.min(num2, dictionary2.get(text3));
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
                        Map<String, LocalPair> dictionary4 = this.IsFair(dictionary, OutgoingTransitionTable);
                        if (dictionary4 != null) {
                            return dictionary4;
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
                return null;
            }
        }
        return null;
    }
}
