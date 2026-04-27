package sinange.sinb.admn.kamusi;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.util.List;

public class DatabaseAdapter extends RecyclerView.Adapter<DatabaseAdapter.MyView> {
List<DatabaseModel> mean;
        MainActivity mainActivity;
    public DatabaseAdapter(MainActivity mainActivity, List<DatabaseModel> wmean) {
        this.mean=wmean;
        this.mainActivity=mainActivity;
    }

    @NonNull
    @Override
    public DatabaseAdapter.MyView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mainActivity);
        View view = inflater.inflate(R.layout.recylerview, parent, false);
        return new  MyView(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DatabaseAdapter.MyView holder, int position) {
        DatabaseModel model = mean.get(position);
        if (model.getKundilaManeno()!=null) {
            holder.kundilaManeno.setText(model.getKundilaManeno());
        }
        if (model.getJina()!=null) {
            holder.jina.setText(model.getJina());
        }
        if (model.getMaana()!=null) {
            holder.maana.setText("1." + model.getMaana());
        }
        if (model.getMaanaPili()!=null) {
            holder.maanaPIli.setText("2." + model.getMaanaPili());
        }
        if (model.getKisawe()!=null) {
            holder.kisawe.setText("."+model.getKisawe());
        }
        if (model.getMnyambuliko()!=null) {
            holder.mnyambuliko.setText("."+model.getMnyambuliko());
        }
        if (model.getMfano()!=null) {
            holder.mfano.setText("1." +model.getMfano());
        }
        if (model.getMfanoPili()!= null) {
        holder.mfanoPili.setText("2." +model.getMfanoPili());
        }
        int pos = holder.getAdapterPosition();
    }

    @Override
    public int getItemCount() {
        return mean.size();
    }

    public class MyView extends RecyclerView.ViewHolder {
        TextView  kundilaManeno,jina,maana,maanaPIli,kisawe,mnyambuliko,mfano,mfanoPili;
        public MyView(@NonNull View itemView) {
            super(itemView);
            kundilaManeno = itemView.findViewById(R.id.type);
            jina = itemView.findViewById(R.id.jina);
            maana = itemView.findViewById(R.id.maana);
            maanaPIli = itemView.findViewById(R.id.maanapili);
            kisawe = itemView.findViewById(R.id.kisawe);
            mnyambuliko = itemView.findViewById(R.id.mnyambuliko);
            mfano = itemView.findViewById(R.id.mfano);
            mfanoPili=itemView.findViewById(R.id.mfanoPili);
            itemView.setOnClickListener( v-> {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {

                }

            });
        }
    }
}

