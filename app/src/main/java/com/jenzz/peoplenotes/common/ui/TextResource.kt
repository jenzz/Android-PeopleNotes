package com.jenzz.peoplenotes.common.ui

import android.content.res.Resources
import android.os.Parcelable
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import kotlinx.parcelize.Parcelize

interface TextResource : Parcelable {

    fun asString(res: Resources): String

    companion object {

        fun fromText(text: String): TextResource =
            SimpleTextResource(text)

        fun fromId(@StringRes id: Int): TextResource =
            IdTextResource(id, emptyList())

        fun fromId(@StringRes id: Int, vararg formatArgs: String): TextResource =
            IdTextResource(id, formatArgs.map { formatArg -> fromText(formatArg) })

        fun fromId(@StringRes id: Int, vararg formatArgs: TextResource): TextResource =
            IdTextResource(id, formatArgs.asList())
    }
}

@Composable
fun TextResource.asString(): String {
    val context = LocalContext.current
    return asString(context.resources)
}

@Parcelize
private data class SimpleTextResource(
    private val text: String,
) : TextResource {

    override fun asString(res: Resources): String = text
}

@Parcelize
private data class IdTextResource(
    @StringRes private val id: Int,
    private val formatArgs: List<TextResource>,
) : TextResource {

    override fun asString(res: Resources): String {
        val args = formatArgs.map { arg -> arg.asString(res) }
        return res.getString(id, *args.toTypedArray())
    }
}
