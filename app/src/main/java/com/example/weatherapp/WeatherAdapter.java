package com.example.weatherapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.ViewHolder> {
    private Context context;
    private ArrayList<WeatherModel>WeatherModelArray;

    public WeatherAdapter(Context context, ArrayList<WeatherModel> weatherModelArray) {
        this.context = context;
        this.WeatherModelArray = weatherModelArray;
    }

    @NonNull
    @Override
    public WeatherAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.weather_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WeatherModel model=WeatherModelArray.get(position);
        holder.idTemparatureCard.setText(model.getTemp()+"°C");

        String iconUrl = model.getIcon();
        if (!iconUrl.startsWith("http")) {
            iconUrl = "https:" + iconUrl;
        }
        Picasso.get().load(iconUrl).into(holder.idConditionCard);


        holder.idWindspeed.setText(model.getWindspeed()+"Km/h");
        SimpleDateFormat input=new SimpleDateFormat("yyyy-MM-dd HH:mm");
        SimpleDateFormat output=new SimpleDateFormat("hh:mm aa");
        try {
            Date t=input.parse(model.getTime());
            holder.idTime.setText(output.format(t));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int getItemCount() {
        return WeatherModelArray.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView idTime,idTemparatureCard,idWindspeed;
        private ImageView idConditionCard;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            idTime=itemView.findViewById(R.id.idTime);
            idTemparatureCard=itemView.findViewById(R.id.idTemparatureCard);
            idWindspeed=itemView.findViewById(R.id.idWindspeed);
            idConditionCard=itemView.findViewById(R.id.idConditionCard);
        }
    }
}
