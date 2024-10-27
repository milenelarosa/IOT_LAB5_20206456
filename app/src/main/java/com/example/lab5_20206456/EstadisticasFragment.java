package com.example.lab5_20206456;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EstadisticasFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EstadisticasFragment extends Fragment {

    private ProgressBar progressBarCalorias;
    private TextView tvCaloriasProgreso, tvCaloriasObjetivo, tvCaloriasTotales, tvCaloriasActividad, tvCaloriasConteo;
    private EditText etIntervaloNotificacion;

    private static final String SHARED_PREFS_NAME = "UserPreferences";
    private int caloriasRequeridas, caloriasConsumidas, caloriasGastadas;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public EstadisticasFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EstadisticasFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EstadisticasFragment newInstance(String param1, String param2) {
        EstadisticasFragment fragment = new EstadisticasFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_estadisticas, container, false);

        // Inicializar vistas
        progressBarCalorias = view.findViewById(R.id.progressBarCalorias);
        tvCaloriasProgreso = view.findViewById(R.id.tvCaloriasProgreso);
        tvCaloriasObjetivo = view.findViewById(R.id.tvCaloriasObjetivo);
        tvCaloriasTotales = view.findViewById(R.id.tvCaloriasTotales);
        tvCaloriasActividad = view.findViewById(R.id.tvCaloriasActividad);
        tvCaloriasConteo = view.findViewById(R.id.tvCaloriasConteo);
        etIntervaloNotificacion = view.findViewById(R.id.etIntervaloNotificacion);

        cargarDatosDeCalorias();
        actualizarGrafico();

        // Configurar notificaciones de motivación
        etIntervaloNotificacion.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                configurarNotificacionDeMotivacion();
                return true;
            }
            return false;
        });

        return view;
    }

    private void cargarDatosDeCalorias() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        caloriasRequeridas = sharedPreferences.getInt("caloriasRequeridas", 0);
        caloriasConsumidas = sharedPreferences.getInt("totalCalorias", 0);
        caloriasGastadas = sharedPreferences.getInt("totalCaloriasQuemadas", 0);

        tvCaloriasObjetivo.setText("Objetivo " + caloriasRequeridas + " kcal");
        tvCaloriasTotales.setText("Total de calorías quemadas: " + caloriasConsumidas + " kcal");
        tvCaloriasActividad.setText("Quemadas por actividad: " + caloriasGastadas + " kcal");
        tvCaloriasConteo.setText("Caloricas faltantes: " + (caloriasRequeridas - caloriasConsumidas + caloriasGastadas)  + " kcal");
    }

    private void actualizarGrafico() {
        int progreso = (caloriasConsumidas - caloriasGastadas) * 100 / caloriasRequeridas;
        progressBarCalorias.setProgress(progreso);
        tvCaloriasProgreso.setText(String.valueOf(progreso) + " %");
    }

    private void configurarNotificacionDeMotivacion() {
        int intervalo = Integer.parseInt(etIntervaloNotificacion.getText().toString());

        AlarmManager alarmManager = (AlarmManager) requireActivity().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(requireContext(), MotivacionReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                requireContext(),
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE // Asegúrate de especificar esta bandera
        );

        long intervaloEnMilisegundos = intervalo * 60 * 1000;
        alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis() + intervaloEnMilisegundos,
                intervaloEnMilisegundos,
                pendingIntent
        );

        Toast.makeText(requireContext(), "Notificación de motivación configurada cada " + intervalo + " minutos.", Toast.LENGTH_SHORT).show();
    }
}