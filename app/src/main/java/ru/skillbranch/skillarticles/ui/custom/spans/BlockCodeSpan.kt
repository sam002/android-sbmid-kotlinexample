package ru.skillbranch.skillarticles.ui.custom.spans

import android.graphics.*
import android.text.style.ReplacementSpan
import androidx.annotation.ColorInt
import androidx.annotation.Px
import androidx.annotation.VisibleForTesting
import ru.skillbranch.skillarticles.data.repositories.Element


class BlockCodeSpan(
    @ColorInt
    private val textColor: Int,
    @ColorInt
    private val bgColor: Int,
    @Px
    private val cornerRadius: Float,
    @Px
    private val padding: Float,
    private val type: Element.BlockCode.Type
) : ReplacementSpan() {
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    var rect = RectF()
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    var path = Path()

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
            when (type) {
                Element.BlockCode.Type.START -> {
                    rect.set(x, top.toFloat()+padding, canvas.width.toFloat(), bottom.toFloat())
                    val corners = floatArrayOf(
                        cornerRadius, cornerRadius,   // Top left radius in px
                        cornerRadius, cornerRadius,   // Top right radius in px
                        0f, 0f,     // Bottom right radius in px
                        0f, 0f// Bottom left radius in px
                    )

                    path.reset()
                    path.addRoundRect(rect, corners, Path.Direction.CW)
                    canvas.drawPath(path, paint)
                }
                Element.BlockCode.Type.MIDDLE -> {
                    rect.set(x, top.toFloat(), canvas.width.toFloat(), bottom.toFloat())
                    canvas.drawRect(rect,paint)
                }
                Element.BlockCode.Type.END -> {
                    rect.set(x, top.toFloat(), canvas.width.toFloat(), bottom.toFloat()-padding)
                    val corners = floatArrayOf(
                        0f, 0f,   // Top left radius in px
                        0f, 0f,   // Top right radius in px
                        cornerRadius, cornerRadius,     // Bottom right radius in px
                        cornerRadius, cornerRadius      // Bottom left radius in px
                    )

                    path.reset()
                    path.addRoundRect(rect, corners, Path.Direction.CW)
                    canvas.drawPath(path, paint)
                }
                Element.BlockCode.Type.SINGLE -> {
                    rect.set(x, top.toFloat() + padding, canvas.width.toFloat(), bottom.toFloat() - padding)
                    canvas.drawRoundRect(rect, cornerRadius, cornerRadius, paint)
                }

            }
        }

        paint.forText {
            canvas.drawText(text, start, end, x+padding, y.toFloat(), paint)
        }
    }

    override fun getSize(
        paint: Paint,
        text: CharSequence,
        start: Int,
        end: Int,
        fm: Paint.FontMetricsInt?
    ): Int {

        if (fm != null) {
            val origAscent = paint.ascent()
            val origDescent = paint.descent()
            when (type) {
                Element.BlockCode.Type.START -> {
                    fm.ascent = (origAscent - 2*padding).toInt()
                    fm.descent = origDescent.toInt()
                }
                Element.BlockCode.Type.END -> {
                    fm.ascent = origAscent.toInt()
                    fm.descent = (origDescent + 2*padding).toInt()
                }
                Element.BlockCode.Type.MIDDLE -> {
                    fm.ascent = origAscent.toInt()
                    fm.descent = origDescent.toInt()
                }
                Element.BlockCode.Type.SINGLE -> {
                    fm.ascent = (origAscent - 2*padding).toInt()
                    fm.descent = (origDescent + 2*padding).toInt()
                }
            }
            fm.top = fm.ascent
            fm.bottom = fm.descent
        }
        return 0
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
