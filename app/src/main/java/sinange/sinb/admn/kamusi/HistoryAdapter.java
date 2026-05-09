package sinange.sinb.admn.kamusi;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Set;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.Brian> {

    public ArrayList<String> messages;
    public History notes;
    public Set<String> queries;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(String message);
    }

    public HistoryAdapter(History history, ArrayList<String> messages, Set<String> queries, OnItemClickListener listener) {
        this.messages = messages;
        this.notes = history;
        this.queries = queries;
        this.listener = listener;
    }


    @Override
    public HistoryAdapter.Brian onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(notes);
        View view = layoutInflater.inflate(R.layout.history,parent,false);
        return new Brian(view);
    }

    @Override
    public void onBindViewHolder(HistoryAdapter.Brian holder, int position) {
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
        public Brian(View itemView) {
            super(itemView);
            txt = itemView.findViewById(R.id.message);
            button = itemView.findViewById(R.id.imageView);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPreferences preferences = notes.getSharedPreferences("HistoryAdapter", Context.MODE_PRIVATE);
                    int pos = getAdapterPosition();

                    if (pos != RecyclerView.NO_POSITION) {
                        String message = messages.get(pos);
                        queries.remove(message);
                        messages.remove(pos);
                        preferences.edit().putStringSet("set", queries).apply();
                        notifyItemRemoved(pos);
                    }
                }
            });
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        String message = messages.get(pos);
                        if (listener != null) {
                            listener.onItemClick(message);
                        }
                    }
                }
            });
        }
    }
}
