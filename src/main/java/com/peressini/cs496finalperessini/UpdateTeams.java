package com.peressini.cs496finalperessini;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

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

public class UpdateTeams extends AppCompatActivity {

    Button deleteTeamButton;
    Button updateTeamButton;
    OkHttpClient mOkHttpClient;

    public static final MediaType JSON = MediaType.parse("application/json");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_teams);

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
                    ArrayList<Team> teamList = new ArrayList<Team>();
                    ArrayList<String> heroNames = new ArrayList<String>();
                    JSONArray j = new JSONArray(r);
                    List<Map<String, String>> teams = new ArrayList<Map<String, String>>();
                    for (int i = 0; i < j.length(); i++) {
                        HashMap<String, String> m = new HashMap<String, String>();
                        m.put("name", j.getJSONObject(i).getString("name"));
                        m.put("assembled_date", j.getJSONObject(i).getString("assembled_date"));
                        m.put("leader", j.getJSONObject(i).getString("leader"));
                        JSONArray hero_arr = j.getJSONObject(i).getJSONArray("heroes");
                        for(int x = 0; x < hero_arr.length(); x++)
                        {
                            heroNames.add(hero_arr.getString(x));
                        }
                        m.put("id", j.getJSONObject(i).getString("id"));
                        teams.add(m);
                        teamList.add(new Team(m.get("id"), m.get("name")));


                    }

                    /*final SimpleAdapter teamsAdapter = new SimpleAdapter(
                            UpdateTeams.this,
                            teams,
                            android.R.layout.simple_spinner_item, //R.layout.spinner_item,
                            new String[]{"name", "id"},
                            new int[]{android.R.id.text1, android.R.id.text2}
                    );*/

                    final ArrayAdapter<Team> teamsAdapter = new ArrayAdapter<Team>(
                            UpdateTeams.this,
                            android.R.layout.simple_spinner_item,
                            teamList
                    );

                    final ArrayAdapter<String> leaderAdapter = new ArrayAdapter<String>(
                            UpdateTeams.this,
                            android.R.layout.simple_spinner_item,
                            heroNames
                    );

                    /*final ArrayAdapter<String> heroNamesAdapter = new ArrayAdapter<String>(
                            UpdateTeams.this,
                            android.R.layout.simple_list_item_checked,
                            heroNames
                    );*/



                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ((Spinner) findViewById(R.id.team_update_spin)).setAdapter(teamsAdapter);
                            ((Spinner) findViewById(R.id.team_delete_spin)).setAdapter(teamsAdapter);
                            ((Spinner) findViewById(R.id.update_leader_spin)).setAdapter(leaderAdapter);
                            //((ListView) findViewById(R.id.update_heroes_lv)).setAdapter(heroNamesAdapter);
                        }
                    });

                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
            }
        });

        /*final ListView mListView = findViewById(R.id.update_heroes_lv);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                ((CheckedTextView)view).setChecked(!((CheckedTextView)view).isChecked());
                /*HashMap<String, Object> obj = (HashMap<String, Object>) mListView.getAdapter().getItem(position);
                String ident = (String) obj.get("id");
                Log.d("Yourtag", ident);
            }
        });*/

        Button ct = (Button) findViewById(R.id.button_ct);
        ct.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                startActivity(new Intent(UpdateTeams.this, CreateTeam.class));
            }
        });

        Button ch = (Button) findViewById(R.id.button_ch);
        ch.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                startActivity(new Intent(UpdateTeams.this, MainActivity.class));
            }
        });

        Button uh = (Button) findViewById(R.id.button_uh);
        uh.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                startActivity(new Intent(UpdateTeams.this, UpdateHeroes.class));
            }
        });

        updateTeamButton = findViewById(R.id.button_up_team);

        updateTeamButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Team curTeam = (Team)((Spinner)findViewById(R.id.team_update_spin)).getSelectedItem();
                System.out.println(curTeam.getId());

                String json;
                String name = ((EditText) findViewById(R.id.update_team_name_input)).getText().toString();
                String assembled_date = ((EditText) findViewById(R.id.update_team_date_input)).getText().toString();
                String leader = ((Spinner) findViewById(R.id.update_leader_spin)).getSelectedItem().toString();
                //JSONArray heroes = ((EditText) findViewById(R.id.update_hero_power_input)).getText().toString();

                json = "{\"name\": \"" + name + "\", \"leader\": \"" + leader + "\", \"assembled_date\": \"" + assembled_date + "\"}"; //, \"main_superpower\": \"" + power + "\"}";

                mOkHttpClient = new OkHttpClient();
                String url = "https://cs-496-final-peressini.appspot.com/teams/" + curTeam.getId();
                HttpUrl reqUrl = HttpUrl.parse(url);
                RequestBody body = RequestBody.create(JSON, json);
                Request req = new Request.Builder()
                        .url(reqUrl)
                        .patch(body)
                        .build();

                mOkHttpClient.newCall(req).enqueue(new Callback(){
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {

                        final String r = response.body().string();
                        System.out.println(r);
                        //final TextView tv = (TextView)findViewById(R.id.hero_updated_text);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //tv.setText(r);
                                startActivity(new Intent(UpdateTeams.this, CreateTeam.class));
                            }
                        });


                    }
                });


            }


        });

        deleteTeamButton = findViewById(R.id.button_dt);

        deleteTeamButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                TextView textView = (TextView)((Spinner)findViewById(R.id.team_delete_spin)).getSelectedView();
                String name = textView.getText().toString();
                System.out.println(name);

                String json;
                /*String name = ((EditText) findViewById(R.id.add_hero_name_input)).getText().toString();
                String bop = ((EditText) findViewById(R.id.add_hero_bop_input)).getText().toString();
                String race = ((EditText) findViewById(R.id.add_hero_race_input)).getText().toString();
                String power = ((EditText) findViewById(R.id.add_hero_power_input)).getText().toString();*/

                json = "{\"name\": \"" + name + "\"}"; //, \"base_of_operations\": \"" + bop + "\", \"race\": \"" + race + "\", \"main_superpower\": \"" + power + "\"}";

                System.out.println(json);

                mOkHttpClient = new OkHttpClient();
                String url = "https://cs-496-final-peressini.appspot.com/teams?name=" + name;
                HttpUrl reqUrl = HttpUrl.parse(url);
                RequestBody body = RequestBody.create(JSON, json);
                Request req = new Request.Builder()
                        .url(reqUrl)
                        .delete()
                        .build();


                mOkHttpClient.newCall(req).enqueue(new Callback(){
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {

                        final String r = response.body().string();
                        System.out.println(r);
                        final TextView tv = (TextView)findViewById(R.id.team_deleted_text);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tv.setText(r);
                                startActivity(new Intent(UpdateTeams.this, UpdateTeams.class));
                            }
                        });


                    }
                });

            }
        });
    }
}