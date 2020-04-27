package ru.skillbranch.skillarticles.markdown.spans

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.text.style.ReplacementSpan
import androidx.annotation.ColorInt
import androidx.annotation.Px
import androidx.annotation.VisibleForTesting

class InlineCodeSpan(
    @ColorInt
    private val textColor: Int,
    @ColorInt
    private val bgColor: Int,
    @Px
    private val cornerRadius: Float,
    @Px
    private val padding: Float
) : ReplacementSpan() {
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    var rect: RectF = RectF()
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    var measureWidth: Int = 0

    override fun getSize(
        paint: Paint,
        text: CharSequence,
        start: Int,
        end: Int,
        fm: Paint.FontMetricsInt?
    ): Int {
        paint.forText {
            val measureText = paint.measureText(text.toString(), start, end)
            measureWidth = (measureText + 2*padding).toInt()
        }
        return measureWidth
    }

    override fun draw(
        canvas: Canvas,
        text: CharSequence,
        start: Int,
        end: Int,
        x: Float,
        top: Int,
        y: Int,
        bottom: Int,
        paint: Paint
    ) {
        paint.forBackground {
            rect.set(x, top.toFloat(), x+ measureWidth, bottom.toFloat())
            canvas.drawRoundRect(rect, cornerRadius, cornerRadius, paint)
        }

        paint.forText {
            canvas.drawText(text, start, end, x+padding, y.toFloat(), paint)
        }
    }

    private inline fun Paint.forText(block: () -> Unit) {
        val origSyze = textSize
        val origStyle = typeface?.style ?: 0
        val origFont = typeface
        val origColor = color

        color = textColor
        typeface = Typeface.create(Typeface.MONOSPACE, origStyle)
        textSize *= 0.85f

        block()

        color = origColor
        typeface = origFont
        textSize = origSyze
    }

    private inline fun Paint.forBackground(block: () -> Unit) {
        val origColor = color
        val origStyle = style

        color = bgColor
        style = Paint.Style.FILL

        block()

        color = origColor
        style = origStyle
    }
}