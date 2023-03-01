package module.tcj.lts;

import common.classes.datastructure.StringDictionaryEntryWithKey;
import common.classes.expressions.EvaluatorDenotational;
import common.classes.expressions.Valuation;
import common.classes.expressions.expressionclass.Expression;
import common.classes.expressions.expressionclass.ExpressionValue;
import common.classes.lts.Event;
import common.classes.ultility.Ultility;
import module.tcj.assertions.TCJDataStore;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public final class DataOperationPrefix extends TCJProcess {
    public Event Event;


    public TCJProcess Process;


    public Expression AssignmentExpr;


    public boolean HasLocalVar;


    public boolean IsUrgent;

    public DataOperationPrefix(Event e, Expression assignment, TCJProcess process, boolean localvar, boolean isUrgent) {
        this.Event = e;
        this.AssignmentExpr = assignment;
        this.Process = process;
        this.HasLocalVar = localvar;
        this.IsUrgent = isUrgent;
        this.ProcessID = TCJDataStore.DataManager.InitializeProcessID(
                this.Event.GetID() + "{" + this.AssignmentExpr.expressionID + "}"
                        + (this.IsUrgent ? ">>" : ">")
                        + this.Process.ProcessID);
    }

    @Override
    public List<ZoneConfiguration> MoveOneStep(ZoneConfiguration eStep) throws CloneNotSupportedException {
        List<ZoneConfiguration> list = new ArrayList<>();
        String eventID = this.Event.GetEventID(eStep.globalEnv);
        Valuation valuation = eStep.globalEnv.getVariableClone();
        EvaluatorDenotational.evaluate(this.AssignmentExpr, valuation);
        if (this.HasLocalVar) {
            Valuation variableClone = eStep.globalEnv.getVariableClone();
            for (int i = 0; i < variableClone.variables._entries.length; i++) {
                StringDictionaryEntryWithKey<ExpressionValue> stringDictionaryEntryWithKey = variableClone.variables._entries[i];
                if (stringDictionaryEntryWithKey != null) {
                    stringDictionaryEntryWithKey.value = valuation.variables.getContainsKey(stringDictionaryEntryWithKey.key);
                }
            }
            valuation = variableClone;
        }
        String eventName = this.Event.GetEventName(eStep.globalEnv);
        list.add(new ZoneConfiguration(this.Process, eventID, (eventID != eventName) ? eventName : null, valuation, true, this.IsUrgent ? eStep.DBM.addUrgency() : eStep.DBM));
        return list;
    }

    @Override
    public void GetDiscretEventTransitions(TickConfiguration eStep, List<TickConfiguration> toReturn) throws CloneNotSupportedException {

        String eventID = this.Event.GetEventID(eStep.globalEnv);
        Valuation valuation = eStep.globalEnv.getVariableClone();
        EvaluatorDenotational.evaluate(this.AssignmentExpr, valuation);
        if (this.HasLocalVar) {
            Valuation variableClone = eStep.globalEnv.getVariableClone();
            for (int i = 0; i < variableClone.variables._entries.length; i++) {
                StringDictionaryEntryWithKey<ExpressionValue> stringDictionaryEntryWithKey = variableClone.variables._entries[i];
                if (stringDictionaryEntryWithKey != null) {
                    stringDictionaryEntryWithKey.value = valuation.variables.getContainsKey(stringDictionaryEntryWithKey.key);
                }
            }
            valuation = variableClone;
        }
        String eventName = this.Event.GetEventName(eStep.globalEnv);
        toReturn.add(new TickConfiguration(this.Process, eventID, (eventID != eventName) ? eventName : null, valuation, true, this.IsUrgent || eventID == "τ"));
    }

    @Override
    public String toString() {
        return "(" + this.Event + "{" + this.AssignmentExpr + "}" + (this.IsUrgent ? "->>" : "->") + this.Process + ")";
    }

    @Override
    public HashSet<String> GetAlphabets(Map<String, String> visitedDefinitionRefs) {
        return this.Process.GetAlphabets(visitedDefinitionRefs);
    }

    @Override
    public List<String> GetGlobalVariables() {
        List<String> globalVariables = this.Process.GetGlobalVariables();
        Ultility.addList(globalVariables, this.AssignmentExpr.getVars());
        if (this.Event.ExpressionList != null) {
            for (Expression expression : this.Event.ExpressionList) {
                Ultility.addList(globalVariables, expression.getVars());
            }
        }
        return globalVariables;
    }

    @Override
    public List<String> GetChannels() {
        return this.Process.GetChannels();
    }

    @Override
    public TCJProcess ClearConstant(Map<String, Expression> constMapping) {
        Expression assignment = this.AssignmentExpr.clearConstant(constMapping);
        return new DataOperationPrefix(this.Event.ClearConstant(constMapping), assignment, this.Process.ClearConstant(constMapping), this.HasLocalVar, this.IsUrgent);
    }

    @Override
    public boolean MustBeAbstracted() {
        return this.Process.MustBeAbstracted();
    }
}
