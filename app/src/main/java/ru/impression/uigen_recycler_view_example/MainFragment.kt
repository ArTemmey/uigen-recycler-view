package ru.impression.uigen_recycler_view_example

import android.widget.Toast
import androidx.fragment.app.Fragment
import ru.impression.ui_generator_annotations.MakeComponent
import ru.impression.ui_generator_base.ComponentScheme
import ru.impression.ui_generator_base.ComponentViewModel
import ru.impression.uigen_recycler_view_example.databinding.FragmentMainBinding
import ru.impression.uigen_recycler_view_example.databinding.ItemThingBinding

@MakeComponent
class MainFragment : ComponentScheme<Fragment, MainFragmentViewModel>({ viewModel ->
    viewModel.toastMessage?.let {
        Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        viewModel.toastMessage = null
    }
    FragmentMainBinding::class
})

class MainFragmentViewModel : ComponentViewModel() {

    val things = listOf("tree", "house", "cat", "computer", "bicycle")

    val itemThingBindingClass = ItemThingBinding::class

    fun onThingClick(thing: String) {
        toastMessage = "$thing is clicked"
    }


    var toastMessage by state<String?>(null)
}