package capstone.com.cybertracker.models.dao;

import java.util.List;

import capstone.com.cybertracker.models.Patroller;

/**
 * Created by Arjel on 7/17/2016.
 */

public interface PatrollerDao {

    public List<Patroller> getPatrollers();
    public void clearPatrollers();
    public void addPatrollers(List<Patroller> patrollers);
}
