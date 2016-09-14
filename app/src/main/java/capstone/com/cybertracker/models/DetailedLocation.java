package capstone.com.cybertracker.models;

/**
 * Created by Arjel on 9/14/2016.
 */
public class DetailedLocation {

    private String region;
    private String city;
    private String street;

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    @Override
    public String toString() {
        return "DetailedLocation{" +
                "region='" + region + '\'' +
                ", city='" + city + '\'' +
                ", street='" + street + '\'' +
                '}';
    }
}
