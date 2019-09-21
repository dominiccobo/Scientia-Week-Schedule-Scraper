package com.dominiccobo.weekscheduler.ui;

import com.dominiccobo.weekscheduler.domain.UserScheduleDetail;
import javafx.scene.control.TextField;

public class StudentIDField extends TextField {

    private UserScheduleDetail userScheduleDetail;

    public StudentIDField(UserScheduleDetail userScheduleDetail) {
        this.userScheduleDetail = userScheduleDetail;
        setPromptText("Student ID");
        textProperty().addListener((observable, oldValue, newValue) -> userScheduleDetail.setUsername(newValue));
    }
}
