package capstone.com.cybertracker.enums;

/**
 * Created by Arjel on 7/18/2016.
 */

public enum EvidenceEnum {

    TRACK("Tracks"),
    DUNG("Dung"),
    SOUND("Sound"),
    FEEDING("Feeding"),
    SCRAPE("Scrapes"),
    SCENT("Scent");

    EvidenceEnum(String label) {
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
