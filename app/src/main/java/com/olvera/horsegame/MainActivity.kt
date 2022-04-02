package com.olvera.horsegame

import android.graphics.Point
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.os.postDelayed
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private var mHandler: Handler? = null
    private var timeSeconds: Long = 0
    private var widht_bonus = 0
    private var gaming = true

    private var cellSelected_x = 0
    private var cellSelected_y = 0

    private var levelMoves = 64
    private var movesRequired = 4
    private var moves = 64
    private var options = 0
    private var bonus = 0

    private var checkMovement = true

    private var nameColorBlack = "black_cell"
    private var nameColorWhite = "white_cell"


    private lateinit var board: Array<IntArray>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initScreenGame()
        startGame()
    }

    fun checkCellClicked(v: View) {
        var name = v.tag.toString()
        var x = name.subSequence(1, 2).toString().toInt()
        var y = name.subSequence(2, 3).toString().toInt()
        checkCell(x, y)
    }

    private fun checkCell(x: Int, y: Int) {
        var dif_x = x - cellSelected_x
        var dif_y = y - cellSelected_y
        var checkTrue = true

        if (checkMovement) {
            checkTrue = false
            if (dif_x == 1 && dif_y == 2) checkTrue = true // right - top Long
            if (dif_x == 1 && dif_y == -2) checkTrue = true // right - bottom long
            if (dif_x == 2 && dif_y == 1) checkTrue = true // right long - top
            if (dif_x == 2 && dif_y == -1) checkTrue = true // right long - bottom
            if (dif_x == -1 && dif_y == 2) checkTrue = true // left - top long
            if (dif_x == -1 && dif_y == -2) checkTrue = true // left - bottom long
            if (dif_x == -2 && dif_y == 1) checkTrue = true // left long - top
            if (dif_x == -2 && dif_y == -1) checkTrue = true // left long - bottom
        } else {
            if (board[x][y] != 1) {
                bonus--
                var tvBonusData = findViewById<TextView>(R.id.tvBonusData)
                tvBonusData.text = " + $bonus"

                if (bonus == 0) tvBonusData.text = ""
            }
        }

        if (board[x][y] == 1) checkTrue = false

        if (checkTrue) selectCell(x, y)

    }

    private fun resetBoard() {
        // 0 esta libre
        // 1 casilla marcada
        // 2 es un bonus
        // 9 es una opcion del movimiento actual

        board = arrayOf(
            intArrayOf(0, 0, 0, 0, 0, 0, 0, 0),
            intArrayOf(0, 0, 0, 0, 0, 0, 0, 0),
            intArrayOf(0, 0, 0, 0, 0, 0, 0, 0),
            intArrayOf(0, 0, 0, 0, 0, 0, 0, 0),
            intArrayOf(0, 0, 0, 0, 0, 0, 0, 0),
            intArrayOf(0, 0, 0, 0, 0, 0, 0, 0),
            intArrayOf(0, 0, 0, 0, 0, 0, 0, 0),
            intArrayOf(0, 0, 0, 0, 0, 0, 0, 0)
            )
    }

    private fun setFirstPosition() {
        var x = 0
        var y = 0

        x = (0..7).random()
        y = (0..7).random()

        cellSelected_x = x
        cellSelected_y = y

        selectCell(x, y)
    }

    private fun selectCell( x: Int, y: Int) {

        moves--

        var tvMovesData = findViewById<TextView>(R.id.tvMovesData)
        tvMovesData.text = moves.toString()

        growProgressBonus()

        if (board[x][y] == 2) {
            bonus++

            var tvBonusData = findViewById<TextView>(R.id.tvBonusData)
            tvBonusData.text = " + $bonus"
        }

        board[x][y] = 1
        paintHorseCell(cellSelected_x, cellSelected_y, "previus_cell")

        cellSelected_x = x
        cellSelected_y = y

        clearOptions()

        paintHorseCell(x, y, "selected_cell")
        checkMovement = true
        checkOption(x, y)

        if (moves >= 0) {
            checkNewBonus()
            checkGameOver()
        } else {
            showMessage ("You win!", "Next Level", false)
        }
    }

    private fun checkGameOver() {
        if (options == 0) {
            if (bonus > 0) {
                checkMovement = false
                paintAllOptions()
            }
            else {
                showMessage ("Game Over", "Try Again!", true)
            }
        }
    }

    private fun paintAllOptions() {
        for (i in 0..7) {
            for( j in 0..7) {
                if (board[i][j] != 1) paintOption(i, j)
                if (board[i][j] == 0) board[i][j] = 9

            }
        }
    }

    private fun showMessage(title: String, action: String, gameOver: Boolean) {

        gaming = false

        var lyMessage = findViewById<LinearLayout>(R.id.lyMessage)
        lyMessage.visibility = View.VISIBLE

        var tvTitleMessage = findViewById<TextView>(R.id.tvTitleMessage)
        tvTitleMessage.text = title

        var tvTimeData = findViewById<TextView>(R.id.tvTimeData)
        var score: String = ""
        if (gameOver) {
            score = "Score: " + (levelMoves-moves) + "/" + levelMoves
        } else {
            score = tvTimeData.text.toString()
        }

        var tvScoreMessage = findViewById<TextView>(R.id.tvScoreMessage)
        tvScoreMessage.text = score

        var tvAction = findViewById<TextView>(R.id.tvAction)
        tvAction.text = action
    }
    private fun growProgressBonus() {

        var moves_done = levelMoves - moves

        var bonus_done = moves_done / movesRequired
        var moves_rest = movesRequired * (bonus_done)
        var bonus_grow = moves_done - moves_rest

        var v = findViewById<View>(R.id.vNewBonus)
        var widthBonus = ((widht_bonus/movesRequired) * bonus_grow).toFloat()

        var heigth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8f, resources.displayMetrics).toInt()
        var width = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, widthBonus, resources.displayMetrics).toInt()
        v.layoutParams = TableRow.LayoutParams(width, heigth)

    }

    private fun checkNewBonus() {
        if (moves % movesRequired == 0) {
            var bonusCell_x = 0
            var bonusCell_y = 0

            var bonusCell = false

            while (bonusCell == false) {
                bonusCell_x = (0..7).random()
                bonusCell_y = (0..7).random()

                if (board[bonusCell_x][bonusCell_y] == 0) bonusCell = true
            }

            board[bonusCell_x][bonusCell_y] = 2
            paintBonusCell(bonusCell_x, bonusCell_y)
        }
    }

    private fun paintBonusCell(x: Int, y: Int) {
        var iv: ImageView = findViewById(resources.getIdentifier("c$x$y", "id", packageName))
        iv.setImageResource(R.drawable.bonus)
    }

    private fun clearOptions() {
        for (i in 0..7) {
            for (j in 0..7) {
                if (board[i][j] == 9 || board[i][j] == 2) {
                    if (board[i][j] == 9) board[i][j] == 0
                    clearOption(i, j)
                }
            }
        }
    }

    private fun clearOption(x: Int, y: Int) {
        var iv: ImageView = findViewById(resources.getIdentifier("c$x$y", "id", packageName))

        if (checkColorCell(x, y) == "black") {
            iv.setBackgroundColor(ContextCompat.getColor(this, resources.getIdentifier(nameColorBlack, "color", packageName)))
        } else {
            iv.setBackgroundColor(ContextCompat.getColor(this, resources.getIdentifier(nameColorWhite, "color", packageName)))
        }

        if (board[x][y] == 1) iv.setBackgroundColor(ContextCompat.getColor(this, resources.getIdentifier("previus_cell", "color", packageName)))

    }

    private fun checkOption( x: Int, y: Int) {
        options = 0

        checkMove(x, y, 1, 2) // check move right - top long
        checkMove(x, y, 2, 1) // check move right long - top
        checkMove(x, y, 1, -2) // check move right - bottom long
        checkMove(x, y, 2, -1) // check move right long - bottom
        checkMove(x, y, -1, 2) // check move left - top long
        checkMove(x, y, -2, 1) // check move left long - top
        checkMove(x, y, -1, -2) // check move left - bottom long
        checkMove(x, y, -2, -1) // check move left long - bottom

        var tvOptionsData = findViewById<TextView>(R.id.tvOptionsData)
        tvOptionsData.text = options.toString()


    }

    private fun checkMove(x: Int, y: Int, mov_x: Int, mov_y: Int) {
        var option_x = x + mov_x
        var option_y = y + mov_y

        if (option_x < 8 && option_y < 8 && option_x >= 0 && option_y >= 0) {
            if (board[option_x][option_y] == 0 ||
                board[option_x][option_y] == 2) {
                options++
                paintOption(option_x, option_y)
                if (board[option_x][option_y] == 0) board[option_x][option_y] = 9
            }
        }
    }

    private fun paintOption( x: Int, y: Int) {
        var iv: ImageView = findViewById(resources.getIdentifier("c$x$y", "id", packageName))
        if (checkColorCell(x, y) == "black") iv.setBackgroundResource(R.drawable.option_black)
        else iv.setBackgroundResource(R.drawable.option_white)
    }

    private fun checkColorCell(x: Int, y: Int): String {
        var color = ""
        var blackColumn_x = arrayOf(0, 2, 4, 6)
        var blackRow_x = arrayOf(1, 3, 5, 7)

        if ((blackColumn_x.contains(x) && blackColumn_x.contains(y))
            || (blackRow_x.contains(x) && blackRow_x.contains(y))) {
            color = "black"
        } else {
            color = "white"
        }

        return color

    }
    private fun paintHorseCell(x: Int, y: Int, color: String) {
        var iv: ImageView = findViewById(resources.getIdentifier("c$x$y", "id", packageName))
        iv.setBackgroundColor(ContextCompat.getColor(this, resources.getIdentifier(color, "color", packageName)))
        iv.setImageResource(R.drawable.horse)
    }

    private fun initScreenGame() {
        setSizeBoard()
        hide_message()
    }

    private fun setSizeBoard(){
        var iv: ImageView

        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        val width = size.x

        var width_dp = (width / resources.displayMetrics.density)

        var lateralMarginsDP = 0
        val width_cell = (width_dp - lateralMarginsDP)/8
        val heigth_cell = width_cell

        widht_bonus = 2 * width_cell.toInt()

        for (i in 0..7){
            for (j in 0..7){
                iv = findViewById(resources.getIdentifier("c$i$j", "id", packageName))
                var heigth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, heigth_cell, resources.displayMetrics).toInt()
                var width = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, width_cell, resources.displayMetrics).toInt()

                iv.layoutParams = TableRow.LayoutParams(width, heigth)
            }
        }
    }

    private fun hide_message() {
        val lyMessage = findViewById<LinearLayout>(R.id.lyMessage)
        lyMessage.visibility = View.INVISIBLE
    }

    private fun clearBoard() {
        var iv: ImageView

        var colorBlack = ContextCompat.getColor(this, resources.getIdentifier(nameColorBlack, "color", packageName))
        var colorWhite = ContextCompat.getColor(this, resources.getIdentifier(nameColorWhite, "color", packageName))

        for (i in 0..7) {
            for (j in 0..7) {
                iv = findViewById(resources.getIdentifier("c$i$j", "id", packageName))
                iv.setImageResource(0)

                if (checkColorCell(i, j) == "black") iv.setBackgroundColor(colorBlack)
                else iv.setBackgroundColor(colorWhite)
            }
        }

    }

    private fun resetTime() {
        mHandler?.removeCallbacks(chronometer)
        timeSeconds = 0
        var tvTimeData = findViewById<TextView>(R.id.tvTimeData)
        tvTimeData.text = "00:00"

    }

    private fun startTime() {
        mHandler = Handler(Looper.getMainLooper()!!)
        chronometer.run()
    }

    private var chronometer: Runnable = object: Runnable {
        override fun run() {
            try {

                if (gaming) {
                    timeSeconds++
                    updateStopWatchView(timeSeconds)
                }

            } finally {
                mHandler!!.postDelayed(this, 1000L)
            }
        }
    }

    private fun updateStopWatchView(timeInSeconds: Long) {
        val formattedTime = getFormattedStopWatch((timeInSeconds * 1000))
        var tvTimeData = findViewById<TextView>(R.id.tvTimeData)
        tvTimeData.text = formattedTime
    }

    private fun getFormattedStopWatch(ms: Long): String {
        var milliseconds = ms
        val minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds)
        milliseconds -= TimeUnit.MINUTES.toMillis(minutes)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds)

        return "${if (minutes < 10) "0" else ""}$minutes" + ":" +
                "${if (seconds < 10) "0" else ""}$seconds"
    }

    private fun startGame() {

        gaming = true

        resetBoard()
        clearBoard()
        setFirstPosition()

        resetTime()
        startTime()
    }

}