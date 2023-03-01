package module.tcj.lts;

import common.ParsingException;
import common.classes.expressions.expressionclass.Expression;
import common.classes.expressions.expressionclass.IntConstant;
import common.classes.lts.ParallelDefinition;
import common.classes.ultility.Ultility;

import java.util.*;

public class IndexedProcess {
    public TCJProcess Process;

    public List<ParallelDefinition> Definitions;


    public List<DefinitionRef> ContainedDefRefs;

    public IndexedProcess(TCJProcess process, List<ParallelDefinition> definitions, List<DefinitionRef> drefs) {
        this.Process = process;
        this.Definitions = definitions;
        this.ContainedDefRefs = drefs;
    }

    public List<String> GetGlobalVariables() {
        List<String> list = new ArrayList<>();
        for (ParallelDefinition parallelDefinition : this.Definitions) {
            Ultility.addList(list, parallelDefinition.GetGlobalVariables());
        }
        Ultility.addList(list, this.Process.GetGlobalVariables());
        for (ParallelDefinition parallelDefinition2 : this.Definitions) {
            list.remove(parallelDefinition2.Parameter);
        }
        return list;
    }

    public void GlobalVariablesAsIndex(List<String> visitedDef) {
        for (ParallelDefinition parallelDefinition : this.Definitions) {
            if (parallelDefinition.GetGlobalVariables().size() > 0) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("ERROR - PAT FAILED to calculate the alphabet of process " + this.toString() + "." + "\n");
                stringBuilder.append("CAUSE - The process definition contains global variables!" + "\n");
                stringBuilder.append("REMEDY - 1) Avoid using global variables in process 2) Or manually specify the alphabet of the relevant process using the following syntax: \n\r\t #alphabet someProcess {X}; \n\rwhere X is a set of event names." + "\n");
                throw new RuntimeException(stringBuilder.toString());
            }
        }
    }

    public List<TCJProcess> GetIndexedProcesses(Map<String, Expression> constMapping) throws ParsingException {
        List<ParallelDefinition> list = new ArrayList<>();
        for (ParallelDefinition parallelDefinition : this.Definitions) {
            ParallelDefinition item = parallelDefinition.ClearConstant(constMapping);
            list.add(item);
        }
        List<TCJProcess> list2 = new ArrayList<>(16);
        for (ParallelDefinition parallelDefinition2 : list) {
            Arrays.sort(parallelDefinition2.DomainValues.toArray());
        }
        List<List<Expression>> list3 = new ArrayList<>();
        for (int v : list.get(0).DomainValues) {
            List<Expression> temp = new ArrayList<>(list.size());
            temp.add(new IntConstant(v));
            list3.add(temp);
        }
        for (int i = 1; i < list.size(); i++) {
            List<List<Expression>> list4 = new ArrayList<>();
            List<Integer> domainValues = list.get(i).DomainValues;
            for (int j = 0; j < list3.size(); j++) {
                for (int v2 : domainValues) {
                    List<Expression> temp = new ArrayList<>(list3.get(j));
                    temp.add(new IntConstant(v2));
                    list4.add(temp);
                }
            }
            list3 = list4;
        }
        for (List<Expression> list5 : list3) {
            Map<String, Expression> dictionary = new HashMap<>();
            for (int k = 0; k < list5.size(); k++) {
                Expression value = list5.get(k);
                dictionary.put(list.get(k).Parameter, value);
            }
            TCJProcess item2 = this.Process.ClearConstant(dictionary);
            list2.add(item2);
        }
        return list2;
    }

    public List<DefinitionRef> GetIndexedDefRef(Map<String, Expression> constMapping) throws ParsingException {
        if (this.ContainedDefRefs == null) {
            return null;
        }
        List<ParallelDefinition> list = new ArrayList<>();
        for (ParallelDefinition parallelDefinition : this.Definitions) {
            ParallelDefinition item = parallelDefinition.ClearConstant(constMapping);
            list.add(item);
        }
        List<DefinitionRef> list2 = new ArrayList<>(16);
        for (ParallelDefinition parallelDefinition2 : list) {
            Arrays.sort(parallelDefinition2.DomainValues.toArray());
        }
        List<List<Expression>> list3 = new ArrayList<>();
        for (int v : list.get(0).DomainValues) {
            List<Expression> temp = new ArrayList<>(list.size());
            temp.add(new IntConstant(v));
            list3.add(temp);
        }
        for (int i = 1; i < list.size(); i++) {
            List<List<Expression>> list4 = new ArrayList<>();
            List<Integer> domainValues = list.get(i).DomainValues;
            for (int j = 0; j < list3.size(); j++) {
                for (int v2 : domainValues) {
                    List<Expression> temp = new ArrayList<>(list3.get(j));
                    temp.add(new IntConstant(v2));
                    list4.add(temp);
                }
            }
            list3 = list4;
        }
        for (List<Expression> list5 : list3) {
            Map<String, Expression> dictionary = new HashMap<>();
            for (int k = 0; k < list5.size(); k++) {
                Expression value = list5.get(k);
                if (dictionary.containsKey(list.get(k).Parameter)) {
                    dictionary.put(list.get(k).Parameter, value);
                } else {
                    dictionary.put(list.get(k).Parameter, value);
                }
            }
            for (DefinitionRef definitionRef : this.ContainedDefRefs) {
                DefinitionRef item2 = (DefinitionRef) definitionRef.ClearConstant(dictionary);
                list2.add(item2);
            }
        }
        return list2;
    }

    public IndexedProcess ClearConstant(Map<String, Expression> constMapping) throws ParsingException {
        TCJProcess process = this.Process.ClearConstant(constMapping);
        List<ParallelDefinition> list = new ArrayList<>(this.Definitions.size());
        for (ParallelDefinition parallelDefinition : this.Definitions) {
            list.add(parallelDefinition.ClearConstant(constMapping));
        }
        if (this.ContainedDefRefs != null) {
            List<DefinitionRef> list2 = new ArrayList<>(this.ContainedDefRefs.size());
            for (DefinitionRef definitionRef : this.ContainedDefRefs) {
                list2.add((DefinitionRef) definitionRef.ClearConstant(constMapping));
            }
            return new IndexedProcess(process, list, list2);
        }
        return new IndexedProcess(process, list, this.ContainedDefRefs);
    }

    @Override
    public String toString() {
        String text = "";
        for (ParallelDefinition parallelDefinition : this.Definitions) {
            text = text + parallelDefinition.toString() + ";";
        }
        text = text.substring(0, text.length() - 1);
        return text + "@" + this.Process;
    }
}
