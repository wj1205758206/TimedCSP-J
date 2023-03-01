package common.classes.moduleinterface;

import java.util.ArrayList;
import java.util.List;

public class ModelCheckingOptions {
    public List<String> addimissibleBehaviorsNames;

    public List<AddimissibleBehavior> addimissibleBehaviors;

    public ModelCheckingOptions(){
        this.addimissibleBehaviors = new ArrayList<AddimissibleBehavior>();
        this.addimissibleBehaviorsNames = new ArrayList<String>();
    }

    public void addAddimissibleBehavior(String name, List<String> verificationEngines)
    {
        this.addimissibleBehaviors.add(new AddimissibleBehavior(name, verificationEngines));
        this.addimissibleBehaviorsNames.add(name);
    }

}
