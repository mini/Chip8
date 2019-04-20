package mini.chip8;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

public class MainController {

	//@formatter:off
	@FXML private Screen screen;
	@FXML private Button loadButton;
	@FXML private Button runButton;
	@FXML private Button haltButton;
	@FXML private Button stepButton;
	//@formatter:on

	private FileChooser fileChooser;
	private Stage primaryStage;
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
		screen.render();

		timeline = new Timeline(new KeyFrame(Duration.seconds(0.003), cycle -> {
			step();
		}));
		
		timeline.setCycleCount(Timeline.INDEFINITE);
	}


	@FXML
	private void loadPressed() {
		haltPressed();
		runButton.setDisable(true);
		
		while (true) {
			File file = fileChooser.showOpenDialog(primaryStage);
			if (file != null) {
				fileChooser.setInitialDirectory(file.getParentFile());
				fileChooser.setInitialFileName(file.getName());
				try {
					cpu.reset();
					cpu.load(Files.readAllBytes(file.toPath()));
					runButton.setDisable(false);
					stepButton.setDisable(false);
					break;
				} catch (IOException e) {
					e.printStackTrace();
				} catch (IndexOutOfBoundsException e) {
					System.out.println(e.getMessage());
					Alert alert = new Alert(AlertType.WARNING, e.getMessage(), ButtonType.OK);
					alert.initOwner(primaryStage);
					alert.showAndWait();
				}
			}
		}
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
		runButton.setDisable(true);
		haltButton.setDisable(true);
		stepButton.setDisable(true);
		
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
	}
	
	@FXML
	private void keyDown(KeyEvent key) {
		cpu.setKey(keymap.get(key.getCode()), true);
	}
	
	@FXML
	private void keyUp(KeyEvent key) {
		cpu.setKey(keymap.get(key.getCode()), false);
	}
}
