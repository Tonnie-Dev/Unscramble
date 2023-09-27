package com.uxstate.unscramble.ui

import androidx.lifecycle.ViewModel
import com.uxstate.unscramble.data.allWords
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class GameViewModel: ViewModel() {

    private var _uiState = MutableStateFlow(GameUiState())

    //makes this mutable state flow a read-only state flow
    val uiState = _uiState.asStateFlow()

private lateinit var currentWord:String
private var usedWords:MutableSet<String> = mutableSetOf()


    init {
        resetGame()
    }
private fun pickRandomWordAndShuffle():String {
    currentWord = allWords.random()

    return if (usedWords.contains(currentWord))

        pickRandomWordAndShuffle()
    else {

        usedWords.add(currentWord)

        shuffleCurrentWord(currentWord)
    }

}



    private fun shuffleCurrentWord(word:String):String{

        val tempWord = word.toCharArray()
        tempWord.shuffle()

        while (String(tempWord) == word){
            tempWord.shuffle()

        }

        return String(tempWord)
    }

    fun resetGame(){

        _uiState.update { it.copy(currentScrambledWord = pickRandomWordAndShuffle()) }
    }
}

