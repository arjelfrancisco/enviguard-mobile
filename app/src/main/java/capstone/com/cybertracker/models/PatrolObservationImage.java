package capstone.com.cybertracker.models;

import java.util.Date;

import capstone.com.cybertracker.enums.ObservationTypeEnum;

/**
 * Created by Arjel on 7/26/2016.
 */

public class PatrolObservationImage {

    private Long id;
    private Long observationId;
    private ObservationTypeEnum observationType;
    private String imageLocation;
    private String longitude;
    private String latitude;
    private Date timestamp;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getImageLocation() {
        return imageLocation;
    }

    public void setImageLocation(String imageLocation) {
        this.imageLocation = imageLocation;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
