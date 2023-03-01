package common.classes.moduleinterface;

import common.CancelRunningException;
import common.ParsingException;
import common.classes.datastructure.StringHashTable;
import common.classes.expressions.expressionclass.OutOfMemoryException;
import common.classes.ultility.Ultility;
import org.antlr.v4.runtime.Token;


import java.awt.*;
import java.beans.EventHandler;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;
import java.util.Stack;


public abstract class AssertionBase {

    public void start() {
        synchronized (this) {
            this.isRunning = true;
        }
        //new MethodInvoker(this.InternalStart).BeginInvoke(null, null);
        new Thread(this::internalStart).start();
    }

    public void cancel() {
        synchronized (this) {
            this.cancelledFlag = true;
        }
    }

    public boolean cancelAndWait() {
        synchronized (this) {
            this.cancelledFlag = true;
            while (!this.isDone()) {
                //Monitor.Wait(this, 1000);
                try {
                    this.wait(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException("InterruptedException: " + e.getMessage());
                }
            }
        }
        return !this.hasCompleted();
    }

    public boolean waitUntilDone() {
        synchronized (this) {
            while (!this.isDone()) {
                //Monitor.Wait(this, 1000);
                try {
                    this.wait(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException("InterruptedException: " + e.getMessage());
                }
            }
        }
        return this.hasCompleted();
    }

    public boolean isDone() {
        boolean result;
        synchronized (this) {
            result = (this.completeFlag || this.cancelAcknowledgedFlag || this.failedFlag);
        }
        return result;
    }

    public boolean cancelRequested() {
        boolean result;
        synchronized (this) {
            result = this.cancelledFlag;
        }
        return result;
    }

    protected boolean hasCompleted() {
        boolean result;
        synchronized (this) {
            result = this.completeFlag;
        }
        return result;
    }

    protected void acknowledgeCancel() {
        lock(this)
        {
            this.cancelAcknowledgedFlag = true;
            this.isRunning = false;
            Monitor.Pulse(this);
            this.FireAsync(this.Cancelled, new object[]
                    {
                            this,
                            EventArgs.Empty
                    });
        }
    }


    private void completeOperation() {
        synchronized (this) {
            this.completeFlag = true;
            this.isRunning = false;
            Monitor.Pulse(this);
            this.FireAsync(this.Completed, new object[]
                    {
                            this,
                            EventArgs.Empty
                    });
        }
    }

    private void failOperation(Exception e) {
        synchronized (this) {
            this.failedFlag = true;
            this.isRunning = false;
            //Monitor.Pulse(this);
            this.notifyAll();
            this.fireAsync(this.fi, new object[]
                    {
                            this,
                            new ThreadExceptionEventArgs(e)
                    });
        }
    }

    protected void fireAsync(Delegate dlg, params object[] pList) {
        if (dlg != null) {
            this.isiTarget.BeginInvoke(dlg, pList);
        }
    }

    protected void onReturnResult() {
        synchronized (this) {
            this.fireAsync(this.ReturnResult, new Object[0]);
        }
    }

    protected void printMessage(String msg) {
    }

    public void initialize(SpecificationBase spec) throws ParsingException, NoSuchMethodException {
        this.modelCheckingOptions = new ModelCheckingOptions();
        List<String> list = new ArrayList<>();
        list.add("First Witness Trace using Depth First Search");
        list.add("Shortest Witness Trace using Breadth First Search");
        this.modelCheckingOptions.addAddimissibleBehavior("All", list);
    }

    //todo: ISynchronizeInvoke?
    public void uiInitialize(ISynchronizeInvoke target, int behavior, int engine) {
        this.isiTarget = target;
        this.isRunning = false;
        this.selectedBahavior = behavior;
        this.selectedEngine = engine;
        if (this.modelCheckingOptions.addimissibleBehaviorsNames.size() <= this.selectedBahavior) {
            throw new RuntimeException("Invalid Behavior Selection");
        }
        this.selectedBahaviorName = this.modelCheckingOptions.addimissibleBehaviorsNames.get(this.selectedBahavior);
        if (this.modelCheckingOptions.addimissibleBehaviors.get(this.selectedBahavior).VerificationEngines.size() <= this.selectedEngine) {
            throw new RuntimeException("Invalid Engine Selection");
        }
        this.selectedEngineName = this.modelCheckingOptions.addimissibleBehaviors.get(this.selectedBahavior).VerificationEngines.get(this.selectedEngine);
        this.verificationOutput = new VerificationOutput(this.selectedEngineName);
        this.verificationMode = true;
    }

    //todo: ISynchronizeInvoke?
    public void uiInitialize(ISynchronizeInvoke target, String behavior, String engine) {
        int num = this.modelCheckingOptions.addimissibleBehaviorsNames.indexOf(behavior);
        int engine2 = -1;
        if (this.modelCheckingOptions.addimissibleBehaviorsNames.contains(behavior)) {
            engine2 = this.modelCheckingOptions.addimissibleBehaviors.get(num).VerificationEngines.indexOf(engine);
        }
        this.uiInitialize(target, num, engine2);
    }

    public void internalStart() {
        this.cancelledFlag = false;
        this.completeFlag = false;
        this.cancelAcknowledgedFlag = false;
        this.failedFlag = false;
        try {
            if (this.verificationMode) {
                this.verificationOutput.startVerification();
                this.printMessage("Verification starts....");
                if (this.verificationOutput.verificationResult == VerificationResultType.UNKNOWN) {
                    this.modelChecking();
                }
                this.printMessage("Verification ends....");
            } else {
                this.verificationOutput.resultString = this.getResultString();
            }
            if (this.cancelRequested()) {
                this.verificationOutput.resultString = this.getResultStringForUnfinishedSearching(null);
                this.acknowledgeCancel();
            } else {
                this.onReturnResult();
            }
        } catch (CancelRunningException ex) {
            this.acknowledgeCancel();
        } catch (Exception ex) {
            try {
                if (ex instanceof OutOfMemoryException) {
                    ex = new common.classes.expressions.expressionclass.OutOfMemoryException("");
                    //ex = new PAT.Common.Classes.Expressions.ExpressionClass.OutOfMemoryException("");
                }
                if (this.verificationOutput.counterExampleTrace != null && this.verificationOutput.counterExampleTrace.size() > 0) {
                    StringBuilder stringBuilder = new StringBuilder();
                    this.verificationOutput.getCounterxampleString(stringBuilder);
                    //ex.Data.Add("trace", stringBuilder.toString());
                }
                this.verificationOutput.resultString = this.getResultStringForUnfinishedSearching(ex);
                this.failOperation(ex);
            } catch (Exception exception) {
            }
            if (ex instanceof Exception) {
                throw new RuntimeException("System Exception");
            }
        }
        synchronized (this) {
            if (!this.cancelAcknowledgedFlag && !this.failedFlag) {
                this.completeOperation();
            }
        }
    }

    public void clear() {
        this.isiTarget = null;
    }

    protected void modelChecking() throws Exception {
        this.runVerification();
        if (this.mustAbstract && this.verificationOutput.counterExampleTrace != null && this.verificationOutput.counterExampleTrace.size() > 0) {
            Ultility.CutNumber = -1;
            if (this.isCounterExampleSpurious()) {
                this.verificationOutput.verificationResult = VerificationResultType.UNKNOWN;
            }
            Ultility.CutNumber = 2;
        }
    }

    public abstract void runVerification() throws Exception;

    public abstract String getResultString();

    protected String getAddtionalStatsString() {
        return "";
    }

    public String getVerificationStatistics() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("********Verification Statistics********" + "\n");
        stringBuilder.append(this.getAddtionalStatsString());
        stringBuilder.append(this.verificationOutput.getVerificationStatistics());
        return stringBuilder.toString();
    }

    public String getResultStringForUnfinishedSearching(Exception ex) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("********Verification Result********" + "\n");
        if (ex != null) {
            stringBuilder.append("Exception_happened_during_the_verification" + "\n");
            String str = "";
            if (ex.getMessage().contains("trace")) {
                str = System.getProperty("line.separator") + "Trace leads to exception:" + System.getProperty("line.separator") + ex.getMessage();
            }
            if (ex instanceof RuntimeException) {
                RuntimeException ex2 = (RuntimeException) ex;
                stringBuilder.append(ex2.getMessage() + str + ((ex2.getCause() != null) ? (System.getProperty("line.separator") + "Exception stack trace:" + System.getProperty("line.separator") + ex2.getCause()) : "") + "\n");
            } else {
                stringBuilder.append(ex.getMessage() + str + "\n");
            }
        } else {
            stringBuilder.append("Verification cancelled" + "\n");
        }
        stringBuilder.append("\n");
        stringBuilder.append("********Verification Setting********" + "\n");
        stringBuilder.append("Admissible Behavior: " + this.selectedBahaviorName + "\n");
        stringBuilder.append("Search Engine: " + this.selectedEngineName + "\n");
        stringBuilder.append("System Abstraction: " + this.mustAbstract + "\n");
        stringBuilder.append("\n");
        return stringBuilder.toString();
    }

    public abstract String startingProcess();

    protected boolean isCounterExampleSpurious() throws Exception {
        Stack<ConfigurationBase> stack = new Stack<ConfigurationBase>();
        List<ConfigurationBase> list = new ArrayList<>(64);
        stack.push(this.initialStep);
        Stack<Integer> stack2 = new Stack<Integer>();
        stack2.push(0);
        List<Integer> list2 = new ArrayList<>(1024);
        StringHashTable stringHashTable = new StringHashTable(1024);
        stringHashTable.add("0-" + this.initialStep.getID());
        for (; ; ) {
            ConfigurationBase configurationBase = stack.pop();
            int num = stack2.pop();
            if (num > 0) {
                while (list2.get(list2.size() - 1) >= num) {
                    int index = list2.size() - 1;
                    list2.remove(index);
                    list.remove(index);
                }
            }
            list.add(configurationBase);
            list2.add(num);
            if (list.size() == this.verificationOutput.counterExampleTrace.size()) {
                break;
            }
            ConfigurationBase configurationBase2 = this.verificationOutput.counterExampleTrace.get(num + 1);
            Iterable<ConfigurationBase> enumerable = configurationBase.makeOneMove(configurationBase2.event);
            for (ConfigurationBase configurationBase3 : enumerable) {
                if (configurationBase2.equalsV(configurationBase3)) {
                    String key = num + 1 + "-" + configurationBase3.getID();
                    if (!stringHashTable.containsKey(key)) {
                        stack.push(configurationBase3);
                        stack2.push(num + 1);
                        stringHashTable.add(key);
                    }
                }
            }
            if (stack.size() <= 0) {
                return true;
            }
        }
        this.verificationOutput.counterExampleTrace = list;
        Ultility.CutNumber = 2;
        return false;
    }

    public boolean isBDDSelected() {
        return this.selectedEngineName == "Symbolic Model Checking using BDD with Forward Search Strategy"
                || this.selectedEngineName == "Symbolic Model Checking using BDD with Backward Search Strategy"
                || this.selectedEngineName == "Symbolic Model Checking using BDD with Forward-Backward Search Strategy";
    }


    //protected ISynchronizeInvoke isiTarget;
    protected EventQueue isiTarget;


    private boolean cancelledFlag;


    private boolean completeFlag;


    private boolean cancelAcknowledgedFlag;


    private boolean failedFlag;


    protected boolean isRunning;


    public ModelCheckingOptions modelCheckingOptions;


    private int selectedBahavior;


    private int selectedEngine;


    public String selectedBahaviorName;


    public String selectedEngineName;


    //public IToken assertToken;
    public Token assertToken;

    public ConfigurationBase initialStep;


    public static boolean calculateParticipatingProcess;


    public VerificationOutput verificationOutput;


    public boolean mustAbstract;


    public boolean verificationMode = true;


//    public delegate void ActionEvent(string action);
//
//    public delegate void ReturnResultEvent();
//
//    public delegate void ProgressEvent(int progress);

    /*
    * ActionEvent actionEvent = (String action) -> {
        //你需要执行的代码
    };
    * */
    public interface ActionEvent {
        public void doEvent(String action);
    }

    public interface ReturnResultEvent {
        public void doEvent();
    }

    public interface ProgressEvent {
        public void doEvent(int progress);
    }


}
