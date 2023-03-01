package module.tcj.lts;

import common.ParsingException;
import common.classes.datastructure.DBM;
import common.classes.datastructure.TimerOperationType;
import common.classes.expressions.EvaluatorDenotational;
import common.classes.expressions.Valuation;
import common.classes.expressions.expressionclass.Expression;
import common.classes.expressions.expressionclass.IntConstant;
import module.tcj.assertions.TCJDataStore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public final class WaitProcess extends TCJProcess {

    public int TimerID;

    public Expression DelayTime;

    public WaitProcess(Expression delay, int timerID) {
        this.DelayTime = delay;
        this.TimerID = timerID;
        this.ProcessID = TCJDataStore.DataManager.InitializeProcessID("W[" + delay.expressionID + "]");
    }

    @Override
    public List<ZoneConfiguration> MoveOneStep(ZoneConfiguration eStep) throws CloneNotSupportedException, ParsingException {
        List<ZoneConfiguration> list = new ArrayList<>();
        int value = ((IntConstant) EvaluatorDenotational.evaluate(this.DelayTime, eStep.globalEnv))._value;
        DBM dbm = eStep.DBM.clone();
        dbm.addConstraint(this.TimerID, TimerOperationType.Equals, value);
        ZoneConfiguration item = new ZoneConfiguration(new Skip(), "τ", "[Wait[" + value + "]]", eStep.globalEnv, false, dbm);
        list.add(item);
        return list;
    }

    @Override
    public void GetDiscretEventTransitions(TickConfiguration eStep, List<TickConfiguration> toReturn) throws CloneNotSupportedException, ParsingException {
        if (((IntConstant) EvaluatorDenotational.evaluate(this.DelayTime, eStep.globalEnv))._value == 0) {
            toReturn.add(new TickConfiguration(new Skip(), "τ", "[Wait[0]]", eStep.globalEnv, false, true));
        }
    }

    @Override
    public TCJProcess GetProcessAfterDelay(TickConfiguration eStep) throws ParsingException {
        int value = ((IntConstant) EvaluatorDenotational.evaluate(this.DelayTime, eStep.globalEnv))._value;
        if (value == 0) {
            return null;
        }
        return new WaitProcess(new IntConstant(value - 1), this.TimerID);
    }

    @Override
    public String toString() {
        if (this.TimerID == 0) {
            return "Wait[" + this.DelayTime + "]";
        }
        return "Wait[" + this.DelayTime + "]c" + this.TimerID;
    }

    @Override
    public TCJProcess ClearConstant(Map<String, Expression> constMapping) throws ParsingException {
        return new WaitProcess(this.DelayTime.clearConstant(constMapping), this.TimerID);
    }

    @Override
    public List<String> GetGlobalVariables() {
        return this.DelayTime.getVars();
    }

    @Override
    public TCJProcess SetTimer(Valuation globalEnv, List<List<Integer>> clockBound, Map<Integer, Integer> mapping) throws ParsingException {
        int value = ((IntConstant) EvaluatorDenotational.evaluate(this.DelayTime, globalEnv))._value;
        clockBound.add(Arrays.asList(value, value, value));
        if (this.TimerID > 0) {
            mapping.put(this.TimerID, clockBound.size());
        }
        return new WaitProcess(this.DelayTime, clockBound.size());
    }

    @Override
    public boolean IsTimeImmediate() {
        return true;
    }
}
