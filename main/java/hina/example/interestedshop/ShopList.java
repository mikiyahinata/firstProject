package hina.example.interestedshop;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import androidx.cursoradapter.widget.SimpleCursorAdapter;

public class ShopList extends ListActivity {
    //カーソル宣言
    Cursor cursor;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shoplist); //画面レイアウト

        //DBのオープン
        OpenDatabase OpenDb = new OpenDatabase(this, "shop.db", null, 1);
        final SQLiteDatabase db = OpenDb.getReadableDatabase();

        //データ取得（カーソル）
        cursor = db.query("shop_list",
                new String[]{"_id", "name", "address", "comment"},
                null, null, null, null, null, null);

        //アダプタ作成
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
                android.R.layout.simple_list_item_1,
                cursor,//カーソル
                new String[]{"name"},//表示するカラム名
                new int[]{android.R.id.text1}//データを適用するTextViewのID
                , 0);

        //アダプタのセット
        setListAdapter(adapter);

        //DBを閉じる
        db.close();

        // リストビューがタップされた時の処理（内部クラスを使う）
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() { //内部クラス
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //Intentの作成
                Intent intent = new Intent(ShopList.this, ShopDetail.class);

                //Intentにデータ設定
                cursor.moveToPosition(position);
                intent.putExtra("name", cursor.getString(1));
                intent.putExtra("address", cursor.getString(2));
                intent.putExtra("comment", cursor.getString(3));

                //画面遷移
                startActivity(intent);
            }
        });
    }
}
