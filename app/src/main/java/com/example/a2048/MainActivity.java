package com.example.a2048;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);  // Layout con la imagen

        // Usamos un Handler para esperar 3 segundos y luego cambiar de pantalla
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Pasar a la siguiente activity (MainActivity)
                Intent intent = new Intent(MainActivity.this, PantallaPrincipal.class);
                startActivity(intent);
                finish();  // Cierra la SplashActivity para que no se quede en el back stack
            }
        }, 3000);  // 3000 milisegundos = 3 segundos
    }
}
