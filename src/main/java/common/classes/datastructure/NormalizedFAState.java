package common.classes.datastructure;

import java.util.*;

public class NormalizedFAState {

    public HashSet<FAState> States;


    private String ID;

    public NormalizedFAState(FAState faState) {
        this.States = new HashSet<FAState>();
        this.States.add(faState);
        this.ID = null;
    }

    public NormalizedFAState(HashSet<FAState> faStates) {
        this.States = faStates;
        this.ID = null;
    }

    public boolean IsDiv() {
        for (FAState fastate : this.States) {
            if (fastate.IsDiv) {
                return true;
            }
        }
        return false;
    }


    public NormalizedFAState NextWithTauReachable(String evt) {
        HashSet<FAState> hashSet = new HashSet<FAState>();
        for (FAState fastate : this.States) {
            HashSet<FAState> hashSet2 = fastate.Next(evt);
            if (hashSet2 != null) {
                for (FAState item : hashSet2) {
                    hashSet.add(item);
                }
            }
        }
        Stack<FAState> stack = new Stack<FAState>();
        Map<Integer, Boolean> dictionary = new HashMap<>(hashSet.size() * 5);
        for (FAState fastate2 : hashSet) {
            stack.push(fastate2);
            dictionary.put(fastate2.ID, false);
        }

        while (!stack.isEmpty()) {
            HashSet<FAState> hashSet3 = stack.pop().Next("τ");
            if (hashSet3 != null) {
                for (FAState fastate3 : hashSet3) {
                    if (!dictionary.containsKey(fastate3.ID)) {
                        dictionary.put(fastate3.ID, false);
                        stack.push(fastate3);
                        hashSet.add(fastate3);
                    }
                }
            }
        }

        return new NormalizedFAState(hashSet);


        /*using (HashSet<FAState>.Enumerator enumerator3 = hashSet.GetEnumerator())
        {
            while (enumerator3.MoveNext())
            {
                FAState fastate2 = enumerator3.Current;
                stack.Push(fastate2);
                dictionary.Add(fastate2.ID, false);
            }
				goto IL_146;
        }
        IL_D1:
        HashSet<FAState> hashSet3 = stack.pop().Next("τ");
        if (hashSet3 != null)
        {
            for (FAState fastate3 : hashSet3)
            {
                if (!dictionary.containsKey(fastate3.ID))
                {
                    dictionary.put(fastate3.ID, false);
                    stack.push(fastate3);
                    hashSet.add(fastate3);
                }
            }
        }
        IL_146:
        if (stack.size() <= 0)
        {
            return new NormalizedFAState(hashSet);
        }
			goto IL_D1;*/
    }

    public List<List<String>> GetFailuresNegate() {
        List<List<String>> list = new ArrayList<>();
        for (FAState fastate : this.States) {
            if (!fastate.IsUnstable()) {
                list.add(fastate.NegatedRefusal);
            }
        }
        return list;
    }

    public NormalizedFAState TauReachable() {
        Stack<FAState> stack = new Stack<FAState>();
        Map<Integer, Boolean> dictionary = new HashMap<>(this.States.size() * 5);
        HashSet<FAState> hashSet = new HashSet<FAState>();
        for (FAState fastate : this.States) {
            if (!dictionary.containsKey(fastate.ID)) {
                stack.push(fastate);
                dictionary.put(fastate.ID, false);
                hashSet.add(fastate);
            }
        }
        while (!stack.isEmpty()) {
            HashSet<FAState> hashSet2 = stack.pop().Next("τ");
            if (hashSet2 != null) {
                for (FAState fastate2 : hashSet2) {
                    if (!dictionary.containsKey(fastate2.ID)) {
                        dictionary.put(fastate2.ID, false);
                        stack.push(fastate2);
                        hashSet.add(fastate2);
                    }
                }
            }
        }
        return new NormalizedFAState(hashSet);

        /*using (HashSet<FAState>.Enumerator enumerator = this.States.GetEnumerator())
        {
            while (enumerator.MoveNext())
            {
                FAState fastate = enumerator.Current;
                if (!dictionary.ContainsKey(fastate.ID))
                {
                    stack.Push(fastate);
                    dictionary.Add(fastate.ID, false);
                    hashSet.Add(fastate);
                }
            }
				goto IL_EA;
        }
        IL_79:
        HashSet<FAState> hashSet2 = stack.Pop().Next("τ");
        if (hashSet2 != null)
        {
            foreach (FAState fastate2 in hashSet2)
            {
                if (!dictionary.ContainsKey(fastate2.ID))
                {
                    dictionary.Add(fastate2.ID, false);
                    stack.Push(fastate2);
                    hashSet.Add(fastate2);
                }
            }
        }
        IL_EA:
        if (stack.Count <= 0)
        {
            return new NormalizedFAState(hashSet);
        }
			goto IL_79;*/
    }

    public String GetID() {
        if (this.ID != null) {
            return this.ID;
        }
        if (this.States == null || this.States.size() == 0) {
            return "";
        }
        List<Integer> list = new ArrayList<>();
        for (FAState fastate : this.States) {
            list.add(fastate.ID);
        }
        Collections.sort(list);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(list.get(0));
        for (int i = 1; i < list.size(); i++) {
            stringBuilder.append("$");
            stringBuilder.append(list.get(i));
        }
        this.ID = stringBuilder.toString();
        return this.ID;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (FAState fastate : this.States) {
            stringBuilder.append(fastate.toString() + ",");
        }
        return stringBuilder.toString();
    }
}
