package com.practicum.playlistmaker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textview.MaterialTextView

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val themeSwitch by lazy { findViewById<SwitchMaterial>(R.id.theme_switch) }
        val sharedPref = getSharedPreferences(KEY_SWITCH_THEME, MODE_PRIVATE)
        val arrowBack = findViewById<MaterialToolbar>(R.id.arrow_back_settings)
        arrowBack.setNavigationOnClickListener {
            finish()
        }
        val shareApplicationView = findViewById<MaterialTextView>(R.id.share_application_activity_settings)
        shareApplicationView.setOnClickListener {
            val shareMessage = Intent(Intent.ACTION_SEND)
            shareMessage.putExtra(Intent.EXTRA_TEXT, getString(R.string.android_developer_course_link))
            shareMessage.setType("text/plain")
            startActivity(shareMessage)
        }
        val writeHelpDesk = findViewById<MaterialTextView>(R.id.help_desk_activity_settings)
        writeHelpDesk.setOnClickListener {
            val sendMail = Intent(Intent.ACTION_SENDTO)
            sendMail.data = Uri.parse("mailto:")
            sendMail.putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.email_student)))
            sendMail.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.theme_mail))
            sendMail.putExtra(Intent.EXTRA_TEXT, getString(R.string.body_mail))
            startActivity(Intent.createChooser(sendMail, getString(R.string.title_choose)))
        }
        val userAgreement = findViewById<MaterialTextView>(R.id.user_agreement_activity_settings)
        userAgreement.setOnClickListener {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(
                        getString(R.string.user_agreement_link)
                    ))
            )
        }

        themeSwitch.isChecked = sharedPref.getBoolean(KEY_SWITCH_THEME, false)
        themeSwitch.setOnCheckedChangeListener { switcher, checked ->
            (applicationContext as App).switchTheme(checked)
            sharedPref.edit()
                .putBoolean(KEY_SWITCH_THEME, checked)
                .apply()
        }
    }

    companion object {
        const val KEY_SWITCH_THEME = "key_switch_theme"
    }
}