package com.example.aaa;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;

public class SignupStep1by3 extends AppCompatActivity {
    JSONObject jsonobject;
    Spinner spinner1;
    Spinner spinner2;
    Spinner spinner3;
    EditText phone,name;

    private Handler mainHandler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_step1by3);
        ImageButton xyz = (ImageButton) findViewById(R.id.roundbuttonsignup);
        spinner1=(Spinner)findViewById(R.id.spinner1);
        spinner2=(Spinner)findViewById(R.id.spinner2);
        spinner3=(Spinner)findViewById(R.id.spinner3);
        phone=(EditText)findViewById(R.id.mobileno2);
        name=(EditText)findViewById(R.id.name2);

        ImageButton date = (ImageButton) findViewById(R.id.datebutton);
        final EditText date1=(EditText) findViewById(R.id.date);
        // perform click event on edit text
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // calender class's instance and get current date , month and year from calender
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR); // current year
                int mMonth = c.get(Calendar.MONTH); // current month
                int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
                // date picker dialog
                DatePickerDialog datePickerDialog = new DatePickerDialog(SignupStep1by3.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                // set day of month , month and year value in the edit text
                                date1.setText(dayOfMonth + "/"
                                        + (monthOfYear + 1) + "/" + year);

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });
        new Thread(new Runnable() {
            @Override
            public void run() {

                final ArrayList<String> items1=getCountries("countries+states+cities.json");
                final ArrayAdapter<String> adapter1=new ArrayAdapter<String>(SignupStep1by3.this,android.R.layout.simple_list_item_1,items1);
                adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        spinner1.setAdapter(adapter1);
                    }
                });
            }
        }).start();
        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, final int position, long id) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        ArrayList<String> items2=getStates("countries+states+cities.json",position);
                        final ArrayAdapter<String> adapter2=new ArrayAdapter<String>(SignupStep1by3.this,android.R.layout.simple_list_item_1,items2);
                        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                spinner2.setAdapter(adapter2);
                            }
                        });
                    }
                }).start();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, final int position, long id) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String state = spinner2.getSelectedItem().toString();
                        ArrayList<String> items3=getCities("countries+states+cities.json",state);
                        final ArrayAdapter<String> adapter3=new ArrayAdapter<String>(SignupStep1by3.this,android.R.layout.simple_list_item_1,items3);
                        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                spinner3.setAdapter(adapter3);
                            }
                        });
                    }
                }).start();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        xyz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(name.getText().toString().isEmpty())
                {
                    name.setError("Name Required");
                }
                if(date1.getText().toString().isEmpty())
                {
                    date1.setError("Required");
                }
                if(!phone.getText().toString().isEmpty() && phone.getText().toString().length() == 10) {

                    String phoneNum = "+91"+phone.getText().toString();
                    Intent in= new Intent(SignupStep1by3.this,otp.class);
                    in.putExtra("phoneno",phoneNum);
                    startActivity(in);


                }
                else {
                    phone.setError("Valid Phone Required");
                }
            }
        });

    }
    private ArrayList<String> getCountries(String fileName){
        JSONArray jsonArray=null;
        ArrayList<String> cList=new ArrayList<String>();
        try {
            InputStream is = getResources().getAssets().open(fileName);
            int size = is.available();
            byte[] data = new byte[size];
            is.read(data);
            is.close();
            String json = new String(data, "UTF-8");
            jsonArray=new JSONArray(json);
            if (jsonArray != null) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    cList.add(jsonArray.getJSONObject(i).getString("name"));
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }catch (JSONException je){
            je.printStackTrace();
        }
        return cList;
    }
    private ArrayList<String> getStates(String fileName,int pos){
        JSONArray jsonArray=null;
        ArrayList<String> cList=new ArrayList<String>();
        try {
            InputStream is = getResources().getAssets().open(fileName);
            int size = is.available();
            byte[] data = new byte[size];
            is.read(data);
            is.close();
            String json = new String(data, StandardCharsets.UTF_8);
            jsonArray=new JSONArray(json);
            if (jsonArray != null) {
                Iterator st =jsonArray.getJSONObject(pos).getJSONObject("states").keys();
                while(st.hasNext()) {

                    String currentKey = (String)st.next();
                    cList.add(currentKey);
                }
                jsonobject=jsonArray.getJSONObject(pos);
            }
        }catch (IOException e){
            e.printStackTrace();
        }catch (JSONException je){
            je.printStackTrace();
        }
        return cList;
    }
    private ArrayList<String> getCities(String fileName,String state){

        ArrayList<String> cList=new ArrayList<String>();
        try {


            JSONArray st= jsonobject.getJSONObject("states").getJSONArray(state);
            for(int i=0;i<st.length();i++)
            {
                String city = st.getString(i);
                cList.add(city);

            }


        }catch (JSONException je){
            je.printStackTrace();
        }
        return cList;
    }
}
