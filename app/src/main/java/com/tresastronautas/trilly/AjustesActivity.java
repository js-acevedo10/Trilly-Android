package com.tresastronautas.trilly;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class AjustesActivity extends AppCompatActivity {

    private ParseUser currentUser;
    private ExtendedButton ajustes_texto_edad_dinamico, ajustes_texto_altura_dinamico, ajustes_texto_peso_dinamico;
    private ExtendedImageButton ajustes_boton_guardarcambios;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajustes);
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
        ajustes_texto_edad_dinamico = (ExtendedButton) findViewById(R.id.ajustes_texto_edad_dinamico);
        ajustes_texto_edad_dinamico.setText(getString(R.string.ajustes_edad_dinamico, 0));
        ajustes_texto_altura_dinamico = (ExtendedButton) findViewById(R.id.ajustes_texto_altura_dinamico);
        ajustes_texto_altura_dinamico.setText(getString(R.string.ajustes_altura_dinamico, 0));
        ajustes_texto_peso_dinamico = (ExtendedButton) findViewById(R.id.ajustes_texto_peso_dinamico);
        ajustes_texto_peso_dinamico.setText(getString(R.string.ajustes_peso_dinamico, 0));
        ajustes_boton_guardarcambios = (ExtendedImageButton) findViewById(R.id.ajustes_boton_guardarcambios);
        ajustes_boton_guardarcambios.setEnabled(false);
    }

    public void ajustesEditEdad(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Ingresa tu Edad");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);
        builder.setPositiveButton("Ingresar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ajustes_boton_guardarcambios.setEnabled(true);
            }
        });
        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }

    public void ajustesEditAltura(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Ingresa tu Altura");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);
        builder.setPositiveButton("Ingresar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ajustes_boton_guardarcambios.setEnabled(true);
            }
        });
        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }

    public void ajustesEditPeso(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Ingresa tu Peso");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);
        builder.setPositiveButton("Ingresar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ajustes_boton_guardarcambios.setEnabled(true);
            }
        });
        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }

    public void ajustesGuardarCambios(View view) {
        ajustes_boton_guardarcambios.setEnabled(false);
    }

    public void ajustesCerrarSesion(View view) {
        setResult(Activity.DEFAULT_KEYS_SHORTCUT);
        finish();
    }
}
