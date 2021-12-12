package hina.example.interestedshop;

import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ShopDataEntry extends AppCompatActivity {
    private String mode; // 実行モード（insert or update)
    private ContentValues values; // データを入れる箱
    private TextView name;
    private TextView address;
    private TextView comment;

    private FusedLocationProviderClient fusedLocationClient;
    private Location location;
    private double latitude = Double.MAX_VALUE;
    private double longitude = Double.MAX_VALUE;

    private StringBuilder strBuf = new StringBuilder();

    private String TAG = "shop_data_entry";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shopinput);

        Intent intent = getIntent(); // 引数を受け取るインテント
        mode = intent.getStringExtra("mode"); // 実行モード（insert or update)

        Button button_get_location = findViewById((R.id.button_get_location));
        if (mode.equals("insert")) {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            button_get_location.setEnabled(true);//位置情報取得ボタンを有効化
            //位置情報を取得、住所へ変換・出力する
            getLastLocation();

        } else if (mode.equals("update")) {
            getData(); // データの取得
            button_get_location.setEnabled(false);//位置情報取得ボタンを無効化

            //取得したデータを画面に入れる
            name.setText(intent.getStringExtra("name"));
            address.setText(intent.getStringExtra("address"));
            comment.setText(intent.getStringExtra("comment"));

        }

    }

    //データの追加・更新ボタン
    public void shopInput(View view) {

        getData(); // データの取得
        // DB用にデータ生成
        if (makeValues()) {
            // DBのオープン
            OpenDatabase OpenDb = new OpenDatabase(this, "shop.db", null, 1);
            final SQLiteDatabase db = OpenDb.getWritableDatabase();

            long ret = -1; // データ挿入判定値

            try {
                if (mode.equals("insert")) {
                    ret = db.insert("shop_list", null, values);
                }

                if (mode.equals("update")) { //update処理
                    ret = db.update("shop_list", values, "Name = ?",
                            new String[]{name.getText().toString()});
                }
                // update処理

            } finally {
                db.close();
            }

            if (ret == -1) { // データ挿入失敗
                setContentView(R.layout.fail);
            } else { // データ挿入成功
                setContentView(R.layout.success);
            }
        }

    }

    public void getData() { // データの取得
        name = (TextView) this.findViewById(R.id.input_name);
        address = (TextView) this.findViewById(R.id.input_address);
        comment = (TextView) this.findViewById(R.id.input_comment);
    }

    public boolean makeValues() { // DB用にデータ生成
        values = new ContentValues(); // データを入れる箱
        values.put("name", name.getText().toString());
        values.put("address", address.getText().toString());
        values.put("comment", comment.getText().toString());
        //入力チェック
        if (name.getText().toString().equals("")) {
            setContentView(R.layout.fail);

            Resources res = getResources(); //リソース取得
            String err_msg = res.getString(R.string.err_msg); //エラーメッセージ取得

            Toast.makeText(this, err_msg, Toast.LENGTH_LONG).show();
            return false;
        } else {
            return true;
        }
    }

    // トップに戻るボタン
    public void top(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    // 候補ボタン(位置から取得)
    public void getCandidates(View view) throws IOException {

//        if(location != null){
            //HTTPヘッダ
            Map<String,String> headers=new HashMap<String,String>();
    //        headers.put("X-Example-Header","Example-Value");
            String endpoint = "http://webservice.recruit.co.jp/hotpepper/gourmet/v1/?key=12a7ceb6456a963b&lat=35.748117&lng=139.803720&range=1&format=json";
    //        String endpoint = "http://webservice.recruit.co.jp/hotpepper/gourmet/v1/?key=12a7ceb6456a963b&lat=" + location.getLatitude() + "&lng=1" + location.getLongitude() + "&range=1&format=json";
            String result = HttpClient.get(endpoint,"UTF-8",headers);
        Log.d("debug", result);
        name.setText(result);

//        }
    }

    // 位置取得ボタン
    public void getLocation(View view) {
        getLastLocation();
    }

    /**
     * 位置情報を取得、住所へ変換・出力する
     */
    @SuppressWarnings("MissingPermission")
    public void getLastLocation() {
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
                                    Log.d("debug", strBuf.toString());
                                    //住所に変換し住所欄へ出力する
                                    getData();
                                    address.setText(getAddress(location.getLatitude(), location.getLongitude()));
//                                    address.setText(getAddress(34.206417, 132.994896));
                                    //34.010676, 134.569767 tokushima
                                    //35.738449, 139.797683 jitaku
                                    //34.206417, 132.994896 shimanami


                                } else {
                                    Log.d("debug", "計測不能");
                                    getData();
                                    address.setText(getAddress(Double.MAX_VALUE, 0));
                                }
                            }
                        });
    }

    //位置情報（緯度・経度）→住所情報取得
    private String getAddress(double latitude, double longitude) {
        String result = null;
        List<Address> addressList = new ArrayList<>();//変換後の住所
        String errorMessage = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addressList = geocoder.getFromLocation(
                    latitude,
                    longitude,
                    // In this sample, get just a single address.
                    1);
        } catch (IOException ioException) {
            // Catch network or other I/O problems.
            errorMessage = getString(R.string.address_service_not_available);
            Log.e(TAG, errorMessage, ioException);
            result = getString(R.string.address_service_not_available);
        } catch (IllegalArgumentException illegalArgumentException) {
            // Catch invalid latitude or longitude values.
            errorMessage = getString(R.string.invalid_lat_long_used);
            Log.e(TAG, errorMessage + ". " +
                    "Latitude = " + latitude +
                    ", Longitude = " +
                    longitude, illegalArgumentException);
            result = "計測不能 googleMapを起動してから再度試してみてください";
        }

        if (!addressList.isEmpty()) {
            Address address = addressList.get(0);
            String addressLine = address.getAddressLine(0);
            int fspidx = addressLine.indexOf(" ");
            result = addressLine.substring(fspidx + 1);

        }
        return result;
    }

}
