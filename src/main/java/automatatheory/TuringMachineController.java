package automatatheory;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
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

    private final String[] states = {"right", "carry", "done"};
    private final String[] transitions = {
            "1, 0 -> R",
            "1, 0 -> L",
            "1, 0 -> L"
    };

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

            int result = 0;
            if (operation.equals("addition")) {
                result = num1 + num2;
            } else if (operation.equals("subtraction")) {
                result = num1 - num2;
            }

            String binaryResult = toBinary(result);
            String binaryNum1 = toBinary(num1);
            String binaryNum2 = toBinary(num2);

            resultLabel.setText("Result: " + result);
            binaryResultLabel.setText("Binary: " + binaryResult);
            binaryConversionMessage.setText("All numbers converted to binary");

            // Initialize tape based on the largest binary number
            int maxLength = Math.max(binaryNum1.length(), Math.max(binaryNum2.length(), binaryResult.length()));
            tape = new String[maxLength + 2]; // +2 for overflow space
            for (int i = 0; i < tape.length; i++) {
                tape[i] = "_";
            }

            // Update tape with binary numbers
            updateTape(binaryNum1, binaryNum2, binaryResult);

        } catch (NumberFormatException e) {
            resultLabel.setText("Error: Please enter valid numbers.");
            binaryResultLabel.setText("");
            binaryConversionMessage.setText("Error: Invalid input.");
        }
    }

    private String toBinary(int number) {
        if (number == 0) {
            return "0";
        }
        StringBuilder binary = new StringBuilder();
        int num = Math.abs(number);
        while (num > 0) {
            binary.append(num % 2);
            num /= 2;
        }
        if (number < 0) {
            binary.append("-");
        }
        return binary.reverse().toString();
    }

    private void updateTape(String binaryNum1, String binaryNum2, String binaryResult) {
        inputTape.getChildren().clear();

        for (int i = 0; i < tape.length; i++) {
            Label tapeCell = new Label(tape[i]);
            tapeCell.setStyle("-fx-border-color: black; -fx-padding: 10; -fx-background-color: white;");
            if (i == currentPos) {
                tapeCell.setStyle("-fx-border-color: black; -fx-padding: 10; -fx-background-color: yellow;");
            }
            inputTape.getChildren().add(tapeCell);
        }

        Text currentStateText = new Text("Current State: " + states[currentState]);
        inputTape.getChildren().add(currentStateText);
    }

    private void stepTransition() {
        if (isHalted) {
            transitionLog.appendText("Machine already halted.\n");
            return;
        }

        transitionLog.appendText("State: " + states[currentState] + "\n");
        transitionLog.appendText("Transition: " + transitions[currentState] + "\n");

        switch (states[currentState]) {
            case "right":
                if (currentPos < tape.length - 1) {
                    currentPos++;
                } else {
                    transitionLog.appendText("Tape head reached the end.\n");
                    isHalted = true;
                    stopMachine();
                    return;
                }
                break;
            case "carry":
                // Logic for carry
                break;
            case "done":
                transitionLog.appendText("Machine halted in state 'done'.\n");
                isHalted = true;
                stopMachine();
                return;
        }

        currentState = (currentState + 1) % states.length;
        updateTape("", "", "");
    }

    private void runMachine() {
        if (isHalted) {
            return;
        }

        if (!isRunning) {
            isRunning = true;
            timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
                stepTransition();
                if (isHalted) {
                    transitionLog.appendText("Halt (Δ)\n");
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
        updateTape("", "", "");
    }

    private void skipToHalt() {
        while (!isHalted) {
            stepTransition();
        }
        transitionLog.appendText("Halt (Δ)\n");
    }
}