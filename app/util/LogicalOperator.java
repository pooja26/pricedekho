package util;

import com.google.common.base.Throwables;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Harkrishn Patro on 01/04/17 5:12 PM.
 */
public enum  LogicalOperator {
    AND(1,"and"),
    OR(2,"or");

    private int index;

    private String op;

    LogicalOperator(int index, String op) {
        this.index = index;
        this.op = op;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getOp() {
        return op;
    }

    public void setOp(String op) {
        this.op = op;
    }

    private final static Map<String, LogicalOperator> CONSTANTS = new HashMap<>();

    static {
        for (LogicalOperator c: values()) {
            CONSTANTS.put(c.op, c);
        }
    }

    public static LogicalOperator fromValue(String value) {
        LogicalOperator constant = CONSTANTS.get(value);
        if (constant == null) {
            Throwables.propagate(new IllegalArgumentException(value + " isn't supported."));
        }
        return constant;
    }
}
