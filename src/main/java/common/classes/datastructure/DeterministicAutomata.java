package common.classes.datastructure;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class DeterministicAutomata {
    public Map<Integer, DeterministicFAState> States;


    public DeterministicFAState InitialState;


    public HashSet<String> Alphabet;

    public DeterministicAutomata() {
        this.States = new HashMap<>();
        this.Alphabet = new HashSet<String>();
    }

    public DeterministicFAState GetInitState() {
        return this.InitialState;
    }

    public void SetIsUnstable(int id, boolean isUnstable) {
        this.States.get(id).IsUnstable = isUnstable;
    }


    public DeterministicFAState AddInitialState() {
        DeterministicFAState deterministicFAState = new DeterministicFAState(0);
        this.States.put(0, deterministicFAState);
        this.InitialState = deterministicFAState;
        return deterministicFAState;
    }

    public DeterministicFAState AddState() {
        int count = this.States.size();
        DeterministicFAState deterministicFAState = new DeterministicFAState(this.States.size());
        this.States.put(count, deterministicFAState);
        return deterministicFAState;
    }

    public void AddTransition(DeterministicFAState source, String evt, DeterministicFAState target) {
        source.AddTransition(evt, target);
    }

    @Override
    public String toString() {
        String text = "";
        for (Map.Entry<Integer, DeterministicFAState> keyValuePair : this.States.entrySet()) {
            text += keyValuePair.getValue().toString();
        }
        return text;
    }
}
