package common.classes.moduleinterface;

import common.ParsingException;
import common.ParsingExceptionComparer;
import common.classes.expressions.expressionclass.Expression;
import common.classes.ultility.Ultility;
import org.antlr.v4.runtime.Token;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public abstract class SpecificationBase {
    public String specificationName;

    public String filePath;

    public HashSet<String> includeFiles;

    public String inputModelText;


    public static boolean hasAtomicEvent;

    public static boolean hasJavaCode;


    public static List<String> syncrhonousChannelNames;


    public static boolean hasSyncrhonousChannel;


    public static boolean isParsing;


    public static boolean checkVariableDeclare = true;

    public Map<String, AssertionBase> assertionDatabase = new HashMap<>();


    public Map<String, Expression> declarationDatabase = new HashMap<>();


    public Map<String, Map<List<String>, Expression>> macroDefinition = new HashMap<>();


    public Map<String, Expression> globalConstantDatabase;


    public TreeMap<String, Declaration> declaritionTable = new TreeMap<>();


    public Map<String, List<ParsingException>> usageTable = new HashMap<>();


    public static boolean isSimulation = false;


    public Map<String, ParsingException> warnings = new HashMap<>();

    public Map<String, ParsingException> errors = new HashMap<>();


    public abstract ConfigurationBase simulationInitialization(String startingProcess);

    public BufferedImage mapConfigurationToImage(ConfigurationBase config, int imageSize) {
        return null;
    }

    //todo: Graph?
    public Graph generateBAGraph(String assertion) {
        if (this.assertionDatabase.containsKey(assertion)) {
            AssertionLTL assertionLTL = (AssertionLTL) this.assertionDatabase.get(assertion);
            if (assertionLTL != null) {
                Graph graph = LTL2BA.AutomatonToDot(assertionLTL.BA);
                graph.UserData = "!(" + assertionLTL.LTLString + ")";
                return graph;
            }
        }
        return null;
    }

    protected SpecificationBase(String spec, String filePath) {
        this.inputModelText = spec;
        if (spec.startsWith("//@@")) {
            int num = spec.indexOf("@@", 4);
            if (num != -1) {
                this.specificationName = spec.substring(4, num - 4);
            }
        }
        this.filePath = filePath;
        this.errors.clear();
        this.warnings.clear();
        this.includeFiles = new HashSet<String>();
        ParsingException.fileOffset.clear();
        ParsingException.fileOffset.put(
                ParsingException.countLinesInFile(spec),
                new String[]{this.filePath});
    }

    protected String getCompleteSpecFromIncludeFiles() throws ParsingException {
        String text = this.inputModelText;
        ParsingException.fileOffset.clear();
        ParsingException.fileOffset.put(ParsingException.countLinesInFile(text), new String[]
                {
                        this.filePath
                });
        for (String text2 : this.includeFiles) {
            try {
                if (text2 != this.filePath) {
                    List<String> lines = Files.readAllLines(Paths.get(text2), StandardCharsets.UTF_8);
                    for (String line : lines) {
                        text += "\r\n" + line;
                    }

                    ParsingException.fileOffset.put(ParsingException.countLinesInFile(text), new String[]
                            {
                                    text2
                            });
                }
            } catch (Exception ex) {
                throw new ParsingException("Error happend in loading \"" + text2 + "\":" + ex.getMessage(), -1, -1, "");
            }
        }
        this.includeFiles.clear();
        return text;
    }

    public abstract String getSpecification();


    public abstract List<String> getProcessNames();


    public abstract void lockSpecificationData();


    public void unLockSpecificationData() {
    }

    public ParsingException goToDeclarition(String term) {
        if (this.declaritionTable.containsKey(term)) {
            return this.declaritionTable.get(term).declarationToken;
        }
        return null;
    }

    public List<ParsingException> findUsages(String term) {
        if (this.usageTable.containsKey(term)) {
            this.usageTable.get(term).sort(new ParsingExceptionComparer());
            return this.usageTable.get(term);
        }
        return new ArrayList<>(0);
    }

    public List<ParsingException> renameFindUsages(String term) {
        if (this.usageTable.containsKey(term)) {
            List<ParsingException> list = new ArrayList<>(this.usageTable.get(term));
            if (this.declaritionTable.containsKey(term)) {
                list.add(this.declaritionTable.get(term).declarationToken);
            }
            list.sort(new ParsingExceptionComparer());
            return list;
        }
        return new ArrayList<ParsingException>(0);
    }

    public abstract List<String> getAllProcessNames();


    public abstract List<String> getGlobalVarNames();


    public List<String> getChannelNames() {
        return new ArrayList<String>();

    }


    public abstract String[] getParameterNames(String process);


    public void lockSharedData(boolean isSimulation) {
        SpecificationBase.isSimulation = isSimulation;
        Ultility.lockSharedData(this);
    }

    public void unLockSharedData() {
        Ultility.unLockSharedData(this);
    }

    public boolean grabSharedDataLock() {
        return Ultility.grabSharedDataLock();
    }

    public void addNewWarning(String msg, Token token) {
        String key = msg + token.getLine() + token.getCharPositionInLine();
//        String key = msg + token.Line + token.CharPositionInLine;
        if (!this.warnings.containsKey(key)) {
            this.warnings.put(key, new ParsingException(msg, token));
        }
    }

    public void addNewError(String msg, Token token) {
        String key = msg + token.getLine() + token.getCharPositionInLine();
        if (!this.errors.containsKey(key)) {
            this.errors.put(key, new ParsingException(msg, token));
        }
    }
}
