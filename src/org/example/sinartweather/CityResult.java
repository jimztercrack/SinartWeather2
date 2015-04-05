package org.example.sinartweather;

public class CityResult {
private String woeid;
private String cityName;
private String country;
public CityResult() {}
public CityResult(String woeid, String cityName, String country) {
this.woeid = woeid;
this.cityName = cityName;
this.country = country;
}
public String getWoeid() {
return woeid;
}
public void setWoeid(String woeid) {
this.woeid = woeid;
}
public String getCityName() {
return cityName;
}
public void setCityName(String cityName) {
this.cityName = cityName;
}
public String getCountry() {
return country;
}
public void setCountry(String country) {
this.country = country;
}
@Override
public String toString() {
return cityName + "," + country;
}
}