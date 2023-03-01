package common.classes.ultility;

import common.classes.expressions.expressionclass.Expression;
import common.classes.expressions.expressionclass.ExpressionValue;
import common.classes.moduleinterface.SpecificationBase;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Ultility {
    public static int MC_INITIAL_SIZE = 1048576;
    public static int CutNumber = 2;
    public static Object ShareDataLock;
    public static int DEFAULT_PRECISION = 5;
    public static int PARALLEL_MODEL_CHECKIMG_BOUND = 16;

    public static <T> List<T> Intersect(List<T> list1, List<T> list2) {
        List<T> list3 = new ArrayList<>(list1.size());
        for (int i = 0; i < list2.size(); i++) {
            T item = list2.get(i);
            if (list1.contains(item)) {
                list3.add(item);
            }
        }
        return list3;
    }

    public static int ProcessCounterIncrement(int cutNumber, int counter, int increment) {
        if (counter == -1) {
            return -1;
        }
        if (cutNumber == -1 || cutNumber >= counter + increment) {
            return counter + increment;
        }
        return -1;
    }

    public static List<Map<String, Integer>> ProcessCounterDecrement(int cutNumber, Map<String, Integer> counters, String index, int stepSize) {
        List<Map<String, Integer>> list = new ArrayList<>();
        Map<String, Integer> dictionary = new HashMap<>(counters);
        if (counters.get(index) == -1) {
            list.add(dictionary);
            if (cutNumber != -1) {
                Map<String, Integer> dictionary2 = new HashMap<>(counters);
                dictionary2.put(index, cutNumber + 1 - stepSize);
                list.add(dictionary2);
            }
        } else {
            int value = counters.get(index) - stepSize;
            dictionary.put(index, value);
            list.add(dictionary);
        }
        return list;
    }

    public static <T> List<T> Union(List<T> list1, List<T> list2) {
        for (int i = 0; i < list2.size(); i++) {
            T item = list2.get(i);
            if (!list1.contains(item)) {
                list1.add(item);
            }
        }
        return list1;
    }

    public static <T> String PPStringListDot(T[] list) {
        if (list == null) {
            return "";
        }
        String text = "";
        for (T t : list) {
            text = text + "." + t;
        }
        return text;
    }

    public static <T> void addList(List<T> list1, List<T> list2) {
        for (T item : list2) {
            if (!list1.contains(item)) {
                list1.add(item);
            }
        }
    }

    public static <T> String PPStringList(List<T> list) {
        if (list == null) {
            return "";
        }
        String text = "";
        for (int i = 0; i < list.size(); i++) {
            T t = list.get(i);
            text = text + "," + t;
        }
        return text.replaceFirst("^\\,", "");
//        return text.TrimStart(new char[]
//                {
//                        ','
//                });
    }

    public static <T> String pPStringList(T[] list) {
        if (list == null) {
            return "";
        }
        String text = "";
        for (T t : list) {
            if (t != null) {
                text = text + "," + t.toString();
            }
        }
        return text.replaceFirst(",", "");
    }

    public static String pPIDListDot(Expression[] list) {
        if (list == null) {
            return "";
        }
        String text = "";
        for (Expression expression : list) {
            text = text + "." + expression.expressionID;
        }
        return text;
    }

    public static void lockSharedData(SpecificationBase specification) {
        if (Ultility.ShareDataLock != null && Ultility.ShareDataLock.toString() == "True") {
            synchronized (Ultility.ShareDataLock) {
                Ultility.ShareDataLock = specification;
                specification.lockSpecificationData();
            }
        }
    }

    public static boolean unLockSharedData(SpecificationBase specification) {
        if (Ultility.ShareDataLock != null && Ultility.ShareDataLock == specification) {
            synchronized (Ultility.ShareDataLock) {
                specification.unLockSpecificationData();
                Ultility.ShareDataLock = null;
            }
            return true;
        }
        return false;
    }

    public static String getProbIntervalString(double min) {
        int length = String.valueOf((1.0 * Math.pow(10.0, (double) (-1 * (Ultility.DEFAULT_PRECISION + 1))))).length();
        BigDecimal bigDecimal = new BigDecimal(min);
        bigDecimal.setScale(length, BigDecimal.ROUND_HALF_UP);
        return bigDecimal.toString();
    }

    public static String getProbIntervalString(double min, double max) {
        int length = String.valueOf((1.0 * Math.pow(10.0, (double) (-1 * (Ultility.DEFAULT_PRECISION + 1))))).length();
        BigDecimal minDecimal = new BigDecimal(min);
        BigDecimal maxDecimal = new BigDecimal(max);
        minDecimal.setScale(length, BigDecimal.ROUND_HALF_UP);
        maxDecimal.setScale(length, BigDecimal.ROUND_HALF_UP);
        return "[" + minDecimal.toString() + "," + maxDecimal.toString() + "]";
    }

    public static float roundProbWithPrecision(double prob) {
        int length = String.valueOf((1f * (float) Math.pow(10.0, (double) (-1 * (Ultility.DEFAULT_PRECISION + 1))))).length();
        BigDecimal bigDecimal = new BigDecimal(prob);
        bigDecimal.setScale(length, RoundingMode.HALF_UP);
        return bigDecimal.floatValue();
    }

    public static float roundProbWithPrecision(double prob, int precisionDigits) {
        int length = String.valueOf((1f * (float) Math.pow(10.0, (double) (-1 * (precisionDigits + 1))))).length();
        BigDecimal bigDecimal = new BigDecimal(prob);
        bigDecimal.setScale(length, RoundingMode.HALF_UP);
        return bigDecimal.floatValue();
    }

    public static int[][] floydAlgorithm(int n, int[][] weights) {
        int[][] array = new int[n][n];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                array[i][j] = weights[i][j];

            }
        }
        for (int k = 0; k < n; k++) {
            for (int l = 0; l < n; l++) {
                for (int m = 0; m < n; m++) {
                    if (Ultility.sumWithOverFlowChecking(array[l][k], array[k][m]) < array[l][m]) {
                        array[l][m] = Ultility.sumWithOverFlowChecking(array[l][k], array[k][m]);
                    }
                }
            }
        }
        return array;
    }

    private static int sumWithOverFlowChecking(int a, int b) {
        if (a != 2147483647 && b != 2147483647) {
            return a + b;
        }
        return Integer.MAX_VALUE;
    }

    public static boolean grabSharedDataLock() {
        if (Ultility.ShareDataLock == null) {
            Ultility.ShareDataLock = true;
            return true;
        }
        return false;
    }
}
