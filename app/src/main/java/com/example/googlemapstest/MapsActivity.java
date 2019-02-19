package com.example.googlemapstest;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    private Spinner spinnerCategoria;
    private EditText editTextDescripcion;
    Map<String, Integer> mapIdCategoria = new HashMap<>();

    LatLng nuevaPosicion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        spinnerCategoria = findViewById(R.id.spinnerCategoria);
        editTextDescripcion = findViewById(R.id.editTextDescripcion);

        DB_SQLite db = new DB_SQLite(this);

        SQLiteDatabase conn = db.getReadableDatabase();

        String sql = "SELECT id, categoria FROM CATEGORIAS ";
        Cursor cursor = conn.rawQuery(sql, null);
        if (cursor.getCount() == 0) {
            // TODO: mostrar error
        } else {
            cursor.moveToFirst();
            do {
                Integer id = cursor.getInt(cursor.getColumnIndex("id"));
                String categoria = cursor.getString(cursor.getColumnIndex("categoria"));
                mapIdCategoria.put(categoria, id);
            } while (cursor.moveToNext());
        }
        cursor.close();
        conn.close();

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMarkerClickListener(this);
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                nuevaPosicion = latLng;
                String titulo = (editTextDescripcion.getText() == null) ? "Falta título" : editTextDescripcion.getText().toString();
                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(nuevaPosicion).title(titulo).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
            }
        });
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        return false;
    }


    public void guardarPosicion(View view) {
        DB_SQLite db = new DB_SQLite(this);

        SQLiteDatabase conn = db.getWritableDatabase();

        String sql = "INSERT INTO POSICIONES ( latitud, longitud, descripcion, idCategoria) VALUES (" + nuevaPosicion.latitude + ","+ nuevaPosicion.longitude+ ", '" +editTextDescripcion.getText().toString() + "'," + mapIdCategoria.get(spinnerCategoria.getSelectedItem().toString()) + ")";

        conn.execSQL(sql);
        conn.close();
        db.close();

        Toast.makeText(this, "Se ha insertado la posición", Toast.LENGTH_SHORT).show();
    }
}
