package com.practicum.playlistmaker

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SearchActivity : AppCompatActivity() {

    private var enterText: String = ENEMY_TEXT
    private val iTunes = "https://itunes.apple.com"
    private val iTunesService = RetrofitCreate(iTunes).createdRetrofit()
    private val track = ArrayList<Track>()
    private val youSearchId by lazy { findViewById<TextView>(R.id.you_search_id) }
    private val clearHistoryButtonId by lazy { findViewById<Button>(R.id.history_clear_button_id) }


    override fun onCreate(savedInstanceState: Bundle?) {
        val shared = getSharedPreferences(SearchHistory.KEY_ADD_HISTORY_TRACK, MODE_PRIVATE)
        val searchShared = SearchHistory(shared)
        val trackAdapter = TrackAdapter(
            { clickedTrack ->
                searchShared.addTrackHistory(clickedTrack)
                SavedData.savedTrackOpen = clickedTrack
                val intent = Intent(this, AudioPlayerActivity::class.java)
                startActivity(intent)
            }
        )

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
            inputEditText.clearFocus()
            hideKeyboard()
        }

        clearHistoryButtonId.setOnClickListener {
            searchShared.removeData()
            youSearchId.visibility = View.GONE
            clearHistoryButtonId.visibility = View.GONE
            trackAdapter.tracks.clear()
            trackAdapter.notifyDataSetChanged()
            inputEditText.clearFocus()
        }

        inputEditText.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus && inputEditText.text.isEmpty() && !searchShared.getEmptyOrNullSharedTracks()) {
                youSearchId.visibility = View.VISIBLE
                clearHistoryButtonId.visibility = View.VISIBLE
                trackAdapter.tracks = searchShared.getArrHistoryTrack()
                trackAdapter.notifyDataSetChanged()
            } else {
                youSearchId.visibility = View.GONE
                clearHistoryButtonId.visibility = View.GONE
            }

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
                                } else if (response.body()?.resultCount == 0) {
                                    adapterToggle(true)
                                    trackAdapter.notifyDataSetChanged()
                                }

                            }
                        }

                        override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                            adapterToggle(false)
                            trackAdapter.notifyDataSetChanged()

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
                if (inputEditText.hasFocus() && s?.isEmpty() == true && !searchShared.getEmptyOrNullSharedTracks()) {
                    youSearchId.visibility = View.VISIBLE
                    clearHistoryButtonId.visibility = View.VISIBLE
                }
                else {
                    youSearchId.visibility = View.GONE
                    clearHistoryButtonId.visibility = View.GONE
                    trackAdapter.tracks = track
                    trackAdapter.notifyDataSetChanged()
                }

            }

            override fun afterTextChanged(s: Editable?) {
                enterText = s.toString()
            }

        }
        inputEditText.addTextChangedListener(simpleTextWatcher)
        val rvTrack = findViewById<RecyclerView>(R.id.rvTrack)
        rvTrack.adapter = trackAdapter

    }

    override fun onDestroy() {
        super.onDestroy()
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
        val emptyTrack = Track("", "", "", "", 0, "", "", "", "", "")
        if (isNotFoundTrack) {
            emptyTrack.typeRes = Track.TRACK_TYPE_RES_NOT_FOUND
            track.add(emptyTrack)
        }
        else {
            emptyTrack.typeRes = Track.TRACK_TYPE_RES_NO_ETHERNET
            track.add(emptyTrack)
        }
    }


    companion object {
        private const val ENTER_TEXT_KEY = "ENTER_TEXT_SEARCH"
        private const val ENEMY_TEXT = ""
    }
}