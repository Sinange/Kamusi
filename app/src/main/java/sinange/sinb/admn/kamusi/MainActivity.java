package sinange.sinb.admn.kamusi;

import android.Manifest;
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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.PendingPurchasesParams;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.ProductDetailsResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesResponseListener;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.QueryProductDetailsParams;
import com.android.billingclient.api.QueryProductDetailsResult;
import com.android.billingclient.api.QueryPurchasesParams;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.material.navigation.NavigationView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
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
    Set<String> set = new HashSet<>();
    public static Set<String> allWords = new HashSet<>();
    android.speech.tts.TextToSpeech textToSpeech;
    SharedPreferences pref;
   public static InterstitialAd interstitialAds;
   BillingClient billingClient;
   ProductDetails removeAds;
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
         pref = getSharedPreferences("AppSettings", Context.MODE_PRIVATE);
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

        // Initialize TextToSpeech
        textToSpeech = new android.speech.tts.TextToSpeech(this, status -> {
            if (status == android.speech.tts.TextToSpeech.SUCCESS) {
                int result = textToSpeech.setLanguage(java.util.Locale.forLanguageTag("sw"));
                if (result == android.speech.tts.TextToSpeech.LANG_MISSING_DATA ||
                        result == android.speech.tts.TextToSpeech.LANG_NOT_SUPPORTED) {
                    textToSpeech.setLanguage(java.util.Locale.getDefault());
                }
            }
        });
        loadAds();
        loadInterstitialAds();
        setupBilling();
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
        databaseAdapter = new DatabaseAdapter(this, wmean);
        recyclerView.setAdapter(databaseAdapter);
        loadAllWords();
        databaseAdapter.notifyDataSetChanged();
        navigationView.setNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.history) {
                startActivity(new Intent(this, History.class));
            } else if (item.getItemId() == R.id.help) {
                View popup = LayoutInflater.from(this).inflate(R.layout.popup_help, null);
                PopupWindow popupWindow = new PopupWindow(popup,LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT,true);
                popupWindow.showAtLocation(drawerLayout, Gravity.CENTER,0,0);
                Button btnClose = popup.findViewById(R.id.btn_close_popup);
                if (btnClose!=null) {
                    btnClose.setOnClickListener(v->{
                        popupWindow.dismiss();
                    });
                }
            } else if (item.getItemId()==R.id.exit) {
                finishAffinity();
            } else if (item.getItemId()==R.id.backup) {

            } else if (item.getItemId()==R.id.restore) {

            } else if (item.getItemId()==R.id.removeAds) {
                if (removeAds != null) {
                    List<BillingFlowParams.ProductDetailsParams> productDetailsParams = Collections.singletonList(BillingFlowParams.ProductDetailsParams.newBuilder().setProductDetails(removeAds).build());
                    BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder().setProductDetailsParamsList(productDetailsParams).build();
                    billingClient.launchBillingFlow(this, billingFlowParams);

                } else {
                    Toast.makeText(this, "Store loading, please try again in a moment.", Toast.LENGTH_SHORT).show();
                }
                
            }
            drawerLayout.close();
            return false;
        });

        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra("QUERY")) {
                String word = intent.getStringExtra("QUERY");
                getMaana(word);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Intent.ACTION_PROCESS_TEXT.equals(intent.getAction())) {
                CharSequence sequencetext = intent.getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT);
                if (sequencetext != null) {
                    String word = sequencetext.toString().trim();
                    getMaana(word);
                }
            } else if (Intent.ACTION_SEND.equals(intent.getAction()) && "text/plain".equals(intent.getType())) {
                String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
                if (sharedText != null) {
                    getMaana(sharedText.trim());
                }
            }
        }
    }

    private void setupBilling() {
       billingClient= BillingClient.newBuilder(this).setListener(new PurchasesUpdatedListener() {
           @Override
           public void onPurchasesUpdated(@NonNull BillingResult billingResult, @Nullable List<Purchase> list) {
               if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && list != null) {
                   for (Purchase purchase : list) {
                       handlePurchase(purchase);
                   }
               }
           }
       }).enablePendingPurchases(PendingPurchasesParams.newBuilder().enableOneTimeProducts().build()).build();
    billingClient.startConnection(new BillingClientStateListener() {
        @Override
        public void onBillingServiceDisconnected() {

        }

        @Override
        public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
            checkExistingPurchasesAndQueryExistingProducts();
        }
    });

    }

    private void checkExistingPurchasesAndQueryExistingProducts() {
    billingClient.queryPurchasesAsync(QueryPurchasesParams.newBuilder().setProductType(BillingClient.ProductType.INAPP).build(), new PurchasesResponseListener() {
    @Override
    public void onQueryPurchasesResponse(@NonNull BillingResult billingResult, @NonNull List<Purchase> list) {
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
            for (Purchase purchase : list) {
                if (purchase.getProducts().contains("removeAds") && purchase.isAcknowledged()) {
                    pref.edit().putBoolean("isAdfree", true).apply();
                    hideAds();
                }
            }
        }
    }
});
        List<QueryProductDetailsParams.Product> product = new ArrayList<>();
        product.add(QueryProductDetailsParams.Product.newBuilder().setProductId("removeAds").setProductType(BillingClient.ProductType.INAPP).build());
        QueryProductDetailsParams params = QueryProductDetailsParams.newBuilder().setProductList(product).build();
        billingClient.queryProductDetailsAsync(params, new ProductDetailsResponseListener() {
            @Override
            public void onProductDetailsResponse(@NonNull BillingResult billingResult, @NonNull QueryProductDetailsResult queryProductDetailsResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK&& !queryProductDetailsResult.getProductDetailsList().isEmpty()) {
                    removeAds = queryProductDetailsResult.getProductDetailsList().get(0);

                }
            }
        });
   }

    private void hideAds() {runOnUiThread(new Runnable() {
        @Override
        public void run() {
            AdView adView = findViewById(R.id.adview);

                 if (adView != null) {
                adView.setVisibility(View.GONE);
            }
            interstitialAds = null;
       }
    });    }

    private void handlePurchase(Purchase purchase) {
        if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
            AcknowledgePurchaseParams acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder().setPurchaseToken(purchase.getPurchaseToken()).build();
            billingClient.acknowledgePurchase(acknowledgePurchaseParams, new AcknowledgePurchaseResponseListener() {
                @Override
                public void onAcknowledgePurchaseResponse(@NonNull BillingResult billingResult) {
                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                        pref.edit().putBoolean("isAdfree", true).apply();
                        hideAds();
                        Toast.makeText(MainActivity.this, "Asante! Matangazo yameondolewa.", Toast.LENGTH_LONG).show();
                    }
                }
            });
        } else {
            pref.edit().putBoolean("isAdfree", true).apply();
            hideAds();
        }
    }

    private  void loadInterstitialAds() {
        boolean isAdfree = pref.getBoolean("isAdfree", false);
        if (isAdfree) return;
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(this,"ca-app-pub-3940256099942544/1033173712", adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                super.onAdLoaded(interstitialAd);
                interstitialAds = interstitialAd;
                interstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdDismissedFullScreenContent() {
                        super.onAdDismissedFullScreenContent();
                        loadInterstitialAds();
                    }

                });
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                interstitialAds = null;
            }

        });
    }

    private void loadAds() {
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {

                AdView adView = findViewById(R.id.adview);
                boolean isAdfree = pref.getBoolean("isAdfree", false);
                if (isAdfree) {
                    adView.setVisibility(View.GONE);
                } else {
                    adView.setVisibility(View.VISIBLE);
                    AdRequest adRequest = new AdRequest.Builder().build();
                    adView.loadAd(adRequest);

                }

            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        if (intent != null) {
            if (intent.hasExtra("QUERY")) {
                String word = intent.getStringExtra("QUERY");
                getMaana(word);
                if (searchView != null) {
                    searchView.setQuery(word, false);
                }
            } else if (Intent.ACTION_SEND.equals(intent.getAction()) && "text/plain".equals(intent.getType())) {
                String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
                if (sharedText != null) {
                    getMaana(sharedText.trim());
                    if (searchView != null) {
                        searchView.setQuery(sharedText.trim(), false);
                    }
                }
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Intent.ACTION_PROCESS_TEXT.equals(intent.getAction())) {
                CharSequence sequencetext = intent.getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT);
                if (sequencetext != null) {
                    String word = sequencetext.toString().trim();
                    getMaana(word);
                    if (searchView != null) {
                        searchView.setQuery(word, false);
                    }
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        MenuItem search = menu.findItem(R.id.app_bar_search);
        if (search != null) {
            searchView = (SearchView) search.getActionView();
            if (searchView!=null) {
                searchView.setEnabled(false);
                searchView.setQueryHint("Loading database...");
            }
        }
        if (searchView != null) {
            SearchManager searchManager = (SearchManager) MainActivity.this.getSystemService(Context.SEARCH_SERVICE);
            searchView.setSearchableInfo(searchManager.getSearchableInfo(MainActivity.this.getComponentName()));
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    getMaana(query);
                    SharedPreferences preferences = getSharedPreferences("HistoryAdapter", Context.MODE_PRIVATE);
                    set = new HashSet<>(preferences.getStringSet("set", new HashSet<>()));
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

    public void getMaana(String query) {
        wmean.clear();
        SharedPreferences prefs = getSharedPreferences("kamusi", Context.MODE_PRIVATE);
        boolean dataLoaded = prefs.getBoolean("kamusi", false);
        if (!dataLoaded) {
            Toast.makeText(this, "Hifadhi (Database) bado inapakia, tafadhali subiri...", Toast.LENGTH_SHORT).show();
            databaseAdapter.notifyDataSetChanged();
            return;
        }
        if (database == null) {
            Toast.makeText(this, "Database not initialized", Toast.LENGTH_SHORT).show();
            databaseAdapter.notifyDataSetChanged();
            return;
        } else if (query == null) {
            return;
        } else if (query.trim().isEmpty()) {
            wmean.clear();
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
            int ngel = cursor.getColumnIndex(Database.COLUMN_ngeli);
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
                    String ngeli= cursor.getString(ngel);
                    String maana = cursor.getString(maan);
                    String maanaPili = cursor.getString(maanPili);
                    String kisawe = cursor.getString(kisaw);
                    String mfano = cursor.getString(mfan);
                    String mfanoPili = cursor.getString(mfanPili);
                    String mnyambuliko = cursor.getString(mnyamb);
                    wmean.add(new DatabaseModel(kundiLaManeno,jina,ngeli, maana, maanaPili, kisawe, mnyambuliko, mfano, mfanoPili));
                } while (cursor.moveToNext());
                databaseAdapter.notifyDataSetChanged();

            } else {
                wmean.clear();
                Toast.makeText(this, "Neno halikupatikana", Toast.LENGTH_SHORT).show();
                databaseAdapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error loading data", Toast.LENGTH_SHORT).show();
        } finally {
            if (cursor != null) {
                cursor.close();
                runOnUiThread(() -> {
                    if (searchView != null) {
                        searchView.setEnabled(true);
                        searchView.setQueryHint("Search...");

                    }
                });
            }
            databaseAdapter.notifyDataSetChanged();


        }

    }

    private void loadAllWords() {
        if (allWords.isEmpty()) {
            new Thread(() -> {
                SQLiteDatabase db = database.getReadableDatabase();
                Cursor cursor = db.query(Database.TABLE_NAME, new String[]{Database.COLUMN_JINA}, null, null, null, null, null);
                if (cursor != null) {
                    int index = cursor.getColumnIndex(Database.COLUMN_JINA);
                    while (cursor.moveToNext()) {
                        allWords.add(cursor.getString(index).toLowerCase().trim());
                    }
                    cursor.close();
                }
            }).start();
        }
    }

    @Override
    public boolean onOptionsItemSelected( MenuItem item) {

        return super.onOptionsItemSelected(item);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode,  Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    Toast.makeText(this, "permission granted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "permission not granted", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "permission granted", Toast.LENGTH_SHORT).show();
            }
        }


    }

    public void speakWord(String text) {
        if (textToSpeech != null && text != null && !text.isEmpty()) {
            textToSpeech.speak(text, android.speech.tts.TextToSpeech.QUEUE_FLUSH, null, "kamusi_tts_id");
        } else {
            Toast.makeText(this, "Sauti haipatikani kwa sasa", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }
}