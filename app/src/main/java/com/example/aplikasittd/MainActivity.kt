package com.example.aplikasittd

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.Glide.init
import com.example.aplikasittd.databinding.ActivityMainBinding
import com.github.gcacace.signaturepad.views.SignaturePad
import kotlinx.coroutines.delay
import java.util.*


class MainActivity : AppCompatActivity() {

    private var WRITE_EXTERNAL_STORAGE_PERMISSION_CODE: Int = 1
    private var READ_EXTERNAL_STORAGE_PERMISSION_CODE: Int = 2

    private lateinit var mSignature: SignaturePad
    private lateinit var mSignatureBitmap: Bitmap

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = resources.getString(R.string.ttd)

        checkPermission()
        init()

    }

    private fun checkPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            when (PackageManager.PERMISSION_DENIED) {
                checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) -> {
                    requestPermissions(
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        WRITE_EXTERNAL_STORAGE_PERMISSION_CODE
                    )
                }
                checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) -> {
                    requestPermissions(
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        READ_EXTERNAL_STORAGE_PERMISSION_CODE
                    )
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            WRITE_EXTERNAL_STORAGE_PERMISSION_CODE -> if (grantResults.isNotEmpty()) {
                if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    Toast.makeText(this, "Anda perlu memberikan semua izin untuk menggunakan aplikasi ini.", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
            READ_EXTERNAL_STORAGE_PERMISSION_CODE -> if (grantResults.isNotEmpty()) {
                if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    Toast.makeText(this, "Anda perlu memberikan semua izin untuk menggunakan aplikasi ini.", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }

    private fun init() {
        mSignature = findViewById(R.id.signature_pad)
    }

    //Hapus Data TTD
    fun clickClear(view: View){
        mSignature.clear()
    }

    //Sinpan & Tampilkan TTD
    fun clickSave(view: View){
        mSignatureBitmap = mSignature.signatureBitmap
        val fileName = getFileName()
        val imgPath = saveImage(mSignatureBitmap, fileName)

        if ( imgPath != null){
            Toast.makeText(this,"TTD Kamu Berhasil Tersimpan di Galery", Toast.LENGTH_SHORT).show()
            dialogSignature(mSignatureBitmap, imgPath)
        } else {
            Toast.makeText(this,"Kamu belum membuat TTD nya", Toast.LENGTH_SHORT).show()
        }

    }

    private fun getFileName(): String {
        return "${Calendar.getInstance().timeInMillis}.jpg"
    }

    private fun saveImage(myBitmap: Bitmap, nameFile: String): String {
        return MediaStore.Images.Media.insertImage(contentResolver, myBitmap, nameFile, null)
    }

    private fun dialogSignature(bitmap: Bitmap, imgPath: String) {
        val builder = AlertDialog.Builder(this)
        val factory = LayoutInflater.from(this)
        val myView = factory.inflate(R.layout.dialog_signature, null)
        val ivResult = myView.findViewById<ImageView>(R.id.iv_result)


        builder.setView(myView)
        builder.setTitle("Tanda Tangan Kamu")

        builder.setNegativeButton("Tutup"){dialog, _ ->
            dialog.dismiss()
        }

        builder.setPositiveButton("Bagikan"){_, _ ->
            share(imgPath)
        }

        builder.show().withCenteredButtons()

        bitmap.let {
            Glide.with(this)
                .load(it)
                .into(ivResult)
        }

    }

    private fun AlertDialog.withCenteredButtons() {
        val positive = getButton(AlertDialog.BUTTON_POSITIVE)
        val negative = getButton(AlertDialog.BUTTON_NEGATIVE)

        //nonaktifkan tampilan spacer material jika ada
        val parent = positive.parent as? LinearLayout
        parent?.gravity = Gravity.CENTER_HORIZONTAL
        val leftSpacer = parent?.getChildAt(1)
        leftSpacer?.visibility = View.GONE

        //paksa tombol default ke tengah
        val layoutParems = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        layoutParems.weight = 1f
        layoutParems.gravity = Gravity.CENTER

        positive.layoutParams = layoutParems
        negative.layoutParams = layoutParems


    }

    private fun share(bitmapPath: String) {
        val bitmapUri: Uri = Uri.parse(bitmapPath)

        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "image/png"
        intent.putExtra(Intent.EXTRA_STREAM, bitmapUri)
        startActivity(Intent.createChooser(intent, "Bagikan TTD Kamu Melalui: "))
    }

    //Menu Options
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.optionmenu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.petunjukMenu -> {
                val intent = Intent(this@MainActivity, PetunjukActivity::class.java)
                startActivity(intent)
            }
            R.id.tentangSaya -> {
                val intent = Intent(this@MainActivity, TentangActivity::class.java)
                startActivity(intent)
            }

        }
        return super.onOptionsItemSelected(item)
    }

}