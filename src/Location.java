import java.lang.foreign.AddressLayout;
import java.util.Locale;

/**
 * A location on Earth!<p>
 * Has a<br>
 * Latitude<br>
 * Longitude<br>
 * Address<br>
 * City<br>
 * State<br>
 * Country
 */
public class Location {
    public final double longitude;
    public final double latitude;
    public final String address;
    public final String city;
    public final String state;
    public final String country;


    public static final Location NULL_LOCATION = new Location(0, 0, "null", "null", "null", "null");

    public Location(double longitude, double latitude, String address, String city, String state, String country) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.address = address;
        this.city = city;
        this.state = state;
        this.country = country;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getAddress() {
        return address;
    }

    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
    }

    public String getState() {
        return state;
    }

    @Override
    public String toString() {
        return "Location{" +
                "longitude=" + longitude +
                ", latitude=" + latitude +
                ", address='" + address + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", country='" + country + '\'' +
                '}';
    }
}
