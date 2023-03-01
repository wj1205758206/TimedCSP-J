package common.classes.semanticmodels.lts.assertion;

import common.CancelRunningException;
import common.ParsingException;
import common.classes.assertion.parallel.TarjanThread;
import common.classes.ba.BuchiAutomata;
import common.classes.ba.EventBAPairSafety;
import common.classes.datastructure.StringDictionary;
import common.classes.datastructure.StringHashTable;
import common.classes.moduleinterface.AssertionBase;
import common.classes.moduleinterface.ConfigurationBase;
import common.classes.moduleinterface.VerificationResultType;
import common.classes.ultility.FairnessType;
import common.classes.ultility.Ultility;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public abstract class AssertionLTL extends AssertionBase {
    public static final int CORES = 1;

    public static final String EVENT_DUMMY = "#event_dummy";

    protected static final int VISITED_NOPREORDER = -1;


    protected static final int SCC_FOUND = -2;


    public List<ConfigurationBase> finalTrace;


    public int finalLoopIndex;


    public boolean isGlobalStop;


    public Object globalCounterExampleLocker;


    private ConcurrentHashMap<String, Boolean> globalRedStates;


    private ConcurrentHashMap<String, Integer> globalAcceptingCounter;

    private ConcurrentHashMap<String, Boolean> globalBlueRedStates;

    private ConcurrentHashMap<String, Boolean> globalFoundSCCs;

    private ConcurrentLinkedQueue<SCC>[] queueSCCArray;

    private ConcurrentHashMap<String, Integer> allVisitedStates;

    //public List<CUDDNode> transitionsNoEvents = new List<CUDDNode>();

    //public List<CUDDNode> prefix = new List<CUDDNode>();

    //public List<CUDDNode> period = new List<CUDDNode>();

    public Stack<LocalPair> LocalTaskStack;


    private Object MultiCoreLock;


    private boolean StopMutliCoreThreads;


    private Map<String, List<String>> MultiCoreOutgoingTransitionTable;


    private Map<String, LocalPair> MultiCoreResultedLoop;

    private Stack<LocalPair> MultiCoreLocalTaskStack;


    private int MultiCoreSeed;


    public boolean[] isProducerStop;


    private int MULTICORE_IMPROVED_TARJAN_GELDENHUYSVALMARI_NUM_THREADS = Runtime.getRuntime().availableProcessors();

    private StringDictionary<Boolean> foundSCCTarjanGeldenhuysValmari;


    private int SWARM_NDFS_NUM_THREADS = Runtime.getRuntime().availableProcessors();


    protected int MULTICORE_NDFS_NUM_THREADS = Runtime.getRuntime().availableProcessors();


    private StringDictionary<Boolean> SharedRedStatesMultiCoreNDFS;


    private Map<String, Integer> StateCountMultiCoreNDFS;


    private int SWARM_TARJAN_NUM_THREADS = Runtime.getRuntime().availableProcessors();


    private TarjanThread tarjanThread;


    public BuchiAutomata BA;


    public BuchiAutomata negationLTLBuchi;


    public boolean hasFairness;


    protected boolean IsNegateLiveness;


    public boolean IsSafety;


    public FairnessType FairnessType;

    public String LTLString;

    protected AssertionLTL(String ltl) {
        this.LTLString = ltl;
    }

    public boolean IsProcessLevelFairnessApplicable() {
        return false;
    }

    public void BFSVerification() throws Exception {
        StringHashTable stringHashTable = new StringHashTable(Ultility.MC_INITIAL_SIZE);
        Queue<EventBAPairSafety> queue = new ArrayDeque<>();
        Queue<List<ConfigurationBase>> queue2 = new ArrayDeque<>(1024);
        EventBAPairSafety initialPairs = EventBAPairSafety.GetInitialPairs(this.BA, this.initialStep);
        queue.add(initialPairs);
        queue2.add(Arrays.asList(this.initialStep));
        stringHashTable.add(initialPairs.GetCompressedState());
        while (!this.cancelRequested()) {
            EventBAPairSafety eventBAPairSafety = queue.poll();
            List<ConfigurationBase> list = queue2.poll();
            if (eventBAPairSafety.States.size() == 0) {
                this.verificationOutput.verificationResult = VerificationResultType.INVALID;
                this.verificationOutput.noOfStates = (long) stringHashTable.count;
                this.verificationOutput.counterExampleTrace = list;
                return;
            }
            ConfigurationBase[] array = eventBAPairSafety.configuration.makeOneMove().toArray(new ConfigurationBase[0]);
            this.verificationOutput.transitions += (long) array.length;
            EventBAPairSafety[] array2 = eventBAPairSafety.Next(this.BA, array);
            for (EventBAPairSafety eventBAPairSafety2 : array2) {
                String compressedState = eventBAPairSafety2.GetCompressedState();
                if (!stringHashTable.containsKey(compressedState)) {
                    stringHashTable.add(compressedState);
                    queue.add(eventBAPairSafety2);
                    List<ConfigurationBase> temp = new ArrayList<>(list);
                    temp.add(eventBAPairSafety2.configuration);
                    queue2.add(temp);
                }
            }
            if (queue.size() <= 0) {
                this.verificationOutput.noOfStates = (long) stringHashTable.count;
                this.verificationOutput.verificationResult = VerificationResultType.VALID;
                return;
            }
        }
        this.verificationOutput.noOfStates = (long) stringHashTable.count;
    }

    public void DFSVerification() throws Exception {
        Stack<Integer> stack = new Stack<Integer>();
        stack.push(0);
        List<Integer> list = new ArrayList<>(1024);
        Stack<EventBAPairSafety> stack2 = new Stack<EventBAPairSafety>();
        EventBAPairSafety initialPairs = EventBAPairSafety.GetInitialPairs(this.BA, this.initialStep);
        stack2.push(initialPairs);
        StringHashTable stringHashTable = new StringHashTable(Ultility.MC_INITIAL_SIZE);
        while (stack2.size() != 0) {
            if (this.cancelRequested()) {
                this.verificationOutput.noOfStates = (long) stringHashTable.count;
                return;
            }
            EventBAPairSafety eventBAPairSafety = stack2.pop();
            String compressedState = eventBAPairSafety.GetCompressedState();
            int num = stack.pop();
            if (num > 0) {
                while (list.get(list.size() - 1) >= num) {
                    int index = list.size() - 1;
                    list.remove(index);
                    this.verificationOutput.counterExampleTrace.remove(index);
                }
            }
            this.verificationOutput.counterExampleTrace.add(eventBAPairSafety.configuration);
            list.add(num);
            if (eventBAPairSafety.States.size() == 0) {
                this.verificationOutput.noOfStates = (long) stringHashTable.count;
                this.verificationOutput.verificationResult = VerificationResultType.INVALID;
                return;
            }
            if (!stringHashTable.containsKey(compressedState)) {
                stringHashTable.add(compressedState);
                ConfigurationBase[] array = eventBAPairSafety.configuration.makeOneMove().toArray(new ConfigurationBase[0]);
                this.verificationOutput.transitions += (long) array.length;
                EventBAPairSafety[] array2 = eventBAPairSafety.Next(this.BA, array);
                for (EventBAPairSafety item : array2) {
                    stack2.push(item);
                    stack.push(num + 1);
                }
            }
        }
        this.verificationOutput.counterExampleTrace = null;
        this.verificationOutput.noOfStates = (long) stringHashTable.count;
        this.verificationOutput.verificationResult = VerificationResultType.VALID;
    }

    public void TarjanModelChecking() throws Exception {
        this.verificationOutput.counterExampleTrace = null;
        Map<String, List<String>> dictionary = new HashMap<>(Ultility.MC_INITIAL_SIZE);
        Stack<Map.Entry<ConfigurationBase, String>> stack = new Stack<>();
        StringDictionary<int[]> stringDictionary = new StringDictionary<int[]>(Ultility.MC_INITIAL_SIZE);
        List<Map.Entry<ConfigurationBase, String>> list = new ArrayList<>();
        HashSet<String> hashSet = new HashSet<>();
        for (String state : this.BA.InitialStates) {
            List<String> list2 = this.BA.MakeOneMove(state, this.initialStep);
            for (String text : list2) {
                if (hashSet.add(text)) {
                    list.add(new AbstractMap.SimpleEntry<>(this.initialStep, text));
                }
            }
        }
        if (list.size() == 0) {
            this.verificationOutput.verificationResult = VerificationResultType.VALID;
            return;
        }
        for (int i = 0; i < list.size(); i++) {
            Map.Entry<ConfigurationBase, String> item = list.get(i);
            stack.push(item);
            String text2 = item.getKey().getIDWithEvent() + "$" + item.getValue();
            StringDictionary<int[]> stringDictionary2 = stringDictionary;
            String key = text2;
            int[] array = new int[2];
            array[0] = -1;
            stringDictionary2.add(key, array);
            dictionary.put(text2, new ArrayList<>(8));
        }
        List<String> list3 = new ArrayList<>(1024);
        Stack<Map.Entry<ConfigurationBase, String>> stack2 = new Stack<>();
        int num = 0;
        Map<String, List<Map.Entry<ConfigurationBase, String>>> dictionary2 = new HashMap<>(1024);
        while (!this.cancelRequested()) {
            Map.Entry<ConfigurationBase, String> item2 = stack.peek();
            ConfigurationBase key2 = item2.getKey();
            String text3 = item2.getKey().getIDWithEvent() + "$" + item2.getValue();
            List<String> list4 = dictionary.get(text3);
            int[] containsKey = stringDictionary.getContainsKey(text3);
            if (containsKey[0] == -1) {
                containsKey[0] = num;
                num++;
            }
            boolean flag = true;
            if (dictionary2.containsKey(text3)) {
                List<Map.Entry<ConfigurationBase, String>> list5 = dictionary2.get(text3);
                if (list5.size() > 0) {
                    for (int j = list5.size() - 1; j >= 0; j--) {
                        Map.Entry<ConfigurationBase, String> item3 = list5.get(j);
                        String key3 = item3.getKey().getIDWithEvent() + "$" + item3.getValue();
                        if (stringDictionary.getContainsKey(key3)[0] == -1) {
                            if (flag) {
                                stack.push(item3);
                                flag = false;
                                list5.remove(j);
                            }
                        } else {
                            list5.remove(j);
                        }
                    }
                }
            } else {
                List<ConfigurationBase> enumerable = key2.makeOneMove();
                List<Map.Entry<ConfigurationBase, String>> list6 = new ArrayList<>();
                for (ConfigurationBase configurationBase : enumerable) {
                    List<String> list7 = this.BA.MakeOneMove(item2.getValue(), configurationBase);
                    for (int k = 0; k < list7.size(); k++) {
                        list6.add(new AbstractMap.SimpleEntry<>(configurationBase, list7.get(k)));
                    }
                }
                this.verificationOutput.transitions += (long) list6.size();
                for (int l = list6.size() - 1; l >= 0; l--) {
                    Map.Entry<ConfigurationBase, String> keyValuePair = list6.get(l);
                    String text4 = keyValuePair.getKey().getIDWithEvent() + "$" + keyValuePair.getValue();
                    int[] containsKey2 = stringDictionary.getContainsKey(text4);
                    if (containsKey2 != null) {
                        list4.add(text4);
                        if (containsKey2[0] == -1) {
                            if (flag) {
                                stack.push(keyValuePair);
                                flag = false;
                                list6.remove(l);
                            } else {
                                list6.set(l, keyValuePair);
                            }
                        } else {
                            list6.remove(l);
                        }
                    } else {
                        StringDictionary<int[]> stringDictionary3 = stringDictionary;
                        String key4 = text4;
                        int[] array2 = new int[2];
                        array2[0] = -1;
                        stringDictionary3.add(key4, array2);
                        dictionary.put(text4, new ArrayList<>(8));
                        list4.add(text4);
                        if (flag) {
                            stack.push(keyValuePair);
                            flag = false;
                            list6.remove(l);
                        } else {
                            list6.set(l, keyValuePair);
                        }
                    }
                }
                dictionary2.put(text3, list6);
            }
            if (flag) {
                int num2 = containsKey[0];
                int num3 = num2;
                boolean flag2 = false;
                for (int m = 0; m < list4.size(); m++) {
                    String text5 = list4.get(m);
                    if (text5 == text3) {
                        flag2 = true;
                    }
                    int[] containsKey3 = stringDictionary.getContainsKey(text5);
                    if (containsKey3[0] != -2) {
                        if (containsKey3[0] > num3) {
                            num2 = Math.min(num2, containsKey3[1]);
                        } else {
                            num2 = Math.min(num2, containsKey3[0]);
                        }
                    }
                }
                containsKey[1] = num2;
                stack.pop();
                if (num2 == num3) {
                    list3.add(text3);
                    containsKey[0] = -2;
                    boolean flag3 = item2.getValue().endsWith("-");
                    if (stack2.size() > 0) {
                        Map.Entry<ConfigurationBase, String> keyValuePair2 = stack2.peek();
                        String text6 = keyValuePair2.getKey().getIDWithEvent() + "$" + keyValuePair2.getValue();
                        while (stack2.size() > 0 && stringDictionary.getContainsKey(text6)[0] > num3) {
                            stack2.pop();
                            list3.add(text6);
                            stringDictionary.getContainsKey(text6)[0] = -2;
                            if (!flag3 && keyValuePair2.getValue().endsWith("-")) {
                                flag3 = true;
                            }
                            if (stack2.size() > 0) {
                                keyValuePair2 = stack2.peek();
                                text6 = keyValuePair2.getKey().getIDWithEvent() + "$" + keyValuePair2.getValue();
                            }
                        }
                    }
                    if (flag3 && (key2.isDeadLock || list3.size() > 1 || flag2)) {
                        this.verificationOutput.verificationResult = VerificationResultType.INVALID;
                        this.verificationOutput.noOfStates = (long) stringDictionary.count;
                        while (stack.size() > 0 && stack.peek().getKey().event != "init") {
                            stack.pop();
                        }
                        String startID = null;
                        if (stack.size() > 0) {
                            startID = stack.peek().getKey().getIDWithEvent() + "$" + stack.peek().getValue();
                        }
                        this.verificationOutput.counterExampleTrace = this.GetConcreteTrace(this.initialStep, this.GetCounterExample(list3, startID, dictionary));
                        return;
                    }
                    for (String key5 : list3) {
                        dictionary2.remove(key5);
                    }
                    list3.clear();
                } else {
                    stack2.push(item2);
                }
            }
            if (stack.size() <= 0) {
                this.verificationOutput.verificationResult = VerificationResultType.VALID;
                this.verificationOutput.noOfStates = (long) stringDictionary.count;
                return;
            }
        }
        this.verificationOutput.noOfStates = (long) stringDictionary.count;
    }

    protected Map<String, LocalPair> TarjanModelChecking2(Map<String, LocalPair> SCC, Map<String, List<String>> OutgoingTransitionTable) throws Exception {
        Map<String, LocalPair> dictionary = new HashMap<>(64);
        Map<String, Integer> dictionary2 = new HashMap<>(64);
        Map<String, Integer> dictionary3 = new HashMap<>(64);
        Stack<String> stack = new Stack<>();
        StringHashTable stringHashTable = new StringHashTable(64);
        int num = 0;
        Stack<String> stack2 = new Stack<>();
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
        /*Block_21:
        return null;*/
    }


    protected List<ConfigurationBase> GetConcreteTrace(ConfigurationBase init, List<String> trace) throws ParsingException, CloneNotSupportedException {
        List<ConfigurationBase> list = new ArrayList<>(64);
        ConfigurationBase configurationBase = init;
        list.add(configurationBase);
        for (int i = 1; i < trace.size(); i++) {
            String configID = this.GetConfigID(trace.get(i));
            List<ConfigurationBase> enumerable = configurationBase.makeOneMove();
            for (ConfigurationBase configurationBase2 : enumerable) {
                if (configurationBase2.getIDWithEvent() == configID) {
                    list.add(configurationBase2);
                    configurationBase = configurationBase2;
                    break;
                }
            }
        }
        return list;
    }

    protected String GetConfigID(String ID) {
        int length = ID.lastIndexOf("$");
        return ID.substring(0, length);
    }


    protected List<String> GetCounterExample(List<String> FairSCC, String startID, Map<String, List<String>> OutgoingTransitionTable) {
        List<String> result;
        try {
            List<String> list;
            if (startID != null) {
                list = this.Path(startID, FairSCC, OutgoingTransitionTable);
            } else {
                list = new ArrayList<>();
            }
            String text = FairSCC.get(0);
            if (FairSCC.size() > 1 || OutgoingTransitionTable.get(text).contains(text)) {
                this.verificationOutput.loopIndex = list.size();
                String text2 = this.FindShortestPathToAFairState(text, null, list, FairSCC, OutgoingTransitionTable);
                this.FindShortestPathToAFairState(text2, text2, list, FairSCC, OutgoingTransitionTable);
            }
            result = list;
        } catch (CancelRunningException exception) {
            result = new ArrayList<>();
        }
        return result;
    }

    protected List<String> Path(String startID, List<String> FairSCC, Map<String, List<String>> OutgoingTransitionTable) throws CancelRunningException {
        Hashtable hashtable = new Hashtable(1000);
        Queue<String> queue = new ArrayDeque<>();
        Queue<List<String>> queue2 = new ArrayDeque<>();
        queue.add(startID);
        queue2.add(new ArrayList<>());
        hashtable.put(startID, null);
        while (!this.cancelRequested()) {
            String text = queue.poll();
            List<String> collection = queue2.poll();
            List<String> list = new ArrayList<>(collection);
            list.add(text);
            if (FairSCC.contains(text)) {
                FairSCC.remove(text);
                FairSCC.add(0, text);
                return list;
            }
            if (OutgoingTransitionTable.containsKey(text)) {
                List<String> list2 = OutgoingTransitionTable.get(text);
                for (int i = 0; i < list2.size(); i++) {
                    String text2 = list2.get(i);
                    if (!hashtable.containsKey(text2)) {
                        hashtable.put(text2, null);
                        queue.add(text2);
                        queue2.add(list);
                    }
                }
            }
            if (queue.size() <= 0) {
                return new ArrayList<>(0);
            }
        }
        throw new CancelRunningException();
    }

    protected String FindShortestPathToAFairState(String start, String end, List<String> trace, List<String> FairSCC, Map<String, List<String>> OutgoingTransitionTable) throws CancelRunningException {
        int capacity = 8;
        Hashtable hashtable = new Hashtable(capacity);
        Queue<String> queue = new ArrayDeque<>();
        Queue<List<String>> queue2 = new ArrayDeque<>();
        queue.add(start);
        queue2.add(new ArrayList<>());
        while (!this.cancelRequested()) {
            String text = queue.poll();
            List<String> list = queue2.poll();
            List<String> list2 = new ArrayList<>(list);
            list2.add(text);
            if ((end == null && text.endsWith("-")) || (list.size() > 0 && end == text)) {
                list2.remove(0);
                trace.addAll(list2);
                return text;
            }
            List<String> list3 = OutgoingTransitionTable.get(text);
            for (int i = 0; i < list3.size(); i++) {
                String text2 = list3.get(i);
                if (FairSCC.contains(text2) && !hashtable.containsKey(text2)) {
                    hashtable.put(text2, null);
                    queue.add(text2);
                    queue2.add(list2);
                }
            }
            if (queue.size() <= 0) {
                return null;
            }
        }
        throw new CancelRunningException();
    }

    public void ModelCheckingLivenessWithFairness() throws Exception {
        Map<String, List<String>> dictionary = new HashMap<>(Ultility.MC_INITIAL_SIZE);
        this.LocalTaskStack = new Stack<>();
        this.verificationOutput.counterExampleTrace = null;
        List<LocalPair> initialPairsLocal = LocalPair.GetInitialPairsLocal(this.BA, this.initialStep);
        StringDictionary<int[]> stringDictionary = new StringDictionary<>();
        if (initialPairsLocal.size() == 0) {
            this.verificationOutput.verificationResult = VerificationResultType.VALID;
            return;
        }
        for (int i = 0; i < initialPairsLocal.size(); i++) {
            LocalPair localPair = initialPairsLocal.get(i);
            this.LocalTaskStack.push(localPair);
            String compressedState = localPair.GetCompressedState(this.FairnessType);
            StringDictionary<int[]> stringDictionary2 = stringDictionary;
            String key = compressedState;
            int[] array = new int[2];
            array[0] = -1;
            stringDictionary2.add(key, array);
            dictionary.put(compressedState, new ArrayList<>(8));
        }
        Map<String, LocalPair> dictionary2 = new HashMap<>(1024);
        Stack<LocalPair> stack = new Stack<>();
        int num = 0;
        Map<String, List<LocalPair>> dictionary3 = new HashMap<>(1024);
        while (!this.cancelRequested()) {
            LocalPair localPair2 = this.LocalTaskStack.peek();
            ConfigurationBase configuration = localPair2.configuration;
            String state = localPair2.state;
            String compressedState2 = localPair2.GetCompressedState(this.FairnessType);
            List<String> list = dictionary.get(compressedState2);
            int[] containsKey = stringDictionary.getContainsKey(compressedState2);
            if (containsKey[0] == -1) {
                containsKey[0] = num;
                num++;
            }
            boolean flag = true;
            if (dictionary3.containsKey(compressedState2)) {
                List<LocalPair> list2 = dictionary3.get(compressedState2);
                if (list2.size() > 0) {
                    for (int j = list2.size() - 1; j >= 0; j--) {
                        LocalPair localPair3 = list2.get(j);
                        String compressedState3 = localPair3.GetCompressedState(this.FairnessType);
                        if (stringDictionary.getContainsKey(compressedState3)[0] == -1) {
                            if (flag) {
                                this.LocalTaskStack.push(localPair3);
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
                this.verificationOutput.transitions += (long) list3.size();
                for (int k = list3.size() - 1; k >= 0; k--) {
                    LocalPair localPair4 = list3.get(k);
                    String compressedState4 = localPair4.GetCompressedState(this.FairnessType);
                    int[] containsKey2 = stringDictionary.getContainsKey(compressedState4);
                    if (containsKey2 != null) {
                        list.add(compressedState4);
                        if (containsKey2[0] == -1) {
                            if (flag) {
                                this.LocalTaskStack.push(localPair4);
                                flag = false;
                                list3.remove(k);
                            } else {
                                list3.set(k, localPair4);
                            }
                        } else {
                            list3.remove(k);
                        }
                    } else {
                        StringDictionary<int[]> stringDictionary3 = stringDictionary;
                        String key2 = compressedState4;
                        int[] array2 = new int[2];
                        array2[0] = -1;
                        stringDictionary3.add(key2, array2);
                        dictionary.put(compressedState4, new ArrayList<>(8));
                        list.add(compressedState4);
                        if (flag) {
                            this.LocalTaskStack.push(localPair4);
                            flag = false;
                            list3.remove(k);
                        } else {
                            list3.set(k, localPair4);
                        }
                    }
                }
                dictionary3.put(compressedState2, list3);
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
                    int[] containsKey3 = stringDictionary.getContainsKey(text);
                    if (containsKey3[0] != -2) {
                        if (containsKey3[0] > num3) {
                            num2 = Math.min(num2, containsKey3[1]);
                        } else {
                            num2 = Math.min(num2, containsKey3[0]);
                        }
                    }
                }
                containsKey[1] = num2;
                this.LocalTaskStack.pop();
                if (num2 == num3) {
                    dictionary2.put(compressedState2, localPair2);
                    containsKey[0] = -2;
                    boolean flag3 = localPair2.state.endsWith("-");
                    while (stack.size() > 0 && stringDictionary.getContainsKey(stack.peek().GetCompressedState(this.FairnessType))[0] > num3) {
                        LocalPair localPair5 = stack.pop();
                        String compressedState5 = localPair5.GetCompressedState(this.FairnessType);
                        stringDictionary.getContainsKey(compressedState5)[0] = -2;
                        dictionary2.put(compressedState5, localPair5);
                        if (!flag3 && localPair5.state.endsWith("-")) {
                            flag3 = true;
                        }
                    }
                    int count = dictionary2.size();
                    if (flag3 && (configuration.isDeadLock || count > 1 || flag2)) {
                        this.printMessage("A SCC of size " + count + " is found");
                        Map<String, LocalPair> dictionary4 = this.IsFair(dictionary2, dictionary);
                        if (dictionary4 != null) {
                            this.printMessage("The SCC found is FAIR.");
                            this.verificationOutput.verificationResult = VerificationResultType.INVALID;
                            this.verificationOutput.noOfStates = (long) stringDictionary.count;
                            this.LocalGetCounterExample(dictionary4, dictionary);
                            return;
                        }
                        this.printMessage("The SCC found is NOT fair.");
                    }
                    for (String key3 : dictionary2.keySet()) {
                        dictionary3.remove(key3);
                        dictionary.remove(key3);
                    }
                    dictionary2.clear();
                } else {
                    stack.push(localPair2);
                }
            }
            if (this.LocalTaskStack.size() <= 0) {
                this.verificationOutput.verificationResult = VerificationResultType.VALID;
                this.verificationOutput.noOfStates = (long) stringDictionary.count;
                return;
            }
        }
        this.verificationOutput.noOfStates = (long) stringDictionary.count;
    }

    protected void LocalGetCounterExample(Map<String, LocalPair> FairSCC, Map<String, List<String>> OutgoingTransitionTable) throws Exception {
        try {
            if (this.verificationOutput.generateCounterExample) {
                if (this.LocalTaskStack.size() == 0) {
                    this.verificationOutput.counterExampleTrace = this.GetConcreteTrace(this.initialStep, new ArrayList<>());
                } else {
                    while (this.LocalTaskStack.size() > 0 && this.LocalTaskStack.peek().configuration.event != "init") {
                        this.LocalTaskStack.pop();
                    }
                    String compressedState = this.LocalTaskStack.peek().GetCompressedState(this.FairnessType);
                    List<String> list = new ArrayList<>(FairSCC.keySet());
                    List<String> list2 = this.Path(compressedState, list, OutgoingTransitionTable);
                    String text = list.get(0);
                    if (FairSCC.size() > 1 || OutgoingTransitionTable.get(text).contains(text)) {
                        this.verificationOutput.loopIndex = list2.size();
                        Map<String, Boolean> dictionary = new HashMap<>(FairSCC.size());
                        Stack<String> stack = new Stack<>();
                        stack.push(text);
                        do {
                            String text2 = stack.pop();
                            if (!dictionary.containsKey(text2)) {
                                dictionary.put(text2, false);
                                list2.add(text2);
                                List<String> list3 = OutgoingTransitionTable.get(text2);
                                boolean flag = false;
                                for (int i = 0; i < list3.size(); i++) {
                                    String text3 = list3.get(i);
                                    if (FairSCC.containsKey(text3) && !dictionary.containsKey(text3)) {
                                        flag = true;
                                        stack.push(text3);
                                    }
                                }
                                if (!flag) {
                                    while (stack.size() > 0 && dictionary.containsKey(stack.peek())) {
                                        stack.pop();
                                    }
                                    if (stack.size() > 0) {
                                        list2.addAll(this.FindShortestPath(text2, stack.peek(), dictionary, stack, FairSCC, OutgoingTransitionTable));
                                    } else if (text2 != text) {
                                        list2.addAll(this.FindShortestPath(text2, text, dictionary, null, FairSCC, OutgoingTransitionTable));
                                    }
                                }
                            }
                        }
                        while (stack.size() > 0);
                        list2.add(text);
                    }
                    this.verificationOutput.counterExampleTrace = this.MyGetConcreteTraceWithFairness(list2);
                }
            }
        } catch (CancelRunningException | ParsingException | CloneNotSupportedException e) {
            throw new Exception("LocalGetCounterExample: " + e.getMessage());
        }
    }

    private List<String> FindShortestPath(String start, String end, Map<String, Boolean> localVisited,
                                          Stack<String> outStack, Map<String, LocalPair> FairSCC,
                                          Map<String, List<String>> OutgoingTransitionTable) throws CancelRunningException {
        int count = localVisited.size();
        Hashtable hashtable = new Hashtable(count);
        Queue<String> queue = new ArrayDeque<>();
        Queue<List<String>> queue2 = new ArrayDeque<>();
        queue.add(start);
        queue2.add(new ArrayList<>());
        hashtable.put(start, null);
        while (!this.cancelRequested()) {
            String text = queue.poll();
            List<String> collection = queue2.poll();
            List<String> list = new ArrayList<>(collection);
            list.add(text);
            List<String> list2 = OutgoingTransitionTable.get(text);
            for (int i = 0; i < list2.size(); i++) {
                String text2 = list2.get(i);
                if (text2 == end) {
                    list.remove(0);
                    return list;
                }
                if (localVisited.containsKey(text2) && FairSCC.containsKey(text2) && !hashtable.containsKey(text2)) {
                    hashtable.put(text2, null);
                    queue.add(text2);
                    queue2.add(list);
                }
            }
            if (queue.size() <= 0) {
                list2 = OutgoingTransitionTable.get(text);
                for (int j = 0; j < list2.size(); j++) {
                    String text3 = list2.get(j);
                    if (FairSCC.containsKey(text3) && !localVisited.containsKey(text3)) {
                        outStack.push(text3);
                    }
                }
                return list;
            }
        }
        throw new CancelRunningException();
    }

    private List<ConfigurationBase> MyGetConcreteTraceWithFairness(List<String> trace) throws ParsingException, CloneNotSupportedException {
        List<ConfigurationBase> list = new ArrayList<>();
        ConfigurationBase configurationBase = this.initialStep;
        list.add(configurationBase);
        for (int i = 1; i < trace.size(); i++) {
            String configID = this.GetConfigID(trace.get(i));
            List<ConfigurationBase> enumerable = configurationBase.makeOneMove();
            for (ConfigurationBase configurationBase2 : enumerable) {
                String text = "";
                if ((this.FairnessType == FairnessType.PROCESS_LEVEL_STRONG_FAIRNESS || this.FairnessType == FairnessType.PROCESS_LEVEL_WEAK_FAIRNESS) && configurationBase2.participatingProcesses != null) {
                    for (String str : configurationBase2.participatingProcesses) {
                        text = text + "$" + str;
                    }
                }
                if (configurationBase2.getIDWithEvent() + text == configID) {
                    list.add(configurationBase2);
                    configurationBase = configurationBase2;
                    break;
                }
            }
        }
        return list;
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

    @Override
    public String getResultString() {
        if (this.IsSafety) {
            return this.GetResultStringSafety();
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("********Verification Result********" + "\n");
        if (this.verificationOutput.verificationResult == VerificationResultType.VALID) {
            stringBuilder.append("The Assertion (" + this.toString() + ") is VALID." + "\n");
        } else if (this.verificationOutput.verificationResult == VerificationResultType.UNKNOWN) {
            stringBuilder.append("The Assertion (" + this.toString() + ") is NEITHER PROVED NOR DISPROVED." + "\n");
        } else {
            stringBuilder.append("The Assertion (" + this.toString() + ") is NOT valid." + "\n");
            if (this.verificationOutput.generateCounterExample) {
                stringBuilder.append("A counterexample is presented as follows." + "\n");
                stringBuilder.append("<");
                boolean flag = false;
                for (int i = 0; i < this.verificationOutput.counterExampleTrace.size(); i++) {
                    ConfigurationBase configurationBase = this.verificationOutput.counterExampleTrace.get(i);
                    if (configurationBase.event == "init") {
                        stringBuilder.append(configurationBase.event);
                    } else {
                        stringBuilder.append(" -> ");
                        if (this.verificationOutput.loopIndex >= 0 && i == this.verificationOutput.loopIndex) {
                            stringBuilder.append("(");
                        }
                        stringBuilder.append(configurationBase.getDisplayEvent());
                        if (configurationBase.event != "τ" && i >= this.verificationOutput.loopIndex) {
                            flag = true;
                        }
                    }
                }
                if (!stringBuilder.toString().contains("(") && !flag) {
                    stringBuilder.append(" -> (τ -> ");
                }
                if (this.verificationOutput.loopIndex >= 0) {
                    stringBuilder.append(")*");
                }
                stringBuilder.append(">" + "\n");
            }
        }
        stringBuilder.append("\n");
        stringBuilder.append("********Verification Setting********" + "\n");
        stringBuilder.append("Admissible Behavior: " + this.selectedBahaviorName + "\n");
        if (this.IsNegateLiveness) {
            stringBuilder.append("Search Engine: " + this.selectedEngineName + "\n");
        } else {
            stringBuilder.append("Search Engine: Loop Existence Checking - The negation of the LTL formula is a safety property!" + "\n");
            stringBuilder.append("System Abstraction: " + this.mustAbstract + "\n");
        }
        stringBuilder.append("\n");
        return stringBuilder.toString();
    }

    public String GetResultStringSafety() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("********Verification Result********" + "\n");
        if (this.verificationOutput.verificationResult == VerificationResultType.VALID) {
            stringBuilder.append("The Assertion (" + this.toString() + ") is VALID." + "\n");
        } else if (this.verificationOutput.verificationResult == VerificationResultType.UNKNOWN) {
            stringBuilder.append("The Assertion (" + this.toString() + ") is NEITHER PROVED NOR DISPROVED." + "\n");
        } else {
            stringBuilder.append("The Assertion (" + this.toString() + ") is NOT valid." + "\n");
            stringBuilder.append("A counterexample is presented as follows." + "\n");
            this.verificationOutput.getCounterxampleString(stringBuilder);
        }
        stringBuilder.append("\n");
        stringBuilder.append("********Verification Setting********" + "\n");
        stringBuilder.append("Admissible Behavior: " + this.selectedBahaviorName + "\n");
        if (this.selectedEngineName == "First Witness Trace using Depth First Search") {
            stringBuilder.append("Method: Refinement Based Safety Analysis using DFS - The LTL formula is a safety property!" + "\n");
        } else {
            stringBuilder.append("Method: Refinement Based Safety Analysis using BFS - The LTL formula is a safety property!" + "\n");
        }
        stringBuilder.append("System Abstraction: " + this.mustAbstract + "\n");
        stringBuilder.append("\n");
        return stringBuilder.toString();
    }
}
