package capstone.com.cybertracker.models;

import java.util.Date;

import capstone.com.cybertracker.enums.PatrolStatusEnum;

/**
 * Created by Arjel on 7/16/2016.
 */

public class Patrol {

    private Long id;
    private String name;
    private String patrollerName;
    private PatrolStatusEnum status;
    private Date startDate;
    private Date endDate;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPatrollerName() {
        return patrollerName;
    }

    public void setPatrollerName(String patrollerName) {
        this.patrollerName = patrollerName;
    }

    public PatrolStatusEnum getStatus() {
        return status;
    }

    public void setStatus(PatrolStatusEnum status) {
        this.status = status;
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
}
