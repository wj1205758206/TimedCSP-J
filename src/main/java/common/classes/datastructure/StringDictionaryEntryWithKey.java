package common.classes.datastructure;

public final class StringDictionaryEntryWithKey<T> {
    public StringDictionaryEntryWithKey(int hashA, int hashB, T val, String key) {
        this.hashA = hashA;
        this.hashB = hashB;
        this.value = val;
        this.key = key;
    }


    public int hashA;


    public int hashB;


    public T value;


    public String key;
}
