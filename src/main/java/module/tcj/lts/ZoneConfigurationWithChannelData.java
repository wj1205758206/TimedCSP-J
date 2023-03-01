package module.tcj.lts;

import common.classes.expressions.Valuation;
import common.classes.expressions.expressionclass.Expression;
import common.classes.datastructure.DBM;

public class ZoneConfigurationWithChannelData extends ZoneConfiguration {
    public ZoneConfigurationWithChannelData(TCJProcess p, String e, String hiddenEvent, Valuation globalEnv,
                                            boolean isDataOperation, String name, Expression[] expressions, DBM dbm) {
        super(p, e, hiddenEvent, globalEnv, isDataOperation, dbm);
        this.ChannelName = name;
        this.Expressions = expressions;
    }


    public String ChannelName;


    public Expression[] Expressions;
}
