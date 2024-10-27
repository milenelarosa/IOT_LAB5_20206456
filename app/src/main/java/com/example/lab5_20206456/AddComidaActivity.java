package com.example.lab5_20206456;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AddComidaActivity extends AppCompatActivity {

    private EditText etNombreComida;
    private EditText etCaloriasComida;
    private Button btnSave;
    private Button btnCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_comida);

        etNombreComida = findViewById(R.id.et_nombre_comida);
        etCaloriasComida = findViewById(R.id.et_calorias_comida);
        btnSave = findViewById(R.id.btn_save);
        btnCancel = findViewById(R.id.btn_cancel);

        btnSave.setOnClickListener(v -> {
            String nombreComida = etNombreComida.getText().toString().trim();
            String caloriasStr = etCaloriasComida.getText().toString().trim();

            if (nombreComida.isEmpty() || caloriasStr.isEmpty()) {
                Toast.makeText(AddComidaActivity.this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            int caloriasComida;
            try {
                caloriasComida = Integer.parseInt(caloriasStr);
            } catch (NumberFormatException e) {
                Toast.makeText(AddComidaActivity.this, "Ingrese un número válido para las calorías", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent resultIntent = new Intent();
            resultIntent.putExtra("nombreComida", nombreComida);
            resultIntent.putExtra("caloriasComida", caloriasComida);
            setResult(RESULT_OK, resultIntent);
            finish(); // Cerrar la actividad
        });

        btnCancel.setOnClickListener(v -> finish());
    }
}

