package bassamalim.hidaya.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.cardview.widget.CardView;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.util.ArrayList;

import bassamalim.hidaya.R;
import bassamalim.hidaya.models.BookDoorCard;

public class BookViewerAdapter extends RecyclerView.Adapter<BookViewerAdapter.ViewHolder> {

    private final int MARGIN = 15;
    private final Context context;
    private final SharedPreferences pref;
    private final Gson gson;
    private final int bookId;
    private final int chapterId;
    private final ArrayList<BookDoorCard> doorCards;
    private boolean[] favs;
    private int textSize;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final CardView card;
        private final TextView titleTv;
        private final TextView textTv;
        private final ImageButton favBtn;

        public ViewHolder(View view) {
            super(view);
            card = view.findViewById(R.id.book_door_model_card);
            titleTv = view.findViewById(R.id.title_tv);
            textTv = view.findViewById(R.id.text_tv);
            favBtn = view.findViewById(R.id.fav_btn);
        }
    }

    public BookViewerAdapter(Context context, ArrayList<BookDoorCard> cards,
                             int bookId, int chapterId) {
        this.context = context;
        pref = PreferenceManager.getDefaultSharedPreferences(context);
        gson = new Gson();

        this.bookId = bookId;
        this.chapterId = chapterId;

        doorCards = cards;

        textSize = pref.getInt(context.getString(R.string.books_text_size_key), 15) + MARGIN;

        getFavs();
    }

    private void getFavs() {
        String favsStr = pref.getString("book" + bookId +
                "_chapter" + chapterId + "_favs", "");
        if (favsStr.length() == 0)
            favs = new boolean[doorCards.size()];
        else
            favs = gson.fromJson(favsStr, boolean[].class);
    }

    @NonNull
    @Override
    public BookViewerAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        BookViewerAdapter.ViewHolder viewHolder = new BookViewerAdapter.ViewHolder(
                LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_book_door,
                        viewGroup, false));

        viewHolder.titleTv.setTextSize(textSize);
        viewHolder.textTv.setTextSize(textSize);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        BookDoorCard card = doorCards.get(position);

        viewHolder.titleTv.setText(doorCards.get(position).getDoorTitle());

        viewHolder.textTv.setText(doorCards.get(position).getText());

        boolean fav = card.isFav();
        if (fav)
            viewHolder.favBtn.setImageDrawable(
                    AppCompatResources.getDrawable(context, R.drawable.ic_star));
        else
            viewHolder.favBtn.setImageDrawable(
                    AppCompatResources.getDrawable(context, R.drawable.ic_star_outline));

        viewHolder.favBtn.setOnClickListener(view -> {
            if (card.isFav()) {
                favs[doorCards.get(position).getDoorId()] = false;
                card.setFav(false);
            }
            else {
                favs[doorCards.get(position).getDoorId()] = true;
                card.setFav(true);
            }
            notifyItemChanged(position);
            updateFavorites();
        });
    }

    private void updateFavorites() {
        String favStr = gson.toJson(favs);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("book" + bookId + "_chapter" + chapterId + "_favs", favStr);
        editor.apply();
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize + MARGIN;
    }

    @Override
    public int getItemCount() {
        return doorCards.size();
    }
}
