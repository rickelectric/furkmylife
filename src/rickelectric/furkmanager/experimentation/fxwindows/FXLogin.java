package rickelectric.furkmanager.views.fxwindows;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import rickelectric.furkmanager.FurkManager;
import rickelectric.furkmanager.network.API;
import rickelectric.furkmanager.network.APIBridge;
import rickelectric.furkmanager.utils.UtilBox;

public class FXLogin extends Application implements Initializable {

	public Stage stage;

	public void start(){
		launch();
	}
	
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
		stage.setScene(scene);
		stage.sizeToScene();
		stage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}

	@FXML
	private TextField input_username, input_apikey;
	@FXML
	private PasswordField input_password;

	@FXML
	private Label label_key,status_bar;
	
	@FXML
	private CheckBox check_savecreds, check_remember, check_autologin;

	@FXML
	private ImageView image_logo;

	@FXML
	private Button button_login, button_apilogin;

	@FXML
	private Pane main_panel;

	private EventHandler<KeyEvent> enterLoginEvent = new EventHandler<KeyEvent>() {
		@Override
		public void handle(KeyEvent e) {
			if (e.getCode().equals(KeyCode.ENTER)) {
				login();
			}
		}
	};
	protected boolean loggingIn;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {

		input_password.setOnKeyReleased(enterLoginEvent);

		input_username.setOnKeyReleased(enterLoginEvent);

		main_panel.setStyle("-fx-background-color: #EEEEEE;");

		image_logo.setImage(new Image(FurkManager.class
				.getResourceAsStream("img/fl_anim/fr-logo-speed-0.gif")));
		image_logo.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (event.getButton() == MouseButton.SECONDARY
						&& event.getClickCount() == 2) {
					login();
				}
			}
		});
		button_login.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				login();
			}
		});
		button_apilogin.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				apilogin();
			}
		});
		label_key.setOnMouseClicked(new EventHandler<MouseEvent>(){
			@Override
			public void handle(MouseEvent e){
				if(e.getButton()==MouseButton.SECONDARY&&e.getClickCount()==2){
					input_apikey.setText("5323228d687ed9f7f1bdf9ce87050a1fa672e485");
				}
				
			}
			
		});

	}

	public void login() {
		Thread t = new Thread(new Runnable() {
			public void run() {
				String username = input_username.getText();
				if (username == null || username.equals(""))
					return;
				String password = input_password.getText();
				if (password == null || password.equals(""))
					return;

				furkAnimate(4);
				try {
					UtilBox.pause(500);
					boolean login = APIBridge.userLogin(username, password);
					if (login) {
						stage.close();
						loggingIn = false;
						FurkManager.main(new String[] {});
						return;
					} else {
						throw new Exception("Invalid Username or Password");
					}
				} catch (Exception e) {
					furkAnimate(2);
					FurkManager.trayAlert(FurkManager.TRAY_ERROR,
							"Login Failed", e.getMessage(), null);
					loggingIn = false;
					furkAnimate(0);
					return;
				}
			}
		});
		t.start();
	}

	public void apilogin() {
		Thread t = new Thread(new Runnable() {
			public void run() {
				furkAnimate(4);
				String key = input_apikey.getText();
				if(!APIBridge.ping(key)){
					furkAnimate(0);
					return;
				}
				API.init(key);
				FurkManager.appRun();
				stage.close();
				
			}
		});
		t.start();

	}

	public void furkAnimate(int speed) {
		if (speed >= 0 && speed <= 4)
			image_logo.setImage(new Image(FurkManager.class
					.getResourceAsStream("img/fl_anim/fr-logo-speed-" + speed
							+ ".gif")));
	}

}
