package com.bignerdranch.android.myfirstapp

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import com.bignerdranch.android.myfirstapp.db.MyDbManager
import com.bignerdranch.android.myfirstapp.db.MyIntentConstants
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EditActivity : AppCompatActivity() {

    var id = 0
    var isEditState = false
    val imageRequestCode = 10
    var tempImageUri = "empty"
    val myDbManager = MyDbManager(this)
    lateinit var fbAddImage : FloatingActionButton
    lateinit var fbSave : FloatingActionButton
    private lateinit var mainImageLayout : ConstraintLayout
    lateinit var imMainImage : ImageView
    lateinit var editTitle : EditText
    lateinit var editDescription : EditText
    lateinit var imButtonDeleteImage : ImageButton
    lateinit var imButtonEditImage :ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_activity)
        getMyIntents()

        fbAddImage = findViewById(R.id.fb_add_image)
        fbSave = findViewById(R.id.fbSave)
        mainImageLayout = findViewById(R.id.mainImageLayout)
        imMainImage = findViewById(R.id.imMainImage)
        editTitle = findViewById(R.id.edTitle)
        editDescription = findViewById(R.id.edDescription)
        imButtonDeleteImage = findViewById(R.id.imButtonDeleteImage)
        imButtonEditImage = findViewById(R.id.imButtonEditImage)
    }

    override fun onDestroy() {
        super.onDestroy()
        myDbManager.closeDb()
    }

    override fun onResume() {
        super.onResume()
        myDbManager.openDb()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == imageRequestCode){

            imMainImage.setImageURI(data?.data)
            tempImageUri = data?.data.toString()
            contentResolver.takePersistableUriPermission(data?.data!!, Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
    }

    fun onClickAddImage(view: View) {
        mainImageLayout.visibility = View.VISIBLE
        fbAddImage.visibility = View.GONE
    }

    fun onClickDeleteImage(view: View) {
        mainImageLayout.visibility = View.GONE
        fbAddImage.visibility = View.VISIBLE
    }

    fun onClickChooseImage(view: View) {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.type = "image/*"
        startActivityForResult(intent, imageRequestCode)
    }

    fun onClickSave(view: View) {

        val myTitle = editTitle.text.toString()
        val myDesc = editDescription.text.toString()

        if (myTitle != "" && myDesc != "") {

            CoroutineScope(Dispatchers.Main).launch {

                if (isEditState){
                    myDbManager.updateItem(myTitle, myDesc, tempImageUri, id)
                } else {
                    myDbManager.insertToDb(myTitle, myDesc, tempImageUri)
                }
                finish()
            }

        }
    }

    fun getMyIntents(){

        val i = intent

        if (i != null) {
            if (i.getStringExtra((MyIntentConstants.I_TITLE_KEY)) != null) {

                isEditState = true
                fbAddImage.visibility = View.GONE
                editTitle.setText(i.getStringExtra(MyIntentConstants.I_TITLE_KEY))
                editDescription.setText(i.getStringExtra(MyIntentConstants.I_DESK_KEY))
                id = i.getIntExtra(MyIntentConstants.I_ID_KEY, 0)
                tempImageUri = i.getStringExtra(MyIntentConstants.I_URI_KEY)!!
                if (i.getStringExtra(MyIntentConstants.I_URI_KEY) != "empty") {

                    mainImageLayout.visibility = View.VISIBLE
                    imMainImage.setImageURI(Uri.parse(i.getStringExtra(MyIntentConstants.I_URI_KEY)))
                    imButtonDeleteImage
                    imButtonEditImage
                }
            }
        }


    }
}