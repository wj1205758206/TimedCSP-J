package common.classes.datastructure;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class DBM {

    public int[] matrix;

    private String id;

    public DBM() {
        this.matrix = new int[1];
    }

    public DBM(int[] matrix) {
        this.matrix = matrix;
    }

    public int getDim() {

        return (int) Math.sqrt((double) this.matrix.length);
    }

    public static boolean IsLUSimSubset(DBM z1, DBM z2, List<List<Integer>> clocksBound) {
        if (z1.getDim() != z2.getDim()) {
            return false;
        }
        int dim = z1.getDim();
        for (int i = 0; i < dim; i++) {
            int num = -DBM.getUpper(i, clocksBound);
            if (num <= z1.matrix[i]) {
                for (int j = 0; j < dim; j++) {
                    if (i != j) {
                        int num2 = j * dim + i;
                        if (z2.matrix[num2] < z1.matrix[num2] && DBM.IsSumLessThan(z2.matrix[num2], -DBM.getLower(j, clocksBound), z1.matrix[i])) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    private static boolean IsSumLessThan(int value1, int value2, int value) {
        return value1 != Integer.MAX_VALUE && value2 != Integer.MAX_VALUE && value1 + value2 < value;
    }

    public static boolean IsSubSet(DBM dbm1, DBM dbm2) {
        if (dbm1.getDim() != dbm2.getDim()) {
            return false;
        }
        int dim = dbm1.getDim();
        for (int i = 0; i < dim; i++) {
            for (int j = 0; j < dim; j++) {
                int num = i * dim + j;
                if (i != j && dbm1.matrix[num] > dbm2.matrix[num]) {
                    return false;
                }
            }
        }
        return true;
    }

    public void addTimer(int timerID) {
        int dim = this.getDim();
        int num = dim + 1;
        int[] array = new int[num * num];
        for (int i = 0; i < num; i++) {
            for (int j = 0; j < num; j++) {
                if (i < dim && j < dim) {
                    array[i * num + j] = this.matrix[i * dim + j];
                } else if ((i == 0 || i == num - 1) && j == num - 1) {
                    array[i * num + j] = 0;
                } else if (i >= 1 && i <= dim - 1 && j == num - 1) {
                    array[i * num + j] = this.matrix[i * dim];
                } else {
                    array[i * num + j] = this.matrix[j];
                }
            }
        }
        this.matrix = array;
    }

    public DBM addUrgency() {
        int timer = this.getDim() - 1;
        DBM dbm = this.clone();
        dbm.addConstraint(timer, TimerOperationType.LessThanOrEqualTo, 0);
        return dbm;
    }

    public boolean isUrgent() {
        int timerID = this.getDim() - 1;
        return this.getTimerUpper(timerID) == 0;
    }

    public static DBM returnUrgentDBM(DBM dbm1, DBM dbm2) {
        if (dbm1.isUrgent()) {
            return dbm1;
        }
        return dbm2;
    }

    public DBM cleanAndRename(Map<Integer, Integer> mapping, int clockCounter) {
        int num = clockCounter + 1;
        int[] array = (clockCounter == 0) ? new int[1] : new int[num * num];
        List<Integer> list = new ArrayList<>();
        for (int i = 1; i <= clockCounter; i++) {
            if (!mapping.containsValue(i)) {
                list.add(i);
            }
        }
        int dim = this.getDim();
        for (Map.Entry<Integer, Integer> pair : mapping.entrySet()) {
            array[pair.getValue()] = this.matrix[pair.getKey()];
            array[pair.getValue() * num] = this.matrix[pair.getKey() * dim];
            for (Integer num2 : list) {
                array[num2 * num + pair.getValue()] = this.matrix[pair.getKey()];
                array[pair.getValue() * num + num2] = this.matrix[pair.getKey() * dim];
            }
            for (Map.Entry<Integer, Integer> pair2 : mapping.entrySet()) {
                array[pair2.getValue() * num + pair.getValue()] = this.matrix[pair2.getKey() * dim + pair.getKey()];
                array[pair2.getValue() * num + pair2.getValue()] = this.matrix[pair.getKey() * dim + pair2.getKey()];
            }
        }
        return new DBM(array);
    }

    private void setValue(int index, int value) {
        this.matrix[index] = value;
    }

    public int getTimerUpper(int timerID) {
        int dim = this.getDim();
        return this.matrix[timerID * dim];
    }

    public int getTimerLower(int timerID) {
        return -this.matrix[timerID];
    }

    public void reset(int x) {
        int dim = this.getDim();
        for (int i = 0; i < dim; i++) {
            this.plus(x * dim + i, 0, this.matrix[i]);
            this.plus(i * dim + x, this.matrix[i * dim], 0);
        }
    }

    public void extraPlusM(List<List<Integer>> clocksBound) {
        if (this.matrix[0] >= 0) {
            int dim = this.getDim();
            int[] array = new int[dim];
            for (int i = 0; i < dim; i++) {
                array[i] = DBM.GetMax(i, clocksBound);
            }
            int[] newMatrix = new int[dim * dim];
            for (int j = 0; j < dim; j++) {
                for (int k = 0; k < dim; k++) {
                    if (k != j) {
                        int num = j * dim + k;
                        if (this.matrix[num] > array[j]) {
                            this.setElementIgnored(newMatrix, num);
                        } else if (-this.matrix[j] > array[j]) {
                            this.setElementIgnored(newMatrix, num);
                        } else if (-this.matrix[k] > array[k]) {
                            if (j != 0) {
                                this.setElementIgnored(newMatrix, num);
                            } else if (array[k] < 0) {
                                this.setValue(newMatrix, num, 0);
                            } else {
                                this.setValue(newMatrix, num, -DBM.GetMax(k, clocksBound));
                            }
                        } else {
                            this.setValue(newMatrix, num, this.matrix[num]);
                        }
                    }
                }
            }
            this.matrix = newMatrix;
        }
    }

    private boolean hasNegativeCycle(int clock) {
        int dim = this.getDim();
        return this.matrix[clock * dim + clock] < 0;
    }

    public void addConstraint(int timer, TimerOperationType op, int constant) {
        switch (op) {
            case Equals:
                this.AddConstraintXY(timer, 0, constant);
                this.AddConstraintXY(0, timer, -constant);
                return;
            case LessThanOrEqualTo:
                this.AddConstraintXY(timer, 0, constant);
                return;
            case GreaterThanOrEqualTo:
                this.AddConstraintXY(0, timer, -constant);
                return;
            case LessThan:
                this.AddConstraintXY(timer, 0, constant);
                return;
            case GreaterThan:
                this.AddConstraintXY(0, timer, -constant);
                return;
            default:
                return;
        }
    }

    private void AddConstraintXY(int x, int y, int value) {
        int dim = this.getDim();
        if (DBM.isSumLessThan(this.matrix[y * dim + x], value, 0)) {
            this.setDBMEmpty();
            return;
        }
        if (value < this.matrix[x * dim + y]) {
            this.setValue(x * dim + y, value);
            for (int i = 0; i < dim; i++) {
                for (int j = 0; j < dim; j++) {
                    this.plusMin(i * dim + j, i * dim + x, x * dim + j);
                    this.plusMin(i * dim + j, i * dim + y, y * dim + j);
                    if (i == j && this.hasNegativeCycle(i)) {
                        this.setDBMEmpty();
                        return;
                    }
                }
            }
        }
    }

    public void up() {
        int dim = this.getDim();
        for (int i = 1; i < dim; i++) {
            this.setElementIgnored(i * dim);
        }
    }

    public boolean isEmpty() {
        return this.hasNegativeCycle(0);
    }

    public static boolean isSubSet(DBM dbm1, DBM dbm2) {
        if (dbm1.getDim() != dbm2.getDim()) {
            return false;
        }
        int dim = dbm1.getDim();
        for (int i = 0; i < dim; i++) {
            for (int j = 0; j < dim; j++) {
                int num = i * dim + j;
                if (i != j && dbm1.matrix[num] > dbm2.matrix[num]) {
                    return false;
                }
            }
        }
        return true;
    }

    public String toString(Map<String, Short> clockMapping) {
        StringBuilder stringBuilder = new StringBuilder();
        int dim = this.getDim();
        for (Map.Entry<String, Short> keyValuePair : clockMapping.entrySet()) {
            String key = keyValuePair.getKey();
            int value = (int) keyValuePair.getValue();
            if (!this.isElementIgnored(value * dim)) {
                stringBuilder.append(key + this.matrix[value * dim]);
            }
            if (!this.isElementIgnored(value)) {
                stringBuilder.append(key + this.negationElementToString(value));
            }
            for (Map.Entry<String, Short> keyValuePair2 : clockMapping.entrySet()) {
                String key2 = keyValuePair2.getKey();
                int value2 = (int) keyValuePair2.getValue();
                if (value != value2) {
                    int num = value * dim + value2;
                    if (!this.isElementIgnored(value * dim + value2)) {
                        if (this.matrix[num] > 0) {
                            stringBuilder.append(key + "-" + key2 + this.elementToString(num));
                        } else if (this.matrix[num] == 0) {
                            stringBuilder.append(key + "=" + key2);
                        } else {
                            stringBuilder.append(key2 + "-" + key + this.negationElementToString(num));
                        }
                    }
                }
            }
        }
        return stringBuilder.toString();
    }

    public DBM clone() {
        int[] array = new int[this.matrix.length];
        for (int i = 0; i < this.matrix.length; i++) {
            array[i] = this.matrix[i];
        }
        return new DBM(array);
    }

    public String getID() {
        if (this.id == null) {
            StringBuilder stringBuilder = new StringBuilder();
            int dim = this.getDim();
            for (int i = 0; i < dim; i++) {
                for (int j = 0; j < dim; j++) {
                    if (i != j) {
                        int num = i * dim + j;
                        stringBuilder.append((this.isElementIgnored(num) ? "INF" : this.matrix[num]) + "$");
                    }
                }
            }
            this.id = stringBuilder.toString();
        }
        return this.id;
    }

    public static boolean isLUSimSubset(DBM z1, DBM z2, List<List<Integer>> clocksBound) {
        if (z1.getDim() != z2.getDim()) {
            return false;
        }
        int dim = z1.getDim();
        for (int i = 0; i < dim; i++) {
            int num = -DBM.getUpper(i, clocksBound);
            if (num <= z1.matrix[i]) {
                for (int j = 0; j < dim; j++) {
                    if (i != j) {
                        int num2 = j * dim + i;
                        if (z2.matrix[num2] < z1.matrix[num2] && DBM.isSumLessThan(z2.matrix[num2], -DBM.getLower(j, clocksBound), z1.matrix[i])) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    public static boolean IsLUSimulated(int[] valuation1, int[] valuation2, String currentState, DBMInfo dbmInfo) {
        for (int i = 0; i < valuation1.length; i++) {
            if (!DBM.isLUSimulated(valuation1[i], valuation2[i], dbmInfo.getLower((int) dbmInfo.clockIDs.get(i), true, currentState), dbmInfo.getUpper((int) dbmInfo.clockIDs.get(i), true, currentState))) {
                return false;
            }
        }
        return true;
    }

    private static boolean isLUSimulated(int clockValue1, int clockValue2, int clockLowerbound, int clockUpperBound) {
        return clockValue1 == clockValue2 || (clockLowerbound < clockValue2 && clockValue2 < clockValue1) || (clockUpperBound < clockValue1 && clockValue1 < clockValue2);
    }

    private static int GetMax(int clockID, List<List<Integer>> clocksBound) {
        if (clockID == 0) {
            return 0;
        }
        return clocksBound.get(clockID - 1).get(0);

    }


    private static int getLower(int clockID, List<List<Integer>> clocksBound) {
        if (clockID == 0) {
            return 0;
        }
        return clocksBound.get(clockID - 1).get(1);
    }


    private static int getUpper(int clockID, List<List<Integer>> clocksBound) {
        if (clockID == 0) {
            return 0;
        }
        return clocksBound.get(clockID - 1).get(2);
    }


    private static boolean isSumLessThan(int value1, int value2, int value) {
        return value1 != Integer.MAX_VALUE && value2 != Integer.MAX_VALUE && value1 + value2 < value;
    }


    private void plus(int index, int value1, int value2) {
        if (value1 == 2147483647 || value2 == 2147483647) {
            this.setElementIgnored(index);
            return;
        }
        this.setValue(index, value1 + value2);
    }

    private void plusMin(int index, int index1, int index2) {
        if (this.matrix[index1] != 2147483647 && this.matrix[index2] != 2147483647 && this.matrix[index1] + this.matrix[index2] < this.matrix[index]) {
            this.setValue(index, this.matrix[index1] + this.matrix[index2]);
        }
    }


    private void setElementIgnored(int index) {
        this.matrix[index] = Integer.MAX_VALUE;
    }


    private void setElementIgnored(int[] newMatrix, int index) {
        newMatrix[index] = Integer.MAX_VALUE;
    }


    private void setValue(int[] newMatrix, int index, int value) {
        newMatrix[index] = value;
    }


    private boolean isElementIgnored(int index) {
        return this.matrix[index] == Integer.MAX_VALUE;
    }


    private void setDBMEmpty() {
        this.matrix[0] = -1;
    }


    public String elementToString(int index) {
        return String.valueOf(this.matrix[index]);
    }


    public String negationElementToString(int index) {
        return String.valueOf(-this.matrix[index]);
    }
}
