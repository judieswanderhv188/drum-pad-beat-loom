package com.tempo.beatloom.presentation.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tempo.beatloom.core.config.GameConfig
import com.tempo.beatloom.data.audio.PercussionSynth
import com.tempo.beatloom.domain.model.Puzzle
import com.tempo.beatloom.domain.model.SubmissionResult
import com.tempo.beatloom.domain.repository.ProgressRepository
import com.tempo.beatloom.domain.repository.PuzzleRepository
import com.tempo.beatloom.domain.repository.SettingsRepository
import com.tempo.beatloom.domain.usecase.ScoreSubmissionUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GameViewModel(
    private val puzzleRepository: PuzzleRepository,
    private val progressRepository: ProgressRepository,
    private val settingsRepository: SettingsRepository,
    private val scoreSubmission: ScoreSubmissionUseCase,
    private val synth: PercussionSynth
) : ViewModel() {

    private val _state = MutableStateFlow(GameUiState())
    val state: StateFlow<GameUiState> = _state.asStateFlow()

    private val _events = Channel<SubmissionResult>(Channel.BUFFERED)
    val events: Flow<SubmissionResult> = _events.receiveAsFlow()

    private var puzzle: Puzzle? = null
    private var loopJob: Job? = null
    private var triesUsed = 0
    private var volume = 0.85f

    fun start(puzzleId: Int) {
        loopJob?.cancel()
        loopJob = null
        puzzle = null
        triesUsed = 0
        _state.value = GameUiState()
        viewModelScope.launch {
            val loaded = puzzleRepository.puzzle(puzzleId)
            val settings = settingsRepository.settings()
            val styles = puzzleRepository.styles()
            puzzle = loaded
            volume = settings.masterVolume
            _state.value = GameUiState(
                ready = true,
                styleRes = styles[loaded.chapterIndex].nameRes,
                loopNumber = loaded.indexInChapter + 1,
                bpm = loaded.bpm,
                voices = loaded.voices,
                pattern = loaded.voices.map { List(GameConfig.STEPS_PER_BAR) { false } },
                lockedRows = loaded.voices.map { false },
                threadProgress = loaded.voices.map { 0f },
                triesLeft = GameConfig.MAX_SUBMISSIONS,
                labelsVisible = settings.gridLabels,
                highContrast = settings.highContrastPads
            )
        }
        viewModelScope.launch(Dispatchers.Default) {
            runCatching { synth.prepare() }
        }
    }

    fun toggle(row: Int, step: Int) {
        setStep(row, step, null)
    }

    fun paint(row: Int, step: Int, value: Boolean) {
        setStep(row, step, value)
    }

    fun playTarget() {
        val current = puzzle ?: return
        startLoop(current.target, target = true)
    }

    fun playMine() {
        startLoop(_state.value.pattern, target = false)
    }

    fun stop() {
        loopJob?.cancel()
        loopJob = null
        _state.update {
            it.copy(playheadStep = -1, playingTarget = false, playingMine = false)
        }
    }

    fun submit() {
        val current = puzzle ?: return
        stop()
        val tries = (triesUsed + 1).coerceAtMost(GameConfig.MAX_SUBMISSIONS)
        triesUsed = tries
        val result = score(current, tries)
        _state.update {
            it.copy(
                triesLeft = result.triesLeft,
                lockedRows = result.matchedRows,
                threadProgress = result.matchedRows.map { matched -> if (matched) 1f else 0f }
            )
        }
        if (result.solved || result.triesLeft <= 0) {
            finish(result)
        }
    }

    fun endRound() {
        val current = puzzle ?: return
        stop()
        val tries = (triesUsed + 1).coerceAtMost(GameConfig.MAX_SUBMISSIONS)
        triesUsed = tries
        finish(score(current, tries))
    }

    override fun onCleared() {
        super.onCleared()
        loopJob?.cancel()
    }

    private fun score(current: Puzzle, tries: Int): SubmissionResult = scoreSubmission(
        puzzle = current,
        pattern = _state.value.pattern,
        lockedRows = _state.value.lockedRows,
        triesUsed = tries,
        maxTries = GameConfig.MAX_SUBMISSIONS
    )

    private fun finish(result: SubmissionResult) {
        puzzle = null
        progressRepository.record(result)
        viewModelScope.launch { _events.send(result) }
    }

    private fun setStep(row: Int, step: Int, value: Boolean?) {
        val current = _state.value
        if (!current.ready || current.lockedRows.getOrElse(row) { false }) return
        val rows = current.pattern.toMutableList()
        val line = rows.getOrNull(row)?.toMutableList() ?: return
        if (step !in line.indices) return
        val next = value ?: !line[step]
        if (line[step] == next) return
        line[step] = next
        rows[row] = line
        _state.update { it.copy(pattern = rows) }
        if (next) {
            current.voices.getOrNull(row)?.let { voice -> synth.play(voice, volume) }
        }
    }

    private fun startLoop(rows: List<List<Boolean>>, target: Boolean) {
        val current = puzzle ?: return
        loopJob?.cancel()
        _state.update { it.copy(playingTarget = target, playingMine = !target) }
        loopJob = viewModelScope.launch {
            val stepMs = (60_000L / current.bpm / 4).coerceAtLeast(45L)
            for (step in 0 until GameConfig.STEPS_PER_BAR) {
                _state.update { it.copy(playheadStep = step) }
                rows.forEachIndexed { row, line ->
                    if (line.getOrElse(step) { false }) {
                        current.voices.getOrNull(row)?.let { voice -> synth.play(voice, volume) }
                    }
                }
                delay(stepMs)
            }
            _state.update {
                it.copy(playheadStep = -1, playingTarget = false, playingMine = false)
            }
        }
    }
}
