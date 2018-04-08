package Controller;

import client.TabelBranch;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class BranchController implements Initializable {

    List<String> NameBranch;

    @FXML
    private TableView<TabelBranch> tabelView;

    @FXML
    private TableColumn<TabelBranch, String> C1;

    public BranchController(List<String> NameBranch) {
        this.NameBranch = NameBranch;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("Size: " + NameBranch.size());
        
        ObservableList<TabelBranch> list;
        TabelBranch[] st = new TabelBranch[NameBranch.size()];
        int idx = 0;
        for (String temp : NameBranch) {
            st[idx] = new TabelBranch(temp);
        }
        list = FXCollections.observableArrayList(st);
        C1.setCellValueFactory(new PropertyValueFactory<>("NameBranch"));
        tabelView.setItems(list);
    }

}