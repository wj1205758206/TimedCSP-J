package common.classes.lts;

import common.classes.expressions.expressionclass.Expression;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EventCollection extends ArrayList<Event> {

    public List<String> EventNames;


    public String ProcessID;


    public EventCollection() {
    }

    public EventCollection(List<Event> events) {
        this.addAll(events);
        this.EventNames = new ArrayList<>(events.size());
//        this.EventNames = new List<string>(base.Count);
        StringBuilder stringBuilder = new StringBuilder("{");
        for (int i = 0; i < events.size(); i++) {
            String text;
            if (!events.get(i).ContainsVariable()) {
                text = events.get(i).GetEventID(null);
            } else {
                text = events.get(i).GetID();
            }
            stringBuilder.append(text);
            this.EventNames.add(text);
            if (i < events.size() - 1) {
                stringBuilder.append(",");
            }
        }
        stringBuilder.append("}");
        this.ProcessID = stringBuilder.toString();
    }

    public boolean HasExternalLibraryCall() {
        for (Event event : this) {
            if (event.hasExternalLibraryCall()) {
                return true;
            }
        }
        return false;
    }

    public boolean ContainEventName(String evt) {
        return this.EventNames.contains(evt);
    }

    public EventCollection ClearConstant(Map<String, Expression> constMapping) {
        List<Event> list = new ArrayList<>();
        for (int i = 0; i < this.size(); i++) {
            list.add(this.get(i).ClearConstant(constMapping));
        }
        return new EventCollection(list);
    }

    public boolean ContainsVariable() {
        for (Event event : this) {
            if (event.ContainsVariable()) {
                return true;
            }
        }
        return false;
    }
}
