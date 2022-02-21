package bassamalim.hidaya.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import bassamalim.hidaya.databinding.ActivityTvBinding;
import bassamalim.hidaya.other.Const;
import bassamalim.hidaya.other.Util;

public class TvActivity extends YouTubeBaseActivity {

    private ActivityTvBinding binding;
    private FirebaseRemoteConfig remoteConfig;
    private String makkah_url;
    private String madina_url;
    private final String apiKey = "AIzaSyBndJVjigZ7MOmj1005ONLUsfFW7BfxZt0";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Util.onActivityCreateSetTheme(this);
        binding = ActivityTvBinding.inflate(getLayoutInflater());
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(binding.getRoot());

        getLinks();

        initYtPlayer();
    }

    private void getLinks() {
        remoteConfig = FirebaseRemoteConfig.getInstance();
        remoteConfig.fetchAndActivate()
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        makkah_url = remoteConfig.getString("makkah_url");
                        madina_url = remoteConfig.getString("madina_url");

                        Log.d(Const.TAG, "Config params updated");
                        Log.d(Const.TAG, "Makkah URL: " + makkah_url);
                        Log.d(Const.TAG, "Madina URL: " + madina_url);
                    }
                    else
                        Log.d(Const.TAG, "Fetch failed");
                });
    }

    private void initYtPlayer() {
        binding.ytPlayer.initialize(apiKey, new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider,
                                                YouTubePlayer youTubePlayer, boolean b) {
                setListeners(youTubePlayer);
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider,
                                                YouTubeInitializationResult
                                                        youTubeInitializationResult) {
                Toast.makeText(getApplicationContext(), "فشل التشغيل",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setListeners(YouTubePlayer ytPlayer) {
        binding.quranBtn.setOnClickListener(v -> {
            ytPlayer.loadVideo(makkah_url);
            ytPlayer.play();
        });
        binding.sunnahBtn.setOnClickListener(v -> {
            ytPlayer.loadVideo(madina_url);
            ytPlayer.play();
        });
    }

}
