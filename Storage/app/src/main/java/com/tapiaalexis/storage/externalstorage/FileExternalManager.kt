package com.tapiaalexis.storage.externalstorage

import android.app.Activity
import android.os.Environment
import android.util.Log
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class FileExternalManager(val actividad: Activity) {
    fun isExternalStorageWritable(): Boolean {
        return Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
    }
    fun SaveInformation() {
        if (isExternalStorageWritable()) {
            FileOutputStream(
                File(
                    actividad.getExternalFilesDir(null),
                    "mySharedInformation.dat"
                )
            ).bufferedWriter().use { outputStream ->
                outputStream.write("dato1")
                outputStream.write(System.lineSeparator())
                outputStream.write("dato2")
            }
        }
    }
    fun isExternalStorageReadable(): Boolean {
        return Environment.getExternalStorageState() in
                setOf(Environment.MEDIA_MOUNTED, Environment.MEDIA_MOUNTED_READ_ONLY)
    }
    fun ReadInformation() {
        if (isExternalStorageReadable()) {
            FileInputStream(
                File(
                    actividad.getExternalFilesDir(null),
                    "mySharedInformation.dat"
                )
            ).bufferedReader().use {
                val datoLeido = it.readText()
                val textArray = datoLeido.split(System.lineSeparator())
                val texto1 = textArray[0]
                val texto2 = textArray[1]
                println(textArray)
            }
        }
    }
}