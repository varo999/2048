package com.example.a2048;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DataBaseManager {
    private DataBase dbHelper;
    private SQLiteDatabase database;

    public DataBaseManager(Context context) {
        dbHelper = new DataBase(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public long insertarUsuario(String nombre, String correo, String contrasena) {
        // Usamos el método de la clase DataBase para insertar el usuario
        return dbHelper.insertarUsuario(nombre, correo, contrasena);
    }

    public String obtenerContrasenaPorCorreo(String correo) {
        return dbHelper.obtenerContrasenaPorCorreo(correo);
    }

    // Método para guardar la puntuación usando DataBase
    public long registrarPuntuacion(int usuarioId, int juegoId, int puntos) {
        return dbHelper.guardarPuntuacion(usuarioId, juegoId, puntos);
    }

    // Llamar al método obtenerUsuarioIdPorCorreo desde DataBaseManager y devolver un int
    public int obtenerUsuarioId(String correo) {
        long usuarioIdLong = dbHelper.obtenerUsuarioIdPorCorreo(correo);

        // Verificar si el usuario existe antes de convertir
        if (usuarioIdLong == -1) {
            return -1; // Si no se encuentra el usuario, devolver -1
        }

        return (int) usuarioIdLong; // Convertir long a int
    }

    public void imprimirTodasLasTablas() {

    }

    public List<Map<String, Object>> obtenerTodasLasPuntuaciones() {
        return dbHelper.obtenerTodasLasPuntuaciones();
    }

}
