package module.tcj.lts;

import common.ParsingException;
import common.classes.expressions.Valuation;
import common.classes.lts.ChannelQueue;
import common.classes.moduleinterface.SpecificationBase;
import common.classes.ultility.Ultility;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CodePointCharStream;
import org.antlr.v4.runtime.CommonTokenStream;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Specification extends SpecificationBase {
    public Map<String, Definition> DefinitionDatabase = new HashMap<>();
    public Map<String, ChannelQueue> ChannelDatabase = new HashMap<>();
    //public List<IndexParallel> ParallelDatabase = new List<IndexParallel>(8);
    public static Map<String, Integer> ChannelArrayDatabase = new HashMap<>();
    public Valuation SpecValuation = new Valuation();
    public SharedDataObjects SharedData;

    public Specification(String spec) throws Exception {
        super(spec, null);
        SharedData = new SharedDataObjects();
        lockSpecificationData();

        ParseSpec(spec, "");
    }

    public Specification(String spec, String option, String filePath) throws Exception {
        super(spec, filePath);
        SharedData = new SharedDataObjects();
        Ultility.lockSharedData(this);

        try {
            ParseSpec(spec, option);

            //we need to store the default values that can be used for the simulation.
            SharedData.HasSyncrhonousChannel = hasSyncrhonousChannel;
            SharedData.SyncrhonousChannelNames = syncrhonousChannelNames;
            SharedData.HasAtomicEvent = hasAtomicEvent;
            SharedData.HasJavaCode = hasJavaCode;
            SharedData.LocalVars = Valuation.hiddenVars;
            SharedData.ChannelArrayDatabase = ChannelArrayDatabase;
        } catch (Exception ex) {
            throw ex;
        } finally {
            Ultility.unLockSharedData(this);
        }
    }

    protected void ParseSpec(String spec, String option) throws ParsingException {
        isParsing = true;

        //TODO: Lexer、Parser
//        ANTLRInputStream input = new ANTLRInputStream(spec);
        //ICharStream input = new ANTLRStringStream(spec);
        CharStream input = CharStreams.fromString(spec);
        TCJTreeLexer lex = new TCJTreeLexer();
        CommonTokenStream tokens = new CommonTokenStream(lex);
        TCJTreeParser parser = new TCJTreeParser(tokens);
        parser.Spec = this;
        TCJTreeParser.specification_return r = parser.specification();
        int num = 0;
        while (this.includeFiles.size() > num) {
            num = this.includeFiles.size();
            String completeSpecFromIncludeFiles = this.getCompleteSpecFromIncludeFiles();
            //input = new ANTLRStringStream(completeSpecFromIncludeFiles);
            //lex = new TCJTreeLexer(input);
            //tokens = new CommonTokenStream(lex);
            //parser = new TCJTreeParser(tokens);
            //parser.Spec = this;
            //r = parser.specification();

        }
        if (parser.HiddenVars.Count > 0) {
            Valuation.hiddenVars = parser.HiddenVars;
        }

        Common.Ultility.ParsingUltility.CheckIsParsingComplete(tokens, r.Tree);


        TCJTreeWalker walker = new TCJTreeWalker(new CommonTreeNodeStream(r.Tree) {
            TokenStream =tokens
        });
        walker.GlobalVarNames = parser.GlobalVarNames;
        walker.GlobalConstNames = parser.GlobalConstNames;
        walker.GlobalRecordNames = parser.GlobalRecordNames;
        walker.ChannelNames = parser.ChannelNames;
        walker.LTLStatePropertyNames = parser.LTLStatePropertyNames;
        walker.DefinitionNames = parser.DefinitionNames;
        walker.IsParameterized = parser.IsParameterized;
        walker.HasArbitraryProcess = parser.HasArbitraryProcess;
        walker.Spec = this;
        walker.program(option);

        isParsing = false;
        this.globalConstantDatabase = walker.ConstantDatabase;


        for (Definition definition : this.DefinitionDatabase.values()) {
            module.tcj.ultility.Ultility.CheckForSelfComposition(definition.Token, definition.Process, new List<string>());
        }


        this.StaticAnalysis();
        foreach(KeyValuePair < string, Definition > keyValuePair in this.DefinitionDatabase){
            List<string> globalVars = keyValuePair.Value.GlobalVars;
            int i = 0;
            while (i < globalVars.Count) {
                if (this.SpecValuation.Variables != null && !this.SpecValuation.Variables.ContainsKey(globalVars[i])) {
                    globalVars.RemoveAt(i);
                } else {
                    i++;
                }
            }
        }
        if (this.ChannelDatabase.Count > 0) {
            SpecificationBase.SyncrhonousChannelNames = new List<string>(0);
            Dictionary<string, ChannelQueue> dic = new Dictionary<string, ChannelQueue>();
            foreach(KeyValuePair < string, ChannelQueue > keyValuePair in this.ChannelDatabase){
                if (keyValuePair.Value.Size == 0) {
                    SpecificationBase.SyncrhonousChannelNames.Add(keyValuePair.Key);
                } else {
                    dic.Add(keyValuePair.Key, keyValuePair.Value);
                }
            }
            this.SpecValuation.Channels = dic;
            SpecificationBase.HasSyncrhonousChannel = (SpecificationBase.SyncrhonousChannelNames.Count > 0);

        }

        foreach(KeyValuePair < string, AssertionBase > keyValuePair in this.AssertionDatabase)
        {
            keyValuePair.Value.Initialize(this);
        }
        this.CheckVariableRange();

    }


    //============================================================
    public List<String> GetChannelNames(List<String> channels) {
        List<String> list = new ArrayList<>();
        for (String text : channels) {
            if (Specification.ChannelArrayDatabase.containsKey(text)) {
                int num = Specification.ChannelArrayDatabase.get(text);
                for (int i = 0; i < num; i++) {
                    list.add(text + "[" + i + "]");
                }
            } else {
                list.add(text);
            }
        }
        return list;
    }
}
