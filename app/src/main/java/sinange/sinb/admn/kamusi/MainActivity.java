package sinange.sinb.admn.kamusi;

import android.Manifest;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class MainActivity extends AppCompatActivity {
    DrawerLayout drawerLayout;
    Toolbar toolbar;
    NavigationView navigationView;
    Database database;
    SearchView searchView;
    List<DatabaseModel> wmean = new ArrayList<>();
    DatabaseAdapter databaseAdapter;
    SQLiteDatabase sqLiteDatabase;
    RecyclerView recyclerView;
    AutoCompleteTextView att;
    Set<String> set = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DatabaseAdder app = (DatabaseAdder) getApplication();
        database = app.getDatabase();
        drawerLayout = findViewById(R.id.drawer);
        toolbar = findViewById(R.id.toolbar);
        navigationView = findViewById(R.id.navigationView);
        recyclerView = findViewById(R.id.recycler);

//        Toast.makeText(this,     "\"=\"", Toast.LENGTH_SHORT).show();
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.MANAGE_EXTERNAL_STORAGE}, 0);
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, Uri.fromParts("package", getPackageName(), null));
                startActivityForResult(intent, 0);
            }
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }

        setSupportActionBar(toolbar);
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
        databaseAdapter = new DatabaseAdapter(this, wmean);
        recyclerView.setAdapter(databaseAdapter);
        databaseAdapter.notifyDataSetChanged();
        navigationView.setNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.history) {
                startActivity(new Intent(this, Notes.class));
            } else if (item.getItemId() == R.id.help) {
            } else if (item.getItemId() == R.id.help) {

            }
            drawerLayout.close();
            return false;
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        MenuItem search = menu.findItem(R.id.app_bar_search);
        if (search != null) {
            searchView = (SearchView) search.getActionView();
        }
        if (searchView != null) {
            SearchManager searchManager = (SearchManager) MainActivity.this.getSystemService(Context.SEARCH_SERVICE);
            searchView.setSearchableInfo(searchManager.getSearchableInfo(MainActivity.this.getComponentName()));
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    getMaana(query);
                    SharedPreferences preferences = getSharedPreferences("history", Context.MODE_PRIVATE);
                    set.add(query);
                    preferences.edit().putStringSet("set", set).apply();
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    getMaana(newText);
                    return true;
                }
            });
        }

        return super.onCreateOptionsMenu(menu);

    }

    private void getMaana(String query) {
        wmean.clear();
        if (database == null) {
            Toast.makeText(this, "Database not initialized", Toast.LENGTH_SHORT).show();
            databaseAdapter.notifyDataSetChanged();
            return;
        }
        String sql = " SELECT * FROM " + Database.TABLE_NAME + "  WHERE " + Database.COLUMN_JINA + " LIKE ? order by  case when " + Database.COLUMN_JINA + " = ? then 0 else 1 end, " + Database.COLUMN_JINA + " limit 10 ";
        String[] selectionArgs = new String[]{"%" + query + "%", query};
        sqLiteDatabase = database.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(sql, selectionArgs);
        try {
            System.out.println(cursor.getCount());
            int kund = cursor.getColumnIndex(Database.COLUMN_kundi);
            int jin = cursor.getColumnIndex(Database.COLUMN_JINA);
            int maan = cursor.getColumnIndex(Database.COLUMN_MAANA);
            int maanPili = cursor.getColumnIndex(Database.COLUMN_MPILI);
            int kisaw = cursor.getColumnIndex(Database.COLUMN_KISAWE);
            int mnyamb = cursor.getColumnIndex(Database.COLUMN_CONJUGATIION);
            int mfan = cursor.getColumnIndex(Database.COLUMN_MFANO);
            int mfanPili = cursor.getColumnIndex(Database.COLUMN_MFANOPILI);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String kundiLaManeno = cursor.getString(kund);
                    String jina = cursor.getString(jin);
                    String maana = cursor.getString(maan);
                    String maanaPili = cursor.getString(maanPili);
                    String kisawe = cursor.getString(kisaw);
                    String mfano = cursor.getString(mfan);
                    String mfanoPili = cursor.getString(mfanPili);
                    String mnyambuliko = cursor.getString(mnyamb);
                    wmean.add(new DatabaseModel(kundiLaManeno, jina, maana, maanaPili, kisawe, mnyambuliko, mfano, mfanoPili));
                } while (cursor.moveToNext());
                databaseAdapter.notifyDataSetChanged();
            } else {
                wmean.clear();
                databaseAdapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error loading data", Toast.LENGTH_SHORT).show();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            databaseAdapter.notifyDataSetChanged();


        }

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults, int deviceId) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults, deviceId);
        if (requestCode==0&&grantResults.length>0){
            for (int i = 0; i < permissions.length; i++) {
                boolean b = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                if (b){
                    Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
                }


            }

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.R)

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (Environment.isExternalStorageManager()) {
                Toast.makeText(this, "permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "permission not granted", Toast.LENGTH_SHORT).show();
            }
        }


    }
}