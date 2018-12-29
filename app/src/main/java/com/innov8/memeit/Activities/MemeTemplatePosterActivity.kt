package com.innov8.memeit.Activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.adroitandroid.chipcloud.ChipCloud
import com.adroitandroid.chipcloud.ChipListener
import com.afollestad.materialdialogs.MaterialDialog
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.imagepipeline.request.ImageRequest
import com.innov8.memeit.R
import com.innov8.memeit.Utils.text
import com.innov8.memeit.Workers.startTemplateUploadWork
import com.innov8.memeit.commons.models.TypefaceManager
import com.memeit.backend.models.MemeTemplate
import com.memeit.backend.models.SavedGifMemeTemplateProperty
import com.memeit.backend.models.SavedImageMemeTemplateProperty
import com.memeit.backend.models.SavedMemeTemplateProperty
import kotlinx.android.synthetic.main.activity_meme_template_poster.*
import java.io.File

class MemeTemplatePosterActivity : AppCompatActivity() {
    companion object {
        const val PARAM_TEMPLATE_JSON = "json"
        val chipCategories = arrayOf("Advice", "Animal", "Cartoon", "Celebrity", "Rage", "Other")
    }

    private val templateProperty by lazy {
        SavedMemeTemplateProperty.readFromString(intent.getStringExtra(PARAM_TEMPLATE_JSON)!!)
    }

    private var selectedIndex = chipCategories.lastIndex
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meme_template_poster)
        setSupportActionBar(toolbar)
        handleIntent()
        category_chips.addChips(chipCategories)
        category_chips.setTypeface(TypefaceManager.byName("Avenir"))
        category_chips.setSelectedChip(chipCategories.lastIndex)
        category_chips.setMode(ChipCloud.Mode.REQUIRED)

        category_chips.setChipListener(object : ChipListener {
            override fun chipDeselected(p0: Int) {

            }

            override fun chipSelected(p0: Int) {
                selectedIndex = p0
            }
        })
    }


    private fun handleIntent() {

        when (templateProperty) {
            is SavedGifMemeTemplateProperty -> {
                meme_image_view.controller = Fresco.newDraweeControllerBuilder()
                        .setImageRequest(ImageRequest.fromFile(File(templateProperty.images[0])))
                        .setAutoPlayAnimations(true)
                        .setControllerListener(MyControllerListener())
                        .build()
            }
            is SavedImageMemeTemplateProperty -> {
                meme_image_view.controller = Fresco.newDraweeControllerBuilder()
                        .setImageRequest(ImageRequest.fromFile(File(templateProperty.previewImageUrl)))
                        .setAutoPlayAnimations(true)
                        .setControllerListener(MyControllerListener())
                        .build()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_meme_poster, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_post -> upload()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun upload() {
        val template = MemeTemplate(
                label = template_label_field.text,
                category = chipCategories[selectedIndex],
                memeType = templateProperty.getType().name,
                tags = description.text.toString().split(" ")
                , memeTemplateProperty = templateProperty
        )

        startTemplateUploadWork(this, template)

        MaterialDialog.Builder(this).title("Your template is getting uploaded")
                .content("You will be notified when its done.")
                .positiveText("Browse Memes")
                .negativeText("Make Another Template")
                .onPositive { _, _ ->
                    finish()
                    startActivity(Intent(this, MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    })
                }
                .onNegative { _, _ ->
                    finish()
                }
                .show()
    }
}
