package com.template;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;


import javafx.stage.FileChooser;

public class ReportsController {

    @FXML
    private TableView<Student> reportTable;
    @FXML private TableColumn<Student, String> idColumn;
    @FXML private TableColumn<Student, String> nameColumn;
    @FXML private TableColumn<Student, String> programmeColumn;
    @FXML private TableColumn<Student, String> levelColumn;
    @FXML private TableColumn<Student, String> gpaColumn;
    @FXML private TableColumn<Student, String> phoneColumn;
    @FXML private TableColumn<Student, String> emailColumn;
    @FXML private TextField gpaThresholdField;

    private final StudentService studentService = new StudentService();
    private final ObservableList<Student> reportList =
            FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("studentId"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        programmeColumn.setCellValueFactory(new PropertyValueFactory<>("programme"));
        levelColumn.setCellValueFactory(new PropertyValueFactory<>("level"));
        gpaColumn.setCellValueFactory(new PropertyValueFactory<>("gpa"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone") );
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("Email"));
        reportTable.setItems(reportList);

    }

    @FXML
    private void handleLoadReport() {
        // Clear any previous data
        reportList.clear();

        // Optional: GPA threshold (for At Risk students)
        double threshold = 2.0; // default
        if (!gpaThresholdField.getText().isEmpty()) {
            try {
                threshold = Double.parseDouble(gpaThresholdField.getText());
            } catch (NumberFormatException e) {
                threshold = 2.0; // fallback
            }
        }

        // Get data from service
        reportList.addAll(studentService.getAtRiskStudents(threshold));
        AppLogger.log("At-risk report generated with GPA threshold:" + threshold);
    }

    @FXML
    private void handleClearReport(){
        reportList.clear();
        gpaThresholdField.clear();
    }

    @FXML
    private void handleExportCSV() {

        // Open file save dialog
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Report");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV Files", "*.csv")
        );
        fileChooser.setInitialFileName("student_report.csv");

        File file = fileChooser.showSaveDialog(reportTable.getScene().getWindow());

        if (file == null) {
            return; // user cancelled
        }

        try (FileWriter writer = new FileWriter(file)) {

            // Write CSV header
            writer.write("Student ID,Full Name,Programme,Level,GPA,Email,Phone\n");

            // Write table data
            for (Student s : reportTable.getItems()) {
                writer.write(
                        s.getStudentId() + "," +
                                s.getFullName() + "," +
                                s.getProgramme() + "," +
                                s.getLevel() + "," +
                                s.getGpa() + "," +
                                s.getEmail() + "," +
                                s.getPhone() + "\n"
                );
            }

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Export Successful");
            alert.setHeaderText(null);
            alert.setContentText("Report exported successfully.");
            alert.showAndWait();

        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Export Failed");
            alert.setHeaderText(null);
            alert.setContentText("Could not export report.");
            alert.showAndWait();
        }
    }
    @FXML
    private void handleImportCSV() {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select CSV File");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV Files", "*.csv")
        );

        File file = fileChooser.showOpenDialog(reportTable.getScene().getWindow());

        if (file == null) {
            return;
        }

        int importedCount = 0;
        int skippedCount = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {

            String line;
            reader.readLine(); // skip header

            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");

                if (data.length < 7) {
                    continue; // skip invalid row
                }

                Student student = new Student(
                        data[0].trim(), // studentId
                        data[1].trim(), // fullName
                        data[2].trim(), // programme
                        data[3].trim(), // level
                        data[4].trim(), // gpa
                        data[5].trim(), // email
                        data[6].trim()  // phone
                );



                boolean added = studentService.addStudent(student);
               if(added){
                importedCount++;
            }else{
                   skippedCount++;
               }
            }

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Import Complete");
            alert.setHeaderText(null);
            alert.setContentText(importedCount + " students imported successfully.\n" + skippedCount + "duplicates skipped.");
            AppLogger.log(importedCount + "Students imported from CSV");
            AppLogger.log(skippedCount + "duplicate students skipped during CSV import");
            alert.showAndWait();

        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Import Failed");
            alert.setHeaderText(null);
            alert.setContentText("Error importing CSV file.");
            alert.showAndWait();
        }
    }

}
