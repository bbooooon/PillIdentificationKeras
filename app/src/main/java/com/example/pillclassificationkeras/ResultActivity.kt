package com.example.pillclassificationkeras

import android.content.Intent
import android.graphics.BitmapFactory
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import com.example.pillclassificationkeras.classify.CameraActivity
import com.example.pillclassificationkeras.classify.CameraUtil
import com.example.pillclassificationkeras.user.MainActivity
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_result.*

class ResultActivity : AppCompatActivity() {
    lateinit var dataReference: DatabaseReference

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        getMenuInflater().inflate(R.menu.menu_add, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.getItemId()) {
            R.id.action_help -> {
                return true
            }
            R.id.action_logout -> {
                finish()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)
        val toolbar = findViewById<Toolbar>(R.id.camera_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        var bitmap = BitmapFactory.decodeByteArray(CameraUtil.bytedata, 0, CameraUtil.bytedata.size)
        imageView3.setImageBitmap(bitmap)

        val intent1 = intent.extras
//        val result = "miformin"
        val result = intent1.getString("result")

        dataReference = FirebaseDatabase.getInstance().getReference("Medicine_info")
        dataReference.child(result).addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {
            }

            override fun onDataChange(p0: DataSnapshot?) {
                val message = p0!!.getValue(MedicineInfo::class.java)
                val msgObject = message!!

                usage_text.setText("ยานี้ใช้สำหรับ : " + msgObject.usage)
                effect_text.setText("สรรพคุณ : " + msgObject.effect)
                medicalname_text.setText("ชื่อสามัญ : " + msgObject.medical_name)
                tradename_text.setText("ชื่อการค้า : " + msgObject.trade_mark)
                manufacturer_text.setText("ผู้ผลิต : " + msgObject.manufacturer)
            }
        })

        result_btn.setOnClickListener {
            finish()
            val intent = Intent(this, CameraActivity::class.java)
            startActivity(intent)
        }
    }

    fun checkAppRun()
    {
        val sharedPreferences = baseContext.getSharedPreferences(
                "MyAppPreference", MODE_PRIVATE
        )
        val IsFirstRun = sharedPreferences.getBoolean("IsFirstRun", true)
        if (IsFirstRun) {
        } else {
        }

    }
}
