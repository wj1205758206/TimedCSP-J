package common.classes.datastructure;

public final class StringDictionaryEntry<T> {

    public int hashA;


    public int hashB;


    public T value;

    public StringDictionaryEntry(int hashA, int hashB, T val)
    {
        this.hashA = hashA;
        this.hashB = hashB;
        this.value = val;
    }
}
