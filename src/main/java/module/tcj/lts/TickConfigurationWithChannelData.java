package module.tcj.lts;

import common.classes.expressions.Valuation;
import common.classes.expressions.expressionclass.Expression;

public class TickConfigurationWithChannelData extends TickConfiguration {
    public String ChannelName;

    public Expression[] Expressions;

    public TickConfigurationWithChannelData(TCJProcess p, String e, String hiddenEvent, Valuation globalEnv,
                                            boolean isDataOperation, boolean isUrgent, String name, Expression[] expressions) {
        super(p, e, hiddenEvent, globalEnv, isDataOperation, isUrgent);
        this.ChannelName = name;
        this.Expressions = expressions;
    }
}
