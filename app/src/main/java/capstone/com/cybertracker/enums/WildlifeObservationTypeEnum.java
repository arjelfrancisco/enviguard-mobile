package capstone.com.cybertracker.enums;

/**
 * Created by Arjel on 7/17/2016.
 */

public enum WildlifeObservationTypeEnum {

    DIRECT("Direct Observation"),
    INDIRECT("Indirect Observation");

    WildlifeObservationTypeEnum(String label) {
        this.label = label;
    }

    private String label;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return this.getLabel();
    }
}
