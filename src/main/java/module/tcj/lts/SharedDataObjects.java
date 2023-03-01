package module.tcj.lts;

import module.tcj.assertions.TCJDataStore;
import module.tcj.ultility.Ultility;

import java.util.HashMap;
import java.util.Map;

public final class SharedDataObjects extends SharedDataObjectBase {
    public TCJDataStore DataManager;


    public boolean TimedRefinementAssertion;


    public int TimedRefinementClockCeiling;


    public int TimedRefinementClockFloor;


    public Map<String, Integer> ChannelArrayDatabase;

    public SharedDataObjects() throws Exception {
        this.DataManager = new TCJDataStore();
        this.TimedRefinementAssertion = false;
        this.TimedRefinementClockCeiling = Ultility.TIME_REFINEMENT_CLOCK_CEILING;
        this.TimedRefinementClockFloor = Ultility.TIME_REFINEMENT_CLOCK_FLOOR;
        this.ChannelArrayDatabase = new HashMap<>();
    }

}
