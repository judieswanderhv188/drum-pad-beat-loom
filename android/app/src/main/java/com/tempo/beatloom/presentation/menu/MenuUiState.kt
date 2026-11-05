package com.tempo.beatloom.presentation.menu

data class ChapterUi(
    val index: Int,
    val nameRes: Int,
    val solved: Int,
    val total: Int,
    val unlocked: Boolean,
    val firstPuzzleId: Int
) {
    val progress: Float get() = if (total == 0) 0f else solved.toFloat() / total
}

data class MenuStat(
    val labelRes: Int,
    val value: String,
    val accentIndex: Int
)

data class MenuUiState(
    val ready: Boolean = false,
    val solvedTotal: Int = 0,
    val totalPuzzles: Int = 0,
    val nextPuzzleId: Int = 0,
    val nextStyleRes: Int = 0,
    val nextLoopNumber: Int = 1,
    val stats: List<MenuStat> = emptyList(),
    val chapters: List<ChapterUi> = emptyList()
)
