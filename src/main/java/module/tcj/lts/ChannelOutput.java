package module.tcj.lts;

import common.classes.expressions.EvaluatorDenotational;
import common.classes.expressions.Valuation;
import common.classes.expressions.expressionclass.Expression;
import common.classes.expressions.expressionclass.ExpressionValue;
import common.classes.expressions.expressionclass.IntConstant;
import common.classes.lts.ChannelQueue;
import common.classes.moduleinterface.SpecificationBase;
import common.classes.ultility.Ultility;
import module.tcj.assertions.TCJDataStore;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public final class ChannelOutput extends TCJProcess {
    public String ChannelName;

    public Expression ChannelIndex;

    public Expression[] ExpressionList;

    public TCJProcess Process;

    public boolean IsUrgent;

    public ChannelOutput(String evtName, Expression index, Expression[] e, TCJProcess process, boolean isUrgent) {
        this.ChannelName = evtName;
        this.ChannelIndex = index;
        this.Process = process;
        this.ExpressionList = e;
        this.IsUrgent = isUrgent;
        String key = this.ChannelName + (this.ChannelIndex != null ? this.ChannelIndex.toString() : "")
                + "!"
                + Ultility.pPIDListDot(this.ExpressionList)
                + (this.IsUrgent ? ">>" : ">")
                + this.Process.ProcessID;

        this.ProcessID = TCJDataStore.DataManager.InitializeProcessID(key);
    }

    @Override
    public List<ZoneConfiguration> MoveOneStep(ZoneConfiguration eStep) {
        List<ZoneConfiguration> list = new ArrayList<>();
        Valuation valuation = eStep.globalEnv;
        ChannelQueue channelQueue = null;
        String text = this.ChannelName;
        if (this.ChannelIndex != null) {
            int value = ((IntConstant) EvaluatorDenotational.evaluate(this.ChannelIndex, valuation))._value;
            if (value >= Specification.ChannelArrayDatabase.get(this.ChannelName)) {
                throw new IndexOutOfBoundsException("Channel index out of bounds for expression " + this.toString());
            }
            text = text + "[" + value + "]";

        }
        channelQueue = valuation.channels.get(text);
        if (channelQueue != null && channelQueue.size() < channelQueue.size) {
            ExpressionValue[] array = new ExpressionValue[this.ExpressionList.length];
            String text2 = text + "!";
            String text3 = text + "?";
            for (int i = 0; i < this.ExpressionList.length; i++) {
                array[i] = EvaluatorDenotational.evaluate(this.ExpressionList[i], valuation);
                if (i == this.ExpressionList.length - 1) {
                    text2 += array[i].toString();
                    text3 += array[i].expressionID;
                } else {
                    text2 = text2 + array[i].toString() + ".";
                    text3 = text3 + array[i].expressionID + ".";
                }
            }
            ChannelQueue channelQueue2 = channelQueue.clone();
            channelQueue2.add(array);
            valuation = valuation.getChannelClone();
            valuation.channels.put(text, channelQueue2);

            list.add(new ZoneConfiguration(this.Process, text3, (text3 != text2) ? text2 : null, valuation, false, this.IsUrgent ? eStep.DBM.addUrgency() : eStep.DBM));
        }
        return list;
    }

    @Override
    public void GetDiscretEventTransitions(TickConfiguration eStep, List<TickConfiguration> toReturn) {
        Valuation valuation = eStep.globalEnv;
        String text = this.ChannelName;
        if (this.ChannelIndex != null) {
            int value = ((IntConstant) EvaluatorDenotational.evaluate(this.ChannelIndex, valuation))._value;
            if (value >= Specification.ChannelArrayDatabase.get(this.ChannelName)) {
                throw new IndexOutOfBoundsException("Channel index out of bounds for expression " + this.toString());
            }
            text = text + "[" + value + "]";

        }
        ChannelQueue channelQueue = valuation.channels.get(text);
        if (channelQueue != null && channelQueue.size() < channelQueue.size) {
            ExpressionValue[] array = new ExpressionValue[this.ExpressionList.length];
            String text2 = text + "!";
            String text3 = text + "?";
            for (int i = 0; i < this.ExpressionList.length; i++) {
                array[i] = EvaluatorDenotational.evaluate(this.ExpressionList[i], valuation);
                if (i == this.ExpressionList.length - 1) {
                    text2 += array[i].toString();
                    text3 += array[i].expressionID;
                } else {
                    text2 = text2 + array[i] + ".";
                    text3 = text3 + array[i].expressionID + ".";
                }
            }
            ChannelQueue channelQueue2 = channelQueue.clone();
            channelQueue2.add(array);
            valuation = valuation.getChannelClone();
            valuation.channels.put(text2, channelQueue2);
            toReturn.add(new TickConfiguration(this.Process, text3, (text3 != text2) ? text2 : null, valuation, false, this.IsUrgent));
        }
    }

    @Override
    public List<ZoneConfigurationWithChannelData> SyncOutput(ZoneConfiguration eStep) {
        List<ZoneConfigurationWithChannelData> list = new ArrayList<>(1);
        String text = this.ChannelName;
        if (this.ChannelIndex != null) {
            int value = ((IntConstant) EvaluatorDenotational.evaluate(this.ChannelIndex, eStep.globalEnv))._value;
            if (value >= Specification.ChannelArrayDatabase.get(this.ChannelName)) {
                throw new IndexOutOfBoundsException("Channel index out of bounds for expression " + this.toString());
            }
            text = text + "[" + value + "]";
        }
        if (SpecificationBase.syncrhonousChannelNames.contains(text)) {
            String text2 = text;
            String text3 = text;
            Expression[] array = new Expression[this.ExpressionList.length];
            for (int i = 0; i < this.ExpressionList.length; i++) {
                array[i] = EvaluatorDenotational.evaluate(this.ExpressionList[i], eStep.globalEnv);
                text2 = text2 + "." + array[i];
                text3 = text3 + "." + array[i].expressionID;
            }
            list.add(new ZoneConfigurationWithChannelData(this.Process, text3, (text3 != text2) ? text2 : null, eStep.globalEnv, false, text, array, this.IsUrgent ? eStep.DBM.addUrgency() : eStep.DBM));
        }
        return list;
    }

    @Override
    public void SyncOutput(TickConfiguration eStep, List<TickConfigurationWithChannelData> toReturn) {
        String text = this.ChannelName;
        if (this.ChannelIndex != null) {
            int value = ((IntConstant) EvaluatorDenotational.evaluate(this.ChannelIndex, eStep.globalEnv))._value;
            if (value >= Specification.ChannelArrayDatabase.get(this.ChannelName)) {
                throw new IndexOutOfBoundsException("Channel index out of bounds for expression " + this.toString());
            }
            text = text + "[" + value + "]";
        }
        if (SpecificationBase.syncrhonousChannelNames.contains(text)) {
            String text2 = text;
            String text3 = text;
            Expression[] array = new Expression[this.ExpressionList.length];
            for (int i = 0; i < this.ExpressionList.length; i++) {
                array[i] = EvaluatorDenotational.evaluate(this.ExpressionList[i], eStep.globalEnv);
                text2 = text2 + "." + array[i];
                text3 = text3 + "." + array[i].expressionID;
            }
            toReturn.add(new TickConfigurationWithChannelData(this.Process, text3, (text3 != text2) ? text2 : null, eStep.globalEnv, false, this.IsUrgent, text, array));
        }
    }

    @Override
    public HashSet<String> GetAlphabets(Map<String, String> visitedDefinitionRefs) {
        return this.Process.GetAlphabets(visitedDefinitionRefs);
    }

    @Override
    public List<String> GetGlobalVariables() {
        List<String> globalVariables = this.Process.GetGlobalVariables();
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
        if (this.ChannelIndex != null) {
            return this.ChannelName + "[" + this.ChannelIndex + "]!"
                    + Ultility.PPStringListDot(this.ExpressionList).replaceFirst("^\\.", "")
                    + (this.IsUrgent ? "->>" : "->")
                    + this.Process;

        }
        return this.ChannelName + "!" + Ultility.PPStringListDot(this.ExpressionList).replaceFirst("^\\.", "")
                + (this.IsUrgent ? "->>" : "->")
                + this.Process;
    }

    @Override
    public TCJProcess ClearConstant(Map<String, Expression> constMapping) {
        Expression[] array = new Expression[this.ExpressionList.length];
        for (int i = 0; i < this.ExpressionList.length; i++) {
            array[i] = this.ExpressionList[i].clearConstant(constMapping);
        }
        return new ChannelOutput(this.ChannelName, (this.ChannelIndex != null) ? this.ChannelIndex.clearConstant(constMapping) : this.ChannelIndex, array, this.Process.ClearConstant(constMapping), this.IsUrgent);
    }

    @Override
    public boolean MustBeAbstracted() {
        return this.Process.MustBeAbstracted();
    }
}
