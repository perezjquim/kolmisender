package com.perezjquim.kolmisender;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Passa para a atividade principal (MainActivity)
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}