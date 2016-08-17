package capstone.com.cybertracker.models;

import capstone.com.cybertracker.enums.ResponseToThreatEnum;

/**
 * Created by Arjel on 7/18/2016.
 */

public class ThreatObservation extends Observation {

    private String threatType;
    private Integer distanceOfThreatFromWaypoint;
    private Integer bearingOfThreatFromWaypoint;
    private ResponseToThreatEnum responseToThreat;
    private String note;

    public String getThreatType() {
        return threatType;
    }

    public void setThreatType(String threatType) {
        this.threatType = threatType;
    }

    public Integer getDistanceOfThreatFromWaypoint() {
        return distanceOfThreatFromWaypoint;
    }

    public void setDistanceOfThreatFromWaypoint(Integer distanceOfThreatFromWaypoint) {
        this.distanceOfThreatFromWaypoint = distanceOfThreatFromWaypoint;
    }

    public Integer getBearingOfThreatFromWaypoint() {
        return bearingOfThreatFromWaypoint;
    }

    public void setBearingOfThreatFromWaypoint(Integer bearingOfThreatFromWaypoint) {
        this.bearingOfThreatFromWaypoint = bearingOfThreatFromWaypoint;
    }

    public ResponseToThreatEnum getResponseToThreat() {
        return responseToThreat;
    }

    public void setResponseToThreat(ResponseToThreatEnum responseToThreat) {
        this.responseToThreat = responseToThreat;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
