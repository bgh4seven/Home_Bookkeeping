package ru.home_bookkeeping;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);  //Вызывает start(). Пока не закроют окно программы, код дальше не пойдет.
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Загружаем FXML-файл (разметка окна)
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/main-view-0.1.1.fxml"));
        Parent root = loader.load();

        //Создание контейнера для визуала
        Scene scene = new Scene(root);

        // Настраиваем и показываем окно
        primaryStage.setTitle("Домашняя бухгалтерия");
        primaryStage.setScene(scene);
        primaryStage.show();
    }


}