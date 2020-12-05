package com.github.k1rakishou.kurobanewnavstacktest.controller.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.airbnb.epoxy.EpoxyRecyclerView
import com.github.k1rakishou.kurobanewnavstacktest.R
import com.github.k1rakishou.kurobanewnavstacktest.base.BaseController

abstract class ThreadController(args: Bundle? = null) : BaseController(args) {
  protected lateinit var recyclerView: EpoxyRecyclerView

  final override fun instantiateView(
    inflater: LayoutInflater,
    container: ViewGroup,
    savedViewState: Bundle?
  ): View {
    return inflater.inflateView(R.layout.controller_thread, container) {
      recyclerView = findViewById(R.id.controller_thread_epoxy_recycler_view)
    }
  }

}