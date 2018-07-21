package Controller;

import CommonClass.NameAndDirectory;
import CommonClass.ProjectToUpload;
import CommonClass.ResourceManager;
import static CommonClass.ResourceManager.load;
import CommonClass.ViewfolderClass;
import CommonCommand.Command;
import CommonCommand.GetFile;
import CommonCommand.GetPush;
import CommonRespone.Respone;
import CommonRespone.ResponeType;
import CommonRespone.SendFile;
import CommonRespone.SendProject;
import static client.Project.networkInput;
import static client.Project.networkOutput;
import com.jfoenix.controls.JFXTextField;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;

public class PushProjectController implements Initializable {

    String NameProject;
    String NameBranch;

    @FXML
    private JFXTextField Comment;

    @FXML
    private JFXTextField Path;

    File selectedFile;

    @FXML
    void btnBrowsers(ActionEvent event) {

        DirectoryChooser dc = new DirectoryChooser();
        selectedFile = dc.showDialog(null);
        if (selectedFile != null) {
            Path.setText(selectedFile.getPath());
        }
    }

    @FXML
    void btnPush(ActionEvent event) {
        if(Comment.getText().length()==0)
        {
            Notifications notification = Notifications.create()
                    .title("Push Project")
                    .text("Not Name")
                    .graphic(null)
                    .hideAfter(Duration.seconds(2))
                    .position(Pos.CENTER);
            notification.showConfirm();
            return;
        }
        if (Path == null || selectedFile == null) {
            Notifications notification = Notifications.create()
                    .title("Push Project")
                    .text("Not Selected File")
                    .graphic(null)
                    .hideAfter(Duration.seconds(2))
                    .position(Pos.CENTER);
            notification.showConfirm();
            return;
        }
        ProjectToUpload hiddenFile = null;
        for (File file : selectedFile.listFiles()) {
            if (file.isFile() && "BEHKN.BEHKN".equals(file.getName())) {
                try {
                    hiddenFile = (ProjectToUpload) load(file.getPath());
                    file.delete();
                    break;
                } catch (Exception ex) {

                }
            }
        }
        if (hiddenFile == null) {
            return;
        }
        hiddenFile.BranchName = NameBranch;
        Command command = new GetPush(NameProject, hiddenFile, selectedFile.getName(), Comment.getText());
        try {
            networkOutput.writeObject(command);
            networkOutput.flush();
        } catch (IOException ex) {
            Logger.getLogger(FileBrowsersController.class.getName()).log(Level.SEVERE, null, ex);
        }
        Respone respone = null;
        try {
            respone = (Respone) networkInput.readObject();
        } catch (IOException | ClassNotFoundException ex) {
            System.out.println("ERROR in GETPUSH: " + ex.getMessage());
        }

        if (respone.TypeRespone == ResponeType.DONE) {
            ViewfolderClass ob = ResourceManager.ViewProject(new File(selectedFile.getPath()));
            ResourceManager.ShowViewfolder(ob);
            Respone newRespone = new SendProject(ob);
            try {
                networkOutput.writeObject(newRespone);
                networkOutput.flush();
            } catch (IOException ex) {
                Logger.getLogger(FileBrowsersController.class.getName()).log(Level.SEVERE, null, ex);
            }
            openProgressBar();
            SendFolder(ob);

            try {
                ProjectToUpload BenkhFile = (ProjectToUpload) networkInput.readObject();
                ResourceManager.save(BenkhFile, Path.getText() + "\\" + "BEHKN.BEHKN");
                Path.getScene().getWindow().hide();

            } catch (IOException | ClassNotFoundException ex) {
                Logger.getLogger(PushProjectController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Exception ex) {
                Logger.getLogger(PushProjectController.class.getName()).log(Level.SEVERE, null, ex);
            }
            /**
             * *
             */
            Notifications notification = Notifications.create()
                    .title("Push Project")
                    .text("Done Push Project")
                    .graphic(null)
                    .hideAfter(Duration.seconds(2))
                    .position(Pos.CENTER);
            notification.showConfirm();

        } else {
            /**
             * **
             */
            Notifications notification = Notifications.create()
                    .title("Push Project")
                    .text("Can't Push Project")
                    .graphic(null)
                    .hideAfter(Duration.seconds(2))
                    .position(Pos.CENTER);
            notification.showError();

        }
    }

    private void SendFolder(ViewfolderClass ob) {
        for (NameAndDirectory temp : ob.MyFile) {
            GetFile get = new GetFile(temp.Directory);
            GETFILE(get);
        }
        for (ViewfolderClass temp : ob.MyFolderView) {
            SendFolder(temp);
        }
    }

    private void GETFILE(Command command) {
        FileInputStream fis = null;
        try {
            byte[] DataFile = new byte[4096];
            String dir = ((GetFile) command).getDirectoryFile();
            File file = new File(dir);
            NameAndDirectory My = new NameAndDirectory(file.getName(), file.length(), file.lastModified(), dir);
            fis = new FileInputStream(file);
            long fileSize = file.length();
            int n;
            if (fileSize == 0) {
                Respone respone = new SendFile(DataFile, fileSize == 0, My, 0);
                networkOutput.writeObject(respone);
                networkOutput.flush();
            }
            while (fileSize > 0 && (n = fis.read(DataFile, 0, (int) Math.min(4096, fileSize))) != -1) {
                long tmp = fileSize;
                fileSize -= n;
                Respone respone = new SendFile(DataFile, fileSize == 0, My, tmp);
                networkOutput.writeObject(respone);
                networkOutput.flush();
            }

        } catch (FileNotFoundException ex) {
        } catch (IOException ex) {
        } finally {
            try {
                fis.close();
            } catch (IOException ex) {
            }
        }

    }

    public void setNameProject(String NameProject) {
        this.NameProject = NameProject;
    }

    public void setNameBranch(String NameBranch) {
        this.NameBranch = NameBranch;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    private void openProgressBar() {
        Stage stage = new Stage();
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/FXML/ProgressBar.fxml"));
            AnchorPane root = (AnchorPane) fxmlLoader.load();

            ((ProgressBarController) fxmlLoader.getController()).setStage(stage);
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.showAndWait();

        } catch (IOException ex) {
            System.out.println("Error in Load Fxml PushProject: " + ex.getMessage());
        }

    }

}
