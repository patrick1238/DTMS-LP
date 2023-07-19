/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package EventHandler;

import com.sun.javafx.scene.control.skin.ComboBoxListViewSkin;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 *
 * @author patri
 */
public class AutoCompleteHandler implements EventHandler<KeyEvent> {
    private ComboBox comboBox;
    private StringBuilder sb;
    private ObservableList data;
    private boolean moveCaretToPos = false;
    private int caretPos;
    private int boxLimit = 30;

    public AutoCompleteHandler(final ComboBox comboBox) {
        this.comboBox = comboBox;
        sb = new StringBuilder();
        data = comboBox.getItems();

        this.comboBox.setEditable(true);
        this.comboBox.setOnKeyPressed(new EventHandler<KeyEvent>() {

            @Override
            public void handle(KeyEvent t) {
                comboBox.hide();
            }
        });
        this.comboBox.setOnKeyReleased(AutoCompleteHandler.this);
    }

    @Override
    public void handle(KeyEvent event) {
        ListView lv = ((ComboBoxListViewSkin) comboBox.getSkin()).getListView();
        if (event.getCode() == KeyCode.UP) {
            caretPos = -1;
            moveCaret(comboBox.getEditor().getText().length());
            return;
        } else if (event.getCode() == KeyCode.DOWN) {
            if (!comboBox.isShowing()) {
                comboBox.show();
            }
            caretPos = -1;
            moveCaret(comboBox.getEditor().getText().length());
            return;
        } else if (event.getCode() == KeyCode.BACK_SPACE) {
            moveCaretToPos = true;
            caretPos = comboBox.getEditor().getCaretPosition();
        } else if (event.getCode() == KeyCode.DELETE) {
            moveCaretToPos = true;
            caretPos = comboBox.getEditor().getCaretPosition();
        }else if (event.getCode() == KeyCode.ENTER) {
            comboBox.hide();
            return;
        }

        if (event.getCode() == KeyCode.RIGHT || event.getCode() == KeyCode.LEFT
                || event.isControlDown() || event.getCode() == KeyCode.HOME
                || event.getCode() == KeyCode.END || event.getCode() == KeyCode.TAB) {
            return;
        }

        ObservableList list = FXCollections.observableArrayList();
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).toString().toLowerCase().contains(
                    AutoCompleteHandler.this.comboBox
                            .getEditor().getText().toLowerCase())) {
                list.add(data.get(i));
            }
        }
        String t = comboBox.getEditor().getText();
        if (t.equals("")) {
            comboBox.setItems(data);
            comboBox.getSelectionModel().clearSelection();
            if (!moveCaretToPos) {
                caretPos = -1;
            }
            moveCaret(t.length());
            comboBox.hide();
        } else {
            if (list.size() < this.boxLimit) {
                comboBox.setItems(list);
                if (!list.isEmpty()) {
                    comboBox.hide();
                    comboBox.setVisibleRowCount(list.size());
                    comboBox.show();
                }
            }
            comboBox.getEditor().setText(t);
            if (!moveCaretToPos) {
                caretPos = -1;
            }
            moveCaret(t.length());
        }
    }

    private void moveCaret(int textLength) {
        if (caretPos == -1) {
            comboBox.getEditor().positionCaret(textLength);
        } else {
            comboBox.getEditor().positionCaret(caretPos);
        }
        moveCaretToPos = false;
    }
}
