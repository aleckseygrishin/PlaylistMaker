package com.practicum.playlistmaker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textview.MaterialTextView

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

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
            sendMail.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.theme_mail))
            sendMail.putExtra(Intent.EXTRA_TEXT, getString(R.string.body_mail))
            sendMail.setData(Uri.parse("mailto:${getString(R.string.email_student)}"))
            startActivity(sendMail)
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
    }
}