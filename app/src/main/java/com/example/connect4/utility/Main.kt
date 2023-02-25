package com.example.connect4.utility

var gameBoard: Board = C4Board.New

fun getPlayerMove(): Move {
    var playerMove: Move = 1000
    while (playerMove !in gameBoard.legalMoves()) {
        print("Enter a column (0-6): ")
        val input = readlnOrNull()
        playerMove = input?.toIntOrNull()?.takeIf { it in 0..6 } ?: continue
    }
    return playerMove
}

fun main() {
    while (true) {
        val humanMove = getPlayerMove()
        gameBoard = gameBoard.makeMove(humanMove)
        if (gameBoard.isWin()) {
            print(gameBoard.asString())
            println("Human wins!")
            break
        } else if (gameBoard.isDraw()) {
            print(gameBoard.asString())
            println("Draw!")
            break
        }
        val computerMove = findBestMove(gameBoard, 5)
        println("Computer move is $computerMove")
        gameBoard = gameBoard.makeMove(computerMove)
        print(gameBoard.asString())
        if (gameBoard.isWin()) {
            println("Computer wins!")
            break
        } else if (gameBoard.isDraw()) {
            println("Draw!")
            break
        }
    }
}