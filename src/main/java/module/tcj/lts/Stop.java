package module.tcj.lts;

import common.classes.expressions.expressionclass.Expression;
import common.classes.ultility.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class Stop extends TCJProcess {

    public Stop() {
        ProcessID = Constants.STOP;
    }

    @Override
    public List<ZoneConfiguration> MoveOneStep(ZoneConfiguration eStep) {
        return new ArrayList<ZoneConfiguration>();
    }

    @Override
    public String toString() {
        return "Stop";
    }

    @Override
    public TCJProcess ClearConstant(Map<String, Expression> constMapping) {
        return this;
    }

    @Override
    public void GetDiscretEventTransitions(TickConfiguration eStep, List<TickConfiguration> toReturn) {

    }
}
