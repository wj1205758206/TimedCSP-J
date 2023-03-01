package common.classes.moduleinterface;

import common.classes.semanticmodels.lts.bdd.Model;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class VerificationOutput {

    public VerificationResultType verificationResult;


    public List<ConfigurationBase> counterExampleTrace;


    public String resultString;


    //private Stopwatch timer;
    private long timer;

    private long startMemroySize;


    public double verificationTime;


    public float estimateMemoryUsage;

    public int actualMemoryUsage;

    public boolean generateCounterExample = true;

    public boolean nonDeterminismInDTMC;

    public VerificationOutputType verificationOutputType;

    public int loopIndex = -1;

    public long noOfStates;

    public long transitions;

    public int SCCCount;

    public long SCCTotalSize;

    public long reducedMDPTransitions;

    public long reducedMDPStates;

    public long MDPIterationNumber;

    public double rewards;

    public int numberOfBoolVars;

    public int numberOfBDDOperation;

    public int numberofARLoops;

    public int maximumNumStatesGenerated;

    public VerificationOutput(String engine) {
        this.verificationResult = VerificationResultType.UNKNOWN;
        this.counterExampleTrace = new ArrayList<ConfigurationBase>(64);
        this.transitions = 0L;
        this.noOfStates = 0L;
        this.loopIndex = -1;
        this.reducedMDPTransitions = 0L;
        this.reducedMDPStates = 0L;
        this.timer = System.currentTimeMillis();
        switch (engine) {
            case "Symbolic Model Checking using BDD":
            case "Symbolic Model Checking using BDD with Backward Search Strategy":
            case "Symbolic Model Checking using BDD with Forward-Backward Search Strategy":
            case "Symbolic Model Checking using BDD with Forward Search Strategy":
                this.verificationOutputType = VerificationOutputType.LTS_BDD;
                return;
            case "Graph-based Probability Computation Based on Value Iteration":
            case "Simluation on MDP model":
            case "Simulation on PCSP for Nondeterministic Model Only":
                this.verificationOutputType = VerificationOutputType.MDP_EXPLICIT;
                return;
            case "(Mulit-Core) Strongly Connected Component Based Search":
                this.verificationOutputType = VerificationOutputType.LTS_EXPLICIT_MULTI_CORE;
                return;
            case "BDD Digitization":
            case "BDD Digitization + LU Simulation":
                this.verificationOutputType = VerificationOutputType.TTS_BDD;
                return;
            case "Direct reachability with POR and Abstraction refinement":
            case "Direct reachablity with Abstraction refinement":
                this.verificationOutputType = VerificationOutputType.POR;
                return;
        }
        this.verificationOutputType = VerificationOutputType.LTS_EXPLICIT;
    }

    public void getCounterxampleString(StringBuilder sb) {
        if (this.generateCounterExample) {
            sb.append("<");
            boolean flag = false;
            for (int i = 0; i < this.counterExampleTrace.size(); i++) {
                ConfigurationBase configurationBase = this.counterExampleTrace.get(i);
                if (configurationBase.event == "init") {
                    sb.append(configurationBase.event);
                } else {
                    sb.append(" -> ");
                    if (this.loopIndex >= 0 && i == this.loopIndex) {
                        sb.append("(");
                    }
                    sb.append(configurationBase.getDisplayEvent());
                    if (configurationBase.event != "τ" && i >= this.loopIndex && this.loopIndex >= 0) {
                        flag = true;
                    }
                }
            }
            if (this.loopIndex >= 0 && !flag) {
                sb.append(" -> (τ -> ");
            }
            if (this.loopIndex >= 0) {
                sb.append(")*");
            }
            sb.append(">" + "\n");
            return;
        }
        sb.append("Counterexample generation is ignored." + "\n");
    }

    public void startVerification() {
        System.gc();
        timer = System.currentTimeMillis();
        this.startMemroySize = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
    }

    public String getVerificationStatistics() {
        long end = System.currentTimeMillis();
        long elapsedTime = end - timer;
        long totalMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

        this.verificationTime = elapsedTime / 1000.0;
        this.estimateMemoryUsage = (float) (totalMemory - this.startMemroySize);
        StringBuilder stringBuilder = new StringBuilder();
        switch (this.verificationOutputType) {
            case LTS_EXPLICIT:
            case TTS_EXPLICIT:
            case MDP_EXPLICIT:
                stringBuilder.append("Visited States:" + ((this.noOfStates >= 0L) ? String.valueOf(this.noOfStates) : "Unknown") + "\n");
                stringBuilder.append("Total Transitions:" + ((this.transitions >= 0L) ? String.valueOf(this.transitions) : "Unknown") + "\n");
                if (this.reducedMDPStates > 0L) {
                    stringBuilder.append("Visited States (Reduced MDP):" + this.reducedMDPStates + "\n");
                    stringBuilder.append("Total Transitions (Reduced MDP):" + this.reducedMDPTransitions + "\n");
                }
                if (this.MDPIterationNumber > 0L) {
                    stringBuilder.append("MDP Iterations:" + this.MDPIterationNumber + "\n");
                }
                break;
            case LTS_EXPLICIT_MULTI_CORE:
                stringBuilder.append("Visited States:" + ((this.noOfStates >= 0L) ? String.valueOf(this.noOfStates) : "Unknown") + "\n");
                stringBuilder.append("Total Transitions:" + ((this.transitions >= 0L) ? String.valueOf(this.transitions) : "Unknown") + "\n");
                stringBuilder.append("Number of SCC found: " + this.SCCCount + "\n");
                stringBuilder.append("Total SCC states: " + this.SCCTotalSize + "\n");
                if (this.SCCCount != 0) {
                    stringBuilder.append("Average SCC Size: " + this.SCCTotalSize / (long) this.SCCCount + "\n");
                } else {
                    stringBuilder.append("Average SCC Size: 0" + "\n");
                }
                DecimalFormat df = new DecimalFormat("#.##");
                stringBuilder.append("SCC Ratio: " + df.format((double) this.SCCTotalSize / (double) this.noOfStates) + "\n");
                //stringBuilder.append("SCC Ratio: " + Math.Round((double) this.SCCTotalSize / (double) this.noOfStates, 2).ToString() + "\n");
                break;
            case LTS_BDD:
            case MDP_BDD:
                stringBuilder.append("Number of Boolean Variables Used:" + this.numberOfBoolVars + "\n");
                stringBuilder.append("Number of BDD Pre/Post-Operations Performed:" + Model.numUntimedSucc + "\n");
                break;
            case TTS_BDD:
                stringBuilder.append("Number of Boolean Variables Used:" + this.numberOfBoolVars + "\n");
                stringBuilder.append("Number of Loops:" + Model.numTimedSucc + "\n");
                stringBuilder.append("Untimed Successors Operations:" + Model.numUntimedSucc + "\n");
                stringBuilder.append("Avarage Untimed Successors/Loops:" + String.format("%.2f", (double) Model.numUntimedSucc / (double) Model.numTimedSucc) + "\n");
                break;
            case POR:
                stringBuilder.append("Number of AR-loops:" + this.numberofARLoops + "\n");
                stringBuilder.append("Maximum of Generated States:" + this.maximumNumStatesGenerated + "\n");
                stringBuilder.append("Visited States:" + ((this.noOfStates >= 0L) ? String.valueOf(this.noOfStates) : "Unknown") + "\n");
                stringBuilder.append("Total Transitions:" + ((this.transitions >= 0L) ? String.valueOf(this.transitions) : "Unknown") + "\n");
                break;
        }
        stringBuilder.append("Time Used:" + this.verificationTime + "s" + "\n");
        if (this.actualMemoryUsage != 0) {
            stringBuilder.append("Memory Used:" + (double) this.actualMemoryUsage / 1000.0 + "KB\r\n" + "\n");
        } else {
            stringBuilder.append("Estimated Memory Used:" + (double) this.estimateMemoryUsage / 1000.0 + "KB\r\n" + "\n");
        }
        return stringBuilder.toString();
    }
}
