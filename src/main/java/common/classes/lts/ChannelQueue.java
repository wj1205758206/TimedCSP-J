package common.classes.lts;

import com.sun.jmx.remote.internal.ArrayQueue;
import common.classes.expressions.expressionclass.Expression;
import common.classes.expressions.expressionclass.ExpressionValue;
import common.classes.ultility.Ultility;

import java.util.ArrayDeque;
import java.util.Queue;

public final class ChannelQueue extends ArrayDeque<ExpressionValue[]> {
    public int size;

    public ChannelQueue(int s) {
        this.size = s;
    }

    public ChannelQueue clone() {
        ChannelQueue channelQueue = new ChannelQueue(this.size);
        for (ExpressionValue[] item : this) {
            channelQueue.add(item);
        }
        return channelQueue;
    }

    @Override
    public String toString() {
        if (this.size() > 0) {
            StringBuilder stringBuilder = new StringBuilder();
            for (ExpressionValue[] array : this) {
                if (array.length == 1) {
                    stringBuilder.append(array[0] + ",");
                } else {
                    stringBuilder.append("[" + Ultility.pPStringList(array) + "],");
                }
            }
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            return stringBuilder.toString();
        }
        return "";
    }

    public String getID() {
        if (this.size() > 0) {
            StringBuilder stringBuilder = new StringBuilder();
            for (ExpressionValue[] item : this) {
                stringBuilder.append(Ultility.pPIDListDot(item) + ",");
            }

            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            return stringBuilder.toString();
        }
        return "";
    }

    public boolean isFull() {
        return this.size() == this.size;
    }

    public boolean isEmpty() {
        return this.size() == 0;
    }
}
