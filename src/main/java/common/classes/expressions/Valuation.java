package common.classes.expressions;


import common.ParsingException;
import common.classes.datastructure.StringDictionary;
import common.classes.datastructure.StringDictionaryEntryWithKey;
import common.classes.datastructure.StringDictionaryWithKey;
import common.classes.datastructure.StringHashTable;
import common.classes.expressions.expressionclass.ExpressionValue;
import common.classes.expressions.expressionclass.IntConstant;
import common.classes.expressions.expressionclass.RecordValue;
import common.classes.expressions.expressionclass.VariableValueOutOfRangeException;
import common.classes.lts.ChannelQueue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Valuation {
    public Map<String, ChannelQueue> channels;


    public StringDictionaryWithKey<ExpressionValue> variables;


    public static StringDictionary<String> valutionHashTable;


    public static StringDictionary<Integer> variableLowerBound;


    public static StringDictionary<Integer> variableUpperLowerBound;


    public static boolean hasVariableConstraints;


    public static StringHashTable hiddenVars;


    public String ID;

    public void extendDestructive(String x, ExpressionValue v) {
        if (!this.variables.setValue(x, v)) {
            this.variables.add(x, v);
        }
    }

    public static void checkVariableRange(String x, ExpressionValue v) {
        if (Valuation.hasVariableConstraints && v instanceof IntConstant) {
            int value = ((IntConstant) v)._value;
            if (Valuation.variableLowerBound.containsKey(x)) {
                int containsKey = Valuation.variableLowerBound.getContainsKey(x);
                if (containsKey > value) {
                    throw new VariableValueOutOfRangeException("Variable "
                            + x
                            + "'s current value "
                            + value
                            + " is smaller than its lower bound "
                            + containsKey);
                }
            }
            if (Valuation.variableUpperLowerBound.containsKey(x)) {
                int containsKey2 = Valuation.variableUpperLowerBound.getContainsKey(x);
                if (value > containsKey2) {
                    throw new VariableValueOutOfRangeException("Variable "
                            + x
                            + "'s current value "
                            + value
                            + " is greater than its upper bound "
                            + containsKey2);
                }
            }
        }
    }

    public static void checkVariableRange(String x, ExpressionValue v, int line, int position) throws ParsingException {
        if (Valuation.variableLowerBound.count > 0 || Valuation.variableUpperLowerBound.count > 0) {
            if (v instanceof IntConstant) {
                int value = ((IntConstant) v)._value;
                if (Valuation.variableLowerBound.containsKey(x)) {
                    int containsKey = Valuation.variableLowerBound.getContainsKey(x);
                    if (containsKey > value) {
                        throw new ParsingException("Variable "
                                + x
                                + "'s current value "
                                + value
                                + " is smaller than its lower bound "
                                + containsKey, line, position, x);

                    }
                }
                if (Valuation.variableUpperLowerBound.containsKey(x)) {
                    int containsKey2 = Valuation.variableUpperLowerBound.getContainsKey(x);
                    if (value > containsKey2) {
                        throw new ParsingException("Variable "
                                + x
                                + "'s current value "
                                + value
                                + " is greater than its upper bound "
                                + containsKey2, line, position, x);
                    }
                }
            } else if (v instanceof RecordValue) {
                RecordValue recordValue = (RecordValue) v;
                for (ExpressionValue v2 : recordValue.associations) {
                    Valuation.checkVariableRange(x, v2, line, position);
                }
            }
        }
    }

    public Valuation getClone() throws CloneNotSupportedException {
        Valuation valuation = new Valuation();
        if (this.variables != null) {
            StringDictionaryWithKey<ExpressionValue> stringDictionaryWithKey = new StringDictionaryWithKey<ExpressionValue>(this.variables);
            for (int i = 0; i < this.variables._entries.length; i++) {
                StringDictionaryEntryWithKey<ExpressionValue> stringDictionaryEntryWithKey = this.variables._entries[i];
                if (stringDictionaryEntryWithKey != null) {
                    stringDictionaryWithKey._entries[i] = new StringDictionaryEntryWithKey<ExpressionValue>(stringDictionaryEntryWithKey.hashA, stringDictionaryEntryWithKey.hashB, stringDictionaryEntryWithKey.value.getClone(), stringDictionaryEntryWithKey.key);
                }
            }
            valuation.variables = stringDictionaryWithKey;
        }
        if (this.channels != null) {
            valuation.channels = new HashMap<>(this.channels);
        }
        return valuation;
    }

    public Valuation getVariableClone() throws CloneNotSupportedException {
        Valuation valuation = new Valuation();
        if (this.variables != null) {
            StringDictionaryWithKey<ExpressionValue> stringDictionaryWithKey = new StringDictionaryWithKey<ExpressionValue>(this.variables);
            for (int i = 0; i < this.variables._entries.length; i++) {
                StringDictionaryEntryWithKey<ExpressionValue> stringDictionaryEntryWithKey = this.variables._entries[i];
                if (stringDictionaryEntryWithKey != null) {
                    stringDictionaryWithKey._entries[i] = new StringDictionaryEntryWithKey<ExpressionValue>(stringDictionaryEntryWithKey.hashA, stringDictionaryEntryWithKey.hashB, stringDictionaryEntryWithKey.value.getClone(), stringDictionaryEntryWithKey.key);
                }
            }
            valuation.variables = stringDictionaryWithKey;
        }
        valuation.channels = this.channels;
        return valuation;
    }

    public Valuation getVariableClone(List<String> visibleVars) {
        Valuation valuation = new Valuation();
        if (this.variables != null) {
            StringDictionaryWithKey<ExpressionValue> stringDictionaryWithKey = new StringDictionaryWithKey<ExpressionValue>();

            for (StringDictionaryEntryWithKey<ExpressionValue> entry : this.variables._entries) {
                if (entry != null) {
                    for (String var : visibleVars) {
                        if (var == entry.key) {
                            stringDictionaryWithKey.add(entry.key, entry.value.getClone());
                            break;
                        }
                    }
                }
            }

            valuation.variables = stringDictionaryWithKey;
        }
        valuation.channels = this.channels;
        return valuation;
    }

    public Valuation getChannelClone() {
        Valuation clone = new Valuation();
        clone.variables = this.variables;
        clone.channels = new HashMap<>(this.channels);
        return clone;
    }

    public Valuation getVariableChannelClone(List<String> visibleVars, List<String> visibleChannels) {
        Valuation variableClone = this.getVariableClone(visibleVars);
        if (this.channels != null) {
            Map<String, ChannelQueue> dictionary = new HashMap<>();

            for (Map.Entry<String, ChannelQueue> keyValuePair : this.channels.entrySet()) {
                if (visibleChannels.contains(keyValuePair.getKey()) && keyValuePair.getValue().size != 0) {
                    dictionary.put(keyValuePair.getKey(), keyValuePair.getValue());
                }
            }
            variableClone.channels = dictionary;
        }
        return variableClone;
    }

    public String getID() {
        if (this.ID == null) {
            StringBuilder stringBuilder = new StringBuilder();
            if (this.variables != null) {
                if (Valuation.hiddenVars != null) {
                    for (StringDictionaryEntryWithKey<ExpressionValue> stringDictionaryEntryWithKey : this.variables._entries) {
                        if (stringDictionaryEntryWithKey != null && !Valuation.hiddenVars.containsKey(stringDictionaryEntryWithKey.key)) {
                            stringBuilder.append(stringDictionaryEntryWithKey.value.expressionID + "&");
                        }
                    }
                } else {
                    for (StringDictionaryEntryWithKey<ExpressionValue> stringDictionaryEntryWithKey2 : this.variables._entries) {
                        if (stringDictionaryEntryWithKey2 != null) {
                            stringBuilder.append(stringDictionaryEntryWithKey2.value.expressionID + "&");
                        }
                    }


                }
            }
            if (this.channels != null && this.channels.size() > 0) {
                for (ChannelQueue channelQueue : this.channels.values()) {
                    stringBuilder.append(channelQueue.getID());
                    stringBuilder.append("+");
                }
            }
            this.ID = stringBuilder.toString();
            String text = Valuation.valutionHashTable.getContainsKey(this.ID);
            if (text == null) {
                text = String.valueOf(Valuation.valutionHashTable.count);
                Valuation.valutionHashTable.add(this.ID, text);
            }
            this.ID = text;
        }
        return this.ID;
    }

    public String getID(String processID) {
        return this.getID() + "$" + processID;
    }

    public boolean isEmpty() {
        return this.variables == null && this.channels == null;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        if (this.variables != null) {
            stringBuilder.append("Variables:" + "\n");
            for (StringDictionaryEntryWithKey<ExpressionValue> stringDictionaryEntryWithKey : this.variables._entries) {
                if (stringDictionaryEntryWithKey != null) {
                    stringBuilder.append(stringDictionaryEntryWithKey.key + "=" + stringDictionaryEntryWithKey.value.toString() + ";" + "\n");
                }
            }
        }
        if (this.channels != null && this.channels.size() > 0) {
            if (stringBuilder.length() > 0) {
                stringBuilder.append("\n");
            }
            stringBuilder.append("Channels:" + "\n");
            for (Map.Entry<String, ChannelQueue> keyValuePair : this.channels.entrySet()) {
                stringBuilder.append(keyValuePair.getKey() + "<=" + keyValuePair.getValue().toString() + ">" + "\n");
            }

        }
        return stringBuilder.toString();
    }
}
