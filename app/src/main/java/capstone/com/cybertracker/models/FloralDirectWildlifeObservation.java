package capstone.com.cybertracker.models;

/**
 * Created by Arjel on 7/18/2016.
 */

public class FloralDirectWildlifeObservation extends  WildlifeObservation {

    private Integer diameter;
    private Integer noOfTrees;
    private Boolean observedThrougGathering;
    private String otherTreeSpeciedObserved;

    public Integer getDiameter() {
        return diameter;
    }

    public void setDiameter(Integer diameter) {
        this.diameter = diameter;
    }

    public Integer getNoOfTrees() {
        return noOfTrees;
    }

    public void setNoOfTrees(Integer noOfTrees) {
        this.noOfTrees = noOfTrees;
    }

    public Boolean getObservedThrougGathering() {
        return observedThrougGathering;
    }

    public void setObservedThrougGathering(Boolean observedThrougGathering) {
        this.observedThrougGathering = observedThrougGathering;
    }

    public String getOtherTreeSpeciedObserved() {
        return otherTreeSpeciedObserved;
    }

    public void setOtherTreeSpeciedObserved(String otherTreeSpeciedObserved) {
        this.otherTreeSpeciedObserved = otherTreeSpeciedObserved;
    }
}


