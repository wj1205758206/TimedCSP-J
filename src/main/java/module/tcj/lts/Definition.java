package module.tcj.lts;

import common.ParsingException;
import common.classes.datastructure.StringDictionary;
import common.classes.expressions.expressionclass.Expression;
import common.classes.lts.EventCollection;
import common.classes.ultility.Ultility;
import org.antlr.v4.runtime.Token;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public final class Definition {
    public String Name;

    public TCJProcess Process;


    public String[] LocalVariables;


    public StringDictionary<Integer> ParameterLowerBound;


    public StringDictionary<Integer> ParameterUpperLowerBound;


    public List<Definition> SubDefinitions;


    public List<String> SubDefinitionNames;


    public List<String> GlobalVars;


    public List<String> Channels;


    public boolean MustAbstract;


    public EventCollection AlphabetEvents;


    public HashSet<String> Alphabets;


    public boolean AlphabetsCalculable;


    public Token Token;


    public boolean UsedInParallel;


    public boolean ParameterCanBeConstantized;


    public Definition(String name, String[] vars, TCJProcess process) throws Exception {
        this.Name = name;
        this.Process = process;
        this.LocalVariables = vars;
        this.SubDefinitions = new ArrayList<>();
        ;
        this.SubDefinitionNames = new ArrayList<>();
        this.Channels = new ArrayList<>();
        this.GlobalVars = new ArrayList<>();
        this.AlphabetsCalculable = true;
        this.Alphabets = new HashSet<>();
        this.ParameterLowerBound = new StringDictionary<Integer>(8);
        this.ParameterUpperLowerBound = new StringDictionary<Integer>(8);
    }

    @Override
    public String toString() {
        return this.Process.toString();
    }


    public String GetFullDefinition() {
        return this.Name + "(" + Ultility.pPStringList(this.LocalVariables) + ")=" + this.Process + ";";
    }


    public Definition ClearConstant(Map<String, Expression> constMapping) throws ParsingException {
        this.Process = this.Process.ClearConstant(constMapping);
        return this;
    }


    public void StaticAnalysis() throws ParsingException {
        this.MustAbstract = this.Process.MustBeAbstracted();
        this.Channels = this.Process.GetChannels();
        this.GlobalVars = this.Process.GetGlobalVariables();
        for (String item : this.LocalVariables) {
            this.GlobalVars.remove(item);
        }
        if (this.AlphabetEvents == null) {
            if (this.AlphabetsCalculable) {
                this.Alphabets = this.Process.GetAlphabets(null);
            }
            return;
        }
        if (this.AlphabetEvents.ContainsVariable()) {
            this.AlphabetsCalculable = false;
            this.Alphabets = null;
            return;
        }
        this.Alphabets = new HashSet<String>(new EventCollection(this.AlphabetEvents).EventNames);
        this.AlphabetsCalculable = true;
    }
}
