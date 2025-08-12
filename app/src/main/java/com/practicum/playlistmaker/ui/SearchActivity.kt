package com.practicum.playlistmaker.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Parcelable
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
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
import com.practicum.playlistmaker.Creator.provideSearchHistoryRepository
import com.practicum.playlistmaker.Creator.provideTracksInteractor
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.domain.api.TracksInteractor
import com.practicum.playlistmaker.domain.models.TrackTypeRes
import com.practicum.playlistmaker.domain.repository.SearchHistoryRepository

class SearchActivity : AppCompatActivity() {

    private var enterText: String = DEFAULT_TEXT
    private val tracksInteractor = provideTracksInteractor()
    private val tracks = ArrayList<Track>()
    private lateinit var searchHistoryRepository: SearchHistoryRepository
    private var isClickAllowed = true

    private val youSearchId by lazy { findViewById<TextView>(R.id.you_search_id) }
    private val clearHistoryButtonId by lazy { findViewById<Button>(R.id.history_clear_button_id) }
    private val inputEditText by lazy { findViewById<EditText>(R.id.search_edit_text) }
    private val progressBar by lazy { findViewById<ProgressBar>(R.id.progress_bar_id) }
    private val rvTrack by lazy { findViewById<RecyclerView>(R.id.rvTrack) }
    private val clearButton by lazy { findViewById<ImageView>(R.id.clear_icon) }

    private val handler = Handler(Looper.getMainLooper())
    private lateinit var trackAdapter: TrackAdapter
    private val searchRunnable = Runnable { performSearch() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        searchHistoryRepository = provideSearchHistoryRepository(this)
        restoreState(savedInstanceState)
        setupUI()
    }

    private fun restoreState(savedInstanceState: Bundle?) {
        savedInstanceState?.let {
            enterText = it.getString(ENTER_TEXT_KEY, DEFAULT_TEXT)
            inputEditText.setText(enterText)
            if (it.getBoolean(IS_LOADING_KEY, false)) {
                showOrNotProgressBar(true)
            }
        }
    }

    private fun setupUI() {
        clearButton.visibility = View.GONE
        setupToolbar()
        setupRecyclerView()
        setupClickListeners()
        setupTextWatcher()
    }

    private fun setupToolbar() {
        findViewById<MaterialToolbar>(R.id.arrow_back_search).setNavigationOnClickListener { finish() }
    }

    private fun setupRecyclerView() {
        trackAdapter = TrackAdapter { clickedTrack ->
            if (clickDebounce() && clickedTrack.typeRes == TrackTypeRes.DEFAULT) {
                searchHistoryRepository.addTrack(clickedTrack)
                startPlayerActivity(clickedTrack)
            }
        }
        trackAdapter.tracks = tracks
        rvTrack.adapter = trackAdapter
    }

    private fun startPlayerActivity(track: Track) {
        Intent(this, AudioPlayerActivity::class.java).apply {
            putExtra(KEY_TRACK_SWITCH_ACTIVITY, track as Parcelable)
            startActivity(this)
        }
    }

    private fun setupClickListeners() {
        clearButton.setOnClickListener {
            clearSearch()
        }

        clearHistoryButtonId.setOnClickListener {
            clearSearchHistory()
        }
    }

    private fun clearSearch() {
        inputEditText.setText("")
        clearTracks()
        inputEditText.clearFocus()
        hideKeyboard()
    }

    private fun clearSearchHistory() {
        updateHistoryVisibility(false)
        searchHistoryRepository.clearHistory()
        inputEditText.clearFocus()
        clearTracks()
    }

    private fun clearTracks() {
        tracks.clear()
        trackAdapter.tracks = ArrayList()
        trackAdapter.notifyDataSetChanged()
    }

    private fun setupTextWatcher() {
        inputEditText.addTextChangedListener(createTextWatcher())
        inputEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && inputEditText.text.isEmpty()) {
                showSearchHistory()
            }
        }
    }

    private fun createTextWatcher(): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.visibility = if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
                searchDebounce()
            }

            override fun afterTextChanged(s: Editable?) {
                enterText = s.toString()
            }
        }
    }

    private fun showSearchHistory() {
        if (!searchHistoryRepository.isHistoryEmpty()) {
            updateHistoryVisibility(true)
            trackAdapter.tracks = searchHistoryRepository.getHistory()
                .filter { it.typeRes == TrackTypeRes.DEFAULT } as ArrayList<Track>
            trackAdapter.notifyDataSetChanged()
        }
    }

    private fun updateHistoryVisibility(show: Boolean) {
        youSearchId.visibility = if (show) View.VISIBLE else View.GONE
        clearHistoryButtonId.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(ENTER_TEXT_KEY, enterText)
        outState.putBoolean(IS_LOADING_KEY, progressBar.visibility == View.VISIBLE)
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    private fun hideKeyboard() {
        currentFocus?.let { view ->
            (getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager)?.hideSoftInputFromWindow(
                view.windowToken, 0
            )
        }
    }

    private fun createErrorTrack(type: TrackTypeRes): Track {
        return Track(
            trackName = "",
            artistName = "",
            trackTimeMillis = "",
            artworkUrl100 = "",
            trackId = 0,
            collectionName = "",
            releaseDate = "",
            primaryGenreName = "",
            country = "",
            previewUrl = "",
            typeRes = type
        )
    }

    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    private fun performSearch() {
        val query = inputEditText.text.toString()
        if (query.isEmpty()) return

        showOrNotProgressBar(true)
        updateHistoryVisibility(false)

        tracksInteractor.searchTracks(query, object : TracksInteractor.TracksConsumer {
            override fun consume(foundTracks: List<Track>) {
                Log.d("Search", "Received ${foundTracks.size} tracks")
                handler.post {
                    tracks.clear()
                    if (foundTracks.isEmpty()) {
                        tracks.add(createErrorTrack(TrackTypeRes.NOT_FOUND))
                    } else {
                        tracks.addAll(foundTracks)
                    }
                    trackAdapter.tracks = tracks
                    trackAdapter.notifyDataSetChanged()
                    showOrNotProgressBar(false)
                }
            }

            override fun onError(error: Exception) {
                Log.d("Search", "Received ${error} tracks")
                handler.post {
                    tracks.clear()
                    tracks.add(createErrorTrack(TrackTypeRes.NO_INTERNET))
                    trackAdapter.notifyDataSetChanged()
                    showOrNotProgressBar(false)
                }
            }
        })
    }

    private fun showOrNotProgressBar(isShow: Boolean) {
        progressBar.visibility = if (isShow) View.VISIBLE else View.GONE
        rvTrack.visibility = if (isShow) View.GONE else View.VISIBLE
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(searchRunnable)
    }

    companion object {
        private const val ENTER_TEXT_KEY = "ENTER_TEXT_SEARCH"
        private const val IS_LOADING_KEY = "IS_LOADING"
        private const val DEFAULT_TEXT = ""
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        private const val KEY_TRACK_SWITCH_ACTIVITY = "key_track_switch_activity"
    }
}