package com.example.connect4.utility

const val numCols: Int = 7
const val numRows: Int = 6
const val segmentLength: Int = 4

data class Location(val col: Int, val row: Int)
typealias Segment = List<Location>

fun generateSegments(): List<Segment> {
    val segments = mutableListOf<Segment>()
    for (c in 0 until numCols) {
        for (r in 0 until numRows - segmentLength + 1) {
            segments += listOf(
                Location(c, r),
                Location(c, r + 1),
                Location(c, r + 2),
                Location(c, r + 3)
            )
        }
    }
    for (c in 0 until numCols - segmentLength + 1) {
        for (r in 0 until numRows) {
            segments += listOf(
                Location(c, r),
                Location(c + 1, r),
                Location(c + 2, r),
                Location(c + 3, r)
            )
        }
    }
    for (c in 0 until numCols - segmentLength + 1) {
        for (r in 0 until numRows - segmentLength + 1) {
            segments += listOf(
                Location(c, r),
                Location(c + 1, r + 1),
                Location(c + 2, r + 2),
                Location(c + 3, r + 3)
            )
        }
    }
    for (c in 0 until numCols - segmentLength + 1) {
        for (r in segmentLength - 1 until numRows) {
            segments += listOf(
                Location(c, r),
                Location(c + 1, r - 1),
                Location(c + 2, r - 2),
                Location(c + 3, r - 3)
            )
        }
    }
    return segments
}

val allSegments: List<Segment>
    get() = generateSegments()

class C4Board(
    val position: MutableList<MutableList<Piece>> = MutableList(numCols) { MutableList(numRows) { Piece.Empty } },
    private val colCount: MutableList<Int> = MutableList(numCols) { 0 },
    private var turn: Piece
): Board {

    companion object {
        val New = C4Board(turn = Piece.Black)
    }

    override fun isWin(): Boolean {
        return allSegments.any { segment ->
            segment.count(Piece.Black) == segmentLength || segment.count(Piece.Red) == segmentLength
        }
    }

    override fun isDraw(): Boolean {
        return !isWin() && legalMoves().isEmpty()
    }

    override fun evaluate(player: Piece): Float {
        return allSegments.fold(0f) { accumulator, segment ->
            accumulator + segment.evaluate(player) - segment.evaluate(player.opposite())
        }
    }

    override fun legalMoves(): List<Move> {
        return colCount.mapIndexedNotNull { index, count ->
            index.takeIf { count < numRows }
        }
    }

    override fun makeMove(move: Move): Board {
        return copy().apply {
            position[move][colCount[move]] = turn
            colCount[move] = colCount[move] + 1
            turn = turn.opposite()
        }
    }

    override fun turn(): Piece = turn

    override fun asString(): String {
        val result = StringBuilder()
        for (row in numRows - 1 downTo 0) {
            for (col in 0 until numCols) {
                result.append(position[col][row].asString())
            }
            result.append("\n")
        }
        return result.toString()
    }

    private fun copy(
        position: MutableList<MutableList<Piece>> = this.position.map { it.toMutableList() }.toMutableList(),
        colCount: MutableList<Int> = this.colCount.toMutableList(),
        turn: Piece = this.turn
    ) = C4Board(position, colCount, turn)

    private fun Segment.evaluate(player: Piece): Float {
        return listOf(0f, 1f, 10f, 100f, 1000000f)[count(player)]
    }

    private fun Segment.count(player: Piece): Int {
        return count { position[it.col][it.row] == player }
    }
}