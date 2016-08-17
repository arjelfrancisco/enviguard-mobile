package capstone.com.cybertracker.models;

import java.util.Date;

import capstone.com.cybertracker.enums.ObservationTypeEnum;

/**
 * Created by Arjel on 7/17/2016.
 */

public class Observation {

    private Long id;
    private Long patrolId;
    private ObservationTypeEnum observationType;
    private Date startDate;
    private Date endDate;
    // List of Images


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPatrolId() {
        return patrolId;
    }

    public void setPatrolId(Long patrolId) {
        this.patrolId = patrolId;
    }

    public ObservationTypeEnum getObservationType() {
        return observationType;
    }

    public void setObservationType(ObservationTypeEnum observationType) {
        this.observationType = observationType;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    @Override
    public String toString() {
        return "Observation{" +
                "id=" + id +
                ", patrolId=" + patrolId +
                ", observationType=" + observationType +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                '}';
    }
}
