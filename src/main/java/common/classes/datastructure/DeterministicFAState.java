package common.classes.datastructure;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeterministicFAState {
    public Map<String, DeterministicFAState> Post;


    public int ID;

    public boolean IsUnstable;


    public boolean IsDivergent;


    public List<List<String>> NegatedRefusals;


    public DeterministicFAState(int id) {
        this.ID = id;
        this.Post = new HashMap<>();
    }

    @Override
    public String toString() {
        String text = "";
        for (Map.Entry<String, DeterministicFAState> keyValuePair : this.Post.entrySet()) {
            Object obj = text;
            text = obj.toString() + this.ID + " -" + keyValuePair.getKey() + "-> " + keyValuePair.getValue().ID + "\r\n";
        }
        return text;
    }

    public void AddTransition(String evt, DeterministicFAState target) {
        this.Post.put(evt, target);
    }

    public DeterministicFAState Next(String evt) {
        if (this.Post.containsKey(evt)) {
            return this.Post.get(evt);
        }
        return null;
    }

    public int GetID() {
        return this.ID;
    }

    public void SetNegatedRefusals(List<List<String>> negatedRefusals) {
        this.NegatedRefusals = negatedRefusals;
    }
}
