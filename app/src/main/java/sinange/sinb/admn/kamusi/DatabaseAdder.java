package sinange.sinb.admn.kamusi;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.JsonReader;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

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
                Set<String> nomino=new HashSet<>(Arrays.asList(
                            "mwanafunzi", "mwalimu", "daktari", "fundi", "kiongozi", "mtoto", "baba", "mama", "kaka", "dada",
                            "binamu", "jirani", "mgeni", "mfalme", "malkia", "rais", "waziri", "askari", "polisi", "mwindaji",
                            "mvuvi", "mkulima", "mfanyabiashara", "mchoraji", "msanii", "mwanamuziki", "mwimbaji", "mchezaji",
                            "mwanariadha", "mwandishi", "mtafiti", "mwanasayansi", "mhandisi", "mpishi", "mhudumu", "msafiri",
                            "mchunga", "mchungaji", "mwendesha", "dereva", "rubani", "baharia", "mchuuzi", "mshonaji", "mrembo",
                            "mzee", "kijana", "mtoto", "mwanamume", "mwanamke", "watu", "jamii", "taifa", "kabila", "koo", "ndugu",
                            "rafiki", "adui", "majaji", "wakili", "hakimu", "mkuu", "katibu", "meneja", "msimamizi", "mwangalizi",
                            "mlinzi", "mteja", "mtoa huduma", "mpiga picha", "mwigizaji", "msimulizi", "mtangazaji", "mshawishi",
                            "msuluhishi", "mpatanishi", "mtunga", "mshairi", "mwandishi wa habari", "mchongaji", "mfinyanzi",
                            "mfua chuma", "mjenzi", "mchukuzi", "mwangalizi", "mkalimani", "mtaalamu", "bingwa", "gwiji", "shujaa",
                            "jambazi", "gaidi", "mgonjwa", "mganga", "mgombea", "mpiga kura", "mshindi", "mshiriki", "mgeni rasmi",
                            "mwendesha sherehe", "mtoa hotuba", "mwandishi wa vitabu", "mhariri", "mchapishaji", "mchezaji wa mpira",
                            "mwanamieleka", "bondia", "muogeleaji", "mpanda milima", "mchunguzi", "mdadisi", "mfuasi", "mpingaji",
                            "msaidizi", "mkuu wa shule", "mkuu wa chuo", "mkuu wa idara", "mkuu wa mkoa", "mkuu wa wilaya", "balozi",
                            "konseli", "jaji mkuu", "spika", "mbunge", "seneta", "diwani", "mwenyekiti", "katibu mkuu", "afisa",
                            "mkurugenzi", "meneja", "msimamizi", "mwangalizi", "mlinzi", "mlinzi wa amani", "mlinzi wa wanyamapori",
                            "mlinzi wa mazingira", "mlinzi wa pwani", "mlinzi wa mpaka", "mlinzi wa taifa", "mkuu wa jeshi", "jenerali",
                            "kanali", "meja", "kapteni", "luteni", "sajenti", "koplo", "askari wa kawaida", "dereva wa lori",
                            "dereva wa basi", "dereva wa teksi", "dereva wa pikipiki", "dereva wa treni", "dereva wa meli",
                            "dereva wa ndege", "fundi seremala", "fundi bomba", "fundi umeme", "fundi magari", "fundi simu",
                            "fundi kompyuta", "fundi elektroniki", "fundi ujenzi", "fundi mkuu", "mwanaanga", "mwanadamu", "kiumbe",
                            "mtakatifu", "mchawi", "roho","mwizi", "malaika", "shetani", "jini", "pepo", "mungu", "nabii", "mtume", "imamu",
                            "padre", "sheikh", "rabi", "mama", "baba", "kaka", "dada", "mke", "mume", "mtoto", "ndugu", "rafiki",
                            "jirani", "bibi", "babu", "mjomba", "shangazi", "wifi", "shemeji", "mkwe", "mkaza", "mwenyeji", "mgeni",
                            "simba", "chui", "tembo", "nyati", "kifaru", "twiga", "punda milia", "fisi", "mbwa", "paka", "ng'ombe",
                            "mbuzi", "kondoo", "kuku", "bata", "bukini", "kanga", "njiwa", "tai", "mwewe", "kasuku", "kunguru", "popo",
                            "sungura", "swala", "nungu", "nguruwe", "panya", "panya buku", "kobe", "mamba", "nyoka", "mjusi", "chura",
                            "samaki", "papa", "pomboo", "kamba", "kaa", "pweza", "nzi", "mbu", "nyuki", "siafu", "chungu", "mchwa",
                            "kipepeo", "nzige", "panzi", "wadudu", "ndege", "wanyama", "viumbe", "samaki", "konokono", "tandu", "jongoo",
                            "buibui", "kunguni", "chawa", "viroboto", "chawa wa kichwa", "chawa wa kitandani", "nondo", "chura", "mende",
                            "viwavi", "kereng'ende", "kipepeo", "funza", "minyoo", "mabuu", "mdudu wa maji", "mdudu wa ardhini",
                            "mdudu wa angani", "mdudu wa bustani", "mdudu wa nyumba", "kware", "kwale", "shore", "kokoro", "kolokolokoko",
                            "kuro", "korongo", "koho", "kolelo", "kiondoko", "kindi", "kiroboto", "kindu", "kinanda", "kinamasi", "kisa",
                            "kisu", "kisha", "kisukari", "kisulisuli", "kisumbufu", "kiswidi", "kitala", "kitale", "kitani", "kitasa",
                            "kitawi", "kitendawili", "kitengo", "kitenzi", "kitete", "kithiri", "kiti", "kitimoto", "kitinda", "kitini",
                            "kitita", "kitori", "kitovu", "kitoweo", "kitulizo", "kitumbua", "kitumbuizo", "kitunga", "kitunguu", "kituo",
                            "kitupu", "kitururu", "kituzo", "kiuka", "kiulizo", "kiumbe", "kiunzi", "kiungo", "kiunganishi", "kiungulia",
                            "kiunguzia", "kiuni", "kiusha", "kiusio", "kiutando", "kiutawala", "kivukio", "kivuli", "kivumishi", "kivumo",
                            "kivuno", "kivunjo", "kivuo", "kivuta", "kivutio", "kivuzi", "kiwanda", "kiwambo", "kiwango", "kiwavi",
                            "kiwazo", "kiweko", "kiwewe", "kiwiliwili", "kiwinda", "kiwindo", "kiwingu", "kiwira", "kiwisha", "kiwivi",
                            "kiwizi", "kiza", "kizalendo", "kizazi", "kizibao", "kizibo", "kizidi", "kizingiti", "kizingo", "kiziwi",
                            "kizuka", "kizungu", "kizunguzungu", "kizushi", "nchi", "mji", "kijiji", "nyumba", "shule", "chuo", "hospitali",
                            "soko", "duka", "benki", "kanisa", "msikiti", "hekalu", "ofisi", "kiwanda", "shamba", "bustani", "msitu",
                            "jangwa", "ziwa", "mto", "bahari", "pwani", "mlima", "bonde", "milima", "mbuga", "hifadhi", "uwanja", "barabara",
                            "daraja", "reli", "kituo", "bandari", "uwanja wa ndege", "hoteli", "mgahawa", "sinema", "ukumbi", "maktaba",
                            "jumba la makumbusho", "gereza", "mahakama", "bunge", "ikulu", "wizara", "balozi", "konseli", "bandari",
                            "uwanja", "stadium", "gym", "makazi", "maskani", "mahali", "kitongoji", "mtaa", "jimbo", "mkoa", "bara",
                            "kisiwa", "ghuba", "hori", "hifadhi ya taifa", "bustani ya wanyama", "bustani ya mimea", "bustani ya maua",
                            "kituo cha basi", "kituo cha treni", "kituo cha polisi", "kituo cha afya", "kituo cha utafiti", "kituo cha redio",
                            "kituo cha televisheni", "kituo cha habari", "kituo cha mawasiliano", "kituo cha biashara", "kituo cha huduma",
                            "kituo cha mafuta", "kituo cha umeme"));
                sqLiteDatabase.beginTransaction();
                try {
                    for (int i = 0; i <=16683; i++) {
                        String key = String.valueOf(i);
                        if(object.has(key)) {
                            String jina= object.getJSONObject(key).getString("Word");
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
                                if (nomino.contains(jina)) {
                                    contentValues.put(Database.COLUMN_kundi,"Nomino");
                                }

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
                                if (nomino.contains(jina)) {
                                    contentValues.put(Database.COLUMN_kundi,"Nomino");
                                }
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
//                            words.put(key,object.optJSONObject(key));

                            long id=  sqLiteDatabase.insertWithOnConflict(Database.TABLE_NAME, null, contentValues,SQLiteDatabase.CONFLICT_IGNORE);
                          preferences.edit().putBoolean("kamusi",true).apply();
//                            System.out.println(id);
                        }


//                        else{
//                            words.put(key,object.optJSONObject(String.valueOf(i-1)));
//                        }
                    }
                    sqLiteDatabase.setTransactionSuccessful();


//                JSONObject words=new JSONObject(hash);

//                    File file=new File("storage/emulated/0/Kamusi");
//                    if (!file.exists()) {
//                        file.mkdirs();
//                    }
//
//                    FileWriter writer=new FileWriter(file+"/word.json");
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