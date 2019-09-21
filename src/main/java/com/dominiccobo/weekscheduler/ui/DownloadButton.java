package com.dominiccobo.weekscheduler.ui;

import com.dominiccobo.weekscheduler.domain.AcademicYear;
import com.dominiccobo.weekscheduler.domain.Schedule;
import com.dominiccobo.weekscheduler.domain.UserScheduleDetail;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class DownloadButton extends Button {

    private final Stage stageContext;
    private UserScheduleDetail userScheduleDetail;
    private File downloadTo;

    public DownloadButton(Stage stageContext, UserScheduleDetail userScheduleDetail) {
        this.stageContext = stageContext;
        this.userScheduleDetail = userScheduleDetail;
        setText("Download");
        setOnAction(actionEvent -> {
            selectDownloadLocation();
            download();
        });
    }

    public void selectDownloadLocation() {
        FileChooser folderChooser = getFileChooser(userScheduleDetail);
        File file = folderChooser.showSaveDialog(stageContext);
        downloadTo = file;
        if(file!= null) {
            userScheduleDetail.setOutputDir(downloadTo.toString());
        }
    }

    private void download() {
        try {

            if(downloadTo == null) {
                return;
            }

            AcademicYear academicYear = AcademicYear.getCurrentAcademicYear();
            Schedule schedule = new Schedule(academicYear, userScheduleDetail);
            schedule.retrieve();

            String message = String.format("Downloaded %d events", schedule.getEvents().size());
            Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.CLOSE);
            alert.setTitle("Download successful!");
            alert.show();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage(), ButtonType.OK);
            alert.setTitle("Something went wrong!");
            alert.show();
        }
    }

    private FileChooser getFileChooser(UserScheduleDetail userScheduleDetail) {
        FileChooser folderChooser = new FileChooser();

        if(downloadTo != null) {
            File selectedFile = downloadTo;
            File selectedFolder = new File(selectedFile.getParent());
            folderChooser.setInitialDirectory(selectedFolder);
            folderChooser.setInitialFileName(selectedFile.getName());
        }
        else {
            String defaultFileName = "timetable_week-" + userScheduleDetail.getWeekNumber() + ".ics";
            folderChooser.setInitialFileName(defaultFileName);
        }
        return folderChooser;
    }
}
