package com.example.connect4

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.connect4.ui.theme.Connect4Theme
import com.example.connect4.utility.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Connect4Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    MainScreen()
                }
            }
        }
    }
}

class MainViewModel: ViewModel() {
    private val boardMutableLiveData = MutableLiveData(C4Board.New)
    val boardLiveData: LiveData<C4Board> = boardMutableLiveData
    private val board: C4Board get() = boardLiveData.value!!

    private fun setBoard(board: C4Board) {
        boardMutableLiveData.value = board
    }

    fun restart() = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            delay(3000L)
            withContext(Dispatchers.Main) {
                setBoard(C4Board.New)
            }
        }
    }

    private fun performComputerMove() = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            val computerMove = findBestMove(board, 3)
            withContext(Dispatchers.Main) {
                setBoard(board.makeMove(computerMove) as C4Board)
            }
        }
    }

    fun performPlayerMove(move: Move) {
        if (board.turn() != Piece.Black) return
        if (move !in board.legalMoves()) return
        setBoard(board.makeMove(move) as C4Board)
        if (!board.isWin() && !board.isDraw()) performComputerMove()
    }
}

@Composable
fun MainScreen() {
    val viewModel: MainViewModel = viewModel()
    val board by viewModel.boardLiveData.observeAsState()
    val context = LocalContext.current

    if (board?.isWin() == true || board?.isDraw() == true) {
        val messageId = when {
            board?.isDraw() == true -> R.string.message_draw
            board?.turn() == Piece.Black -> R.string.message_lose
            else -> R.string.message_win
        }
        Toast.makeText(context, stringResource(messageId), Toast.LENGTH_SHORT).show()
        viewModel.restart()
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Connect4Grid(
            board = board ?: C4Board.New,
            onItemClick = { viewModel.performPlayerMove(it) }
        )
    }
}

@Composable
private fun Connect4Grid(board: C4Board, onItemClick: (Int) -> Unit) {
    LazyVerticalGrid(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Blue, RoundedCornerShape(8.dp)),
        columns = GridCells.Fixed(numCols),
        contentPadding = PaddingValues(8.dp)
    ) {
        for (row in numRows - 1 downTo 0) {
            for (col in 0 until numCols) {
                item {
                    Connect4GridItem(
                        piece = board.position[col][row],
                        onClick = { onItemClick(col) }
                    )
                }
            }
        }
    }
}

@Composable
private fun Connect4GridItem(piece: Piece, onClick: () -> Unit) {
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .aspectRatio(1f)
            .padding(all = 2.dp)
            .clickable { onClick() }
    ) {
        drawCircle(
            color = if (piece == Piece.Empty) Color.White else if (piece == Piece.Black) Color.Black else Color.Red,
            radius = size.minDimension / 2f
        )
    }
}