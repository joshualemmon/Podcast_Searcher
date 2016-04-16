package sample;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.geometry.Insets;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import org.json.simple.*;
import org.json.simple.parser.JSONParser;

public class Main extends Application {

    private String searchUrl = "";
    private TextField searchField = null;
    private TableView<JSONData> table = null;
    private ObservableList<JSONData> list = null;

    @Override
    public void start(Stage primaryStage) throws Exception{
        Group root = new Group();
        Scene scene = new Scene(root, 955,805);
        primaryStage.setTitle("Podcast Browser");
        primaryStage.setScene(scene);
        primaryStage.show();
        drawUI(root, primaryStage);
    }

    private void drawUI(Group root, Stage primaryStage)
    {
        BorderPane bp = new BorderPane();
        bp.setPadding(new Insets(0,10,20,0));
        table = new TableView<>();
        table.setMinHeight(730);
        TableColumn nameCol = new TableColumn("Name");
        nameCol.setMinWidth(300);
        nameCol.setCellValueFactory(new PropertyValueFactory<JSONData, String>("trackName"));
        TableColumn presenterCol = new TableColumn("Presenter");
        presenterCol.setMinWidth(300);
        presenterCol.setCellValueFactory(new PropertyValueFactory<JSONData, String>("artistName"));
        TableColumn releaseCol = new TableColumn("Release Date");
        releaseCol.setMinWidth(150);
        releaseCol.setCellValueFactory(new PropertyValueFactory<JSONData, String>("date"));
        TableColumn ratingCol = new TableColumn("Rating");
        ratingCol.setMinWidth(100);
        ratingCol.setCellValueFactory(new PropertyValueFactory<JSONData, String>("rating"));
        TableColumn countryCol = new TableColumn("Country");
        countryCol.setMinWidth(100);
        countryCol.setCellValueFactory(new PropertyValueFactory<JSONData, String>("country"));
        table.getColumns().addAll(nameCol,presenterCol,releaseCol,ratingCol,countryCol);
        MenuBar mb = new MenuBar();
        final Menu fileMenu = new Menu("File");
        MenuItem saveAs = new MenuItem("Save As");
        saveAs.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
                FileChooser fc = new FileChooser();
                fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV File", "*.csv"));
                toFile(fc.showSaveDialog(primaryStage));
            }
        });
        MenuItem exit = new MenuItem("Exit");
        exit.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
                System.exit(0);
            }
        });
        fileMenu.getItems().addAll(saveAs, exit);
        mb.getMenus().add(fileMenu);

        Label searchLabel = new Label("Search Keyword:");
        searchField = new TextField();
        Button searchButton = new Button("Search");
        searchButton.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
                Thread t = new Thread(new handleDownloads());
                t.start();
            }
        });

        bp.setCenter(table);
        VBox vb = new VBox();
        HBox searchBox = new HBox();
        searchBox.setSpacing(10);
        searchBox.getChildren().addAll(new Label("   "),searchLabel,searchField,searchButton);
        vb.getChildren().addAll(new Label(""),searchBox);
        bp.setBottom(vb);
        bp.setTop(mb);
        root.getChildren().add(bp);
    }

    private void updateTable(ObservableList<JSONData> list)
    {
        table.setItems(list);
    }

    private void toFile(File file)
    {
        try
        {
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            for(JSONData jd : list)
            {
                bw.write(jd.getTrackName() + "\t" + jd.getArtistName() + "\t" + jd.getDate() + "\t" + jd.getRating()
                        + "\t" + jd.getCountry() + "\n");
            }
            bw.close();
        }catch(IOException ioe)
        {
            ioe.printStackTrace();
        }

    }


    public class handleDownloads implements Runnable
    {
        @Override
        public void run()
        {
            try
            {
                searchUrl = "https://itunes.apple.com/search?term=" + searchField.getText() + "&entity=podcast";
                URL url = new URL(searchUrl);
                URLConnection conn = url.openConnection();
                conn.setDoOutput(false);
                conn.setDoInput(true);
                InputStream inStream = conn.getInputStream();
                BufferedReader in = new BufferedReader(new InputStreamReader(inStream));

                ArrayList joList = new ArrayList<JSONData>();
                JSONParser parser = new JSONParser();
                JSONObject obj = (JSONObject)parser.parse(in);//only getting resultCount and results?
                JSONArray results = (JSONArray)obj.get("results");
                for(int i =0; i < results.size(); i++)
                {
                    joList.add(new JSONData((JSONObject)results.get(i)));
                }

                list = FXCollections.observableArrayList(joList);
                updateTable(list);

                in.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
