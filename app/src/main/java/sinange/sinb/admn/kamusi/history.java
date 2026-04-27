package sinange.sinb.admn.kamusi;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class history extends RecyclerView.Adapter<history.Brian> {

public  ArrayList<String> messages;
public Notes notes;
public Set<String > queries;


    public history(Notes notes, ArrayList<String> messages, Set<String> queries) {
        this.messages = messages;
        this.notes = notes;
        this.queries=queries;
    }

    @NonNull
    @Override
    public history.Brian onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(notes);
        View view = layoutInflater.inflate(R.layout.history,parent,false);
        return new Brian(view);
    }

    @Override
    public void onBindViewHolder(@NonNull history.Brian holder, int position) {
        String message = messages.get(position);
        holder.txt.setText(message);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public  class Brian extends RecyclerView.ViewHolder {
        TextView txt;
        ImageView button;
        public Brian(@NonNull View itemView) {
            super(itemView);
            txt = itemView.findViewById(R.id.message);
            button = itemView.findViewById(R.id.imageView);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPreferences preferences =notes.getSharedPreferences("history", Context.MODE_PRIVATE);
                    int pos=getAdapterPosition();

                    if (pos != RecyclerView.NO_POSITION) {
                        String message=txt.getText().toString().trim();
                        queries.remove(message);
                        messages.remove(pos);
                        preferences.edit().putStringSet("set",queries).apply();
                        notifyDataSetChanged();
                    }
                }
            });

        }
    }
}
