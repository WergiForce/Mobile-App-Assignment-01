package org.wit.geosite.activities


import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import org.wit.geosite.R
import org.wit.geosite.databinding.ActivityGeositeBinding
import org.wit.geosite.helpers.showImagePicker
import org.wit.geosite.main.MainApp
import org.wit.geosite.models.GeositeModel
import org.wit.geosite.models.Location
import timber.log.Timber
import timber.log.Timber.i

class GeositeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGeositeBinding
    private lateinit var imageIntentLauncher : ActivityResultLauncher<Intent>
    private lateinit var mapIntentLauncher : ActivityResultLauncher<Intent>
    var geosite = GeositeModel()
    lateinit var app : MainApp
    var image: Uri = Uri.EMPTY
    // var location = Location(53.070983524081065, -9.354122575168192, 15f)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var edit = false
        binding = ActivityGeositeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbarAdd.title = title
        setSupportActionBar(binding.toolbarAdd)

        app = application as MainApp
        i("Geosite Activity started...")

        if (intent.hasExtra("geosite_edit")) {
            edit = true
            geosite = intent.extras?.getParcelable("geosite_edit")!!
            binding.geositeTitle.setText(geosite.title)
            binding.description.setText(geosite.description)
            binding.btnAdd.setText(R.string.save_geosite)
            Picasso.get()
                .load(geosite.image)
                .into(binding.geositeImage)
            if (geosite.image != Uri.EMPTY) {
                binding.chooseImage.setText(R.string.change_geosite_image)
            }
        }

        binding.btnAdd.setOnClickListener() {
            geosite.title = binding.geositeTitle.text.toString()
            geosite.description = binding.description.text.toString()
            if (geosite.title.isEmpty()) {
                Snackbar.make(it,R.string.enter_geosite_title, Snackbar.LENGTH_LONG)
                    .show()
            } else {
                if (edit) {
                    app.geosites.update(geosite.copy())
                } else {
                    app.geosites.create(geosite.copy())
                }
            }
            setResult(RESULT_OK)
            finish()
        }

        binding.chooseImage.setOnClickListener {
            showImagePicker(imageIntentLauncher)
        }

        registerImagePickerCallback()

        binding.geositeLocation.setOnClickListener {
            val location = Location(53.070983524081065, -9.354122575168192, 15f)
            if (geosite.zoom != 0f) {
                location.lat =  geosite.lat
                location.lng = geosite.lng
                location.zoom = geosite.zoom
            }
            val launcherIntent = Intent(this, MapActivity::class.java)
                .putExtra("location", location)
            mapIntentLauncher.launch(launcherIntent)
        }

        registerMapCallback()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_geosite, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_cancel -> { finish() }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun registerImagePickerCallback() {
        imageIntentLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult())
            { result ->
                when(result.resultCode){
                    RESULT_OK -> {
                        if (result.data != null) {
                            i("Got Result ${result.data!!.data}")
                            geosite.image = result.data!!.data!!
                            Picasso.get()
                                .load(geosite.image)
                                .into(binding.geositeImage)
                            binding.chooseImage.setText(R.string.change_geosite_image)
                        } // end of if
                    }
                    RESULT_CANCELED -> { } else -> { }
                }
            }
    }

    private fun registerMapCallback() {
        mapIntentLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult())
            { result ->
                when (result.resultCode) {
                    RESULT_OK -> {
                        if (result.data != null) {
                            i("Got Location ${result.data.toString()}")
                            val location = result.data!!.extras?.getParcelable<Location>("location")!!
                            i("Location == $location")
                            geosite.lat = location.lat
                            geosite.lng = location.lng
                            geosite.zoom = location.zoom
                        } // end of if
                    }
                    RESULT_CANCELED -> { } else -> { }
                }
            }
    }
}