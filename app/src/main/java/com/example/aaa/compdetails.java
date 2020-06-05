package com.example.aaa;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class compdetails extends AppCompatActivity {
Button con;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compdetails);
        con=(Button) findViewById(R.id.continue1);
        con.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(compdetails.this , addsupplier.class);
                startActivity(in);
            }
        });
    }
}
