package ru.skillbranch.skillarticles.ui.custom

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.VisibleForTesting
import androidx.core.view.setPadding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import ru.skillbranch.skillarticles.R
import ru.skillbranch.skillarticles.data.ArticleItemData
import ru.skillbranch.skillarticles.extensions.dpToIntPx
import ru.skillbranch.skillarticles.extensions.format
import kotlin.math.abs
import kotlin.math.max

@SuppressLint("ViewConstructor")
class ArticleItemView(context: Context) : ViewGroup(context, null, 0) {

    //views
    val tv_date: TextView
    val tv_author: TextView
    val tv_title: TextView
    val tv_description: TextView
    val tv_likes_count: TextView
    val tv_comments_count: TextView
    val tv_read_duration: TextView
    val iv_poster: ImageView
    val iv_category: ImageView
    val iv_likes: ImageView
    val iv_comments: ImageView
    val iv_bookmark: ImageView

    //colors
    @ColorInt
    private val colorGray: Int = context.getColor(R.color.color_gray)

    @ColorInt
    private val colorPrimary: Int = context.getColor(R.color.color_primary)

    //sizes
    private val basePadding = context.dpToIntPx(16)
    private val posterSize = context.dpToIntPx(64)
    private val categorySize = context.dpToIntPx(40)
    private val imagesSize = posterSize + categorySize / 2
    private val bottomIconSize = context.dpToIntPx(16)
    private val cornerRadius = context.dpToIntPx(8)
    private val baseMargin = context.dpToIntPx(8)


    private val baseFontSize = 12f
    private val descriptionFontSize = 14f
    private val titleFontSize = 18f

    init {
        setPadding(basePadding)

        tv_date = TextView(context).apply {
            textSize = baseFontSize
            setTextColor(colorGray)
            id = R.id.tv_date
        }
        addView(tv_date)

        tv_author = TextView(context).apply {
            textSize = baseFontSize
            setTextColor(colorPrimary)
            id = R.id.tv_author
        }
        addView(tv_author)

        tv_title = TextView(context).apply {
            textSize = titleFontSize
            setTextColor(colorPrimary)
            setTypeface(typeface, Typeface.BOLD)
            id = R.id.tv_title
        }
        addView(tv_title)

        iv_poster = ImageView(context).apply {
            layoutParams = LayoutParams(posterSize, posterSize)
            id = R.id.iv_poster
        }
        addView(iv_poster)

        iv_category = ImageView(context).apply {
            layoutParams = LayoutParams(categorySize, categorySize)
            id = R.id.iv_category
        }
        addView(iv_category)

        tv_description = TextView(context).apply {
            textSize = descriptionFontSize
            setTextColor(colorGray)
            id = R.id.tv_description
        }
        addView(tv_description)

        iv_likes = ImageView(context).apply {
            layoutParams = LayoutParams(bottomIconSize, bottomIconSize)
            id = R.id.iv_likes
            setImageResource(R.drawable.ic_favorite_black_24dp)
            imageTintList = ColorStateList.valueOf(colorGray)
        }
        addView(iv_likes)

        tv_likes_count = TextView(context).apply {
            textSize = baseFontSize
            setTextColor(colorGray)
            id = R.id.tv_likes_count
        }
        addView(tv_likes_count)

        iv_comments = ImageView(context).apply {
            layoutParams = LayoutParams(bottomIconSize, bottomIconSize)
            id = R.id.iv_comments
            setImageResource(R.drawable.ic_insert_comment_black_24dp)
            imageTintList = ColorStateList.valueOf(colorGray)
        }
        addView(iv_comments)

        tv_comments_count = TextView(context).apply {
            textSize = baseFontSize
            setTextColor(colorGray)
            id = R.id.tv_comments_count
        }
        addView(tv_comments_count)

        tv_read_duration = TextView(context).apply {
            textSize = baseFontSize
            setTextColor(colorGray)
            id = R.id.tv_read_duration
        }
        addView(tv_read_duration)


        iv_bookmark = ImageView(context).apply {
            layoutParams = LayoutParams(bottomIconSize, bottomIconSize)
            id = R.id.iv_bookmark
            setImageResource(R.drawable.bookmark_states)
            imageTintList = ColorStateList.valueOf(colorGray)
        }
        addView(iv_bookmark)
    }

    fun bind(item: ArticleItemData) {
        Glide.with(context)
            .load(item.poster)
            .transform(CenterCrop(), RoundedCorners(cornerRadius))
            .override(posterSize)
            .into(iv_poster)

        Glide.with(context)
            .load(item.categoryIcon)
            .transform(CenterCrop(), RoundedCorners(cornerRadius))
            .override(categorySize)
            .into(iv_category)

        tv_date.text = item.date.format()
        tv_author.text = item.author
        tv_title.text = item.title
        tv_description.text = item.description
        tv_likes_count.text = "${item.likeCount}"
        tv_comments_count.text = "${item.commentCount}"
        tv_read_duration.text = "${item.readDuration} min read"
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    public override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var usedHeight = paddingTop
        val width = getDefaultSize(suggestedMinimumWidth, widthMeasureSpec)
        val contentWidth = width - paddingLeft - paddingRight

        // first line
        measureChild(tv_date, widthMeasureSpec, heightMeasureSpec)
        tv_author.maxWidth = contentWidth - tv_date.width - basePadding
        measureChild(tv_author, widthMeasureSpec, heightMeasureSpec)
        usedHeight += tv_date.measuredHeight
        usedHeight += baseMargin

        // 2nd line
        tv_title.maxWidth = contentWidth - imagesSize - baseMargin / 2
        measureChild(tv_title, widthMeasureSpec, heightMeasureSpec)


        usedHeight += max(tv_title.measuredHeight, imagesSize)
        usedHeight += baseMargin

        // 3rd line
        measureChild(tv_description, widthMeasureSpec, heightMeasureSpec)
        usedHeight += tv_description.measuredHeight
        usedHeight += baseMargin

        // 4th line
//        measureChild(iv_likes, widthMeasureSpec, heightMeasureSpec)
        measureChild(tv_likes_count, widthMeasureSpec, heightMeasureSpec)
//        measureChild(iv_comments, widthMeasureSpec, heightMeasureSpec)
        measureChild(tv_comments_count, widthMeasureSpec, heightMeasureSpec)
        tv_read_duration.maxWidth = contentWidth - bottomIconSize * 3 - baseMargin * 8
        measureChild(tv_read_duration, widthMeasureSpec, heightMeasureSpec)
//        measureChild(iv_bookmark, widthMeasureSpec, heightMeasureSpec)

        usedHeight += tv_read_duration.measuredHeight

        usedHeight += paddingBottom
        setMeasuredDimension(width, usedHeight)
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    public override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        var usedHeight = paddingTop
        val bodyWidth = r - l - paddingLeft - paddingRight
        var left = paddingLeft
        val right = paddingLeft + bodyWidth

        // first line (author + date)
        tv_date.layout(
            left,
            usedHeight,
            left + tv_date.measuredWidth,
            usedHeight + tv_date.measuredHeight
        )
        left += tv_date.measuredWidth + basePadding

        tv_author.layout(
            left,
            usedHeight,
            left + tv_author.measuredWidth,
            usedHeight + tv_author.measuredHeight
        )

        usedHeight += tv_author.measuredHeight
        usedHeight += baseMargin
        left = paddingLeft

        // 2nd line
        val sizeDiff = abs(imagesSize - tv_title.measuredHeight) / 2
        if (imagesSize > tv_title.measuredHeight) {
            tv_title.layout(
                left,
                usedHeight + sizeDiff,
                left + tv_title.measuredWidth,
                usedHeight + tv_title.measuredHeight + sizeDiff
            )
            iv_poster.layout(
                right - posterSize,
                usedHeight,
                right,
                usedHeight + posterSize
            )
            iv_category.layout(
                right - imagesSize,
                usedHeight + posterSize - categorySize / 2,
                right - posterSize + categorySize / 2,
                usedHeight + imagesSize
            )

        } else {
            tv_title.layout(
                left,
                usedHeight,
                left + tv_title.measuredWidth,
                usedHeight + tv_title.measuredHeight
            )
            iv_poster.layout(
                right - posterSize,
                usedHeight + sizeDiff,
                right,
                usedHeight + sizeDiff + posterSize
            )
            iv_category.layout(
                right - imagesSize,
                usedHeight + sizeDiff + posterSize - categorySize / 2,
                right - posterSize + categorySize / 2,
                usedHeight + imagesSize + sizeDiff
            )
        }

        usedHeight += max(tv_title.measuredHeight, imagesSize)
        usedHeight += baseMargin

        // 3rd line
        tv_description.layout(
            left,
            usedHeight,
            right,
            usedHeight + tv_description.measuredHeight
        )

        usedHeight += tv_description.measuredHeight
        usedHeight += baseMargin

        // 4th line
        iv_likes.layout(
            left,
            usedHeight,
            left + bottomIconSize,
            usedHeight + bottomIconSize
        )
        left += bottomIconSize + baseMargin

        tv_likes_count.layout(
            left,
            usedHeight,
            left + tv_likes_count.measuredWidth,
            usedHeight + tv_likes_count.measuredHeight
        )
        left += bottomIconSize + tv_likes_count.measuredWidth + baseMargin * 2

        iv_comments.layout(
            left,
            usedHeight,
            left + bottomIconSize,
            usedHeight + bottomIconSize
        )
        left += bottomIconSize + baseMargin

        tv_comments_count.layout(
            left,
            usedHeight,
            left + tv_comments_count.measuredWidth,
            usedHeight + tv_comments_count.measuredHeight
        )
        left += bottomIconSize + tv_comments_count.measuredWidth + baseMargin * 2

        tv_read_duration.layout(
            left,
            usedHeight,
            left + tv_read_duration.measuredWidth,
            usedHeight + tv_read_duration.measuredHeight
        )

        iv_bookmark.layout(
            right - bottomIconSize,
            usedHeight,
            right,
            usedHeight + bottomIconSize
        )
    }
}