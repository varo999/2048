package com.example.a2048;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataBase extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "juego2048.db";
    private static final int DATABASE_VERSION = 3; // Cambiar versión para que se actualice la BD

    public DataBase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Crear la tabla de usuarios
        String crearUsuarios = "CREATE TABLE usuarios (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nombre TEXT NOT NULL, " +
                "correoelectronico TEXT UNIQUE NOT NULL, " +
                "contrasena TEXT NOT NULL" +
                ");";

        // Crear la tabla de juegos
        String crearJuegos = "CREATE TABLE juegos (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nombre TEXT UNIQUE NOT NULL" +
                ");";

        // Crear la tabla de puntuaciones con relación a usuarios y juegos
        String crearPuntuacion = "CREATE TABLE puntuacion (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "puntos INTEGER NOT NULL, " +
                "usuario_id INTEGER NOT NULL, " +
                "juego_id INTEGER NOT NULL, " +
                "FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE, " +
                "FOREIGN KEY (juego_id) REFERENCES juegos(id) ON DELETE CASCADE" +
                ");";

        db.execSQL(crearUsuarios);
        db.execSQL(crearJuegos);
        db.execSQL(crearPuntuacion);

        // Insertar juegos iniciales específicos
        insertarJuegoSiNoExiste(db, "2048");
        insertarJuegoSiNoExiste(db, "Candy Crush");

        // Insertar usuarios de prueba
        insertarUsuarioDePrueba(db, "Carlos", "carlos@correo.com", "12345");
        insertarUsuarioDePrueba(db, "Ana", "ana@correo.com", "54321");

        // Insertar puntuaciones de prueba
        insertarPuntuacionDePrueba(db, 1, 1, 1500); // Usuario Carlos, Juego 2048, Puntuación 1500
        insertarPuntuacionDePrueba(db, 2, 2, 2000); // Usuario Ana, Juego Candy Crush, Puntuación 2000
        insertarPuntuacionDePrueba(db, 1, 2, 1800); // Usuario Carlos, Juego Candy Crush, Puntuación 1800
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Limpiar las tablas
        db.execSQL("DELETE FROM puntuacion");
        db.execSQL("DELETE FROM juegos");
        db.execSQL("DELETE FROM usuarios");

        // Reinsertar los datos iniciales
        insertarJuegoSiNoExiste(db, "2048");
        insertarJuegoSiNoExiste(db, "Candy Crush");

        insertarUsuarioDePrueba(db, "Carlos", "carlos@correo.com", "12345");
        insertarUsuarioDePrueba(db, "Ana", "ana@correo.com", "54321");

        insertarPuntuacionDePrueba(db, 1, 1, 1500); // Usuario Carlos, Juego 2048, Puntuación 1500
        insertarPuntuacionDePrueba(db, 2, 2, 2000); // Usuario Ana, Juego Candy Crush, Puntuación 2000
        insertarPuntuacionDePrueba(db, 1, 2, 1800); // Usuario Carlos, Juego Candy Crush, Puntuación 1800
    }

    private void insertarUsuarioDePrueba(SQLiteDatabase db, String nombre, String correo, String contrasena) {
        ContentValues valores = new ContentValues();
        valores.put("nombre", nombre);
        valores.put("correoelectronico", correo);
        valores.put("contrasena", contrasena);
        long id = db.insert("usuarios", null, valores);
        Log.d("DataBase", "Usuario insertado: " + nombre + ", ID: " + id);
    }

    private void insertarPuntuacionDePrueba(SQLiteDatabase db, int usuarioId, int juegoId, int puntos) {
        ContentValues valores = new ContentValues();
        valores.put("usuario_id", usuarioId);
        valores.put("juego_id", juegoId);
        valores.put("puntos", puntos);
        long id = db.insert("puntuacion", null, valores);
        Log.d("DataBase", "Puntuación insertada: Usuario ID = " + usuarioId + ", Juego ID = " + juegoId + ", Puntos = " + puntos + ", ID: " + id);
    }

    // Método para insertar un usuario
    public long insertarUsuario(String nombre, String correo, String contrasena) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put("nombre", nombre);
        valores.put("correoelectronico", correo);
        valores.put("contrasena", contrasena);
        return db.insert("usuarios", null, valores);
    }

    // Método para obtener la contraseña por correo
    public String obtenerContrasenaPorCorreo(String correo) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        String contrasena = null;

        try {
            String[] columnas = {"contrasena"};
            String selection = "correoelectronico = ?";
            String[] selectionArgs = {correo};

            cursor = db.query("usuarios", columnas, selection, selectionArgs, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                int index = cursor.getColumnIndex("contrasena");
                if (index >= 0) {
                    contrasena = cursor.getString(index);
                } else {
                    Log.e("DataBase", "Columna 'contrasena' no encontrada.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }

        return contrasena;
    }

    // Método para insertar un juego si no existe ya en la base de datos
    private void insertarJuegoSiNoExiste(SQLiteDatabase db, String nombreJuego) {
        // Verificar si el juego ya existe
        Cursor cursor = db.query("juegos", new String[]{"id"}, "nombre = ?", new String[]{nombreJuego}, null, null, null);

        if (cursor.getCount() == 0) {
            // Si el juego no existe, insertarlo
            ContentValues valores = new ContentValues();
            valores.put("nombre", nombreJuego);
            db.insert("juegos", null, valores);
            Log.d("DataBase", "Juego insertado: " + nombreJuego);
        } else {
            Log.d("DataBase", "El juego ya existe: " + nombreJuego);
        }

        cursor.close();
    }

    // Método para guardar la puntuación de un usuario en un juego (recibiendo solo ID de usuario)
    public long guardarPuntuacion(int usuarioId, int juegoId, int puntos) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Validar que el usuarioId sea válido
        if (usuarioId <= 0) {
            Log.e("DataBase", "ID de usuario inválido: " + usuarioId);
            return -1; // Retorna -1 si el ID del usuario no es válido
        }

        ContentValues valores = new ContentValues();
        valores.put("usuario_id", usuarioId);
        valores.put("juego_id", juegoId);
        valores.put("puntos", puntos);

        long resultado = db.insert("puntuacion", null, valores);
        db.close(); // Cerrar la base de datos
        return resultado;
    }

    // Método auxiliar para obtener el usuario_id basado en correo
    public long obtenerUsuarioIdPorCorreo(String correo) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        long usuarioId = -1;

        try {
            String[] columnas = {"id"};
            String selection = "correoelectronico = ?";
            String[] selectionArgs = {correo};

            cursor = db.query("usuarios", columnas, selection, selectionArgs, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                // Asegurarnos de que la columna existe
                int columnIndex = cursor.getColumnIndex("id");
                if (columnIndex >= 0) {
                    usuarioId = cursor.getLong(columnIndex);  // Obtener el valor de la columna "id"
                } else {
                    Log.e("DataBase", "Columna 'id' no encontrada en la tabla 'usuarios'.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }

        return usuarioId;
    }

    public List<Map<String, Object>> obtenerTodasLasPuntuaciones() {
        List<Map<String, Object>> resultados = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            // Realizamos el JOIN solo con la tabla de usuarios para obtener el nombre
            String query = "SELECT puntuacion.puntos, usuarios.nombre AS usuario_nombre, puntuacion.juego_id " +
                    "FROM puntuacion " +
                    "INNER JOIN usuarios ON puntuacion.usuario_id = usuarios.id";

            cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                // Iterar por cada registro obtenido
                do {
                    Map<String, Object> registro = new HashMap<>();
                    // Obtener los nombres de las columnas
                    String[] columnas = cursor.getColumnNames();
                    for (String columna : columnas) {
                        int index = cursor.getColumnIndex(columna);
                        // Detectar el tipo de dato y asignar el valor correspondiente
                        switch (cursor.getType(index)) {
                            case Cursor.FIELD_TYPE_INTEGER:
                                registro.put(columna, cursor.getInt(index));
                                break;
                            case Cursor.FIELD_TYPE_FLOAT:
                                registro.put(columna, cursor.getFloat(index));
                                break;
                            case Cursor.FIELD_TYPE_STRING:
                                registro.put(columna, cursor.getString(index));
                                break;
                            case Cursor.FIELD_TYPE_BLOB:
                                registro.put(columna, cursor.getBlob(index));
                                break;
                            case Cursor.FIELD_TYPE_NULL:
                            default:
                                registro.put(columna, null);
                                break;
                        }
                    }
                    resultados.add(registro);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
        return resultados;
    }


}
