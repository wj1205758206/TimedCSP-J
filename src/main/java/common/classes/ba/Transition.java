package common.classes.ba;

import common.classes.ultility.Ultility;

import java.util.List;

public final class Transition {
    public String FromState;

    public String ToState;

    public List<Proposition> labels;

    public Transition(String from, String to)
    {
        this.FromState = from;
        this.ToState = to;
    }

    public Transition(List<Proposition> labels, String from, String to)
    {
        this.labels = labels;
        this.FromState = from;
        this.ToState = to;
    }

    @Override
    public String toString() {
        return this.FromState + " --(" + Ultility.PPStringList(this.labels) + ")--> " + this.ToState;
    }
}
