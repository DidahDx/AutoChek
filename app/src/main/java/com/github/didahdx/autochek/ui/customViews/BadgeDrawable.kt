package com.github.didahdx.autochek.ui.customViews

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.github.didahdx.autochek.R

/**
 * @author Daniel Didah on 6/13/22
 */
class BadgeDrawable(private val paramContext: Context) : Drawable() {
    private val mBadgePaint: Paint
    private val mBadgePaintLarge: Paint
    private var mCount = ""
    private var mCountText = ""
    private val mTextPaint: Paint
    private val mTextSize: Float
    private val mTxtRect = Rect()
    private var mWillDraw = false
    override fun draw(paramCanvas: Canvas) {
        if (!mWillDraw) {
            return
        }
        val localRect = bounds
        val width = (localRect.right - localRect.left).toFloat()
        val height = (localRect.bottom - localRect.top).toFloat()
        var circleRadius: Float
//        circleRadius = Math.min(width, height) / 4.0f + 2.5f
        circleRadius = if (mCount.toInt() < 10) {
            Math.min(width, height) / 4.0f + 2.5f
        } else {
            Math.min(width, height) / 4.0f + 4.5f
        }
        val circleX = width - circleRadius + 6.2f
        val circleY = circleRadius - 9.5f
        paramCanvas.drawCircle(circleX, circleY, circleRadius + 3.0f, mBadgePaintLarge)
        paramCanvas.drawCircle(circleX, circleY, circleRadius + 1.0f, mBadgePaint)
        mTextPaint.getTextBounds(mCount, 0, mCount.length, mTxtRect)
        var textY = circleY + (mTxtRect.bottom - mTxtRect.top) / 2.0f
        var textX = circleX
        if (mCount.toInt() >= 10) {
            textX = textX - 1.0f
            textY = textY - 1.0f
        }
        paramCanvas.drawText(mCountText, textX, textY, mTextPaint)
    }

    override fun getOpacity(): Int {
        return 0
    }

    override fun setAlpha(paramInt: Int) {}
    override fun setColorFilter(paramColorFilter: ColorFilter?) {}
    fun setCount(paramInt: Int) {
        if (paramInt > 99) {
            mCountText = "99+"
            mCount = "1000"
        } else {
            mCount = Integer.toString(paramInt)
            mCountText = mCount
        }


        if (paramInt > 0) {
            mWillDraw = true
            invalidateSelf()
        }

    }

    init {
        mTextSize = paramContext.resources.getDimension(
            R.dimen.badge_text_size
        )
        mBadgePaint = Paint()
        mBadgePaint.color = ContextCompat.getColor(
            paramContext,
            R.color.black
        )
        mBadgePaint.isAntiAlias = true
        mBadgePaint.style = Paint.Style.FILL

        mBadgePaintLarge = Paint()
        mBadgePaintLarge.color = ContextCompat.getColor(
            paramContext,
            R.color.white
        )
        mBadgePaintLarge.isAntiAlias = true
        mBadgePaintLarge.style = Paint.Style.FILL
        mTextPaint = Paint()
        mTextPaint.color = Color.parseColor("#FFCC66")
        mTextPaint.typeface = Typeface.DEFAULT
        mTextPaint.textSize = mTextSize
        mTextPaint.isAntiAlias = true
        mTextPaint.textAlign = Paint.Align.CENTER
    }
}
