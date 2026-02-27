package com.template;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;

public class MainController {

    @FXML private TextField studentIdField;
    @FXML private TextField fullNameField;
    @FXML private TextField programmeField;
    @FXML private TextField levelField;
    @FXML private TextField gpaField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;

    @FXML private TableView<Student> studentTable;
    @FXML private TableColumn<Student, String> idColumn;
    @FXML private TableColumn<Student, String> nameColumn;
    @FXML private TableColumn<Student, String> programmeColumn;
    @FXML private TableColumn<Student, String> levelColumn;
    @FXML private TableColumn<Student, String> gpaColumn;
    @FXML private TableColumn<Student, String> emailColumn;
    @FXML private TableColumn<Student, String> phoneColumn;
    @FXML
    private ComboBox<String> programmeFilter;



    @FXML private Button editStudentButton;
    @FXML private Button deleteStudentButton;
    @FXML private Button clearEditStudent;
    @FXML private Button addStudentButton;

    private final StudentService studentService = new StudentService();

    // ADD: StudentDAO for SQLite integration
    private final StudentDAO studentDAO = new StudentDAO();

    private final ObservableList<Student> studentList = FXCollections.observableArrayList();

    // Call this method automatically after FXML is loaded
    @FXML
    private void initialize() {
        // Connect columns to com.template.Student fields
        idColumn.setCellValueFactory(new PropertyValueFactory<>("studentId"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        programmeColumn.setCellValueFactory(new PropertyValueFactory<>("programme"));
        levelColumn.setCellValueFactory(new PropertyValueFactory<>("level"));
        gpaColumn.setCellValueFactory(new PropertyValueFactory<>("gpa"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));

        // Bind list to table
        studentTable.setItems(studentList);

        // ADD: Load students from DB
        studentDAO.createTable(); // Ensure table exists
        studentList.addAll(studentDAO.getAllStudents());

        programmeFilter.getItems().add("All");

        for (Student s : studentList) {
            if (!programmeFilter.getItems().contains(s.getProgramme())) {
                programmeFilter.getItems().add(s.getProgramme());
            }
        }

        programmeFilter.getSelectionModel().selectFirst();

        studentTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                studentIdField.setText(newSelection.getStudentId());
                fullNameField.setText(newSelection.getFullName());
                programmeField.setText(newSelection.getProgramme());
                levelField.setText(newSelection.getLevel());
                gpaField.setText(newSelection.getGpa());
                emailField.setText(newSelection.getEmail());
                phoneField.setText(newSelection.getPhone());
            }
        });
    }

    @FXML
    private void handleAddStudent() {
        try{ // Create a com.template.Student object from form
            Student student = new Student(
                    studentIdField.getText(),
                    fullNameField.getText(),
                    programmeField.getText(),
                    levelField.getText(),
                    gpaField.getText(),
                    emailField.getText(),
                    phoneField.getText()
            );

            studentService.validateStudent(student);

            if(studentDAO.studentExists(student.getStudentId())){
                showError("Student ID already exits!");
                return;
            }

            studentDAO.insert(student);

            studentList.add(student);
            handleClearForm();
        } catch (IllegalArgumentException ex) {
            showError(ex.getMessage());
        }
    }

    @FXML
    private void handleEditStudent(){
        Student selected = studentTable.getSelectionModel().getSelectedItem();
        if(selected != null){
            selected = new Student(
                    studentIdField.getText(),
                    fullNameField.getText(),
                    programmeField.getText(),
                    levelField.getText(),
                    gpaField.getText(),
                    emailField.getText(),
                    phoneField.getText()
            );

            int index = studentTable.getSelectionModel().getSelectedIndex();
            studentList.set(index,selected);

            // ADD: Update in database
            studentDAO.update(selected);

            handleClearForm();
        }else{
            System.out.println("No selected selected to edit:");
        }
    }

    @FXML
    private void handleDeleteStudent(){
        Student selected = studentTable.getSelectionModel().getSelectedItem();
        if(selected != null){
            studentList.remove(selected);

            // ADD: Delete from database
            studentDAO.delete(selected.getStudentId());

            handleClearForm();
        }else{
            System.out.println("No student selected to delete");
        }
    }

    @FXML
    private void handleClearForm(){
        studentIdField.clear();
        fullNameField.clear();
        programmeField.clear();
        levelField.clear();
        gpaField.clear();
        emailField.clear();
        phoneField.clear();
    }

    private void showError(String message){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Validation Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    @FXML
    private void handleProgrammeFilter() {
        String selected = programmeFilter.getValue();

        if (selected.equals("All")) {
            studentTable.setItems(studentList);
            return;
        }

        ObservableList<Student> filtered = FXCollections.observableArrayList();

        for (Student s : studentList) {
            if (s.getProgramme().equalsIgnoreCase(selected)) {
                filtered.add(s);
            }
        }

        studentTable.setItems(filtered);
    }

    @FXML
    private void handleSortByGpa() {

        ObservableList<Student> sortedList = FXCollections.observableArrayList(studentTable.getItems());

        sortedList.sort((s1, s2) -> {
            try {
                double gpa1 = Double.parseDouble(s1.getGpa());
                double gpa2 = Double.parseDouble(s2.getGpa());
                return Double.compare(gpa2, gpa1); // Descending
            } catch (NumberFormatException e) {
                return 0;
            }
        });

        studentTable.setItems(sortedList);
    }

    @FXML
    private void handleSortByName() {
        ObservableList<Student> sortedList = FXCollections.observableArrayList(studentTable.getItems());
        sortedList.sort((s1, s2) -> s1.getFullName().compareToIgnoreCase(s2.getFullName()));
        studentTable.setItems(sortedList);
    }

    @FXML
    private void handleGenerateReport() {

        int totalStudents = studentList.size();
        double totalGpa = 0;
        int count = 0;

        for (Student s : studentList) {
            try {
                totalGpa += Double.parseDouble(s.getGpa());
                count++;
            } catch (NumberFormatException ignored) {
            }
        }

        double averageGpa = count == 0 ? 0 : totalGpa / count;

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Student Report");
        alert.setHeaderText("Summary Report");
        alert.setContentText(
                "Total Students: " + totalStudents +
                        "\nAverage GPA: " + String.format("%.2f", averageGpa)
        );
        alert.showAndWait();
    }

    @FXML
    private void openReportsWindow() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com.template/reports.fxml"));
        Parent root = loader.load();
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle("Student Reports");
        stage.show();
    }
}
