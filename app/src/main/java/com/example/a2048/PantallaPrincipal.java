package com.example.a2048;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class PantallaPrincipal extends AppCompatActivity {

    private boolean usuarioRegistrado = false; // Control de sesión
    private String correoUsuario = null; // Guardar el correo del usuario autenticado

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pantalla_principal);

        Button btnRegistro = findViewById(R.id.btn_registro);
        Button btnIniciarSesion = findViewById(R.id.btn_iniciar_sesion);
        Button btnmejoresPuntuaciones = findViewById(R.id.btn_mejores_puntuaciones);
        ImageView image1 = findViewById(R.id.image1);
        ImageView image2 = findViewById(R.id.image2);

        // Configurar el listener de clic para la imagen 1
        image1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (usuarioRegistrado) {
                    Intent intent = new Intent(PantallaPrincipal.this, Juego1Activity.class);
                    intent.putExtra("correoUsuario", correoUsuario);
                    startActivity(intent);
                } else {
                    Toast.makeText(PantallaPrincipal.this, "Debes iniciar sesión para jugar.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Configurar el listener de clic para la imagen 2
        image2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (usuarioRegistrado) {
                    Intent intent = new Intent(PantallaPrincipal.this, Juego2Activity.class);
                    intent.putExtra("correoUsuario", correoUsuario);
                    startActivity(intent);
                } else {
                    Toast.makeText(PantallaPrincipal.this, "Debes iniciar sesión para jugar.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnRegistro.setOnClickListener(v -> mostrarDialogoRegistro());
        btnIniciarSesion.setOnClickListener(v -> mostrarIniciarSesion());
        Button btnDesplegar = findViewById(R.id.btn_desplegar);
        btnDesplegar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleOptions(view); // Llamamos a la función toggleOptions
            }
        });
        btnmejoresPuntuaciones.setOnClickListener(v -> {
            Intent intent = new Intent(PantallaPrincipal.this, TableActivity.class);
            startActivity(intent);
        });
    }

    public void toggleOptions(View view) {
        LinearLayout llBotones = findViewById(R.id.ll_botones);
        // Cambiar la visibilidad del LinearLayout (mostrar u ocultar)
        if (llBotones.getVisibility() == View.GONE) {
            llBotones.setVisibility(View.VISIBLE);  // Mostrar los botones
        } else {
            llBotones.setVisibility(View.GONE);     // Ocultar los botones
        }
    }

    // Método para mostrar el cuadro de diálogo de registro
    private void mostrarDialogoRegistro() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Registro de Usuario");

        final EditText inputNombre = new EditText(this);
        inputNombre.setHint("Nombre");
        final EditText inputCorreo = new EditText(this);
        inputCorreo.setHint("Correo Electrónico");
        final EditText inputContrasena = new EditText(this);
        inputContrasena.setHint("Contraseña");
        inputContrasena.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);



        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(inputNombre);
        layout.addView(inputCorreo);
        layout.addView(inputContrasena);
        builder.setView(layout);

        builder.setPositiveButton("Registrar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String nombre = inputNombre.getText().toString();
                String correo = inputCorreo.getText().toString();
                String contrasena = inputContrasena.getText().toString();

                Context context = PantallaPrincipal.this;
                DataBaseManager dbManager = new DataBaseManager(context);

                long resultado = dbManager.insertarUsuario(nombre, correo, contrasena);

                if (resultado != -1) {
                    Toast.makeText(context, "Usuario registrado con éxito.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Error al registrar usuario.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }

    // Método para mostrar el cuadro de diálogo de inicio de sesión
    private void mostrarIniciarSesion() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Iniciar Sesión");

        final EditText inputCorreo = new EditText(this);
        inputCorreo.setHint("Correo Electrónico");
        final EditText inputContrasena = new EditText(this);
        inputContrasena.setHint("Contraseña");
        inputContrasena.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);



        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(inputCorreo);
        layout.addView(inputContrasena);
        builder.setView(layout);

        builder.setPositiveButton("Iniciar Sesión", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String correo = inputCorreo.getText().toString();
                String contrasenaIngresada = inputContrasena.getText().toString();

                Context context = PantallaPrincipal.this;
                DataBaseManager dbManager = new DataBaseManager(context);

                String contrasenaGuardada = dbManager.obtenerContrasenaPorCorreo(correo);

                if (contrasenaGuardada != null) {
                    if (contrasenaGuardada.equals(contrasenaIngresada)) {
                        usuarioRegistrado = true; // Actualizar estado
                        correoUsuario = correo;  // Guardar correo del usuario autenticado
                        Toast.makeText(context, "Inicio de sesión exitoso.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Contraseña incorrecta.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, "Usuario no encontrado.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());
        builder.show();
    }
}
