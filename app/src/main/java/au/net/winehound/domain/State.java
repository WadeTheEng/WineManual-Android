package au.net.winehound.domain;

import java.io.Serializable;

public enum State implements Serializable {

    Victoria(1, "Victoria"),
    South_Australia(2, "South Australia"),
    New_South_Wales(3, "New South Wales"),
    Western_Australia(4, "Western Australia"),
    Queensland(5, "Queensland"),
    ACT(6, "ACT"),
    Tasmania(8, "Tasmania"),
    National(9, "National");

    private final int key;
    private final String label;

    State(int key, String label) {
        this.key = key;
        this.label = label;
    }

    public static State fromKey(int key) {
        for (State type : State.values()) {
            if (type.getKey() == key) {
                return type;
            }
        }
        return null;
    }

    public static State[] getAll() {
        return State.class.getEnumConstants();
    }

    public int getKey() {
        return this.key;
    }

    public String toString() {
        return label;
    }
}
