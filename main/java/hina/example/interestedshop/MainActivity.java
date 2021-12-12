package hina.example.interestedshop;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // インスタンス作成（まだDBはできない）
        OpenDatabase OpenDb = new OpenDatabase(this, "shop.db", null,1);
        // DBにアクセス（と同時にDB作成）
        SQLiteDatabase db = OpenDb.getWritableDatabase();
        //DBを閉じる
        db.close();
    }

    //お店一覧ボタン
    public void shopList(View view){
        Intent intent = new Intent(this, ShopList.class);
        startActivity(intent);
    }

    // お店追加画面を表示するボタン
    public void shopDataEntry(View view) {
        Intent intent = new Intent(this, ShopDataEntry.class);
        intent.putExtra("mode", "insert");
        startActivity(intent);
    }
}
