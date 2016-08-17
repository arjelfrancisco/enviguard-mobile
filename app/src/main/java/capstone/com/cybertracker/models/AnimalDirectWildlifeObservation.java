package capstone.com.cybertracker.models;

import capstone.com.cybertracker.enums.ActionTakenEnum;

/**
 * Created by Arjel on 7/18/2016.
 */

public class AnimalDirectWildlifeObservation extends WildlifeObservation {

    private Integer noOfMaleAdults;
    private Integer noOfFemaleAdults;
    private Integer noOfYoung;
    private Integer noOfUndetermined;
    private ActionTakenEnum actionTaken;
    private Boolean observedThroughHunting;

    public Integer getNoOfMaleAdults() {
        return noOfMaleAdults;
    }

    public void setNoOfMaleAdults(Integer noOfMaleAdults) {
        this.noOfMaleAdults = noOfMaleAdults;
    }

    public Integer getNoOfFemaleAdults() {
        return noOfFemaleAdults;
    }

    public void setNoOfFemaleAdults(Integer noOfFemaleAdults) {
        this.noOfFemaleAdults = noOfFemaleAdults;
    }

    public Integer getNoOfYoung() {
        return noOfYoung;
    }

    public void setNoOfYoung(Integer noOfYoung) {
        this.noOfYoung = noOfYoung;
    }

    public Integer getNoOfUndetermined() {
        return noOfUndetermined;
    }

    public void setNoOfUndetermined(Integer noOfUndetermined) {
        this.noOfUndetermined = noOfUndetermined;
    }

    public ActionTakenEnum getActionTaken() {
        return actionTaken;
    }

    public void setActionTaken(ActionTakenEnum actionTaken) {
        this.actionTaken = actionTaken;
    }

    public Boolean getObservedThroughHunting() {
        return observedThroughHunting;
    }

    public void setObservedThroughHunting(Boolean observedThroughHunting) {
        this.observedThroughHunting = observedThroughHunting;
    }
}
