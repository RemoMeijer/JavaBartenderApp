package com.example.javabartender;

import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class DrinkAdapter extends RecyclerView.Adapter<DrinkAdapter.ViewHolder> {
    private List<Drink> drinks;
    private String ip;
    private String port;

    public DrinkAdapter(String ip, String port) {
        this.ip = ip;
        this.port = port;

        drinks = new ArrayList<>();

        executeGetRequest();
    }

    private void executeGetRequest() {
        String url = "http://" + ip + ":" + port + "/getDrinks"; // Adjust the URL as needed

        new HttpGetTask().execute(url);
    }

    private class HttpGetTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            try {
                String urlStr = params[0];

                URL url = new URL(urlStr);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                // Set the request method to GET
                connection.setRequestMethod("GET");

                // Get the response from the server
                InputStream inputStream = connection.getInputStream();
                InputStreamReader reader = new InputStreamReader(inputStream);
                StringBuilder result = new StringBuilder();
                int c;
                while ((c = reader.read()) != -1) {
                    result.append((char) c);
                }

                try {
                    JSONArray jsonArray = new JSONArray(result.toString());

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonDrink = jsonArray.getJSONObject(i);

                        String name = jsonDrink.getString("name");
                        JSONObject jsonIngredients = jsonDrink.getJSONObject("ingredients");

                        // Create a Map to store ingredients and their quantities
                        Map<String, Integer> ingredients = new HashMap<>();

                        // Iterate over the ingredients JSON object
                        Iterator<String> ingredientNames = jsonIngredients.keys();
                        while (ingredientNames.hasNext()) {
                            String ingredientName = ingredientNames.next();
                            int ingredientQuantity = jsonIngredients.getInt(ingredientName);
                            ingredients.put(ingredientName, ingredientQuantity);
                        }

                        // Create a Drink object and add it to the list
                        Drink drink = new Drink(name, ingredients);
                        drinks.add(drink);

                        publishProgress();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                for(Drink drink: drinks) {
                    System.out.println(drink.getName());
                }


                return result.toString();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            // Handle the result here
            if (result != null) {
                // Update UI or perform other tasks with the GET request result
            }
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            // Notify the adapter that the data set has changed
            notifyDataSetChanged();
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.drink_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Drink drink = drinks.get(position);
        holder.drinkNameTextView.setText(drink.getName());
        holder.drinkImageView.setImageResource(R.drawable.drink);
    }

    @Override
    public int getItemCount() {
        return drinks.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView drinkNameTextView;
        public ImageView drinkImageView;
        public View itemViewContext;

        public ViewHolder(View itemView) {
            super(itemView);
            drinkNameTextView = itemView.findViewById(R.id.drinkName);
            drinkImageView = itemView.findViewById(R.id.patchImage);
            itemViewContext = itemView;
            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View view) {
            CharSequence toastText = "Making drink: " + drinkNameTextView.getText().toString();
            int duration =  Toast.LENGTH_SHORT;

            Toast drinkToast = Toast.makeText(itemViewContext.getContext(), toastText, duration);
            drinkToast.show();
            
            new HttpPostTask().execute("http://" + ip + ":" + port, drinkNameTextView.getText().toString());
        }

        private class HttpPostTask extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... params) {
                try {
                    String urlStr = params[0];
                    String postData = params[1];

                    URL url = new URL(urlStr);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                    // Set the request method to POST
                    connection.setRequestMethod("POST");

                    // Enable input/output streams for the connection
                    connection.setDoInput(true);
                    connection.setDoOutput(true);

                    // Set the content type for the request body
                    connection.setRequestProperty("Content-Type", "text/plain; charset=UTF-8");

                    // Write the POST data to the output stream
                    try (DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream())) {
                        byte[] postDataBytes = postData.getBytes(StandardCharsets.UTF_8);
                        outputStream.write(postDataBytes);
                    }

                    // Get the response from the server
                    InputStream inputStream = connection.getInputStream();
                    InputStreamReader reader = new InputStreamReader(inputStream);
                    StringBuilder result = new StringBuilder();
                    int c;
                    while ((c = reader.read()) != -1) {
                        result.append((char) c);
                    }

                    System.out.println(result.toString());

                    return result.toString();
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String result) {
                // Handle the result here
                if (result != null) {
                    // Update UI or perform other tasks with the result
                }
            }
        }
    }
}
