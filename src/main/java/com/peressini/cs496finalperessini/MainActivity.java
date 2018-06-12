package com.peressini.cs496finalperessini;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    Button addHeroButton;
    OkHttpClient mOkHttpClient;

    public static final MediaType JSON = MediaType.parse("application/json");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mOkHttpClient = new OkHttpClient();
        HttpUrl reqUrl = HttpUrl.parse("https://cs-496-final-peressini.appspot.com/heroes");
        Request req = new Request.Builder()
                .url(reqUrl)
                .build();

        mOkHttpClient.newCall(req).enqueue(new Callback(){
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String r = response.body().string();
                try {
                    System.out.println(r);
                    //LinearLayout heroes_list = findViewById(R.id.heroes_list);
                    JSONArray j = new JSONArray(r);
                    List<Map<String, String>> heroes = new ArrayList<Map<String, String>>();
                    for (int i = 0; i < j.length(); i++) {
                        HashMap<String, String> m = new HashMap<String, String>();
                        m.put("name", j.getJSONObject(i).getString("name"));
                        m.put("base_of_operations", j.getJSONObject(i).getString("base_of_operations"));
                        m.put("race", j.getJSONObject(i).getString("race"));
                        m.put("main_superpower", j.getJSONObject(i).getString("main_superpower"));
                        m.put("id", j.getJSONObject(i).getString("id"));
                        heroes.add(m);


                    }

                    final SimpleAdapter heroesAdapter = new SimpleAdapter(
                            MainActivity.this,
                            heroes,
                            R.layout.hero_item,
                            new String[]{"name", "id", "base_of_operations", "race", "main_superpower"},
                            new int[]{R.id.hero_name, R.id.hero_id, R.id.hero_bop, R.id.hero_race, R.id.hero_power}
                            );

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ((ListView) findViewById(R.id.hero_lv)).setAdapter(heroesAdapter);
                        }
                    });

                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
            }
        });

        final ListView mListView = findViewById(R.id.hero_lv);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HashMap<String, Object> obj = (HashMap<String, Object>) mListView.getAdapter().getItem(position);
                String ident = (String) obj.get("id");
                Log.d("Yourtag", ident);
            }
        });

        Button ct = (Button) findViewById(R.id.button_ct);
        ct.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, CreateTeam.class));
            }
        });

        Button uh = (Button) findViewById(R.id.button_uh);
        uh.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, UpdateHeroes.class));
            }
        });

        Button ut = (Button) findViewById(R.id.button_ut);
        ut.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, UpdateTeams.class));
            }
        });


        addHeroButton = findViewById(R.id.add_hero_button);

        addHeroButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                String json;
                String name = ((EditText) findViewById(R.id.add_hero_name_input)).getText().toString();
                String bop = ((EditText) findViewById(R.id.add_hero_bop_input)).getText().toString();
                String race = ((EditText) findViewById(R.id.add_hero_race_input)).getText().toString();
                String power = ((EditText) findViewById(R.id.add_hero_power_input)).getText().toString();

                json = "{\"name\": \"" + name + "\", \"base_of_operations\": \"" + bop + "\", \"race\": \"" + race + "\", \"main_superpower\": \"" + power + "\"}";

                System.out.println(json);

                mOkHttpClient = new OkHttpClient();
                HttpUrl reqUrl = HttpUrl.parse("https://cs-496-final-peressini.appspot.com/heroes");
                RequestBody body = RequestBody.create(JSON, json);
                Request req = new Request.Builder()
                        .url(reqUrl)
                        .post(body)
                        .build();


                mOkHttpClient.newCall(req).enqueue(new Callback(){
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {

                        mOkHttpClient = new OkHttpClient();
                        HttpUrl reqUrl = HttpUrl.parse("https://cs-496-final-peressini.appspot.com/heroes");
                        Request req = new Request.Builder()
                                .url(reqUrl)
                                .build();

                        mOkHttpClient.newCall(req).enqueue(new Callback(){
                            @Override
                            public void onFailure(Call call, IOException e) {
                                e.printStackTrace();
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                String r = response.body().string();
                                try {

                                    JSONArray j = new JSONArray(r);
                                    List<Map<String, String>> heroes = new ArrayList<Map<String, String>>();
                                    for (int i = 0; i < j.length(); i++) {
                                        HashMap<String, String> m = new HashMap<String, String>();
                                        m.put("name", j.getJSONObject(i).getString("name"));
                                        m.put("base_of_operations", j.getJSONObject(i).getString("base_of_operations"));
                                        m.put("race", j.getJSONObject(i).getString("race"));
                                        m.put("main_superpower", j.getJSONObject(i).getString("main_superpower"));
                                        m.put("id", j.getJSONObject(i).getString("id"));
                                        heroes.add(m);
                                    }

                                    final SimpleAdapter heroesAdapter = new SimpleAdapter(
                                            MainActivity.this,
                                            heroes,
                                            R.layout.hero_item,
                                            new String[]{"name", "id", "base_of_operations", "race", "main_superpower"},
                                            new int[]{R.id.hero_name, R.id.hero_id, R.id.hero_bop, R.id.hero_race, R.id.hero_power}
                                    );

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            ((ListView) findViewById(R.id.hero_lv)).setAdapter(heroesAdapter);
                                        }
                                    });

                                } catch (JSONException e1) {
                                    e1.printStackTrace();
                                }
                            }
                        });
                    }
                });

            }
        });
    }
}
