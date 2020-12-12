package com.github.k1rakishou.kurobanewnavstacktest.controller.split

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.doOnPreDraw
import androidx.recyclerview.widget.RecyclerView
import com.bluelinelabs.conductor.RouterTransaction
import com.github.k1rakishou.kurobanewnavstacktest.R
import com.github.k1rakishou.kurobanewnavstacktest.base.BaseController
import com.github.k1rakishou.kurobanewnavstacktest.base.ControllerTag
import com.github.k1rakishou.kurobanewnavstacktest.controller.ControllerType
import com.github.k1rakishou.kurobanewnavstacktest.controller.RecyclerViewProvider
import com.github.k1rakishou.kurobanewnavstacktest.controller.base.*
import com.github.k1rakishou.kurobanewnavstacktest.core.CollapsingViewsHolder
import com.github.k1rakishou.kurobanewnavstacktest.data.BoardDescriptor
import com.github.k1rakishou.kurobanewnavstacktest.data.ThreadDescriptor
import com.github.k1rakishou.kurobanewnavstacktest.utils.getBehaviorExt
import com.github.k1rakishou.kurobanewnavstacktest.utils.setBehaviorExt
import com.github.k1rakishou.kurobanewnavstacktest.viewcontroller.ViewScreenAttachSide
import com.github.k1rakishou.kurobanewnavstacktest.widget.fab.KurobaFloatingActionButton
import com.github.k1rakishou.kurobanewnavstacktest.widget.behavior.CatalogFabBehavior
import timber.log.Timber

@SuppressLint("TimberTagLength")
class SplitUiElementsController(
  args: Bundle? = null
) : BaseUiElementsController(args),
  UiElementsControllerCallbacks,
  ChanNavigationContract,
  RecyclerViewProvider {
  private lateinit var catalogFab: KurobaFloatingActionButton
  private lateinit var splitControllerCatalogControllerContainer: FrameLayout
  private lateinit var threadNavigationContract: ThreadNavigationContract

  private val collapsingViewsHolder = CollapsingViewsHolder()

  fun threadNavigationContract(threadNavigationContract: ThreadNavigationContract) {
    this.threadNavigationContract = threadNavigationContract
  }

  override fun instantiateView(
    inflater: LayoutInflater,
    container: ViewGroup,
    savedViewState: Bundle?
  ): View {
    return inflater.inflateView(R.layout.controller_split_catalog_ui_elements, container) {
      splitControllerCatalogControllerContainer = findViewById(R.id.split_controller_catalog_controller_container)
      bottomNavView = findViewById(R.id.split_controller_bottom_nav_view)
      toolbarContainer = findViewById(R.id.split_controller_toolbar_container)

      catalogFab = findViewById(R.id.split_controller_fab)
      catalogFab.setBehaviorExt(CatalogFabBehavior(context, null))
    }
  }

  override fun onControllerCreated(savedViewState: Bundle?) {
    super.onControllerCreated(savedViewState)

    bottomNavView.selectedItemId = R.id.action_browse

    bottomNavView.setOnNavigationItemSelectedListener { item ->
      if (bottomNavView.selectedItemId == item.itemId) {
        return@setOnNavigationItemSelectedListener true
      }

      splitControllerCatalogControllerContainer.switchTo(
        controller = createControllerBySelectedItemId(
          itemId = item.itemId,
          uiElementsControllerCallbacks = this
        )
      )

      return@setOnNavigationItemSelectedListener true
    }

    splitControllerCatalogControllerContainer.setupChildRouterIfNotSet(
      RouterTransaction.with(createSplitCatalogController(this))
    )
  }

  override fun onControllerShown() {
    super.onControllerShown()

    bottomNavView.doOnPreDraw {
      catalogFab.getBehaviorExt<CatalogFabBehavior>()?.init(bottomNavView)
      catalogFab.translationX = -KurobaFloatingActionButton.DEFAULT_MARGIN_RIGHT.toFloat()
    }
  }

  override fun onControllerHidden() {
    super.onControllerHidden()

    catalogFab.getBehaviorExt<CatalogFabBehavior>()?.reset()
  }

  override fun provideRecyclerView(recyclerView: RecyclerView, controllerType: ControllerType) {
    collapsingViewsHolder.attach(
      recyclerView = recyclerView,
      collapsableView = toolbarContract.collapsableView(),
      viewAttachSide = ViewScreenAttachSide.Top
    )

    collapsingViewsHolder.attach(
      recyclerView = recyclerView,
      collapsableView = bottomNavView,
      viewAttachSide = ViewScreenAttachSide.Bottom
    )
  }

  override fun withdrawRecyclerView(recyclerView: RecyclerView, controllerType: ControllerType) {
    collapsingViewsHolder.detach(recyclerView, toolbarContract.collapsableView())
    collapsingViewsHolder.detach(recyclerView, bottomNavView)
  }

  override fun lockUnlockCollapsableViews(recyclerView: RecyclerView?, lock: Boolean, animate: Boolean) {
    collapsingViewsHolder.lockUnlockCollapsableViews(
      recyclerView = recyclerView,
      lock = lock,
      animate = animate
    )
  }

  override fun showFab(lock: Boolean?) {
    catalogFab.showFab(lock)
  }

  override fun hideFab(lock: Boolean?) {
    catalogFab.hideFab(lock)
  }

  override fun getControllerTag(): ControllerTag = CONTROLLER_TAG

  override fun isSplitLayout(): Boolean = true

  @SuppressLint("BinaryOperationInTimber")
  override fun openBoard(boardDescriptor: BoardDescriptor) {
    val router = getChildRouter(splitControllerCatalogControllerContainer)

    val splitCatalogController = router.getControllerWithTag(SplitCatalogController.CONTROLLER_TAG.tag)
    if (splitCatalogController == null) {
      // TODO(KurobaEx): add ability to switch to SlideNavController in some cases
      Timber.tag(TAG).e("openBoard($boardDescriptor) " +
        "getControllerWithTag(SplitCatalogController) returned null")
      return
    }

    (splitCatalogController as CatalogNavigationContract).openBoard(boardDescriptor)
  }

  override fun openThread(threadDescriptor: ThreadDescriptor) {
    threadNavigationContract.openThread(threadDescriptor)
  }

  private fun createControllerBySelectedItemId(
    itemId: Int,
    uiElementsControllerCallbacks: UiElementsControllerCallbacks
  ): BaseController {
    return when (itemId) {
      R.id.action_bookmarks -> createBookmarksController(uiElementsControllerCallbacks)
      R.id.action_browse -> createSplitCatalogController(uiElementsControllerCallbacks)
      R.id.action_settings -> createSettingsController(uiElementsControllerCallbacks)
      else -> throw IllegalStateException("Unknown itemId: $itemId")
    }
  }

  private fun createSplitCatalogController(
    uiElementsControllerCallbacks: UiElementsControllerCallbacks
  ): SplitCatalogController {
    return SplitCatalogController().apply {
      uiElementsControllerCallbacks(uiElementsControllerCallbacks)
      recyclerViewProvider(this@SplitUiElementsController)
      threadNavigationContract(this@SplitUiElementsController)
      toolbarContract(toolbarContract)
    }
  }

  companion object {
    private const val TAG = "SplitUiElementsController"
    val CONTROLLER_TAG = ControllerTag("SplitUiElementsControllerTag")
  }
}