package com.enescanpolat.firebaseogrenme

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.FirebaseAuthKtxRegistrar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*

class KullaniciActivity : AppCompatActivity() {

    private lateinit var auth : FirebaseAuth





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        auth = FirebaseAuth.getInstance()

        val guncelKullanici = auth.currentUser

        if (guncelKullanici!=null){
            val intent = Intent(this,HaberlerActivity::class.java)
            startActivity(intent)
            finish()
        }



        val girisYap = findViewById<Button>(R.id.giris)
        val kayitOl = findViewById<Button>(R.id.kayit)


        kayitOl.setOnClickListener {
            val email = email.text.toString()
            val sifre = sifre.text.toString()

            auth.createUserWithEmailAndPassword(email,sifre).addOnCompleteListener { task ->

                if (task.isSuccessful){
                    Toast.makeText(this,"Hosgeldiniz : ${email}",Toast.LENGTH_LONG).show()
                    val intent = Intent(this,HaberlerActivity::class.java)
                    startActivity(intent)
                    finish()
                }


            }.addOnFailureListener { exception ->
                Toast.makeText(applicationContext,exception.localizedMessage,Toast.LENGTH_LONG).show()
            }


        }

        girisYap.setOnClickListener {

            val email = email.text.toString()
            val sifre = sifre.text.toString()


            auth.signInWithEmailAndPassword(email,sifre).addOnCompleteListener { task ->

                if (task.isSuccessful){
                    val guncelKullanici = auth.currentUser?.email.toString()
                    Toast.makeText(applicationContext,"Hosgeldiniz ${guncelKullanici}",Toast.LENGTH_LONG).show()
                    val intent = Intent(this,HaberlerActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }.addOnFailureListener { exception->
                Toast.makeText(applicationContext,exception.localizedMessage,Toast.LENGTH_LONG).show()
            }

        }

    }

}