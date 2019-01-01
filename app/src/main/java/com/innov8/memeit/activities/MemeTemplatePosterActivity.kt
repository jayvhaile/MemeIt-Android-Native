package com.innov8.memeit.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.adroitandroid.chipcloud.ChipCloud
import com.afollestad.materialdialogs.MaterialDialog
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.imagepipeline.request.ImageRequest
import com.innov8.memeit.R
import com.innov8.memeit.commons.models.TypefaceManager
import com.innov8.memeit.commons.toast
import com.innov8.memeit.utils.onChipSelected
import com.innov8.memeit.workers.startTemplateUploadWork
import com.memeit.backend.models.MemeTemplate
import com.memeit.backend.models.SavedGifMemeTemplateProperty
import com.memeit.backend.models.SavedImageMemeTemplateProperty
import com.memeit.backend.models.SavedMemeTemplateProperty
import kotlinx.android.synthetic.main.activity_meme_template_poster.*
import java.io.File

class MemeTemplatePosterActivity : AppCompatActivity() {
    companion object {
        const val PARAM_TEMPLATE_JSON = "json"

    }

    private val templateProperty by lazy {
        SavedMemeTemplateProperty.readFromString(intent.getStringExtra(PARAM_TEMPLATE_JSON)!!)
    }
    private val chipCategories by lazy {
        resources.getStringArray(R.array.template_categories)
    }
    var selectedIndex = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meme_template_poster)
        setSupportActionBar(toolbar)
        handleIntent()
        category_chips.setTypeface(TypefaceManager.byName("Avenir"))
        category_chips.setMode(ChipCloud.Mode.REQUIRED)
        category_chips.onChipSelected { selectedIndex = it }
        category_chips.setSelectedChip(chipCategories.lastIndex)
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
        val label = label_field.text?.toString()
        if (label.isNullOrBlank()) {
            toast("Label is Required")
            return
        }
        val template = MemeTemplate(
                label = label,
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
                    MainActivity.start(this) {
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    }
                }
                .onNegative { _, _ ->
                    finish()
                }
                .show()
    }
}
