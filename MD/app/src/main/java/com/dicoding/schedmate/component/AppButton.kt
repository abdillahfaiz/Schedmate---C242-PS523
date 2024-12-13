package com.dicoding.schedmate.component

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.Gravity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import com.dicoding.schedmate.R

class AppButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatButton(context, attrs) {

    private var txtColor: Int = 0
    private lateinit var enabledBackground: Drawable
    private lateinit var disabledBackground: Drawable
    private var fontSize : Float = 16f

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        background = if (isEnabled) enabledBackground else disabledBackground
        setTextColor(txtColor)
        textSize = fontSize
        gravity = Gravity.CENTER
    }

    init {

        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.AppButton)
        try {
            fontSize = typedArray.getDimension(R.styleable.AppButton_customFontSize, fontSize)
        } finally {
            typedArray.recycle()
        }

        txtColor = ContextCompat.getColor(context, android.R.color.background_light)
        enabledBackground = ContextCompat.getDrawable(context, R.drawable.button_bg) as Drawable
        disabledBackground = ContextCompat.getDrawable(context, R.drawable.button_bg_disable) as Drawable
    }

}