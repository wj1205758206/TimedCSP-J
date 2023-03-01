package common.classes.datastructure;

import java.util.List;
import java.util.Map;

public class DBMInfo {

    public int[] clockMaxValues;


    public int[] clockLowerValues;


    public int[] clockUpperValues;


    public Map<String, Integer>[] localMax;


    public Map<String, Integer>[] localLower;


    public Map<String, Integer>[] localUpper;


    public Map<Integer, Integer> clockToProcessIndex;


    public int dim;


    public List<Short> clockIDs;

    public int getMax(int clockID, boolean isLocalBased, String[] currentLocation) {
        if (!isLocalBased) {
            return this.clockMaxValues[clockID];
        }
        if (clockID == 0) {
            return 0;
        }
        if (!this.clockToProcessIndex.containsKey(clockID)) {
            return this.clockMaxValues[clockID];
        }
        int num = this.clockToProcessIndex.get(clockID);
        return this.localMax[clockID].get(currentLocation[num]);

    }

    public int getMax(int clockID, boolean isLocalBased, String stateID) {
        if (!isLocalBased) {
            return this.clockMaxValues[clockID];
        }
        if (clockID == 0) {
            return 0;
        }
        return this.localMax[clockID].get(stateID);
    }

    public int getLower(int clockID, boolean isLocalBased, String[] currentLocation) {
        if (!isLocalBased) {
            return this.clockLowerValues[clockID];
        }
        if (clockID == 0) {
            return 0;
        }
        if (!this.clockToProcessIndex.containsKey(clockID)) {
            return this.clockLowerValues[clockID];
        }
        int num = this.clockToProcessIndex.get(clockID);
        return this.localLower[clockID].get(currentLocation[num]);

    }


    public int getLower(int clockID, boolean isLocalBased, String stateID) {
        if (!isLocalBased) {
            return this.clockLowerValues[clockID];
        }
        if (clockID == 0) {
            return 0;
        }
        return this.localLower[clockID].get(stateID);
    }


    public int getUpper(int clockID, boolean isLocalBased, String[] currentLocation) {
        if (!isLocalBased) {
            return this.clockUpperValues[clockID];
        }
        if (clockID == 0) {
            return 0;
        }
        if (!this.clockToProcessIndex.containsKey(clockID)) {
            return this.clockUpperValues[clockID];
        }
        int num = this.clockToProcessIndex.get(clockID);
        return this.localUpper[clockID].get(currentLocation[num]);

    }


    public int getUpper(int clockID, boolean isLocalBased, String stateID) {
        if (!isLocalBased) {
            return this.clockUpperValues[clockID];
        }
        if (clockID == 0) {
            return 0;
        }
        return this.localUpper[clockID].get(stateID);
    }

    public int getMaxMoreStates(short clockID, boolean isLocalBased, Map<String[], List<Short>> specStates, Map<Short, Short> mappingtoOrigin) {
        if (!isLocalBased) {
            return this.clockMaxValues[(int) mappingtoOrigin.get(clockID)];
        }
        int num = this.clockToProcessIndex.get((int) mappingtoOrigin.get(clockID));
        int num2 = 0;
        for (Map.Entry<String[], List<Short>> keyValuePair : specStates.entrySet()) {
            if (keyValuePair.getValue().contains(clockID)) {
                int num3 = this.localMax[(int) mappingtoOrigin.get(clockID)].get(keyValuePair.getKey()[num]);
                if (num3 > num2) {
                    num2 = num3;
                }
            }
        }
        return num2;
    }

}
