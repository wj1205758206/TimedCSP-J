package module.tcj.ultility;

import module.tcj.lts.IndexParallel;
import module.tcj.lts.TCJProcess;
import org.antlr.v4.runtime.Token;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Ultility {
    public static int TIME_REFINEMENT_CLOCK_CEILING = 32767;
    public static int TIME_REFINEMENT_CLOCK_FLOOR = -32768;
    public static Map<String, Method> JavaMethods = new HashMap<>();

    public static void CheckForSelfComposition(Token name, TCJProcess p, List<String> visitedDef)
    {
        if (p instanceof IndexParallel)
        {
            IndexParallel indexParallel = (IndexParallel) p;
            if (indexParallel.Processes == null)
            {
                return;
            }
            using (List<TCJProcess>.Enumerator enumerator = indexParallel.Processes.GetEnumerator())
            {
                while (enumerator.MoveNext())
                {
                    TCJProcess TCJProcess = enumerator.Current;
                    if (TCJProcess is DefinitionRef && (TCJProcess as DefinitionRef).Name == name.Text)
                    {

                        throw new Common.ParsingException("Self parallel composition will generate infinite behavior, hence it is not allowed.", name);
                    }
                    Ultility.CheckForSelfComposition(name, TCJProcess, visitedDef);
                }
                return;
            }
        }
        if (p is IndexInterleave)
        {
            IndexInterleave indexInterleave = p as IndexInterleave;
            if (indexInterleave.Processes == null)
            {
                return;
            }
            using (List<TCJProcess>.Enumerator enumerator2 = indexInterleave.Processes.GetEnumerator())
            {
                while (enumerator2.MoveNext())
                {
                    TCJProcess TCJProcess2 = enumerator2.Current;
                    if (TCJProcess2 is DefinitionRef && (TCJProcess2 as DefinitionRef).Name == name.Text)
                    {
                        throw new Common.ParsingException("Self interleave composition will generate infinite behavior, hence it is not allowed!", name);
                    }
                    Ultility.CheckForSelfComposition(name, TCJProcess2, visitedDef);
                }
                return;
            }
        }
        if (p is IndexChoice)
        {
            IndexChoice indexChoice = p as IndexChoice;
            if (indexChoice.Processes == null)
            {
                return;
            }
            using (List<TCJProcess>.Enumerator enumerator3 = indexChoice.Processes.GetEnumerator())
            {
                while (enumerator3.MoveNext())
                {
                    TCJProcess TCJProcess3 = enumerator3.Current;
                    if (TCJProcess3 is DefinitionRef && (TCJProcess3 as DefinitionRef).Name == name.Text)
                    {
                        throw new Common.ParsingException("Self choice composition will generate infinite behavior, hence it is not allowed!", name);
                    }
                    Ultility.CheckForSelfComposition(name, TCJProcess3, visitedDef);
                }
                return;
            }
        }
        if (p is IndexExternalChoice)
        {
            IndexExternalChoice indexExternalChoice = p as IndexExternalChoice;
            if (indexExternalChoice.Processes == null)
            {
                return;
            }
            using (List<TCJProcess>.Enumerator enumerator4 = indexExternalChoice.Processes.GetEnumerator())
            {
                while (enumerator4.MoveNext())
                {
                    TCJProcess TCJProcess4 = enumerator4.Current;
                    if (TCJProcess4 is DefinitionRef && (TCJProcess4 as DefinitionRef).Name == name.Text)
                    {
                        throw new Common.ParsingException("Self external choice composition will generate infinite behavior, hence it is not allowed!", name);
                    }
                    Ultility.CheckForSelfComposition(name, TCJProcess4, visitedDef);
                }
                return;
            }
        }
        if (p is IndexInterleaveAbstract)
        {
            IndexInterleaveAbstract indexInterleaveAbstract = p as IndexInterleaveAbstract;
            using (List<TCJProcess>.Enumerator enumerator5 = indexInterleaveAbstract.Processes.GetEnumerator())
            {
                while (enumerator5.MoveNext())
                {
                    TCJProcess TCJProcess5 = enumerator5.Current;
                    if (TCJProcess5 is DefinitionRef && (TCJProcess5 as DefinitionRef).Name == name.Text)
                    {
                        throw new Common.ParsingException("Self interleave composition will generate infinite behavior, hence it is not allowed!", name);
                    }
                    Ultility.CheckForSelfComposition(name, TCJProcess5, visitedDef);
                }
                return;
            }
        }
        if (p is GuardProcess)
        {
            GuardProcess guardProcess = p as GuardProcess;
            TCJProcess firstProcess = guardProcess.FirstProcess;
            if (firstProcess is DefinitionRef && (firstProcess as DefinitionRef).Name == name.Text)
            {
                throw new Common.ParsingException("Self composition will generate infinite behavior, hence it is not allowed!", name);
            }
            Ultility.CheckForSelfComposition(name, firstProcess, visitedDef);
            return;
        }
            else if (p is ConditionalChoiceAtomic)
        {
            ConditionalChoiceAtomic conditionalChoiceAtomic = p as ConditionalChoiceAtomic;
            TCJProcess TCJProcess6 = conditionalChoiceAtomic.FirstProcess;
            if (TCJProcess6 is DefinitionRef && (TCJProcess6 as DefinitionRef).Name == name.Text)
            {
                throw new Common.ParsingException("Self internal choice composition will generate infinite behavior, hence it is not allowed!", name);
            }
            Ultility.CheckForSelfComposition(name, TCJProcess6, visitedDef);
            if (conditionalChoiceAtomic.SecondProcess != null)
            {
                TCJProcess6 = conditionalChoiceAtomic.SecondProcess;
                if (TCJProcess6 is DefinitionRef && (TCJProcess6 as DefinitionRef).Name == name.Text)
                {
                    throw new Common.ParsingException("Self internal choice composition will generate infinite behavior, hence it is not allowed!", name);
                }
                Ultility.CheckForSelfComposition(name, TCJProcess6, visitedDef);
                return;
            }
        }
            else if (p is AtomicProcess)
        {
            AtomicProcess atomicProcess = p as AtomicProcess;
            TCJProcess process = atomicProcess.Process;
            if (process is DefinitionRef && (process as DefinitionRef).Name == name.Text)
            {
                throw new Common.ParsingException("Self composition will generate infinite behavior, hence it is not allowed!", name);
            }
            Ultility.CheckForSelfComposition(name, process, visitedDef);
            return;
        }
            else if (p is PAT.TCJ.LTS.Sequence)
        {
            PAT.TCJ.LTS.Sequence sequence = p as PAT.TCJ.LTS.Sequence;
            TCJProcess firstProcess2 = sequence.FirstProcess;
            if (firstProcess2 is DefinitionRef && (firstProcess2 as DefinitionRef).Name == name.Text)
            {
                throw new Common.ParsingException("Self composition will generate infinite behavior, hence it is not allowed!", name);
            }
            Ultility.CheckForSelfComposition(name, firstProcess2, visitedDef);
            return;
        }
            else if (p is DefinitionRef)
        {
            if ((p as DefinitionRef).Name == name.Text)
            {
                throw new Common.ParsingException("Self-looping definition is not allowed!", name);
            }
            if (!visitedDef.Contains((p as DefinitionRef).Name))
            {
                List<string> list = new List<string>(visitedDef);
                list.Add((p as DefinitionRef).Name);
                Ultility.CheckForSelfComposition(name, (p as DefinitionRef).Def.Process, list);
            }
        }
    }
}
