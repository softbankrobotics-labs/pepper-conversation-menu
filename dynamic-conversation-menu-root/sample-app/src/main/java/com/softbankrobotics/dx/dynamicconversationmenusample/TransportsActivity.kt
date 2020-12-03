package com.softbankrobotics.dx.dynamicconversationmenusample

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import com.aldebaran.qi.Future
import com.aldebaran.qi.sdk.QiContext
import com.aldebaran.qi.sdk.QiSDK
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks
import com.aldebaran.qi.sdk.builder.ListenBuilder
import com.aldebaran.qi.sdk.builder.PhraseSetBuilder
import com.aldebaran.qi.sdk.builder.SayBuilder
import com.aldebaran.qi.sdk.design.activity.RobotActivity
import com.aldebaran.qi.sdk.design.activity.conversationstatus.SpeechBarDisplayStrategy
import kotlinx.android.synthetic.main.activity_flights.listView
import kotlinx.android.synthetic.main.activity_security.homeButton
import kotlinx.android.synthetic.main.activity_transports.*

class TransportsActivity : RobotActivity(), RobotLifecycleCallbacks {

    companion object {
        private const val TAG = "FlightsActivity"
    }

    private var displayTrains = false
    private var displayTaxis = false
    private var mainFuture: Future<Void>? = null

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        QiSDK.register(this, this)
        setContentView(R.layout.activity_transports)
        setSpeechBarDisplayStrategy(SpeechBarDisplayStrategy.IMMERSIVE)

        homeButton.setOnClickListener {
            finish()
        }

        displayTrains = intent.getBooleanExtra("DISPLAY_TRAINS", false)
        displayTaxis = intent.getBooleanExtra("DISPLAY_TAXIS", false)

        if (displayTrains) {
            nextLabel.text = "Next train"
        } else if (displayTaxis) {
            lineLabel.text = "Destination"
            destinationLabel.visibility = View.INVISIBLE
            nextLabel.visibility = View.INVISIBLE
        }

        val transportList = ArrayList<Transport>()
        when {
            displayTrains -> {
                transportList.add(Transport("RER B", "Paris Centre", "11.40€", "12:34"))
                transportList.add(Transport("RER B", "Paris Centre", "11.40€", "12:39"))
                transportList.add(Transport("RER B", "Paris Centre", "11.40€", "12:44"))
                transportList.add(Transport("TGV", "Lyon", "112€", "12:46"))
                transportList.add(Transport("RER B", "Paris Centre", "11.40€", "12:49"))
                transportList.add(Transport("TGV", "Strasbourg", "88€", "12:52"))
                transportList.add(Transport("RER B", "Paris Centre", "11.40€", "12:54"))
            }
            displayTaxis -> {
                transportList.add(Transport("Belleville", "", "53€", ""))
                transportList.add(Transport("Panthéon", "", "58€", ""))
                transportList.add(Transport("Montparnasse", "", "58€", ""))
                transportList.add(Transport("Opéra", "", "53€", ""))
                transportList.add(Transport("Arc de Triomphe", "", "53€", ""))
                transportList.add(Transport("Concorde", "", "53€", ""))
                transportList.add(Transport("Odéon", "", "58€", ""))
            }
            else -> {
                transportList.add(Transport("RoissyBus", "Opéra", "13.70€", "06:45"))
                transportList.add(Transport("350", "Porte de la Chapelle", "6€", "06:50"))
                transportList.add(Transport("351", "Nation", "6€", "06:55"))
                transportList.add(Transport("19", "Torcy RER", "2€", "07:05"))
                transportList.add(Transport("Noctilien", "Paris Centre", "8€", "07:15"))
                transportList.add(Transport("VEA", "Disneyland Pairs", "23€", "07:45"))
                transportList.add(Transport("Filéo", "Roissy", "1.14€", "08:00"))
            }
        }
        val adapter = CustomTransportAdapter(this, transportList)
        listView.adapter = adapter
    }

    override fun onRobotFocusGained(qiContext: QiContext) {
        Log.i(TAG, "Robot focus gained")

        val textToSay = when {
            displayTrains -> {
                getString(R.string.say_trains)
            }
            displayTaxis -> {
                getString(R.string.say_taxis)
            }
            else -> {
                getString(R.string.say_busses)
            }
        }
        mainFuture = SayBuilder.with(qiContext)
            .withText(textToSay)
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