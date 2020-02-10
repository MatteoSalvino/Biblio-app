package com.example.biblio

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.example.biblio.api.User
import com.example.biblio.api.UserBuilder
import com.example.biblio.databinding.ActivityLoginBinding
import com.example.biblio.helpers.GoogleHelper.getSignInIntent
import com.example.biblio.helpers.SimpleBiblioHelper
import com.example.biblio.helpers.XActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.apache.commons.validator.routines.EmailValidator

class LoginActivity : XActivity(LoginActivity::class.java) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.loginBtn.setOnClickListener { _: View? ->
            val email = binding.emailField.editText!!.text.toString().trim { it <= ' ' }
            val password = binding.passwordField.editText!!.text.toString().trim { it <= ' ' }
            if (validateEmail(email))
                uiScope.launch { executeLogin(UserBuilder().setEmail(email).setPassword(password).build(), true) }
        }
        binding.googleLoginBtn.setOnClickListener { startActivityForResult(getSignInIntent(this), RC_GOOGLE_SIGN_IN) }
        binding.signupSuggestionBtn.setOnClickListener { startActivityForResult(Intent(this, SignupActivity::class.java), RC_SIGN_UP) }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_GOOGLE_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach a listener.
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        } else {
            // Handles RC_SIGN_UP requests
            setResult(resultCode)
            finish()
        }
    }

    private fun showErrorMessage() {
        val dialog = AlertDialog.Builder(this)
                .setTitle(R.string.login_error_title)
                .setMessage(R.string.login_error_msg)
                .setIcon(R.drawable.baseline_error_outline_24)
                .create()
        dialog.show()
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            if (account != null) {
                val user = UserBuilder().fromGoogleAccount(account).build()
                uiScope.launch { executeLogin(user, false) }
                return
            }
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            logger.w("signInResult:failed code=" + e.statusCode)
        }
        setResult(Activity.RESULT_CANCELED)
        finish()
    }

    private fun validateEmail(email: String): Boolean {
        if (EmailValidator.getInstance().isValid(email)) return true
        logger.d("Invalid email inserted")
        runOnUiThread { showErrorMessage() }
        return false
    }

    private suspend fun executeLogin(user: User, showProgress: Boolean) {
        val dialog = initProgress(showProgress)
        val successful = withContext(Dispatchers.IO) { user.login() }
        if (showProgress) dialog?.dismiss()
        if (successful) {
            logger.d("successful login")
            SimpleBiblioHelper.setCurrentUser(user, applicationContext)
            setResult(Activity.RESULT_OK)
            finish()
        } else {
            logger.d("login failed")
            showErrorMessage()
        }
    }

    private fun initProgress(show: Boolean): ProgressDialog? {
        if (!show) return null
        val dialog = ProgressDialog.show(this, resources.getString(R.string.login_process_tv), resources.getString(R.string.please_wait_tv), true)
        dialog.setContentView(R.layout.progress_login)
        return dialog
    }

    companion object {
        private const val RC_SIGN_UP = 1
        private const val RC_GOOGLE_SIGN_IN = 2
    }
}