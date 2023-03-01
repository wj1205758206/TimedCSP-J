package module.tcj.assertions;

import common.classes.datastructure.DBM;
import common.classes.datastructure.StringDictionary;
import common.classes.expressions.expressionclass.Expression;
import common.classes.moduleinterface.ConfigurationBase;
import common.classes.moduleinterface.VerificationResultType;
import common.classes.ultility.Ultility;
import module.tcj.lts.ZoneConfiguration;

import java.util.*;

public abstract class ExtrapolationReachability {
    protected TCJAssertionReachability assertion;

    public ExtrapolationReachability(TCJAssertionReachability assertion) {
        this.assertion = assertion;
    }

    protected abstract void extrapolation(DBM dbm, List<List<Integer>> clocksBound);

    protected abstract boolean IsSubSet(DBM dbm1, DBM dbm2, List<List<Integer>> clocksBound);

    public void BFSVerification() throws Exception {
        StringDictionary<List<DBM>> stringDictionary = new StringDictionary<List<DBM>>(Ultility.MC_INITIAL_SIZE);
        int num = 0;
        Expression reachableStateCondition = this.assertion.reachableStateCondition;
        Queue<ConfigurationBase> queue = new ArrayDeque<>(1024);
        Queue<List<ConfigurationBase>> queue2 = new ArrayDeque<>(1024);
        ZoneConfiguration zoneConfiguration = (ZoneConfiguration) this.assertion.initialStep;
        this.extrapolation(zoneConfiguration.DBM, zoneConfiguration.clocksBound);
        stringDictionary.add(zoneConfiguration.GetIDNoDBM(), Arrays.asList(zoneConfiguration.DBM));
        queue.add(this.assertion.initialStep);
        queue2.add(Arrays.asList(this.assertion.initialStep));
        while (!this.assertion.cancelRequested()) {
            ConfigurationBase configurationBase = queue.poll();
            List<ConfigurationBase> list = queue2.poll();
            if (configurationBase.implyCondition(reachableStateCondition)) {
                this.assertion.verificationOutput.verificationResult = VerificationResultType.VALID;
                this.assertion.verificationOutput.noOfStates = (long) (num + stringDictionary.count);
                this.assertion.verificationOutput.counterExampleTrace = list;
                return;
            }
            List<ConfigurationBase> enumerable = configurationBase.makeOneMove();
            this.assertion.verificationOutput.transitions += (long) enumerable.size();
            for (ConfigurationBase configurationBase2 : enumerable) {
                ZoneConfiguration zoneConfiguration2 = (ZoneConfiguration) configurationBase2;
                String idnoDBM = zoneConfiguration2.GetIDNoDBM();
                if (stringDictionary.containsKey(idnoDBM)) {
                    boolean flag = false;
                    List<DBM> containsKey = stringDictionary.getContainsKey(idnoDBM);
                    for (int i = containsKey.size() - 1; i >= 0; i--) {
                        DBM dbm = containsKey.get(i);
                        if (this.IsSubSet(zoneConfiguration2.DBM, dbm, zoneConfiguration2.clocksBound)) {
                            flag = true;
                            break;
                        }
                    }
                    if (!flag) {
                        this.extrapolation(zoneConfiguration2.DBM, zoneConfiguration2.clocksBound);
                        containsKey.add(zoneConfiguration2.DBM);
                        stringDictionary.setValue(idnoDBM, containsKey);
                        num++;
                        queue.add(zoneConfiguration2);
                        List<ConfigurationBase> temp = new ArrayList<>(list);
                        temp.add(zoneConfiguration2);
                        queue2.add(temp);
                    }
                } else {
                    this.extrapolation(zoneConfiguration2.DBM, zoneConfiguration2.clocksBound);
                    stringDictionary.add(idnoDBM, Arrays.asList(zoneConfiguration2.DBM));
                    queue.add(zoneConfiguration2);
                    List<ConfigurationBase> temp = new ArrayList<>(list);
                    temp.add(zoneConfiguration2);
                    queue2.add(temp);
                }
            }
            if (queue.size() <= 0) {
                this.assertion.verificationOutput.verificationResult = VerificationResultType.INVALID;
                this.assertion.verificationOutput.noOfStates = (long) (num + stringDictionary.count);
                return;
            }
        }
        this.assertion.verificationOutput.noOfStates = (long) num;
    }

    public void DFSVerification() throws Exception {
        StringDictionary<List<DBM>> stringDictionary = new StringDictionary<List<DBM>>(Ultility.MC_INITIAL_SIZE);
        int num = 0;
        Expression reachableStateCondition = this.assertion.reachableStateCondition;
        Stack<ConfigurationBase> stack = new Stack<ConfigurationBase>();
        ZoneConfiguration zoneConfiguration = (ZoneConfiguration) this.assertion.initialStep;
        this.extrapolation(zoneConfiguration.DBM, zoneConfiguration.clocksBound);
        stringDictionary.add(zoneConfiguration.GetIDNoDBM(), Arrays.asList(zoneConfiguration.DBM));
        stack.push(this.assertion.initialStep);
        Stack<Integer> stack2 = new Stack<Integer>();
        stack2.push(0);
        List<Integer> list = new ArrayList<>(1024);
        while (!this.assertion.cancelRequested()) {
            ConfigurationBase configurationBase = stack.pop();
            int num2 = stack2.pop();
            if (num2 > 0) {
                while (list.get(list.size() - 1) >= num2) {
                    int index = list.size() - 1;
                    list.remove(index);
                    this.assertion.verificationOutput.counterExampleTrace.remove(index);
                }
            }
            this.assertion.verificationOutput.counterExampleTrace.add(configurationBase);
            if (configurationBase.implyCondition(reachableStateCondition)) {
                this.assertion.verificationOutput.verificationResult = VerificationResultType.VALID;
                this.assertion.verificationOutput.noOfStates = (long) (num + stringDictionary.count);
                return;
            }
            list.add(num2);
            List<ConfigurationBase> enumerable = configurationBase.makeOneMove();
            this.assertion.verificationOutput.transitions += (long) enumerable.size();
            for (ConfigurationBase configurationBase2 : enumerable) {
                ZoneConfiguration zoneConfiguration2 = (ZoneConfiguration) configurationBase2;
                String idnoDBM = zoneConfiguration2.GetIDNoDBM();
                if (stringDictionary.containsKey(idnoDBM)) {
                    boolean flag = false;
                    List<DBM> containsKey = stringDictionary.getContainsKey(idnoDBM);
                    for (int i = containsKey.size() - 1; i >= 0; i--) {
                        DBM dbm = containsKey.get(i);
                        if (this.IsSubSet(zoneConfiguration2.DBM, dbm, zoneConfiguration2.clocksBound)) {
                            flag = true;
                            break;
                        }
                    }
                    if (!flag) {
                        this.extrapolation(zoneConfiguration2.DBM, zoneConfiguration2.clocksBound);
                        containsKey.add(zoneConfiguration2.DBM);
                        stringDictionary.setValue(idnoDBM, containsKey);
                        stack.push(zoneConfiguration2);
                        stack2.push(num2 + 1);
                        num++;
                    }
                } else {
                    this.extrapolation(zoneConfiguration2.DBM, zoneConfiguration2.clocksBound);
                    stringDictionary.add(idnoDBM, Arrays.asList(zoneConfiguration2.DBM));
                    stack.push(zoneConfiguration2);
                    stack2.push(num2 + 1);
                }
            }
            if (stack.size() <= 0) {
                this.assertion.verificationOutput.counterExampleTrace = null;
                this.assertion.verificationOutput.verificationResult = VerificationResultType.INVALID;
                this.assertion.verificationOutput.noOfStates = (long) (num + stringDictionary.count);
                return;
            }
        }
        this.assertion.verificationOutput.noOfStates = (long) num;
    }
}
