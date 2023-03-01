package module.tcj.assertions;

import common.classes.datastructure.StringDictionary;
import module.tcj.lts.TCJProcess;

public class TCJDataStore {

    public static TCJDataStore DataManager;


    public StringDictionary<TCJProcess> DefinitionInstanceDatabase;


    public StringDictionary<String> ExpressionHashTable;


    public String LastProcessString;


    public TCJDataStore() throws Exception {
        this.ExpressionHashTable = new StringDictionary<String>(1048576); //Ultility.MC_INITIAL_SIZE
        this.DefinitionInstanceDatabase = new StringDictionary<TCJProcess>();
    }


    public void ClearVisitedTable() {
        this.LastProcessString = "";
    }


    public String InitializeProcessID(String key) {
        this.LastProcessString = key;
        String containsKey = this.ExpressionHashTable.getContainsKey(key);
        if (containsKey != null) {
            return containsKey;
        }
        String text = String.valueOf(this.ExpressionHashTable.count);
        this.ExpressionHashTable.add(key, text);
        return text;
    }

    public boolean SetLastProcessID(String id) {
        return this.ExpressionHashTable.setValue(this.LastProcessString, id);
    }
}
