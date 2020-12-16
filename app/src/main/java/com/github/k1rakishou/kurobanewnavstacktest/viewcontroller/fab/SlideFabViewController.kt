package com.github.k1rakishou.kurobanewnavstacktest.viewcontroller.fab

import com.github.k1rakishou.kurobanewnavstacktest.controller.ControllerType
import com.github.k1rakishou.kurobanewnavstacktest.utils.BackgroundUtils
import com.github.k1rakishou.kurobanewnavstacktest.widget.bottom_panel.KurobaBottomPanel
import com.github.k1rakishou.kurobanewnavstacktest.widget.fab.SlideKurobaFloatingActionButton

class SlideFabViewController : FabViewController {
  private lateinit var fab: SlideKurobaFloatingActionButton
  private var state = State()

  init {
    state.searchToolbarShown[ControllerType.Catalog] = false
    state.searchToolbarShown[ControllerType.Thread] = false

    state.controllerFullyLoaded[ControllerType.Catalog] = false
    state.controllerFullyLoaded[ControllerType.Thread] = false
  }

  fun setFab(fab: SlideKurobaFloatingActionButton) {
    this.fab = fab
  }

  override fun onBottomPanelInitialized(controllerType: ControllerType) {
    BackgroundUtils.ensureMainThread()

    if (state.bottomPanelInitialized) {
      return
    }

    state.bottomPanelInitialized = true
    onStateChanged()
  }

  override fun onBottomPanelStateChanged(controllerType: ControllerType, newState: KurobaBottomPanel.State) {
    BackgroundUtils.ensureMainThread()

    val prevState = state.bottomPanelState
    if (prevState == newState) {
      return
    }

    state.bottomPanelState = newState
    onStateChanged()
  }

  override fun onControllerStateChanged(controllerType: ControllerType, fullyLoaded: Boolean) {
    BackgroundUtils.ensureMainThread()

    val prev = state.controllerFullyLoaded[controllerType]
    if (prev == fullyLoaded) {
      return
    }

    state.controllerFullyLoaded[controllerType] = fullyLoaded
    onStateChanged()
  }

  override fun onSearchToolbarShownOrHidden(controllerType: ControllerType, shown: Boolean) {
    BackgroundUtils.ensureMainThread()

    val prev = state.searchToolbarShown[controllerType]
    if (prev == shown) {
      return
    }

    state.searchToolbarShown[controllerType] = shown
    onStateChanged()
  }

  fun onControllerFocused(controllerType: ControllerType) {
    BackgroundUtils.ensureMainThread()

    if (state.currentControllerType == controllerType) {
      return
    }

    state.currentControllerType = controllerType
    onStateChanged()
  }

  private fun onStateChanged() {
    BackgroundUtils.ensureMainThread()

    val currentControllerType = state.currentControllerType
      ?: return

    if (!state.bottomPanelInitialized) {
      return
    }

    val bottomPanelState = state.bottomPanelState

    val searchToolbarShown = state.searchToolbarShown[currentControllerType]
      ?: false
    val isBottomPanelStateNotOk = bottomPanelState == KurobaBottomPanel.State.Uninitialized
      || bottomPanelState == KurobaBottomPanel.State.SelectionPanel
      || bottomPanelState == KurobaBottomPanel.State.ReplyLayoutPanel

    check(::fab.isInitialized) { "fab is not initialized" }

    if (searchToolbarShown || isBottomPanelStateNotOk) {
      fab.hideFab(lock = true)
    } else {
      fab.showFab(lock = false)
    }
  }

  class State(
    var searchToolbarShown: MutableMap<ControllerType, Boolean> = mutableMapOf(),
    var controllerFullyLoaded: MutableMap<ControllerType, Boolean> = mutableMapOf(),
    var currentControllerType: ControllerType? = null,
    var bottomPanelInitialized: Boolean = false,
    var bottomPanelState: KurobaBottomPanel.State = KurobaBottomPanel.State.Uninitialized
  )

}