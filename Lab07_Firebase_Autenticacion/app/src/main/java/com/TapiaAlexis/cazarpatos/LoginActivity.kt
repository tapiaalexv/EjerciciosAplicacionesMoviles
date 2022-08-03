package com.TapiaAlexis.cazarpatos

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.regex.Pattern


class LoginActivity : AppCompatActivity() {
    lateinit var manejadorArchivo: FileHandler //Lab04
    lateinit var checkBoxRecordarme: CheckBox //Lab04
    lateinit var editTextEmail: EditText
    lateinit var editTextPassword: EditText
    lateinit var buttonLogin: Button
    lateinit var buttonNewUser: Button
    lateinit var mediaPlayer: MediaPlayer
    private lateinit var auth: FirebaseAuth

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

        // Initialize Firebase Auth
        auth = Firebase.auth


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
                    /*Si pasa validación de datos requeridos, ir a pantalla principal
                    val intencion = Intent(this, MainActivity::class.java)
                    intencion.putExtra(EXTRA_LOGIN, email)
                    startActivity(intencion)*/
                    AutenticarUsuario(email, clave)
                }
            }
        }
        buttonNewUser.setOnClickListener {

        }
        mediaPlayer = MediaPlayer.create(this, R.raw.title_screen)
        mediaPlayer.start()
    }

    fun AutenticarUsuario(email:String, password:String){
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(EXTRA_LOGIN, "signInWithEmail:success")
                    //Si pasa validación de datos requeridos, ir a pantalla principal
                    val intencion = Intent(this, MainActivity::class.java)
                    intencion.putExtra(EXTRA_LOGIN, auth.currentUser!!.email)
                    startActivity(intencion)
                    //finish()
                } else {
                    Log.w(EXTRA_LOGIN, "signInWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, task.exception!!.message,
                        Toast.LENGTH_SHORT).show()
                }
            }
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
