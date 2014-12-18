package rickelectric.furkmanager.views.fxwindows;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class FXLoginWindow extends Application {
	
	private static Stage stage;
	
	@Override
	public void start(Stage primaryStage) {
		stage=primaryStage;
		Parent root;
		try {
			root = FXMLLoader.load(getClass().getResource("FXLogin.fxml"));
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		Scene scene = new Scene(root);
		primaryStage.setScene(scene);
		primaryStage.sizeToScene();
		primaryStage.show();
	}

	public static void main(String[] args) {
		FXLoginWindow.launch(args);
	}
	
	public static void closeStage(){
		stage.close();
	}
}
