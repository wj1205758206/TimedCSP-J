package common.classes.datastructure;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class FAState {
    public Map<String, HashSet<FAState>> Pre;


    public Map<String, HashSet<FAState>> Post;


    public int ID;


    public boolean IsDiv;


    public List<String> NegatedRefusal;


    public FAState(int id) {
        this.ID = id;
        this.Pre = new HashMap<>();
        this.Post = new HashMap<>();
    }

    public boolean IsUnstable() {
        return this.NegatedRefusal == null;
    }

    @Override
    public String toString() {
        return String.valueOf(this.ID);
    }

    @Override
    public boolean equals(Object obj) {
        return ((FAState) obj).ID == this.ID;
    }

    @Override
    public int hashCode() {
        return this.ID;
    }

    public void SetNegatedReusal(List<String> negatedRefusal) {
        this.NegatedRefusal = negatedRefusal;
    }

    public void AddTransition(String evt, FAState target) {
        if (this.Post.containsKey(evt)) {
            this.Post.get(evt).add(target);
        } else {
            HashSet<FAState> hashSet = new HashSet<FAState>();
            hashSet.add(target);
            this.Post.put(evt, hashSet);
        }
        if (target.Pre.containsKey(evt)) {
            target.Pre.get(evt).add(target);
            return;
        }
        HashSet<FAState> hashSet2 = new HashSet<FAState>();
        hashSet2.add(target);
        target.Pre.put(evt, hashSet2);
    }

    public HashSet<FAState> Next(String evt) {
        if (this.Post.containsKey(evt)) {
            return this.Post.get(evt);
        }
        return null;
    }
}
