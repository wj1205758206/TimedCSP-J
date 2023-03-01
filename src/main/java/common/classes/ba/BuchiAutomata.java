package common.classes.ba;

import common.ParsingException;
import common.classes.expressions.Valuation;
import common.classes.expressions.expressionclass.Expression;
import common.classes.moduleinterface.ConfigurationBase;
import common.classes.ultility.Ultility;
import module.tcj.ultility.ParsingUltility;
import org.antlr.v4.runtime.CommonToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class BuchiAutomata {

    public List<String> InitialStates;


    public String[] States;


    public Transition[] Transitions;


    public boolean HasAcceptState;


    public String Name;


    public Map<String, Expression> DeclarationDatabase;


    public List<String> VisibleVariables;


    public Map<String, Transition[]> fromTransitions;

    public boolean HasXOperator;


    public boolean SyntacticSafety;

    public BuchiAutomata(List<String> initials, String[] states, Transition[] transitions, String[] fairset) {
        this.Transitions = transitions;
        this.States = states;
        this.InitialStates = initials;
        this.HasAcceptState = (fairset.length > 0);
        this.fromTransitions = new HashMap<>();
        for (String text : this.States) {
            List<Transition> list = new ArrayList<>();
            for (Transition transition : this.Transitions) {
                if (transition.FromState == text) {
                    list.add(transition);
                }
            }
            List<Transition> list2 = new ArrayList<>();
            for (Transition transition2 : list) {
                if (transition2.ToState.endsWith("-")) {
                    list2.add(transition2);
                }
            }
            for (Transition transition3 : list) {
                if (!transition3.ToState.endsWith("-")) {
                    list2.add(transition3);
                }
            }
            this.fromTransitions.put(text, list2.toArray(new Transition[0]));
        }
    }

    public void Initialize(Map<String, Expression> declare, Valuation valuation) throws ParsingException, NoSuchMethodException {
        this.DeclarationDatabase = declare;
        this.VisibleVariables = new ArrayList<>();
        for (Transition transition : this.Transitions) {
            for (Proposition proposition : transition.labels) {
                if (!proposition.IsSigmal() && this.DeclarationDatabase.containsKey(proposition.Label)) {
                    Ultility.Union(this.VisibleVariables, this.DeclarationDatabase.get(proposition.Label).getVars());
                    ParsingUltility.TestIsBooleanExpression(this.DeclarationDatabase.get(proposition.Label),
                            new CommonToken(null, -1, -1, -1, -1), " used in LTL proposition " + proposition.Label, valuation, new HashMap<>());
                }
            }
        }
    }

    public List<String> MakeOneMove(String state, ConfigurationBase config) {
        List<String> list = new ArrayList<>();
        Transition[] array = this.fromTransitions.get(state);
        String event = config.event;
        for (Transition transition : array) {
            boolean flag = true;
            for (Proposition proposition : transition.labels) {
                if (proposition.IsSigmal()) {
                    list.add(transition.ToState);
                    break;
                }
                String label = proposition.Label;
                Expression expression = this.DeclarationDatabase.get(label);
                if (proposition.Negated) {
                    if (!this.DeclarationDatabase.containsKey(label)) {
                        if (label == event) {
                            flag = false;
                            break;
                        }
                    } else if (config.implyCondition(expression)) {
                        flag = false;
                        break;
                    }
                } else if (!this.DeclarationDatabase.containsKey(label)) {
                    if (label != event) {
                        flag = false;
                        break;
                    }
                } else if (!config.implyCondition(expression)) {
                    flag = false;
                    break;
                }
            }
            if (flag && !list.contains(transition.ToState)) {
                list.add(transition.ToState);
            }
        }
        return list;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("States" + "\n");
        for (String value : this.States) {
            stringBuilder.append(value + "\n");
        }
        stringBuilder.append("Transitions" + "\n");
        for (Transition transition : this.Transitions) {
            if (this.InitialStates.contains(transition.FromState)) {
                stringBuilder.append("-->" + "\n");
            }
            stringBuilder.append(transition.toString() + "\n");
        }
        return stringBuilder.toString();
    }
}
