package capstone.com.cybertracker.enums;

/**
 * Created by Arjel on 7/18/2016.
 */

public enum ResponseToThreatEnum {

    RECORDED("Recorded"),
    ACTED_UPON("Acted Upon"),
    RESOLVED("Resolved");

    ResponseToThreatEnum(String label) {
        this.label = label;
    }

    private String label;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public static ResponseToThreatEnum labelValueOf(String label) {
        for(ResponseToThreatEnum responseToThreatEnum : ResponseToThreatEnum.values()) {
            if(responseToThreatEnum.getLabel().equals(label)) {
                return responseToThreatEnum;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return this.getLabel();
    }

}
