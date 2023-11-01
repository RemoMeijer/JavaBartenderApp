package com.example.javabartender;

import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PumpAdapter extends RecyclerView.Adapter<PumpAdapter.ViewHolder> {
    private List<Pump> pumps;
    private final String ip;
    private final String port;

    public PumpAdapter(String ip, String port) {
        this.ip = ip;
        this.port = port;

        pumps = new ArrayList<>();

        executeGetRequest();
    }

    private void executeGetRequest() {
        String url = "http://" + ip + ":" + port + "/getPumps"; // Adjust the URL as needed

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
                    JSONObject jsonObject = new JSONObject(result.toString()); // Assuming 'result' contains your JSON string

                    // Iterate through the keys (pump names)
                    Iterator<String> keys = jsonObject.keys();
                    while (keys.hasNext()) {
                        String pumpKey = keys.next();
                        JSONObject jsonPump = jsonObject.getJSONObject(pumpKey);

                        String name = jsonPump.getString("name");
                        String value = jsonPump.getString("value");

                        // Create a Pump object and add it to the list
                        Pump pump = new Pump(name, value);
                        pumps.add(pump);

                        publishProgress();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                for(Pump pump: pumps) {
                    System.out.println(pump.getPumpName());
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
                .inflate(R.layout.pump_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Pump pump = pumps.get(position);
        holder.pumpNameTextView.setText(pump.getPumpName());
        holder.pumpValueTextView.setText(pump.getPumpValue());
        holder.barrelImageView.setImageResource(R.drawable.barrel);
    }

    @Override
    public int getItemCount() {
        return pumps.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView pumpNameTextView;
        public TextView pumpValueTextView;
        public ImageView barrelImageView;

        public ViewHolder(View itemView) {
            super(itemView);
            pumpNameTextView = itemView.findViewById(R.id.pumpName);
            pumpValueTextView = itemView.findViewById(R.id.pumpIngredient);
            barrelImageView = itemView.findViewById(R.id.barrelImage);
            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View view) {

        }
    }
}
