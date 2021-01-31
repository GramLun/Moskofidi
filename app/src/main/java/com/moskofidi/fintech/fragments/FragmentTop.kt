package com.moskofidi.fintech.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.Result
import com.moskofidi.fintech.DevApplication
import com.moskofidi.fintech.R
import com.moskofidi.fintech.data.PageData
import com.moskofidi.fintech.data.ResultList
import kotlinx.android.synthetic.main.fragment_top.*

class FragmentTop : Fragment() {
    private val pageData = PageData(0, "top", 0)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_top, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        loadGif()

        swipeRefreshTop.setOnRefreshListener {
            swipeRefreshTop.isRefreshing = true
            imgViewTop.setImageDrawable(null)
            textViewTop.text = ""
            progressBarTop.visibility = View.VISIBLE

            loadGif()
        }

        swipeRefreshTop.setColorSchemeResources(
            android.R.color.black
        )
    }

    fun noData() {
        progressBarTop.visibility = View.INVISIBLE
        gifContainerTop.visibility = View.INVISIBLE

        btnNextTop.visibility = View.INVISIBLE
        btnPrevTop.visibility = View.INVISIBLE

        requireActivity().runOnUiThread {
            Glide.with(this).load(R.mipmap.baseline_image_not_supported_black_48)
                .into(imgErrorTop)
            textErrorTop.text = resources.getString(R.string.noData)
            textErrorTop.visibility = View.VISIBLE
            imgErrorTop.visibility = View.VISIBLE
        }
    }

    fun noConnection() {
        progressBarTop.visibility = View.INVISIBLE
        gifContainerTop.visibility = View.INVISIBLE

        btnNextTop.visibility = View.INVISIBLE
        btnPrevTop.visibility = View.INVISIBLE

        requireActivity().runOnUiThread {
            Glide.with(this).load(R.mipmap.baseline_wifi_off_black_48)
                .into(imgErrorTop)
            textErrorTop.text = resources.getString(R.string.noConnection)
            textErrorTop.visibility = View.VISIBLE
            imgErrorTop.visibility = View.VISIBLE
        }
    }

    fun getGif(pageData: PageData) {
        var jsonIn = ""
        val url = "https://developerslife.ru/${pageData.topic}/${pageData.page}?json=true"

        url.httpGet().responseString { _, _, result ->
            when (result) {
                is Result.Success -> {
                    jsonIn = result.get()
                    val publicationList = gson.fromJson(jsonIn, ResultList::class.java)
                    if (publicationList.result.isEmpty()) {
                        noData()
                    } else {
                        requireActivity().runOnUiThread {
                            Glide.with(this).asGif()
                                .load(publicationList.result[pageData.postsCount].gifURL)
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .listener(GifRequestListener())
                                .into(object : SimpleTarget<GifDrawable>() {
                                    override fun onResourceReady(
                                        resource: GifDrawable,
                                        transition: Transition<in GifDrawable>?
                                    ) {
                                        imgViewTop.setImageDrawable(resource)
                                        resource.start()
                                    }
                                })
                            textViewTop.text =
                                publicationList.result[pageData.postsCount].description
                        }
                    }
                }
                is Result.Failure -> {
                    if (DevApplication.getNetworkState()) {
                        noData()
                    } else {
                        noConnection()
                    }
                }
            }
        }
    }

    fun loadGif() {
        if (DevApplication.getNetworkState()) {
            btnNextTop.visibility = View.VISIBLE
            btnPrevTop.visibility = View.VISIBLE
            gifContainerTop.visibility = View.VISIBLE

            if (pageData.postsCount == 0 && pageData.page == 0) {
                btnPrevTop.isEnabled = false
            }
            getGif(pageData)
            swipeRefreshTop.isRefreshing = false

            btnPrevTop.setOnClickListener {
                imgViewTop.setImageDrawable(null)
                textViewTop.text = ""
                progressBarTop.visibility = View.VISIBLE

                pageData.postsCount--
                if (pageData.postsCount > 0) {
                    getGif(pageData)
                } else {
                    if (pageData.postsCount == 0) {
                        if (pageData.page == 0) {
                            getGif(pageData)
                            btnPrevTop.isEnabled = false
                        } else {
                            getGif(pageData)
                        }
                    } else {
                        pageData.page--
                        pageData.postsCount = 4
                        getGif(pageData)
                    }
                }
            }

            btnNextTop.setOnClickListener {
                imgViewTop.setImageDrawable(null)
                textViewTop.text = ""
                progressBarTop.visibility = View.VISIBLE

                btnPrevTop.isEnabled = true

                pageData.postsCount++
                if (pageData.postsCount <= 4) {
                    getGif(pageData)
                } else {
                    pageData.postsCount = 0
                    pageData.page++
                    getGif(pageData)
                }
            }
        } else {
            noConnection()
            swipeRefreshTop.isRefreshing = false
        }
    }

    inner class GifRequestListener() : RequestListener<GifDrawable> {
        override fun onLoadFailed(
            e: GlideException?,
            model: Any?,
            target: Target<GifDrawable>?,
            isFirstResource: Boolean
        ): Boolean {
            return false
        }

        override fun onResourceReady(
            resource: GifDrawable?,
            model: Any?,
            target: Target<GifDrawable>?,
            dataSource: DataSource?,
            isFirstResource: Boolean
        ): Boolean {
            progressBarTop.visibility = View.GONE
            return false
        }
    }
}