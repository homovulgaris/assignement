package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import sample.core.CommonsPart;
import sample.core.Eater;
import sample.core.OccurencePart;

import java.util.List;
import java.util.Map;

public class Controller {
    
    @FXML
    private TableView<CommonsPart> commons = new TableView<>();
    
    @FXML
    private TableView<OccurencePart> occurrence = new TableView<>();
    
    @FXML
    private TableColumn<OccurencePart, String> wordOccurrenceWord = new TableColumn<>();
    
    @FXML
    private TableColumn<OccurencePart, String> wordOccurrenceCount = new TableColumn<>();
    
    @FXML
    private TableColumn<CommonsPart, String> wordCommonsLongest = new TableColumn<>();
    
    @FXML
    private TableColumn<CommonsPart, String> wordCommonsLetter = new TableColumn<>();
    
    @FXML
    private TextArea textArea;
    
    @FXML
    private Button button;
    
    @FXML
    private TextField urlInput;
    
    @FXML
    private CheckBox includeNumber;
    
    @FXML
    private CheckBox includeSingleCharacter;
    
    public Controller() {
    }
    
    @FXML
    private void initialize() {
        button.setOnAction(this::handleButtonAction);
        
        wordOccurrenceWord.setCellValueFactory(new PropertyValueFactory<>("word"));
        wordOccurrenceCount.setCellValueFactory(new PropertyValueFactory<>("count"));
        
        wordCommonsLongest.setCellValueFactory(new PropertyValueFactory<>("longestWord"));
        wordCommonsLetter.setCellValueFactory(new PropertyValueFactory<>("mostCommonLetter"));
        
        
    }
    
    private void handleButtonAction(ActionEvent event) {
        
        textArea.clear();
        occurrence.getItems().clear();
        commons.getItems().clear();
        final boolean numberSelected = includeNumber.isSelected();
        final boolean singleCharacterSelected = includeSingleCharacter.isSelected();
        String url = urlInput.getText();
        Eater eater = new Eater(url, numberSelected, singleCharacterSelected);
        List<String> wholeText;
        try {
            wholeText = eater.getWholeText();
        } catch (Exception e) {
            showError(e);
            return;
        }
        textArea.appendText(wholeText.toString());
        occurrence.setEditable(true);
        occurrence.setItems(populateObservableList(eater.sortedByLengthOfWord(eater.getOfOccurencesOfWord())));
        commons.setItems(populateCommons(eater));
    }
    
    
    private void showError(Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Well, something is not right!");
        alert.setContentText(e.getMessage());
        alert.showAndWait().ifPresent(rs -> {
            if (rs == ButtonType.OK) {
                System.out.println("Pressed OK.");
            }
        });
    }
    
    private ObservableList<OccurencePart> populateObservableList(Map<String, Long> anOccurence) {
        final ObservableList<OccurencePart> data =
                FXCollections.observableArrayList();
        anOccurence.forEach((key, value) -> {
            data.add(new OccurencePart(key, value));
        });
        return data;
    }
    
    private ObservableList<CommonsPart> populateCommons(Eater eater) {
        CommonsPart commonsPart = new CommonsPart(eater.getLongestWord().getKey(), eater.getMostCommonLetter());
        final ObservableList<CommonsPart> data =
                FXCollections.observableArrayList();
        data.add(commonsPart);
        return data;
    }
    
    
}
