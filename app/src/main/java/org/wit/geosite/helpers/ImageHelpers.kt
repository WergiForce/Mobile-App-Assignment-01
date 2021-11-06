package org.wit.geosite.helpers

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import org.wit.geosite.R

fun showImagePicker(intentLauncher : ActivityResultLauncher<Intent>) {
    var chooseFile = Intent(Intent.ACTION_OPEN_DOCUMENT)
    chooseFile.type = "image/*"
    chooseFile = Intent.createChooser(chooseFile, R.string.select_geosite_image.toString())
    intentLauncher.launch(chooseFile)
}