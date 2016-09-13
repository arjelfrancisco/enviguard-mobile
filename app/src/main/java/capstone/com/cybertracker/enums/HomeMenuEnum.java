package capstone.com.cybertracker.enums;

/**
 * Created by Arjel on 7/15/2016.
 */

public enum HomeMenuEnum {

    START_CYBERTRACKER("Start Enviguard"),
    EXIT("Exit");

    HomeMenuEnum(String label) {
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
