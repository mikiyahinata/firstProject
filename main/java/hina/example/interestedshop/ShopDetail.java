package hina.example.interestedshop;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ShopDetail extends AppCompatActivity {
    private TextView shopView;
    private String name;
    private String address;
    private String comment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //呼び出し元からの値を取得
        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        address = intent.getStringExtra("address");
        comment = intent.getStringExtra("comment");

        //画面レイアウトの設定
        setContentView(R.layout.shopdetail);

        //画面への表示設定
        shopView =(TextView)findViewById(R.id.hotelInfo);
        shopView.setText(name+"\n"+address+"\n"+ comment+"\n"
        );

    }
        //地図連携メソッド
        public void map(View view){
            //地図インテント
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("geo:0,0?q="+address));
            startActivity(intent);
        }

    // お店削除ボタン
    public void shopDelete(View view) {
        // DBのオープン
        OpenDatabase OpenDb = new OpenDatabase(this, "shop.db", null, 1);
        final SQLiteDatabase db = OpenDb.getWritableDatabase();

        try { //削除実行
            db.delete("shop_list", "Name = ?" , new String[] {name});
            setContentView(R.layout.success); //成功画面
        } catch(Exception e){
            setContentView(R.layout.fail); //失敗画面
        } finally {
            db.close();
        }
    }

    // お店変更ボタン
    public void shopUpdate(View view) {

        // インテントの作成（データを引数で渡す）
        Intent intent = new Intent(this, ShopDataEntry.class);
        intent.putExtra("mode", "update");
        intent.putExtra("name", name);
        intent.putExtra("address", address);
        intent.putExtra("comment", comment);
        startActivity(intent);

    }

    // トップに戻るボタン
    public void top(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
