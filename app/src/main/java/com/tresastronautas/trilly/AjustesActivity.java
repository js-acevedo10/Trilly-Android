package com.tresastronautas.trilly;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class AjustesActivity extends AppCompatActivity {

    public static final int RESULT_CERRAR_SESION = 1101;
    public static final int RESULT_GUARDAR_CAMBIOS = 1102;
    public static final int RESULT_NOTHING_TODO = 1102;
    private ParseUser currentUser;
    private EditText ajustes_texto_edad_dinamico, ajustes_texto_altura_dinamico, ajustes_texto_peso_dinamico;
    private ExtendedImageButton ajustes_boton_guardarcambios;
    private CircleImageView ajustes_circle_profile;
    private boolean cambios = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajustes);
        currentUser = StaticThings.getCurrentUser();
        prepareLayout();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    public void prepareLayout() {
        ajustes_circle_profile = (CircleImageView) findViewById(R.id.ajustes_circle_profile);
        Picasso.with(this).load(currentUser.getString(ParseConstants.User.PIC.val())).into(ajustes_circle_profile);
        ajustes_texto_edad_dinamico = (EditText) findViewById(R.id.ajustes_texto_edad_dinamico);
        ajustes_texto_edad_dinamico.setHint(getString(R.string.ajustes_edad_dinamico, currentUser.getDouble(ParseConstants.User.EDAD.val())));
        ajustes_texto_edad_dinamico.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                ajustes_boton_guardarcambios.setEnabled(true);
            }
        });
        ajustes_texto_altura_dinamico = (EditText) findViewById(R.id.ajustes_texto_altura_dinamico);
        ajustes_texto_altura_dinamico.setHint(getString(R.string.ajustes_altura_dinamico, currentUser.getDouble(ParseConstants.User.ALTURA.val())));
        ajustes_texto_altura_dinamico.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                ajustes_boton_guardarcambios.setEnabled(true);
            }
        });
        ajustes_texto_peso_dinamico = (EditText) findViewById(R.id.ajustes_texto_peso_dinamico);
        ajustes_texto_peso_dinamico.setHint(getString(R.string.ajustes_peso_dinamico, currentUser.getDouble(ParseConstants.User.KG.val())));
        ajustes_texto_peso_dinamico.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                ajustes_boton_guardarcambios.setEnabled(true);
            }
        });
        ajustes_boton_guardarcambios = (ExtendedImageButton) findViewById(R.id.ajustes_boton_guardarcambios);
        ajustes_boton_guardarcambios.setEnabled(false);
    }

    public void ajustesGuardarCambios(View view) {
        ajustes_boton_guardarcambios.setEnabled(false);
        final ProgressDialog progressDialog = new ProgressDialog(AjustesActivity.this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage(getString(R.string.progressSavingUserSettings));
        progressDialog.show();
        if (!ajustes_texto_peso_dinamico.getText().toString().isEmpty()) {
            currentUser.put(ParseConstants.User.KG.val(), Double.parseDouble(ajustes_texto_peso_dinamico.getText().toString()));
        }
        if (!ajustes_texto_edad_dinamico.getText().toString().isEmpty()) {
            currentUser.put(ParseConstants.User.EDAD.val(), Double.parseDouble(ajustes_texto_edad_dinamico.getText().toString()));
        }
        if (!ajustes_texto_altura_dinamico.getText().toString().isEmpty()) {
            currentUser.put(ParseConstants.User.ALTURA.val(), Double.parseDouble(ajustes_texto_altura_dinamico.getText().toString()) / 100.0);
        }
        currentUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                progressDialog.dismiss();
                StaticThings.setCurrentUser(currentUser);
            }
        });
    }

    public void ajustesCerrarSesion(View view) {
        setResult(RESULT_CERRAR_SESION);
        finish();
    }
}
