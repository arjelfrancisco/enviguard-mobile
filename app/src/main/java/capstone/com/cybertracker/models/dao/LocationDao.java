package capstone.com.cybertracker.models.dao;


import java.util.List;

import capstone.com.cybertracker.models.PatrolLocation;

/**
 * Created by Arjel on 7/24/2016.
 */

public interface LocationDao {

    public void addLocation(PatrolLocation patrolLocation);
    public List<PatrolLocation> getLocationByPatrolId(Long patrolId);

}
