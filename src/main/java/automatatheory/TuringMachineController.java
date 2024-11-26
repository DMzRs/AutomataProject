package automatatheory;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class TuringMachineController {

    @FXML
    private TextField number1Field;

    @FXML
    private TextField number2Field;

    @FXML
    private Button additionButton;

    @FXML
    private Button subtractionButton;

    @FXML
    private Label resultLabel;

    @FXML
    private Label binaryResultLabel;

    @FXML
    private Label binaryConversionMessage;

    @FXML
    private HBox inputTape;

    @FXML
    private VBox tapeBox;

    @FXML
    private TextArea transitionLog;

    @FXML
    private Button stepButton;

    @FXML
    private Button runButton;

    @FXML
    private Button pauseButton;

    @FXML
    private Button stopButton;

    @FXML
    private Button skipButton;

    private String[] tape; // Tape array
    private int currentPos = 0; // Position of the tape head
    private int currentState = 0; // Current state

    private boolean isHalted = false;
    private boolean isRunning = false;
    private Timeline timeline;

    @FXML
    public void initialize() {
        additionButton.setOnAction(event -> handleOperation("addition"));
        subtractionButton.setOnAction(event -> handleOperation("subtraction"));
        stepButton.setOnAction(event -> stepTransition());
        runButton.setOnAction(event -> runMachine());
        pauseButton.setOnAction(event -> pauseMachine());
        stopButton.setOnAction(event -> stopMachine());
        skipButton.setOnAction(event -> skipToHalt());
    }

    private void handleOperation(String operation) {
        try {
            int num1 = Integer.parseInt(number1Field.getText());
            int num2 = Integer.parseInt(number2Field.getText());

            int result = operation.equals("addition") ? num1 + num2 : num1 - num2;

            String binaryNum1 = toBinary(num1);
            String binaryNum2 = toBinary(num2);
            String binaryResult = toBinary(result);

            resultLabel.setText("Result: " + result);
            binaryResultLabel.setText("Binary: " + binaryResult);
            binaryConversionMessage.setText("No conversion yet");

            // Initialize tape with binary numbers and halt marker
            tape = createTape(binaryNum1, binaryNum2, binaryResult);
            currentPos = 0; // Reset tape head position
            currentState = 0; // Reset state
            isHalted = false;
            updateTape();

        } catch (NumberFormatException e) {
            resultLabel.setText("Error: Please enter valid numbers.");
            binaryResultLabel.setText("");
            binaryConversionMessage.setText("Error: Invalid input.");
        }
    }

    private String toBinary(int number) {
        return Integer.toBinaryString(number);
    }

    private String[] createTape(String binaryNum1, String binaryNum2, String binaryResult) {
        // Combine inputs, result, and halt marker into the tape
        String tapeContent = binaryNum1 + "_" + binaryNum2 + "_" + binaryResult + "_Δ";
        return tapeContent.split("");
    }

    private void updateTape() {
        inputTape.getChildren().clear();

        for (int i = 0; i < tape.length; i++) {
            VBox cellBox = new VBox();

            Label tapeCell = new Label(tape[i]);
            tapeCell.setStyle("-fx-border-color: black; -fx-padding: 10; -fx-background-color: white;");

            if (i == currentPos) {
                tapeCell.setStyle("-fx-border-color: black; -fx-padding: 10; -fx-background-color: yellow;");
            }

            Text stateIndicator = new Text();
            if (i == currentPos) {
                stateIndicator.setText("q" + currentState);
            }

            cellBox.getChildren().addAll(tapeCell, stateIndicator);
            cellBox.setSpacing(5);
            cellBox.setStyle("-fx-alignment: center;");
            inputTape.getChildren().add(cellBox);
        }
    }

    private void stepTransition() {
        if (isHalted) {
            transitionLog.appendText("Machine already halted.\n");
            return;
        }

        transitionLog.appendText("Processing tape index: " + currentPos + "\n");

        // Check if the current tape cell is the halt marker (Δ)
        if (tape[currentPos].equals("Δ")) {
            isHalted = true;
            transitionLog.appendText("Machine halted.\n");

            // Stop the timeline if halt state is reached
            if (timeline != null) {
                timeline.stop();
            }

            // Update binary conversion message after halting
            binaryConversionMessage.setText("All numbers converted to binary");
            return;
        }

        // Move to the next position on the tape
        if (currentPos < tape.length - 1) {
            currentPos++;
            currentState++; // Example state change (you can modify logic)
        } else {
            isHalted = true;
            transitionLog.appendText("Machine halted.\n");

            // Stop the timeline when halt state is reached
            if (timeline != null) {
                timeline.stop();
            }

            // Update binary conversion message after halting
            binaryConversionMessage.setText("All numbers converted to binary");
        }

        updateTape();
    }

    private void runMachine() {
        if (isHalted) {
            return; // If already halted, do nothing
        }

        if (!isRunning) {
            isRunning = true;
            timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
                if (!isHalted) {
                    stepTransition();  // Call stepTransition on each step
                }
                if (isHalted) {
                    transitionLog.appendText("Halt (Δ)\n");
                    // Update binary conversion message after halting
                    binaryConversionMessage.setText("All numbers converted to binary");
                }
            }));
            timeline.setCycleCount(Timeline.INDEFINITE);
            timeline.play();
        }
    }

    private void pauseMachine() {
        if (timeline != null) {
            timeline.pause();
            isRunning = false;
        }
    }

    private void stopMachine() {
        if (timeline != null) {
            timeline.stop();
            isRunning = false;
        }
        currentState = 0;
        currentPos = 0;
        isHalted = false;
        transitionLog.clear();
        updateTape();
    }

    private void skipToHalt() {
        while (!isHalted) {
            stepTransition();
        }
        transitionLog.appendText("Halt (Δ)\n");
    }
}
