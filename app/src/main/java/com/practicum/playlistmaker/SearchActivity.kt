package com.practicum.playlistmaker

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Parcelable
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import kotlinx.coroutines.Runnable
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchActivity : AppCompatActivity() {

    private var enterText: String = ENEMY_TEXT
    private val iTunes = "https://itunes.apple.com"
    private val iTunesService = RetrofitCreate(iTunes).createdRetrofit()
    private val track = ArrayList<Track>()
    private lateinit var searchShared: SearchHistory
    private var isClickAllowed = true

    private val youSearchId by lazy { findViewById<TextView>(R.id.you_search_id) }
    private val clearHistoryButtonId by lazy { findViewById<Button>(R.id.history_clear_button_id) }
    private val inputEditText by lazy { findViewById<EditText>(R.id.search_edit_text) }
    private val progressBarElementId by lazy { findViewById<ProgressBar>(R.id.progress_bar_id)}
    private val rvTrack by lazy { findViewById<RecyclerView>(R.id.rvTrack) }


    private val handler = Handler(Looper.getMainLooper())
    private lateinit var trackAdapter: TrackAdapter
    private val searchRunnable = Runnable { searchRequest(trackAdapter) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        searchShared = SearchHistory(getSharedPreferences(SearchHistory.KEY_ADD_HISTORY_TRACK, MODE_PRIVATE))

        val arrowBack = findViewById<MaterialToolbar>(R.id.arrow_back_search)
        arrowBack.setNavigationOnClickListener { finish() }

        val clearButton = findViewById<ImageView>(R.id.clear_icon)
        clearButton.visibility = View.GONE

        trackAdapter = TrackAdapter { clickedTrack ->
            if(clickDebounce()) {
                clickedTrack.typeRes = clickedTrack.typeRes ?: ""
                searchShared.addTrackHistory(clickedTrack)
                val intent = Intent(this, AudioPlayerActivity::class.java).apply {
                    putExtra(AllKeys.KEY_TRACK_SWITCH_ACTIVITY, clickedTrack as Parcelable)
                }
                startActivity(intent)
            }
        }

        trackAdapter.tracks = track

        clearButton.setOnClickListener {
            inputEditText.setText("")
            if (track.isNotEmpty()) {
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

        inputEditText.addTextChangedListener(createTextWatcher(clearButton))
        rvTrack.adapter = trackAdapter
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(searchRunnable)
    }

    private fun createTextWatcher(clearButton: ImageView): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.visibility = clearButtonVisibility(s)
                if (inputEditText.hasFocus() && s?.isEmpty() == true && !searchShared.getEmptyOrNullSharedTracks()) {
                    youSearchId.visibility = View.VISIBLE
                    clearHistoryButtonId.visibility = View.VISIBLE
                } else {
                    youSearchId.visibility = View.GONE
                    clearHistoryButtonId.visibility = View.GONE
                    trackAdapter.tracks = track
                    trackAdapter.notifyDataSetChanged()
                }
                searchDebounce()
            }

            override fun afterTextChanged(s: Editable?) {
                enterText = s.toString()
            }
        }
    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(ENTER_TEXT_KEY, enterText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        enterText = savedInstanceState.getString(ENTER_TEXT_KEY, ENEMY_TEXT)
    }

    private fun clickDebounce() : Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    private fun searchRequest(trackAdapter: TrackAdapter) {
        if (inputEditText.text.isNotEmpty()) {

            showOrNotProgressBar(true)

            val query = inputEditText.text.toString()
            iTunesService.search(query).enqueue(object : Callback<TrackResponse> {
                override fun onResponse(call: Call<TrackResponse>, response: Response<TrackResponse>) {

                    showOrNotProgressBar(false)

                    if (response.code() == 200) {
                        track.clear()
                        response.body()?.let { body ->
                            if (body.resultCount != 0) {
                                body.results?.let { results ->
                                    track.addAll(results)
                                    trackAdapter.notifyDataSetChanged()
                                }
                            } else {
                                adapterToggle(true)
                            }
                        } ?: adapterToggle(true)
                    }
                }

                override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                    showOrNotProgressBar(false)
                    adapterToggle(false)
                    trackAdapter.notifyDataSetChanged()
                }
            })
        }
    }

    private fun hideKeyboard() {
        currentFocus?.let { view ->
            (getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager)?.hideSoftInputFromWindow(
                view.windowToken, 0
            )
        }
    }

    private fun adapterToggle(isNotFoundTrack: Boolean) {
        track.clear()
        val emptyTrack = Track(
            trackName = "",
            artistName = "",
            trackTime = "",
            artworkUrl100 = "",
            trackId = 0,
            collectionName = "",
            releaseDate = "",
            primaryGenreName = "",
            country = "",
            previewUrl = ""
        ).apply {
            typeRes = if (isNotFoundTrack) Track.TRACK_TYPE_RES_NOT_FOUND else Track.TRACK_TYPE_RES_NO_ETHERNET
        }
        track.add(emptyTrack)
        trackAdapter.notifyDataSetChanged()
    }

    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    private fun showOrNotProgressBar(isShow: Boolean) {
        if(isShow) {
            rvTrack.visibility = View.GONE
            progressBarElementId.visibility = View.VISIBLE
        } else {
            progressBarElementId.visibility = View.GONE
            rvTrack.visibility = View.VISIBLE

        }
    }

    companion object {
        private const val ENTER_TEXT_KEY = "ENTER_TEXT_SEARCH"
        private const val ENEMY_TEXT = ""
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }
}