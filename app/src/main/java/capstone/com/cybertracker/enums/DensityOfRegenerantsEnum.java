package capstone.com.cybertracker.enums;

/**
 * Created by Arjel on 7/17/2016.
 */

public enum DensityOfRegenerantsEnum {

    DENSE("Dense"),
    MODERATE("Moderate"),
    SPARSE("Sparse");

    DensityOfRegenerantsEnum(String label) {
        this.label = label;
    };

    private String label;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public static DensityOfRegenerantsEnum labelValueOf(String label) {
        for(DensityOfRegenerantsEnum densityOfRegenerantsEnum : DensityOfRegenerantsEnum.values()) {
            if(densityOfRegenerantsEnum.getLabel().equals(label)) {
                return densityOfRegenerantsEnum;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return this.label;
    }

}
