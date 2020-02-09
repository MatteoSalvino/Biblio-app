package com.example.biblio.helpers

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.biblio.R

/**
 * A very simple extension of androidx Fragment, providing a default logger and implementing some
 * common patterns (e.g. fragment transactions).
 *
 * @see LogHelper
 */
open class XFragment(clazz: Class<*>) : Fragment() {
    val TAG: String = clazz.simpleName

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
}