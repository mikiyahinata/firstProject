package hina.example.interestedshop;

import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.Locale;

public class LocationActivity extends AppCompatActivity {

    private FusedLocationProviderClient fusedLocationClient;
    private Location location;

    private StringBuilder strBuf = new StringBuilder();
//    private TextView textView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        textView = findViewById((R.id.text_view));

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        LocationRequest locationRequest = new LocationRequest();

        locationRequest.setPriority(
// どれにするかはお好みで、ただしできない状況ではできないので
                LocationRequest.PRIORITY_HIGH_ACCURACY);
//                LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
//                LocationRequest.PRIORITY_LOW_POWER);
//                LocationRequest.PRIORITY_NO_POWER);

        Button button = findViewById((R.id.button_get_location));

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLastLocation();
            }
        });

    }

    @SuppressWarnings("MissingPermission")
    private void getLastLocation() {
        fusedLocationClient.getLastLocation()
                .addOnCompleteListener(
                        this,
                        new OnCompleteListener<Location>() {
                            @Override
                            public void onComplete(@NonNull Task<Location> task) {
                                if (task.isSuccessful() && task.getResult() != null) {
                                    location = task.getResult();

                                    strBuf.append((String.format(Locale.ENGLISH, "%s: %f,  ",
                                            "緯度", location.getLatitude())));
                                    strBuf.append((String.format(Locale.ENGLISH, "%s: %f\n",
                                            "経度", location.getLongitude())));
//                                    textView.setText(strBuf);
                                    Log.d("debug",strBuf.toString());

                                } else {
                                    Log.d("debug","計測不能");
//                                    textView.setText("計測不能");
                                }
                            }
                        });
    }

}