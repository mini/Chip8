package mini.chip8;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

public class MainController {

	//@formatter:off
	@FXML private Screen screen;
	@FXML private Button stateButton;
	@FXML private Button loadButton;
	@FXML private Button runButton;
	@FXML private Button haltButton;
	@FXML private Button stepButton;
	//@formatter:on

	private FileChooser fileChooser;
	private Stage primaryStage;
	private Stage stateStage;
	private StateController stateController;
	private Chip8 cpu;
	private Timeline timeline;

	private static HashMap<KeyCode, Integer> keymap;
	static {
		keymap = new HashMap<KeyCode, Integer>(16, 1);
		keymap.put(KeyCode.DIGIT1, 1);
		keymap.put(KeyCode.DIGIT2, 2);
		keymap.put(KeyCode.DIGIT3, 3);
		keymap.put(KeyCode.DIGIT4, 0xC);

		keymap.put(KeyCode.Q, 4);
		keymap.put(KeyCode.W, 5);
		keymap.put(KeyCode.E, 6);
		keymap.put(KeyCode.R, 0xD);

		keymap.put(KeyCode.A, 7);
		keymap.put(KeyCode.S, 8);
		keymap.put(KeyCode.D, 9);
		keymap.put(KeyCode.F, 0xE);

		keymap.put(KeyCode.Z, 0xA);
		keymap.put(KeyCode.X, 0);
		keymap.put(KeyCode.C, 0xB);
		keymap.put(KeyCode.V, 0xF);
	}

	void setup(Stage primaryStage) {
		this.primaryStage = primaryStage;

		fileChooser = new FileChooser();

		cpu = new Chip8(screen);
		screen.init();

		timeline = new Timeline(new KeyFrame(Duration.seconds(0.003), cycle -> {
			step();
		}));

		timeline.setCycleCount(Timeline.INDEFINITE);

	}

	@FXML
	private void loadPressed() {
		timeline.stop();

		File file = fileChooser.showOpenDialog(primaryStage);
		if (file != null) {
			fileChooser.setInitialDirectory(file.getParentFile());
			fileChooser.setInitialFileName(file.getName());
			try {
				cpu.reset();
				screen.clear();
				cpu.load(Files.readAllBytes(file.toPath()));
				runButton.setDisable(false);
				stepButton.setDisable(false);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (IndexOutOfBoundsException e) {
				runButton.setDisable(true);
				haltButton.setDisable(true);
				stepButton.setDisable(true);
				Alert alert = new Alert(AlertType.WARNING, e.getMessage(), ButtonType.OK);
				alert.initOwner(primaryStage);
				alert.showAndWait();
			}
		}
	}

	@FXML
	private void statePressed() throws IOException {
		stateButton.setDisable(true);

		if (stateStage == null) {
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/state.fxml"));
			stateStage = new Stage();
			stateStage.setResizable(false);
			stateStage.setTitle("CPU State");
			stateStage.setScene(new Scene(fxmlLoader.<Pane>load()));
			stateController = fxmlLoader.<StateController>getController();
			cpu.setListener(stateController);

			stateStage.setOnCloseRequest(evt -> {
				stateStage.hide();
				evt.consume();
				stateButton.setDisable(false);
			});
		}

		stateStage.show();
	}

	@FXML
	private void runPressed() {
		runButton.setDisable(true);
		haltButton.setDisable(false);
		stepButton.setDisable(false);
		timeline.play();
	}

	@FXML
	private void haltPressed() {
		runButton.setDisable(false);
		haltButton.setDisable(true);
		stepButton.setDisable(false);

		timeline.stop();
	}

	@FXML
	private void stepPressed() {
		runButton.setDisable(false);
		timeline.stop();

		step();
	}

	private void step() {
		cpu.step();
		if (cpu.shouldDraw()) {
			screen.render();
			cpu.resetDrawFlag();
		}
		cpu.updateTimer();
		if (stateStage != null && stateStage.isShowing()) {
			cpu.updateListener();
		}
	}

	@FXML
	private void keyDown(KeyEvent key) {
		if (keymap.containsKey(key.getCode())) {
			cpu.setKey(keymap.get(key.getCode()), true);
		}
	}

	@FXML
	private void keyUp(KeyEvent key) {
		if (keymap.containsKey(key.getCode())) {
			cpu.setKey(keymap.get(key.getCode()), false);
		}
	}
}
