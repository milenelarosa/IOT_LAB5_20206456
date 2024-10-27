package com.example.lab5_20206456;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PerfilFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PerfilFragment extends Fragment {

    private EditText etPeso, etAltura, etEdad;
    private Spinner spinnerGenero, spinnerNivelActividad;
    private RadioGroup rgObjetivo;
    private Button btnCalcular;
    private TextView tvResultadoCalorias;

    private static final String SHARED_PREFS_NAME = "UserPreferences";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public PerfilFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PerfilFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PerfilFragment newInstance(String param1, String param2) {
        PerfilFragment fragment = new PerfilFragment();
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_perfil, container, false);

        etPeso = view.findViewById(R.id.et_peso);
        etAltura = view.findViewById(R.id.et_altura);
        etEdad = view.findViewById(R.id.et_edad);
        spinnerGenero = view.findViewById(R.id.spinner_genero);
        spinnerNivelActividad = view.findViewById(R.id.spinner_nivel_actividad);
        rgObjetivo = view.findViewById(R.id.rg_objetivo);
        btnCalcular = view.findViewById(R.id.btn_calcular);
        tvResultadoCalorias = view.findViewById(R.id.tv_resultado_calorias);

        cargarDatosPerfil();

        btnCalcular.setOnClickListener(v -> calcularCaloriasDiarias());

        return view;
    }

    private void calcularCaloriasDiarias() {
        double peso = Double.parseDouble(etPeso.getText().toString());
        double altura = Double.parseDouble(etAltura.getText().toString());
        int edad = Integer.parseInt(etEdad.getText().toString());
        String genero = spinnerGenero.getSelectedItem().toString();
        String nivelActividad = spinnerNivelActividad.getSelectedItem().toString();

        // Calcular TMB
        double tmb = (10 * peso) + (6.25 * altura) - (5 * edad) + (genero.equals("Masculino") ? 5 : -161);

        double caloriasDiarias = calcularCaloriasActividad(tmb, nivelActividad);

        // Ajuste por objetivo
        int objetivo = rgObjetivo.getCheckedRadioButtonId();
        if (objetivo == R.id.rb_subir_peso) {
            caloriasDiarias += 500;
        } else if (objetivo == R.id.rb_bajar_peso) {
            caloriasDiarias -= 300;
        }

        tvResultadoCalorias.setText("Calorías diarias recomendadas: " + caloriasDiarias);

        // Guardar datos en SharedPreferences
        guardarDatosPerfil((int) caloriasDiarias, peso, altura, edad, genero, nivelActividad, objetivo);
    }

    private double calcularCaloriasActividad(double tmb, String nivelActividad) {
        switch (nivelActividad) {
            case "Sedentario":
                // Personas que no realizan nada de ejercicio
                return tmb * 1.2;
            case "Ligero":
                // Personas que realizan ejercicios suaves de 1 a 3 veces por semana
                return tmb * 1.375;
            case "Moderado":
                // Personas que practican deporte de 3 a 5 veces por semana
                return tmb * 1.55;
            case "Activo":
                // Personas muy activas que practican deporte de 6 a 7 veces por semana
                return tmb * 1.725;
            case "Muy Activo":
                // Personas hiperactivas que practican ejercicios físicos muy intensos
                return tmb * 1.9;
            default:
                return tmb;
        }
    }

    private void guardarDatosPerfil(int calorias, double peso, double altura, int edad, String genero, String actividad, int objetivo) {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("caloriasRequeridas", calorias);
        editor.putFloat("peso", (float) peso);
        editor.putFloat("altura", (float) altura);
        editor.putInt("edad", edad);
        editor.putString("genero", genero);
        editor.putString("nivelActividad", actividad);
        editor.putInt("objetivo", objetivo);
        editor.apply();
    }

    private void cargarDatosPerfil() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);

        // Cargar valores guardados
        etPeso.setText(String.valueOf(sharedPreferences.getFloat("peso", 0)));
        etAltura.setText(String.valueOf(sharedPreferences.getFloat("altura", 0)));
        etEdad.setText(String.valueOf(sharedPreferences.getInt("edad", 0)));

        // Género
        String genero = sharedPreferences.getString("genero", "");
        ArrayAdapter<CharSequence> adapterGenero = (ArrayAdapter<CharSequence>) spinnerGenero.getAdapter();
        spinnerGenero.setSelection(adapterGenero.getPosition(genero));

        // Nivel de actividad
        String actividad = sharedPreferences.getString("nivelActividad", "");
        ArrayAdapter<CharSequence> adapterActividad = (ArrayAdapter<CharSequence>) spinnerNivelActividad.getAdapter();
        spinnerNivelActividad.setSelection(adapterActividad.getPosition(actividad));

        // Objetivo
        int objetivo = sharedPreferences.getInt("objetivo", -1);
        if (objetivo != -1) {
            rgObjetivo.check(objetivo);
        }

        // Calorías requeridas
        int calorias = sharedPreferences.getInt("caloriasRequeridas", 0);
        if (calorias != 0) {
            tvResultadoCalorias.setText("Calorías diarias recomendadas: " + calorias);
        }
    }
}