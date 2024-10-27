package com.example.lab5_20206456;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private final InicioFragment inicioFragment = new InicioFragment();
    private final EstadisticasFragment estadisticasFragment = new EstadisticasFragment();
    private final PerfilFragment perfilFragment = new PerfilFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = findViewById(R.id.bottom_navigation);

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        if (savedInstanceState == null) {
            loadFragment(inicioFragment);
        }
    }

    private final BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;

                    if (item.getItemId() == R.id.navigation_home) {
                        selectedFragment = inicioFragment;
                    } else if (item.getItemId() == R.id.navigation_estadisticas) {
                        selectedFragment = estadisticasFragment;
                    } else if (item.getItemId() == R.id.navigation_perfil) {
                        selectedFragment = perfilFragment;
                    }

                    if (selectedFragment != null) {
                        loadFragment(selectedFragment);
                    }
                    return true;
                }
            };

    public void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_navigation_container, fragment)
                .commit();
    }
}