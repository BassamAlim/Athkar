package bassamalim.hidaya.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import bassamalim.hidaya.activities.AboutActivity;
import bassamalim.hidaya.activities.DateConverter;
import bassamalim.hidaya.activities.FeaturesGuide;
import bassamalim.hidaya.activities.HadeethBooks;
import bassamalim.hidaya.activities.QuizLobbyActivity;
import bassamalim.hidaya.activities.RadioClient;
import bassamalim.hidaya.activities.Settings;
import bassamalim.hidaya.activities.TelawatCollectionActivity;
import bassamalim.hidaya.activities.TvActivity;
import bassamalim.hidaya.databinding.FragmentOtherBinding;
import bassamalim.hidaya.other.Const;

public class OtherFragment extends Fragment {

    private FragmentOtherBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentOtherBinding.inflate(inflater, container, false);

        setListeners();

        return binding.getRoot();
    }

    public void setListeners() {
        binding.telawat.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Intent intent = new Intent(getContext(), TelawatCollectionActivity.class);
                startActivity(intent);
            }
            else
                Toast.makeText(getContext(), "جهازك لا يدعم هذه الميزة",
                        Toast.LENGTH_SHORT).show();
        });

        binding.quiz.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), QuizLobbyActivity.class);
            startActivity(intent);
        });

        binding.hadeethBooks.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), HadeethBooks.class);
            startActivity(intent);
        });

        binding.channels.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), TvActivity.class);
            startActivity(intent);
        });

        binding.radio.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Intent intent = new Intent(getContext(), RadioClient.class);
                startActivity(intent);
            }
            else
                Toast.makeText(getContext(), "جهازك لا يدعم هذه الميزة",
                        Toast.LENGTH_SHORT).show();
        });

        binding.dateConverter.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), DateConverter.class);
            startActivity(intent);
        });

        binding.settings.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), Settings.class);
            startActivity(intent);
        });

        binding.features.setOnClickListener(view -> {
            Intent intent = new Intent(getContext(), FeaturesGuide.class);
            startActivity(intent);
        });

        binding.contact.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                    "mailto",Const.CONTACT_EMAIL, null));
            intent.putExtra(Intent.EXTRA_SUBJECT, "Hidaya");
            startActivity(Intent.createChooser(intent, "Choose an Email client :"));
        });

        binding.share.setOnClickListener(v -> {
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "App Share");
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, Const.PLAY_STORE_URL);
            startActivity(Intent.createChooser(sharingIntent, "Share via"));
        });

        binding.about.setOnClickListener(v -> {
            Intent about = new Intent(getContext(), AboutActivity.class);
            startActivity(about);
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}