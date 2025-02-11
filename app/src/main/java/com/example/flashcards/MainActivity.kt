package com.example.flashcards

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Card
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flashcards.ui.theme.FlashcardsTheme
import kotlinx.coroutines.delay
import org.xmlpull.v1.XmlPullParser
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.text.style.TextAlign


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FlashcardsTheme {
                FlashCardScreen()
            }
        }
    }
}
// Data class
data class FlashCard(val question: String, val answer: String)

@Composable
fun FlashCardScreen() {
    val context = LocalContext.current
    val initialFlashcards = remember { parse(context) }
    var cards by remember { mutableStateOf(initialFlashcards) }

    //shuffle every 15s
    LaunchedEffect(Unit) {
        while (true) {
            delay(15000)
            cards = cards.shuffled()
        }
    }

    // horizontal list of flashcards
    LazyRow(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        items(cards) { card ->
            FlashcardView(card)
        }
    }
}

fun parse(context: Context):List<FlashCard> {
    val list = mutableListOf<FlashCard>()
    val parser = context.resources.getXml(R.xml.flashcards)
    var event = parser.eventType
    var q: String? = null
    var a: String? = null
    while (event != XmlPullParser.END_DOCUMENT) {
        when (event) {
            XmlPullParser.START_TAG -> {
                when (parser.name) {
                    "question" -> {
                        parser.next(); q = parser.text
                    }

                    "answer" -> {
                        parser.next(); a = parser.text
                    }
                }
            }

            XmlPullParser.END_TAG -> {
                if (parser.name == "flashcard" && q != null && a != null) {
                    list.add(FlashCard(q, a))
                    q = null; a = null
                }
            }
        }
        event = parser.next()
    }
    return list
}
@Composable
fun FlashcardView(card: FlashCard) {
    var flipped by remember { mutableStateOf(false) }
    val angle by animateFloatAsState(targetValue = if (flipped) 180f else 0f)

    Card(
        modifier = Modifier
            .size(
                width = 250.dp,
                height = 150.dp
            )
            .clickable { flipped = !flipped }
            .graphicsLayer {
                rotationY = angle
                cameraDistance = 8 * density
            }
    ) {
        val textFlip = if (angle > 90f) 180f else 0f
        val text = if (angle < 90f) card.question else card.answer
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.graphicsLayer { rotationY = textFlip }
        ) {

            Text(
                text = text,
                fontSize = 18.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    FlashcardsTheme {
        FlashCardScreen()
    }
}