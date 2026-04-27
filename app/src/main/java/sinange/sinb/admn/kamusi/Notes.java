package sinange.sinb.admn.kamusi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


public class Notes extends AppCompatActivity {
    RecyclerView recyclerView;
   ArrayList<String> messages =new ArrayList<>();
   Set<String> queries;
   ImageButton imageButton;
   static history his;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);
        imageButton = findViewById(R.id.imageButton);
        SharedPreferences preferences = getSharedPreferences("history", Context.MODE_PRIVATE);
         queries=new HashSet<>(preferences.getStringSet("set", new HashSet<>()));
        his = new history(this, messages,queries);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(Notes.this));
        messages.addAll(queries);
        recyclerView.setAdapter(his);
        his.notifyDataSetChanged();
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Notes.this,MainActivity.class));
            }
        });
    }


}