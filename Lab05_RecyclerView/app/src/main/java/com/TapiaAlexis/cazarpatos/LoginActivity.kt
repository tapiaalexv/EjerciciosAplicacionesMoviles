package com.TapiaAlexis.cazarpatos

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import java.util.regex.Pattern


class LoginActivity : AppCompatActivity() {
    lateinit var manejadorArchivo: FileHandler //Lab04
    lateinit var checkBoxRecordarme: CheckBox //Lab04
    lateinit var editTextEmail: EditText
    lateinit var editTextPassword: EditText
    lateinit var buttonLogin: Button
    lateinit var buttonNewUser: Button
    lateinit var mediaPlayer: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        /*Inicialización de variables*/

        //manejadorArchivo = EncryptedSharedPreferencesManager(this) //Lab 04 - Encriptacion
        //manejadorArchivo = SharedPreferencesManager(this) //Lab 04 - Normal
        manejadorArchivo = FileExternalManager(this) //Lab 04 - Almacenamiento Externo

        editTextEmail = findViewById(R.id.editTextEmail)
        editTextPassword = findViewById(R.id.editTextPassword)
        buttonLogin = findViewById(R.id.buttonLogin)
        buttonNewUser = findViewById(R.id.buttonNewUser)
        checkBoxRecordarme = findViewById(R.id.checkBoxRecordarme) //lab 04

        LeerDatosDePreferencias() //Lab 04

        //Eventos clic
        buttonLogin.setOnClickListener {
            val email = editTextEmail.text.toString()
            val clave = editTextPassword.text.toString()
            //Validaciones de datos requeridos y formatos
            if (!ValidarDatosRequeridos())
                return@setOnClickListener

            //Guardar datos en preferencias.
            GuardarDatosEnPreferencias() //Lab 04
            if(!validarEmail(email)){
                editTextEmail.setError("Email no válido")
            }else{
                if (editTextPassword.text.toString().length < 8) {
                    editTextPassword.setError("La contraseña mínnima 8 caracteres")
                } else {
                    //Si pasa validación de datos requeridos, ir a pantalla principal
                    val intencion = Intent(this, MainActivity::class.java)
                    intencion.putExtra(EXTRA_LOGIN, email)
                    startActivity(intencion)
                }
            }
        }
        buttonNewUser.setOnClickListener {

        }
        mediaPlayer = MediaPlayer.create(this, R.raw.title_screen)
        mediaPlayer.start()
    }

    private fun ValidarDatosRequeridos(): Boolean {
        val email = editTextEmail.text.toString()
        val clave = editTextPassword.text.toString()
        if (email.isEmpty()) {
            editTextEmail.setError("El email es obligatorio")
            editTextEmail.requestFocus()
            return false
        }
        if (clave.isEmpty()) {
            editTextPassword.setError("La clave es obligatoria")
            editTextPassword.requestFocus()
            return false
        }
        if (clave.length < 3) {
            editTextPassword.setError("La clave debe tener al menos 3 caracteres")
            editTextPassword.requestFocus()
            return false
        }
        return true
    }

    private fun GuardarDatosEnPreferencias() { //Lab 04
        val email = editTextEmail.text.toString()
        val clave = editTextPassword.text.toString()
        val listadoAGrabar: Pair<String, String>
        if (checkBoxRecordarme.isChecked) {
            listadoAGrabar = email to clave
        } else {
            listadoAGrabar = "" to ""
        }
        manejadorArchivo.SaveInformation(listadoAGrabar)
    }

    private fun LeerDatosDePreferencias() { //Lab 04
        val listadoLeido = manejadorArchivo.ReadInformation()
        if (listadoLeido.first != null) {
            checkBoxRecordarme.isChecked = true
        }
        editTextEmail.setText(listadoLeido.first)
        editTextPassword.setText(listadoLeido.second)
    }

    private fun validarEmail(email: String): Boolean {
        val pattern: Pattern = Patterns.EMAIL_ADDRESS
        return pattern.matcher(email).matches()
    }

    override fun onDestroy() {
        mediaPlayer.release()
        super.onDestroy()
    }
}
