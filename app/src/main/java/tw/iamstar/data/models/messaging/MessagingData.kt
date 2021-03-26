package tw.iamstar.data.models.messaging

import android.os.Bundle

interface MessagingData {

    fun getTitleResId(): Int

    fun getDestId(): Int

    fun getArgs(): Bundle?
}