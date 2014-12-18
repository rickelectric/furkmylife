package rickelectric.furkmanager.views.fxwindows;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import rickelectric.furkmanager.FurkManager;
import rickelectric.furkmanager.network.APIBridge;
import rickelectric.furkmanager.utils.SettingsManager;
import rickelectric.furkmanager.views.ConsoleWin;

public class APIConsoleFX extends Application implements Initializable,
		ConsoleWin {

	public static Stage stage;

	@FXML
	private Pane contentPane;

	@FXML
	private TextField input_text;
	@FXML
	private TextArea output_result;
	@FXML
	private ProgressIndicator progress_loading;

	@FXML
	private ComboBox<String> method;

	public void start() {
		launch();
	}

	public static void main(String[] args) {
		SettingsManager.init();
		launch(args);
	}

	public void start(Stage primaryStage) {
		stage = primaryStage;
		Parent root;
		try {
			root = FXMLLoader.load(getClass().getResource("APIConsoleFX.fxml"));
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		final Scene scene = new Scene(root);
		stage.setTitle("API Console");
		stage.getIcons().add(
				new Image(FurkManager.class
						.getResourceAsStream("img/fr-32.png")));
		stage.setScene(scene);
		
		stage.setOnCloseRequest(new EventHandler<WindowEvent>(){

			@Override
			public void handle(WindowEvent e) {
				
			}
			
		});
		
		stage.sizeToScene();
		stage.show();
	}
	
	public static void begin(){
		launch(new String[]{});
	}
	
	public static void setVisible(final boolean b){
		Task<Object> t = new Task<Object>() {

			@Override
			protected Object call() throws Exception {
				if(b){
					stage.show();
					stage.toFront();
				}
				else stage.hide();
				return new Object();
			}
		};
		new Thread(t).start();
	}

	private EventHandler<Event> enterKeySendEvent = new EventHandler<Event>() {
		@Override
		public void handle(Event e) {
			if (((KeyEvent) e).getCode().equals(KeyCode.ENTER)) {
				Task<Object> t = new Task<Object>() {

					@Override
					protected Object call() throws Exception {
						request();
						req = false;
						return null;
					}
				};
				new Thread(t).start();
			}
		}
	};

	public void initialize(URL arg0, ResourceBundle arg1) {

		input_text.setOnKeyReleased(enterKeySendEvent);
		progress_loading.setVisible(false);

		ObservableList<String> clist = FXCollections.observableArrayList("GET",
				"POST");
		
		method.setItems(clist);
		method.getSelectionModel().select(0);

	}

	boolean req = false;

	protected void request() {
		if (req)
			return;
		req = true;
		String s = input_text.getText();

		// input_text.setVisible(false);
		output_result.setText("");
		progress_loading.setVisible(true);

		if (APIBridge.key() == null)
			APIBridge.initialize("5323228d687ed9f7f1bdf9ce87050a1fa672e485");
		String key = APIBridge.key();
		String[] q = s.split("[?]");
		s = q[0] += "?" + key + "&pretty=1";
		if (q.length > 1) {
			s += "&" + q[1];
		}
		s = "https://www.furk.net/api" + s;
		try {
			String m = method.getSelectionModel().getSelectedItem();

			String json;
			if (m.equals("GET"))
				json = APIBridge.jsonGet(s, false, false);
			else
				json = APIBridge.jsonPost(s, false, false);

			output_result.setText(json);
		} catch (Exception e) {
			output_result.setText(e.getMessage());
		}
		progress_loading.setVisible(false);
		// input_text.setVisible(true);

		req = false;
	}

}
