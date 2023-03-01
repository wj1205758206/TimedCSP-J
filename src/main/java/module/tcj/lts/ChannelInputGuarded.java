package module.tcj.lts;

import common.classes.expressions.EvaluatorDenotational;
import common.classes.expressions.Valuation;
import common.classes.expressions.expressionclass.*;
import common.classes.lts.ChannelQueue;
import common.classes.ultility.Ultility;
import module.tcj.assertions.TCJDataStore;

import java.util.*;

public final class ChannelInputGuarded extends TCJProcess {

    public Expression GuardExpression;

    public String ChannelName;

    public Expression ChannelIndex;

    public Expression[] ExpressionList;

    public TCJProcess Process;

    public boolean IsUrgent;

    public ChannelInputGuarded(String evtName, Expression index, Expression[] exp, TCJProcess process, Expression guard, boolean isUrgent) {
        this.ChannelName = evtName;
        this.ChannelIndex = index;
        this.ExpressionList = exp;
        this.Process = process;
        this.GuardExpression = guard;
        this.IsUrgent = isUrgent;
        String key = this.ChannelName + "?" + (this.ChannelIndex != null ? this.ChannelIndex.toString() : "")
                + Ultility.pPIDListDot(this.ExpressionList)
                + guard.expressionID
                + (this.IsUrgent ? ">>" : ">")
                + this.Process.ProcessID;
        this.ProcessID = TCJDataStore.DataManager.InitializeProcessID(key);
    }

    @Override
    public List<ZoneConfiguration> MoveOneStep(ZoneConfiguration eStep) {
        List<ZoneConfiguration> list = new ArrayList<>();
        Valuation globalEnv = eStep.globalEnv;
        ChannelQueue channelQueue = null;
        String text = this.ChannelName;
        if (this.ChannelIndex != null) {
            int value = ((IntConstant) EvaluatorDenotational.evaluate(this.ChannelIndex, globalEnv))._value;
            if (value >= Specification.ChannelArrayDatabase.get(this.ChannelName)) {
                throw new IndexOutOfBoundsException("Channel index out of bounds for expression " + this.toString());
            }
            text = text + "[" + value + "]";
        }
        channelQueue = globalEnv.channels.get(text);
        if (channelQueue != null && channelQueue.size() > 0) {
            ChannelQueue channelQueue2 = channelQueue.clone();
            ExpressionValue[] array = channelQueue2.poll();
            if (array.length == this.ExpressionList.length) {
                Map<String, Expression> dictionary = new HashMap<>(array.length);
                Valuation channelClone = eStep.globalEnv.getChannelClone();
                channelClone.channels.put(text, channelQueue2);

                String text2 = text + "?";
                String text3 = text + "?";
                for (int i = 0; i < this.ExpressionList.length; i++) {
                    ExpressionValue expressionValue = array[i];
                    if (i == this.ExpressionList.length - 1) {
                        text2 += expressionValue;
                        text3 += expressionValue.expressionID;
                    } else {
                        text2 = text2 + expressionValue + ".";
                        text3 = text3 + expressionValue.expressionID + ".";
                    }
                    if (this.ExpressionList[i] instanceof Variable) {
                        dictionary.put(this.ExpressionList[i].expressionID, expressionValue);
                    } else if (expressionValue.expressionID != this.ExpressionList[i].expressionID) {
                        return list;
                    }
                }
                Expression exp = this.GuardExpression.clearConstant(dictionary);
                ExpressionValue expressionValue2 = EvaluatorDenotational.evaluate(exp, channelClone);

                if (((BoolConstant) expressionValue2)._value) {
                    TCJProcess p = (dictionary.size() > 0) ? this.Process.ClearConstant(dictionary) : this.Process;
                    list.add(new ZoneConfiguration(p, text3, (text2 != text3) ? text2 : null, channelClone, false, this.IsUrgent ? eStep.DBM.addUrgency() : eStep.DBM));
                }
            }
        }
        return list;
    }

    @Override
    public void GetDiscretEventTransitions(TickConfiguration eStep, List<TickConfiguration> toReturn) {
        Valuation globalEnv = eStep.globalEnv;
        ChannelQueue channelQueue = null;
        String text = this.ChannelName;
        if (this.ChannelIndex != null) {
            int value = ((IntConstant) EvaluatorDenotational.evaluate(this.ChannelIndex, globalEnv))._value;
            if (value >= Specification.ChannelArrayDatabase.get(this.ChannelName)) {
                throw new IndexOutOfBoundsException("Channel index out of bounds for expression " + this.toString());
            }
            text = text + "[" + value + "]";
        }
        channelQueue = globalEnv.channels.get(text);
        if (channelQueue != null && channelQueue.size() > 0) {
            ChannelQueue channelQueue2 = channelQueue.clone();
            ExpressionValue[] array = channelQueue2.poll();
            if (array.length == this.ExpressionList.length) {
                Map<String, Expression> dictionary = new HashMap<>(array.length);
                Valuation channelClone = eStep.globalEnv.getChannelClone();
                channelClone.channels.put(text, channelQueue2);
                String text2 = text + "?";
                String text3 = text + "?";
                for (int i = 0; i < this.ExpressionList.length; i++) {
                    ExpressionValue expressionValue = array[i];
                    if (i == this.ExpressionList.length - 1) {
                        text2 += expressionValue;
                        text3 += expressionValue.expressionID;
                    } else {
                        text2 = text2 + expressionValue + ".";
                        text3 = text3 + expressionValue.expressionID + ".";
                    }
                    if (this.ExpressionList[i] instanceof Variable) {
                        dictionary.put(this.ExpressionList[i].expressionID, expressionValue);
                    } else if (expressionValue.expressionID != this.ExpressionList[i].expressionID) {
                        return;
                    }
                }
                Expression exp = this.GuardExpression.clearConstant(dictionary);
                ExpressionValue expressionValue2 = EvaluatorDenotational.evaluate(exp, channelClone);

                if (((BoolConstant) expressionValue2)._value) {
                    TCJProcess p = (dictionary.size() > 0) ? this.Process.ClearConstant(dictionary) : this.Process;
                    toReturn.add(new TickConfiguration(p, text3, (text2 != text3) ? text2 : null, channelClone, false, this.IsUrgent));
                }
            }
        }
    }

    @Override
    public List<ZoneConfiguration> SyncInput(ZoneConfigurationWithChannelData eStep) {
        List<ZoneConfiguration> list = new ArrayList<>();
        String text = this.ChannelName;
        if (this.ChannelIndex != null) {
            int value = ((IntConstant) EvaluatorDenotational.evaluate(this.ChannelIndex, eStep.globalEnv))._value;
            if (value >= Specification.ChannelArrayDatabase.get(this.ChannelName)) {
                throw new IndexOutOfBoundsException("Channel index out of bounds for expression " + this.toString());
            }
            text = text + "[" + value + "]";
        }
        if (eStep.ChannelName == text && eStep.Expressions.length == this.ExpressionList.length) {
            Map<String, Expression> dictionary = new HashMap<>(eStep.Expressions.length);
            for (int i = 0; i < this.ExpressionList.length; i++) {
                Expression expression = eStep.Expressions[i];
                if (this.ExpressionList[i] instanceof Variable) {
                    dictionary.put(this.ExpressionList[i].expressionID, expression);
                } else if (expression.expressionID != this.ExpressionList[i].expressionID) {
                    return list;
                }
            }
            Expression exp = this.GuardExpression.clearConstant(dictionary);
            ExpressionValue expressionValue = EvaluatorDenotational.evaluate(exp, eStep.globalEnv);

            if (!((BoolConstant) expressionValue)._value) {
                return list;
            }
            TCJProcess p = this.Process;
            if (dictionary.size() > 0) {
                p = this.Process.ClearConstant(dictionary);
            }
            list.add(new ZoneConfiguration(p, null, null, eStep.globalEnv, false, this.IsUrgent ? eStep.DBM.addUrgency() : eStep.DBM));
        }
        return list;
    }

    @Override
    public void SyncInput(TickConfigurationWithChannelData eStep, List<TickConfiguration> toReturn) {
        String text = this.ChannelName;
        if (this.ChannelIndex != null) {
            int value = ((IntConstant) EvaluatorDenotational.evaluate(this.ChannelIndex, eStep.globalEnv))._value;
            if (value >= Specification.ChannelArrayDatabase.get(this.ChannelName)) {
                throw new IndexOutOfBoundsException("Channel index out of bounds for expression " + this.toString());
            }
            text = text + "[" + value + "]";
        }
        if (eStep.ChannelName == text && eStep.Expressions.length == this.ExpressionList.length) {
            Map<String, Expression> dictionary = new HashMap<>(eStep.Expressions.length);
            for (int i = 0; i < this.ExpressionList.length; i++) {
                Expression expression = eStep.Expressions[i];
                if (this.ExpressionList[i] instanceof Variable) {
                    dictionary.put(this.ExpressionList[i].expressionID, expression);
                } else if (expression.expressionID != this.ExpressionList[i].expressionID) {
                    return;
                }
            }
            Expression exp = this.GuardExpression.clearConstant(dictionary);
            ExpressionValue expressionValue = EvaluatorDenotational.evaluate(exp, eStep.globalEnv);

            if (!((BoolConstant) expressionValue)._value) {
                return;
            }
            TCJProcess p = this.Process;
            if (dictionary.size() > 0) {
                p = this.Process.ClearConstant(dictionary);
            }
            toReturn.add(new TickConfiguration(p, null, null, eStep.globalEnv, false, this.IsUrgent));
        }
    }

    @Override
    public HashSet<String> GetAlphabets(Map<String, String> visitedDefinitionRefs) {
        return this.Process.GetAlphabets(visitedDefinitionRefs);
    }

    @Override
    public List<String> GetGlobalVariables() {
        List<String> globalVariables = this.Process.GetGlobalVariables();
        Ultility.addList(globalVariables, this.GuardExpression.getVars());
        for (Expression expression : this.ExpressionList) {
            Ultility.addList(globalVariables, expression.getVars());
        }
        if (this.ChannelIndex != null) {
            Ultility.addList(globalVariables, this.ChannelIndex.getVars());
        }
        return globalVariables;
    }

    @Override
    public List<String> GetChannels() {
        List<String> channels = this.Process.GetChannels();
        if (!channels.contains(this.ChannelName)) {
            channels.add(this.ChannelName);
        }
        return channels;
    }

    @Override
    public String toString() {
        if (this.ChannelIndex == null) {
            return this.ChannelName + "?[" + this.GuardExpression.toString()
                    + Ultility.pPStringList(this.ExpressionList).toString().replaceFirst("^\\.", "")
                    + (this.IsUrgent ? "->>" : "->")
                    + this.Process;
        }
        return this.ChannelName + "[" + this.ChannelIndex + "]?[" + this.GuardExpression.toString()
                + Ultility.PPStringListDot(this.ExpressionList).replaceFirst("^\\.", "")
                + (this.IsUrgent ? "->>" : "->")
                + this.Process;
    }

    @Override
    public TCJProcess ClearConstant(Map<String, Expression> constMapping) {
        Expression[] array = new Expression[this.ExpressionList.length];
        for (int i = 0; i < this.ExpressionList.length; i++) {
            if (this.ExpressionList[i] instanceof ExpressionValue) {
                array[i] = this.ExpressionList[i];
            } else {
                array[i] = this.ExpressionList[i].clearConstant(constMapping);
                if (!array[i].hasVar) {
                    array[i] = EvaluatorDenotational.evaluate(array[i], null);
                }
            }
        }
        return new ChannelInputGuarded(this.ChannelName, (this.ChannelIndex != null) ? this.ChannelIndex.clearConstant(constMapping) : this.ChannelIndex, array, this.Process.ClearConstant(constMapping), this.GuardExpression.clearConstant(constMapping), this.IsUrgent);
    }

    @Override
    public boolean MustBeAbstracted() {
        return this.Process.MustBeAbstracted();
    }
}
