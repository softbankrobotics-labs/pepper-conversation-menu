package com.softbankrobotics.dx.dynamicconversationmenusample

import android.annotation.SuppressLint
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
import kotlinx.android.synthetic.main.activity_flights.*
import kotlinx.android.synthetic.main.activity_security.homeButton
import java.util.*

class FlightsActivity : RobotActivity(), RobotLifecycleCallbacks {

    companion object {
        private const val TAG = "FlightsActivity"
    }

    private var displayArrivals = false
    private var mainFuture: Future<Void>? = null

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        QiSDK.register(this, this)
        setContentView(R.layout.activity_flights)
        setSpeechBarDisplayStrategy(SpeechBarDisplayStrategy.IMMERSIVE)

        homeButton.setOnClickListener {
            finish()
        }

        displayArrivals = intent.getBooleanExtra("DISPLAY_ARRIVALS", false)

        if (displayArrivals) {
            destinationLabel.text = "Origin"
        }

        val flightsList = ArrayList<Flight>()
        if (displayArrivals) {
            flightsList.add(Flight("Terminal D", "BA117", "Edinburgh", "06:45", "40", "Arrived"))
            flightsList.add(Flight("Terminal B", "CX1347", "Bucharest", "06:50", "ZZ", "Arrived"))
            flightsList.add(Flight("Terminal A", "BA165", "Brussels", "06:55", "10", "Arrived"))
            flightsList.add(Flight("Terminal D", "BA3925", "Amsterdam", "07:05", "41", "Landing"))
            flightsList.add(Flight("Terminal D", "AF1007DL", "Istanbul", "07:15", "4Z", "Landing"))
            flightsList.add(
                Flight(
                    "Terminal C",
                    "AF1765KL",
                    "Copenhagen",
                    "07:45",
                    "02",
                    "On time"
                )
            )
            flightsList.add(Flight("Terminal Z", "AZ3635", "Glasgow", "07:45", "23", "On time"))
            flightsList.add(Flight("Terminal B", "LH4Z4", "Zurich", "08:00", "43", "Delayed"))
            flightsList.add(Flight("Terminal B", "LH1072AY", "Rome", "08:00", "11", "On time"))
            flightsList.add(Flight("Terminal D", "EK050", "Newcastle", "08:00", "1Z", "On time"))
            flightsList.add(
                Flight(
                    "Terminal D",
                    "IB3533LB",
                    "Dusseldorf",
                    "08:10",
                    "45",
                    "Delayed"
                )
            )
            flightsList.add(Flight("Terminal A", "IB3533", "Berlin", "08:20", "31", "On time"))
            flightsList.add(Flight("Terminal A", "AB3120", "Aberdeen", "08:25", "04", "On time"))
            flightsList.add(Flight("Terminal C", "NSZ26", "Kyiv", "08:30", "24", "On time"))
            flightsList.add(Flight("Terminal D", "NY120", "Paris CDG", "08:35", "47", "On time"))
        } else {
            flightsList.add(Flight("Terminal D", "BA117", "Edinburgh", "06:45", "40", "Check-in"))
            flightsList.add(Flight("Terminal B", "CX1347", "Bucharest", "06:50", "ZZ", "Check-in"))
            flightsList.add(Flight("Terminal A", "BA165", "Brussels", "06:55", "10", "Check-in"))
            flightsList.add(Flight("Terminal D", "BA3925", "Amsterdam", "07:05", "41", "Check-in"))
            flightsList.add(Flight("Terminal D", "AF1007DL", "Istanbul", "07:15", "4Z", "Check-in"))
            flightsList.add(
                Flight(
                    "Terminal C",
                    "AF1765KL",
                    "Copenhagen",
                    "07:45",
                    "02",
                    "On time"
                )
            )
            flightsList.add(Flight("Terminal Z", "AZ3635", "Glasgow", "07:45", "23", "On time"))
            flightsList.add(Flight("Terminal B", "LH4Z4", "Zurich", "08:00", "43", "Delayed"))
            flightsList.add(Flight("Terminal B", "LH1072AY", "Rome", "08:00", "11", "On time"))
            flightsList.add(Flight("Terminal D", "EK050", "Newcastle", "08:00", "1Z", "On time"))
            flightsList.add(
                Flight(
                    "Terminal D",
                    "IB3533LB",
                    "Dusseldorf",
                    "08:10",
                    "45",
                    "Delayed"
                )
            )
            flightsList.add(Flight("Terminal A", "IB3533", "Berlin", "08:20", "31", "On time"))
            flightsList.add(Flight("Terminal A", "AB3120", "Aberdeen", "08:25", "04", "On time"))
            flightsList.add(Flight("Terminal C", "NSZ26", "Kyiv", "08:30", "24", "On time"))
            flightsList.add(Flight("Terminal D", "NY120", "Paris CDG", "08:35", "47", "On time"))
        }
        val adapter = CustomFlightsAdapter(this, flightsList)
        listView.adapter = adapter
    }

    override fun onRobotFocusGained(qiContext: QiContext) {
        Log.i(TAG, "Robot focus gained")

        val textToSay = if (displayArrivals) {
            getString(R.string.say_arrivals)
        } else {
            getString(R.string.say_departures)
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