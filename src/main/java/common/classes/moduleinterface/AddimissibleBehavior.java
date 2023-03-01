package common.classes.moduleinterface;

import java.util.List;

public class AddimissibleBehavior {
    public String Name;

    public List<String> VerificationEngines;

    public AddimissibleBehavior(String name, List<String> engines) {
        this.Name = name;
        this.VerificationEngines = engines;
    }
}
