package com.dominiccobo.weekscheduler.ui;

import com.dominiccobo.weekscheduler.domain.UserScheduleDetail;
import javafx.scene.control.PasswordField;

public class StudentPasswordField extends PasswordField {

    private UserScheduleDetail userScheduleDetail;

    public StudentPasswordField(UserScheduleDetail userScheduleDetail) {
        this.userScheduleDetail = userScheduleDetail;
        setPromptText("Student password");
        textProperty().addListener((observable, oldValue, newValue) -> userScheduleDetail.setPassword(newValue));
    }
}
