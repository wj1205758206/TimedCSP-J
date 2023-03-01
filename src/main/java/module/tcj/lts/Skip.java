package module.tcj.lts;

import common.classes.expressions.expressionclass.Expression;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class Skip extends TCJProcess {

    public Skip() {
        this.ProcessID = "~";
    }

    @Override
    public List<ZoneConfiguration> MoveOneStep(ZoneConfiguration eStep) {

        List<ZoneConfiguration> list = new ArrayList<>();
        list.add(new ZoneConfiguration(new Stop(), "terminate", null, eStep.globalEnv, false, eStep.DBM));
        return list;
    }

    @Override
    public void GetDiscretEventTransitions(TickConfiguration eStep, List<TickConfiguration> toReturn) {
        toReturn.add(new TickConfiguration(new Stop(), "terminate", null, eStep.globalEnv, false, false));
    }

    @Override
    public String toString() {
        return "Skip";
    }

    @Override
    public boolean IsSkip() {
        return true;
    }

    @Override
    public TCJProcess ClearConstant(Map<String, Expression> constMapping) {
        return this;
    }
}
