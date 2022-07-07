package com.TapiaAlexis.cazarpatos

import android.app.Activity
import android.content.Context
import android.util.Log
import java.io.*

class FileInternalManager(val actividad: Activity) : FileHandler {
    val fichero = "fichero.txt"
    override fun SaveInformation(datosAGrabar: Pair<String, String>) {
        val texto = datosAGrabar.first
        val fos: FileOutputStream
        try {
            fos = actividad.openFileOutput(fichero, Context.MODE_PRIVATE)
            fos.write(datosAGrabar.first.toByteArray())
            fos.write(System.lineSeparator().toByteArray())
            fos.write(datosAGrabar.second.toByteArray())
            fos.close()
        } catch (e: FileNotFoundException) {
            Log.e("Mi aplicacion", e.message, e)
        } catch (e: IOException) {
            Log.e("Mi aplicacion", e.message, e)
        }
    }

    override fun ReadInformation(): Pair<String, String> {
        actividad.openFileInput(fichero).bufferedReader().use {
            val datoLeido = it.readText()
            val textArray = datoLeido.split("\n")
            val email = textArray[0]
            val clave = textArray[1]
            return (email to clave)
        }
    }
}