package com.techsmith.mw_so

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.*
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.techsmith.mw_so.utils.AppConfigSettings
import com.techsmith.mw_so.utils.UserPL
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class LoginActivity : AppCompatActivity() {

    var sharedPreferences: SharedPreferences? = null
    private val myPreference = "myPref"
    var etUsername: TextInputEditText? = null
    var etPassword: TextInputEditText? = null
    var username: String? = null
    var password: String? = null
    var pDialog: ProgressDialog? = null
    var strCheckLogin: String? = null
    var strErrorMsg: String? = null
    var permissions = arrayOf(
        Manifest.permission.INTERNET,
        Manifest.permission.ACCESS_WIFI_STATE,
        Manifest.permission.ACCESS_NETWORK_STATE,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_BACKGROUND_LOCATION
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        Dexter.withContext(this)
            .withPermissions(
                *permissions
            ).withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    println("Permission Checked $report")
                }

                override fun onPermissionRationaleShouldBeShown(
                    list: List<PermissionRequest>,
                    permissionToken: PermissionToken
                ) {
                }
            }).check()
        sharedPreferences = getSharedPreferences(myPreference, Context.MODE_PRIVATE)
        etUsername = findViewById(R.id.etUsername)
        etPassword = findViewById(R.id.etPassword)

    }

    fun LoGin(view: View) {


        username = etUsername!!.text.toString()
        password = etPassword!!.text.toString()


        val editor: SharedPreferences.Editor = sharedPreferences!!.edit()
        editor.putString("username", username)
        editor.putString("password", password)
        editor.commit()
        editor.apply()
        CheckLoginTask().execute()

    }

    @SuppressLint("StaticFieldLeak")
    private inner class CheckLoginTask :
        AsyncTask<String?, String?, String>() {
        override fun onPreExecute() {
            super.onPreExecute()
            pDialog = ProgressDialog(this@LoginActivity)
            pDialog!!.setMessage("Logging in..Please wait.!!")
            pDialog!!.setCancelable(false)
            pDialog!!.show()
        }

        override fun doInBackground(vararg params: String?): String? {
            strCheckLogin = "" //https://tsmithy.in/somemouat/api/
            try {
                //URL url = new URL("https://tsmithy.in/somemouat/api/LoginVer2?Name=salam_ka@yahoo.com&secret=1047109119116122626466");
                val url =
                    URL("https://tsmithy.in/somemouat/api/LoginVer2?Name=$username&secret=1047109119116122626466")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.readTimeout = 300000
                connection.connectTimeout = 300000
                connection.setRequestProperty("authkey", AppConfigSettings.auth_id)
                connection.setRequestProperty("name", "")
                connection.setRequestProperty("password", "")
                connection.setRequestProperty("debugkey", "")
                connection.setRequestProperty("remarks", "")
                connection.setRequestProperty("machineid", "saffull@gmail.com")
                // connection.setRequestProperty("machineid","salam_ka@yahoo.com");
                connection.setRequestProperty("Content-Type", "application/json")
                connection.connect()
                val responsecode = connection.responseCode
                val responseMsg = connection.responseMessage
                println("Response message is $responsecode")
                try {
                    if (responsecode == 200) {
                        val streamReader = InputStreamReader(connection.inputStream)
                        val reader = BufferedReader(streamReader)
                        val sb = StringBuilder()
                        var inputLine: String? = ""
                        while (reader.readLine().also { inputLine = it } != null) {
                            sb.append(inputLine)
                            break
                        }
                        reader.close()
                        val str = ""
                        strCheckLogin = sb.toString()
                        println("Response of Login--->$strCheckLogin")
                    } else {
//                        strErrorMsg = connection.getResponseMessage();
                        strErrorMsg = responseMsg
                        strCheckLogin = "httperror"
                    }

//                    {"Id":9,"Username":"500_9","Name":"500 User 9","Password":"","Email":"pavithrapurushan06@gmail.com","Phone":"","Active":0,"StoreId":50,"StoreCode":"500","StoreName":"SEPL-PHARMA WAREHOUSE","DeviceId":"","PasswordOtp":"","ErrorStatus":0,"Message":"Success"}
                } catch (e: IOException) {
                    e.printStackTrace()
                } finally {
                    connection.disconnect()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return strCheckLogin as String
        }

        override fun onPostExecute(s: String) {
            super.onPostExecute(s)
            if (pDialog!!.isShowing) {
                pDialog!!.dismiss()
                println(s)

                if (strCheckLogin == "" || strCheckLogin == null) {
                    Toast.makeText(this@LoginActivity, "No result", Toast.LENGTH_SHORT).show()
                } else {
                    try {
                        val gson = Gson()
                        val userPLObj = gson.fromJson(strCheckLogin, UserPL::class.java)
                        if (userPLObj.statusFlag==0){
                            val editor: SharedPreferences.Editor = sharedPreferences!!.edit()
                            editor.putString("loginResponse", strCheckLogin)
                            editor.putString("BillRemarksMWSO", "")
                            editor.apply()
                            startActivity(Intent(this@LoginActivity, Category::class.java))
                        }else{
                            tsMessages("" + userPLObj.errorMessage)
                        }

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }


    }

    private fun tsMessages(msg: String) {
        try {
            val dialog = Dialog(this@LoginActivity)
            dialog.setContentView(R.layout.ts_message_dialouge)
            //            dialog.setCanceledOnTouchOutside(false);
            dialog.setCanceledOnTouchOutside(true)
            //            dialog.setTitle("Save");
            dialog.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
            val imgBtnCloseSaveWindow =
                dialog.findViewById<ImageButton>(R.id.imgBtnClosetsMsgWindow)
            val tvMsgTodisplay = dialog.findViewById<TextView>(R.id.tvTsMessageDisplay)
            val lp = WindowManager.LayoutParams()
            lp.copyFrom(dialog.window!!.attributes)
            lp.width = WindowManager.LayoutParams.MATCH_PARENT
            lp.height = WindowManager.LayoutParams.MATCH_PARENT
            lp.gravity = Gravity.CENTER
            dialog.window!!.attributes = lp
            imgBtnCloseSaveWindow.setOnClickListener { dialog.dismiss() }
            tvMsgTodisplay.text = msg
            dialog.show()
        } catch (ex: Exception) {
            Toast.makeText(this, "" + ex, Toast.LENGTH_SHORT).show()
        }
    }
}