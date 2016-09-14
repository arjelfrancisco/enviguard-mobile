package capstone.com.cybertracker.models;

import java.util.Date;

/**
 * Created by Arjel on 7/24/2016.
 */

public class PatrolLocation {

    private Long id;
    private Long patrolId;
    private String longitude;
    private String latitude;
    private DetailedLocation detailedLocation;
    private Date timestamp;

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

    public DetailedLocation getDetailedLocation() {
        return detailedLocation;
    }

    public void setDetailedLocation(DetailedLocation detailedLocation) {
        this.detailedLocation = detailedLocation;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
