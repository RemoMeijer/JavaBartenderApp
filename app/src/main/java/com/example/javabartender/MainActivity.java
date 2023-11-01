package com.example.javabartender;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView drinksRecyclerView = findViewById(R.id.drinksRecyclerView);
        LinearLayoutManager drinksLayoutManager = new LinearLayoutManager(this);
        drinksRecyclerView.setLayoutManager(drinksLayoutManager);

        RecyclerView pumpsRecyclerView = findViewById(R.id.pumpsRecyclerView);
        LinearLayoutManager pumpsLayoutManager = new LinearLayoutManager(this);
        pumpsRecyclerView.setLayoutManager(pumpsLayoutManager);


        BottomNavigationView bottomNavigationView = findViewById(R.id.navBar);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.drinks:
                    drinksRecyclerView.setVisibility(View.VISIBLE);
                    pumpsRecyclerView.setVisibility(View.GONE);
                    return true;
                case R.id.pumps:
                    drinksRecyclerView.setVisibility(View.GONE);
                    pumpsRecyclerView.setVisibility(View.VISIBLE);
                    return true;
            }
            return false;
        });

        TextView ipText = findViewById(R.id.ipText);
        TextView portText = findViewById(R.id.portText);
        Button ipButton = findViewById(R.id.ipButton);

                ipButton.setOnClickListener(view -> {

                    DrinkAdapter drinkAdapter = new DrinkAdapter(ipText.getText().toString(), portText.getText().toString());
                    drinksRecyclerView.setAdapter(drinkAdapter);

                    PumpAdapter pumpAdapter = new PumpAdapter(ipText.getText().toString(), portText.getText().toString());
                    pumpsRecyclerView.setAdapter(pumpAdapter);
                });
    }
}
