package com.app.photobook.frag.Portfolio

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.photobook.R
import com.app.photobook.adapter.portfolio.VideoAdapter
import com.app.photobook.model.User
import com.app.photobook.model.Video
import com.app.photobook.tools.Constants
import com.app.photobook.tools.DividerItemDecoration
import com.app.photobook.tools.MyPrefManager
import com.app.photobook.tools.Utils
import kotlinx.android.synthetic.main.frag_portfolio_video.view.*
import kotlinx.android.synthetic.main.view_empty.view.*
import java.util.*

class FragVideos : Fragment() {

    internal lateinit var albumAdapter: VideoAdapter
    internal var videos = ArrayList<Video>()

    internal lateinit var myPrefManager: MyPrefManager
    internal var user: User? = null
    internal lateinit var myView: View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        myView = inflater.inflate(R.layout.frag_portfolio_video, container, false)

        setBroadCast()
        myPrefManager = MyPrefManager(activity)
        user = myPrefManager.userDetails

        //myView.tvEmptyMsg.typeface = CustomApp.getFontNormal()

        val gridLayoutManager = LinearLayoutManager(context)
        myView.recyclerView.layoutManager = gridLayoutManager
        myView.recyclerView.setHasFixedSize(true)

        return myView
    }

    private fun updateList() {

        if (!Utils.isOnline(activity)) {
            ShowEmptyMessage(true, false, context.getString(R.string.message_no_internet),
                    ContextCompat.getDrawable(context, R.drawable.ic_svg_wifi))
            return
        }

        if (videos.isEmpty()) {
            ShowEmptyMessage(true, false, context.getString(R.string.message_empty),
                    ContextCompat.getDrawable(context, R.drawable.ic_svg_data_no))
        } else {
            albumAdapter = VideoAdapter(context, videos)
            myView.recyclerView.adapter = albumAdapter
            myView.recyclerView.addItemDecoration(DividerItemDecoration(context))

            ShowEmptyMessage(false, false, getString(R.string.message_empty),
                    ContextCompat.getDrawable(context, R.drawable.ic_svg_data_no))
        }
    }

    private fun ShowEmptyMessage(value: Boolean, showRetry: Boolean, msg: String, img: Drawable) {

        if (value) {
            myView.frmEmpty!!.visibility = View.VISIBLE
            myView.tvEmptyMsg!!.text = msg
            myView.recyclerView!!.visibility = View.GONE
            myView.ivWifi!!.setImageDrawable(img)
            if (showRetry)
                myView.btnRetry!!.visibility = View.VISIBLE
            else
                myView.btnRetry!!.visibility = View.GONE

        } else {
            myView.recyclerView!!.visibility = View.VISIBLE
            myView.frmEmpty!!.visibility = View.GONE
        }
    }


    fun setBroadCast() {
        var intentFilter = IntentFilter(Constants.ACTION_UPDATE_VIDEO_PORTFOLIO)
        LocalBroadcastManager.getInstance(context)
                .registerReceiver(myBroadCast, intentFilter)
    }

    var myBroadCast = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {

            if (intent != null) {
                if (intent.action == Constants.ACTION_UPDATE_VIDEO_PORTFOLIO) {
                    if (intent.hasExtra("videos")) {
                        videos = intent.getParcelableArrayListExtra("videos")
                    }
                }
            }
            updateList()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(context)
                .unregisterReceiver(myBroadCast)
    }


    companion object {
        private val TAG = FragVideos::class.java.simpleName
    }
}