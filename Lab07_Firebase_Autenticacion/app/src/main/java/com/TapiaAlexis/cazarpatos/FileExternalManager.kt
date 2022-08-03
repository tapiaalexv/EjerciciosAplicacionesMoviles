package com.TapiaAlexis.cazarpatos

import android.app.Activity
import android.os.Environment
import android.util.Log
import java.io.*

class FileExternalManager(val actividad: Activity) : FileHandler {
    fun isExternalStorageWritable(): Boolean {
        return Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
    }

    override fun SaveInformation(datosAGrabar: Pair<String, String>) {
        if (isExternalStorageWritable()) {
            FileOutputStream(
                File(
                    actividad.getExternalFilesDir(null),
                    "myExtSharedInformation.dat"
                )
            ).bufferedWriter().use { outputStream ->
                outputStream.write(datosAGrabar.first)
                outputStream.write(System.lineSeparator())
                outputStream.write(datosAGrabar.second)
            }
        }
    }

    fun isExternalStorageReadable(): Boolean {
        return Environment.getExternalStorageState() in
                setOf(Environment.MEDIA_MOUNTED, Environment.MEDIA_MOUNTED_READ_ONLY)
    }

    override fun ReadInformation(): Pair<String, String> {
        if (isExternalStorageReadable()) {
            try {
                FileInputStream(
                    File(
                        actividad.getExternalFilesDir(null),
                        "myExtSharedInformation.dat"
                    )
                ).bufferedReader().use {
                    val datoLeido = it.readText()
                    val textArray = datoLeido.split(System.lineSeparator())
                    val email = textArray[0]
                    val clave = textArray[1]
                    return (email to clave)
                }
            } catch (e: FileNotFoundException) {
                Log.e("Mi aplicacion", e.message, e)
            } catch (e: IOException) {
                Log.e("Mi aplicacion", e.message, e)
            }
        }
        return ("" to "")
    }
}