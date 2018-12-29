package com.innov8.memeit

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.google.android.gms.appinvite.AppInviteInvitation
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.innov8.memegenerator.MemeEditorActivity
import com.innov8.memeit.Activities.*
import com.innov8.memeit.Activities.MainActivity
import com.innov8.memeit.Adapters.MemeAdapters.MemeAdapter
import com.innov8.memeit.CustomViews.DrawableBadge
import com.innov8.memeit.Fragments.MemeListFragment
import com.innov8.memeit.Fragments.ProfileFragment
import com.innov8.memeit.Loaders.FavoriteMemeLoader
import com.innov8.memeit.Loaders.HomeMemeLoader
import com.innov8.memeit.Loaders.TrendingMemeLoader
import com.memeit.backend.MemeItClient
import com.memeit.backend.MemeItClient.context
import com.memeit.backend.MemeItUsers
import com.memeit.backend.call
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : MainActivity() {
    override fun onFeedbackMenu() {
        startActivity(Intent(this@MainActivity, FeedbackActivity::class.java))
    }

}