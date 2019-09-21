package com.dominiccobo.weekscheduler.ui;

import com.dominiccobo.weekscheduler.domain.AcademicYear;
import com.dominiccobo.weekscheduler.domain.Schedule;
import com.dominiccobo.weekscheduler.domain.UserScheduleDetail;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class DownloadTimetableForm extends GridPane {

    private UserScheduleDetail userScheduleDetail = new UserScheduleDetail();

    private StudentIDField studentId;
    private StudentPasswordField networkPassword;
    private WeekSelectionDropdown weekSelectionDropdown;
    private DownloadButton downloadButton;

    public DownloadTimetableForm(Stage stageCtx) {
        setAlignment(Pos.CENTER);
        setHgap(10);
        setVgap(10);
        setPadding(new Insets(10));

        studentId = new StudentIDField(userScheduleDetail);
        networkPassword = new StudentPasswordField(userScheduleDetail);
        weekSelectionDropdown = new WeekSelectionDropdown(userScheduleDetail);
        downloadButton = new DownloadButton(stageCtx, userScheduleDetail);

        getStylesheets().add("org/kordamp/bootstrapfx/bootstrapfx.css");

        add(studentId, 0, 1);
        add(networkPassword, 0, 2);
        add(weekSelectionDropdown,0,  3);
        add(downloadButton, 0, 4);
    }




}
