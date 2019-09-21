package com.dominiccobo.weekscheduler;

import com.dominiccobo.weekscheduler.ui.*;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.kordamp.bootstrapfx.scene.layout.Panel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JavaFxApplication extends Application {

    private static final Logger LOGGER = LoggerFactory.getLogger(JavaFxApplication.class);

    @Override
    public void start(Stage primaryStage) throws Exception {

        LOGGER.info("Starting Java FX UI Application");

        primaryStage.setWidth(900);
        primaryStage.setHeight(500);
        primaryStage.setMinWidth(900);
        primaryStage.setMinHeight(500);
        primaryStage.setResizable(false);


        Panel panel = new Panel("Week Scheduler");
        panel.getStyleClass().add("panel-primary");
        DownloadTimetableForm downloadTimetableForm = new DownloadTimetableForm(primaryStage);
        panel.setBody(downloadTimetableForm);

        Scene scene = new Scene(panel, 900, 500);
        scene.getStylesheets().add("org/kordamp/bootstrapfx/bootstrapfx.css");

        primaryStage.setTitle("Week Scheduler");
        primaryStage.setScene(scene);
        primaryStage.sizeToScene();
        primaryStage.show();
    }

}
