package com.example.lab5_20206456;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link InicioFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InicioFragment extends Fragment {

    private TextView tvCaloriasRequeridas;
    private Button btnAgregar, btn_add_exercise;

    private static final String SHARED_PREFS_NAME = "UserPreferences";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public InicioFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment InicioFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static InicioFragment newInstance(String param1, String param2) {
        InicioFragment fragment = new InicioFragment();
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
        View view = inflater.inflate(R.layout.fragment_inicio, container, false);

        tvCaloriasRequeridas = view.findViewById(R.id.caloriasRequeridasTextView);
        obtenerCaloriasRequeridas();

        // Recuperar y mostrar totalCalorias
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        int totalCalorias = sharedPreferences.getInt("totalCalorias", 0); // Valor por defecto 0
        int caloriasGastadas = sharedPreferences.getInt("totalCaloriasQuemadas", 0);

        TextView tvCaloriasConsumidas = view.findViewById(R.id.tv_calorias_consumidas);
        tvCaloriasConsumidas.setText(totalCalorias + " / " + tvCaloriasRequeridas.getText());

        TextView tvCaloriasGastadas = view.findViewById(R.id.tv_calorias_gastadas);
        tvCaloriasGastadas.setText(caloriasGastadas + " kcal");

        // Ir a comidas
        btnAgregar = view.findViewById(R.id.btn_agregar);
        btnAgregar.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ComidasActivity.class);
            startActivity(intent);
        });

        // Ir a ejercicios
        btn_add_exercise = view.findViewById(R.id.btn_add_exercise);
        btn_add_exercise.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), EjerciciosActivity.class);
            startActivity(intent);
        });


        return view;
    }

    private void obtenerCaloriasRequeridas() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        int caloriasRequeridas = sharedPreferences.getInt("caloriasRequeridas", 0);

        tvCaloriasRequeridas.setText(caloriasRequeridas + " kcal");
    }
}