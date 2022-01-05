package com.jenzz.peoplenotes.common.ui

import android.content.res.Resources
import androidx.annotation.StringRes

interface TextResource {

    fun asString(res: Resources): String

    companion object {

        fun fromText(text: String): TextResource =
            SimpleTextResource(text)

        fun fromId(@StringRes id: Int, vararg formatArgs: Any): TextResource =
            IdTextResource(id, formatArgs.asList())
    }
}

private data class SimpleTextResource(
    private val text: String,
) : TextResource {

    override fun asString(res: Resources): String = text
}

private data class IdTextResource(
    @StringRes private val id: Int,
    private val formatArgs: List<Any>,
) : TextResource {

    override fun asString(res: Resources): String {
        val args = formatArgs.map { arg -> if (arg is TextResource) arg.asString(res) else arg }
        return res.getString(id, *args.toTypedArray())
    }
}