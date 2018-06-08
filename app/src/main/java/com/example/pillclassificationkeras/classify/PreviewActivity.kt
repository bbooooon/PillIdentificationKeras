package com.example.pillclassificationkeras.classify

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_preview.*
import android.hardware.Camera
import android.graphics.Bitmap
import android.graphics.Matrix
import android.support.design.widget.Snackbar
import android.widget.Toast
import com.example.pillclassificationkeras.R
import com.example.pillclassificationkeras.ResultActivity
import com.example.pillclassificationkeras.user.MainActivity
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.GsonBuilder
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.io.ByteArrayOutputStream
import java.io.File


class PreviewActivity : AppCompatActivity() {
    private var result:String? = null
    private var fpath:String? = null
    lateinit var loadingDialog: ProgressDialog
    private val cameraId = Camera.CameraInfo.CAMERA_FACING_BACK

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
        setContentView(R.layout.activity_preview)

        val toolbar = findViewById<Toolbar>(R.id.camera_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        var bitmap = BitmapFactory.decodeByteArray(CameraUtil.bytedata, 0, CameraUtil.bytedata.size)
        val angle: Int = 90
        bitmap = RotateBitmap(bitmap,angle.toFloat())

        val x:Int = bitmap.width*40/100
        val y:Int = bitmap.height*12/100
        val resize = Bitmap.createBitmap(bitmap, x, y,(bitmap.width/2).toInt(), (bitmap.height/2).toInt())
        preview_imageView.setImageBitmap(resize)

        val stream = ByteArrayOutputStream()
        resize.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val byteArray = stream.toByteArray()
        CameraUtil.bytedata = byteArray

        preview_backbtn.setOnClickListener {
            finish()
            val intent = Intent(this, CameraActivity::class.java)
            startActivity(intent)
        }

        preview_submitbtn.setOnClickListener {
            val file = CameraUtil.savePicture();
            val orientation = CameraUtil.getCameraDisplayOrientation(this, cameraId)
            CameraUtil.setImageOrientation(file, orientation)
            CameraUtil.updateMediaScanner(this, file)
            fpath = file.absolutePath

            callRetrofit()
//            val intent = Intent(this@PreviewActivity, ResultActivity::class.java)
//            startActivity(intent)
        }
    }

    fun RotateBitmap(source: Bitmap, angle: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
    }

    private fun callRetrofit(){
        loadingDialog = ProgressDialog.show(this, "Identifying", "Please wait...", true, false)
        val gson = GsonBuilder()
                .setLenient()
                .create()
        val client = OkHttpClient.Builder()
        client.addInterceptor{ chain ->
            val req = chain.request()
            val requestBuilder = req.newBuilder().method(req.method(),req.body())
            val request = requestBuilder.build()
            chain.proceed(request)
        }

        val retrofit = Retrofit.Builder()
//                .baseUrl("http://172.29.50.85/run/run.php/")//aj.narit's
                .baseUrl("http://192.168.43.22:8080/pill_keras/run.php/")//boon's
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addConverterFactory(ScalarsConverterFactory.create())
                .build()

        val file = File(fpath)
        val reqFile = RequestBody.create(MediaType.parse("multipart/form-data"), file)
        val body = MultipartBody.Part.createFormData("uploaded_file", file.getName(),reqFile)

        val service = retrofit.create(Service::class.java)
        val call: Call<Prediction> = service.postImage(body)
        call.enqueue(object : Callback<Prediction> {
            override fun onResponse(call: Call<Prediction>, response: Response<Prediction>) {
                val pre = response.body()!!
                result = pre.msg.get(0)
                loadingDialog.dismiss()
//                Toast.makeText(applicationContext, result, Toast.LENGTH_LONG).show()
                if (result != null) {
                    val intent = Intent(this@PreviewActivity, ResultActivity::class.java)
                    intent.putExtra("result", result)
                    startActivity(intent)
                }
            }

            override fun onFailure(call: Call<Prediction>, t: Throwable) {
                t.printStackTrace();
                Toast.makeText(applicationContext, t.message, Toast.LENGTH_SHORT).show()
                finish()
            }
        }
        )
    }

    private fun uploadToStorage(){
        if (fpath!=null){
            val storage = FirebaseStorage.getInstance()
            val storageReference = storage!!.reference
            val imageRef = storageReference!!.child("pills/"+CameraUtil.filename)//send to firebase folder

            imageRef.putBytes(CameraUtil.bytedata)
                    .addOnCompleteListener{

                    }
                    .addOnFailureListener {
                        Toast.makeText(this,"Storage Upload Failed",Toast.LENGTH_SHORT).show()
                    }
        }
    }
}

