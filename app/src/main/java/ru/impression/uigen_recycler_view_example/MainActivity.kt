package ru.impression.uigen_recycler_view_example

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportFragmentManager.findFragmentByTag(MainFragmentComponent::class.simpleName)
            ?: supportFragmentManager.beginTransaction().replace(
                R.id.container,
                MainFragmentComponent(),
                MainFragmentComponent::class.simpleName
            ).commit()
    }
}
