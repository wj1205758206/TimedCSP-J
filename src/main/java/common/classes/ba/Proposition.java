package common.classes.ba;

public final class Proposition {

    public String Label;


    public boolean Negated;


    public String FullLabel;

    public Proposition(boolean bool_Renamed, boolean isNegated) {
        this(bool_Renamed ? "true" : "false", isNegated);
    }

    public Proposition(String label, boolean isNegated) {
        this.Label = label;
        this.Negated = isNegated;
        this.FullLabel = (this.Negated ? "!" : "") + this.Label;
    }

    @Override
    public String toString() {
        return this.FullLabel;
    }

    public boolean IsSigmal() {
        return this.Label == "Σ";
    }
}
