package com.example.connect4.utility

typealias Move = Int

enum class Piece {
    Empty, Black, Red;

    fun opposite(): Piece {
        if (this == Empty) return Empty
        return enumValues<Piece>()[3 - this.ordinal]
    }

    fun asString(): String = when(this) {
        Empty -> "⚪"
        Black -> "🔵"
        Red -> "🔴"
    }
}

interface Board {
    fun isWin(): Boolean
    fun isDraw(): Boolean
    fun evaluate(player: Piece): Float
    fun legalMoves(): List<Move>
    fun makeMove(move: Move): Board
    fun turn(): Piece
    fun asString(): String
}