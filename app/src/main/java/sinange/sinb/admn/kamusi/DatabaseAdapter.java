package sinange.sinb.admn.kamusi;

import android.content.Intent;
import android.graphics.Color;
import android.provider.Settings;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DatabaseAdapter extends RecyclerView.Adapter<DatabaseAdapter.MyView> {
    List<DatabaseModel> mean;
    MainActivity mainActivity;

    public DatabaseAdapter(MainActivity mainActivity, List<DatabaseModel> wmean) {
        this.mean = wmean;
        this.mainActivity = mainActivity;
    }

    @Override
    public DatabaseAdapter.MyView onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mainActivity);
        View view = inflater.inflate(R.layout.recylerview, parent, false);
        return new MyView(view);
    }

    @Override
    public void onBindViewHolder(DatabaseAdapter.MyView holder, int position) {
        DatabaseModel model = mean.get(position);

        // Bind type (Kundi la maneno)
        if (model.getKundilaManeno() != null && !model.getKundilaManeno().trim().isEmpty()) {
            holder.kundilaManeno.setText("KN: " + model.getKundilaManeno());
            holder.layoutType.setVisibility(View.VISIBLE);
        } else {
            holder.layoutType.setVisibility(View.GONE);
        }

        // Bind ngeli
        if (model.getNgeli() != null && !model.getNgeli().trim().isEmpty()) {
            holder.ngeli.setText("Ngeli: " + model.getNgeli());
            holder.layoutNgeli.setVisibility(View.VISIBLE);
        } else {
            holder.layoutNgeli.setVisibility(View.GONE);
        }

        // Bind word name
        if (model.getJina() != null && !model.getJina().equals("null")) {
            holder.jina.setText(model.getJina());
        }

        // Bind meanings
        if (model.getMaana() != null&& !model.getMaana().equals("null") && !model.getMaana().trim().isEmpty()) {
            highlightAndLink(holder.maana, "1. " + model.getMaana());
            holder.layoutMaana.setVisibility(View.VISIBLE);
        } else {
            holder.layoutMaana.setVisibility(View.GONE);
        }

        if (model.getMaanaPili() != null && !model.getMaanaPili().equals("null") && !model.getMaanaPili().trim().isEmpty()) {
            highlightAndLink(holder.maanaPIli, "2. " + model.getMaanaPili());
            holder.layoutMaanaPili.setVisibility(View.VISIBLE);
        } else {
            holder.layoutMaanaPili.setVisibility(View.GONE);
        }

        // Bind synonyms (Kisawe)
        if (model.getKisawe() != null && !model.getKisawe().equals("null") &&!model.getKisawe().trim().isEmpty()) {
            highlightAndLink(holder.kisawe, model.getKisawe());
            holder.layoutKisawe.setVisibility(View.VISIBLE);
        } else {
            holder.layoutKisawe.setVisibility(View.GONE);
        }

        // Bind conjugations (Mnyambuliko)
        if (model.getMnyambuliko() != null && !model.getMnyambuliko().equals("null") && !model.getMnyambuliko().trim().isEmpty()) {
            highlightAndLink(holder.mnyambuliko, model.getMnyambuliko());
            holder.layoutMnyambuliko.setVisibility(View.VISIBLE);
        }else {

            holder.layoutMnyambuliko.setVisibility(View.GONE);
        }

        // Bind examples
        boolean hasMfano1 = model.getMfano() != null && !model.getMfano().equals("null") && !model.getMfano().trim().isEmpty();
        boolean hasMfano2 = model.getMfanoPili() != null&& !model.getMaanaPili().equals("null") && !model.getMfanoPili().trim().isEmpty();

        if (hasMfano1 || hasMfano2) {
            holder.layoutMifano.setVisibility(View.VISIBLE);
            if (hasMfano1) {
                holder.mfano.setVisibility(View.VISIBLE);
                highlightAndLink(holder.mfano, "1. " + model.getMfano());
            } else {
                holder.mfano.setVisibility(View.GONE);
            }
            if (hasMfano2) {
                holder.mfanoPili.setVisibility(View.VISIBLE);
                highlightAndLink(holder.mfanoPili, "2. " + model.getMfanoPili());
            } else {
                holder.mfanoPili.setVisibility(View.GONE);
            }
        } else {
            holder.layoutMifano.setVisibility(View.GONE);
        }

        // Bind Text-to-Speech action to sauti button
        if (holder.sautiButton != null) {
            holder.sautiButton.setOnClickListener(v -> {
                StringBuilder sb = new StringBuilder();

                if (model.getJina() != null) {
                    sb.append(model.getJina()).append(". ");
                }
                if (model.getMaana() != null && !model.getMaana().equals("null")) {
                    sb.append("Maana: ").append(model.getMaana()).append(". ");
                }
                if (model.getMaanaPili() != null && !model.getMaanaPili().equals("null")) {
                    sb.append("Maana nyingine: ").append(model.getMaanaPili()).append(". ");
                }
                if (model.getKisawe() != null && !model.getKisawe().equals("null")) {
                    sb.append("Visawe: ").append(model.getKisawe()).append(". ");
                }
                if (model.getMnyambuliko() != null && !model.getMnyambuliko().equals("null")) {
                    sb.append("Mnyambuliko: ").append(model.getMnyambuliko()).append(". ");
                }
                if (model.getMfano() != null && !model.getMfano().equals("null")) {
                    sb.append("Mfano: ").append(model.getMfano()).append(". ");
                }
                if (model.getMfanoPili() != null && !model.getMfanoPili().equals("null")) {
                    sb.append("Mfano wa pili: ").append(model.getMfanoPili()).append(".");
                }

                mainActivity.speakWord(sb.toString());
            });
        }
    }

    private void highlightAndLink(TextView textView, String text) {
        SpannableString ss = new SpannableString(text);
        Pattern p = Pattern.compile("\\b\\w+\\b");
        Matcher m = p.matcher(text);

        while (m.find()) {
            final String word = m.group().toLowerCase();
            if (MainActivity.allWords.contains(word)) {
                ClickableSpan clickableSpan = new ClickableSpan() {
                    @Override
                    public void onClick(@NonNull View textView) {
                        mainActivity.getMaana(word);
                        if (mainActivity.searchView != null) {
                            mainActivity.searchView.setQuery(word, false);
                        }
                    }

                    @Override
                    public void updateDrawState(@NonNull TextPaint ds) {
                        super.updateDrawState(ds);
                        ds.setUnderlineText(false);
                        ds.setColor(Color.parseColor("#D9F26C"));
                    }
                };
                ss.setSpan(clickableSpan, m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        textView.setText(ss);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    public int getItemCount() {
        return mean.size();
    }

    public class MyView extends RecyclerView.ViewHolder {
        TextView kundilaManeno, jina, maana, maanaPIli, kisawe, mnyambuliko, mfano, mfanoPili, ngeli;
        View layoutNgeli, layoutType, layoutMaana, layoutMaanaPili, layoutKisawe, layoutMnyambuliko, layoutMifano;
        ImageButton sautiButton;

        public MyView(View itemView) {
            super(itemView);
            kundilaManeno = itemView.findViewById(R.id.type);
            ngeli = itemView.findViewById(R.id.ngeli);
            jina = itemView.findViewById(R.id.jina);
            maana = itemView.findViewById(R.id.maana);
            maanaPIli = itemView.findViewById(R.id.maanapili);
            kisawe = itemView.findViewById(R.id.kisawe);
            mnyambuliko = itemView.findViewById(R.id.mnyambuliko);
            mfano = itemView.findViewById(R.id.mfano);
            mfanoPili = itemView.findViewById(R.id.mfanoPili);

            layoutNgeli = itemView.findViewById(R.id.layout_ngeli);
            layoutType = itemView.findViewById(R.id.layout_type);
            layoutMaana = itemView.findViewById(R.id.layout_maana);
            layoutMaanaPili = itemView.findViewById(R.id.layout_maanapili);
            layoutKisawe = itemView.findViewById(R.id.layout_kisawe);
            layoutMnyambuliko = itemView.findViewById(R.id.layout_mnyambuliko);
            layoutMifano = itemView.findViewById(R.id.layout_mifano);
            sautiButton = itemView.findViewById(R.id.sauti_button);

            itemView.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    // Item click action if needed
                }
            });
        }
    }
}
