package module.tcj.lts;

import common.ParsingException;
import common.classes.expressions.EvaluatorDenotational;
import common.classes.expressions.Valuation;
import common.classes.expressions.expressionclass.Expression;
import common.classes.expressions.expressionclass.ExpressionValue;
import common.classes.expressions.expressionclass.IntConstant;
import common.classes.expressions.expressionclass.VariableValueOutOfRangeException;
import common.classes.lts.EventCollection;
import common.classes.ultility.Ultility;
import module.tcj.assertions.TCJDataStore;

import java.util.*;

public final class DefinitionRef extends TCJProcess {
    public String name;
    public Expression[] args;
    public Definition def;

    public DefinitionRef(String name, Expression[] args) {
        this.name = name;
        this.args = args;
        StringBuilder stringBuilder = new StringBuilder(this.name + "(");
        for (int i = 0; i < this.args.length; i++) {
            stringBuilder.append(this.args[i].expressionID);
            if (i < this.args.length - 1) {
                stringBuilder.append(",");
            }
        }
        stringBuilder.append(")");
        this.ProcessID = TCJDataStore.DataManager.InitializeProcessID(stringBuilder.toString());
    }

    public TCJProcess getProcess(Valuation global) throws ParsingException {
        Expression[] array = new Expression[this.args.length];
        String text = this.name + "(";
        String text2 = this.name + "(";
        for (int i = 0; i < this.args.length; i++) {
            Expression expression = this.args[i];
            text2 += expression.expressionID;
            if (!expression.hasVar) {
                array[i] = expression;
                text += expression.expressionID;
            } else {
                ExpressionValue expressionValue = EvaluatorDenotational.evaluate(expression, global);
                array[i] = expressionValue;
                text += expressionValue.expressionID;
            }
            if (array[i] instanceof IntConstant) {
                int value = ((IntConstant) array[i])._value;
                String text3 = this.def.LocalVariables[i];
                if (this.def.ParameterLowerBound.containsKey(text3)) {
                    int containsKey = this.def.ParameterLowerBound.getContainsKey(text3);
                    if (containsKey > value) {
                        throw new VariableValueOutOfRangeException("Argument "
                                + text3
                                + "'s current value "
                                + value
                                + " is smaller than its lower bound "
                                + containsKey);
                    }
                }
                if (this.def.ParameterUpperLowerBound.containsKey(text3)) {
                    int containsKey2 = this.def.ParameterUpperLowerBound.getContainsKey(text3);
                    if (value > containsKey2) {
                        throw new VariableValueOutOfRangeException("Argument "
                                + text3
                                + "'s current value "
                                + value
                                + " is greater than its upper bound "
                                + containsKey2);
                    }
                }
            }
            if (i < this.args.length - 1) {
                text += ",";
                text2 += ",";
            }
        }
        text2 += ")";
        text += ")";
        TCJProcess TCJProcess = TCJDataStore.DataManager.DefinitionInstanceDatabase.getContainsKey(text);
        if (TCJProcess != null) {
            return TCJProcess;
        }
        Map<String, Expression> dictionary = new HashMap<>(this.args.length);
        for (int j = 0; j < array.length; j++) {
            dictionary.put(this.def.LocalVariables[j], array[j]);
        }
        this.ProcessID = TCJDataStore.DataManager.InitializeProcessID(text2);
        synchronized (TCJDataStore.DataManager) {
            TCJProcess = this.def.Process.ClearConstant(dictionary);
            TCJDataStore.DataManager.SetLastProcessID(this.ProcessID);
        }
        TCJProcess.ProcessID = this.ProcessID;
        TCJDataStore.DataManager.DefinitionInstanceDatabase.add(text, TCJProcess);
        return TCJProcess;
    }

    @Override
    public List<ZoneConfiguration> MoveOneStep(ZoneConfiguration eStep) throws CloneNotSupportedException, ParsingException {
        return this.getProcess(eStep.globalEnv).MoveOneStep(eStep);
    }

    @Override
    public void GetDiscretEventTransitions(TickConfiguration eStep, List<TickConfiguration> toReturn) throws CloneNotSupportedException, ParsingException {
        this.getProcess(eStep.globalEnv).GetDiscretEventTransitions(eStep, toReturn);
    }

    @Override
    public TCJProcess GetProcessAfterDelay(TickConfiguration eStep) throws ParsingException {
        return this.getProcess(eStep.globalEnv).GetProcessAfterDelay(eStep);
    }

    @Override
    public List<ZoneConfigurationWithChannelData> SyncOutput(ZoneConfiguration eStep) throws ParsingException {
        return this.getProcess(eStep.globalEnv).SyncOutput(eStep);
    }

    @Override
    public List<ZoneConfiguration> SyncInput(ZoneConfigurationWithChannelData eStep) throws ParsingException {
        return this.getProcess(eStep.globalEnv).SyncInput(eStep);
    }

    @Override
    public void SyncOutput(TickConfiguration eStep, List<TickConfigurationWithChannelData> toReturn) throws ParsingException {
        this.getProcess(eStep.globalEnv).SyncOutput(eStep, toReturn);
    }

    @Override
    public void SyncInput(TickConfigurationWithChannelData eStep, List<TickConfiguration> toReturn) throws ParsingException {
        this.getProcess(eStep.globalEnv).SyncInput(eStep, toReturn);
    }

    @Override
    public String toString() {
        return this.name + "(" + Ultility.pPStringList(this.args) + ")";
    }

    @Override
    public HashSet<String> GetAlphabets(Map<String, String> visitedDefinitionRefs) throws ParsingException {
        if (visitedDefinitionRefs == null) {
            return new HashSet<String>();
        }
        if (this.def.AlphabetsCalculable) {
            return new HashSet<String>(this.def.Alphabets);
        }
        if (this.def.AlphabetEvents != null) {
            Map<String, Expression> dictionary = new HashMap<>();
            for (int i = 0; i < this.args.length; i++) {
                String key = this.def.LocalVariables[i];
                dictionary.put(key, this.args[i]);
            }
            EventCollection eventCollection = this.def.AlphabetEvents.ClearConstant(dictionary);
            if (!eventCollection.ContainsVariable()) {
                return new HashSet<String>(eventCollection.EventNames);
            }
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("ERROR - TCJ FAILED to calculate the alphabet of process " + this.name + "." + "\n");
            stringBuilder.append("CAUSE - Process " + this.name + " is invoked with gloabl variables as parameters!" + "\n");
            stringBuilder.append("REMEDY - 1) Avoid passing global variable as parameters  2) Or manually specify the alphabet of process "
                    + this.name
                    + " using the following syntax: \n\r\t #alphabet "
                    + this.name
                    + " {X}; \n\rwhere X is a set of event names with no variables." + "\n");
            throw new RuntimeException(stringBuilder.toString());
        } else {
            for (Expression expression : this.args) {
                if (expression.hasVar) {
                    StringBuilder stringBuilder2 = new StringBuilder();
                    stringBuilder2.append("ERROR - TCJ FAILED to calculate the alphabet of process " + this.name + "." + "\n");
                    stringBuilder2.append("CAUSE - Process " + this.name + " is invoked with gloabl variables as parameters!");
                    stringBuilder2.append("REMEDY - Manually specify the alphabet of process "
                            + this.name
                            + " using the following syntax: \n\r\t #alphabet "
                            + this.name
                            + "{X}; \n\rwhere X is a set of events." + "\n");

                    throw new RuntimeException(stringBuilder2.toString());
                }
            }
            String text = this.name + "(";
            for (int k = 0; k < this.args.length; k++) {
                Expression expression2 = this.args[k];
                text += expression2.toString();
                if (k < this.args.length - 1) {
                    text += ",";
                }
            }
            text += ")";
            if (!visitedDefinitionRefs.containsKey(this.name)) {
                Map<String, String> dictionary2 = new HashMap<>();
                for (String key2 : visitedDefinitionRefs.keySet()) {
                    dictionary2.put(key2, visitedDefinitionRefs.get(key2));
                }
                dictionary2.put(this.name, text);
                return this.getProcess(null).GetAlphabets(dictionary2);
            }
            if (visitedDefinitionRefs.get(this.name) != text) {
                StringBuilder stringBuilder3 = new StringBuilder();
                stringBuilder3.append("ERROR - PAT FAILED to calculate the alphabet of process " + this.name + "." + "\n");
                stringBuilder3.append("CAUSE - Process " + this.name + " is recursively invoked with different parameters!" + "\n");
                stringBuilder3.append("REMEDY - Manually specify the alphabet of process "
                        + this.name
                        + " using the following syntax: \n\r\t #alphbet "
                        + this.name
                        + "{X}; \n\rwhere X is a set of events");

                throw new RuntimeException(stringBuilder3.toString());
            }
            return new HashSet<String>();
        }
    }

    @Override
    public List<String> GetGlobalVariables() {
        List<String> globalVars = this.def.GlobalVars;
        for (Expression expression : this.args) {
            Ultility.Union(globalVars, expression.getVars());
        }
        return globalVars;
    }

    @Override
    public List<String> GetChannels() {
        return this.def.Channels;
    }

    @Override
    public TCJProcess ClearConstant(Map<String, Expression> constMapping) {
        Expression[] array = new Expression[this.args.length];
        for (int i = 0; i < this.args.length; i++) {
            Expression expression = this.args[i].clearConstant(constMapping);
            if (!expression.hasVar) {
                array[i] = EvaluatorDenotational.evaluate(expression, null);
            } else {
                array[i] = expression;
            }
        }
        DefinitionRef definitionRef = new DefinitionRef(this.name, array);
        if (this.def == null) {
            //TODO: TCJTreeWalker
            //TCJTreeWalker.dlist.Add(definitionRef);
            //TCJTreeWalker.dtokens.Add(null);
        } else {
            definitionRef.def = this.def;
        }
        return definitionRef;
    }

    @Override
    public boolean MustBeAbstracted() {
        return this.def.MustAbstract;
    }

    @Override
    public TCJProcess GetTopLevelConcurrency(List<String> visitedDef) {
        if (!visitedDef.contains(this.name)) {
            List<String> list = new ArrayList<>();
            list.add(this.name);
            return this.def.Process.GetTopLevelConcurrency(list);
        }
        return null;
    }

    @Override
    public TCJProcess SetTimer(Valuation globalEnv, List<List<Integer>> clockBound, Map<Integer, Integer> mapping) throws ParsingException {
        return this.getProcess(globalEnv).SetTimer(globalEnv, clockBound, mapping);
    }

    @Override
    public boolean IsTimeImmediate() {
        return this.def.Process.IsTimeImmediate();
    }
}
