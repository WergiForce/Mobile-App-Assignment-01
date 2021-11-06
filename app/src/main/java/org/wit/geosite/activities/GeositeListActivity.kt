package org.wit.geosite.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import org.wit.geosite.R
import org.wit.geosite.adapters.GeositeAdapter
import org.wit.geosite.adapters.GeositeListener
import org.wit.geosite.databinding.ActivityGeositeListBinding
import org.wit.geosite.main.MainApp
import org.wit.geosite.models.GeositeModel

class GeositeListActivity : AppCompatActivity(), GeositeListener {

    lateinit var app: MainApp
    private lateinit var binding: ActivityGeositeListBinding
    private lateinit var refreshIntentLauncher : ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGeositeListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbar.title = title
        setSupportActionBar(binding.toolbar)

        app = application as MainApp

        val layoutManager = LinearLayoutManager(this)
        binding.recyclerView.layoutManager = layoutManager
        loadGeosites()

        registerRefreshCallback()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_add -> {
                val launcherIntent = Intent(this, GeositeActivity::class.java)
                refreshIntentLauncher.launch(launcherIntent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onGeositeClick(geosite: GeositeModel) {
        val launcherIntent = Intent(this, GeositeActivity::class.java)
        launcherIntent.putExtra("geosite_edit", geosite)
        refreshIntentLauncher.launch(launcherIntent)
    }

    private fun registerRefreshCallback() {
        refreshIntentLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult())
            { loadGeosites() }
    }

    private fun loadGeosites() {
        showGeosites(app.geosites.findAll())
    }

    fun showGeosites (geosites: List<GeositeModel>) {
        binding.recyclerView.adapter = GeositeAdapter(geosites, this)
        binding.recyclerView.adapter?.notifyDataSetChanged()
    }
}