package com.uxstate.unscramble.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.uxstate.unscramble.data.MAX_NO_OF_WORDS
import com.uxstate.unscramble.data.SCORE_INCREASE
import com.uxstate.unscramble.data.allWords
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class GameViewModel : ViewModel() {

    private var _uiState = MutableStateFlow(GameUiState())

    //makes this mutable state flow a read-only state flow
    val uiState = _uiState.asStateFlow()

    private lateinit var currentWord: String
    private var usedWords: MutableSet<String> = mutableSetOf()

    //mutableStateOf() so that Compose observes this value
    var userGuess by mutableStateOf("")
        private set

    init {
        resetGame()
    }

    private fun pickRandomWordAndShuffle(): String {
        currentWord = allWords.random()

        return if (usedWords.contains(currentWord))

            pickRandomWordAndShuffle()
        else {

            usedWords.add(currentWord)

            shuffleCurrentWord(currentWord)
        }

    }


    private fun shuffleCurrentWord(word: String): String {

        val tempWord = word.toCharArray()
        tempWord.shuffle()

        while (String(tempWord) == word) {
            tempWord.shuffle()

        }

        return String(tempWord)
    }

    fun updateUserGuess(guessWord: String) {
        userGuess = guessWord

    }


    fun checkUserGuess() {

        if (userGuess.equals(currentWord, ignoreCase = true)) {

            _uiState.update { it.copy(score = it.score.plus(SCORE_INCREASE)) }
            updateGameState(_uiState.value.score)

        } else {

            _uiState.update { it.copy(isGuessedWordWrong = true) }
        }

        updateUserGuess("")
    }


    fun skipWord() {

        updateGameState(_uiState.value.score)
        updateUserGuess("")
    }

    private fun updateGameState(updatedScore: Int) {

        if (usedWords.size == MAX_NO_OF_WORDS) {
            //last round in the game

            _uiState.update {
                it.copy(
                        isGuessedWordWrong = false,
                        score = updatedScore,
                        isGameOver = true
                )
            }
        } else {
            //normal game round
            _uiState.update {
                it.copy(
                        isGuessedWordWrong = false,
                        currentScrambledWord = pickRandomWordAndShuffle(),
                        score = updatedScore,
                        currentWordCount = it.currentWordCount.inc()
                )
            }

        }
    }

    fun resetGame() {

        _uiState.update { it.copy(currentScrambledWord = pickRandomWordAndShuffle()) }
    }
}

