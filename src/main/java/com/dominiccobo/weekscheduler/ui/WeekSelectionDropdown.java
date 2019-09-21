package com.dominiccobo.weekscheduler.ui;

import com.dominiccobo.weekscheduler.domain.UserScheduleDetail;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;

public class WeekSelectionDropdown extends ListView<String> {

    private static final int ASSUMED_ROW_HEIGHT = 24;
    private final ObservableList<String> options = FXCollections.observableArrayList();

    public WeekSelectionDropdown(UserScheduleDetail userScheduleDetail) {
        getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        setItems(options);
        setPrefHeight(ASSUMED_ROW_HEIGHT * 3);

        options.add("current");
        options.add("previous");
        options.add("next");

        for (int i= 1; i <= 52; i++) {
            options.add(Integer.toString(i));
        }
        setOnMouseClicked(mouseEvent-> userScheduleDetail.setWeekNumber(getSelectedItem()));
    }

    String getSelectedItem() {
        return getSelectionModel().getSelectedItem();
    }
}
