package capstone.com.cybertracker.models.dao;

import java.util.List;

import capstone.com.cybertracker.enums.ObservationTypeEnum;
import capstone.com.cybertracker.models.PatrolObservationImage;

/**
 * Created by Arjel on 7/26/2016.
 */

public interface ImageDao {

    public void addObservationImage(PatrolObservationImage patrolObservationImage);
    public List<PatrolObservationImage> getImagesByObsIdAndObsType(Long observationId, ObservationTypeEnum observationType);
    public void addTempObservationImage(PatrolObservationImage patrolObservationImage);
    public List<PatrolObservationImage> getTemporaryPatrolObservationImages();
    public void clearTempPatrolObservationImages();

}

