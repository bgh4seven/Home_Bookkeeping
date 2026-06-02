import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);  // стандартный запуск JavaFX
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Загружаем FXML-файл (разметка окна)
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/main-view-0.1.fxml"));
        Parent root = loader.load();

        // Применяем CSS-стили
        Scene scene = new Scene(root);
        //удалитьscene.getStylesheets().add(getClass().getResource("/styles/main.css").toExternalForm());

        // Настраиваем окно
        primaryStage.setTitle("Домашняя бухгалтерия");
        primaryStage.setScene(scene);
        primaryStage.show();
    }


}