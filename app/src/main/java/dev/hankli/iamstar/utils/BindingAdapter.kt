package dev.hankli.iamstar.utils

import android.annotation.SuppressLint
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import dev.hankli.iamstar.R
import java.text.DateFormatSymbols
import java.text.SimpleDateFormat
import java.util.*

const val SECOND_MILLIS = 1000
const val MINUTE_MILLIS = 60 * SECOND_MILLIS
const val HOUR_MILLIS = 60 * MINUTE_MILLIS

@BindingAdapter("setImage")
fun setImage(imageView: ImageView, url: Any?) {
    url?.let {
        Glide.with(imageView.context.applicationContext).load(it)
            .placeholder(R.drawable.placeholder).into(imageView)
    } ?: imageView.setImageResource(R.drawable.placeholder)
}

@BindingAdapter("imageFromUrl")
fun listingImageFromUrl(view: ImageView, imageUrl: String?) {
    if (!imageUrl.isNullOrEmpty()) {
        Glide.with(view.context.applicationContext)
            .load(imageUrl)
            .placeholder(R.drawable.no_image_placeholder)
            .error(R.drawable.no_image_placeholder)
            .into(view)
    } else {
        view.setImageResource(R.drawable.no_image_placeholder)
    }
}


fun getMonthForInt(num: Int): String? {
    var month = "wrong"
    val dfs = DateFormatSymbols()
    val months: Array<String> = dfs.months
    if (num in 0..11) {
        month = months[num]
    }
    return month.substring(IntRange(0, 2))
}


//@BindingAdapter("chatUrl")
//fun setChatUrl(imageView: ImageView, message: MessageData?) {
//    message?.let {
//        when {
//            it.url.isNotEmpty() -> {
//                Glide.with(imageView)
//                    .load(it.url)
//                    .placeholder(R.drawable.no_image_placeholder)
//                    .error(R.drawable.no_image_placeholder)
//                    .apply(RequestOptions().override(600, 600))
//                    .centerCrop()
//                    .into(imageView)
//                imageView.visibility = View.VISIBLE
//            }
//            it.videoURL.isNotEmpty() -> {
//                Glide.with(imageView).load(message.videoURL)
//                    .placeholder(R.drawable.no_image_placeholder)
//                    .error(R.drawable.no_image_placeholder)
//                    .apply(RequestOptions().override(600, 600))
//                    .centerInside()
//                    .into(imageView)
//                imageView.visibility = View.VISIBLE
//
//            }
//            else -> {
//                imageView.visibility = View.GONE
//            }
//        }
//    }
//}

@SuppressLint("SimpleDateFormat", "SetTextI18n")
@BindingAdapter("setMessageTime")
fun setMessageTime(textView: TextView, time: Long) {
    val sfd = SimpleDateFormat("dd/MM/yyyy hh:mm a")
    val dateAndTime = sfd.format(Date(time)).split(" ")
    textView.text = "${dateAndTime[1]} ${dateAndTime[2]}\n${dateAndTime[0]}"
}

//@SuppressLint("SetTextI18n")
//@BindingAdapter("conversation")
//fun setTime(textView: TextView, conversationModel: ConversationModel) {
//    var timeMilis = conversationModel.lastMessageDate.seconds
//    if (timeMilis < 1000000000000L) {
//        // if timestamp given in seconds, convert to millis
//        timeMilis *= 1000
//    }
//    val sfd = SimpleDateFormat("hh:mm a")
//    val time = sfd.format(Date(timeMilis))
//    val diff = System.currentTimeMillis() - timeMilis
//    when {
//        diff < MINUTE_MILLIS -> textView.text = "${conversationModel.lastMessage} • $time"
//        diff < 2 * MINUTE_MILLIS -> textView.text = "${conversationModel.lastMessage} • $time"
//        diff < 50 * MINUTE_MILLIS -> textView.text =
//            "${conversationModel.lastMessage} • $time"
//        diff < 90 * MINUTE_MILLIS -> textView.text = "${conversationModel.lastMessage} • $time"
//        diff < 24 * HOUR_MILLIS -> textView.text =
//            "${conversationModel.lastMessage} • $time"
//        diff < 48 * HOUR_MILLIS -> textView.text = "${conversationModel.lastMessage} • yesterday"
//        else -> {
//            val cal: Calendar = Calendar.getInstance()
//            cal.timeInMillis = timeMilis
//            textView.text =
//                "${conversationModel.lastMessage} • ${getMonthForInt(cal.get(Calendar.MONTH))} ${cal.get(
//                    Calendar.DAY_OF_MONTH
//                )}"
//        }
//    }
//}

//@BindingAdapter("contactStatus")
//fun setContactStatus(button: Button, type: ContactType) {
//    when (type) {
//        ContactType.PENDING -> button.text = button.context.getString(R.string.cancel_request)
//        ContactType.FRIEND -> button.text = button.context.getString(R.string.unfriend)
//        ContactType.BLOCKED -> button.text = button.context.getString(R.string.unblock)
//        ContactType.UNKNOWN -> button.text = button.context.getString(R.string.add_friend)
//        ContactType.ACCEPT -> button.text = button.context.getString(R.string.accept)
//    }
//}

//@BindingAdapter("blurrEffect")
//fun setBlurrEffect(target: ImageView, imageUrl: String?) {
//    imageUrl?.let {
//        Glide.with(target.context)
//            .load(it)
//            .placeholder(R.drawable.call_bg_for_no_image)
//            .error(R.drawable.call_bg_for_no_image)
//            .apply(bitmapTransform(BlurTransformation(25, 2)))
//            .into(target)
//    }
//}
//
//@BindingAdapter("setMessageTime")
//fun setMessageTime(textView: TextView, time: Long) {
//    val sfd = java.text.SimpleDateFormat("dd/MM/yyyy hh:mm a")
//    val dateAndTime = sfd.format(Date(time)).split(" ")
//    textView.text = "${dateAndTime[1]} ${dateAndTime[2]}\n${dateAndTime[0]}"
//}