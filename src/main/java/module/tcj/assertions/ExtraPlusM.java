package module.tcj.assertions;

import common.classes.datastructure.DBM;

import java.util.List;

public class ExtraPlusM extends ExtrapolationReachability {
    public ExtraPlusM(TCJAssertionReachability assertion) {
        super(assertion);
    }

    @Override
    protected void extrapolation(DBM dbm, List<List<Integer>> clocksBound) {
        dbm.extraPlusM(clocksBound);
    }

    @Override
    protected boolean IsSubSet(DBM dbm1, DBM dbm2, List<List<Integer>> clocksBound) {
        return DBM.isSubSet(dbm1, dbm2);
    }

}
