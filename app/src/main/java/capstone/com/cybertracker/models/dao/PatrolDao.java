package capstone.com.cybertracker.models.dao;

import java.util.List;

import capstone.com.cybertracker.enums.PatrolStatusEnum;
import capstone.com.cybertracker.models.Patrol;

/**
 * Created by Arjel on 7/16/2016.
 */

public interface PatrolDao {

    public Long addPatrol(Patrol patrol);
    public List<Patrol> getPatrols();
    public Patrol getPatrolById(Long patrolId);
    public Patrol getPatrolByName(String name);
    public void updatePatrolStatus(Long patrolId, PatrolStatusEnum patrolStatus);
    public void endPatrol(Long patrolId);

}
