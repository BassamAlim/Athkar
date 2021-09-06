package com.bassamalim.athkar.ui;

import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.bassamalim.athkar.Constants;
import com.bassamalim.athkar.MainActivity;
import com.bassamalim.athkar.QiblaMaster;
import com.bassamalim.athkar.R;
import com.bassamalim.athkar.databinding.QiblaFragmentBinding;

import java.util.HashMap;

public class QiblaFragment extends Fragment {

    private QiblaFragmentBinding binding;
    private QiblaMaster compass;
    private Location location;
    private float currentAzimuth;
    private double distance;
    private float bearing;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        location = MainActivity.location;

        distance = getDistance();
        bearing = calculateBearing();

        setupCompass();

        if (compass != null)
            compass.start(requireContext());
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = QiblaFragmentBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        String text = "المسافة الى الكعبة: " + translateNumbers(String.valueOf(distance)) + " كم";
        binding.distanceText.setText(text);

        inflater.inflate(R.layout.qibla_fragment, container, false);
        return root;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (compass != null)
            compass.stop();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (compass != null)
            compass.start(requireContext());
    }

    @Override
    public void onStop() {
        super.onStop();
        if (compass != null)
            compass.stop();
    }

    private void setupCompass() {
        compass = new QiblaMaster(requireContext());

        QiblaMaster.CompassListener listener = azimuth -> {
            //adjustGambarDial(azimuth);
            adjust(azimuth);
        };

        compass.setListener(listener);
    }

    private void adjust(float azimuth) {
        float target = bearing - currentAzimuth;

        Animation rotate = new RotateAnimation(target, -azimuth, Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(500);
        rotate.setRepeatCount(0);
        rotate.setFillAfter(true);
        binding.qiblaView.startAnimation(rotate);

        currentAzimuth = (azimuth);

        if (target > -2 && target < 2)
            binding.bingo.setVisibility(View.VISIBLE);
        else
            binding.bingo.setVisibility(View.INVISIBLE);
    }

    // maybe points north
    /*public void adjustGambarDial(float azimuth) {
        Animation an = new RotateAnimation(-currentAzimuth, -azimuth,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        currentAzimuth = (azimuth);
        an.setDuration(500);
        an.setRepeatCount(0);
        an.setFillAfter(true);
        imageDial.startAnimation(an);
    }*/

    public double getDistance() {
        double dLon = Math.toRadians(Math.abs(location.getLatitude() - Constants.KAABA_LAT));
        double dLat = Math.toRadians(Math.abs(location.getLongitude() - Constants.KAABA_LNG));
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) + Math.cos(Math.toRadians(location.getLatitude()))
                * Math.cos(Math.toRadians(Constants.KAABA_LAT)) * Math.sin(dLon/2) * Math.sin(dLon/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        distance = Constants.EARTH_RADIUS * c;
        distance = (int) (distance * 10) / 10.0;

        return distance;
    }

    private float calculateBearing() {
        float result;
        double myLatRad = Math.toRadians(location.getLatitude());
        double lngDiff = Math.toRadians(Constants.KAABA_LNG - location.getLongitude());
        double y = Math.sin(lngDiff) * Math.cos(Constants.KAABA_LAT_IN_RAD);
        double x = Math.cos(myLatRad) * Math.sin(Constants.KAABA_LAT_IN_RAD) - Math.sin(myLatRad)
                * Math.cos(Constants.KAABA_LAT_IN_RAD) * Math.cos(lngDiff);
        result = (float) ((Math.toDegrees(Math.atan2(y, x)) + 360) % 360);

        return result;
    }

    private String translateNumbers(String english) {
        String result;
        HashMap<Character, Character> map = new HashMap<>();
        map.put('0', '٠');
        map.put('1', '١');
        map.put('2', '٢');
        map.put('3', '٣');
        map.put('4', '٤');
        map.put('5', '٥');
        map.put('6', '٦');
        map.put('7', '٧');
        map.put('8', '٨');
        map.put('9', '٩');
        map.put('A', 'ص');
        map.put('P', 'م');

        if (english.charAt(0) == '0') {
            english = english.replaceFirst("0", "");
            if (english.charAt(0) == '0')
                english = english.replaceFirst("0:", "");
        }


        english = english.replaceAll(":0", ":");

        StringBuilder temp = new StringBuilder();
        for (int j = 0; j < english.length(); j++) {
            char t = english.charAt(j);
            if (map.containsKey(t))
                t = map.get(t);
            temp.append(t);
        }
        result = temp.toString();

        return result;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}