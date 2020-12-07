package ru.impression.uigen_recycler_view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.databinding.BindingAdapter
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.recyclerview.widget.RecyclerView
import kotlin.Boolean
import kotlin.Int
import kotlin.collections.List
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic
import ru.impression.ui_generator_base.Component
import ru.impression.ui_generator_base.ComponentViewModel
import ru.impression.ui_generator_base.Renderer
import ru.impression.ui_generator_base.resolveAttrs
import ru.impression.ui_generator_base.safeSetProp

/*
 This is a copy of generated class. There is some problem using generated views from third party
 libraries.
 */
class UigenRecyclerViewComponent @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr), Component<RecyclerView, UigenRecyclerViewModel>,
    LifecycleOwner {
  override val scheme: UigenRecyclerView = UigenRecyclerView()

  override val viewModel: UigenRecyclerViewModel =
      createViewModel(ru.impression.uigen_recycler_view.UigenRecyclerViewModel::class)

  override val container: View = this

  override val boundLifecycleOwner: LifecycleOwner = this

  override val renderer: Renderer = Renderer(this)

  private val lifecycleRegistry: LifecycleRegistry = LifecycleRegistry(this)

  private var isDetachedFromWindow: Boolean = false

  init {
    resolveAttrs(attrs)
        render(false)
        startObservations()
  }

  override fun getLifecycle() = lifecycleRegistry
  override fun onAttachedToWindow() {
    super.onAttachedToWindow()
    lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
    if (isDetachedFromWindow) {
      isDetachedFromWindow = false
      startObservations()
    }
  }

  override fun onDetachedFromWindow() {
    super.onDetachedFromWindow()
    lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    viewModel.onCleared()
    isDetachedFromWindow = true
  }

  companion object {
    @JvmStatic
    @BindingAdapter("getItemBindingClass")
    fun setGetItemBindingClass(view: UigenRecyclerViewComponent,
        value: UigenRecyclerViewModel.GetItemBindingClass?) {
      if (value === view.viewModel.getItemBindingClass) return
      view.viewModel::getItemBindingClass.safeSetProp(value)
    }

    @JvmStatic
    @BindingAdapter("data")
    fun setData(view: UigenRecyclerViewComponent, value: List<*>?) {
      if (value === view.viewModel.data) return
      view.viewModel::data.safeSetProp(value)
    }

    @JvmStatic
    @BindingAdapter("viewModel")
    fun setViewModel(view: UigenRecyclerViewComponent, value: ComponentViewModel?) {
      if (value === view.viewModel.viewModel) return
      view.viewModel::viewModel.safeSetProp(value)
    }
  }
}
