package bassamalim.hidaya.adapters;

import android.app.DownloadManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.cardview.widget.CardView;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import bassamalim.hidaya.R;
import bassamalim.hidaya.database.AppDatabase;
import bassamalim.hidaya.models.ReciterCard;
import bassamalim.hidaya.other.Util;

public class TelawatAdapter extends RecyclerView.Adapter<TelawatAdapter.ViewHolder> {

    private final Context context;
    private final AppDatabase db;
    private final SharedPreferences pref;
    private final RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();
    private final ArrayList<ReciterCard> recitersCards;
    private final ArrayList<ReciterCard> recitersCardsCopy;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView reciterNamescreen;
        private final ImageView favBtn;
        private final RecyclerView recyclerView;

        public ViewHolder(View view) {
            super(view);

            reciterNamescreen = view.findViewById(R.id.reciter_namescreen);
            favBtn = view.findViewById(R.id.telawa_fav_btn);
            recyclerView = view.findViewById(R.id.versions_recycler);
        }
    }

    public TelawatAdapter(Context context, ArrayList<ReciterCard> cards) {
        this.context = context;
        db = Room.databaseBuilder(context, AppDatabase.class, "HidayaDB").createFromAsset(
                "databases/HidayaDB.db").allowMainThreadQueries().build();

        pref = PreferenceManager.getDefaultSharedPreferences(context);

        recitersCards = new ArrayList<>(cards);
        recitersCardsCopy = new ArrayList<>(cards);
    }

    @NonNull @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_telawat_reciter, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        ReciterCard card = recitersCards.get(position);

        viewHolder.reciterNamescreen.setText(card.getName());

        doFavorite(viewHolder, position);

        setupVerRecycler(viewHolder, card);
    }

    public void filter(String text) {
        recitersCards.clear();
        if (text.isEmpty())
            recitersCards.addAll(recitersCardsCopy);
        else {
            for(ReciterCard reciterCard: recitersCardsCopy) {
                if (reciterCard.getName().contains(text))
                    recitersCards.add(reciterCard);
            }
        }
        notifyDataSetChanged();
    }

    private void doFavorite(ViewHolder viewHolder, int position) {
        ReciterCard card = recitersCards.get(position);

        int fav = card.getFavorite();
        if (fav == 0)
            viewHolder.favBtn.setImageDrawable(
                    AppCompatResources.getDrawable(context, R.drawable.ic_star_outline));
        else if (fav == 1)
            viewHolder.favBtn.setImageDrawable(
                    AppCompatResources.getDrawable(context, R.drawable.ic_star));

        viewHolder.favBtn.setOnClickListener(
                view -> {
                    if (card.getFavorite() == 0) {
                        db.telawatRecitersDao().setFav(card.getId(), 1);
                        card.setFavorite(1);
                    }
                    else if (card.getFavorite() == 1) {
                        db.telawatRecitersDao().setFav(card.getId(), 0);
                        card.setFavorite(0);
                    }
                    notifyItemChanged(position);

                    updateFavorites();
                });
    }

    private void setupVerRecycler(ViewHolder viewHolder, ReciterCard card) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setInitialPrefetchItemCount(card.getVersions().size());
        TelawaVersionAdapter versionsAdapter = new TelawaVersionAdapter(
                context, card.getVersions(), card.getId(), db.suraDao().getNames());
        viewHolder.recyclerView.setLayoutManager(layoutManager);
        viewHolder.recyclerView.setAdapter(versionsAdapter);
        viewHolder.recyclerView.setRecycledViewPool(viewPool);
    }

    private void updateFavorites() {
        Object[] favReciters = db.telawatRecitersDao().getFavs().toArray();

        Gson gson = new Gson();
        String recitersJson = gson.toJson(favReciters);

        SharedPreferences.Editor editor = pref.edit();
        editor.putString("favorite_reciters", recitersJson);
        editor.apply();
    }

    @Override
    public int getItemCount() {
        return recitersCards.size();
    }
}


class TelawaVersionAdapter extends RecyclerView.Adapter<TelawaVersionAdapter.ViewHolder> {

    private final Context context;
    private final List<ReciterCard.RecitationVersion> versions;
    private final int reciterId;
    private boolean[] downloaded;
    private final String prefix;
    private final List<String> names;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final CardView cardView;
        private final TextView tv;
        private final ImageButton download_btn;

        public ViewHolder(View view) {
            super(view);

            cardView = view.findViewById(R.id.main_card);
            tv = view.findViewById(R.id.version_namescreen);
            download_btn = view.findViewById(R.id.download_btn);
        }
    }

    public TelawaVersionAdapter(Context context, List<ReciterCard.RecitationVersion> versions,
                                int reciterId, List<String> names) {
        this.context = context;
        this.versions = versions;
        this.reciterId = reciterId;
        this.names = names;

        prefix = "/Telawat Downloads/" + reciterId;

        checkDownloaded();
    }

    @NonNull @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_telawa_version, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        ReciterCard.RecitationVersion ver = versions.get(position);

        viewHolder.tv.setText(ver.getRewaya());

        viewHolder.cardView.setOnClickListener(ver.getListener());

        doDownloaded(viewHolder, position, ver);
    }

    private void doDownloaded(ViewHolder viewHolder, int position,
                              ReciterCard.RecitationVersion ver) {

        int verId = versions.get(position).getVersionId();

        if (downloaded[verId])
            viewHolder.download_btn.setImageDrawable(AppCompatResources.getDrawable(
                    context, R.drawable.ic_downloaded));
        else
            viewHolder.download_btn.setImageDrawable(AppCompatResources.getDrawable(
                    context, R.drawable.ic_download));

        viewHolder.download_btn.setOnClickListener(v -> {
            if (downloaded[verId]) {
                String postfix = prefix + "/" + ver.getVersionId();
                Util.deleteFile(context, postfix);

                downloaded[ver.getVersionId()] = false;
                viewHolder.download_btn.setImageDrawable(AppCompatResources.getDrawable(
                        context, R.drawable.ic_download));
            }
            else {
                downloadVer(ver);

                viewHolder.download_btn.setImageDrawable(AppCompatResources.getDrawable(
                        context, R.drawable.ic_downloaded));
            }
        });
    }

    private void checkDownloaded() {
        downloaded = new boolean[versions.size()];

        File dir = new File(context.getExternalFilesDir(null) + prefix);

        if (!dir.exists())
            return;

        File[] files = dir.listFiles();

        if (files == null || files.length == 0)
            return;

        for (int i = 0; i < Objects.requireNonNull(files).length; i++) {
            String name = files[i].getName();
            try {
                int num = Integer.parseInt(name);
                if (Objects.requireNonNull(files[i].listFiles()).length == 0)
                    cleanup(dir.getAbsolutePath(), name);
                else
                    downloaded[num] = true;
            } catch (NumberFormatException ignored) {}
        }
    }

    private void downloadVer(ReciterCard.RecitationVersion ver) {
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(
                Context.DOWNLOAD_SERVICE);
        DownloadManager.Request request;

        for (int i = 0; i < 114; i++) {
            if (ver.getSuras().contains("," + (i+1) + ",")) {
                String link = String.format(Locale.US, "%s/%03d.mp3",
                        ver.getServer(), i+1);

                Uri uri = Uri.parse(link);
                request = new DownloadManager.Request(uri);
                request.setTitle(names.get(i));
                String postfix = prefix + "/" + ver.getVersionId();
                Util.createDir(context, postfix);
                request.setDestinationInExternalFilesDir(context, postfix, i + ".mp3");
                request.setNotificationVisibility(
                        DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                downloadManager.enqueue(request);
            }
        }

        downloaded[ver.getVersionId()] = true;
    }

    private void cleanup(String path, String name) {
        File dir = new File(path + "/" + name);
        if (Objects.requireNonNull(dir.listFiles()).length == 0)
            dir.delete();

        dir = new File(path);
        if (Objects.requireNonNull(dir.listFiles()).length == 0)
            dir.delete();
    }

    @Override
    public int getItemCount() {
        return versions.size();
    }
}
