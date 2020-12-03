package com.softbankrobotics.dx.dynamicconversationmenusample

import android.os.Bundle
import android.util.Log
import com.aldebaran.qi.Future
import com.aldebaran.qi.sdk.QiContext
import com.aldebaran.qi.sdk.QiSDK
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks
import com.aldebaran.qi.sdk.builder.ListenBuilder
import com.aldebaran.qi.sdk.builder.PhraseSetBuilder
import com.aldebaran.qi.sdk.builder.SayBuilder
import com.aldebaran.qi.sdk.design.activity.RobotActivity
import com.aldebaran.qi.sdk.design.activity.conversationstatus.SpeechBarDisplayStrategy
import kotlinx.android.synthetic.main.activity_security.*

class ShopsActivity : RobotActivity(), RobotLifecycleCallbacks {

    companion object {
        private const val TAG = "ShopsActivity"
    }

    private var mainFuture: Future<Void>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        QiSDK.register(this, this)
        setContentView(R.layout.activity_shops)
        setSpeechBarDisplayStrategy(SpeechBarDisplayStrategy.IMMERSIVE)

        homeButton.setOnClickListener {
            finish()
        }
    }

    override fun onRobotFocusGained(qiContext: QiContext) {
        Log.i(TAG, "Robot focus gained")

        mainFuture = SayBuilder.with(qiContext)
            .withText(getString(R.string.say_shops))
            .build()
            .async()
            .run()
            .thenCompose {
                val phrases = resources.getStringArray(R.array.listen_home)
                val phraseSet = PhraseSetBuilder.with(qiContext)
                    .withTexts(*phrases)
                    .build()
                return@thenCompose ListenBuilder.with(qiContext)
                    .withPhraseSet(phraseSet)
                    .build()
                    .async()
                    .run()
            }?.andThenConsume {
                finish()
            }
    }

    override fun onRobotFocusRefused(reason: String) {
        Log.e(TAG, "Robot focus refused: $reason")
    }

    override fun onRobotFocusLost() {
        Log.i(TAG, "Robot focus lost")
        mainFuture?.requestCancellation()
    }

    override fun onDestroy() {
        super.onDestroy()
        QiSDK.unregister(this, this)
    }
}