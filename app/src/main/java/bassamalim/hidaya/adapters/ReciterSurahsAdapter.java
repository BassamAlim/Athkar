package bassamalim.hidaya.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import bassamalim.hidaya.R;
import bassamalim.hidaya.models.ReciterSurahCard;

import java.util.ArrayList;

public class ReciterSurahsAdapter extends RecyclerView.Adapter<ReciterSurahsAdapter.ViewHolder> {

    private final ArrayList<ReciterSurahCard> surahsCards;
    private final ArrayList<ReciterSurahCard> surahsCardsCopy;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final CardView card;

        public ViewHolder(View view) {
            super(view);
            card = view.findViewById(R.id.surah_model_card);
        }

        public CardView getCard() {
            return card;
        }
    }

    public ReciterSurahsAdapter(ArrayList<ReciterSurahCard> cards) {
        surahsCards = new ArrayList<>(cards);
        surahsCardsCopy = new ArrayList<>(cards);
    }

    @NonNull @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_reciter_surah, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        ((TextView) viewHolder.getCard().findViewById(R.id.surah_namescreen))
                .setText(surahsCards.get(position).getSurahName());

        viewHolder.getCard().setOnClickListener(surahsCards.get(position).getListener());
    }

    @Override
    public int getItemCount() {
        return surahsCards.size();
    }

    public void filter(String text) {
        surahsCards.clear();
        if (text.isEmpty())
            surahsCards.addAll(surahsCardsCopy);
        else {
            for(ReciterSurahCard reciterCard: surahsCardsCopy) {
                if (reciterCard.getSearchName().contains(text))
                    surahsCards.add(reciterCard);
            }
        }
        notifyDataSetChanged();
    }

}
