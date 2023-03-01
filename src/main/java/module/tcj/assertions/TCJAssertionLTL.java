package module.tcj.assertions;

import common.CancelRunningException;
import common.ParsingException;
import common.classes.ba.BuchiAutomata;
import common.classes.datastructure.DBM;
import common.classes.expressions.Valuation;
import common.classes.expressions.expressionclass.Expression;
import common.classes.moduleinterface.*;
import common.classes.semanticmodels.lts.assertion.AssertionLTL;
import common.classes.ultility.Ultility;
import module.tcj.lts.*;

import java.util.*;

public class TCJAssertionLTL extends AssertionLTL {
    private DefinitionRef Process;
    protected boolean ApplyZenoCheck;
    private Valuation GlobalEnv;


    public TCJAssertionLTL(DefinitionRef processDef, String ltl) {
        super(ltl);
        Process = processDef;
    }

    @Override
    public void initialize(SpecificationBase spec) throws ParsingException, NoSuchMethodException {
        Specification specification = (Specification) spec;
        List<String> globalVariables = this.Process.GetGlobalVariables();
        this.BA.Initialize(specification.declarationDatabase, specification.SpecValuation);
        this.negationLTLBuchi.Initialize(specification.declarationDatabase, specification.SpecValuation);
        for (Map.Entry<String, Expression> keyValuePair : this.BA.DeclarationDatabase.entrySet()) {
            globalVariables.addAll(keyValuePair.getValue().getVars());
        }
        this.GlobalEnv = specification.SpecValuation.getVariableChannelClone(globalVariables, specification.GetChannelNames(this.Process.GetChannels()));
        this.mustAbstract = this.Process.MustBeAbstracted();
        this.modelCheckingOptions = new ModelCheckingOptions();
        List<String> list = new ArrayList<>();
        if (this.IsSafety) {
            list.add("First Witness Trace with Zone Abstraction");
            list.add("First Witness Trace with Digitization");
            list.add("Shortest Witness Trace with Zone Abstraction");
            list.add("Shortest Witness Trace with Digitization");
            this.modelCheckingOptions.addAddimissibleBehavior("All", list);
            return;
        }
        list.add("Strongly Connected Component Based Search with Zone Abstraction");
        list.add("Strongly Connected Component Based Search with Digitization");
        this.modelCheckingOptions.addAddimissibleBehavior("All", list);
        list = new ArrayList<>();
        list.add("Strongly Connected Component Based Search with Zone Abstraction");
        list.add("Strongly Connected Component Based Search with Digitization");
        this.modelCheckingOptions.addAddimissibleBehavior("Non-Zeno Only", list);
        list = new ArrayList<>();
        list.add("Strongly Connected Component Based Search with Zone Abstraction");
        list.add("Strongly Connected Component Based Search with Digitization");
        this.modelCheckingOptions.addAddimissibleBehavior("Event-level Weak Fair Only", list);
        list = new ArrayList<>();
        list.add("Strongly Connected Component Based Search with Zone Abstraction");
        list.add("Strongly Connected Component Based Search with Digitization");
        this.modelCheckingOptions.addAddimissibleBehavior("Event-level Strong Fair Only", list);
        if (this.IsProcessLevelFairnessApplicable()) {
            list = new ArrayList<>();
            list.add("Strongly Connected Component Based Search with Zone Abstraction");
            list.add("Strongly Connected Component Based Search with Digitization");
            this.modelCheckingOptions.addAddimissibleBehavior("Process-level Weak Fair Only", list);
            list = new ArrayList<>();
            list.add("Strongly Connected Component Based Search with Zone Abstraction");
            list.add("Strongly Connected Component Based Search with Digitization");
            this.modelCheckingOptions.addAddimissibleBehavior("Process-level Strong Fair Only", list);
        }
        list = new ArrayList<>();
        list.add("Strongly Connected Component Based Search with Zone Abstraction");
        list.add("Strongly Connected Component Based Search with Digitization");
        this.modelCheckingOptions.addAddimissibleBehavior("Global Fair Only", list);
    }

    public static void GetCounterExampleWithTocks(TickSimulationConfiguration init, VerificationOutput output) throws ParsingException, CloneNotSupportedException {
        if (output.counterExampleTrace == null || output.counterExampleTrace.size() == 0) {
            return;
        }
        Queue<TickSimulationConfiguration> queue = new ArrayDeque<>(1024);
        Queue<Integer> queue2 = new ArrayDeque<>(1024);
        Queue<List<ConfigurationBase>> queue3 = new ArrayDeque<>(1024);
        Map<String, Boolean> dictionary = new HashMap<>(1024);
        queue.add(init);
        queue2.add(0);
        dictionary.put(init.getID() + "$0", false);
        queue3.add(Arrays.asList(init));
        List<ConfigurationBase> list;
        int num2;
        List<ConfigurationBase> list2;
        for (; ; ) {
            TickSimulationConfiguration tickSimulationConfiguration = queue.poll();
            list = queue3.poll();
            int num = queue2.poll();
            if (num == output.counterExampleTrace.size() - 1) {
                if (output.loopIndex == -1) {
                    //goto IL_1C2;
                    output.counterExampleTrace = list;
                    return;
                }
                num2 = 0;
                for (int i = 0; i <= output.loopIndex; i++) {
                    ConfigurationBase configurationBase = list.get(i + num2);
                    while (configurationBase.displayName != null && configurationBase.displayName == "tock") {
                        num2++;
                        configurationBase = list.get(num2 + i);
                    }
                }
                TickSimulationConfiguration tickSimulationConfiguration2 = (TickSimulationConfiguration) list.get(num2 + output.loopIndex);
                list2 = new ArrayList<>(list);
                boolean flag = true;
                TickSimulationConfiguration tickSimulationConfiguration3 = tickSimulationConfiguration;
                while (flag) {
                    flag = false;
                    ConfigurationBase[] array = tickSimulationConfiguration.makeOneMove().toArray(new ConfigurationBase[0]);
                    TickSimulationConfiguration tickSimulationConfiguration4 = null;
                    for (int j = array.length - 1; j >= 0; j--) {
                        TickSimulationConfiguration tickSimulationConfiguration5 = (TickSimulationConfiguration) array[j];
                        if (!tickSimulationConfiguration5.IsTock()) {
                            if (tickSimulationConfiguration2.getIDWithEvent() == tickSimulationConfiguration5.getIDWithEvent()) {
                                //goto Block_7;
                                output.counterExampleTrace = list2;
                                output.loopIndex = num2 + output.loopIndex;
                                return;
                            }
                        } else {
                            flag = true;
                            tickSimulationConfiguration4 = tickSimulationConfiguration5;
                        }
                    }
                    list2.add(tickSimulationConfiguration4);
                    tickSimulationConfiguration3 = tickSimulationConfiguration4;
                }
            } else {
                ConfigurationBase configurationBase2 = output.counterExampleTrace.get(num + 1);
                ConfigurationBase[] array2 = tickSimulationConfiguration.makeOneMove().toArray(new ConfigurationBase[0]);
                for (int k = array2.length - 1; k >= 0; k--) {
                    TickSimulationConfiguration tickSimulationConfiguration6 = (TickSimulationConfiguration) array2[k];
                    if (!tickSimulationConfiguration6.IsTock()) {
                        if (configurationBase2.event == tickSimulationConfiguration6.event && configurationBase2.globalEnv.getID() == tickSimulationConfiguration6.globalEnv.getID()) {
                            String key = tickSimulationConfiguration6.getID() + "$" + String.valueOf(num + 1);
                            if (!dictionary.containsKey(key)) {
                                queue.add(tickSimulationConfiguration6);
                                queue2.add(num + 1);
                                dictionary.put(key, false);
                                List<ConfigurationBase> temp = new ArrayList<>(list);
                                temp.add(tickSimulationConfiguration6);
                                queue3.add(temp);
                            }
                        }
                    } else {
                        String key2 = tickSimulationConfiguration6.getID() + "$" + num;
                        if (!dictionary.containsKey(key2)) {
                            queue.add(tickSimulationConfiguration6);
                            queue2.add(num);
                            dictionary.put(key2, false);
                            List<ConfigurationBase> temp = new ArrayList<>(list);
                            temp.add(tickSimulationConfiguration6);
                            queue3.add(temp);
                        }
                    }
                }
            }
            if (queue.size() <= 0) {
                return;
            }
        }
        /*Block_7:
        output.counterExampleTrace = list2;
        output.loopIndex = num2 + output.loopIndex;
        return;
        IL_1C2:
        output.counterExampleTrace = list;*/
    }

    @Override
    public String startingProcess() {
        return this.Process.toString();
    }

    @Override
    public boolean IsProcessLevelFairnessApplicable() {
        TCJProcess topLevelConcurrency = this.Process.GetTopLevelConcurrency(new ArrayList<>());
        if (this.mustAbstract) {
            if (topLevelConcurrency instanceof IndexInterleaveAbstract) {
                IndexInterleaveAbstract indexInterleaveAbstract = (IndexInterleaveAbstract) topLevelConcurrency;
                for (TCJProcess TCJProcess : indexInterleaveAbstract.Processes) {
                    if (TCJProcess.MustBeAbstracted()) {
                        return false;
                    }
                }
                return true;
            }
        } else if (topLevelConcurrency instanceof IndexInterleave || topLevelConcurrency instanceof IndexParallel || topLevelConcurrency instanceof IndexInterleaveAbstract) {
            return true;
        }
        return false;
    }

    @Override
    public void runVerification() throws Exception {
        String selectedBahaviorName;
        if ((selectedBahaviorName = this.selectedBahaviorName) != null) {
            if (!(selectedBahaviorName == "All")) {
                if (selectedBahaviorName == "Non-Zeno Only") {
                    String selectedEngineName;
                    if ((selectedEngineName = this.selectedEngineName) == null) {
                        //goto IL_2FC;
                        if (this.selectedBahaviorName != "Non-Zeno Only" || this.selectedEngineName != "Strongly Connected Component Based Search with Digitization") {
                            TCJAssertionLTL.GetCounterExampleWithTocks(new TickSimulationConfiguration(this.Process, "init", null, this.GlobalEnv, false, false), this.verificationOutput);
                        }
                        return;
                    }
                    if (selectedEngineName == "Strongly Connected Component Based Search with Zone Abstraction") {
                        this.initialStep = new ZoneConfiguration(this.Process, "init", null, this.GlobalEnv, false, new DBM());
                        ((ZoneConfiguration) this.initialStep).SetTimer();
                        this.TarjanModelCheckingTimed(this.negationLTLBuchi);
                        //goto IL_2FC;
                        if (this.selectedBahaviorName != "Non-Zeno Only" || this.selectedEngineName != "Strongly Connected Component Based Search with Digitization") {
                            TCJAssertionLTL.GetCounterExampleWithTocks(new TickSimulationConfiguration(this.Process, "init", null, this.GlobalEnv, false, false), this.verificationOutput);
                        }
                        return;
                    }
                    if (!(selectedEngineName == "Strongly Connected Component Based Search with Digitization")) {
                        //goto IL_2FC;
                        if (this.selectedBahaviorName != "Non-Zeno Only" || this.selectedEngineName != "Strongly Connected Component Based Search with Digitization") {
                            TCJAssertionLTL.GetCounterExampleWithTocks(new TickSimulationConfiguration(this.Process, "init", null, this.GlobalEnv, false, false), this.verificationOutput);
                        }
                        return;
                    }
                    this.initialStep = new TickConfiguration(this.Process, "init", null, this.GlobalEnv, false, false);
                    this.TarjanModelCheckingUntimed(this.negationLTLBuchi);
                    //goto IL_2FC;
                    if (this.selectedBahaviorName != "Non-Zeno Only" || this.selectedEngineName != "Strongly Connected Component Based Search with Digitization") {
                        TCJAssertionLTL.GetCounterExampleWithTocks(new TickSimulationConfiguration(this.Process, "init", null, this.GlobalEnv, false, false), this.verificationOutput);
                    }
                    return;
                }
            } else {
                String selectedEngineName2;
                if ((selectedEngineName2 = this.selectedEngineName) == null) {
                    //goto IL_2FC;
                    if (this.selectedBahaviorName != "Non-Zeno Only" || this.selectedEngineName != "Strongly Connected Component Based Search with Digitization") {
                        TCJAssertionLTL.GetCounterExampleWithTocks(new TickSimulationConfiguration(this.Process, "init", null, this.GlobalEnv, false, false), this.verificationOutput);
                    }
                    return;
                }
                if (selectedEngineName2 == "First Witness Trace with Zone Abstraction") {
                    this.initialStep = new ZoneConfiguration(this.Process, "init", null, this.GlobalEnv, false, new DBM());
                    ((ZoneConfiguration) this.initialStep).SetTimer();
                    this.DFSVerification();
                    //goto IL_2FC;
                    if (this.selectedBahaviorName != "Non-Zeno Only" || this.selectedEngineName != "Strongly Connected Component Based Search with Digitization") {
                        TCJAssertionLTL.GetCounterExampleWithTocks(new TickSimulationConfiguration(this.Process, "init", null, this.GlobalEnv, false, false), this.verificationOutput);
                    }
                    return;
                }
                if (selectedEngineName2 == "First Witness Trace with Digitization") {
                    this.initialStep = new TickConfiguration(this.Process, "init", null, this.GlobalEnv, false, false);
                    this.DFSVerification();
                    //goto IL_2FC;
                    if (this.selectedBahaviorName != "Non-Zeno Only" || this.selectedEngineName != "Strongly Connected Component Based Search with Digitization") {
                        TCJAssertionLTL.GetCounterExampleWithTocks(new TickSimulationConfiguration(this.Process, "init", null, this.GlobalEnv, false, false), this.verificationOutput);
                    }
                    return;
                }
                if (selectedEngineName2 == "Shortest Witness Trace with Zone Abstraction") {
                    this.initialStep = new ZoneConfiguration(this.Process, "init", null, this.GlobalEnv, false, new DBM());
                    ((ZoneConfiguration) this.initialStep).SetTimer();
                    this.BFSVerification();
                    //goto IL_2FC;
                    if (this.selectedBahaviorName != "Non-Zeno Only" || this.selectedEngineName != "Strongly Connected Component Based Search with Digitization") {
                        TCJAssertionLTL.GetCounterExampleWithTocks(new TickSimulationConfiguration(this.Process, "init", null, this.GlobalEnv, false, false), this.verificationOutput);
                    }
                    return;
                }
                if (selectedEngineName2 == "Shortest Witness Trace with Digitization") {
                    this.initialStep = new TickConfiguration(this.Process, "init", null, this.GlobalEnv, false, false);
                    this.BFSVerification();
                    //goto IL_2FC;
                    if (this.selectedBahaviorName != "Non-Zeno Only" || this.selectedEngineName != "Strongly Connected Component Based Search with Digitization") {
                        TCJAssertionLTL.GetCounterExampleWithTocks(new TickSimulationConfiguration(this.Process, "init", null, this.GlobalEnv, false, false), this.verificationOutput);
                    }
                    return;
                }
                if (selectedEngineName2 == "Strongly Connected Component Based Search with Zone Abstraction") {
                    this.initialStep = new ZoneConfiguration(this.Process, "init", null, this.GlobalEnv, false, new DBM());
                    ((ZoneConfiguration) this.initialStep).SetTimer();
                    this.TarjanModelChecking();
                    //goto IL_2FC;
                    if (this.selectedBahaviorName != "Non-Zeno Only" || this.selectedEngineName != "Strongly Connected Component Based Search with Digitization") {
                        TCJAssertionLTL.GetCounterExampleWithTocks(new TickSimulationConfiguration(this.Process, "init", null, this.GlobalEnv, false, false), this.verificationOutput);
                    }
                    return;
                }
                if (!(selectedEngineName2 == "Strongly Connected Component Based Search with Digitization")) {
                    //goto IL_2FC;
                    if (this.selectedBahaviorName != "Non-Zeno Only" || this.selectedEngineName != "Strongly Connected Component Based Search with Digitization") {
                        TCJAssertionLTL.GetCounterExampleWithTocks(new TickSimulationConfiguration(this.Process, "init", null, this.GlobalEnv, false, false), this.verificationOutput);
                    }
                    return;
                }
                this.initialStep = new TickConfiguration(this.Process, "init", null, this.GlobalEnv, false, false);
                this.TarjanModelChecking();
                //goto IL_2FC;
                if (this.selectedBahaviorName != "Non-Zeno Only" || this.selectedEngineName != "Strongly Connected Component Based Search with Digitization") {
                    TCJAssertionLTL.GetCounterExampleWithTocks(new TickSimulationConfiguration(this.Process, "init", null, this.GlobalEnv, false, false), this.verificationOutput);
                }
                return;
            }
        }
        String selectedEngineName3;
        if ((selectedEngineName3 = this.selectedEngineName) != null) {
            if (!(selectedEngineName3 == "Strongly Connected Component Based Search with Zone Abstraction")) {
                if (selectedEngineName3 == "Strongly Connected Component Based Search with Digitization") {
                    this.initialStep = new TickConfiguration(this.Process, "init", null, this.GlobalEnv, false, false);
                    this.ModelCheckingLivenessWithFairness();
                }
            } else {
                this.initialStep = new ZoneConfiguration(this.Process, "init", null, this.GlobalEnv, false, new DBM());
                ((ZoneConfiguration) this.initialStep).SetTimer();
                this.ModelCheckingLivenessWithFairness();
            }
        }
        /*IL_2FC:
        if (this.selectedBahaviorName != "Non-Zeno Only" || this.selectedEngineName != "Strongly Connected Component Based Search with Digitization") {
            TCJAssertionLTL.GetCounterExampleWithTocks(new TickSimulationConfiguration(this.Process, "init", null, this.GlobalEnv, false, false), this.verificationOutput);
        }*/
    }

    private boolean AllClocksRemoved(List<String> scc, String minClockState, int numbeOfClocks, Map<String, Map<Integer, Integer>> mapping, Map<String, List<String>> transitionrelation) {
        Map<String, Boolean> dictionary = new HashMap<>();
        for (int i = 1; i <= numbeOfClocks; i++) {
            Map<String, Boolean> dictionary2 = new HashMap<>();
            Stack<String> stack = new Stack<String>();
            stack.push(minClockState);
            Stack<Integer> stack2 = new Stack<>();
            stack2.push(i);
            dictionary2.put(minClockState + "$" + i, false);
            boolean flag = false;
            while (stack.size() > 0) {
                String text = stack.pop();
                int num = stack2.pop();
                List<String> list = transitionrelation.get(text);
                for (String text2 : list) {
                    if (scc.contains(text2)) {
                        if (!mapping.get(text).containsKey(num)) {
                            flag = true;
                            dictionary.put(text + "$" + num, true);
                            break;
                        }
                        int num2 = mapping.get(text).get(num);
                        String key = text2 + "$" + num2;
                        if (dictionary.containsKey(key)) {
                            flag = true;
                            break;
                        }
                        if (!dictionary2.containsKey(key)) {
                            stack.push(text2);
                            stack2.push(num2);
                        }
                    }
                }
                if (flag) {
                    break;
                }
            }
            if (!flag) {
                return false;
            }
            if (!dictionary.containsKey(minClockState + "$" + i)) {
                dictionary.put(minClockState + "$" + i, true);
            }
        }
        return true;
    }

    protected void TarjanModelCheckingTimed(BuchiAutomata negatedBA) throws ParsingException, CloneNotSupportedException, CancelRunningException {
        this.verificationOutput.loopIndex = -1;
        this.verificationOutput.counterExampleTrace = null;
        Map<String, int[]> dictionary = new HashMap<>();
        List<Map.Entry<ZoneConfiguration, String>> list = new ArrayList<>();
        Map<String, List<String>> dictionary2 = new HashMap<>();
        Map<String, Map<Integer, Integer>> dictionary3 = new HashMap<>(Ultility.MC_INITIAL_SIZE);
        for (String state : negatedBA.InitialStates) {
            List<String> list2 = negatedBA.MakeOneMove(state, this.initialStep);
            for (String value : list2) {
                list.add(new AbstractMap.SimpleEntry<>((ZoneConfiguration) this.initialStep, value));
            }
        }
        Stack<Map.Entry<ZoneConfiguration, String>> stack = new Stack<>();
        if (list.size() == 0) {
            this.verificationOutput.verificationResult = VerificationResultType.VALID;
            return;
        }
        for (int i = 0; i < list.size(); i++) {
            Map.Entry<ZoneConfiguration, String> item = list.get(i);
            stack.push(item);
            String text = item.getKey().getIDWithEvent() + "$" + item.getValue();
            Map<String, int[]> dictionary4 = dictionary;
            String key = text;
            int[] array = new int[2];
            array[0] = -1;
            dictionary4.put(key, array);
            dictionary2.put(text, new ArrayList<>(8));
        }
        List<String> list3 = new ArrayList<>(1024);
        Stack<Map.Entry<ZoneConfiguration, String>> stack2 = new Stack<>();
        int num = 0;
        Map<String, List<Map.Entry<ZoneConfiguration, String>>> dictionary5 = new HashMap<>();
        while (!this.cancelRequested()) {
            Map.Entry<ZoneConfiguration, String> item2 = stack.peek();
            ZoneConfiguration key2 = item2.getKey();
            String value2 = item2.getValue();
            String text2 = item2.getKey().getIDWithEvent() + "$" + item2.getValue();
            List<String> list4 = dictionary2.get(text2);
            int[] array2 = dictionary.get(text2);
            if (array2[0] == -1) {
                array2[0] = num;
                num++;
            }
            boolean flag = true;
            if (dictionary5.containsKey(text2)) {
                List<Map.Entry<ZoneConfiguration, String>> list5 = dictionary5.get(text2);
                if (list5.size() > 0) {
                    for (int j = list5.size() - 1; j >= 0; j--) {
                        Map.Entry<ZoneConfiguration, String> item3 = list5.get(j);
                        String key3 = item3.getKey().getIDWithEvent() + "$" + item3.getValue();
                        if (dictionary.get(key3)[0] == -1) {
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
                List<ZoneConfiguration> list6 = key2.MakeOneMoveLocal();
                List<Map.Entry<ZoneConfiguration, String>> list7 = new ArrayList<>();
                for (ZoneConfiguration zoneConfiguration : list6) {
                    List<String> list8 = negatedBA.MakeOneMove(value2, zoneConfiguration);
                    for (int k = 0; k < list8.size(); k++) {
                        list7.add(new AbstractMap.SimpleEntry<>(zoneConfiguration, list8.get(k)));
                    }
                }
                this.verificationOutput.transitions += (long) list7.size();
                for (int l = list7.size() - 1; l >= 0; l--) {
                    Map.Entry<ZoneConfiguration, String> keyValuePair = list7.get(l);
                    String text3 = keyValuePair.getKey().getIDWithEvent() + "$" + keyValuePair.getValue();
                    if (!dictionary3.containsKey(text3)) {
                        dictionary3.put(text3, keyValuePair.getKey().mapping);
                    }
                    int[] array3 = dictionary.get(text3);
                    if (dictionary.containsKey(text3)) {
                        list4.add(text3);
                        if (array3[0] == -1) {
                            if (flag) {
                                stack.push(keyValuePair);
                                flag = false;
                                list7.remove(l);
                            } else {
                                list7.set(l, keyValuePair);
                            }
                        } else {
                            list7.remove(l);
                        }
                    } else {
                        Map<String, int[]> dictionary6 = dictionary;
                        String key4 = text3;
                        int[] array = new int[2];
                        array[0] = -1;
                        dictionary6.put(key4, array);
                        dictionary2.put(text3, new ArrayList<>(8));
                        list4.add(text3);
                        if (flag) {
                            stack.push(keyValuePair);
                            flag = false;
                            list7.remove(l);
                        } else {
                            list7.set(l, keyValuePair);
                        }
                    }
                }
                dictionary5.put(text2, list7);
            }
            if (flag) {
                int num2 = array2[0];
                int num3 = num2;
                boolean flag2 = false;
                for (int m = 0; m < list4.size(); m++) {
                    String text4 = list4.get(m);
                    if (text4 == text2) {
                        flag2 = true;
                    }
                    int[] array4 = dictionary.get(text4);
                    if (array4[0] != -2) {
                        if (array4[0] > num3) {
                            num2 = Math.min(num2, array4[1]);
                        } else {
                            num2 = Math.min(num2, array4[0]);
                        }
                    }
                }
                array2[1] = num2;
                stack.pop();
                if (num2 == num3) {
                    boolean flag3 = !key2.IsUrgent;
                    int count = key2.clocksBound.size();
                    String minClockState = text2;
                    list3.add(text2);
                    array2[0] = -2;
                    boolean flag4 = item2.getValue().endsWith("-");
                    if (stack2.size() > 0) {
                        Map.Entry<ZoneConfiguration, String> keyValuePair2 = stack2.peek();
                        String text5 = keyValuePair2.getKey().getIDWithEvent() + "$" + keyValuePair2.getValue();
                        while (stack2.size() > 0 && dictionary.get(text5)[0] > num3) {
                            stack2.pop();
                            list3.add(text5);
                            dictionary.get(text5)[0] = -2;
                            if (!flag4 && keyValuePair2.getValue().endsWith("-")) {
                                flag4 = true;
                            }
                            ZoneConfiguration key5 = keyValuePair2.getKey();
                            flag3 = (flag3 || !key5.IsUrgent);
                            if (stack2.size() > 0) {
                                keyValuePair2 = stack2.peek();
                                text5 = keyValuePair2.getKey().getIDWithEvent() + "$" + keyValuePair2.getValue();
                            }
                            if (key5.clocksBound.size() < count) {
                                minClockState = text5;
                                count = key5.clocksBound.size();
                            }
                        }
                    }
                    int count2 = list3.size();
                    if (flag4 && (key2.isDeadLock || count2 > 1 || flag2) && (key2.isDeadLock || flag3) && (count == 0 || this.AllClocksRemoved(list3, minClockState, count, dictionary3, dictionary2))) {
                        this.verificationOutput.verificationResult = VerificationResultType.INVALID;
                        this.verificationOutput.noOfStates = (long) dictionary.size();
                        while (stack.size() > 0 && stack.peek().getKey().event != "init") {
                            stack.pop();
                        }
                        List<ConfigurationBase> list9 = new ArrayList<>(64);
                        ZoneConfiguration zoneConfiguration2 = (ZoneConfiguration) this.initialStep;
                        list9.add(zoneConfiguration2);
                        if (stack.size() > 0) {
                            String startID = stack.peek().getKey().getIDWithEvent() + "$" + stack.peek().getValue();
                            List<String> list10 = this.LocalGetCounterExample(list3, dictionary2, startID);
                            for (int n = 1; n < list10.size(); n++) {
                                String configID = this.GetConfigID(list10.get(n));
                                ZoneConfiguration[] array5 = zoneConfiguration2.makeOneMove().toArray(new ZoneConfiguration[0]);
                                //ConfigurationBase[] array5 = zoneConfiguration2.MakeOneMove().ToArray < ConfigurationBase > ();
                                for (ZoneConfiguration zoneConfiguration3 : array5) {
                                    if (zoneConfiguration3.getIDWithEvent() == configID) {
                                        list9.add(zoneConfiguration3);
                                        zoneConfiguration2 = zoneConfiguration3;
                                        break;
                                    }
                                }
                            }
                        }
                        this.verificationOutput.counterExampleTrace = list9;
                        return;
                    }
                    for (String key6 : list3) {
                        dictionary5.remove(key6);
                    }
                    list3.clear();
                } else {
                    stack2.push(item2);
                }
            }
            if (stack.size() <= 0) {
                this.verificationOutput.verificationResult = VerificationResultType.VALID;
                this.verificationOutput.noOfStates = (long) dictionary.size();
                return;
            }
        }
        this.verificationOutput.noOfStates = (long) dictionary.size();
    }


    protected void TarjanModelCheckingUntimed(BuchiAutomata negatedBA) throws ParsingException, CloneNotSupportedException, CancelRunningException {
        this.verificationOutput.loopIndex = -1;
        this.verificationOutput.counterExampleTrace = null;
        Map<String, int[]> dictionary = new HashMap<>();
        List<Map.Entry<TickConfiguration, String>> list = new ArrayList<>();
        Map<String, List<String>> dictionary2 = new HashMap<>();
        for (String state : negatedBA.InitialStates) {
            List<String> list2 = negatedBA.MakeOneMove(state, this.initialStep);
            for (String value : list2) {
                list.add(new AbstractMap.SimpleEntry<>((TickConfiguration) this.initialStep, value));
            }
        }
        Stack<Map.Entry<TickConfiguration, String>> stack = new Stack<>();
        if (list.size() == 0) {
            this.verificationOutput.verificationResult = VerificationResultType.VALID;
            return;
        }
        for (int i = 0; i < list.size(); i++) {
            Map.Entry<TickConfiguration, String> item = list.get(i);
            stack.push(item);
            String text = item.getKey().getIDWithEvent() + "$" + item.getValue();
            Map<String, int[]> dictionary3 = dictionary;
            String key = text;
            int[] array = new int[2];
            array[0] = -1;
            dictionary3.put(key, array);
            dictionary2.put(text, new ArrayList<>(8));
        }
        List<String> list3 = new ArrayList<>(1024);
        Stack<Map.Entry<TickConfiguration, String>> stack2 = new Stack<>();
        int num = 0;
        Map<String, List<Map.Entry<TickConfiguration, String>>> dictionary4 = new HashMap<>();
        while (!this.cancelRequested()) {
            Map.Entry<TickConfiguration, String> item2 = stack.peek();
            TickConfiguration key2 = item2.getKey();
            String value2 = item2.getValue();
            String text2 = item2.getKey().getIDWithEvent() + "$" + item2.getValue();
            List<String> list4 = dictionary2.get(text2);
            int[] array2 = dictionary.get(text2);
            if (array2[0] == -1) {
                array2[0] = num;
                num++;
            }
            boolean flag = true;
            if (dictionary4.containsKey(text2)) {
                List<Map.Entry<TickConfiguration, String>> list5 = dictionary4.get(text2);
                if (list5.size() > 0) {
                    for (int j = list5.size() - 1; j >= 0; j--) {
                        Map.Entry<TickConfiguration, String> item3 = list5.get(j);
                        String key3 = item3.getKey().getIDWithEvent() + "$" + item3.getValue();
                        if (dictionary.get(key3)[0] == -1) {
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
                List<TickConfiguration> list6 = key2.MakeOneMoveWithTockTransitions();
                List<Map.Entry<TickConfiguration, String>> list7 = new ArrayList<>();
                for (TickConfiguration tickConfiguration : list6) {
                    List<String> list8 = negatedBA.MakeOneMove(value2, tickConfiguration);
                    for (int k = 0; k < list8.size(); k++) {
                        list7.add(new AbstractMap.SimpleEntry<>(tickConfiguration, list8.get(k)));
                    }
                }
                this.verificationOutput.transitions += (long) list7.size();
                for (int l = list7.size() - 1; l >= 0; l--) {
                    Map.Entry<TickConfiguration, String> keyValuePair = list7.get(l);
                    String text3 = keyValuePair.getKey().getIDWithEvent() + "$" + keyValuePair.getValue();
                    int[] array3 = dictionary.get(text3);
                    if (dictionary.containsKey(text3)) {
                        list4.add(text3);
                        if (array3[0] == -1) {
                            if (flag) {
                                stack.push(keyValuePair);
                                flag = false;
                                list7.remove(l);
                            } else {
                                list7.set(l, keyValuePair);
                            }
                        } else {
                            list7.remove(l);
                        }
                    } else {
                        Map<String, int[]> dictionary5 = dictionary;
                        String key4 = text3;
                        int[] array4 = new int[2];
                        array4[0] = -1;
                        dictionary5.put(key4, array4);
                        dictionary2.put(text3, new ArrayList<>(8));
                        list4.add(text3);
                        if (flag) {
                            stack.push(keyValuePair);
                            flag = false;
                            list7.remove(l);
                        } else {
                            list7.set(l, keyValuePair);
                        }
                    }
                }
                dictionary4.put(text2, list7);
            }
            if (flag) {
                int num2 = array2[0];
                int num3 = num2;
                boolean flag2 = false;
                for (int m = 0; m < list4.size(); m++) {
                    String text4 = list4.get(m);
                    if (text4 == text2) {
                        flag2 = true;
                    }
                    int[] array5 = dictionary.get(text4);
                    if (array5[0] != -2) {
                        if (array5[0] > num3) {
                            num2 = Math.min(num2, array5[1]);
                        } else {
                            num2 = Math.min(num2, array5[0]);
                        }
                    }
                }
                array2[1] = num2;
                stack.pop();
                if (num2 == num3) {
                    boolean flag3 = key2.IsTock();
                    boolean flag4 = flag3;
                    list3.add(text2);
                    array2[0] = -2;
                    boolean flag5 = item2.getValue().endsWith("-");
                    if (stack2.size() > 0) {
                        Map.Entry<TickConfiguration, String> keyValuePair2 = stack2.peek();
                        String text5 = keyValuePair2.getKey().getIDWithEvent() + "$" + keyValuePair2.getValue();
                        while (stack2.size() > 0 && dictionary.get(text5)[0] > num3) {
                            stack2.pop();
                            list3.add(text5);
                            dictionary.get(text5)[0] = -2;
                            if (!flag5 && keyValuePair2.getValue().endsWith("-")) {
                                flag5 = true;
                            }
                            if (keyValuePair2.getKey().IsTock()) {
                                flag3 = true;
                            } else {
                                flag4 = false;
                            }
                            if (stack2.size() > 0) {
                                keyValuePair2 = stack2.peek();
                                text5 = keyValuePair2.getKey().getIDWithEvent() + "$" + keyValuePair2.getValue();
                            }
                        }
                    }
                    int count = list3.size();
                    if (flag5 && (key2.isDeadLock || count > 1 || flag2) && (key2.isDeadLock || flag3) && !flag4) {
                        this.verificationOutput.verificationResult = VerificationResultType.INVALID;
                        this.verificationOutput.noOfStates = (long) dictionary.size();
                        while (stack.size() > 0 && stack.peek().getKey().event != "init") {
                            stack.pop();
                        }
                        String startID = stack.peek().getKey().getIDWithEvent() + "$" + stack.peek().getValue();
                        List<String> trace = this.LocalGetCounterExample(list3, dictionary2, startID);
                        this.verificationOutput.counterExampleTrace = this.GetConcreteTraceWithTock(trace);
                        return;
                    }
                    for (String key5 : list3) {
                        dictionary4.remove(key5);
                    }
                    list3.clear();
                } else {
                    stack2.push(item2);
                }
            }
            if (stack.size() <= 0) {
                this.verificationOutput.verificationResult = VerificationResultType.VALID;
                this.verificationOutput.noOfStates = (long) dictionary.size();
                return;
            }
        }
        this.verificationOutput.noOfStates = (long) dictionary.size();
    }

    protected List<ConfigurationBase> GetConcreteTraceWithTock(List<String> trace) throws ParsingException, CloneNotSupportedException {
        List<ConfigurationBase> list = new ArrayList<>(64);
        TickConfiguration tickConfiguration = (TickConfiguration) this.initialStep;
        list.add(tickConfiguration);
        for (int i = 1; i < trace.size(); i++) {
            String configID = this.GetConfigID(trace.get(i));
            List<TickConfiguration> list2 = tickConfiguration.MakeOneMoveWithTockTransitions();
            for (TickConfiguration tickConfiguration2 : list2) {
                if (tickConfiguration2.getIDWithEvent() == configID) {
                    list.add(tickConfiguration2);
                    tickConfiguration = tickConfiguration2;
                    break;
                }
            }
        }
        return list;
    }

    protected List<String> LocalGetCounterExample(List<String> FairSCC, Map<String, List<String>> OutgoingTransitionTable, String startID) throws CancelRunningException {
        if (!this.verificationOutput.generateCounterExample) {
            return new ArrayList<>();
        }
        List<String> list = new ArrayList<>(FairSCC);
        List<String> list2 = this.Path(startID, list, OutgoingTransitionTable);
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
                        if (FairSCC.contains(text3) && !dictionary.containsKey(text3)) {
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
        return list2;
    }

    private List<String> FindShortestPath(String start, String end, Map<String, Boolean> localVisited,
                                          Stack<String> outStack, List<String> FairSCC, Map<String, List<String>> OutgoingTransitionTable) {
        int count = localVisited.size();
        Hashtable hashtable = new Hashtable(count);
        Queue<String> queue = new ArrayDeque<>();
        Queue<List<String>> queue2 = new ArrayDeque<>();
        queue.add(start);
        queue2.add(new ArrayList<>());
        hashtable.put(start, null);
        String text;
        List<String> list;
        List<String> list2;
        for (; ; ) {
            text = queue.poll();
            List<String> collection = queue2.poll();
            list = new ArrayList<>(collection);
            list.add(text);
            list2 = OutgoingTransitionTable.get(text);
            for (int i = 0; i < list2.size(); i++) {
                String text2 = list2.get(i);
                if (text2 == end) {
                    //goto Block_1;
                    list.remove(0);
                    return list;
                }
                if (localVisited.containsKey(text2) && FairSCC.contains(text2) && !hashtable.containsKey(text2)) {
                    hashtable.put(text2, null);
                    queue.add(text2);
                    queue2.add(list);
                }
            }
            if (queue.size() <= 0) {
                //goto Block_6;
                list2 = OutgoingTransitionTable.get(text);
                for (int j = 0; j < list2.size(); j++) {
                    String text3 = list2.get(j);
                    if (FairSCC.contains(text3) && !localVisited.containsKey(text3)) {
                        outStack.push(text3);
                    }
                }
                return list;
            }
        }
        /*Block_1:
        list.remove(0);
        return list;
        Block_6:
        list2 = OutgoingTransitionTable.get(text);
        for (int j = 0; j < list2.size(); j++) {
            String text3 = list2.get(j);
            if (FairSCC.contains(text3) && !localVisited.containsKey(text3)) {
                outStack.push(text3);
            }
        }
        return list;*/
    }
}
