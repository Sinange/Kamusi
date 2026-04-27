package sinange.sinb.admn.kamusi;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class Database extends SQLiteOpenHelper {
  public static final String TABLE_NAME="Kamusi";
  public  static final String COLUMN_ID="id";
  public static final String COLUMN_JINA="jina";
  public static final String COLUMN_kundi="neno";
  public static final String COLUMN_MAANA="maana";
  public static final String COLUMN_MPILI="maanapili";
    public static final String COLUMN_KISAWE="kisawe";
    public static final String COLUMN_CONJUGATIION="mnyambuliko";
    public static final String COLUMN_MFANO="mfano";
  public static final String COLUMN_MFANOPILI="mfanopili";
    Context context;
    public Database(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.context=context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
      String createTableQuery = "CREATE TABLE " + TABLE_NAME + "(" +
              COLUMN_ID + " TEXT, " +
              COLUMN_JINA + " TEXT, " +
              COLUMN_kundi + " TEXT, " +
              COLUMN_MAANA + " TEXT, " +
              COLUMN_MPILI  + " TEXT, "  +
              COLUMN_KISAWE + " TEXT, " +
              COLUMN_CONJUGATIION + " TEXT, " +
              COLUMN_MFANO + " TEXT, "  +
              COLUMN_MFANOPILI + " TEXT)";
        db.execSQL(createTableQuery);
        db.execSQL("create unique index idx_id ON "+ TABLE_NAME +"(" +COLUMN_ID +")");
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    db.execSQL("drop table if exists "+TABLE_NAME);
    onCreate(db);
    }
}
