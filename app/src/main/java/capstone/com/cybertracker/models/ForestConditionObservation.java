package capstone.com.cybertracker.models;

import capstone.com.cybertracker.enums.DensityOfRegenerantsEnum;
import capstone.com.cybertracker.enums.ForestConditionTypeEnum;

/**
 * Created by Arjel on 7/17/2016.
 */

public class ForestConditionObservation extends Observation {

    private Long forestConditionObservationId;
    private ForestConditionTypeEnum forestConditionType;
    private boolean presenceOfRegenerants;
    private DensityOfRegenerantsEnum densityOfRegenerants;

    public Long getForestConditionObservationId() {
        return forestConditionObservationId;
    }

    public void setForestConditionObservationId(Long forestConditionObservationId) {
        this.forestConditionObservationId = forestConditionObservationId;
    }

    public ForestConditionTypeEnum getForestConditionType() {
        return forestConditionType;
    }

    public void setForestConditionType(ForestConditionTypeEnum forestConditionType) {
        this.forestConditionType = forestConditionType;
    }

    public boolean isPresenceOfRegenerants() {
        return presenceOfRegenerants;
    }

    public void setPresenceOfRegenerants(boolean presenceOfRegenerants) {
        this.presenceOfRegenerants = presenceOfRegenerants;
    }

    public DensityOfRegenerantsEnum getDensityOfRegenerants() {
        return densityOfRegenerants;
    }

    public void setDensityOfRegenerants(DensityOfRegenerantsEnum densityOfRegenerants) {
        this.densityOfRegenerants = densityOfRegenerants;
    }

    @Override
    public String toString() {
        return "ForestConditionObservation{" +
                "forestConditionObservationId=" + forestConditionObservationId +
                ", forestConditionType=" + forestConditionType +
                ", presenceOfRegenerants=" + presenceOfRegenerants +
                ", densityOfRegenerants=" + densityOfRegenerants +
                '}';
    }
}
