package com.edibca.tests;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ShareActionProvider;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private ImageButton currPaint, drawBtn, eraseBtn, newBtn;
    private float smallBrush, mediumBrush, largeBrush;
    private LinearLayout linearColors, linearDrawButtons;

    private ShareActionProvider mShareActionProvider;
    private ArrayList<String> files;

    private String questionTitle;
    private int primaryColor, secondaryColor, accentColor;

    private Typeface font;

    private JSONArray json;
    private Map<Integer , Integer> respuestas;
    private ArrayList<Map<String, String>> opciones;
    private ListView listview1 , listview;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            json = new JSONArray(loadJSON());
            questionTitle = json.getJSONObject(0).getString("text");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        TextView title = (TextView) findViewById(R.id.calc_title);
        title.setTextColor(getResources().getColor(R.color.colorPrimary));
        title.setText(questionTitle);
        title.setTextSize(20);

        listview1 = (ListView) findViewById(R.id.test_listview);

        respuestas = new HashMap<>();

        ArrayList<Map<String , Object>> resources = new ArrayList<Map<String , Object>>();
        ArrayList<String> titles = new ArrayList<String>();

        for (int j = 0; j < json.length(); j++) {

            try {
                String tit = json.getJSONObject(j).getString("text");
                titles.add(tit);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            opciones = new ArrayList<Map<String, String>>();

            try {
                for (int i = 0; i < json.getJSONObject(j).getJSONArray("options").length(); i++) {
                    try {
                        String text = json.getJSONObject(j).getJSONArray("options").getJSONObject(i).getString("text");
                        int points = json.getJSONObject(j).getJSONArray("options").getJSONObject(i).getInt("points");
                        Map<String, String> preg = new HashMap<>();
                        preg.put("text", text);
                        Log.d("text", text);
                        preg.put("points", String.valueOf(points));
                        Log.d("points", String.valueOf(points));
                        opciones.add(preg);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


            ArrayList<Map<String, Object>> options = new ArrayList<Map<String, Object>>();

            try {
                for (int i = 0; i < json.getJSONObject(j).getJSONArray("options").length(); i++) {
                    try {
                        String text = json.getJSONObject(j).getJSONArray("options").getJSONObject(i).getString("text");
                        int points = json.getJSONObject(j).getJSONArray("options").getJSONObject(i).getInt("points");
                        Map<String, Object> preg = new HashMap<>();
                        preg.put("text", text);
                        Log.d("text", text);
                        preg.put("points", points);
                        Log.d("points", String.valueOf(points));
                        options.add(preg);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Map<String, Object> quest = new HashMap<String, Object>();

            try {
                String text = json.getJSONObject(j).getString("text");
                quest.put("text", text);
                Log.d("text", text);
                quest.put("options", options);
                options.add(quest);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            resources.add(quest);

        }



        final PreguntasAdapter preguntasAdapter = new PreguntasAdapter(getApplicationContext(), R.layout.listcell_test, resources);
        listview1.setAdapter(preguntasAdapter);


    }

    private class ContenidoAdapter extends ArrayAdapter<Map<String, String>> {
        Context context;
        int resourceid;
        List<Map<String, String>> data;

        public ContenidoAdapter(Context context , int resourceid , List<Map<String, String>> data) {
            super(context, resourceid, data);
            this.context = context;
            this.resourceid = resourceid;
            this.data = data;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null){
                LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.listcell, null);
            }

            TextView textView = (TextView) convertView.findViewById(R.id.firstLine);
            textView.setText(data.get(position).get("text"));
            textView.setTextColor(getResources().getColor(R.color.colorPrimary));

            if (data.get(position).get("points").equals(String.valueOf(respuestas.get(0)))){
                textView.setBackgroundResource(R.drawable.rounded_box_pressed);
                textView.setTextColor(getResources().getColor(R.color.white));
            } else {
                textView.setBackgroundResource(R.drawable.rounded_box);
            }

            return convertView;
        }
    }


    private class PreguntasAdapter extends ArrayAdapter<Map<String , Object>> {
        Context context;
        int resourceid;
        List<Map<String , Object>> data;

        public PreguntasAdapter(Context context , int resourceid , List<Map<String , Object>> data) {
            super(context, resourceid, data);
            this.context = context;
            this.resourceid = resourceid;
            this.data = data;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null){
                LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.listcell_test, null);
            }

            TextView textView = (TextView) convertView.findViewById(R.id.test_cell_title);
            Log.d("question",data.get(position).get("text").toString());
            textView.setText(data.get(position).get("text").toString());
            textView.setTextColor(getResources().getColor(R.color.colorPrimary));

            Log.d( "size" , String.valueOf(opciones.size()));

            final TextView result = (TextView) convertView.findViewById(R.id.calc_result);
            final ContenidoAdapter contenidoAdapter = new ContenidoAdapter(getApplicationContext(), R.layout.listcell, opciones);
            listview = (ListView) convertView.findViewById(R.id.intra_list);

            listview.setAdapter(contenidoAdapter);

            listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, final View view, int position1, long id) {



                    int index = position1;
                    Log.d("index" , String.valueOf(index));

                    //Guardar index's en un array
                    try {
                        int pPoints = json.getJSONObject(position).getJSONArray("options").getJSONObject(index).getInt("points");
                        respuestas.put(position , pPoints);
                        Log.d("points" , String.valueOf(respuestas.get(position)));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Log.d("r",respuestas.get(0).toString());

                    int sum = 0;

                    if (respuestas.keySet().size() == 1){

                        for (int i = 0; i< respuestas.keySet().size(); i++){
                            sum += respuestas.get(i);
                        }

                        result.setText(String.valueOf(sum));
                    }

                    contenidoAdapter.notifyDataSetChanged();

                }
            });



            return convertView;
        }
    }

    public String loadJSON() {
        String json = null;
        try {
            InputStream inputStream = getAssets().open("resources.json");

            int size = inputStream.available();

            byte[] buffer = new byte[size];

            inputStream.read(buffer);

            inputStream.close();

            json = new String(buffer, "UTF-8");


        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }
}
