package com.practicum.playlistmaker.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textview.MaterialTextView
import com.practicum.playlistmaker.App
import com.practicum.playlistmaker.Creator
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.usecases.SettingsInteractor

class SettingsActivity : AppCompatActivity() {

    private lateinit var interactor: SettingsInteractor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        interactor = Creator.provideSettingsInteractor(this)

        setupViews()
    }

    private fun setupViews() {
        setupToolbar()
        setupThemeSwitch()
        setupShareApp()
        setupContactSupport()
        setupUserAgreement()
    }

    private fun setupThemeSwitch() {
        val themeSwitch = findViewById<SwitchMaterial>(R.id.theme_switch)
        themeSwitch.isChecked = interactor.isDarkThemeEnabled()

        themeSwitch.setOnCheckedChangeListener { _, checked ->
            interactor.setDarkThemeEnabled(checked)
            (application as App).switchTheme(checked)
        }
    }

    private fun setupToolbar() {
        findViewById<MaterialToolbar>(R.id.arrow_back_settings).setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupShareApp() {
        findViewById<MaterialTextView>(R.id.share_application_activity_settings).setOnClickListener {
            Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, getString(R.string.android_developer_course_link))
                startActivity(this)
            }
        }
    }

    private fun setupContactSupport() {
        findViewById<MaterialTextView>(R.id.help_desk_activity_settings).setOnClickListener {
            Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.email_student)))
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.theme_mail))
                putExtra(Intent.EXTRA_TEXT, getString(R.string.body_mail))
                startActivity(Intent.createChooser(this, getString(R.string.title_choose)))
            }
        }
    }

    private fun setupUserAgreement() {
        findViewById<MaterialTextView>(R.id.user_agreement_activity_settings).setOnClickListener {
            Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.user_agreement_link))).also {
                startActivity(it)
            }
        }
    }
}