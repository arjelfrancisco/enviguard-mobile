package capstone.com.cybertracker.models;

import capstone.com.cybertracker.enums.ObservationTypeEnum;

/**
 * Created by Arjel on 8/18/2016.
 */

public class SendPatrolResponse {

    private Long webPatrolObsId;
    private Long observationId;
    private ObservationTypeEnum observationType;

    public Long getWebPatrolObsId() {
        return webPatrolObsId;
    }

    public void setWebPatrolObsId(Long webPatrolObsId) {
        this.webPatrolObsId = webPatrolObsId;
    }

    public Long getObservationId() {
        return observationId;
    }

    public void setObservationId(Long observationId) {
        this.observationId = observationId;
    }

    public ObservationTypeEnum getObservationType() {
        return observationType;
    }

    public void setObservationType(ObservationTypeEnum observationType) {
        this.observationType = observationType;
    }
}
