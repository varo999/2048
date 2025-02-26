package com.example.a2048;

public class Candy {
    private String color;  // Color del "candy"
    private int row;  // Fila en la cuadrícula
    private int col;  // Columna en la cuadrícula
    private boolean isDestroyed;
    // Constructor
    public Candy(String color, int row, int col) {
        this.color = color;
        this.row = row;
        this.col = col;
        this.isDestroyed = false;
    }

    // Métodos para obtener y modificar las propiedades
    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }
    public boolean isDestroyed() {
        return isDestroyed;
    }

    public void setDestroyed(boolean destroyed) {
        this.isDestroyed = destroyed;
    }

    public void destroy() {
        this.isDestroyed = true;
    }
}


