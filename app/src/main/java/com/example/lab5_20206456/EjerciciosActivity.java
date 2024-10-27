package com.example.lab5_20206456;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

public class EjerciciosActivity extends AppCompatActivity {

    private TextView tvCaloriasQuemadas;
    private Button btnAddEjercicio;
    private LinearLayout llEjercicios;
    private int totalCaloriasQuemadas = 0;

    private static final String SHARED_PREFS_NAME = "UserPreferences";

    private static final String FILE_CALORIAS_QUEMADAS = "calorias_quemadas.txt";
    private static final String FILE_EJERCICIOS = "ejercicios.txt";
    private Set<String> ejerciciosSeleccionados = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ejercicios);

        tvCaloriasQuemadas = findViewById(R.id.tv_calorias_quemadas);
        btnAddEjercicio = findViewById(R.id.btn_add_ejercicio);
        llEjercicios = findViewById(R.id.ll_ejercicios);

        cargarCaloriasQuemadas();
        cargarEjerciciosSeleccionados();
        actualizarCaloriasTextView();

        btnAddEjercicio.setOnClickListener(v -> {
            Intent intent = new Intent(EjerciciosActivity.this, AddEjercicioActivity.class);
            startActivityForResult(intent, 1);
        });

        ImageView backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> finish());
    }

    private void actualizarCaloriasTextView() {
        tvCaloriasQuemadas.setText(totalCaloriasQuemadas + " kcal quemadas");
    }

    private void guardarCaloriasQuemadas() {
        try (FileOutputStream fos = openFileOutput(FILE_CALORIAS_QUEMADAS, Context.MODE_PRIVATE)) {
            fos.write(String.valueOf(totalCaloriasQuemadas).getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Guardar totalCalorias en SharedPreferences para que InicioFragment pueda acceder
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("totalCaloriasQuemadas", totalCaloriasQuemadas);
        editor.apply();
    }

    private void agregarCalorias(int calorias) {
        totalCaloriasQuemadas += calorias;
        actualizarCaloriasTextView();
        guardarCaloriasQuemadas();
    }

    private void reducirCalorias(int calorias) {
        totalCaloriasQuemadas -= calorias;
        actualizarCaloriasTextView();
        guardarCaloriasQuemadas();
    }

    private void cargarCaloriasQuemadas() {
        try (FileInputStream fis = openFileInput(FILE_CALORIAS_QUEMADAS);
             BufferedReader reader = new BufferedReader(new InputStreamReader(fis))) {
            totalCaloriasQuemadas = Integer.parseInt(reader.readLine());
        } catch (Exception e) {
            totalCaloriasQuemadas = 0;
        }
    }

    private void guardarEjerciciosSeleccionados() {
        try (FileOutputStream fos = openFileOutput(FILE_EJERCICIOS, Context.MODE_PRIVATE)) {
            for (String ejercicio : ejerciciosSeleccionados) {
                fos.write((ejercicio + "\n").getBytes());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void cargarEjerciciosSeleccionados() {
        try (FileInputStream fis = openFileInput(FILE_EJERCICIOS);
             BufferedReader reader = new BufferedReader(new InputStreamReader(fis))) {
            String ejercicio;
            while ((ejercicio = reader.readLine()) != null) {
                ejerciciosSeleccionados.add(ejercicio);
                // Divide el string en nombre, calorías y tiempo
                String[] data = ejercicio.split(":");
                if (data.length == 3) {
                    agregarEjercicio(data[0], Integer.parseInt(data[1]), Integer.parseInt(data[2]), false);
                }
            }
        } catch (Exception e) {
            ejerciciosSeleccionados = new HashSet<>();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            String nombreEjercicio = data.getStringExtra("nombreActividad");
            int caloriasQuemadas = data.getIntExtra("caloriasActividad", 0);
            int tiempoEjercicio = data.getIntExtra("timeActividad", 0);
            String ejercicioGuardado = nombreEjercicio + ":" + caloriasQuemadas + ":" + tiempoEjercicio;
            if (!ejerciciosSeleccionados.contains(ejercicioGuardado)) {
                ejerciciosSeleccionados.add(ejercicioGuardado);
                guardarEjerciciosSeleccionados();
                agregarEjercicio(nombreEjercicio, caloriasQuemadas, tiempoEjercicio, true);
            }
        }
    }

    private void agregarEjercicio(String nombre, int calorias, int tiempo, boolean contarCalorias) {
        View item = LayoutInflater.from(this).inflate(R.layout.item_ejercicio, llEjercicios, false);
        TextView tvNombre = item.findViewById(R.id.tv_ejercicio);
        TextView tvCalorias = item.findViewById(R.id.tv_calorias);
        TextView tvTime = item.findViewById(R.id.tv_tiempo);
        ImageView btnEliminar = item.findViewById(R.id.btn_eliminar_ejercicio);

        tvNombre.setText(nombre);
        tvTime.setText(tiempo + " minutos");
        tvCalorias.setText(calorias + " kcal");

        btnEliminar.setOnClickListener(v -> {
            llEjercicios.removeView(item);
            reducirCalorias(calorias);
            ejerciciosSeleccionados.remove(nombre + ":" + calorias + ":" + tiempo);
            guardarEjerciciosSeleccionados();
        });

        llEjercicios.addView(item);
        if (contarCalorias) agregarCalorias(calorias);
    }
}

