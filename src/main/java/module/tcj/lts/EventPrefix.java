package module.tcj.lts;

import common.classes.expressions.expressionclass.Expression;
import common.classes.lts.Event;
import common.classes.ultility.Ultility;
import module.tcj.assertions.TCJDataStore;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public final class EventPrefix extends TCJProcess {
    public boolean IsUrgent;

    public Event Event;


    public TCJProcess Process;

    public EventPrefix(Event e, TCJProcess process, boolean isUrgent) {
        this.Event = e;
        this.Process = process;
        this.IsUrgent = isUrgent;
        this.ProcessID = TCJDataStore.DataManager.InitializeProcessID(this.Event.GetID() + (this.IsUrgent ? ">>" : ">") + this.Process.ProcessID);
    }

    @Override
    public List<ZoneConfiguration> MoveOneStep(ZoneConfiguration eStep) throws CloneNotSupportedException {
        List<ZoneConfiguration> list = new ArrayList<>(1);
        String eventID = this.Event.GetEventID(eStep.globalEnv);
        String eventName = this.Event.GetEventName(eStep.globalEnv);
        list.add(new ZoneConfiguration(this.Process, eventID, (eventID != eventName) ? eventName : null, eStep.globalEnv, false, this.IsUrgent ? eStep.DBM.addUrgency() : eStep.DBM));
        return list;
    }

    @Override
    public void GetDiscretEventTransitions(TickConfiguration eStep, List<TickConfiguration> toReturn) throws CloneNotSupportedException {
        String eventID = this.Event.GetEventID(eStep.globalEnv);
        String eventName = this.Event.GetEventName(eStep.globalEnv);
        toReturn.add(new TickConfiguration(this.Process, eventID, (eventID != eventName) ? eventName : null, eStep.globalEnv, false, this.IsUrgent || eventID == "τ"));

    }

    @Override
    public String toString() {
        return "(" + this.Event + (this.IsUrgent ? "->>" : "->") + this.ProcessID.toString();
    }

    @Override
    public HashSet<String> GetAlphabets(Map<String, String> visitedDefinitionRefs) {
        HashSet<String> alphabets = this.Process.GetAlphabets(visitedDefinitionRefs);
        if (this.Event.ExpressionList != null) {
            for (Expression expression : this.Event.ExpressionList) {
                if (expression.hasVar) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("ERROR - PAT FAILED to calculate the alphabet." + "\n");
                    stringBuilder.append("CAUSE - Event " + this.Event + " contains global variables!" + "\n");
                    stringBuilder.append("REMEDY - 1) Avoid using global variables in events 2) Or manually specify the alphabet of the relevant process using the following syntax: \n\r\t #alphabet someProcess {X}; \n\rwhere X is a set of event names." + "\n");
                    throw new RuntimeException(stringBuilder.toString());
                }
            }
        }
        String eventID = this.Event.GetEventID(null);
        if (eventID != "τ" && !alphabets.contains(eventID)) {
            alphabets.add(eventID);
        }
        return alphabets;
    }

    @Override
    public List<String> GetGlobalVariables() {
        List<String> globalVariables = this.Process.GetGlobalVariables();
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
        return new EventPrefix(this.Event.ClearConstant(constMapping), this.Process.ClearConstant(constMapping), this.IsUrgent);
    }

    @Override
    public boolean MustBeAbstracted() {
        return this.Process.MustBeAbstracted();
    }
}
