package common.classes.datastructure;


import java.util.BitSet;

public final class StringDictionary<T> {
    public StringDictionary() throws Exception {
        this(0);
    }

    public StringDictionary(int capacity) throws Exception {
        this._capacity = ((capacity > 16) ? StringDictionary.getNextCapacity(capacity) : 16);
        this._capacityLess1 = this._capacity - 1;
        this._entries = new StringDictionaryEntry[this._capacity];
        this._spaceMap = new BitSet(this._capacity);
    }

    public void add(String key, T keyValue) {
        int num = 0;
        int num2 = 0;
        for (int i = 0; i < key.length(); i++) {
            num = ((num << 5) + num ^ (int) key.charAt(i));
            num2 = (num2 << 6) + (num2 << 16) - num2 + (int) key.charAt(i);
        }
        boolean flag = false;
        synchronized (this._syncRoot) {
            for (int j = 0; j < 31; j++) {
                int num3 = num + StringDictionary._primes[j] * num2;
                if (!this._spaceMap.get((int) ((long) num3 & (long) ((long) this._capacityLess1)))) {
                    this._entries[(int) ((long) num3 & (long) ((long) this._capacityLess1))] = new StringDictionaryEntry(num, num2, keyValue);
                    flag = true;
                    this._spaceMap.set((int) ((long) num3 & (long) ((long) this._capacityLess1)));
                    break;
                }
            }
            if (!flag) {
                while (!flag) {
                    this.resize();
                    for (int k = 0; k < 31; k++) {
                        int num3 = num + StringDictionary._primes[k] * num2;
                        if (!this._spaceMap.get((int) ((long) num3 & (long) ((long) this._capacityLess1)))) {
                            this._entries[(int) ((long) num3 & (long) ((long) this._capacityLess1))] = new StringDictionaryEntry<T>(num, num2, keyValue);
                            flag = true;
                            this._spaceMap.set((int) ((long) num3 & (long) ((long) this._capacityLess1)));
                            break;
                        }
                    }
                }
            }
            this.count++;
        }
    }

    public boolean containsKey(String key) {
        int num = 0;
        int num2 = 0;
        for (int i = 0; i < key.length(); i++) {
            num = ((num << 5) + num ^ (int) key.charAt(i));
            num2 = (num2 << 6) + (num2 << 16) - num2 + (int) key.charAt(i);
        }
        synchronized (this._syncRoot) {
            for (int j = 0; j < 31; j++) {
                int num3 = num + StringDictionary._primes[j] * num2;
                int num4 = (int) ((long) num3 & (long) ((long) this._capacityLess1));
                if (!this._spaceMap.get(num4)) {
                    return false;
                }
                if (this._entries[num4].hashA == num && this._entries[num4].hashB == num2) {
                    return true;
                }
            }
        }
        return false;
    }

    public T getContainsKey(String key) {
        int num = 0;
        int num2 = 0;
        for (int i = 0; i < key.length(); i++) {
            num = ((num << 5) + num ^ (int) key.charAt(i));
            num2 = (num2 << 6) + (num2 << 16) - num2 + (int) key.charAt(i);
        }
        synchronized (this._syncRoot) {
            for (int j = 0; j < 31; j++) {
                int num3 = num + StringDictionary._primes[j] * num2;
                int num4 = (int) ((long) num3 & (long) ((long) this._capacityLess1));
                if (!this._spaceMap.get(num4)) {
                    return null;
                }
                if (this._entries[num4].hashA == num && this._entries[num4].hashB == num2) {
                    return this._entries[num4].value;
                }
            }
        }
        return null;
    }

    public boolean setValue(String key, T value) {
        int num = 0;
        int num2 = 0;
        for (int i = 0; i < key.length(); i++) {
            num = ((num << 5) + num ^ (int) key.charAt(i));
            num2 = (num2 << 6) + (num2 << 16) - num2 + (int) key.charAt(i);
        }
        synchronized (this._syncRoot) {
            for (int j = 0; j < 31; j++) {
                int num3 = num + StringDictionary._primes[j] * num2;
                int num4 = (int) ((long) num3 & (long) ((long) this._capacityLess1));
                if (!this._spaceMap.get(num4)) {
                    break;
                }
                if (this._entries[num4].hashA == num && this._entries[num4].hashB == num2) {
                    this._entries[num4].value = value;
                    return true;
                }
            }
        }
        return false;
    }

    public void clear() {
        this._entries = new StringDictionaryEntry[this._capacity];
        this._spaceMap.set(0, _spaceMap.size(), false);
        this.count = 0;
    }

    private void resize() {
        StringDictionaryEntry<T>[] array;
        BitSet bitArray;
        for (; ; ) {
            IL_00:
            this._capacity <<= 1;
            this._capacityLess1 = this._capacity - 1;
            array = new StringDictionaryEntry[this._capacity];
            bitArray = new BitSet(this._capacity);
            boolean flag = false;
            for (int i = 0; i < this._entries.length; i++) {
                if (this._spaceMap.get(i)) {
                    for (int j = 0; j < 31; j++) {
                        int num = this._entries[i].hashA + StringDictionary._primes[j] * this._entries[i].hashB;
                        if (!bitArray.get((int) ((long) num & (long) ((long) this._capacityLess1)))) {
                            array[(int) (((long) num & (long) ((long) this._capacityLess1)))] = this._entries[i];
                            bitArray.set((int) ((long) num & (long) ((long) this._capacityLess1)));
                            flag = true;
                            break;
                        }
                    }
                    if (!flag) {
                        break;
                    }
                }
            }
            if (flag) {
                break;
            }
        }
        this._entries = array;
        this._spaceMap = bitArray;
    }

    private static int getNextCapacity(int currentCapacity) throws Exception {
        for (int i = 10; i < 31; i++) {
            if (2 << i >= currentCapacity) {
                return 2 << i;
            }
        }
        throw new Exception("Capacity could not be found for currentCapacity: ");
    }

    private final int _nmHashes = 31;


    private final int _minCapacity = 16;


    private Object _syncRoot = new Object();


    public int count;


    public
    int extraConter;


    public StringDictionaryEntry<T>[] _entries;


    private BitSet _spaceMap;


    private int _capacity;


    private int _capacityLess1;


    private static int[] _primes = new int[]
            {
                    2,
                    3,
                    5,
                    7,
                    11,
                    13,
                    17,
                    19,
                    23,
                    29,
                    31,
                    37,
                    41,
                    43,
                    47,
                    53,
                    59,
                    61,
                    67,
                    71,
                    73,
                    79,
                    83,
                    89,
                    97,
                    101,
                    103,
                    107,
                    109,
                    113,
                    127,
                    131,
                    137,
                    139,
                    149,
                    151,
                    157,
                    163,
                    167,
                    173,
                    179,
                    181,
                    191,
                    193,
                    197,
                    199,
                    211,
                    223,
                    227,
                    229,
                    233,
                    239,
                    241,
                    251,
                    257,
                    263,
                    269,
                    271,
                    277,
                    281,
                    283,
                    293,
                    307,
                    311,
                    313,
                    317,
                    331,
                    337,
                    347,
                    349,
                    353,
                    359,
                    367,
                    373,
                    379,
                    383,
                    389,
                    397,
                    401,
                    409,
                    419,
                    421,
                    431,
                    433,
                    439,
                    443,
                    449,
                    457,
                    461,
                    463,
                    467,
                    479,
                    487,
                    491,
                    499,
                    503,
                    509,
                    521,
                    523,
                    541,
                    547,
                    557,
                    563,
                    569,
                    571,
                    577,
                    587,
                    593,
                    599,
                    601,
                    607,
                    613,
                    617,
                    619,
                    631,
                    641,
                    643,
                    647,
                    653,
                    659,
                    661,
                    673,
                    677,
                    683,
                    691,
                    701,
                    709,
                    719,
                    727,
                    733,
                    739,
                    743,
                    751,
                    757,
                    761,
                    769,
                    773,
                    787,
                    797,
                    809,
                    811,
                    821,
                    823,
                    827,
                    829,
                    839,
                    853,
                    857,
                    859,
                    863,
                    877,
                    881,
                    883,
                    887,
                    907,
                    911,
                    919,
                    929,
                    937,
                    941,
                    947,
                    953,
                    967,
                    971,
                    977,
                    983,
                    991,
                    997,
                    1009,
                    1013,
                    1019,
                    1021,
                    1031,
                    1033,
                    1039,
                    1049,
                    1051,
                    1061,
                    1063,
                    1069,
                    1087,
                    1091,
                    1093,
                    1097,
                    1103,
                    1109,
                    1117,
                    1123,
                    1129,
                    1151,
                    1153,
                    1163,
                    1171,
                    1181,
                    1187,
                    1193,
                    1201,
                    1213,
                    1217,
                    1223,
                    1229,
                    1231,
                    1237,
                    1249,
                    1259,
                    1277,
                    1279,
                    1283,
                    1289,
                    1291,
                    1297,
                    1301,
                    1303,
                    1307,
                    1319,
                    1321,
                    1327,
                    1361,
                    1367,
                    1373,
                    1381,
                    1399,
                    1409,
                    1423,
                    1427,
                    1429,
                    1433,
                    1439,
                    1447,
                    1451,
                    1453,
                    1459,
                    1471,
                    1481,
                    1483,
                    1487,
                    1489,
                    1493,
                    1499,
                    1511,
                    1523,
                    1531,
                    1543,
                    1549,
                    1553,
                    1559,
                    1567,
                    1571,
                    1579,
                    1583,
                    1597,
                    1601,
                    1607,
                    1609,
                    1613,
                    1619,
                    1621,
                    1627,
                    1637,
                    1657,
                    1663,
                    1667,
                    1669,
                    1693,
                    1697,
                    1699,
                    1709,
                    1721,
                    1723,
                    1733,
                    1741,
                    1747,
                    1753,
                    1759,
                    1777,
                    1783,
                    1787,
                    1789,
                    1801,
                    1811,
                    1823,
                    1831,
                    1847,
                    1861,
                    1867,
                    1871,
                    1873,
                    1877,
                    1879,
                    1889,
                    1901,
                    1907,
                    1913,
                    1931,
                    1933,
                    1949,
                    1951,
                    1973,
                    1979,
                    1987,
                    1993,
                    1997,
                    1999,
                    2003,
                    2011,
                    2017,
                    2027,
                    2029
            };
}
