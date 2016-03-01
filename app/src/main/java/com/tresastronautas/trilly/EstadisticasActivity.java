package com.tresastronautas.trilly;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class EstadisticasActivity extends AppCompatActivity {

    public double cal, savedTrees, gas, kmRecorridos, tiempo, kgCO2;
    private ParseUser currentUser;
    private TextView estadisticas_texto_gas_dinamico, estadisticas_texto_arboles_dinamico, estadisticas_texto_calorias_dinamico,
            estadisticas_texto_co2_dinamico, estadisticas_texto_bici_dinamico, estadisticas_texto_km_dinamico, estadisticas_texto_tiempo_dinamico;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estadisticas);
        String userID = getIntent().getStringExtra("user_id");
        ParseQuery<ParseUser> query = ParseQuery.getQuery(ParseUser.class);
        query.fromLocalDatastore();
        query.getInBackground(userID, new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser object, ParseException e) {
                if (e == null) {
                    currentUser = object;
                    prepareLayout();
                }
            }
        });
    }

    public void prepareLayout() {
        cal = currentUser.getDouble(ParseConstants.Estadistica.CAL.val());
        savedTrees = currentUser.getDouble(ParseConstants.Estadistica.SAVED_TREES.val());
        gas = currentUser.getDouble(ParseConstants.Estadistica.GAS.val());
        tiempo = currentUser.getDouble(ParseConstants.Estadistica.TIME.val());
        kgCO2 = currentUser.getDouble(ParseConstants.Estadistica.CO2.val());
        kmRecorridos = currentUser.getDouble(ParseConstants.Estadistica.KM.val());

        estadisticas_texto_gas_dinamico = (TextView) findViewById(R.id.estadisticas_texto_gas_dinamico);
        estadisticas_texto_gas_dinamico.setText(getString(R.string.estadisticas_gas_dinamico, gas));
        estadisticas_texto_arboles_dinamico = (TextView) findViewById(R.id.estadisticas_texto_arboles_dinamico);
        estadisticas_texto_arboles_dinamico.setText(getString(R.string.estadisticas_arboles_dinamico, savedTrees));
        estadisticas_texto_calorias_dinamico = (TextView) findViewById(R.id.estadisticas_texto_calorias_dinamico);
        estadisticas_texto_calorias_dinamico.setText(getString(R.string.estadisticas_calorias_dinamico, cal));
        estadisticas_texto_co2_dinamico = (TextView) findViewById(R.id.estadisticas_texto_co2_dinamico);
        estadisticas_texto_co2_dinamico.setText(getString(R.string.estadisticas_co2_dinamico, kgCO2));
        estadisticas_texto_bici_dinamico = (TextView) findViewById(R.id.estadisticas_texto_bici_dinamico);
        estadisticas_texto_bici_dinamico.setText(getString(R.string.estadisticas_bici_dinamico, 0.0));
        estadisticas_texto_km_dinamico = (TextView) findViewById(R.id.estadisticas_texto_km_dinamico);
        estadisticas_texto_km_dinamico.setText(getString(R.string.estadisticas_km_dinamico, kmRecorridos));
        estadisticas_texto_tiempo_dinamico = (TextView) findViewById(R.id.estadisticas_texto_tiempo_dinamico);
        estadisticas_texto_tiempo_dinamico.setText(getString(R.string.estadisticas_tiempo_dinamico, tiempo));
    }

    public void estadisticasActivityBack(View view) {
        finish();
    }
}
