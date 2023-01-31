package com.khalil.animewallpaper.ui

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.khalil.animewallpaper.*
import com.khalil.animewallpaper.db.WallpaperDatabase
import com.khalil.animewallpaper.repository.WallPaperRepository
import java.util.*


class MainActivity : AppCompatActivity() {
//Your API key: 563492ad6f9170000100000115fe048e9b354434bd96ad9d061c2f68
     lateinit var viewModel: WallpaperViewModel
    lateinit var mAdView : AdView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        createNotificationChannel()
        scheduleNotification()
        MobileAds.initialize(this) {}
        mAdView = findViewById(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)
        val wallpaperRepository=WallPaperRepository(WallpaperDatabase(this))
        val viewModelProviderFactory=WallpaperViewModelProviderFactory(application,wallpaperRepository)
        viewModel=ViewModelProvider(this@MainActivity,viewModelProviderFactory).get(WallpaperViewModel::class.java)
        val bottomNavigationView=findViewById<BottomNavigationView>(R.id.bottomNavigationView)


        val navHostFragment= supportFragmentManager.findFragmentById(R.id.wallpapersNavHostFragment) as NavHostFragment
        val navController= navHostFragment.navController


       bottomNavigationView.setupWithNavController(navController)
        bottomNavigationView.itemIconTintList=null
//       bottomNavigationView
//            .setupWithNavController(supportFragmentManager.findFragmentById(R.id.wallpapersNavHostFragment)!!
//                .findNavController())
        navController.addOnDestinationChangedListener { _, nd: NavDestination, _ ->
            // the IDs of fragments as defined in the `navigation_graph`
            if (nd.id == R.id.favotitiesFragment || nd.id == R.id.popularWallpaperListFragment
                || nd.id == R.id.animeWallpaperListFragment
            ) {
                bottomNavigationView.visibility = View.VISIBLE
            } else {
                bottomNavigationView.visibility = View.GONE
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.action_rate->{
                val appPackageName =
                    packageName // getPackageName() from Context or Activity object

                try {
                    startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("market://details?id=$appPackageName")
                        )
                    )
                } catch (anfe: ActivityNotFoundException) {
                    startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")
                        )
                    )
                }
                true
            }
           R.id.action_share->{
               val appPackage = packageName

               val sendIntent = Intent()
               sendIntent.action = Intent.ACTION_SEND
               sendIntent.putExtra(
                   Intent.EXTRA_TEXT,
                   "https://play.google.com/store/apps/details?id=$appPackage"
               )
               sendIntent.type = "text/plain"

               val shareIntent = Intent.createChooser(sendIntent,getString( R.string.app_name))
               startActivity(shareIntent)
                true
            }

            R.id.action_contact->{
                val intent = Intent(Intent.ACTION_SEND)
                intent.putExtra(Intent.EXTRA_EMAIL, getString(R.string.developer_email))
                intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.mail_title))
                intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.mail_message))
                intent.type = "message/rfc822"
                startActivity(Intent.createChooser(intent, getString(R.string.select_mail)))

                true
            }
            R.id.action_more->{
                val appPackage = packageName
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=$appPackage")
                    )
                )
                return true
            }

          else->super.onOptionsItemSelected(item)
        }
    }
    private fun scheduleNotification()
    {
        val intent = Intent(applicationContext, Notification::class.java)
//        val title = binding.titleET.text.toString()
//        val message = binding.messageET.text.toString()
        val title = "${getString(R.string.notification_title)}"
        val message = "${getString(R.string.notification_message)}"
        intent.putExtra(titleExtra, title)
        intent.putExtra(messageExtra, message)

        val pendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            notificationID,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        // val time = getTime()
        val time = getMyTime()
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            time,
            pendingIntent
        )
        // showAlert(time, title, message)
    }
    fun getMyTime():Long{
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DATE,1)
//    val day = LocalDateTime.now().dayOfMonth
//
//    Log.d("time now",day.toString())
//    Log.d("time now",calendar.time.toString())
        Log.d("time now",calendar.timeInMillis.toString())
        return calendar.timeInMillis
    }
    private fun createNotificationChannel()
    {
        val name = "Wallpaper Notification Channel"
        val desc = "Notification about new anime wallpapers"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(channelID, name, importance)
        channel.description = desc
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}