package com.scrimfinder.EDC;

import com.fasterxml.jackson.annotation.JsonProperty;

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
    @JsonProperty("longitude")
    public final double longitude; //todo: make into a littol list?!??! vector??!!?
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

    public static Location parseLocation(String loc) {
        return NULL_LOCATION;
        //aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa
        //i aint coding all that twin ✌️
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
