package org.example.sinartweather;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import org.example.sinartweather.YahooClient;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import org.example.sinartweather.Weather;
import org.example.sinartweather.IWeatherImageProvider;
import org.example.sinartweather.WeatherImageProvider;
import org.example.sinartweather.YahooClient;
import org.example.sinartweather.WeatherPreferenceActivity;




public class MainActivity extends Activity {
private RequestQueue requestQueue;
private SharedPreferences prefs;
private MenuItem refreshItem;
TextView tvDescrWeather;
TextView tvLocation;
TextView tvTemperature;
TextView tvTempUnit;
TextView tvTempMin;
TextView tvTempMax;
TextView tvHum;
TextView tvWindSpeed;
TextView tvWindDeg;
TextView tvPress;
TextView tvPressStatus;
TextView tvVisibility;
ImageView weatherImage;
TextView tvSunrise;
TextView tvSunset;

@Override
protected void onCreate(Bundle savedInstanceState) {
Log.d("SwA", "onCreate");
super.onCreate(savedInstanceState);
setContentView(R.layout.fragment_main);
requestQueue = Volley.newRequestQueue(getApplicationContext());
prefs = PreferenceManager.getDefaultSharedPreferences(this);

tvDescrWeather = (TextView) findViewById(R.id.descrWeather);
tvLocation = (TextView) findViewById(R.id.location);
tvTemperature = (TextView) findViewById(R.id.temp);
tvTempUnit = (TextView) findViewById(R.id.tempUnit);
tvTempMin = (TextView) findViewById(R.id.tempMin);
tvTempMax = (TextView) findViewById(R.id.tempMax);
tvHum = (TextView) findViewById(R.id.humidity);
tvWindSpeed = (TextView) findViewById(R.id.windSpeed);
tvWindDeg = (TextView) findViewById(R.id.windDeg);
tvPress = (TextView) findViewById(R.id.pressure);
tvPressStatus = (TextView) findViewById(R.id.pressureStat);
tvVisibility = (TextView) findViewById(R.id.visibility);
weatherImage = (ImageView) findViewById(R.id.imgWeather);
tvSunrise = (TextView) findViewById(R.id.sunrise);
tvSunset = (TextView) findViewById(R.id.sunset);


refreshData();
}
private void refreshData() {
if (prefs == null)
return ;

String woeid = prefs.getString("woeid", null);
//Log.d("SwA", "WOEID ["+woeid+"]");
if (woeid != null) {
String loc = prefs.getString("cityName", null) + "," + prefs.getString("country", null);
String unit = prefs.getString("swa_temp_unit", null);
handleProgressBar(true);

YahooClient.getWeather(woeid, unit, requestQueue, new YahooClient.WeatherClientListener() {
@Override
public void onWeatherResponse(Weather weather) {
//Log.d("SwA", "Weather ["+weather+"] - Cond ["+weather.condition+"] - Temp ["+weather.condition.temp+"]");
int code = weather.condition.code;

tvDescrWeather.setText(weather.condition.description);
tvLocation.setText(weather.location.name + "," + weather.location.region + "," + weather.location.country);
// Temperature data
tvTemperature.setText("" + weather.condition.temp);

int resId = getResource(weather.units.temperature, weather.condition.temp);
( (TextView) findViewById(R.id.lineTxt)).setBackgroundResource(resId);

tvTempUnit.setText(weather.units.temperature);
tvTempMin.setText("" + weather.forecast.tempMin + " " + weather.units.temperature);
tvTempMax.setText("" + weather.forecast.tempMax + " " + weather.units.temperature);
// Humidity data
tvHum.setText("" + weather.atmosphere.humidity + " %");
// Wind data
tvWindSpeed.setText("" + weather.wind.speed + " " + weather.units.speed);
tvWindDeg.setText("" + weather.wind.direction + "°");
// Pressure data
tvPress.setText("" + weather.atmosphere.pressure + " " + weather.units.pressure);
int status = weather.atmosphere.rising;
String iconStat = "";
switch (status) {
case 0 :
iconStat = "-";
break;
case 1 :
iconStat = "+";
break;
case 2 :
iconStat = "-";
break;
};
tvPressStatus.setText(iconStat);
// Visibility
tvVisibility.setText("" + weather.atmosphere.visibility + " " + weather.units.distance);
// Astronomy
tvSunrise.setText(weather.astronomy.sunRise);
tvSunset.setText(weather.astronomy.sunSet);
// Update
IWeatherImageProvider provider = new WeatherImageProvider();
provider.getImage(code, requestQueue, new IWeatherImageProvider.WeatherImageListener() {
@Override
public void onImageReady(Bitmap image) {
weatherImage.setImageBitmap(image);
}
});
handleProgressBar(false);
}
});
}
}

// and  in the MainActivity.java we have:

@Override
public boolean onCreateOptionsMenu(Menu menu) {
// Inflate the menu; this adds items to the action bar if it is present.
getMenuInflater().inflate(R.menu.main, menu);
return true;
}
@Override
public boolean onOptionsItemSelected(MenuItem item) {
// Handle action bar item clicks here. The action bar will
// automatically handle clicks on the Home/Up button, so long
// as you specify a parent activity in AndroidManifest.xml.
int id = item.getItemId();
if (id == R.id.action_settings) {
Intent i = new Intent();
i.setClass(this, WeatherPreferenceActivity.class);
startActivity(i);
}
else if (id == R.id.action_refresh) {
refreshItem = item;
refreshData();
}
else if (id == R.id.action_share) {
String playStoreLink = "https://play.google.com/store/apps/details?id=" +
getPackageName();

Intent sendIntent = new Intent();
sendIntent.setAction(Intent.ACTION_SEND);

sendIntent.setType("text/plain");
startActivity(sendIntent);
}

return super.onOptionsItemSelected(item);
}

private void handleProgressBar(boolean visible) {
if (refreshItem == null)
return ;
if (visible)
refreshItem.setActionView(R.layout.progress_bar);
else
refreshItem.setActionView(null);
}
private float convertToC(String unit, float val) {
if (unit.equalsIgnoreCase("°C"))
return val;
return (float) ((val - 32) / 1.8);
}
private int getResource(String unit, float val) {
float temp = convertToC(unit, val);
Log.d("SwA", "Temp ["+temp+"]");
int resId = 0;
if (temp < 10)
resId = R.drawable.line_shape_blue;
else if (temp >= 10 && temp <=24)
resId = R.drawable.line_shape_green;
else if (temp > 25)
resId = R.drawable.line_shape_red;
return resId;
}
}