package com.tresastronautas.trilly;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.malinskiy.materialicons.IconDrawable;
import com.malinskiy.materialicons.Iconify;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class EstadisticasActivity extends AppCompatActivity {

    private FloatingActionButton estadisticas_fab;
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
        estadisticas_fab = (FloatingActionButton) findViewById(R.id.estadisticas_fab);
        estadisticas_fab.setImageDrawable(new IconDrawable(this, Iconify.IconValue.zmdi_chevron_left)
                .colorRes(android.R.color.white));

        ParseObject statistics = currentUser.getParseObject(ParseConstants.User.STATS.val());
        try {
            final ProgressDialog progressDialog = new ProgressDialog(EstadisticasActivity.this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setMessage(getString(R.string.progressCountingKilometers));
            progressDialog.show();
            statistics.fetchInBackground(new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject object, ParseException e) {
                    progressDialog.dismiss();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        cal = statistics.getDouble(ParseConstants.Estadistica.CAL.val());
        savedTrees = statistics.getDouble(ParseConstants.Estadistica.SAVED_TREES.val());
        gas = statistics.getDouble(ParseConstants.Estadistica.GAS.val());
        tiempo = statistics.getDouble(ParseConstants.Estadistica.TIME.val()) / 60;
        kgCO2 = statistics.getDouble(ParseConstants.Estadistica.CO2.val());
        kmRecorridos = statistics.getDouble(ParseConstants.Estadistica.KM.val());

        estadisticas_texto_gas_dinamico = (TextView) findViewById(R.id.estadisticas_texto_gas_dinamico);
        estadisticas_texto_gas_dinamico.setText(getString(R.string.estadisticas_gas_dinamico, gas));
        estadisticas_texto_arboles_dinamico = (TextView) findViewById(R.id.estadisticas_texto_arboles_dinamico);
        estadisticas_texto_arboles_dinamico.setText(getString(R.string.estadisticas_arboles_dinamico, savedTrees));
        estadisticas_texto_calorias_dinamico = (TextView) findViewById(R.id.estadisticas_texto_calorias_dinamico);
        estadisticas_texto_calorias_dinamico.setText(getString(R.string.estadisticas_calorias_dinamico, cal));
        estadisticas_texto_co2_dinamico = (TextView) findViewById(R.id.estadisticas_texto_co2_dinamico);
        estadisticas_texto_co2_dinamico.setText(getString(R.string.estadisticas_co2_dinamico, kgCO2));
        estadisticas_texto_bici_dinamico = (TextView) findViewById(R.id.estadisticas_texto_bici_dinamico);
        estadisticas_texto_bici_dinamico.setText(getString(R.string.estadisticas_bici_dinamico, tiempo));
        estadisticas_texto_km_dinamico = (TextView) findViewById(R.id.estadisticas_texto_km_dinamico);
        estadisticas_texto_km_dinamico.setText(getString(R.string.estadisticas_km_dinamico, kmRecorridos));
        estadisticas_texto_tiempo_dinamico = (TextView) findViewById(R.id.estadisticas_texto_tiempo_dinamico);
        estadisticas_texto_tiempo_dinamico.setText(getString(R.string.estadisticas_tiempo_dinamico, 0.0));
    }

    public void estadisticasActivityBack(View view) {
        finish();
    }
}
