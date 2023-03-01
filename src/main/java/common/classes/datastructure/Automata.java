package common.classes.datastructure;

import common.classes.ultility.Ultility;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Stack;

public class Automata {
    public Map<Integer, FAState> States;


    public HashSet<FAState> InitialState;


    public HashSet<String> Alphabet;

    public Automata() {
        this.States = new HashMap<>();
        this.InitialState = new HashSet<FAState>();
        this.Alphabet = new HashSet<>();
    }

    public void SetInitialState(FAState init) {
        this.InitialState.add(init);
    }

    public FAState AddState() {
        int count = this.States.size();
        FAState fastate = new FAState(count);
        this.States.put(count, fastate);
        return fastate;
    }

    public void AddTransition(FAState source, String evt, FAState target) {
        source.AddTransition(evt, target);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (Map.Entry<Integer, FAState> keyValuePair : this.States.entrySet()) {
            FAState value = keyValuePair.getValue();
            for (Map.Entry<String, HashSet<FAState>> keyValuePair2 : value.Post.entrySet()) {
                for (FAState fastate : keyValuePair2.getValue()) {
                    stringBuilder.append(
                            value.toString()
                                    + "-"
                                    + keyValuePair2.getKey()
                                    + "->"
                                    + fastate.toString()
                                    + "\n"
                    );
                }
            }
        }
        return stringBuilder.toString();
    }

    public DeterministicAutomata DeterminizeWithRefusalsAndDiv() {
        Map<String, DeterministicFAState> dictionary = new HashMap<>(Ultility.MC_INITIAL_SIZE);
        Stack<NormalizedFAState> stack = new Stack<>();
        NormalizedFAState item = new NormalizedFAState(this.InitialState).TauReachable();
        stack.push(item);
        DeterministicAutomata deterministicAutomata = new DeterministicAutomata();
        dictionary.put(item.GetID(), deterministicAutomata.AddInitialState());
        while (stack.size() > 0) {
            item = stack.pop();
            DeterministicFAState deterministicFAState = dictionary.get(item.GetID());
            if (item.IsDiv()) {
                deterministicAutomata.InitialState.IsDivergent = true;
            } else {
                deterministicFAState.SetNegatedRefusals(item.GetFailuresNegate());
                Map<String, HashSet<FAState>> dictionary2 = new HashMap<>();
                for (FAState fastate : item.States) {
                    for (Map.Entry<String, HashSet<FAState>> keyValuePair : fastate.Post.entrySet()) {
                        if (keyValuePair.getKey() != "τ") {
                            HashSet<FAState> hashSet = dictionary2.get(keyValuePair.getKey());
                            if (!dictionary2.containsKey(keyValuePair.getKey())) {
                                hashSet = new HashSet<FAState>();
                                dictionary2.put(keyValuePair.getKey(), hashSet);
                            }
                            for (FAState item2 : keyValuePair.getValue()) {
                                hashSet.add(item2);
                            }
                        }
                    }
                }
                for (Map.Entry<String, HashSet<FAState>> keyValuePair2 : dictionary2.entrySet()) {
                    NormalizedFAState normalizedFAState = new NormalizedFAState(keyValuePair2.getValue());
                    NormalizedFAState item3 = normalizedFAState.TauReachable();
                    DeterministicFAState deterministicFAState2 = dictionary.get(item3.GetID());
                    if (!dictionary.containsKey(item3.GetID())) {
                        deterministicFAState2 = deterministicAutomata.AddState();
                        dictionary.put(item3.GetID(), deterministicFAState2);
                        stack.push(item3);
                    }
                    deterministicAutomata.AddTransition(deterministicFAState, keyValuePair2.getKey(), deterministicFAState2);
                }
            }
        }
        return deterministicAutomata;
    }

    public DeterministicAutomata DeterminizeWithRefusals() {
        Map<String, DeterministicFAState> dictionary = new HashMap<>(Ultility.MC_INITIAL_SIZE);
        Stack<NormalizedFAState> stack = new Stack<>();
        NormalizedFAState item = new NormalizedFAState(this.InitialState).TauReachable();
        stack.push(item);
        DeterministicAutomata deterministicAutomata = new DeterministicAutomata();
        dictionary.put(item.GetID(), deterministicAutomata.AddInitialState());
        deterministicAutomata.InitialState.SetNegatedRefusals(item.GetFailuresNegate());
        while (stack.size() > 0) {
            item = stack.pop();
            DeterministicFAState source = dictionary.get(item.GetID());
            Map<String, HashSet<FAState>> dictionary2 = new HashMap<>();
            for (FAState fastate : item.States) {
                for (Map.Entry<String, HashSet<FAState>> keyValuePair : fastate.Post.entrySet()) {
                    if (keyValuePair.getKey() != "τ") {
                        HashSet<FAState> hashSet = dictionary2.get(keyValuePair.getKey());
                        if (!dictionary2.containsKey(keyValuePair.getKey())) {
                            hashSet = new HashSet<FAState>();
                            dictionary2.put(keyValuePair.getKey(), hashSet);
                        }
                        for (FAState item2 : keyValuePair.getValue()) {
                            hashSet.add(item2);
                        }
                    }
                }
            }
            for (Map.Entry<String, HashSet<FAState>> keyValuePair2 : dictionary2.entrySet()) {
                NormalizedFAState normalizedFAState = new NormalizedFAState(keyValuePair2.getValue());
                NormalizedFAState item3 = normalizedFAState.TauReachable();
                DeterministicFAState deterministicFAState = dictionary.get(item3.GetID());
                if (!dictionary.containsKey(item3.GetID())) {
                    deterministicFAState = deterministicAutomata.AddState();
                    deterministicFAState.SetNegatedRefusals(item3.GetFailuresNegate());
                    dictionary.put(item3.GetID(), deterministicFAState);
                    stack.push(item3);
                }
                deterministicAutomata.AddTransition(source, keyValuePair2.getKey(), deterministicFAState);
            }
        }
        return deterministicAutomata;
    }

    public DeterministicAutomata Determinize() {
        Map<String, DeterministicFAState> dictionary = new HashMap<>(Ultility.MC_INITIAL_SIZE);
        Stack<NormalizedFAState> stack = new Stack<>();
        NormalizedFAState item = new NormalizedFAState(this.InitialState).TauReachable();
        stack.push(item);
        DeterministicAutomata deterministicAutomata = new DeterministicAutomata();
        dictionary.put(item.GetID(), deterministicAutomata.AddInitialState());
        while (stack.size() > 0) {
            item = stack.pop();
            DeterministicFAState source = dictionary.get(item.GetID());
            Map<String, HashSet<FAState>> dictionary2 = new HashMap<>();
            for (FAState fastate : item.States) {
                for (Map.Entry<String, HashSet<FAState>> keyValuePair : fastate.Post.entrySet()) {
                    if (keyValuePair.getKey() != "τ") {
                        HashSet<FAState> hashSet = dictionary2.get(keyValuePair.getKey());
                        if (!dictionary2.containsKey(keyValuePair.getKey())) {
                            hashSet = new HashSet<FAState>();
                            dictionary2.put(keyValuePair.getKey(), hashSet);
                        }
                        for (FAState item2 : keyValuePair.getValue()) {
                            hashSet.add(item2);
                        }
                    }
                }
            }
            for (Map.Entry<String, HashSet<FAState>> keyValuePair2 : dictionary2.entrySet()) {
                NormalizedFAState normalizedFAState = new NormalizedFAState(keyValuePair2.getValue());
                NormalizedFAState item3 = normalizedFAState.TauReachable();
                DeterministicFAState deterministicFAState = dictionary.get(item3.GetID());
                if (!dictionary.containsKey(item3.GetID())) {
                    deterministicFAState = deterministicAutomata.AddState();
                    dictionary.put(item3.GetID(), deterministicFAState);
                    stack.push(item3);
                }
                deterministicAutomata.AddTransition(source, keyValuePair2.getKey(), deterministicFAState);
            }
        }
        return deterministicAutomata;
    }

    public DeterministicAutomata_Subset DeterminizeSubset() {
        Map<String, DeterministicFAState_Subset> dictionary = new HashMap<>(Ultility.MC_INITIAL_SIZE);
        HashSet<NormalizedFAState> hashSet = new HashSet<NormalizedFAState>();
        Stack<NormalizedFAState> stack = new Stack<>();
        NormalizedFAState item = new NormalizedFAState(this.InitialState).TauReachable();
        hashSet.add(item);
        stack.push(item);
        DeterministicAutomata_Subset deterministicAutomata_Subset = new DeterministicAutomata_Subset();
        dictionary.put(item.GetID(), deterministicAutomata_Subset.AddInitialState());
        while (stack.size() > 0) {
            item = stack.pop();
            DeterministicFAState_Subset source = dictionary.get(item.GetID());
            Map<String, HashSet<FAState>> dictionary2 = new HashMap<>();
            for (FAState fastate : item.States) {
                for (Map.Entry<String, HashSet<FAState>> keyValuePair : fastate.Post.entrySet()) {
                    if (keyValuePair.getKey() != "τ") {
                        HashSet<FAState> hashSet2 = dictionary2.get(keyValuePair.getKey());
                        if (!dictionary2.containsKey(keyValuePair.getKey())) {
                            hashSet2 = new HashSet<FAState>();
                            dictionary2.put(keyValuePair.getKey(), hashSet2);
                        }
                        for (FAState item2 : keyValuePair.getValue()) {
                            hashSet2.add(item2);
                        }
                    }
                }
            }
            for (Map.Entry<String, HashSet<FAState>> keyValuePair2 : dictionary2.entrySet()) {
                NormalizedFAState normalizedFAState = new NormalizedFAState(keyValuePair2.getValue());
                NormalizedFAState item3 = normalizedFAState.TauReachable();
                DeterministicFAState_Subset deterministicFAState_Subset = dictionary.get(item3.GetID());
                if (!dictionary.containsKey(item3.GetID())) {
                    hashSet.add(item3);
                    deterministicFAState_Subset = deterministicAutomata_Subset.AddState();
                    dictionary.put(item3.GetID(), deterministicFAState_Subset);
                    stack.push(item3);
                }
                deterministicAutomata_Subset.AddTransition(source, keyValuePair2.getKey(), deterministicFAState_Subset);
                for (NormalizedFAState normalizedFAState2 : hashSet) {
                    if (item3.States.containsAll(normalizedFAState2.States)
                            && item3.States.size() != normalizedFAState2.States.size()) {
                        dictionary.get(item3.GetID()).Sub.add(dictionary.get(normalizedFAState2.GetID()));
                    } else if (item3.States.containsAll(normalizedFAState2.States)
                            && item3.States.size() != normalizedFAState2.States.size()) {
                        dictionary.get(normalizedFAState2.GetID()).Sub.add(dictionary.get(item3.GetID()));
                    }
                }
            }
        }
        return deterministicAutomata_Subset;
    }
}
