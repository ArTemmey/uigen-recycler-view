package ru.impression.uigen_recycler_view

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.impression.ui_generator_annotations.MakeComponent
import ru.impression.ui_generator_annotations.Prop
import ru.impression.ui_generator_base.ComponentScheme
import ru.impression.ui_generator_base.ComponentViewModel
import kotlin.reflect.KClass

interface Identifiable {
    val primaryProperty: Any
}

@MakeComponent
class UigenRecyclerView : ComponentScheme<RecyclerView, UigenRecyclerViewModel>({ viewModel ->
    layoutManager = viewModel.layoutManager
    layoutManager ?: run { layoutManager = LinearLayoutManager(context) }
    (adapter as? Adapter)
        ?.let { adapter ->
            if (adapter.getItemBindingClass != viewModel.getItemBindingClass
                || adapter.viewModel != viewModel.viewModel
            ) this.adapter = viewModel.getItemBindingClass?.let { Adapter(it, viewModel.viewModel) }
        }
        ?: adapter
        ?: viewModel.getItemBindingClass?.let { adapter = Adapter(it, viewModel.viewModel) }
    (adapter as? Adapter)?.let { if (it.currentList !== viewModel.data) it.submitList(viewModel.data) }
    null
}) {

    class Adapter(
        val getItemBindingClass: UigenRecyclerViewModel.GetItemBindingClass,
        val viewModel: ComponentViewModel?
    ) : ListAdapter<Any?, Adapter.ViewHolder>(DiffUtilCallback()) {

        private val itemBindingClasses = ArrayList<KClass<out ViewDataBinding>>()

        override fun getItemViewType(position: Int): Int {
            val itemBindingClass =
                getItemBindingClass.get(getItem(position)) ?: return -1
            val itemBindingClassIndex = itemBindingClasses.indexOf(itemBindingClass)
            if (itemBindingClassIndex != -1) return itemBindingClassIndex
            itemBindingClasses.add(itemBindingClass)
            return itemBindingClasses.size - 1
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            if (viewType == -1) ViewHolder(View(parent.context)) else ViewDataBindingHolder(
                itemBindingClasses[viewType].java.getDeclaredMethod(
                    "inflate",
                    LayoutInflater::class.java,
                    ViewGroup::class.java,
                    Boolean::class.javaPrimitiveType
                ).invoke(
                    null,
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ) as ViewDataBinding
            )

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            if (holder !is ViewDataBindingHolder) return
            val data = getItem(position)
            holder.binding::class.java.declaredMethods.firstOrNull {
                val parameterTypes = it.parameterTypes
                it.name == "setData"
                        && parameterTypes.size == 1
                        && (data == null || parameterTypes[0].isAssignableFrom(data::class.java))
            }?.invoke(holder.binding, data)
            viewModel?.let {
                holder.binding::class.java.getDeclaredMethod("setViewModel", it::class.java)
                    .invoke(holder.binding, it)
            }
        }

        open class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
        class ViewDataBindingHolder(val binding: ViewDataBinding) : ViewHolder(binding.root)

        class DiffUtilCallback : DiffUtil.ItemCallback<Any?>() {
            override fun areItemsTheSame(oldItem: Any, newItem: Any) =
                if (oldItem is Identifiable && newItem is Identifiable)
                    oldItem.primaryProperty == newItem.primaryProperty
                else
                    oldItem === newItem

            @SuppressLint("DiffUtilEquals")
            override fun areContentsTheSame(oldItem: Any, newItem: Any) = oldItem == newItem
        }
    }
}

class UigenRecyclerViewModel : ComponentViewModel() {

    @Prop
    var getItemBindingClass: GetItemBindingClass? by state(null)

    @Prop
    var data: List<*>? by state(null)

    @Prop
    var viewModel: ComponentViewModel? by state(null)

    @Prop
    var layoutManager: RecyclerView.LayoutManager? by state(null)

    interface GetItemBindingClass {
        fun get(itemData: Any?): KClass<out ViewDataBinding>?
    }
}