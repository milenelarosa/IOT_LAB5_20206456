package com.example.lab5_20206456;

import android.Manifest;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ComidasActivity extends AppCompatActivity {
    private TextView tvCaloriasConsumidas;
    private Button btnAddComida;
    private LinearLayout llComidas;
    private LinearLayout llComidasPersonalizadas;
    private int totalCalorias = 0;
    private int caloriasRequeridas = 350;
    private NotificationManager notificationManager;

    private static final String SHARED_PREFS_NAME = "UserPreferences";

    private static final String FILE_CALORIAS = "calorias.txt";
    private static final String FILE_COMIDAS = "comidas.txt";
    private static final int NOTIF_ID_EXCESO_CALORIAS = 1;
    private static final int NOTIF_ID_RECORDATORIO = 2;
    private Set<String> comidasSeleccionadas = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comidas);

        tvCaloriasConsumidas = findViewById(R.id.tv_calorias_consumidas);
        btnAddComida = findViewById(R.id.btn_add_comida);
        llComidas = findViewById(R.id.ll_comidas);
        llComidasPersonalizadas = findViewById(R.id.ll_comidas_personalizadas);

        // Obtener calorías requeridas desde SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        caloriasRequeridas = sharedPreferences.getInt("caloriasRequeridas", 0);

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Verificar y solicitar permiso para notificaciones en Android
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 100);
            }
        }

        crearCanalesDeNotificacion();

        cargarCalorias();
        cargarComidasSeleccionadas();
        actualizarCaloriasTextView();
        cargarComidasPersonalizadas();

        btnAddComida.setOnClickListener(v -> {
            Intent intent = new Intent(ComidasActivity.this, AddComidaActivity.class);
            startActivityForResult(intent, 1);
        });

        ImageView backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> finish());

        configurarRecordatorioComida(8, 00, 100, "Hora de registrar tu desayuno!");
        configurarRecordatorioComida(13, 00, 101, "Hora de registrar tu almuerzo!");
        configurarRecordatorioComida(19, 00, 102, "Hora de registrar tu cena!");
        configurarRecordatorioDiario();
    }

    private void crearCanalesDeNotificacion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channelExceso = new NotificationChannel(
                    "CANAL_EXCESO_CALORIAS", "Exceso Calorías", NotificationManager.IMPORTANCE_HIGH);
            NotificationChannel channelRecordatorio = new NotificationChannel(
                    "CANAL_RECORDATORIO", "Recordatorio Comidas", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channelExceso);
            notificationManager.createNotificationChannel(channelRecordatorio);
        }
    }


    private void configurarRecordatorioComida(int hour, int minute, int requestCode, String mensaje) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, RecordatorioReceiver.class);
        intent.putExtra("mensajeRecordatorio", mensaje);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this, requestCode, intent, PendingIntent.FLAG_IMMUTABLE
        );

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        if (calendar.before(Calendar.getInstance())) {
            calendar.add(Calendar.DATE, 1);
        }

        alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY,
                pendingIntent
        );
    }

    private void configurarRecordatorioDiario() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, RecordatorioReceiver.class);
        intent.putExtra("mensajeRecordatorio", "No has registrado ninguna comida hoy. ¡Recuerda llevar un registro de tus comidas!");

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this, NOTIF_ID_RECORDATORIO, intent, PendingIntent.FLAG_IMMUTABLE
        );

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 21);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY,
                pendingIntent
        );
    }


    private void actualizarCaloriasTextView() {
        tvCaloriasConsumidas.setText(totalCalorias + " / " + caloriasRequeridas + " kcal");
        if (totalCalorias > caloriasRequeridas) {
            tvCaloriasConsumidas.setTextColor(Color.RED);
            mostrarAlertaExcesoCalorias();
        } else {
            tvCaloriasConsumidas.setTextColor(Color.BLACK);
        }
    }

    private void cargarComidasPersonalizadas() {
        String[] comidas = {"Manzana", "Plátano", "Pollo", "Arroz", "Huevos"};
        int[] calorias = {52, 89, 239, 130, 68};

        for (int i = 0; i < comidas.length; i++) {
            String comida = comidas[i];
            int caloria = calorias[i];
            View item = LayoutInflater.from(this).inflate(R.layout.item_comida_catalogo, llComidasPersonalizadas, false);
            TextView tvComida = item.findViewById(R.id.tv_comida);
            TextView tvCalorias = item.findViewById(R.id.tv_calorias);
            CheckBox cbAgregar = item.findViewById(R.id.cb_agregar);

            tvComida.setText(comida);
            tvCalorias.setText(caloria + " kcal");

            cbAgregar.setChecked(comidasSeleccionadas.contains(comida + ":" + caloria));
            cbAgregar.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    agregarCalorias(caloria);
                    comidasSeleccionadas.add(comida + ":" + caloria);
                } else {
                    reducirCalorias(caloria);
                    comidasSeleccionadas.remove(comida + ":" + caloria);
                }
                guardarComidasSeleccionadas();
            });
            llComidasPersonalizadas.addView(item);
        }
    }

    private void agregarCalorias(int calorias) {
        totalCalorias += calorias;
        actualizarCaloriasTextView();
        guardarCalorias();
    }

    private void reducirCalorias(int calorias) {
        totalCalorias -= calorias;
        actualizarCaloriasTextView();
        guardarCalorias();
    }

    private void guardarCalorias() {
        // Guardar totalCalorias en archivo
        try (FileOutputStream fos = openFileOutput(FILE_CALORIAS, Context.MODE_PRIVATE)) {
            fos.write(String.valueOf(totalCalorias).getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Guardar totalCalorias en SharedPreferences para que InicioFragment pueda acceder
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("totalCalorias", totalCalorias);
        editor.apply();
    }

    private void cargarCalorias() {
        try (FileInputStream fis = openFileInput(FILE_CALORIAS);
             BufferedReader reader = new BufferedReader(new InputStreamReader(fis))) {
            totalCalorias = Integer.parseInt(reader.readLine());
        } catch (Exception e) {
            totalCalorias = 0;
        }
    }

    private void guardarComidasSeleccionadas() {
        try (FileOutputStream fos = openFileOutput(FILE_COMIDAS, Context.MODE_PRIVATE)) {
            for (String comida : comidasSeleccionadas) {
                fos.write((comida + "\n").getBytes());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void cargarComidasSeleccionadas() {
        try (FileInputStream fis = openFileInput(FILE_COMIDAS);
             BufferedReader reader = new BufferedReader(new InputStreamReader(fis))) {
            String comida;
            while ((comida = reader.readLine()) != null) {
                comidasSeleccionadas.add(comida);
                String[] data = comida.split(":");
                if (data.length == 2) {
                    agregarComida(data[0], Integer.parseInt(data[1]), false);
                }
            }
        } catch (Exception e) {
            comidasSeleccionadas = new HashSet<>(); // Lista vacía si no existe el archivo
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            String nombreComida = data.getStringExtra("nombreComida");
            int caloriasComida = data.getIntExtra("caloriasComida", 0);
            String comidaGuardada = nombreComida + ":" + caloriasComida;
            if (!comidasSeleccionadas.contains(comidaGuardada)) {
                comidasSeleccionadas.add(comidaGuardada);
                guardarComidasSeleccionadas();
                agregarComida(nombreComida, caloriasComida, true);
            }
        }
    }

    private void agregarComida(String nombre, int calorias, boolean contarCalorias) {
        View item = LayoutInflater.from(this).inflate(R.layout.item_comida, llComidas, false);
        TextView tvNombre = item.findViewById(R.id.tv_comida);
        TextView tvCalorias = item.findViewById(R.id.tv_calorias);
        ImageView btnEliminar = item.findViewById(R.id.btn_eliminar_comida);

        tvNombre.setText(nombre);
        tvCalorias.setText(calorias + " kcal");

        btnEliminar.setOnClickListener(v -> {
            llComidas.removeView(item);
            reducirCalorias(calorias);
            comidasSeleccionadas.remove(nombre + ":" + calorias);
            guardarComidasSeleccionadas();
        });

        llComidas.addView(item);
        if (contarCalorias) agregarCalorias(calorias); // Solo suma calorías si es una comida nueva
    }

    private void mostrarAlertaExcesoCalorias() {
        Notification notification = new Notification.Builder(this, "CANAL_EXCESO_CALORIAS")
                .setContentTitle("¡Exceso de Calorías!")
                .setContentText("Has superado las calorías recomendadas. Considera hacer ejercicio o reducir calorías en la próxima comida.")
                .setSmallIcon(R.drawable.warning)
                .setPriority(Notification.PRIORITY_HIGH)
                .build();

        notificationManager.notify(NOTIF_ID_EXCESO_CALORIAS, notification);
    }
}

