package com.doanh.testggmaps;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;

import java.util.Collections;
import java.util.List;

public class CinemaAdapter extends ArrayAdapter {
    private final Context context;
    private final List<Cinema> cinemas;

    public CinemaAdapter(@NonNull Context context, List<Cinema> cinemas) {
        super(context, 0, cinemas);
        this.context = context;
        this.cinemas = cinemas;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.list_cinema_layout, parent, false);
        }

        Cinema cinema = cinemas.get(position);

        TextView cinemaName = view.findViewById(R.id.cinemaName);
        TextView distance = view.findViewById(R.id.distance);
        cinemaName.setText(cinema.getName());
//        LatLng latLng = new LatLng(cinema.getLatitude(), cinema.getLongitude());
//        cinema.setDistance(MainActivity.calculateDistance(latLng));
        distance.setText(String.format("%.2fKm", cinema.getDistance()));
        return view;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        Collections.sort(cinemas);
    }
}
