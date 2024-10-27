package com.example.lab5_20206456;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AddEjercicioActivity extends AppCompatActivity {

    private EditText etNombreEjercicio;
    private EditText etCaloriasEjercicio;
    private EditText etTiempoEjercicio;
    private Button btnSave;
    private Button btnCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_actividad);

        // Referencias a los elementos del layout
        etNombreEjercicio = findViewById(R.id.et_nombre_actividad);
        etTiempoEjercicio = findViewById(R.id.et_tiempo_actividad);
        etCaloriasEjercicio = findViewById(R.id.et_calorias_actividad);
        btnSave = findViewById(R.id.btn_save);
        btnCancel = findViewById(R.id.btn_cancel);

        // Configuración del botón "Guardar"
        btnSave.setOnClickListener(v -> {
            String nombreActividad = etNombreEjercicio.getText().toString().trim();
            String caloriasStr = etCaloriasEjercicio.getText().toString().trim();
            String timeStr = etTiempoEjercicio.getText().toString().trim();

            // Validación: asegurar que los campos no estén vacíos
            if (nombreActividad.isEmpty() || caloriasStr.isEmpty() || timeStr.isEmpty()) {
                Toast.makeText(AddEjercicioActivity.this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            int caloriasActividad, tiempoActividad;
            try {
                caloriasActividad = Integer.parseInt(caloriasStr);
                tiempoActividad = Integer.parseInt(timeStr);
            } catch (NumberFormatException e) {
                Toast.makeText(AddEjercicioActivity.this, "Ingrese un número válido para las calorías y tiempo", Toast.LENGTH_SHORT).show();
                return;
            }

            // Enviar los datos de la nueva comida de vuelta a InicioFragment
            Intent resultIntent = new Intent();
            resultIntent.putExtra("nombreActividad", nombreActividad);
            resultIntent.putExtra("caloriasActividad", caloriasActividad);
            resultIntent.putExtra("timeActividad", tiempoActividad);
            setResult(RESULT_OK, resultIntent);
            finish(); // Cerrar la actividad
        });

        // Configuración del botón "Cancelar"
        btnCancel.setOnClickListener(v -> finish());
    }

}
