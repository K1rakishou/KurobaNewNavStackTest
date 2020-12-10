package com.github.k1rakishou.kurobanewnavstacktest.widget.toolbar

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import com.github.k1rakishou.kurobanewnavstacktest.R
import com.github.k1rakishou.kurobanewnavstacktest.controller.ControllerType
import com.github.k1rakishou.kurobanewnavstacktest.utils.setAlphaFast
import com.github.k1rakishou.kurobanewnavstacktest.utils.setOnApplyWindowInsetsListenerAndDoRequest
import com.github.k1rakishou.kurobanewnavstacktest.utils.setVisibilityFast

class SlideToolbar @JvmOverloads constructor(
  context: Context,
  attributeSet: AttributeSet? = null,
  attrDefStyle: Int = 0
) : FrameLayout(context, attributeSet, attrDefStyle), ToolbarContract {
  private val actualCatalogToolbar: KurobaToolbar
  private val actualThreadToolbar: KurobaToolbar

  private var transitioningIntoCatalogToolbar: Boolean? = null
  private var initialToolbarShown = false
  private var catalogToolbarVisible: Boolean = false
  private var initialized: Boolean = false

  init {
    inflate(context, R.layout.widget_slide_toolbar, this)

    val slideToolbarRoot = findViewById<FrameLayout>(R.id.slide_toolbar_root)
    actualCatalogToolbar = findViewById(R.id.catalog_toolbar)
    actualThreadToolbar = findViewById(R.id.thread_toolbar)

    val toolbarHeight = context.resources.getDimension(R.dimen.toolbar_height).toInt()

    slideToolbarRoot.setOnApplyWindowInsetsListenerAndDoRequest { v, insets ->
      v.updateLayoutParams<FrameLayout.LayoutParams> {
        height = toolbarHeight + insets.systemWindowInsetTop
      }
      v.updatePadding(top = insets.systemWindowInsetTop)

      return@setOnApplyWindowInsetsListenerAndDoRequest insets
    }
  }

  fun init() {
    check(!this.initialized) { "Double initialization!" }
    this.initialized = true

    actualCatalogToolbar.init(KurobaToolbarType.Catalog)
    actualThreadToolbar.init(KurobaToolbarType.Thread)
  }

  override fun onBackPressed(): Boolean {
    if (catalogToolbarVisible) {
      return actualCatalogToolbar.onBackPressed()
    }

    return actualThreadToolbar.onBackPressed()
  }

  override fun collapsableView(): View {
    return this
  }

  override fun setTitle(controllerType: ControllerType, title: String) {
    when (controllerType) {
      ControllerType.Catalog -> {
        actualCatalogToolbar.newState(ToolbarStateUpdate.Catalog.UpdateTitle(title))
      }
      ControllerType.Thread -> {
        actualThreadToolbar.newState(ToolbarStateUpdate.Thread.UpdateTitle(title))
      }
    }
  }

  override fun setSubTitle(subtitle: String) {
    actualCatalogToolbar.newState(ToolbarStateUpdate.Catalog.UpdateSubTitle(subtitle))
  }

  override fun setToolbarVisibility(visibility: Int) {
    check(visibility == View.VISIBLE || visibility == View.INVISIBLE || visibility == View.GONE) {
      "Bad visibility parameter: $visibility"
    }

    this.visibility = visibility
  }

  fun onBeforeSliding(transitioningIntoCatalogToolbar: Boolean) {
    check(this.transitioningIntoCatalogToolbar == null) { "transitioningIntoCatalogToolbar != null" }
    this.transitioningIntoCatalogToolbar = transitioningIntoCatalogToolbar

    actualCatalogToolbar.setVisibilityFast(View.VISIBLE)
    actualThreadToolbar.setVisibilityFast(View.VISIBLE)

    if (transitioningIntoCatalogToolbar) {
      actualCatalogToolbar.setAlphaFast(0f)
      actualThreadToolbar.setAlphaFast(1f)
    } else {
      actualCatalogToolbar.setAlphaFast(1f)
      actualThreadToolbar.setAlphaFast(0f)
    }
  }

  fun onSliding(offset: Float) {
    if (transitioningIntoCatalogToolbar == null) {
      return
    }

    actualCatalogToolbar.setAlphaFast(offset)
    actualThreadToolbar.setAlphaFast(1f - offset)

    actualCatalogToolbar.newState(ToolbarStateUpdate.Catalog.UpdateSlideProgress(offset))
    actualThreadToolbar.newState(ToolbarStateUpdate.Thread.UpdateSlideProgress(offset))
  }

  fun onAfterSliding(becameCatalogToolbar: Boolean) {
    if (transitioningIntoCatalogToolbar == null) {
      return
    }

    if (transitioningIntoCatalogToolbar != becameCatalogToolbar) {
      // The sliding was canceled, we need to revert everything back
      if (becameCatalogToolbar) {
        actualCatalogToolbar.setVisibilityFast(View.VISIBLE)
        actualCatalogToolbar.setAlphaFast(1f)

        actualThreadToolbar.setVisibilityFast(View.GONE)
        actualThreadToolbar.setAlphaFast(0f)
      } else {
        actualCatalogToolbar.setVisibilityFast(View.GONE)
        actualCatalogToolbar.setAlphaFast(0f)

        actualThreadToolbar.setVisibilityFast(View.VISIBLE)
        actualThreadToolbar.setAlphaFast(1f)
      }

      transitioningIntoCatalogToolbar = null
      return
    }

    if (becameCatalogToolbar) {
      catalogToolbarVisible = true

      actualCatalogToolbar.setAlphaFast(1f)
      actualCatalogToolbar.setVisibilityFast(View.VISIBLE)

      actualThreadToolbar.setAlphaFast(0f)
      actualThreadToolbar.setVisibilityFast(View.GONE)
    } else {
      catalogToolbarVisible = false

      actualCatalogToolbar.setAlphaFast(0f)
      actualCatalogToolbar.setVisibilityFast(View.GONE)

      actualThreadToolbar.setAlphaFast(1f)
      actualThreadToolbar.setVisibilityFast(View.VISIBLE)
    }

    transitioningIntoCatalogToolbar = null
  }

  fun onControllerGainedFocus(isCatalogController: Boolean) {
    if (!initialToolbarShown) {
      initialToolbarShown = true
      catalogToolbarVisible = isCatalogController

      showToolbarInitial(isCatalogController)
      return
    }
  }

  private fun showToolbarInitial(isCatalogController: Boolean) {
    if (isCatalogController) {
      actualCatalogToolbar.setVisibilityFast(View.VISIBLE)
      actualThreadToolbar.setVisibilityFast(View.GONE)
    } else {
      actualCatalogToolbar.setVisibilityFast(View.GONE)
      actualThreadToolbar.setVisibilityFast(View.VISIBLE)
    }
  }

}