package com.github.k1rakishou.kurobanewnavstacktest.controller.slide

import android.os.Bundle
import com.github.k1rakishou.kurobanewnavstacktest.base.ControllerTag
import com.github.k1rakishou.kurobanewnavstacktest.controller.base.CatalogController

class SlideCatalogController(args: Bundle? = null) : CatalogController(args) {

  override fun getControllerTag(): ControllerTag = CONTROLLER_TAG

  companion object {
    val CONTROLLER_TAG = ControllerTag("SlideCatalogControllerTag")
  }

}