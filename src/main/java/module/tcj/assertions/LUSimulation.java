package module.tcj.assertions;

import common.classes.datastructure.DBM;

import java.util.List;

public class LUSimulation extends ExtrapolationReachability {

    public LUSimulation(TCJAssertionReachability assertion)

    {
        super(assertion);
    }

    @Override
    protected void extrapolation(DBM dbm, List<List<Integer>> clocksBound) {

    }

    @Override
    protected boolean IsSubSet(DBM dbm1, DBM dbm2, List<List<Integer>> clocksBound) {
        return DBM.IsLUSimSubset(dbm1, dbm2, clocksBound);
    }

}
