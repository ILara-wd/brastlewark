package com.warriorsdev.brastlewark

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.TypedValue
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.LinearLayout.LayoutParams
import android.widget.LinearLayout.LayoutParams.MATCH_PARENT
import android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.forEach
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.warriorsdev.brastlewark.databinding.ActivityGnomeBinding
import com.warriorsdev.brastlewark.Events
import com.warriorsdev.brastlewark.model.Gnome
import com.warriorsdev.brastlewark.utils.createBitmap
import com.warriorsdev.brastlewark.utils.di.Injector
import com.warriorsdev.brastlewark.utils.observeWith
import com.warriorsdev.brastlewark.utils.runOnWorkerThread
import com.warriorsdev.brastlewark.utils.setRoundBitmap
import com.warriorsdev.brastlewark.viewmodel.GnomeDetailsViewModel

/**
 * Activity that displays the details of a gnome.
 */
class GnomeActivity : AppCompatActivity() {
    companion object {
        const val GNOME_NAME_KEY = "${BuildConfig.APPLICATION_ID}.GNOME_NAME_KEY"
        private const val HAIR_COLOR_GRAY = "Gray"
        private const val HAIR_COLOR_GREEN = "Green"
        private const val HAIR_COLOR_PINK = "Pink"
        private const val HAIR_COLOR_RED = "Red"
    }

    private lateinit var viewModel: GnomeDetailsViewModel
    private lateinit var databinding: ActivityGnomeBinding
    private lateinit var factory: GnomeDetailsViewModel.Factory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestDependencies()
        observeErrors()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        databinding = DataBindingUtil.setContentView(this, R.layout.activity_gnome)
        intent?.extras?.getString(GNOME_NAME_KEY)?.let(::getGnome) ?: run { finish() }
    }

    override fun onOptionsItemSelected(item: MenuItem) = if (item.itemId == android.R.id.home) {
        super.onBackPressed()
        true
    } else {
        super.onOptionsItemSelected(item)
    }

    /**
     * Retrieves the gnome data by its name.
     * @param name Name of the gnome.
     */
    private fun getGnome(name: String) = viewModel.getGnome(name).observeWith(this, {
        databinding.apply {
            gnome = it
            gnomeHairColor.compoundDrawables.filterNotNull().run {
                if (isNotEmpty()) setGnomeHairColor(first(), it.hairColor)
            }
            getGnomePicture(it)
            it.professions.forEach {
                addTextView(it.replace(" T", ""), gnomeProfessionsList)
            }
            it.friends.forEach {
                addTextView(it, gnomeFriendsList)
            }
            setFriendsClickListeners()
            gnomeLoading.visibility = View.GONE
        }
    })

    /**
     * Creates a TextView and adds it to a parent ViewGroup.
     */
    private fun addTextView(text: String, parent: LinearLayout) {
        TextView(this).apply {
            val params = LayoutParams(MATCH_PARENT, WRAP_CONTENT)
            val textView = TextView(this@GnomeActivity).apply {
                setText(text)
                setTextColor(Color.BLACK)
                alpha = 0.7f
                textSize = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_SP,
                    6f,
                    resources.displayMetrics
                )
            }
            parent.addView(textView, params)
        }
    }

    /**
     * Sets the lick listener for any friends in the list.
     */
    private fun setFriendsClickListeners() = databinding.gnomeFriendsList.forEach {
        val textView = it as? TextView
        val name = textView?.text?.toString() ?: ""
        if (name.isNotEmpty()) {
            textView?.setOnClickListener {
                openGnomeDetailsActivity(name)
            }
        }
    }

    /**
     * Sets the gnome hair color view.
     */
    private fun setGnomeHairColor(drawable: Drawable?, hairColor: String) = drawable?.run {
        val color = getGnomeHairColor(hairColor)
        if (color != 0) {
            setTint(color)
        }
    }

    /**
     * Sets the gnome picture
     * @param gnome The gnome data.
     */
    private fun getGnomePicture(gnome: Gnome) =
        viewModel.getGnomePicture(gnome.thumbnailUrl).observeWith(this, {
            it.run(databinding.gnomeProfilePicture::setRoundBitmap)
            databinding.pictureLoading.visibility = View.GONE
        }, {
            runOnWorkerThread {
                val bitmap = createBitmap(gnome.name)
                runOnUiThread {
                    databinding.gnomeProfilePicture.setRoundBitmap(bitmap)
                    databinding.pictureLoading.visibility = View.GONE
                }
            }
        })

    /**
     * Request the dependencies from the [Injector] object.
     */
    private fun requestDependencies() {
        factory = Injector.getInstance(application).provideGnomeDetailsViewModelFactory()
        viewModel = ViewModelProvider(this, factory)[GnomeDetailsViewModel::class.java]
    }

    /**
     * Retrieves the gnome hair color to be applied as a tint to the hair color view.
     */
    private fun getGnomeHairColor(color: String) = when (color) {
        HAIR_COLOR_GRAY -> Color.GRAY
        HAIR_COLOR_GREEN -> Color.GREEN
        HAIR_COLOR_PINK -> Color.MAGENTA
        HAIR_COLOR_RED -> Color.RED
        else -> 0
    }

    /**
     * Shows the details of a gnome friend.
     */
    private fun openGnomeDetailsActivity(gnomeName: String) = startActivity(
        Intent(
            this,
            GnomeActivity::class.java
        ).putExtra(GNOME_NAME_KEY, gnomeName)
    )

    /**
     * Observes error events and displays a toast.
     */
    private fun observeErrors() = Events.errorLiveData.observe(this, Observer {
        it.run { Toast.makeText(this@GnomeActivity, localizedMessage, Toast.LENGTH_SHORT).show() }
    })
}
