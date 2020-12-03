package com.softbankrobotics.dx.dynamicconversationmenusample

import android.os.Bundle
import android.os.Handler
import android.util.Log
import com.aldebaran.qi.Future
import com.aldebaran.qi.sdk.QiContext
import com.aldebaran.qi.sdk.QiSDK
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks
import com.aldebaran.qi.sdk.builder.SayBuilder
import com.aldebaran.qi.sdk.design.activity.RobotActivity
import com.aldebaran.qi.sdk.design.activity.conversationstatus.SpeechBarDisplayStrategy
import kotlinx.android.synthetic.main.activity_security.*

class SecurityActivity : RobotActivity(), RobotLifecycleCallbacks {

    companion object {
        private const val TAG = "SecurityActivity"
    }

    private lateinit var qiContext: QiContext
    private var mainFuture: Future<Void>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        QiSDK.register(this, this)
        setContentView(R.layout.activity_security)
        setSpeechBarDisplayStrategy(SpeechBarDisplayStrategy.IMMERSIVE)

        homeButton.setOnClickListener {
            finish()
        }
    }

    override fun onRobotFocusGained(qiContext: QiContext) {
        Log.i(TAG, "Robot focus gained")
        this.qiContext = qiContext

        mainFuture = buildSayFuture(getString(R.string.say_security1))?.thenCompose {
            runOnUiThread {
                image.setImageResource(R.drawable.security2)
            }
            return@thenCompose buildSayFuture(getString(R.string.say_security2))
        }?.thenCompose {
            runOnUiThread {
                image.setImageResource(R.drawable.security3)
            }
            return@thenCompose buildSayFuture(getString(R.string.say_security3))
        }?.thenCompose {
            runOnUiThread {
                image.setImageResource(R.drawable.security4)
            }
            return@thenCompose buildSayFuture(getString(R.string.say_security4))
        }?.thenCompose {
            runOnUiThread {
                image.setImageResource(R.drawable.security5)
            }
            return@thenCompose buildSayFuture(getString(R.string.say_security5))
        }?.thenCompose {
            runOnUiThread {
                image.setImageResource(R.drawable.security6)
            }
            buildSayFuture(getString(R.string.say_security6))
        }?.thenConsume {
            runOnUiThread {
                Handler().postDelayed({
                    finish()
                }, 5000)
            }
        }
    }

    private fun buildSayFuture(message: String): Future<Void>? {
        return SayBuilder.with(qiContext)
            .withText(message)
            .build()
            .async()
            .run()
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