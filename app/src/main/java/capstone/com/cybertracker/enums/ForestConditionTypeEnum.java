package capstone.com.cybertracker.enums;

/**
 * Created by Arjel on 7/17/2016.
 */

public enum ForestConditionTypeEnum {

    OLD_GROWTH_FOREST("Old Growth Forest"),
    ADVANCE_SECONDARY_FOREST("Advance Secondary Forest"),
    EARLY_SECONDARY_FOREST("Early Secondary Forest"),
    CULTIVATED_AREA("Cultivated Area"),
    NON_FOREST("Non Forest");


    ForestConditionTypeEnum(String label) {
        this.label = label;
        };

    private String label;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public static ForestConditionTypeEnum labelValueOf(String label) {
        for(ForestConditionTypeEnum forestConditionType : ForestConditionTypeEnum.values()) {
            if(forestConditionType.getLabel().equals(label)) {
                return forestConditionType;
            }
        }
        return null;
    }


    @Override
    public String toString() {
        return this.label;
    }
}
