package com.tapiaalexis.storage

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import com.tapiaalexis.storage.externalstorage.FileExternalManager

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.d("TAG", Environment.getExternalStorageDirectory().toString())
        Log.d("TAG", this.getExternalFilesDir(null).toString())

        FileExternalManager(this).SaveInformation()
        FileExternalManager(this).ReadInformation()
    }
}