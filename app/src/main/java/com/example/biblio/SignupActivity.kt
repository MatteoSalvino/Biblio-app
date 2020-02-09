package com.example.biblio

import android.app.Activity
import android.app.ProgressDialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.biblio.api.UserBuilder
import com.example.biblio.databinding.ActivitySignupBinding
import com.example.biblio.helpers.LogHelper
import com.example.biblio.helpers.SimpleBiblioHelper

class SignupActivity : AppCompatActivity() {
    private val logger = LogHelper(javaClass)
    private var username: String? = null
    private var email: String? = null
    private var password: String? = null
    private var passwordConfirmation: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.getRoot())
        binding.signupBtn.setOnClickListener {
            username = binding.signupNameField.editText?.text.toString()
            email = binding.signupEmailField.editText?.text.toString()
            password = binding.signupPasswordField.editText?.text.toString()
            passwordConfirmation = binding.signupPasswordConfirmationField.editText?.text.toString()
            if (binding.signupTermsCb.isChecked)
                trySignup()
        }
    }

    /**
     * Checks wheter ot not the current signup form is valid and tries to signup.
     * If successful, terminates the activity and stores the credentials.
     */
    private fun trySignup() {
        if (!isValidForm()) {
            logger.d("The signup form is not valid.")
            return
        }
        val progressDialog = ProgressDialog.show(this, "", "", true)
        progressDialog.setContentView(R.layout.progress_login)
        Thread(Runnable {
            val user = UserBuilder()
                    .setEmail(email)
                    .setPassword(password)
                    .setUsername(username)
                    .build()
            val successful = user.signup()
            runOnUiThread { progressDialog.dismiss() }
            if (successful) {
                logger.d("successful signup")
                SimpleBiblioHelper.setCurrentUser(user, applicationContext)
                setResult(Activity.RESULT_OK)
                finish()
            } else logger.e("signup failed")
        }).start()
    }

    /**
     * Simple method to check whether the form has been properly filled.
     * This does not check user credentials, since is only a local method.
     *
     * @return true if valid, else false
     */
    private fun isValidForm(): Boolean {
        val blank = username.isNullOrBlank() || email.isNullOrBlank() || password.isNullOrBlank()
        val match = password == passwordConfirmation
        return !blank && match
    }
}