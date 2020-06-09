package com.example.aaa;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

public class create_acc_busi extends AppCompatActivity {


    ImageButton date;
    EditText date1;
    EditText phone,name,email,password,street;
    DatePickerDialog datePickerDialog;
    Button con;
    Spinner spinner1;
    Spinner spinner2;
    Spinner spinner3;
    JSONObject jsonobject;


    FirebaseAuth fAuth;

    HashMap<String,String> user;
    private Handler mainHandler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_acc_busi);
        phone= findViewById(R.id.mobileno1);
        con= findViewById(R.id.continue1);
        name= findViewById(R.id.name);
        email= findViewById(R.id.email);
        street=findViewById(R.id.street1);
        password= findViewById(R.id.password);
        user=new HashMap<>();
        fAuth = FirebaseAuth.getInstance();

        spinner1= findViewById(R.id.spinner1);
        spinner2= findViewById(R.id.spinner2);
        spinner3= findViewById(R.id.spinner3);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // initiate the date picker and a button
        datepicker();
        new Thread(new Runnable() {
            @Override
            public void run() {

                final ArrayList<String> items1=getCountries("countries+states+cities.json");
                final ArrayAdapter<String> adapter1=new ArrayAdapter<>(create_acc_busi.this,android.R.layout.simple_list_item_1,items1);
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
                        final ArrayAdapter<String> adapter2=new ArrayAdapter<>(create_acc_busi.this,android.R.layout.simple_list_item_1,items2);
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
                        final ArrayAdapter<String> adapter3=new ArrayAdapter<String>(create_acc_busi.this,android.R.layout.simple_list_item_1,items3);
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
        con.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(!phone.getText().toString().isEmpty() && phone.getText().toString().length() == 10) {

                    user.put("name",name.getText().toString());
                    user.put("email",email.getText().toString());
                    user.put("password",password.getText().toString());
                    user.put("DOB",date1.getText().toString());
                    user.put("country",spinner1.getSelectedItem().toString());
                    user.put("state",spinner2.getSelectedItem().toString());
                    user.put("city",spinner3.getSelectedItem().toString());
                    user.put("street",name.getText().toString());
                    String phoneNum = "+91"+phone.getText().toString();
                    user.put("mobile",phoneNum);
                    Intent in= new Intent(create_acc_busi.this,otp.class);
                    in.putExtra("user", (Serializable)user);
                    startActivity(in);




                }
                else {
                    phone.setError("Valid Phone Required");
                }
            }
        });

    }

    private void datepicker()
    {
        date = (ImageButton) findViewById(R.id.datebutton);
        date1=(EditText) findViewById(R.id.date);
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
                datePickerDialog = new DatePickerDialog(create_acc_busi.this,
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
            for (int i = 0; i < jsonArray.length(); i++) {
                cList.add(jsonArray.getJSONObject(i).getString("name"));
            }
        }catch (IOException | JSONException e){
            e.printStackTrace();
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
            String json = new String(data, "UTF-8");
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