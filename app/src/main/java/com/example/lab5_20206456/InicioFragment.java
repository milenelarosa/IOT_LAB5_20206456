package com.example.lab5_20206456;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link InicioFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InicioFragment extends Fragment {

    private TextView tvCaloriasRequeridas;
    private Button btnAgregar, btn_add_exercise;

    private TextView tvVasosTomados;
    private int vasosTomados = 0;

    private static final String SHARED_PREFS_NAME = "UserPreferences";
    private static final String VASOS_TOMADOS_KEY = "vasosTomados";

    private static final String CHANNEL_ID = "CANAL_HIDRATACION";

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

        crearCanalDeNotificacion();
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

        tvVasosTomados = view.findViewById(R.id.tv_vasos_tomados);
        ImageView btnAgregarVaso = view.findViewById(R.id.btn_agregar_vaso);

        vasosTomados = sharedPreferences.getInt(VASOS_TOMADOS_KEY, 0);
        actualizarTextoVasosTomados();

        btnAgregarVaso.setOnClickListener(v -> {
            vasosTomados++;
            actualizarTextoVasosTomados();
            guardarVasosTomados();
            Toast.makeText(getContext(), "Se registró 1 vaso tomado", Toast.LENGTH_SHORT).show();

            if (vasosTomados == 8) {
                enviarNotificacion("Objetivo de Hidratación", "¡Felicidades! Cumpliste tu objetivo de 8 vasos de agua.");
            }
        });
        return view;
    }

    private void obtenerCaloriasRequeridas() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        int caloriasRequeridas = sharedPreferences.getInt("caloriasRequeridas", 0);

        tvCaloriasRequeridas.setText(caloriasRequeridas + " kcal");
    }

    private void actualizarTextoVasosTomados() {
        tvVasosTomados.setText(vasosTomados + " / 8 vasos");
    }

    private void guardarVasosTomados() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(VASOS_TOMADOS_KEY, vasosTomados);
        editor.apply();
    }

    private void crearCanalDeNotificacion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence nombre = "Canal de Hidratación";
            String descripcion = "Notificaciones sobre el objetivo de hidratación y consumo de calorías";
            int importancia = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel canal = new NotificationChannel(CHANNEL_ID, nombre, importancia);
            canal.setDescription(descripcion);

            NotificationManager notificationManager = requireActivity().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(canal);
        }
    }

    private void enviarNotificacion(String titulo, String mensaje) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(requireContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.agua)
                .setContentTitle(titulo)
                .setContentText(mensaje)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(requireContext());
        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        notificationManager.notify(1, builder.build());
    }

    private void verificarCalorias(int totalCalorias, int caloriasRequeridas) {
        if (totalCalorias > caloriasRequeridas) {
            enviarNotificacion("Alerta de Calorías", "Has excedido el consumo de calorías permitido.");
        }
    }
}