package Controller;

import CommonCommand.Command;
import CommonCommand.StartProject;
import CommonRespone.Respone;
import CommonRespone.SendCreateProject;
import CommonRespone.ResponeType;
import static Controller.PageMainController.Owner;
import static client.Project.networkInput;
import static client.Project.networkOutput;
import com.jfoenix.controls.JFXTextField;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.stage.DirectoryChooser;

public class CreateProjectController implements Initializable {

    @FXML
    private JFXTextField txtNameProject;

    @FXML
    private JFXTextField txtLocation;

    @FXML
    void btnBrowsers(ActionEvent event) {
        DirectoryChooser dc = new DirectoryChooser();
        File selectedFile = dc.showDialog(null);

        if (selectedFile != null) {
            txtLocation.setText(selectedFile.getPath());
        }
    }

    @FXML
    void btnCreate(ActionEvent event) {
        try {
            String path = txtLocation.getText();
            Command command = new StartProject(txtNameProject.getText(), "true");
            networkOutput.writeObject(command);
            networkOutput.flush();

            Respone response = (Respone) networkInput.readObject();
            if (response.TypeRespone == ResponeType.DONE) {
                Respone respone1 = (Respone) networkInput.readObject();
                int IdProject = ((SendCreateProject) respone1).IdProject;
                String Author = ((SendCreateProject) respone1).Author;
                int lastCommit = 1;
                List<String> Contributors = new ArrayList<>();
                Contributors.add(Author);
                ProjectToUpload Temp = new ProjectToUpload(path + "\\.BENKH", 1, IdProject, Contributors, "Master");
                Temp.Save();
            }
        } catch (IOException | ClassNotFoundException ex) {
            System.out.println("Error START PROJECT");
        }
    }

    @FXML
    void Close(ActionEvent event) {

    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        System.out.println("HELLLOOOOOOOO");
        System.out.println(Owner);
    }

}
