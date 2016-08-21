package capstone.com.cybertracker.models;

import java.util.List;

/**
 * Created by Arjel on 8/21/2016.
 */

public class PatrolResponseStatus {

    private Boolean successful;
    private String message;
    private List<SendPatrolResponse> sendPatrolResponseList;

    public PatrolResponseStatus(Boolean successful, String message, List<SendPatrolResponse> sendPatrolResponseList) {
        this.successful = successful;
        this.message = message;
        this.sendPatrolResponseList = sendPatrolResponseList;
    }

    public Boolean getSuccessful() {
        return successful;
    }

    public void setSuccessful(Boolean successful) {
        this.successful = successful;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<SendPatrolResponse> getSendPatrolResponseList() {
        return sendPatrolResponseList;
    }

    public void setSendPatrolResponseList(List<SendPatrolResponse> sendPatrolResponseList) {
        this.sendPatrolResponseList = sendPatrolResponseList;
    }
}
