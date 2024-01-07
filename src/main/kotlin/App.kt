import javafx.application.Application
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.stage.Stage
import javafx.stage.StageStyle

class App : Application(){
    override fun start(primaryStage: Stage) {
        // Create the content area
        val root = BorderPane()
        root.style = "-fx-background-color: ${toHex(backgroundColor)};" // White content background

        // Create a custom title bar
        val titleBar = HBox()
        titleBar.alignment = Pos.CENTER_RIGHT
        titleBar.style = "-fx-background-color: #333; -fx-padding: 5px;" // Gray title bar

        // Minimize button
        val minimizeButton = Button("—")
        minimizeButton.setOnAction { primaryStage.isIconified = true } // Minimize button action
        minimizeButton.style = "-fx-background-color: transparent; -fx-text-fill: white;"

        // Close button
        val closeButton = Button("✕")
        closeButton.setOnAction { primaryStage.close() } // Close button action
        closeButton.style = "-fx-background-color: transparent; -fx-text-fill: white;"

        // Add title text to the title bar
        val titleText = Label("$serverName Launcher")
        titleText.style = "-fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 0 10px;" // Customize text style

        // Create a spacer to push the buttons to the right
        val spacer = Region()
        HBox.setHgrow(spacer, Priority.ALWAYS)

        // Add buttons to the title bar
        titleBar.children.addAll(titleText, spacer, minimizeButton, closeButton)

        // Create a VBox to hold the UI elements
        val vBox = VBox(20.0) // Spacing between elements
        vBox.alignment = Pos.CENTER

        // Logo
        val logoImage = Image(javaClass.getResourceAsStream("/images/logo.png"))
        val logo = ImageView(logoImage)
        logo.fitWidth = logoImage.width * 0.75
        logo.fitHeight = logoImage.height * 0.75

        // Create an HBox for the buttons and set alignment to Pos.CENTER_RIGHT
        val buttonsBox = HBox(20.0) // Spacing between buttons
        buttonsBox.alignment = Pos.CENTER
        buttonsBox.padding = Insets(0.0, 15.0, 0.0, 0.0) // Add right padding

        // Buttons
        val launchButton = Button("Launch Game")
        val updateButton = Button("Check for Updates")

        // Styling for the buttons with hover effect
        val launchButtonStyle = """
    -fx-min-width: 150px;
    -fx-min-height: 50px;
    -fx-background-color: #14A819;
    -fx-font-weight: bold;
    -fx-text-fill: white;
"""
        val updateButtonStyle = """
    -fx-min-width: 150px;
    -fx-min-height: 50px;
    -fx-background-color: #D49C1C;
    -fx-font-weight: bold;
    -fx-text-fill: white;
"""

        val launchButtonHoverStyle = "-fx-background-color: #0F8A11;" // Slightly lighter color on hover
        val updateButtonHoverStyle = "-fx-background-color: #BB8415;" // Slightly lighter color on hover

        launchButton.style = launchButtonStyle
        launchButton.setOnMouseEntered { launchButton.style = "$launchButtonStyle$launchButtonHoverStyle" }
        launchButton.setOnMouseExited { launchButton.style = launchButtonStyle }

        updateButton.style = updateButtonStyle
        updateButton.setOnMouseEntered { updateButton.style = "$updateButtonStyle$updateButtonHoverStyle" }
        updateButton.setOnMouseExited { updateButton.style = updateButtonStyle }

        // Progress Indicator
        val progressIndicator = ProgressIndicator()
        progressIndicator.isVisible = false // Initially hidden

        // Information Panel
        val informationPanel = TextArea()
        informationPanel.isEditable = false
        informationPanel.text = "Welcome to the Launcher.\n"

        // Add buttons to the HBox
        buttonsBox.children.addAll(updateButton, launchButton)

        // Add UI elements to the VBox
        vBox.children.addAll(progressIndicator, logo, buttonsBox, informationPanel)

        // Add the VBox to the content area
        root.center = vBox

        // Add the title bar to the top of the BorderPane
        root.top = titleBar

        // Create the scene
        val scene = Scene(root, 600.0, 400.0)

        // Set a custom stage style with no decorations
        primaryStage.initStyle(StageStyle.UNDECORATED)

        // Set the scene and show the stage
        primaryStage.scene = scene
        primaryStage.show()

        // Add mouse event handlers for dragging the window
        var xOffset = 0.0
        var yOffset = 0.0

        titleBar.setOnMousePressed { event ->
            xOffset = event.sceneX
            yOffset = event.sceneY
        }

        titleBar.setOnMouseDragged { event ->
            primaryStage.x = event.screenX - xOffset
            primaryStage.y = event.screenY - yOffset
        }

        // Set actions for buttons (you can implement these functions)
        launchButton.setOnAction { launchGame(informationPanel) }
        updateButton.setOnAction { checkForUpdates(progressIndicator, informationPanel) }
    }

    // Implement these functions for button actions
    private fun launchGame(informationPanel: TextArea) {
        informationPanel.appendText("Launching client...\n")

        // Launch the client and check the return value
        if (!clientLauncher.launchClient()) {
            displayNoClientFiles()
        }
    }

    private fun checkForUpdates(progressIndicator: ProgressIndicator, informationPanel: TextArea) {
        informationPanel.appendText("Checking for updates. Please wait...\n")
        progressIndicator.isVisible = true
        versionChecker.checkGameVersions(progressIndicator, informationPanel)
        progressIndicator.isVisible = false
    }

    private fun displayNoClientFiles() {
        val alert = Alert(Alert.AlertType.ERROR)
        alert.title = "No client!"
        alert.headerText = "Unable to find client."
        alert.contentText = "We are unable to find the $serverName client. Please check for updates to ensure you have the latest version."
        alert.showAndWait()
    }

    private fun toHex(color: Color): String {
        return String.format("#%02X%02X%02X",
            (color.red * 255).toInt(),
            (color.green * 255).toInt(),
            (color.blue * 255).toInt()
        )
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            launch(App::class.java)
        }
    }
}