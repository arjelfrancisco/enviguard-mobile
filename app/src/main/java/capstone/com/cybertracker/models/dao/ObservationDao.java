package capstone.com.cybertracker.models.dao;

import java.util.List;

import capstone.com.cybertracker.enums.ObservationTypeEnum;
import capstone.com.cybertracker.models.AnimalDirectWildlifeObservation;
import capstone.com.cybertracker.models.FloralDirectWildlifeObservation;
import capstone.com.cybertracker.models.ForestConditionObservation;
import capstone.com.cybertracker.models.IndirectWildlifeObservation;
import capstone.com.cybertracker.models.Observation;
import capstone.com.cybertracker.models.OtherObservation;
import capstone.com.cybertracker.models.ThreatObservation;

/**
 * Created by Arjel on 7/20/2016.
 */

public interface ObservationDao {

    public Long addForestConditionObservation(ForestConditionObservation forestConditionObservation);
    public Long addAnimalDirectWildlifeObservation(AnimalDirectWildlifeObservation observation);
    public Long addFloraDirectWildlifeObservation(FloralDirectWildlifeObservation observation);
    public Long addIndirectWildlifeObservation(IndirectWildlifeObservation observation);
    public Long addThreadObservation(ThreatObservation observation);
    public Long addOtherObservation(OtherObservation observation);
    public List<Observation> getObservationByPatrolId(Long patrolId);
    public Observation getObsByIdAndObsType(Long obsId, ObservationTypeEnum observationType);

}

