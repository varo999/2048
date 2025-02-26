package com.example.a2048;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;
import android.os.Handler;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class Juego2Activity extends AppCompatActivity {

    // Matrices para el tablero y la interfaz de usuario
    private Candy[][] board = new Candy[8][8]; // Matriz de objetos Candy
    private TextView[][] Celdas = new TextView[8][8]; // Matriz de celdas visuales
    private TextView[][] CeldasAnimacion = new TextView[8][8]; // Matriz para animaciones
    private TextView[][] celdasOriginales = new TextView[8][8]; // Copia original de las celdas

    // Variables para el control de selección de caramelos
    private Candy firstCandy = null; // Primer caramelo seleccionado
    private Candy secondCandy = null; // Segundo caramelo seleccionado
    private TextView firstCell = null; // Celda del primer caramelo
    private TextView secondCell = null; // Celda del segundo caramelo

    // Elementos de UI
    private TextView textView1; // Muestra la puntuación
    private TextView textView2; // Muestra los movimientos restantes
    private TextView plusTen; // Texto animado para sumar puntos

    // Controladores de puntuación y movimientos
    private Handler handler = new Handler(); // Manejo de animaciones y retrasos
    private int puntuacion; // Almacena la puntuación del jugador
    private int movimientos; // Almacena la cantidad de movimientos restantes
    private String correoUsuario; // Almacena el correo del usuario

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.juego2);

        // Recuperamos el correo del usuario desde el Intent
        Intent intent = getIntent();
        correoUsuario = intent.getStringExtra("correoUsuario");

        // Inicialización de los elementos de la interfaz
        textView1 = findViewById(R.id.textView1);
        textView2 = findViewById(R.id.textView2);
        plusTen = findViewById(R.id.plusTen);
        GridLayout gameBoard = findViewById(R.id.gameBoard);
        GridLayout gameBoardAnimacion = findViewById(R.id.gameBoardAnimaciones);

        // Inicialización del tablero y verificación de combinaciones
        FuncionesCandyCrush.creaTablero(gameBoard, board, Celdas);
        FuncionesCandyCrush.verificarCombinaciones(board, Celdas, celdasOriginales);
        FuncionesCandyCrush.recrearTableroAnimacion(gameBoard, gameBoardAnimacion);

        puntuacion = 0; // Se inicia la puntuación en 0
        movimientos = 25; // Se asignan 25 movimientos iniciales

        // Inicializar la matriz CeldasAnimacion
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                // Calcular el índice del TextView en el GridLayout de animaciones
                int index = row * 8 + col;
                CeldasAnimacion[row][col] = (TextView) gameBoardAnimacion.getChildAt(index);  // Asignar el TextView correspondiente
            }
        }

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                celdasOriginales[row][col] = Celdas[row][col];  // Guardar el TextView original
            }
        }

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                int index = row * 8 + col;  // Calcular el índice del TextView en el GridLayout
                Celdas[row][col] = (TextView) gameBoard.getChildAt(index);  // Obtener el TextView de la celda


                final int finalRow = row;
                final int finalCol = col;
                Celdas[row][col].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Obtener el objeto Candy de la celda seleccionada
                        Candy clickedCandy = board[finalRow][finalCol];  // Aquí tenemos el objeto Candy con las coordenadas

                        // Si no se ha seleccionado una celda previamente (primera selección)
                        if (firstCandy == null) {
                            firstCandy = clickedCandy;  // Guardamos el primer caramelo seleccionado
                            firstCell = (TextView) v;   // Guardamos la referencia a la primera celda seleccionada
                            // Muestra las coordenadas de la primera celda
                            Toast.makeText(Juego2Activity.this, "Seleccionaste: (" + firstCandy.getRow() + "," + firstCandy.getCol() + ")", Toast.LENGTH_SHORT).show();
                        } else if (secondCandy == null) {
                            // Si la primera celda ya ha sido seleccionada, guardamos el segundo caramelo
                            secondCandy = clickedCandy;  // Guardamos el segundo caramelo seleccionado
                            secondCell = (TextView) v;   // Guardamos la referencia a la segunda celda seleccionada
                            // Muestra las coordenadas de la segunda celda
                            Toast.makeText(Juego2Activity.this, "Seleccionaste: (" + secondCandy.getRow() + "," + secondCandy.getCol() + ")", Toast.LENGTH_SHORT).show();

                            // Verificar si las celdas seleccionadas son adyacentes
                            int row1 = firstCandy.getRow();
                            int col1 = firstCandy.getCol();
                            int row2 = secondCandy.getRow();
                            int col2 = secondCandy.getCol();

                            // Llamar a la función sonCeldasAdyacentes de FuncionesCandyCrush
                            if (!FuncionesCandyCrush.sonCeldasAdyacentes(board, Celdas, row1, col1, row2, col2)) {
                                // Si no son adyacentes, mostramos un mensaje y limpiamos las selecciones
                                Toast.makeText(Juego2Activity.this, "No son adyacentes", Toast.LENGTH_SHORT).show();
                                // Limpiar las selecciones
                                firstCandy = null;
                                secondCandy = null;
                                firstCell = null;
                                secondCell = null;

                            } else {


                                // Si son adyacentes, intercambiamos los colores de las celdas seleccionadas
                                String tempColor = board[row1][col1].getColor();
                                String color2 = board[row2][col2].getColor();

                                //creamos el tablero transiciones para la parte visual  y el cambio de celdas
                                FuncionesCandyCrush.recrearTableroAnimacion(gameBoard,gameBoardAnimacion);
                                FuncionesCandyCrush.swapCells(gameBoardAnimacion,col1, row1, col2, row2);
                                mostrarPuntos();
                                incrementarPuntuacion(10);

                                // Crear un Handler para hacer una pausa de 1 segundo
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        // Cambiar los colores de las celdas seleccionadas después de la pausa
                                        board[row1][col1].setColor(color2);
                                        board[row2][col2].setColor(tempColor);

                                        FuncionesCandyCrush.actualizarTablero(board, Celdas);

                                        // Verificar si al menos una de las dos celdas seleccionadas puede formar una combinación
                                        boolean firstCanCombine = FuncionesCandyCrush.sePuedeCombinar(board, Celdas, row1, col1);
                                        boolean secondCanCombine = FuncionesCandyCrush.sePuedeCombinar(board, Celdas, row2, col2);

                                        // Si alguna de las dos celdas puede combinarse, mantener el cambio
                                        if (firstCanCombine || secondCanCombine) {

                                            //recrear el tablero animacion(actualizado) y ejecutar la animacion
                                            FuncionesCandyCrush.recrearTableroAnimacion(gameBoard, gameBoardAnimacion);
                                            FuncionesCandyCrush.destroyCells(board, gameBoardAnimacion);

                                            // Hacer una pausa de 1 segundo en un hilo separado
                                            new Thread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    try {
                                                        Thread.sleep(600); // Pausa de 1 segundo (1000 milisegundos)
                                                    } catch (InterruptedException e) {
                                                        e.printStackTrace();
                                                    }

                                                    // Volver al hilo principal para actualizar la UI
                                                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            FuncionesCandyCrush.actualizarTableroConDestruccion(board, Celdas);
                                                            FuncionesCandyCrush.crearCaramelos(board, Celdas);
                                                            FuncionesCandyCrush.actualizarTablero(board, Celdas);
                                                            FuncionesCandyCrush.verificarCombinaciones(board, Celdas, celdasOriginales);

                                                            firstCandy = null;
                                                            secondCandy = null;
                                                            firstCell = null;
                                                            secondCell = null;
                                                            FuncionesCandyCrush.recrearTableroAnimacion(gameBoard, gameBoardAnimacion);
                                                        }
                                                    });
                                                }
                                            }).start();
                                            decrementarpuntuacion();
                                        } else {
                                            // Si ninguna de las dos celdas puede combinarse, revertir el cambio
                                            board[row1][col1].setColor(tempColor);
                                            board[row2][col2].setColor(color2);

                                            // Limpiar las selecciones
                                            firstCandy = null;
                                            secondCandy = null;
                                            firstCell = null;
                                            secondCell = null;

                                            FuncionesCandyCrush.actualizarTablero(board, Celdas);
                                            FuncionesCandyCrush.recrearTableroAnimacion(gameBoard, gameBoardAnimacion);

                                            // Mostrar un mensaje que indique que la combinación no es válida
                                            Toast.makeText(Juego2Activity.this, "No hay combinación válida", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }, 1000); // Pausa inicial de 1 segundo (1000 milisegundos)

                            }
                        }
                    }
                });
            }
        }
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

    // Método para incrementar la puntuación (por ejemplo, cuando el jugador hace algo)
    public void incrementarPuntuacion(int valor) {
        puntuacion += valor;  // Incrementa la puntuación por el valor dado
        actualizarPuntuacion();  // Actualiza el TextView con la nueva puntuación
    }

    // Método para actualizar el texto del TextView
    public void actualizarPuntuacion() {
        // Actualiza el texto del TextView con el valor de la variable puntuacion
        textView1.setText(String.valueOf(puntuacion + "Puntos "));
    }

    // Método para actualizar el texto del TextView
    public void decrementarpuntuacion() {
        movimientos -= 1;  // Decrementa la puntuación por 1
        // Actualiza el texto del TextView con el valor de la variable movimientos
        textView2.setText(String.valueOf(movimientos+ "Movimientos"));

        // Verifica si los movimientos han llegado a 0
        if (movimientos == 0) {
            // Llamar al método para mostrar el cuadro de diálogo
            mostrarFinDeJuego();
        }
    }

    // Método para mostrar el cuadro de diálogo cuando el juego termina
    public void mostrarFinDeJuego() {

        insertarPuntuacion();

        // Crear el cuadro de diálogo
        new AlertDialog.Builder(this)
                .setTitle("Fin de Juego")
                .setMessage("¡Se han acabado los movimientos! ¿Quieres salir?")
                .setCancelable(false) // No permitir que se cierre tocando fuera del cuadro de diálogo
                .setNegativeButton("Salir", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Mostrar un mensaje indicando que el juego ha terminado
                        Toast.makeText(getApplicationContext(), "Juego terminado", Toast.LENGTH_SHORT).show();

                        // Crear un Intent para ir a la actividad PantallaPrincipal
                        Intent intent = new Intent(Juego2Activity.this, PantallaPrincipal.class);

                        // Iniciar la actividad PantallaPrincipal
                        startActivity(intent);

                        // Finalizar la actividad actual para evitar que se regrese a ella
                        finish();
                    }
                })
                .show();
    }
    // Metodo para insertar Puntuacion
    private void insertarPuntuacion() {

        DataBaseManager dbManager = new DataBaseManager(this);
        dbManager.open();
        int usuarioId = dbManager.obtenerUsuarioId(correoUsuario);
        dbManager.registrarPuntuacion(usuarioId, 1, puntuacion);
        dbManager.close();
    }

}


