package com.enescanpolat.firebaseogrenme

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_fotograf_paylas.*
import java.util.*

class FotografPaylasActivity : AppCompatActivity() {
    private var secilenGorsel: Uri? = null
    private var secilenBitmap: Bitmap? = null


    private lateinit var auth: FirebaseAuth
    private lateinit var storage: FirebaseStorage
    private lateinit var database: FirebaseFirestore


    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

            if (result.resultCode == Activity.RESULT_OK) {

                val data: Intent? = result.data
                secilenGorsel = data?.data

                if (secilenGorsel != null) {
                    if (Build.VERSION.SDK_INT >= 28) {
                        val source =
                            ImageDecoder.createSource(this.contentResolver, secilenGorsel!!)
                        secilenBitmap = ImageDecoder.decodeBitmap(source)
                        val resimsec = findViewById<ImageView>(R.id.gorselSec)
                        resimsec.setImageBitmap(secilenBitmap)
                    } else {
                        val source =
                            MediaStore.Images.Media.getBitmap(this.contentResolver, secilenGorsel)
                        val resimsec = findViewById<ImageView>(R.id.gorselSec)
                        resimsec.setImageBitmap(secilenBitmap)
                    }
                }

            }

        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fotograf_paylas)


        val gorselSec = findViewById<ImageView>(R.id.gorselSec)
        val paylas = findViewById<Button>(R.id.paylas)

        auth = FirebaseAuth.getInstance()
        storage = FirebaseStorage.getInstance()
        database = FirebaseFirestore.getInstance()

        gorselSec.setOnClickListener { gorselSec() }



        paylas.setOnClickListener {

            val uuid = UUID.randomUUID()
            val gorselIsmi = "${uuid}.jpg"
            val reference = storage.reference
            val gorselReference = reference.child("images").child(gorselIsmi)

            if(secilenGorsel!=null){
                gorselReference.putFile(secilenGorsel!!).addOnSuccessListener { taskSnapShot ->

                    val yuklenenGorselReference = FirebaseStorage.getInstance().reference.child("images").child(gorselIsmi)
                    yuklenenGorselReference.downloadUrl.addOnSuccessListener { uri ->
                        val downloadurl = uri.toString()
                        val guncelKullaniciemaili = auth.currentUser!!.email.toString()
                        val kullaniciyorumu = yorumText.text.toString()
                        val tarih = Timestamp.now()

                        //veri  islemleri

                        val postHashMap = hashMapOf<String,Any>()
                        postHashMap.put("gorselurl",downloadurl)
                        postHashMap.put("kullaniciemail",guncelKullaniciemaili)
                        postHashMap.put("kullaniciyorum",kullaniciyorumu)
                        postHashMap.put("tarih",tarih)


                        database.collection("Post").add(postHashMap).addOnCompleteListener { task ->

                            if (task.isSuccessful){
                                finish()
                            }

                        }.addOnFailureListener { exception ->
                            Toast.makeText(applicationContext,exception.localizedMessage,Toast.LENGTH_LONG).show()
                        }


                    }

                }.addOnFailureListener { exception ->
                    Toast.makeText(applicationContext,exception.localizedMessage,Toast.LENGTH_LONG).show()
                }
            }


        }






    }


    private fun gorselSec() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),1)
        }else{
            galeriyiAc()
        }
    }

    private fun galeriyiAc(){
        val galeriIntent = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        resultLauncher.launch(galeriIntent)
    }





}