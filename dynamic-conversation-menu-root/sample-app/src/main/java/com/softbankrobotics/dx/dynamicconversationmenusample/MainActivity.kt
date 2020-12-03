package com.softbankrobotics.dx.dynamicconversationmenusample

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import com.aldebaran.qi.Future
import com.aldebaran.qi.sdk.QiContext
import com.aldebaran.qi.sdk.QiSDK
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks
import com.aldebaran.qi.sdk.`object`.conversation.*
import com.aldebaran.qi.sdk.builder.ChatBuilder
import com.aldebaran.qi.sdk.builder.QiChatbotBuilder
import com.aldebaran.qi.sdk.builder.TopicBuilder
import com.aldebaran.qi.sdk.design.activity.RobotActivity
import com.aldebaran.qi.sdk.design.activity.conversationstatus.SpeechBarDisplayStrategy
import com.softbankrobotics.dx.dynamicconversationmenu.DynamicConversationMenuFragment
import com.softbankrobotics.dx.dynamicconversationmenu.MenuItemData
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : RobotActivity(), RobotLifecycleCallbacks {

    companion object {
        private const val TAG = "MainActivity"
    }

    private val fragmentManager = supportFragmentManager

    private lateinit var qiContext: QiContext
    private lateinit var topic: Topic
    private var startBookmark: Bookmark? = null
    private lateinit var qiChatbot: QiChatbot
    private lateinit var chat: Chat
    private var chatFuture: Future<Void>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        QiSDK.register(this, this)
        setContentView(R.layout.activity_main)
        setSpeechBarDisplayStrategy(SpeechBarDisplayStrategy.IMMERSIVE)

        homeButton.setOnClickListener {
            qiChatbot.async()?.goToBookmark(
                startBookmark,
                AutonomousReactionImportance.HIGH,
                AutonomousReactionValidity.IMMEDIATE
            )
        }
    }

    override fun onRobotFocusGained(qiContext: QiContext) {
        Log.i(TAG, "Robot focus gained")
        this.qiContext = qiContext

        buildAndRunChat()
    }

    private fun buildAndRunChat() {
        // Create the topic
        topic = TopicBuilder.with(qiContext)
            .withResource(R.raw.main_topic)
            .build()

        // Create the QiChatbot
        qiChatbot = QiChatbotBuilder.with(qiContext)
            .withTopic(topic)
            .build()

        // Launch actions to do when reaching Bookmarks
        qiChatbot.addOnBookmarkReachedListener {
            val bookmarkName = it.name
            Log.i(TAG, "Bookmark reached: $bookmarkName")
            when (bookmarkName) {
                "start" -> {
                    displayMainMenu()
                }
                "home" -> {
                    qiChatbot.goToBookmark(
                        startBookmark,
                        AutonomousReactionImportance.HIGH,
                        AutonomousReactionValidity.IMMEDIATE
                    )
                }
                "security" -> {
                    startActivity(Intent(this, SecurityActivity::class.java))
                    runOnUiThread {
                        menuContainer.visibility = View.GONE
                    }
                }
                "flights" -> {
                    displayFlightsMenu()
                }
                "departures" -> {
                    startActivity(Intent(this, FlightsActivity::class.java))
                    runOnUiThread {
                        homeButton.visibility = View.GONE
                        menuContainer.visibility = View.GONE
                    }
                }
                "arrivals" -> {
                    val intent = Intent(this, FlightsActivity::class.java)
                    intent.putExtra("DISPLAY_ARRIVALS", true)
                    startActivity(intent)
                    runOnUiThread {
                        homeButton.visibility = View.GONE
                        menuContainer.visibility = View.GONE
                    }
                }
                "transports" -> {
                    displayTransportsMenu()
                }
                "busses" -> {
                    startActivity(Intent(this, TransportsActivity::class.java))
                    runOnUiThread {
                        menuContainer.visibility = View.GONE
                    }
                }
                "trains" -> {
                    val intent = Intent(this, TransportsActivity::class.java)
                    intent.putExtra("DISPLAY_TRAINS", true)
                    startActivity(intent)
                    runOnUiThread {
                        homeButton.visibility = View.GONE
                        menuContainer.visibility = View.GONE
                    }
                }
                "taxis" -> {
                    val intent = Intent(this, TransportsActivity::class.java)
                    intent.putExtra("DISPLAY_TAXIS", true)
                    startActivity(intent)
                    runOnUiThread {
                        homeButton.visibility = View.GONE
                        menuContainer.visibility = View.GONE
                    }
                }
                "shops" -> {
                    startActivity(Intent(this, ShopsActivity::class.java))
                    runOnUiThread {
                        menuContainer.visibility = View.GONE
                    }
                }
            }
        }

        // Create the Chat action
        chat = ChatBuilder.with(qiContext)
            .withChatbot(qiChatbot)
            .build()

        // Go to "start" bookmark when launching Chat
        chat.addOnStartedListener {
            topic.async().bookmarks.andThenConsume {
                startBookmark = it["start"]
                qiChatbot.goToBookmark(
                    startBookmark,
                    AutonomousReactionImportance.HIGH,
                    AutonomousReactionValidity.IMMEDIATE
                )
            }
        }

        // Launch Chat
        chatFuture = chat.async().run()
    }

    private fun displayMainMenu() {
        // Don't display "home" button
        runOnUiThread {
            homeButton.visibility = View.GONE
        }

        // Create the list of the cards to be displayed
        val menuItemList: ArrayList<MenuItemData> = ArrayList()
        menuItemList.add(
            MenuItemData(
                getString(R.string.security),
                R.drawable.security,
                "security"
            )
        )
        menuItemList.add(
            MenuItemData(
                getString(R.string.flights),
                R.drawable.flights,
                "flights"
            )
        )
        menuItemList.add(
            MenuItemData(
                getString(R.string.transports),
                R.drawable.transports,
                "transports"
            )
        )
        menuItemList.add(
            MenuItemData(
                getString(R.string.shops),
                R.drawable.shops,
                "shops"
            )
        )

        // Add the DynamicConversationMenuFragment
        val fragmentTransaction = fragmentManager.beginTransaction()
        val fragment = DynamicConversationMenuFragment(
            menuItemList = menuItemList,
            topic = topic,
            qiChatbot = qiChatbot
        )
        fragmentTransaction.replace(R.id.menuContainer, fragment)
        fragmentTransaction.commit()

        runOnUiThread {
            menuContainer.visibility = View.VISIBLE
        }
    }

    private fun displayFlightsMenu() {
        // Enable "home" button
        runOnUiThread {
            homeButton.visibility = View.VISIBLE
        }

        // Create the list of the cards to be displayed
        val menuItemList: ArrayList<MenuItemData> = ArrayList()
        menuItemList.add(
            MenuItemData(
                getString(R.string.departures),
                R.drawable.departures,
                "departures"
            )
        )
        menuItemList.add(
            MenuItemData(
                getString(R.string.arrivals),
                R.drawable.arrivals,
                "arrivals"
            )
        )

        // Add the DynamicConversationMenuFragment
        val fragmentTransaction = fragmentManager.beginTransaction()
        val fragment = DynamicConversationMenuFragment(
            menuItemList = menuItemList,
            topic = topic,
            qiChatbot = qiChatbot
        )
        fragmentTransaction.replace(R.id.menuContainer, fragment)
        fragmentTransaction.commit()
    }

    private fun displayTransportsMenu() {
        // Enable "home" button
        runOnUiThread {
            homeButton.visibility = View.VISIBLE
        }

        // Create the list of the cards to be displayed
        val menuItemList: ArrayList<MenuItemData> = ArrayList()
        menuItemList.add(
            MenuItemData(
                getString(R.string.trains),
                R.drawable.trains,
                "trains"
            )
        )
        menuItemList.add(
            MenuItemData(
                getString(R.string.busses),
                R.drawable.busses,
                "busses"
            )
        )
        menuItemList.add(
            MenuItemData(
                getString(R.string.taxis),
                R.drawable.taxis,
                "taxis"
            )
        )

        // Add the DynamicConversationMenuFragment
        val fragmentTransaction = fragmentManager.beginTransaction()
        val fragment = DynamicConversationMenuFragment(
            menuItemList = menuItemList,
            topic = topic,
            qiChatbot = qiChatbot
        )
        fragmentTransaction.replace(R.id.menuContainer, fragment)
        fragmentTransaction.commit()
    }

    override fun onRobotFocusRefused(reason: String) {
        Log.e(TAG, "Robot focus refused: $reason")
    }

    override fun onRobotFocusLost() {
        Log.i(TAG, "Robot focus lost")
        chatFuture?.requestCancellation()
        chat.removeAllOnStartedListeners()
        qiChatbot.removeAllOnBookmarkReachedListeners()
    }

    override fun onDestroy() {
        super.onDestroy()
        QiSDK.unregister(this, this)
    }
}
