package common.classes.datastructure;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class DeterministicFAState_Subset {

    public Map<String, DeterministicFAState_Subset> Post;

    public int ID;


    public boolean IsUnstable;


    public boolean IsDivergent;

    public List<List<String>> NegatedRefusals;


    public HashSet<DeterministicFAState_Subset> Sub;

    public DeterministicFAState_Subset(int id) {
        this.ID = id;
        this.Post = new HashMap<>();
        this.Sub = new HashSet<DeterministicFAState_Subset>();
    }

    @Override
    public String toString() {
        String text = "";
        for (Map.Entry<String, DeterministicFAState_Subset> keyValuePair : this.Post.entrySet()) {
            Object obj = text;
            text = obj.toString() + this.ID + " -" + keyValuePair.getKey() + "-> " + keyValuePair.getValue().ID + "\r\n";
        }
        return text;
    }

    public void AddTransition(String evt, DeterministicFAState_Subset target) {
        this.Post.put(evt, target);
    }

    public DeterministicFAState_Subset Next(String evt) {
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
