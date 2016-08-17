package capstone.com.cybertracker.enums;

/**
 * Created by Arjel on 7/20/2016.
 */

public enum ActionTakenEnum {

    OBSERVED("Observed"),
    COLLECTED("Collected"),
    RELEASED("Released"),
    CONFISCATED("Confiscated");

    ActionTakenEnum(String label) {
        this.label = label;
    };

    private String label;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public static ActionTakenEnum labelValueOf(String label) {
        for(ActionTakenEnum actionTaken : ActionTakenEnum.values()) {
            if(actionTaken.getLabel().equals(label)) {
                return actionTaken;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return this.label;
    }
}
