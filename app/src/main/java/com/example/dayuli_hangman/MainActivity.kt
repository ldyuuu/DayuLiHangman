package com.example.dayuli_hangman

import android.content.res.Configuration
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.dayuli_hangman.ui.theme.DayuLihangmanTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DayuLihangmanTheme {
                GuessWordGame()
            }
        }
    }
}


@Composable
fun GuessWordGame() {
    val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

    val wordHintList = listOf(
        "ROBOT" to "A machine capable of carrying out complex actions automatically.",
        "SPACESHIP" to "A vehicle used for travel in outer space.",
        "DINOSAUR" to "A large reptile from the Mesozoic era, now extinct.",
        "VOLCANO" to "A mountain that can erupt with lava and ash.",
        "TURTLE" to "A reptile with a hard shell that moves slowly.",
        "RAINBOW" to "A multicolored arc often seen after rain.",
        "CAMEL" to "An animal with humps, adapted to desert life.",
        "JUGGLER" to "A performer who keeps several objects in motion at once.",
        "OCTOPUS" to "A sea creature with eight arms and a bulbous head.",
        "MARATHON" to "A long-distance race, usually over 26 miles.",
        "CUPCAKE" to "A small cake baked in a cup-shaped container.",
        "PYTHON" to "A large non-venomous snake, or a popular programming language.",
        "BALLOON" to "An inflatable rubber bag that can float when filled with gas.",
        "LANTERN" to "A portable light source typically used outdoors.",
        "HURRICANE" to "A severe tropical storm with strong winds and heavy rain.",
        "TREASURE" to "A collection of valuable items like gold, gems, or coins.",
        "SKELETON" to "The internal framework of bones in an animal's body.",
        "MERMAID" to "A mythical creature with the upper body of a woman and the tail of a fish.",
        "BICYCLE" to "A two-wheeled vehicle that you pedal to move forward.",
        "PIRATE" to "A person who attacks ships at sea to steal valuables."
    )

    var currentWordIndex by rememberSaveable { mutableStateOf(0) }
    val (currentWord, currentHint) = wordHintList[currentWordIndex]
    var guessedLetters by rememberSaveable { mutableStateOf(listOf<Char>()) }
    var remainingTurns by rememberSaveable { mutableStateOf(6) }
    var hintState by rememberSaveable { mutableStateOf(0) }
    var hintText by rememberSaveable { mutableStateOf<String?>(null) }
    var showToast by remember { mutableStateOf<String?>(null) }
    val isGuessedCorrectly = currentWord.all { guessedLetters.contains(it) }

    val context = LocalContext.current

    val onNewGame: () -> Unit = {
        currentWordIndex = (currentWordIndex + 1) % wordHintList.size
        guessedLetters = listOf()
        remainingTurns = 6
        hintState = 0
        hintText=null
    }
    val restartCurrentWord: () -> Unit = {
        guessedLetters = listOf()
        remainingTurns = 6
        hintState = 0
        hintText=null
    }
    LaunchedEffect(remainingTurns) {
        when {
            remainingTurns == 1 -> {
                showToast = "You're about to die!"
            }
            remainingTurns == 0 -> {
                showToast = "You lost!"
                restartCurrentWord()
            }
        }
    }
    LaunchedEffect(isGuessedCorrectly) {
        when {
            isGuessedCorrectly -> {
                showToast = "You guessed it right!"
                onNewGame()
            }
        }
    }
    val onHintUsed: () -> Unit = {
        if (remainingTurns <= 1 && hintState >0 && hintState<3) {
            // If using the hint would cause the player to lose, show a toast
            showToast = "Hints not available!（1 hp left）"
        }  else {
            when (hintState) {
                0 -> {
                    hintText = currentHint
                    hintState++
                }
                1 -> {
                    val wrongLetters = ('A'..'Z').filter { !currentWord.contains(it) }
                    if (wrongLetters.isNotEmpty()) {
                        val lettersToDisable = wrongLetters.shuffled().take(wrongLetters.size / 2)
                        guessedLetters = guessedLetters + lettersToDisable
                    }
                    remainingTurns -= 1
                    hintState++
                }
                2 -> {
                    val vowels = listOf('A', 'E', 'I', 'O', 'U')
                    val vowelsInWord = vowels.filter { currentWord.contains(it) }
                    if (vowelsInWord.isNotEmpty()) {
                        guessedLetters = guessedLetters + vowelsInWord
                    }
                    remainingTurns -= 1
                    hintState++
                }
                else -> {
                    showToast = "No more hints available!"
                }
            }
        }
    }

    if (isLandscape) {
        Row(
            modifier = Modifier.fillMaxSize().padding(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            ) {
                Text(
                    text = "Choose the letter",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Panel1(
                    onLetterSelected = { letter ->
                        if (!guessedLetters.contains(letter)) {
                            guessedLetters = guessedLetters + letter
                            if (!currentWord.contains(letter)) {
                                remainingTurns -= 1
                            }
                        }
                    },
                    selectedLetters = guessedLetters,
                    modifier = Modifier.weight(3f)
                )
                hintText?.let {
                    Text(text = it, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(20.dp))
                }
                Panel2(
                    onHintUsed = onHintUsed,
                    hintState = hintState,
                    remainingLetters = ('A'..'Z').filterNot { guessedLetters.contains(it) },
                    modifier = Modifier.weight(1f)
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) { Spacer(modifier=Modifier.weight(1f))
                Panel3(
                    wordToGuess = currentWord,
                    guessedLetters = guessedLetters,
                    remainingTurns = remainingTurns,
                    modifier = Modifier.weight(3f).fillMaxWidth()
                )
                Button(
                    onClick = onNewGame,
                    modifier = Modifier.height(60.dp).padding(bottom = 20.dp)
                ) {
                    Text("New Game")
                }
            }
        }
    } else {
        Column(
            modifier = Modifier.fillMaxSize().padding(8.dp)
        ) {
            Spacer(modifier=Modifier.weight(0.5f))

            Panel3(
                wordToGuess = currentWord,
                guessedLetters = guessedLetters,
                remainingTurns = remainingTurns,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "Choose the letter",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Panel1(
                onLetterSelected = { letter ->
                    if (!guessedLetters.contains(letter)) {
                        guessedLetters = guessedLetters + letter
                        if (!currentWord.contains(letter)) {
                            remainingTurns -= 1
                        }
                    }
                },
                selectedLetters = guessedLetters,
                modifier = Modifier.weight(1f)
            )
            Button(
                onClick = onNewGame,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                Text("New Game")
            }
        }
    }
    showToast?.let {
        LaunchedEffect(it) {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            showToast = null
        }
    }
}

@Composable
fun Panel1(onLetterSelected: (Char) -> Unit, selectedLetters: List<Char>, modifier: Modifier) {
    val alphabet = ('A'..'Z').toList()

    LazyVerticalGrid(
        columns = GridCells.Fixed(7),
        modifier = modifier.padding(0.dp)
    ) {
        items(alphabet) { letter ->
            Button(
                onClick = { onLetterSelected(letter) },
                enabled = !selectedLetters.contains(letter),
                modifier = Modifier.padding(2.dp).height(35.dp)
            ) {
                Text(letter.toString(), style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
fun Panel2(
    onHintUsed: () -> Unit,
    hintState: Int,
    remainingLetters: List<Char>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(8.dp)
    ) {

        Button(onClick = onHintUsed) {
            when (hintState) {
                0 -> Text("Get Hint")
                1 -> Text("Disable Half Letters (Cost 1 turn)")
                2 -> Text("Show Vowels (Cost 1 turn)")
            }
        }
    }
}


@Composable
fun Panel3(wordToGuess: String, guessedLetters: List<Char>, remainingTurns: Int, modifier: Modifier = Modifier) {
    val displayedWord = wordToGuess.map { letter ->
        if (guessedLetters.contains(letter)) letter else '_'
    }.joinToString(" ")

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.padding(16.dp)
    ) {

        Text(text = displayedWord, style = MaterialTheme.typography.displaySmall)

        Text(text = "Turns remaining: $remainingTurns", style = MaterialTheme.typography.bodySmall)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Your HP",
            style = MaterialTheme.typography.titleLarge,  //
            color = MaterialTheme.colorScheme.primary
        )

        LinearProgressIndicator(
            progress = {
                remainingTurns / 6f
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
                .padding(vertical = 8.dp),
            color = if (remainingTurns >= 3) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.error
            },
        )

    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    DayuLihangmanTheme {
        GuessWordGame()
    }
}
