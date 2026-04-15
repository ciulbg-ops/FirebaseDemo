package aydin.firebasedemo;

import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;


import com.google.firebase.auth.*;
import com.google.cloud.firestore.*;
import com.google.api.core.ApiFuture;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class WelcomeController {
    @FXML
    private TextField emailTextField;

    @FXML
    private TextField passwordField;

    @FXML
    void registerButtonClicked(ActionEvent event) {
        try
        {
            registerUser();

            DocumentReference docRef = DemoApp.fstore.collection("Users").document(UUID.randomUUID().toString());

            Map<String, Object> data = new HashMap<>();
            data.put("Email", emailTextField.getText());
            data.put("Password", passwordField.getText());

            //asynchronously write data
            ApiFuture<WriteResult> result = docRef.set(data);
        }
        catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error registering user");
            alert.setContentText(e.getLocalizedMessage());

            alert.showAndWait();

            return;
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText("Registered user");

        alert.showAndWait();
    }

    @FXML
    void signInButtonClicked(ActionEvent event) throws IOException {
        try {
            boolean authenticated = !DemoApp.fstore.collection("Users")
                    .whereEqualTo("Email", emailTextField.getText())
                    .whereEqualTo("Password", passwordField.getText())
                    .get().get().getDocuments().isEmpty();

            if(authenticated)
            {
                DemoApp.setRoot("Primary");
            }
            else
            {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Incorrect password");
                alert.setHeaderText("Incorrect password");

                alert.showAndWait();
            }
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void registerUser() throws FirebaseAuthException {
        UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                .setEmail(emailTextField.getText())
                .setEmailVerified(false)
                .setPassword(passwordField.getText())
                .setDisabled(false);

        UserRecord userRecord = DemoApp.fauth.createUser(request);
    }
}
