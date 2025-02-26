package com.example.a2048;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class TableActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.table_view);

        // Instanciar base de datos
        DataBaseManager dbManager = new DataBaseManager(this);

        // Obtener todas las puntuaciones con SELECT *
        List<Map<String, Object>> todasLasPuntuaciones = dbManager.obtenerTodasLasPuntuaciones();

        // Ordenar las puntuaciones en orden descendente por puntos
        Collections.sort(todasLasPuntuaciones, new Comparator<Map<String, Object>>() {
            @Override
            public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                int puntos1 = Integer.parseInt(String.valueOf(o1.get("puntos")));
                int puntos2 = Integer.parseInt(String.valueOf(o2.get("puntos")));
                return Integer.compare(puntos2, puntos1); // Orden descendente
            }
        });

        // Limitar a los primeros 10 resultados
        if (todasLasPuntuaciones.size() > 10) {
            todasLasPuntuaciones = todasLasPuntuaciones.subList(0, 10);
        }

        // Referenciar el TableLayout del layout XML
        TableLayout tableLayout = findViewById(R.id.tableLayout);

        // Agregar color de fondo a la tabla
        tableLayout.setBackgroundColor(Color.parseColor("#f5f5f5"));

        // Crear y agregar la fila de encabezados (sin la columna "ID")
        TableRow headerRow = new TableRow(this);
        headerRow.setBackgroundColor(Color.parseColor("#4CAF50")); // Color verde para el encabezado
        agregarTextView(headerRow, "Usuario", true, Color.WHITE); // Mostrar solo "Usuario"
        agregarTextView(headerRow, "Juego", true, Color.WHITE); // Cambié a "Juego" en lugar de "Juego ID"
        agregarTextView(headerRow, "Puntos", true, Color.WHITE); // Mostrar "Puntos"
        tableLayout.addView(headerRow);

        // Iterar sobre los datos y agregarlos a la tabla (sin la columna "ID")
        for (Map<String, Object> puntuacion : todasLasPuntuaciones) {
            TableRow row = new TableRow(this);
            row.setBackgroundColor(Color.parseColor("#ffffff")); // Color de fondo blanco para las filas
            row.setPadding(0, 1, 0, 1); // Añadir un pequeño espacio entre las filas

            String usuarioNombre = String.valueOf(puntuacion.get("usuario_nombre")); // Obtener el nombre del usuario
            int juegoID = (int) puntuacion.get("juego_id"); // Obtener el ID del juego como entero

            // Asignar el nombre del juego basado en si el ID es par o impar
            String juegoNombre = (juegoID % 2 == 0) ? "CandyCrush" : "2048";  // Si ID es par, asigna CandyCrush, si es impar, asigna 2048

            String puntos = String.valueOf(puntuacion.get("puntos"));

            agregarTextView(row, usuarioNombre, false, Color.BLACK); // Mostrar el nombre del usuario
            agregarTextView(row, juegoNombre, false, Color.BLACK); // Mostrar el nombre del juego
            agregarTextView(row, puntos, false, Color.BLACK); // Mostrar los puntos

            // Agregar borde a las celdas para crear una cuadrícula
            row.setBackgroundColor(Color.parseColor("#DDDDDD"));

            tableLayout.addView(row);
        }
    }

    private void agregarTextView(TableRow row, String texto, boolean esEncabezado, int colorTexto) {
        TextView textView = new TextView(this);
        textView.setText(texto);
        textView.setPadding(20, 20, 20, 20);  // Agregar un poco de espacio dentro de las celdas
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(16);
        textView.setTextColor(colorTexto);

        // Borde de las celdas para crear una cuadrícula
        textView.setBackgroundResource(R.drawable.cell_border);  // Este es un borde que debes crear

        if (esEncabezado) {
            textView.setTypeface(null, Typeface.BOLD);
        }

        row.addView(textView);
    }
}

