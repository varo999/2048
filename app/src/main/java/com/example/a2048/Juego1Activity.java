package com.example.a2048;
import com.example.a2048.Funciones;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.GridLayout;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.Stack;
import android.os.Handler;

public class Juego1Activity extends AppCompatActivity {

    private TextView plusTen;
    private GestureDetector gestureDetector;
    private TextView[][] Celdas;
    private LinearLayout rootView;
    private TextView[][] CeldasTransicion;
    private int puntuacion;  // Variable para almacenar la puntuación
    private TextView textView1;  // Esta es la variable global
    private TextView textView2;
    private Handler handler; // Declaramos el Handler aquí
    private Button botonAtras;
    private Stack<int[][]> historialMovimientos = new Stack<>();
    private String correoUsuario;// Stack para guardar el historial de movimientos
    private int movimientos;

    // Declarar el Runnable para verificar el estado
    private Runnable verificarEstadoRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.juego1);

        // Recuperamos el correoUsuario desde el Intent
        Intent intent = getIntent();
        correoUsuario = intent.getStringExtra("correoUsuario");

        // Inicializa el TextView donde se mostrará la puntuación
        textView1 = findViewById(R.id.textView1);
        textView2 = findViewById(R.id.textView2);

        puntuacion = 0;
        plusTen = findViewById(R.id.plusTen);
        botonAtras = findViewById(R.id.butonAtras);
        rootView = findViewById(R.id.root_view);
        GridLayout gameBoard = findViewById(R.id.gameBoard);
        GridLayout gameBoardTransicion = findViewById(R.id.gameBoardTransicion);
        Celdas = new TextView[4][4];
        CeldasTransicion = new TextView[4][4];
        movimientos = 0;

        // Inicializar las celdas del tablero original
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                int idCelda = getResources().getIdentifier("celda_" + i + "_" + j, "id", getPackageName());
                Celdas[i][j] = findViewById(idCelda);
            }
        }

        guardarEstadoTablero();
        // Llamar a la función para crear el tablero inicial
        Funciones.crear2(gameBoard);
        // Llamar a la función para crear el tablero de transición
        Funciones.crearTableroTransicion(gameBoardTransicion, gameBoard, CeldasTransicion);

        // Configurar GestureDetector para manejar gestos
        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                Log.d("Juego1Activity", "Detectado un movimiento de deslizamiento.");
                if (Math.abs(velocityX) > Math.abs(velocityY)) { // Si el movimiento es principalmente horizontal
                    if (velocityX > 0) {
                        Log.d("Juego1Activity", "Movimiento hacia la derecha detectado.");
                        guardarEstadoTablero();
                        // Llamar a las funciones que mueven las columnas
                        Funciones.moverColumnasDerecha(Celdas, CeldasTransicion);
                        // Actualizar el tablero

                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Funciones.crear2(gameBoard);
                                Funciones.crearTableroTransicion(gameBoardTransicion, gameBoard, CeldasTransicion);
                            }
                        }, 0);
                        mostrarPuntos();
                        incrementarPuntuacion(10);
                        mostrarPuntos();
                        incrementarPuntuacion(10);

                    } else {
                        Log.d("Juego1Activity", "Movimiento hacia la izquierda detectado.");
                        guardarEstadoTablero();
                        // Llamar a las funciones que mueven las columnas
                        Funciones.moverColumnasIzquierda(Celdas, CeldasTransicion);
                        // Actualizar el tablero


                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Funciones.crear2(gameBoard);
                                Funciones.crearTableroTransicion(gameBoardTransicion, gameBoard, CeldasTransicion);
                            }
                        }, 0);
                        mostrarPuntos();
                        incrementarPuntuacion(10);
                        mostrarPuntos();
                        incrementarPuntuacion(10);

                    }
                } else { // Si el movimiento es principalmente vertical
                    if (velocityY > 0) {
                        Log.d("Juego1Activity", "Movimiento hacia abajo detectado.");
                        guardarEstadoTablero();
                        // Llamar a las funciones que mueven las filas
                        Funciones.moverFilaAbajo(Celdas, CeldasTransicion);
                        // Actualizar el tablero

                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Funciones.crear2(gameBoard);
                                Funciones.crearTableroTransicion(gameBoardTransicion, gameBoard, CeldasTransicion);
                            }
                        }, 0);
                        mostrarPuntos();
                        incrementarPuntuacion(10);
                    } else {
                        Log.d("Juego1Activity", "Movimiento hacia arriba detectado.");
                        guardarEstadoTablero();
                        // Llamar a las funciones que mueven las filas
                        Funciones.moverFilaArriba(Celdas, CeldasTransicion);
                        // Actualizar el tablero
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Funciones.crear2(gameBoard);
                                Funciones.crearTableroTransicion(gameBoardTransicion, gameBoard, CeldasTransicion);
                            }
                        }, 0);
                        mostrarPuntos();
                        incrementarPuntuacion(10);
                        mostrarPuntos();
                        incrementarPuntuacion(10);
                    }
                }
                aumentarpuntuacion();
                return true; // Indica que el evento táctil ha sido manejado
            }
        });

        botonAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deshacerMovimiento();
                Funciones.crearTableroTransicion(gameBoardTransicion, gameBoard, CeldasTransicion);
            }
        });

        // Configura el OnTouchListener en la vista raíz (LinearLayout)
        rootView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d("Juego1Activity", "Detectado un toque en la pantalla.");
                gestureDetector.onTouchEvent(event);
                return true;
            }
        });

        // Inicializa el Handler con el Looper principal
        handler = new Handler(Looper.getMainLooper());

        // Crear y ejecutar el Runnable para verificar el estado del tablero cada segundo
        verificarEstadoRunnable = new Runnable() {
            @Override
            public void run() {
                verificarEstadoTablero();  // Verificar el estado del tablero
                handler.postDelayed(this, 1000);  // Repetir cada 1000 ms (1 segundo)
            }
        };

        // Iniciar la verificación periódica
        handler.post(verificarEstadoRunnable);
    }

    // Método para actualizar el texto del TextView
    public void actualizarPuntuacion() {
        // Actualiza el texto del TextView con el valor de la variable puntuacion
        textView1.setText(String.valueOf(puntuacion + "Puntos "));
    }

    // Método para incrementar la puntuación (por ejemplo, cuando el jugador hace algo)
    public void incrementarPuntuacion(int valor) {
        puntuacion += valor;  // Incrementa la puntuación por el valor dado
        actualizarPuntuacion();  // Actualiza el TextView con la nueva puntuación
    }

    public void mostrarPuntos() {
        plusTen.setVisibility(View.VISIBLE);

        // Ocultar después de 1 segundo utilizando el Handler correctamente
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                plusTen.setVisibility(View.GONE);
            }
        }, 1000);
    }
    // Método para guardar el estado del tablero
    public void guardarEstadoTablero() {
        int[][] estadoActual = new int[4][4];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                estadoActual[i][j] = Celdas[i][j].getText().toString().equals("") ? 0 : Integer.parseInt(Celdas[i][j].getText().toString());
            }
        }
        historialMovimientos.push(estadoActual);  // Guardar el estado en el historial
    }

    public void deshacerMovimiento() {
        if (!historialMovimientos.isEmpty()) {
            historialMovimientos.pop(); // Elimina el estado actual (porque ya se hizo un movimiento)

            if (!historialMovimientos.isEmpty()) {
                int[][] estadoAnterior = historialMovimientos.peek(); // Obtiene el estado anterior sin eliminarlo

                // Restaurar el tablero con los valores del estadoAnterior
                for (int i = 0; i < 4; i++) {
                    for (int j = 0; j < 4; j++) {
                        if (estadoAnterior[i][j] == 0) {
                            Celdas[i][j].setText(""); // Si es 0, la celda se muestra vacía
                        } else {
                            Celdas[i][j].setText(String.valueOf(estadoAnterior[i][j]));
                        }
                    }
                }
            }
        }
    }

    private void verificarEstadoTablero() {
        boolean hayEspacioVacio = false; // Variable para detectar si hay celdas vacías
        boolean haGanado = false; // Variable para detectar si el jugador ha ganado

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                String texto = Celdas[i][j].getText().toString();

                // Verifica si alguna celda tiene 2048
                if (!texto.isEmpty() && Integer.parseInt(texto) == 2048) {
                    haGanado = true;
                }

                // Verifica si hay celdas vacías
                if (texto.isEmpty()) {
                    hayEspacioVacio = true;
                }
            }
        }

        // Si hay una celda con 2048, mostrar mensaje de victoria
        if (haGanado) {
            mostrarMensajeFinal(true);
            detenerVerificacion();  // Detener la verificación después de la victoria
            return;
        }

        // Si no hay celdas vacías, se ha perdido
        if (!hayEspacioVacio) {
            mostrarMensajeFinal(false);
            detenerVerificacion();  // Detener la verificación después de la derrota
        }
    }

    private void detenerVerificacion() {
        handler.removeCallbacks(verificarEstadoRunnable);
    }

    private void mostrarMensajeFinal(boolean haGanado) {

        insertarPuntuacion();

        String mensaje = haGanado ? "¡Felicidades! Has alcanzado 2048 🎉" : "¡Game Over! No hay más movimientos disponibles 😢";

        // Crear y mostrar un AlertDialog con la puntuación final
        new AlertDialog.Builder(this)
                .setTitle("Fin del Juego")
                .setMessage(mensaje + "\nPuntuación final: " + puntuacion)
                .setNegativeButton("Salir", (dialog, which) -> {
                    // Crear un Intent para ir a la actividad PantallaPrincipal
                    Intent intent = new Intent(Juego1Activity.this, PantallaPrincipal.class);

                    // Iniciar la actividad PantallaPrincipal
                    startActivity(intent);

                    // Finalizar la actividad actual para evitar que se regrese a ella
                    finish();
                })
                .setCancelable(false)
                .show();
    }

    private void insertarPuntuacion() {

        DataBaseManager dbManager = new DataBaseManager(this);
        dbManager.open();
        int usuarioId = dbManager.obtenerUsuarioId(correoUsuario);
        dbManager.registrarPuntuacion(usuarioId, 1, puntuacion);
        dbManager.close();
    }

    // Método para actualizar el texto del TextView
    public void aumentarpuntuacion() {
        movimientos += 1;  // Decrementa la puntuación por 1
        // Actualiza el texto del TextView con el valor de la variable movimientos
        textView2.setText(String.valueOf(movimientos+ "Movimientos"));

    }
}

