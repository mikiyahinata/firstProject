package hina.example.interestedshop;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

public class OpenDatabase extends SQLiteOpenHelper {

    private String[][] shopData= {
            {"あの店","東京都港区芝公園3-3-1","東京タワー近辺"},
            {"その店","鳥取県米子市弥生町8-27","米子駅徒歩５分"},
    };

    public OpenDatabase(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public void onCreate(SQLiteDatabase db) {
//テーブル作成文
        String sql="CREATE TABLE [shop_list] ("+
                "[_id] INTEGER PRIMARY KEY AUTOINCREMENT,"+
                "[name] VARCHAR(100) NOT NULL UNIQUE,"+
                "[address] VARCHAR(200) NOT NULL,"+
                "[comment] VARCHAR(500) NOT NULL"+
                ");";

        db.beginTransaction();
        try {
            db.execSQL(sql);//テーブル生成
            SQLiteStatement stmt = db.compileStatement("INSERT INTO shop_list" +
                    "(name, address, comment) values (?, ?, ?);");
            for (String[] data : shopData) {//繰り返しでデータ挿入
                stmt.bindString(1, data[0]);
                stmt.bindString(2, data[1]);
                stmt.bindString(3, data[2]);
                stmt.executeInsert();
            }
            db.setTransactionSuccessful();
        }catch (Exception e){
            Log.v("shop.db","DB error:"+e.toString());
        } finally {
            db.endTransaction();
        }
    }

    public void onUpgrade(SQLiteDatabase arg0, int arg1,int arg2 ){}
}
