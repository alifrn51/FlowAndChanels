package dictionary

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import java.awt.BorderLayout
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import javax.swing.*
import kotlin.time.Duration.Companion.milliseconds


@OptIn(FlowPreview::class)
object Display {


    private val queries = Channel<String>()
    private val state = MutableSharedFlow<ScreenState>()

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val repository = Repository

    private val enterWordLabel = JLabel("Enter word: ")
    private val searchField = JTextField(20).apply {
        addKeyListener(object : KeyAdapter() {
            override fun keyReleased(e: KeyEvent?) {
                loadDefinition()
            }
        })
    }


    private val searchButton = JButton("Search").apply {
        addActionListener {
            loadDefinition()
        }
    }

    private val resultArea = JTextArea(25, 50).apply {
        isEditable = false
        lineWrap = true
    }

    private val topPanel = JPanel().apply {
        add(enterWordLabel)
        add(searchField)
        add(searchButton)
    }

    private val mainFrame = JFrame("Dictionary App").apply {
        setSize(700, 500)
        layout = BorderLayout()
        add(topPanel, BorderLayout.NORTH)
        add(JScrollPane(resultArea), BorderLayout.CENTER)
    }

    private fun loadDefinition() {
        scope.launch {
            queries.send(searchField.text.trim())
        }
    }

    fun show() {
        mainFrame.isVisible = true
    }

    init {
        queries.consumeAsFlow()
            .onStart {
                state.emit(ScreenState.Initial)
            }
            .onEach {
                state.emit(ScreenState.Loading)
            }
            .debounce(5000.milliseconds)
            .map {
                if(it.isEmpty()){
                    state.emit(ScreenState.Initial)
                }else {
                    val response = repository.loadDefinition(it)

                    if(response.isEmpty()){
                        state.emit(ScreenState.NotFound)
                    }else{
                        state.emit(ScreenState.DefinitionLoaded(response))
                    }
                }
            }.launchIn(scope)

        state.onEach {
            when (it) {
                ScreenState.Initial -> {
                    resultArea.text = ""
                    searchButton.isEnabled = false
                }

                ScreenState.Loading -> {
                    resultArea.text = "Loading..."
                    searchButton.isEnabled = false
                }

                ScreenState.NotFound -> {
                    resultArea.text = "Not found!"
                    searchButton.isEnabled = true
                }

                is ScreenState.DefinitionLoaded -> {
                    resultArea.text = it.definitions.joinToString("\n\n")
                    searchButton.isEnabled = true
                }
            }
        }.launchIn(scope)

    }


}


fun main() {
    Display.show()

}