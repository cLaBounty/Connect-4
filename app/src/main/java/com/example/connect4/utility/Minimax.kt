package com.example.connect4.utility

fun miniMax(board: Board, maximizing: Boolean, originalPlayer: Piece, depth: Int): Float {
    // Base Case
    if (board.isWin() || board.isDraw() || depth == 0) {
        return board.evaluate(originalPlayer)
    }

    // Recursive Case
    return board.legalMoves().fold(
        if (maximizing) Float.NEGATIVE_INFINITY else Float.POSITIVE_INFINITY
    ) { bestEval, move ->
        val result = miniMax(board.makeMove(move), !maximizing, originalPlayer, depth - 1)
        val condition = if (maximizing) { result > bestEval } else { result < bestEval }
        return@fold if (condition) result else bestEval
    }
}

fun findBestMove(board: Board, depth: Int): Move {
    return board.legalMoves().maxBy { move ->
        miniMax(board.makeMove(move), false, board.turn(), depth)
    }
}