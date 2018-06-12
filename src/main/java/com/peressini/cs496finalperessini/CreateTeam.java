package com.peressini.cs496finalperessini;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.json.JSONArray;
import org.json.JSONException;

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

public class CreateTeam extends AppCompatActivity {

    Button addTeamButton;
    OkHttpClient mOkHttpClient;

    public static final MediaType JSON = MediaType.parse("application/json");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_team);

        mOkHttpClient = new OkHttpClient();
        HttpUrl reqUrl = HttpUrl.parse("https://cs-496-final-peressini.appspot.com/teams");
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
                    List<Map<String, String>> teams = new ArrayList<Map<String, String>>();
                    for (int i = 0; i < j.length(); i++) {
                        HashMap<String, String> m = new HashMap<String, String>();
                        m.put("name", j.getJSONObject(i).getString("name"));
                        JSONArray hero_arr = j.getJSONObject(i).getJSONArray("heroes");
                        if(hero_arr.length() == 0)
                            m.put("heroes", "Team has no current members.");
                        else {
                            String heroString = new String();
                            for (int x = 0; x < hero_arr.length(); x++) {
                                heroString += hero_arr.getString(x) + " ";
                            }
                            m.put("heroes", heroString);
                        }
                        m.put("assembled_date", j.getJSONObject(i).getString("assembled_date"));
                        if(j.getJSONObject(i).isNull("leader") || j.getJSONObject(i).getString("leader").equals(""))
                            m.put("leader", "Team has no leader");
                        else
                            m.put("leader", j.getJSONObject(i).getString("leader"));
                        m.put("id", j.getJSONObject(i).getString("id"));
                        teams.add(m);

                    }

                    final SimpleAdapter teamsAdapter = new SimpleAdapter(
                            CreateTeam.this,
                            teams,
                            R.layout.team_item,
                            new String[]{"name", "heroes", "assembled_date", "leader", "id"},
                            new int[]{R.id.team_name, R.id.team_heroes, R.id.team_date, R.id.team_leader, R.id.team_id}
                    );

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ((ListView) findViewById(R.id.team_lv)).setAdapter(teamsAdapter);
                        }
                    });

                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
            }
        });

        final ListView mListView = findViewById(R.id.team_lv);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HashMap<String, Object> obj = (HashMap<String, Object>) mListView.getAdapter().getItem(position);
                String ident = (String) obj.get("id");
                Log.d("Yourtag", ident);
            }
        });

        Button ct = (Button) findViewById(R.id.button_ch);
        ct.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CreateTeam.this, MainActivity.class));
            }
        });

        Button uh = (Button) findViewById(R.id.button_uh);
        uh.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CreateTeam.this, UpdateHeroes.class));
            }
        });

        Button ut = (Button) findViewById(R.id.button_ut);
        ut.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CreateTeam.this, UpdateTeams.class));
            }
        });


        addTeamButton = findViewById(R.id.add_team_button);

        addTeamButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                String json;
                String name = ((EditText) findViewById(R.id.add_team_name_input)).getText().toString();
                //String bop = ((EditText) findViewById(R.id.add_hero_bop_input)).getText().toString();
                //String race = ((EditText) findViewById(R.id.add_hero_race_input)).getText().toString();
                //String power = ((EditText) findViewById(R.id.add_hero_power_input)).getText().toString();

                json = "{\"name\": \"" + name + "\"}"; //, \"base_of_operations\": \"" + bop + "\", \"race\": \"" + race + "\", \"main_superpower\": \"" + power + "\"}";

                System.out.println(json);

                mOkHttpClient = new OkHttpClient();
                HttpUrl reqUrl = HttpUrl.parse("https://cs-496-final-peressini.appspot.com/teams");
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

                        System.out.println(response.body().string());

                        mOkHttpClient = new OkHttpClient();
                        HttpUrl reqUrl = HttpUrl.parse("https://cs-496-final-peressini.appspot.com/teams");
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
                                    List<Map<String, String>> teams = new ArrayList<Map<String, String>>();
                                    for (int i = 0; i < j.length(); i++) {
                                        HashMap<String, String> m = new HashMap<String, String>();
                                        m.put("name", j.getJSONObject(i).getString("name"));
                                        JSONArray hero_arr = j.getJSONObject(i).getJSONArray("heroes");
                                        if(hero_arr.length() == 0)
                                            m.put("heroes", "Team has no current members.");
                                        else {
                                            String heroString = new String();
                                            for (int x = 0; x < hero_arr.length(); x++) {
                                                heroString += hero_arr.getString(x) + " ";
                                            }
                                            m.put("heroes", heroString);
                                        }
                                        m.put("assembled_date", j.getJSONObject(i).getString("assembled_date"));
                                        if(j.getJSONObject(i).isNull("leader") || j.getJSONObject(i).getString("leader").equals(""))
                                            m.put("leader", "Team has no leader");
                                        else
                                            m.put("leader", j.getJSONObject(i).getString("leader"));
                                        m.put("id", j.getJSONObject(i).getString("id"));
                                        teams.add(m);
                                    }

                                    final SimpleAdapter teamsAdapter = new SimpleAdapter(
                                            CreateTeam.this,
                                            teams,
                                            R.layout.team_item,
                                            new String[]{"name", "heroes", "assembled_date", "leader", "id"},
                                            new int[]{R.id.team_name, R.id.team_heroes, R.id.team_date, R.id.team_leader, R.id.team_id}
                                    );

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            ((ListView) findViewById(R.id.team_lv)).setAdapter(teamsAdapter);
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