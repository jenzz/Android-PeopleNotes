package com.jenzz.peoplenotes.common.ui.widgets

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.jenzz.peoplenotes.common.ui.ToastMessage
import com.jenzz.peoplenotes.common.ui.ToastMessageId
import com.jenzz.peoplenotes.common.ui.asString
import com.jenzz.peoplenotes.ext.showShortToast

@Composable
fun Toast(
    message: ToastMessage,
    onToastShown: (ToastMessageId) -> Unit,
) {
    val context = LocalContext.current
    val text = message.text.asString()
    context.showShortToast(text)
    onToastShown(message.id)
}
