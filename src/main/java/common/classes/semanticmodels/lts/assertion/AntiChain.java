package common.classes.semanticmodels.lts.assertion;

import common.classes.datastructure.FAState;
import common.classes.ultility.Ultility;

import java.util.*;

public final class AntiChain {

    private Map<String, List<HashSet<FAState>>> Pairs = new HashMap<>(Ultility.MC_INITIAL_SIZE);

    public boolean Add(String implStateID, HashSet<FAState> specState) {
        List<HashSet<FAState>> list = this.Pairs.get(implStateID);
        if (this.Pairs.containsKey(implStateID)) {
            for (HashSet<FAState> hashSet : list) {
                if (specState.containsAll(hashSet)) {
                    return true;
                }
                /*if (hashSet.IsSubsetOf(specState))
                {
                    return true;
                }*/
            }
            List<HashSet<FAState>> list2 = new ArrayList<>();
            list2.add(specState);
            for (HashSet<FAState> hashSet2 : list) {
                if (!hashSet2.containsAll(specState)) {
                    list2.add(hashSet2);
                }
                /*if (!specState.IsSubsetOf(hashSet2))
                {
                    list2.Add(hashSet2);
                }*/
            }
            this.Pairs.put(implStateID, list2);
            return false;
        }
        List<HashSet<FAState>> list3 = new ArrayList<>();
        list3.add(specState);
        this.Pairs.put(implStateID, list3);
        return false;
    }

    public int GetNoOfStates() {
        int num = 0;
        for (List<HashSet<FAState>> list : this.Pairs.values()) {
            num += list.size();
        }
        return num;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (Map.Entry<String, List<HashSet<FAState>>> keyValuePair : this.Pairs.entrySet()) {
            stringBuilder.append("(" + keyValuePair.getKey() + ":");
            for (HashSet<FAState> hashSet : keyValuePair.getValue()) {
                stringBuilder.append("(" + hashSet.toString() + ")");
            }
            stringBuilder.append(")" + "\n");
        }
        return stringBuilder.toString();
    }
}
