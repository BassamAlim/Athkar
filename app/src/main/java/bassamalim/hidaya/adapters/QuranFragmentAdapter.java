package bassamalim.hidaya.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.cardview.widget.CardView;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.google.gson.Gson;

import java.util.ArrayList;

import bassamalim.hidaya.R;
import bassamalim.hidaya.activities.QuranActivity;
import bassamalim.hidaya.database.AppDatabase;
import bassamalim.hidaya.models.SuraCard;

public class QuranFragmentAdapter extends RecyclerView.Adapter<QuranFragmentAdapter.ViewHolder> {

    private final Context context;
    private final AppDatabase db;
    private final SharedPreferences pref;
    private final ArrayList<SuraCard> suraCards;
    private final ArrayList<SuraCard> suraCardsCopy;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final CardView card;
        private final TextView namescreen;
        private final ImageView tanzeelView;
        private final ImageButton favBtn;

        public ViewHolder(View view) {
            super(view);
            card = view.findViewById(R.id.surah_button_model);
            namescreen = view.findViewById(R.id.namescreen);
            tanzeelView = view.findViewById(R.id.tanzeel_view);
            favBtn = view.findViewById(R.id.sura_fav_btn);
        }
    }

    public QuranFragmentAdapter(Context context, ArrayList<SuraCard> buttons) {
        this.context = context;

        db = Room.databaseBuilder(context, AppDatabase.class, "HidayaDB")
                .createFromAsset("databases/HidayaDB.db").allowMainThreadQueries().build();

        pref = PreferenceManager.getDefaultSharedPreferences(context);

        suraCards = new ArrayList<>(buttons);
        suraCardsCopy = new ArrayList<>(buttons);
    }

    @NonNull @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_quran_fragment,
                viewGroup, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        SuraCard card = suraCards.get(position);

        viewHolder.namescreen.setText(card.getSuraName());

        int tanzeel = card.getTanzeel();
        if (tanzeel == 0) // Makkah
            viewHolder.tanzeelView.setImageDrawable(
                    AppCompatResources.getDrawable(context, R.drawable.ic_kaaba_black));
        else if (tanzeel == 1) // Madina
            viewHolder.tanzeelView.setImageDrawable(
                    AppCompatResources.getDrawable(context, R.drawable.ic_madinah));

        int fav = card.getFavorite();
        if (fav == 0)
            viewHolder.favBtn.setImageDrawable(
                    AppCompatResources.getDrawable(context, R.drawable.ic_star_outline));
        else if (fav == 1)
            viewHolder.favBtn.setImageDrawable(
                    AppCompatResources.getDrawable(context, R.drawable.ic_star));

        viewHolder.card.setOnClickListener(card.getCardListener());
        viewHolder.favBtn.setOnClickListener(view -> {
            if (card.getFavorite() == 0) {
                db.suraDao().setFav(card.getNumber(), 1);
                card.setFavorite(1);
            }
            else if (card.getFavorite() == 1) {
                db.suraDao().setFav(card.getNumber(), 0);
                card.setFavorite(0);
            }
            notifyItemChanged(position);

            updateFavorites();
        });
    }

    @Override
    public int getItemCount() {
        return suraCards.size();
    }

    public void filterName(String text) {
        suraCards.clear();
        if (text.isEmpty())
            suraCards.addAll(suraCardsCopy);
        else {
            for(SuraCard suraCard : suraCardsCopy) {
                if(suraCard.getSearchName().contains(text))
                    suraCards.add(suraCard);
            }
        }
        notifyDataSetChanged();
    }

    public void filterNumber(String text) {
        suraCards.clear();
        if (text.isEmpty())
            suraCards.addAll(suraCardsCopy);
        else {
            try {
                int num = Integer.parseInt(text);
                if (num >= 1 && num <= 604) {
                    Intent openPage = new Intent(context, QuranActivity.class);
                    openPage.setAction("by_page");
                    openPage.putExtra("page", num);
                    context.startActivity(openPage);
                }
                else
                    Toast.makeText(context, context.getString(R.string.page_does_not_exist),
                            Toast.LENGTH_SHORT).show();
            }
            catch (NumberFormatException e) {
                for(SuraCard suraCard : suraCardsCopy) {
                    if(suraCard.getSearchName().contains(text))
                        suraCards.add(suraCard);
                }
            }
        }
        notifyDataSetChanged();
    }

    private void updateFavorites() {
        Object[] favSuras = db.suraDao().getFav().toArray();

        Gson gson = new Gson();
        String surasJson = gson.toJson(favSuras);

        SharedPreferences.Editor editor = pref.edit();
        editor.putString("favorite_suras", surasJson);
        editor.apply();
    }
}
