package util;

import com.google.common.base.Throwables;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Harkrishn Patro on 01/04/17 4:12 PM.
 */
public enum RelationalOperator {
    EQUALS(1,"="),
    NOT_EQUAL(2,"!="),
    GREATER_THAN(3,">"),
    LESS_THAN(4,"<"),
    GREATER_THAN_EQUAL(5,">="),
    LESS_THAN_EQUAL(6,"<=");

    private int index;
    private String rOp;

    RelationalOperator(int index, String rOp) {
        this.index = index;
        this.rOp = rOp;
    }

    public int getIndex() {
        return index;
    }

    public String getrOp() { return rOp; }

    private final static Map<String, RelationalOperator> CONSTANTS = new HashMap<>();

    static {
        for (RelationalOperator c: values()) {
            CONSTANTS.put(c.rOp, c);
        }
    }

    public static RelationalOperator fromValue(String value) {
        RelationalOperator constant = CONSTANTS.get(value);
        if (constant == null) {
            Throwables.propagate(new IllegalArgumentException(value + " isn't supported."));
        }
        return constant;
    }

}
