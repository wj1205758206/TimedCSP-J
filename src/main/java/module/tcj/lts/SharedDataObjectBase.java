package module.tcj.lts;

import common.classes.datastructure.StringDictionary;
import common.classes.datastructure.StringHashTable;
import common.classes.expressions.Valuation;
import common.classes.moduleinterface.SpecificationBase;
import common.ultility.Ultility;


import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SharedDataObjectBase {
    public StringDictionary<Integer> VariableLowerBound;


    public StringDictionary<Integer> VariableUpperLowerBound;


    public StringDictionary<String> ValutionHashTable;


    public List<String> SyncrhonousChannelNames;


    public boolean HasSyncrhonousChannel;

    public Map<String, Method> JavaMethods;

    public Map<String, Type> JavaDataType;


    public boolean HasAtomicEvent;


    public boolean HasJavaCode;


    public StringHashTable LocalVars;
    public SharedDataObjectBase()  {
        try {
            this.VariableLowerBound = new StringDictionary<Integer>(8);
            this.VariableUpperLowerBound = new StringDictionary<Integer>(8);
            this.ValutionHashTable = new StringDictionary<String>(common.classes.ultility.Ultility.MC_INITIAL_SIZE);
            this.SyncrhonousChannelNames = new ArrayList<>();
            this.HasSyncrhonousChannel = false;
            this.HasAtomicEvent = false;
            this.JavaMethods =  new HashMap<>();
            this.JavaDataType =  new HashMap<>();
            this.LocalVars = null;
        }catch (Exception e){
        }
    }
    public void LockSpecificationData() {
        Valuation.variableLowerBound = this.VariableLowerBound;
        Valuation.variableUpperLowerBound = this.VariableUpperLowerBound;
        Valuation.valutionHashTable = this.ValutionHashTable;
        Valuation.hiddenVars = this.LocalVars;
        Valuation.hasVariableConstraints = (Valuation.variableLowerBound.count > 0 || Valuation.variableUpperLowerBound.count > 0);
        SpecificationBase.hasSyncrhonousChannel = this.HasSyncrhonousChannel;
        SpecificationBase.syncrhonousChannelNames = this.SyncrhonousChannelNames;
        SpecificationBase.hasAtomicEvent = this.HasAtomicEvent;
        SpecificationBase.hasJavaCode = this.HasJavaCode;
        Ultility.javaMethods = this.JavaMethods;
        Ultility.javaDataType = this.JavaDataType;
    }
}
