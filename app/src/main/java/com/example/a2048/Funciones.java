package com.example.a2048;
import java.util.HashMap;
import android.util.Log;
import android.widget.GridLayout;
import android.widget.TextView;
import java.util.Random;
import android.graphics.Color;
import android.view.Gravity;
import android.animation.ObjectAnimator;
import android.animation.AnimatorSet;
import android.animation.AnimatorListenerAdapter;
import android.view.View;
import android.animation.Animator;

public class Funciones {

    public static void crear2(GridLayout gameBoard) {
        Random random = new Random();
        int row, column;
        TextView cell;
        do {
            row = random.nextInt(4);
            column = random.nextInt(4);
            cell = (TextView) gameBoard.getChildAt(row * 4 + column);
        } while (!cell.getText().toString().isEmpty());
        cell.setText("2");
    }

    public static void moverColumnasDerecha(TextView[][] Celdas, TextView[][] CeldasTransicion) {
        boolean seMovioAlgunaCelda = true; // Bandera para controlar si se produjo algún movimiento

        // Repetir hasta que no haya más movimientos
        while (seMovioAlgunaCelda) {
            seMovioAlgunaCelda = false; // Reiniciar la bandera al inicio de cada iteración

            // Iterar sobre las filas
            for (int fila = 0; fila < 4; fila++) {  // Suponiendo que hay 4 filas
                // Primero mover las celdas vacías hacia la derecha
                for (int columna = 2; columna >= 0; columna--) {  // Iterar de derecha a izquierda (2, 1, 0)
                    // Mover las celdas hacia la derecha si hay espacio
                    for (int moverColumna = columna; moverColumna < 3; moverColumna++) {
                        String valorCelda = Celdas[fila][moverColumna].getText().toString();
                        String siguienteValor = Celdas[fila][moverColumna + 1].getText().toString();

                        // Si la celda no está vacía y la celda de la derecha está vacía
                        if (!valorCelda.isEmpty() && siguienteValor.isEmpty()) {
                            // Mover el valor a la derecha
                            Celdas[fila][moverColumna + 1].setText(valorCelda);
                            Celdas[fila][moverColumna].setText(""); // Vaciar la celda original

                            animateCellTransition1(CeldasTransicion[fila][moverColumna], CeldasTransicion[fila][moverColumna + 1]);
                            CeldasTransicion[fila][moverColumna + 1].setText(valorCelda);
                            CeldasTransicion[fila][moverColumna].setText("");

                            // Animar la celda de transición que se mueve



                            seMovioAlgunaCelda = true;  // Se ha movido una celda
                        }
                    }
                }

                // Después intentar combinar celdas adyacentes
                for (int columna = 3; columna > 0; columna--) {  // Iterar de derecha a izquierda (3, 2, 1)
                    String valorCelda1 = Celdas[fila][columna].getText().toString();
                    String valorCelda2 = Celdas[fila][columna - 1].getText().toString();

                    if (!valorCelda1.isEmpty() && valorCelda1.equals(valorCelda2)) {
                        // Si las celdas son iguales, combinarlas
                        int nuevoValor = Integer.parseInt(valorCelda1) * 2;  // Multiplicar el valor por 2
                        Celdas[fila][columna].setText(String.valueOf(nuevoValor));  // Actualizar la celda de la derecha
                        Celdas[fila][columna - 1].setText("");  // Vaciar la celda original

                        animateCellTransition(CeldasTransicion[fila][columna - 1], CeldasTransicion[fila][columna],nuevoValor);
                        // Actualizar la celda de transición
                        CeldasTransicion[fila][columna].setText(String.valueOf(nuevoValor));  // Actualizar la celda de la derecha
                        CeldasTransicion[fila][columna - 1].setText("");


                        seMovioAlgunaCelda = true;  // Se ha combinado una celda
                    }
                }
            }
        }
    }

    public static void moverColumnasIzquierda(TextView[][] Celdas, TextView[][] CeldasTransicion) {
        boolean seMovioAlgunaCelda = true; // Bandera para controlar si se produjo algún movimiento

        // Repetir hasta que no haya más movimientos
        while (seMovioAlgunaCelda) {
            seMovioAlgunaCelda = false; // Reiniciar la bandera al inicio de cada iteración

            // Iterar sobre las filas
            for (int fila = 0; fila < 4; fila++) {  // Suponiendo que hay 4 filas
                Log.d("MoverColumnasIzquierda", "Procesando fila " + fila); // Log para ver en qué fila estamos
                // Primero mover las celdas vacías hacia la izquierda
                for (int columna = 1; columna < 4; columna++) {  // Iterar de izquierda a derecha (1, 2, 3)
                    Log.d("MoverColumnasIzquierda", "Procesando columna " + columna); // Log para ver en qué columna estamos
                    // Mover las celdas hacia la izquierda si hay espacio
                    for (int moverColumna = columna; moverColumna > 0; moverColumna--) {
                        String valorCelda = Celdas[fila][moverColumna].getText().toString();
                        String siguienteValor = Celdas[fila][moverColumna - 1].getText().toString();
                        Log.d("MoverColumnasIzquierda", "Valor Celda: " + valorCelda + " | Siguiente Valor: " + siguienteValor); // Log para ver los valores
                        // Si la celda no está vacía y la celda de la izquierda está vacía
                        if (!valorCelda.isEmpty() && siguienteValor.isEmpty()) {
                            Log.d("MoverColumnasIzquierda", "Moviendo la celda hacia la izquierda: " + valorCelda); // Log para el movimiento
                            // Mover el valor a la izquierda
                            Celdas[fila][moverColumna - 1].setText(valorCelda);
                            Celdas[fila][moverColumna].setText("");  // Vaciar la celda original

                            // Animar la celda movida
                            animateCellTransition1(CeldasTransicion[fila][moverColumna], CeldasTransicion[fila][moverColumna - 1]);
                            // Actualizar la celda de transición
                            CeldasTransicion[fila][moverColumna - 1].setText(valorCelda);
                            CeldasTransicion[fila][moverColumna].setText("");

                            seMovioAlgunaCelda = true;  // Se ha movido una celda
                        }
                    }
                }

                // Después intentar combinar celdas adyacentes hacia la izquierda
                for (int columna = 0; columna < 3; columna++) {  // Iterar de izquierda a derecha (0, 1, 2)
                    String valorCelda1 = Celdas[fila][columna].getText().toString();
                    String valorCelda2 = Celdas[fila][columna + 1].getText().toString();

                    // Si las celdas son iguales y no están vacías
                    if (!valorCelda1.isEmpty() && valorCelda1.equals(valorCelda2)) {
                        // Si las celdas son iguales, combinarlas
                        int nuevoValor = Integer.parseInt(valorCelda1) * 2;  // Multiplicar el valor por 2
                        Celdas[fila][columna].setText(String.valueOf(nuevoValor));  // Actualizar la celda de la izquierda
                        Celdas[fila][columna + 1].setText("");  // Vaciar la celda de la derecha

                        // Animar la transición de la celda combinada
                        animateCellTransition(CeldasTransicion[fila][columna + 1], CeldasTransicion[fila][columna], nuevoValor);
                        // Actualizar la celda de transición
                        CeldasTransicion[fila][columna].setText(String.valueOf(nuevoValor));  // Actualizar la celda de la izquierda
                        CeldasTransicion[fila][columna + 1].setText("");

                        seMovioAlgunaCelda = true;  // Se ha combinado una celda
                    }
                }
            }
        }
    }

    public static void moverFilaArriba(TextView[][] Celdas, TextView[][] CeldasTransicion) {
        boolean seMovioAlgunaCelda = true; // Bandera para controlar si se produjo algún movimiento

        // Repetir hasta que no haya más movimientos
        while (seMovioAlgunaCelda) {
            seMovioAlgunaCelda = false; // Reiniciar la bandera al inicio de cada iteración

            // Iterar sobre las columnas (suponiendo que hay 4 columnas)
            for (int columna = 0; columna < 4; columna++) {  // Iterar sobre las columnas
                // Primero mover las celdas vacías hacia arriba
                for (int fila = 1; fila < 4; fila++) {  // Iterar de arriba a abajo (fila 1 a fila 3)
                    // Mover las celdas hacia arriba si hay espacio
                    for (int moverFila = fila; moverFila > 0; moverFila--) {
                        String valorCelda = Celdas[moverFila][columna].getText().toString();
                        String celdaArriba = Celdas[moverFila - 1][columna].getText().toString();

                        // Si la celda no está vacía y la celda de arriba está vacía
                        if (!valorCelda.isEmpty() && celdaArriba.isEmpty()) {
                            Celdas[moverFila - 1][columna].setText(valorCelda);  // Mover el valor hacia arriba
                            Celdas[moverFila][columna].setText("");  // Vaciar la celda original

                            // Animar la transición de las celdas
                            animateCellTransition1(CeldasTransicion[moverFila][columna], CeldasTransicion[moverFila - 1][columna]);

                            seMovioAlgunaCelda = true;  // Se ha movido una celda
                        }
                    }
                }

                // Después intentar combinar celdas adyacentes hacia arriba
                for (int fila = 0; fila < 3; fila++) {  // Iterar de arriba a abajo (fila 0 a fila 2)
                    String valorCelda1 = Celdas[fila][columna].getText().toString();
                    String valorCelda2 = Celdas[fila + 1][columna].getText().toString();

                    // Si las celdas son iguales y no están vacías
                    if (!valorCelda1.isEmpty() && valorCelda1.equals(valorCelda2)) {
                        // Si las celdas son iguales, combinarlas
                        int nuevoValor = Integer.parseInt(valorCelda1) * 2;  // Multiplicar el valor por 2
                        Celdas[fila][columna].setText(String.valueOf(nuevoValor));  // Actualizar la celda de arriba
                        Celdas[fila + 1][columna].setText("");  // Vaciar la celda de abajo

                        // Animar la transición de la celda combinada
                        animateCellTransition(CeldasTransicion[fila + 1][columna], CeldasTransicion[fila][columna], nuevoValor);

                        seMovioAlgunaCelda = true;  // Se ha combinado una celda
                    }
                }
            }
        }
    }

    public static void moverFilaAbajo(TextView[][] Celdas, TextView[][] CeldasTransicion) {
        boolean seMovioAlgunaCelda = true; // Bandera para controlar si se produjo algún movimiento

        // Repetir hasta que no haya más movimientos
        while (seMovioAlgunaCelda) {
            seMovioAlgunaCelda = false; // Reiniciar la bandera al inicio de cada iteración

            // Iterar sobre las columnas (suponiendo que hay 4 columnas)
            for (int columna = 0; columna < 4; columna++) {  // Iterar sobre las columnas
                // Primero mover las celdas vacías hacia abajo
                for (int fila = 2; fila >= 0; fila--) {  // Iterar de abajo hacia arriba (fila 2 a fila 0)
                    // Mover las celdas hacia abajo si hay espacio
                    for (int moverFila = fila; moverFila < 3; moverFila++) {
                        String valorCelda = Celdas[moverFila][columna].getText().toString();
                        String celdaAbajo = Celdas[moverFila + 1][columna].getText().toString();

                        // Si la celda no está vacía y la celda de abajo está vacía
                        if (!valorCelda.isEmpty() && celdaAbajo.isEmpty()) {
                            Celdas[moverFila + 1][columna].setText(valorCelda);  // Mover el valor hacia abajo
                            Celdas[moverFila][columna].setText("");  // Vaciar la celda original

                            // Animar la transición de las celdas
                            animateCellTransition1(CeldasTransicion[moverFila][columna], CeldasTransicion[moverFila + 1][columna]);

                            seMovioAlgunaCelda = true;  // Se ha movido una celda
                        }
                    }
                }

                // Después intentar combinar celdas adyacentes hacia abajo
                for (int fila = 3; fila > 0; fila--) {  // Iterar de abajo hacia arriba (fila 3 a fila 1)
                    String valorCelda1 = Celdas[fila][columna].getText().toString();
                    String valorCelda2 = Celdas[fila - 1][columna].getText().toString();

                    // Si las celdas son iguales y no están vacías
                    if (!valorCelda1.isEmpty() && valorCelda1.equals(valorCelda2)) {
                        // Si las celdas son iguales, combinarlas
                        int nuevoValor = Integer.parseInt(valorCelda1) * 2;  // Multiplicar el valor por 2
                        Celdas[fila][columna].setText(String.valueOf(nuevoValor));  // Actualizar la celda de abajo
                        Celdas[fila - 1][columna].setText("");  // Vaciar la celda de arriba

                        // Animar la transición de la celda combinada
                        animateCellTransition(CeldasTransicion[fila - 1][columna], CeldasTransicion[fila][columna], nuevoValor);

                        seMovioAlgunaCelda = true;  // Se ha combinado una celda
                    }
                }
            }
        }
    }

    public static void animateCellTransition(final TextView celdaDesde, final TextView celdaHacia, final int nuevoValor) {

        // Usar post() para asegurar que el layout esté completamente renderizado antes de animar
        celdaDesde.post(() -> {
            // Obtener las posiciones actuales de las celdas
            float desdeX = celdaDesde.getX();
            float desdeY = celdaDesde.getY();
            float haciaX = celdaHacia.getX();
            float haciaY = celdaHacia.getY();

            // Crear animaciones de movimiento usando ObjectAnimator
            ObjectAnimator animX = ObjectAnimator.ofFloat(celdaDesde, "translationX", haciaX - desdeX);
            ObjectAnimator animY = ObjectAnimator.ofFloat(celdaDesde, "translationY", haciaY - desdeY);

            // Crear animación de opacidad para hacer desaparecer la celda desde
            ObjectAnimator animAlphaDesde = ObjectAnimator.ofFloat(celdaDesde, "alpha", 1f, 0f); // Desaparece de opacidad 1 a 0

            // Crear animación de opacidad para hacer aparecer la celda hacia
            ObjectAnimator animAlphaHacia = ObjectAnimator.ofFloat(celdaHacia, "alpha", 0f, 1f); // Aparece de opacidad 0 a 1

            // Configurar duración de la animación
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(animX, animY, animAlphaDesde, animAlphaHacia);  // Las cuatro animaciones se ejecutan juntas
            animatorSet.setDuration(300); // Duración de la animación en milisegundos (puedes ajustar este valor)

            // Configurar el listener para limpiar y sincronizar las celdas al finalizar la animación
            animatorSet.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);

                    // Limpiar la posición de la celda desde para evitar acumulación de "translation"
                    celdaDesde.setTranslationX(0);
                    celdaDesde.setTranslationY(0);

                    // Después de la animación, actualizar el texto de la celda destino
                    celdaHacia.setText(String.valueOf(nuevoValor)); // Asigna el nuevo valor a la celda de destino
                    celdaDesde.setText(""); // Vaciar la celda de origen (opcional si quieres limpiar la celda)

                    // Establecer posición exacta final (seguridad extra)
                    celdaDesde.setX(haciaX);
                    celdaDesde.setY(haciaY);

                    // Hacer que la celda "desde" sea completamente invisible
                    celdaDesde.setAlpha(0f); // Esto hará que la celda "desde" sea completamente invisible
                    celdaDesde.setVisibility(View.INVISIBLE); // Eliminar la celda "desde" del layout

                    // Asegurar que la celda de destino sea visible y con el nuevo valor
                    celdaHacia.setAlpha(1f); // Hacer que la celda de destino sea completamente visible
                    celdaHacia.setVisibility(View.VISIBLE); // Hacer visible la celda "hacia"
                }
            });

            // Iniciar la animación
            animatorSet.start();
        });
    }

    public static void animateCellTransition1(final TextView celdaDesde, final TextView celdaHacia) {
        // Usar post() para asegurar que la vista esté completamente renderizada antes de animar
        celdaDesde.post(() -> {
            // Obtener las posiciones actuales de las celdas
            float desdeX = celdaDesde.getX();
            float desdeY = celdaDesde.getY();
            float haciaX = celdaHacia.getX();
            float haciaY = celdaHacia.getY();

            // Crear animaciones de movimiento (X e Y)
            ObjectAnimator animX = ObjectAnimator.ofFloat(celdaDesde, "translationX", haciaX - desdeX);
            ObjectAnimator animY = ObjectAnimator.ofFloat(celdaDesde, "translationY", haciaY - desdeY);

            // Crear animación de opacidad para hacer desaparecer la celda origen
            ObjectAnimator animAlphaDesde = ObjectAnimator.ofFloat(celdaDesde, "alpha", 1f, 0f);

            // Agrupar las animaciones en un set
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(animX, animY, animAlphaDesde);
            animatorSet.setDuration(300); // Duración de 300ms

            // Listener para manejar el final de la animación
            animatorSet.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);

                    // Obtener el valor de la celda origen
                    String valorDesde = celdaDesde.getText().toString();

                    // Transferir el valor a la celda destino
                    if (!valorDesde.isEmpty()) {
                        celdaHacia.setText(valorDesde);
                    }

                    // Asegurar que la celda destino es visible
                    celdaHacia.setAlpha(1f);
                    celdaHacia.setVisibility(View.VISIBLE);

                    // Resetear la celda origen
                    celdaDesde.setText("");
                    celdaDesde.setVisibility(View.INVISIBLE);
                    celdaDesde.setTranslationX(0); // Resetear posición X
                    celdaDesde.setTranslationY(0); // Resetear posición Y
                }
            });

            // Iniciar la animación
            animatorSet.start();
        });
    }

    public static void crearTableroTransicion(GridLayout container, GridLayout originalGridLayout, TextView[][] celdasTransicion) {
        // Limpiar el tablero de transición si ya existe
        container.removeAllViews();

        // Obtener el número de filas y columnas del tablero original
        int filas = originalGridLayout.getRowCount();
        int columnas = originalGridLayout.getColumnCount();

        // Establecer las propiedades del tablero de transición
        container.setColumnCount(columnas);
        container.setRowCount(filas);

        // Definir los colores según los valores (puedes personalizarlos)
        HashMap<Integer, String> colores = new HashMap<>();
        colores.put(2, "#FFEB3B");   // Amarillo
        colores.put(4, "#FFC107");   // Ámbar
        colores.put(8, "#FF9800");   // Naranja
        colores.put(16, "#FF5722");  // Rojo oscuro
        colores.put(32, "#F44336");  // Rojo
        colores.put(64, "#E91E63");  // Rosa
        colores.put(128, "#9C27B0"); // Púrpura
        colores.put(256, "#673AB7"); // Índigo
        colores.put(512, "#3F51B5"); // Azul
        colores.put(1024, "#2196F3");// Azul claro
        colores.put(2048, "#03A9F4");// Azul cian

        // Recorrer cada celda del originalGridLayout y agregarla al container (nuevo GridLayout)
        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                // Crear una nueva celda para el tablero de transición
                TextView celda = new TextView(container.getContext());
                celda.setId(View.generateViewId());
                celda.setGravity(Gravity.CENTER);
                celda.setTextSize(24);

                // Crear los LayoutParams para que las celdas sean cuadradas
                GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams();
                layoutParams.rowSpec = GridLayout.spec(i, 1f);
                layoutParams.columnSpec = GridLayout.spec(j, 1f);
                layoutParams.setMargins(4, 4, 4, 4);

                // Asegurarse de que la celda original no sea nula
                TextView originalCelda = (TextView) originalGridLayout.getChildAt(i * columnas + j);
                if (originalCelda == null) {
                    Log.e("Error", "Celda original nula en posición: " + i + "," + j);
                    continue;
                }

                int originalWidth = originalCelda.getWidth();
                int originalHeight = originalCelda.getHeight();

                // Si las dimensiones son 0, asignar un tamaño predeterminado
                if (originalWidth == 0 || originalHeight == 0) {
                    originalWidth = 100;  // Valor arbitrario si aún no se han calculado
                    originalHeight = 100;
                }

                layoutParams.width = originalWidth;
                layoutParams.height = originalHeight;

                // Agregar la celda al GridLayout de transición
                container.addView(celda, layoutParams);
                celdasTransicion[i][j] = celda;

                // Copiar el valor al nuevo GridLayout (container)
                String valor = originalCelda.getText().toString().trim(); // Trim para evitar espacios en blanco
                if (!valor.isEmpty() && !valor.equals("0")) {
                    celda.setText(valor);
                    try {
                        int numero = Integer.parseInt(valor);
                        String color = colores.getOrDefault(numero, "#B0BEC5"); // Color gris si el número no está en la lista
                        celda.setBackgroundColor(Color.parseColor(color));
                    } catch (NumberFormatException e) {
                        Log.e("Error", "Error al parsear el número: " + valor);
                    }
                } else {
                    celda.setText("");
                }
            }
        }
    }

}





