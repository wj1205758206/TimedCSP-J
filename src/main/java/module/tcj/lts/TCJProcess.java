package module.tcj.lts;

import common.ParsingException;
import common.classes.expressions.Valuation;
import common.classes.expressions.expressionclass.Expression;
import common.classes.moduleinterface.ConfigurationBase;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public abstract class TCJProcess {
    public String ProcessID;


    public int IsBDDEncodableProp = -1;

    public abstract List<ZoneConfiguration> MoveOneStep(ZoneConfiguration eStep) throws CloneNotSupportedException, ParsingException;

    public abstract void GetDiscretEventTransitions(TickConfiguration eStep, List<TickConfiguration> toReturn) throws CloneNotSupportedException, ParsingException;

    public TCJProcess GetProcessAfterDelay(TickConfiguration eStep) throws ParsingException {
        return this;
    }


    public List<String> GetGlobalVariables() {
        return new ArrayList<>(0);

    }


    public List<String> GetChannels() {
        return new ArrayList<>(0);

    }


    public HashSet<String> GetAlphabets(Map<String, String> visitedDefinitionRefs) throws ParsingException {
        return new HashSet<>();

    }


    public boolean MustBeAbstracted() {
        return false;
    }


    public abstract TCJProcess ClearConstant(Map<String, Expression> constMapping) throws ParsingException;


    public List<ZoneConfigurationWithChannelData> SyncOutput(ZoneConfiguration eStep) throws ParsingException {
        return new ArrayList<>();

    }


    public List<ZoneConfiguration> SyncInput(ZoneConfigurationWithChannelData eStep) throws ParsingException {
        return new ArrayList<>(0);

    }


    public void SyncInput(TickConfigurationWithChannelData eStep, List<TickConfiguration> toReturn) throws ParsingException {
    }


    public void SyncOutput(TickConfiguration eStep, List<TickConfigurationWithChannelData> toReturn) throws ParsingException {
    }


    public TCJProcess SetTimer(Valuation globalEnv, List<List<Integer>> clockBound, Map<Integer, Integer> mapping) throws ParsingException {
        return this;
    }


    public TCJProcess GetTopLevelConcurrency(List<String> visitedDef) {
        return null;
    }


    public boolean IsTimeImmediate() {
        return false;
    }


    public boolean IsSkip() {
        return false;
    }


}
