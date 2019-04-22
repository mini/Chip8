package mini.chip8;

import java.io.IOException;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class App extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws IOException {

		FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/screen.fxml"));
		Pane root = loader.<Pane>load();
		MainController controller = loader.<MainController>getController();

		primaryStage.setScene(new Scene(root));
		primaryStage.setTitle("Chip-8 Emulator");
		primaryStage.setResizable(true);
		
		primaryStage.setOnCloseRequest(evt ->{
			Platform.exit();
		});
		
		primaryStage.show();
		
		controller.setup(primaryStage);
	}
}
