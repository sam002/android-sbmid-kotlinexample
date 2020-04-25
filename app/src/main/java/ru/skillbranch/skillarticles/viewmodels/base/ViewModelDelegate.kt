package ru.skillbranch.skillarticles.viewmodels.base

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class ViewModelDelegate<T : ViewModel>(private val clazz: Class<T>, private val arg: Any?) :
    ReadOnlyProperty<FragmentActivity, T> {

    private var viewModel : T? = null
    override fun getValue(thisRef: FragmentActivity, property: KProperty<*>): T {
        if (viewModel != null) {
            return viewModel!!
        }
        viewModel = if(arg == null) {
            ViewModelProviders.of(thisRef).get(clazz)
        } else {
            val vmFactory = ViewModelFactory(arg)
            ViewModelProviders.of(thisRef, vmFactory).get(clazz)
        }

        return viewModel!!
    }
}