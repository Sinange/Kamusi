package sinange.sinb.admn.kamusi;

import android.app.Application;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class DatabaseAdder extends Application {

    Database database;
    SQLiteDatabase sqLiteDatabase;
    ContentValues contentValues;


    @Override
    public void onCreate() {
        super.onCreate();
        database = new Database(this, "kamusi.db", null, 1);
        DataToDatabase();
    }

    public Database getDatabase() {
        return database;
    }

    public void DataToDatabase() {
        SharedPreferences preferences = getSharedPreferences("kamusi", Context.MODE_PRIVATE);
        boolean dataLoaded = preferences.getBoolean("kamusi", false);
        if (dataLoaded) {
            return;
        }

        if (database == null) {
            return;
        }

        new Thread(() -> {
            sqLiteDatabase = database.getWritableDatabase();
            try {
                InputStream inputStream =getApplicationContext().getResources().openRawResource(R.raw.word);
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                int bytes = 0;
                StringBuilder builder = new StringBuilder();
                char[] buff = new char[32768];
                while ((bytes = reader.read(buff)) != -1) {
                    builder.append(buff, 0, bytes);
                }
                String text = builder.toString();
                JSONObject object = new JSONObject(text);
                JSONObject words = new JSONObject();
                sqLiteDatabase.beginTransaction();
                try {
                    for (int i = 0; i <=16683; i++) {
                        String key = String.valueOf(i);
                        if(object.has(key)) {
//                            words.put(key,object.optJSONObject(String.valueOf(i)).put("KundiLaNeno","").put("ngeli","").put("sauti","").put("mfano",""));
                            String jina= object.getJSONObject(key).getString("Word");
                            String type= object.getJSONObject(key).getString("KundiLaNeno");
                            String ngeli= object.getJSONObject(key).getString("ngeli");
                            String maana= object.getJSONObject(key).getString("Meaning");
                            String kisawe= object.getJSONObject(key).getString("Synonyms");
                            String mnyambuliko= object.getJSONObject(key).getString("Conjugation");
                            String [] maanaParts=maana.split("\\|");

                            if (maanaParts.length>1) {
                                contentValues=new ContentValues();
                                contentValues.put(Database.COLUMN_ID,key);
                                contentValues.put(Database.COLUMN_JINA,jina);
                                contentValues.put(Database.COLUMN_KISAWE,kisawe);
                                contentValues.put(Database.COLUMN_CONJUGATIION,mnyambuliko);
                                contentValues.put(Database.COLUMN_kundi, type);
                                contentValues.put(Database.COLUMN_ngeli, ngeli);


                                String mkwanza = maanaParts[0];
                                String mpili = maanaParts[1];
                                String [] mkwa =mkwanza.split(":");
                                String[] mpil = mpili.split(":");
                                if (mkwa.length > 1) {
                                    String meaning = mkwa[0];
                                    String mfano = mkwa[1];
                                    contentValues.put(Database.COLUMN_MAANA,meaning);
                                    contentValues.put(Database.COLUMN_MFANO,mfano);

                                }else{
                                    String meaning = mkwa[0];
                                    contentValues.put(Database.COLUMN_MAANA,meaning);
                                    contentValues.put(Database.COLUMN_MFANO," ");


                                }
                                if (mpil.length > 1) {
                                    String meaning = mpil[0];
                                    String mfano = mpil[1];
                                    contentValues.put(Database.COLUMN_MPILI,meaning);
                                    contentValues.put(Database.COLUMN_MFANOPILI,mfano);

                                }else{
                                    String meaning = mpil[0];
                                    contentValues.put(Database.COLUMN_MPILI,meaning);
                                    contentValues.put(Database.COLUMN_MFANOPILI," ");


                                }


                            }else{
                                contentValues=new ContentValues();
                                contentValues.put(Database.COLUMN_ID,key);
                                contentValues.put(Database.COLUMN_JINA,jina);
                                contentValues.put(Database.COLUMN_KISAWE,kisawe);
                                contentValues.put(Database.COLUMN_CONJUGATIION,mnyambuliko);
                                contentValues.put(Database.COLUMN_kundi, type);
                                contentValues.put(Database.COLUMN_ngeli, ngeli);
                                String mkwanza =maanaParts[0];
                                contentValues.put(Database.COLUMN_MPILI,"");
                                String [] split =mkwanza.split(":");
                                if (split.length > 1) {
                                    String meaning = split[0];
                                    String mfano = split[1];
                                    contentValues.put(Database.COLUMN_MAANA,meaning);
                                    contentValues.put(Database.COLUMN_MFANO,mfano);
                                    contentValues.put(Database.COLUMN_MFANOPILI,"");

                                }else{
                                    String meaning = split[0];
                                    contentValues.put(Database.COLUMN_MAANA,meaning);
                                    contentValues.put(Database.COLUMN_MFANO,"");
                                    contentValues.put(Database.COLUMN_MFANOPILI,"");

                                }

                            }


//                            System.out.println(key+"\n"+jina+"\n"+maana+"\n"+kisawe+"\n"+mnyambuliko+"\n");

                            long id=  sqLiteDatabase.insertWithOnConflict(Database.TABLE_NAME, null, contentValues,SQLiteDatabase.CONFLICT_IGNORE);
                          preferences.edit().putBoolean("kamusi",true).apply();
//                            System.out.println(id);
                        }


                    }
                    sqLiteDatabase.setTransactionSuccessful();


//                JSONObject words=new JSONObject(hash);
//
//                    File file=new File("storage/emulated/0/Kamusi");
//                    if (!file.exists()) {
//                        file.mkdirs();
//                    }
//
//                    FileWriter writer=new FileWriter(file+"/word1.json");
//                    writer.write(words.toString());
//                    writer.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }finally{
                    sqLiteDatabase.endTransaction();
                }
            } catch (JSONException | IOException e) {
                e.printStackTrace();
            }finally {
                if (sqLiteDatabase != null && sqLiteDatabase.isOpen()) {
                    sqLiteDatabase.close();

                }
            }
        }).start();


    }

}