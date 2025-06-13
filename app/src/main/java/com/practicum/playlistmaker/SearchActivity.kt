package com.practicum.playlistmaker

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class SearchActivity : AppCompatActivity() {

    private var enterText: String = ENEMY_TEXT
    private val iTunes = "https://itunes.apple.com"
    private val retrofit = Retrofit.Builder()
        .baseUrl(iTunes)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val iTunesService = retrofit.create(ITunesApi::class.java)
    private val track = ArrayList<Track>()
    private val trackAdapter = TrackAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val arrowBack = findViewById<MaterialToolbar>(R.id.arrow_back_search)
        arrowBack.setNavigationOnClickListener {
            finish()
        }

        val inputEditText = findViewById<EditText>(R.id.search_edit_text)
        val clearButton = findViewById<ImageView>(R.id.clear_icon)
        clearButton.visibility = View.GONE

        trackAdapter.tracks = track

        clearButton.setOnClickListener {
            inputEditText.setText("")
            if (track.size > 1) {
                track.clear()
                trackAdapter.notifyDataSetChanged()
            }
            hideKeyboard()
        }

        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (inputEditText.text.isNotEmpty()) {
                    iTunesService.search(inputEditText.text.toString()).enqueue(object :
                        Callback<TrackResponse> {
                        override fun onResponse(call: Call<TrackResponse>,
                                                response: Response<TrackResponse>
                        ) {
                            if (response.code() == 200) {
                                track.clear()
                                if (response.body()?.resultCount != 0) {
                                    track.addAll(response.body()?.results!!)
                                    trackAdapter.notifyDataSetChanged()
                                } else if (response.body()?.resultCount == 0)
                                    adapterToggle(true)
                            }
                        }

                        override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                            adapterToggle(false)
                        }
                    })
                }
                true
            }
            false
        }

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.visibility = clearButtonVisibility(s)
            }

            override fun afterTextChanged(s: Editable?) {
                enterText = s.toString()
            }

        }
        inputEditText.addTextChangedListener(simpleTextWatcher)

        val rvTrack = findViewById<RecyclerView>(R.id.rvTrack)
        rvTrack.adapter = trackAdapter

    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(ENTER_TEXT_KEY, enterText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        enterText = savedInstanceState.getString(ENTER_TEXT_KEY, ENEMY_TEXT)
    }

    private fun hideKeyboard() {
        this.currentFocus?.let { view ->
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    private fun adapterToggle(isNotFoundTrack: Boolean) {
        track.clear()
        if (isNotFoundTrack)
            track.add(Track("", "", "", "", Track.TRACK_TYPE_RES_NOT_FOUND))
        else
            track.add(Track("", "", "", "", Track.TRACK_TYPE_RES_NO_ETHERNET))
        trackAdapter.notifyDataSetChanged()
    }


    companion object {
        private const val ENTER_TEXT_KEY = "ENTER_TEXT_SEARCH"
        private const val ENEMY_TEXT = ""
    }
}