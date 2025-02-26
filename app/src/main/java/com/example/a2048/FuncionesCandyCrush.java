package com.example.a2048;
import android.animation.AnimatorSet;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.widget.GridLayout;
import java.util.Random;
import java.util.List;
import java.util.Arrays;
import android.widget.TextView;
import android.util.Log;
import java.util.ArrayList;
import android.view.View;
import android.graphics.Color;
import android.animation.ObjectAnimator;


public class FuncionesCandyCrush {

    public static void creaTablero(GridLayout gameBoard, Candy[][] board, TextView[][] cellViews) {
        // Lista de colores posibles para los caramelos
        List<String> colores = Arrays.asList("#FF0000", "#00FF00", "#0000FF", "#FFFF00");

        Log.d("CandyGame", "Iniciando la creación del tablero...");

        // Recorrer todas las filas y columnas del GridLayout
        for (int row = 0; row < gameBoard.getRowCount(); row++) {
            for (int col = 0; col < gameBoard.getColumnCount(); col++) {
                // Obtener el TextView en la posición (row, col) de la matriz de celdas
                TextView cell = (TextView) gameBoard.getChildAt(row * gameBoard.getColumnCount() + col);

                // Crear un objeto Candy con un color aleatorio
                Random rand = new Random();
                String randomColor = colores.get(rand.nextInt(colores.size()));  // Obtener un color aleatorio
                Candy candy = new Candy(randomColor, row, col);

                // Almacenar el objeto Candy en la matriz 'board'
                board[row][col] = candy;

                // Log para mostrar la creación del Candy
                Log.d("CandyGame", "Candy creado en [" + row + "][" + col + "] con color: " + randomColor);

                // Establecer el color en el TextView
                cell.setBackgroundColor(android.graphics.Color.parseColor(randomColor));  // Cambiar el color de fondo
                cell.setText("");  // Deja el TextView vacío o puedes poner información adicional si lo deseas

                // Almacenar el TextView en la matriz 'cellViews' en la posición correspondiente
                cellViews[row][col] = cell;
            }
        }

        Log.d("CandyGame", "Tablero creado con éxito.");
    }

    public static boolean sonCeldasAdyacentes(Candy[][] board, TextView[][] Celdas, int row1, int col1, int row2, int col2) {
        // Comprobar que la segunda celda está a una distancia de 1
        // en cualquier dirección (arriba, abajo, izquierda, derecha)

        // Verificar si la fila o columna de la primera celda y la segunda celda son adyacentes
        if ((Math.abs(row1 - row2) == 1 && col1 == col2) || (Math.abs(col1 - col2) == 1 && row1 == row2)) {
            return true; // Las celdas están a una distancia de 1 (adyacentes)
        } else {
            return false; // Las celdas no están adyacentes
        }
    }

    public static boolean sePuedeCombinar(Candy[][] board, TextView[][] Celdas, int row1, int col1) {
        // Verifica si las coordenadas están dentro del rango del tablero
        if (row1 < 0 || col1 < 0 || row1 >= board.length || col1 >= board[0].length) {
            return false;
        }

        Candy selectedCandy = board[row1][col1]; // Obtenemos el caramelo seleccionado
        String targetColor = selectedCandy.getColor(); // Obtenemos el color del caramelo seleccionado

        // Verificamos en las 4 direcciones: arriba, abajo, izquierda, derecha
        if (contarEnDireccion(board, targetColor, row1, col1, -1, 0) >= 2 ||  // Arriba
                contarEnDireccion(board, targetColor, row1, col1, 1, 0) >= 2 ||   // Abajo
                contarEnDireccion(board, targetColor, row1, col1, 0, -1) >= 2 ||  // Izquierda
                contarEnDireccion(board, targetColor, row1, col1, 0, 1) >= 2) {   // Derecha
            return true; // Si encontramos 3 o más caramelos en alguna dirección, se puede combinar
        }

        return false; // Si no se encontró ninguna combinación, no se puede combinar
    }

    private static int contarEnDireccion(Candy[][] board, String targetColor, int row, int col, int rowIncrement, int colIncrement) {
        int count = 1;  // Empezamos con 1 porque el caramelo original ya cuenta
        List<Candy> candiesToDestroy = new ArrayList<>();  // Lista para guardar los caramelos a destruir

        // Añadimos el caramelo actual a la lista de caramelos a destruir
        candiesToDestroy.add(board[row][col]);

        // Contamos en la dirección indicada por rowIncrement y colIncrement (dirección original)
        int currentRow = row + rowIncrement;
        int currentCol = col + colIncrement;

        // Contamos mientras estemos dentro de los límites del tablero
        while (currentRow >= 0 && currentRow < board.length && currentCol >= 0 && currentCol < board[0].length) {
            Candy candy = board[currentRow][currentCol];

            // Si el color del caramelo coincide con el color objetivo, lo contamos
            if (candy.getColor().equals(targetColor)) {
                count++;
                candiesToDestroy.add(candy);  // Agregar el caramelo a la lista
            } else {
                break;  // Si no coincide el color, dejamos de contar en esta dirección
            }

            // Avanzamos en la dirección indicada
            currentRow += rowIncrement;
            currentCol += colIncrement;
        }

        // Contamos también en la dirección contraria
        currentRow = row - rowIncrement;
        currentCol = col - colIncrement;

        // Contamos mientras estemos dentro de los límites del tablero
        while (currentRow >= 0 && currentRow < board.length && currentCol >= 0 && currentCol < board[0].length) {
            Candy candy = board[currentRow][currentCol];

            // Si el color del caramelo coincide con el color objetivo, lo contamos
            if (candy.getColor().equals(targetColor)) {
                count++;
                candiesToDestroy.add(candy);  // Agregar el caramelo a la lista
            } else {
                break;  // Si no coincide el color, dejamos de contar en esta dirección
            }

            // Avanzamos en la dirección contraria
            currentRow -= rowIncrement;
            currentCol -= colIncrement;
        }

        // Si se encontraron 3 o más caramelos del mismo color, se destruyen
        if (count >= 3) {
            for (Candy candy : candiesToDestroy) {
                candy.destroy();  // Marca cada caramelo como destruido
            }
        }

        return count;  // Retornamos el número total de caramelos encontrados en ambas direcciones
    }

    public static boolean actualizarTablero(Candy[][] board, TextView[][] Celdas) {
        boolean cambioRealizado = false;  // Variable para rastrear si hubo cambios

        // Recorrer el tablero de objetos (Candy) y el tablero de TextViews
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                // Obtener el caramelo en la posición [row][col]
                Candy candy = board[row][col];

                // Obtener el TextView en la misma posición
                TextView textView = Celdas[row][col];

                // Verificar si el caramelo está destruido
                if (candy != null && candy.isDestroyed()) {
                    // Si está destruido, ocultar el TextView correspondiente
                    if (textView.getVisibility() != TextView.GONE) {  // Solo marcar cambio si realmente se oculta
                        textView.setVisibility(TextView.GONE);  // Ocultar el TextView
                        cambioRealizado = true;  // Indicar que se realizó un cambio
                    }
                } else {
                    // Obtener el color del caramelo
                    String candyColor = candy.getColor();

                    // Cambiar el fondo del TextView con el color del caramelo
                    if (textView.getSolidColor() != Color.parseColor(candyColor)) {
                        textView.setBackgroundColor(Color.parseColor(candyColor)); // Cambiar el fondo del TextView
                        cambioRealizado = true;  // Indicar que se realizó un cambio
                    }

                    // Asegurarse de que el TextView esté visible si no está destruido
                    if (textView.getVisibility() != View.VISIBLE) {
                        textView.setVisibility(View.VISIBLE);  // Hacer visible el TextView si no lo estaba
                        cambioRealizado = true;  // Indicar que se realizó un cambio
                    }
                }
            }
        }

        // Devolver 'false' si no hubo cambios, 'true' si hubo cambios
        return cambioRealizado;  // Si no hubo cambios, se devuelve 'false'
    }

    public static boolean actualizarTableroConDestruccion(Candy[][] board, TextView[][] Celdas) {
        boolean accionRealizada = false;  // Variable para saber si se ha realizado alguna acción

        // Bucle while que sigue ejecutando hasta que no se realicen más cambios
        boolean cambiosRealizados;

        do {
            cambiosRealizados = false; // Restablecer el estado de los cambios en cada iteración

            // Recorrer todos los elementos de la cuadrícula (tablero)
            for (int row = 0; row < 8; row++) { // Empieza desde la primera fila (fila 0)
                for (int col = 0; col < 8; col++) { // Columna por columna
                    // Obtener el caramelo en la posición [row][col]
                    Candy candy = board[row][col];

                    // Obtener el TextView correspondiente
                    TextView textView = Celdas[row][col];

                    // Verificar si el caramelo está destruido
                    if (candy != null && candy.isDestroyed()) {
                        // Verificar si hay una celda de arriba para copiar el color
                        if (row > 0 && board[row - 1][col] != null && !board[row - 1][col].isDestroyed()) {
                            // Obtener el caramelo de la celda de arriba
                            Candy candyArriba = board[row - 1][col];

                            // Obtener el color del caramelo de arriba
                            final String colorDeArriba = candyArriba.getColor();

                            // Restablecer el estado del caramelo actual (no destruido)
                            candy.setColor(colorDeArriba);    // Establecer el nuevo color
                            candy.setDestroyed(false);        // Restablecer el estado a no destruido

                            // Hacer que el TextView de la celda de arriba sea invisible
                            TextView textViewArriba = Celdas[row - 1][col];
                            textViewArriba.setVisibility(View.INVISIBLE); // Hacer invisible la celda de arriba

                            // Destruir el caramelo de la celda de arriba
                            candyArriba.destroy();  // Destruir el caramelo de arriba

                            // Actualizar el TextView con el nuevo color inmediatamente
                            textView.setBackgroundColor(Color.parseColor(colorDeArriba));
                            textView.setVisibility(View.VISIBLE); // Asegurarse de que el TextView esté visible

                            // Marcar que se ha realizado una acción
                            cambiosRealizados = true; // Si se hace algo, se marca que hubo un cambio
                        }
                    }
                }
            }

            // Si no hubo cambios, el bucle se detendrá
        } while (cambiosRealizados); // El bucle continuará mientras se hagan cambios

        // Retornar el valor de la variable de acción
        return cambiosRealizados;  // Devolver 'true' si hubo cambios, 'false' si no hubo
    }

    public static boolean crearCaramelos(Candy[][] board, TextView[][] Celdas) {
        // Lista de colores posibles
        List<String> colores = Arrays.asList("#FF0000", "#00FF00", "#0000FF", "#FFFF00");

        // Instancia del generador de números aleatorios
        Random random = new Random();

        // Variable para rastrear si hubo algún cambio
        boolean huboCambio = false;

        // Recorrer todas las celdas del tablero
        for (int row = 0; row < board.length; row++) { // Filas del tablero
            for (int col = 0; col < board[row].length; col++) { // Columnas del tablero
                Candy candy = board[row][col]; // Obtenemos el objeto Candy
                TextView textView = Celdas[row][col]; // Obtenemos el TextView correspondiente

                // Comprobamos que tanto el candy como el textView no sean null
                if (candy != null && textView != null) {
                    // Verificar si el caramelo está destruido
                    if (candy.isDestroyed()) {
                        // Si está destruido, cambiar su estado a no destruido
                        candy.setDestroyed(false);

                        // Elegir un color aleatorio de la lista
                        String colorAleatorio = colores.get(random.nextInt(colores.size()));

                        // Establecer el nuevo color del caramelo
                        candy.setColor(colorAleatorio);

                        // Cambiar el color del TextView correspondiente
                        textView.setBackgroundColor(android.graphics.Color.parseColor(colorAleatorio));

                        // Asegurarse de que el TextView esté visible
                        textView.setVisibility(View.VISIBLE); // Cambiar el fondo con el color elegido

                        // Log para ver qué caramelo se está actualizando
                        Log.d("DEBUG", "Celdas actualizadas en [" + row + "][" + col + "] con color " + colorAleatorio);

                        // Marcar que hubo un cambio
                        huboCambio = true;
                    }
                } else {
                    // Si candy o textView es null, log para depurar
                    Log.d("DEBUG", "Advertencia: La celda en [" + row + "][" + col + "] tiene un valor null.");
                }
            }
        }

        // Devolver si hubo algún cambio en el tablero
        return huboCambio;
    }

    public static void verificarCombinaciones(Candy[][] board, TextView[][] Celdas, TextView[][] celdasOriginales) {
        // Número máximo de intentos
        int intentosMaximos = 10;

        for (int intento = 0; intento < intentosMaximos; intento++) {
            boolean cambiosRealizados = false;

            // Primero, verificamos todas las combinaciones posibles
            for (int row = 0; row < board.length; row++) {
                for (int col = 0; col < board[0].length; col++) {
                    // Verificamos si el caramelo en la celda actual se puede combinar
                    if (sePuedeCombinar(board, Celdas, row, col)) {
                        cambiosRealizados = true;
                    }
                }
            }

            // Si hubo combinaciones, actualizamos el tablero
            if (cambiosRealizados) {
                // Actualizamos el tablero para reflejar las combinaciones
                actualizarTablero(board, Celdas);
                actualizarTableroConDestruccion(board, Celdas);
                crearCaramelos(board, Celdas);
                actualizarTablero(board, Celdas);
            }
        }
    }

    public static void recrearTableroAnimacion(GridLayout gameBoard, GridLayout gameBoardAnimacion) {

        //gameBoard.setVisibility(View.INVISIBLE);

        // Limpiar el tablero de animación si ya existe
        gameBoardAnimacion.removeAllViews();

        // Obtener el número de filas y columnas del tablero original
        int filas = gameBoard.getRowCount();
        int columnas = gameBoard.getColumnCount();

        // Establecer las propiedades del tablero de animación
        gameBoardAnimacion.setColumnCount(columnas);
        gameBoardAnimacion.setRowCount(filas);

        // Recorrer cada celda del gameBoard y crear celdas en gameBoardAnimacion con el mismo color de fondo
        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                // Obtener la celda correspondiente del gameBoard original
                View celdaOriginal = gameBoard.getChildAt(i * columnas + j);

                // Crear una nueva celda para el tablero de animación
                TextView celdaAnimacion = new TextView(gameBoardAnimacion.getContext());
                celdaAnimacion.setId(View.generateViewId());
                celdaAnimacion.setGravity(Gravity.CENTER);

                // Crear los LayoutParams para que las celdas sean cuadradas
                GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams();
                layoutParams.rowSpec = GridLayout.spec(i, 1f);
                layoutParams.columnSpec = GridLayout.spec(j, 1f);
                layoutParams.setMargins(4, 4, 4, 4);  // Agregar márgenes entre celdas

                // Obtener el color de fondo de la celda original y asignarlo a la celda de animación
                if (celdaOriginal instanceof TextView) {
                    TextView celdaOriginalTextView = (TextView) celdaOriginal;
                    int colorFondo = ((ColorDrawable) celdaOriginalTextView.getBackground()).getColor();
                    celdaAnimacion.setBackgroundColor(colorFondo); // Copiar el color de fondo
                }

                // Agregar la celda al GridLayout de animación
                gameBoardAnimacion.addView(celdaAnimacion, layoutParams);
            }
        }

        // Después de terminar la animación, hacer visible el tablero principal nuevamente
        //gameBoard.setVisibility(View.VISIBLE);
    }

    public static void swapCells(GridLayout gameBoardAnimacion, int x1, int y1, int x2, int y2) {
        // Obtener las vistas en las posiciones dadas
        View cell1 = gameBoardAnimacion.getChildAt(y1 * gameBoardAnimacion.getColumnCount() + x1);
        View cell2 = gameBoardAnimacion.getChildAt(y2 * gameBoardAnimacion.getColumnCount() + x2);

        // Obtener las posiciones actuales de ambas celdas
        float[] cell1Pos = new float[]{cell1.getX(), cell1.getY()};
        float[] cell2Pos = new float[]{cell2.getX(), cell2.getY()};

        // Animación para mover cell1 a la posición de cell2
        ObjectAnimator animCell1 = ObjectAnimator.ofFloat(cell1, "translationX", cell2Pos[0] - cell1Pos[0]);
        animCell1.setDuration(300); // Duración de la animación

        // Animación para mover cell2 a la posición de cell1
        ObjectAnimator animCell2 = ObjectAnimator.ofFloat(cell2, "translationX", cell1Pos[0] - cell2Pos[0]);
        animCell2.setDuration(300); // Duración de la animación

        // Se pueden agregar animaciones verticales de manera similar
        ObjectAnimator animCell1Y = ObjectAnimator.ofFloat(cell1, "translationY", cell2Pos[1] - cell1Pos[1]);
        animCell1Y.setDuration(300);

        ObjectAnimator animCell2Y = ObjectAnimator.ofFloat(cell2, "translationY", cell1Pos[1] - cell2Pos[1]);
        animCell2Y.setDuration(300);

        // Ejecutar las animaciones de manera conjunta
        animCell1.start();
        animCell2.start();
        animCell1Y.start();
        animCell2Y.start();



    }

    public static void destroyCells(Candy[][] board, GridLayout gameBoardAnimacion) {
        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[row].length; col++) {
                Candy candy = board[row][col];

                if (candy != null && candy.isDestroyed()) {
                    // Obtener la celda en el tablero de animaciones
                    View celdaAnimacion = gameBoardAnimacion.getChildAt(row * board[row].length + col);

                    if (celdaAnimacion != null) {
                        // Crear una animación de "explosión" (desvanecimiento y escala)
                        AnimatorSet animatorSet = new AnimatorSet();

                        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(celdaAnimacion, "alpha", 1f, 0f);
                        fadeOut.setDuration(300); // Duración de la animación

                        ObjectAnimator scaleX = ObjectAnimator.ofFloat(celdaAnimacion, "scaleX", 1f, 1.5f, 0f);
                        ObjectAnimator scaleY = ObjectAnimator.ofFloat(celdaAnimacion, "scaleY", 1f, 1.5f, 0f);
                        scaleX.setDuration(300);
                        scaleY.setDuration(300);

                        animatorSet.playTogether(fadeOut, scaleX, scaleY);
                        animatorSet.start();
                    }
                }
            }
        }
    }

}







