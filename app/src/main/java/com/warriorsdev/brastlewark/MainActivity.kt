package com.warriorsdev.brastlewark

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.warriorsdev.brastlewark.FiltersAlertDialog
import com.warriorsdev.brastlewark.GnomeActivity
import com.warriorsdev.brastlewark.databinding.ActivitySplashBinding
import com.warriorsdev.brastlewark.databinding.ItemGnomeBinding
import com.warriorsdev.brastlewark.model.Gnome
import com.warriorsdev.brastlewark.utils.asCleanString
import com.warriorsdev.brastlewark.utils.createBitmap
import com.warriorsdev.brastlewark.utils.di.Injector
import com.warriorsdev.brastlewark.utils.observeWith
import com.warriorsdev.brastlewark.utils.runOnWorkerThread
import com.warriorsdev.brastlewark.utils.setRoundBitmap
import com.warriorsdev.brastlewark.viewmodel.GnomeListViewModel

/**
 * Activity displaying the list of gnomes. This is the app firs't screen.
 */
class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: GnomeListViewModel
    private lateinit var binding: ActivitySplashBinding
    private lateinit var factory: GnomeListViewModel.Factory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash)
        requestDependencies()
        observeErrors()
        observeGnomeList()
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setLogo(R.drawable.ic_filter_list_white_24dp)
        viewModel.getGnomeList()
    }

    /**
     * Observes error events and displays a toast.
     */
    private fun observeErrors() = Events.errorLiveData.observe(this, Observer {
        it?.run {
            Toast.makeText(this@MainActivity, localizedMessage, Toast.LENGTH_SHORT).show()
        }
    })

    /**
     * Request the dependencies from the injector.
     */
    private fun requestDependencies() {
        factory = Injector.getInstance(application).provideGnomeListViewModelFactory()
        viewModel = ViewModelProvider(this, factory)[GnomeListViewModel::class.java]
    }

    /**
     * Observes changes in the gnome list.
     */
    private fun observeGnomeList() = viewModel.gnomesLiveData.observeWith(this, {
        binding.gnomesListView.apply {
            adapter = null
            adapter = GnomeListAdapter(this@MainActivity, it)
            setOnItemClickListener { _, _, position, _ -> openGnomeDetailsActivity(it[position].name) }
        }
        binding.splashLoading.visibility = View.GONE
    })

    /**
     * Opens details of a clicked gnome from the list.
     */
    private fun openGnomeDetailsActivity(gnomeName: String) = startActivity(
        Intent(
            this,
            GnomeActivity::class.java
        ).putExtra(GnomeActivity.GNOME_NAME_KEY, gnomeName)
    )

    /**
     * Retrieves the profile picture from the Gnome.
     */
    private fun getGnomeProfilePicture(gnome: Gnome, iv: ImageView, pb: ProgressBar) =
        viewModel.getGnomePicture(gnome.thumbnailUrl).observeWith(this, {
            iv.setRoundBitmap(it)
            pb.visibility = View.GONE
        }, {
            runOnWorkerThread {
                val bitmap = createBitmap(gnome.name)
                runOnUiThread {
                    iv.setRoundBitmap(bitmap)
                    pb.visibility = View.GONE
                }
            }
        })

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_search, menu)
        setupSearchView(menu?.findItem(R.id.action_search)?.actionView as? SearchView)
        return true
    }

    /**
     * Sets the SearchView form the toolbar.
     */
    private fun setupSearchView(searchView: SearchView?) = searchView?.run {
        queryHint = getString(R.string.search_gnome_name)
        setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?) =
                viewModel.searchForGnomeByName(query ?: "").let { true }

            override fun onQueryTextChange(newText: String?) =
                viewModel.searchForGnomeByName(newText ?: "").let { true }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_filter) {
            launchFilterDialog()
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * Opens the filter dialog.
     */
    private fun launchFilterDialog() =
        FiltersAlertDialog().show(supportFragmentManager, FiltersAlertDialog::class.java.simpleName)

    /**
     * Adapter class that displays gnomes in a ListView.
     */
    inner class GnomeListAdapter(private val ctx: Context, private val gnomes: List<Gnome>) :
        ArrayAdapter<Gnome>(ctx, R.layout.item_gnome, gnomes) {

        /**
         * Tells how to draw the view in the list.
         */
        @SuppressLint("ViewHolder")
        override fun getView(position: Int, convertView: View?, parent: ViewGroup) =
            ItemGnomeBinding.inflate(LayoutInflater.from(ctx), parent, false).apply {
                val gnomeAtPosition = gnomes[position]
                gnome = gnomeAtPosition
                professions.text = gnomeAtPosition.professions.asCleanString()
                gnomeLoading.visibility = View.GONE
                getGnomeProfilePicture(gnomeAtPosition, gnomeProfilePicture, pictureProgress)
                /*Glide.with(this@MainActivity)
                    .load(gnomeAtPosition.thumbnailUrl)
                    .fitCenter()
                    .into(gnomeProfilePicture)*/
            }.root
    }
}
