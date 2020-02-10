package com.example.biblio.helpers

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.biblio.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel

/**
 * A very simple extension of androidx Fragment, providing a default logger and implementing some
 * common patterns (e.g. fragment transactions).
 *
 * @see LogHelper
 */
open class XFragment(clazz: Class<*>) : Fragment() {
    val TAG: String = clazz.simpleName
    protected val uiScope = CoroutineScope(Dispatchers.Main)
    protected lateinit var xContext: Context

    protected var logger = LogHelper(clazz)

    private val xFragmentManager: FragmentManager?
        get() = activity?.supportFragmentManager

    protected fun popBackStackImmediate() {
        xFragmentManager?.popBackStackImmediate()
    }

    protected fun moveTo(fragment: XFragment) {
        startTransaction(fragment, true)
    }

    protected fun replaceWith(fragment: XFragment) {
        startTransaction(fragment, false)
    }

    private fun startTransaction(fragment: XFragment, addToBackStack: Boolean) {
        val containerId = R.id.fragment_container
        var transaction = xFragmentManager?.beginTransaction()
                ?.replace(containerId, fragment, fragment.TAG)
        if (addToBackStack) transaction = transaction?.addToBackStack(null)
        transaction?.commit()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = super.onCreateView(inflater, container, savedInstanceState)
        xContext = requireContext()
        return v;
    }

    override fun onDestroy() {
        super.onDestroy()
        uiScope.cancel()
    }
}