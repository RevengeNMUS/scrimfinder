import java.lang.foreign.AddressLayout;
import java.util.Locale;

/**
 * A location on Earth
 * Has a
 * Latitude
 * Longitude
 * Address
 * City
 * State
 * Country
 */
public class Location {
    public final double longitude;
    public final double latitude;
    public final String address;
    public final String city;
    public final String state;
    public final String country;

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
}
