package common.classes.datastructure;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class DeterministicAutomata_Subset {

    public Map<Integer, DeterministicFAState_Subset> States;

    public DeterministicFAState_Subset InitialState;

    public HashSet<String> Alphabet;

    public DeterministicAutomata_Subset() {
        this.States = new HashMap<>();
        this.Alphabet = new HashSet<>();
    }

    public DeterministicFAState_Subset GetInitState() {
        return this.InitialState;
    }

    public void SetIsUnstable(int id, boolean isUnstable) {
        this.States.get(id).IsUnstable = isUnstable;
    }

    public DeterministicFAState_Subset AddInitialState() {
        DeterministicFAState_Subset deterministicFAState_Subset = new DeterministicFAState_Subset(0);
        this.States.put(0, deterministicFAState_Subset);
        this.InitialState = deterministicFAState_Subset;
        return deterministicFAState_Subset;
    }

    public DeterministicFAState_Subset AddState() {
        int count = this.States.size();
        DeterministicFAState_Subset deterministicFAState_Subset = new DeterministicFAState_Subset(this.States.size());
        this.States.put(count, deterministicFAState_Subset);
        return deterministicFAState_Subset;
    }

    public void AddTransition(DeterministicFAState_Subset source, String evt, DeterministicFAState_Subset target) {
        source.AddTransition(evt, target);
    }

    @Override
    public String toString() {
        String text = "";
        for (Map.Entry<Integer, DeterministicFAState_Subset> keyValuePair : this.States.entrySet()) {
            text += keyValuePair.getValue().toString();
        }
        return text;
    }
}
