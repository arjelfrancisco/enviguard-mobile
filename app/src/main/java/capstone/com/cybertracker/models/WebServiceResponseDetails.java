package capstone.com.cybertracker.models;

/**
 * Created by Arjel on 8/21/2016.
 */

public class WebServiceResponseDetails {

    private Boolean successful;
    private String message;

    public WebServiceResponseDetails(Boolean successful, String message) {
        this.successful = successful;
        this.message = message;
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
}
