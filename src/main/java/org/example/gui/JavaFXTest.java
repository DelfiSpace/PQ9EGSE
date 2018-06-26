/*
 * Copyright (C) 2018 Stefano Speretta
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.example.gui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Region;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

/**
 *
 * @author Stefano Speretta <s.speretta@tudelft.nl>
 */
public class JavaFXTest extends Application {

 private Scene scene;
 MyBrowser myBrowser;

 /**
  * @param args the command line arguments
  */
 public static void main(String[] args) {
     launch(args);
 }

 @Override
 public void start(Stage primaryStage) {
     primaryStage.setTitle("PQ9 EGSE");
  
     myBrowser = new MyBrowser();
     scene = new Scene(myBrowser, 640, 480);
  
     primaryStage.setScene(scene);
     primaryStage.show();
 }

 class MyBrowser extends Region{
  
     WebView webView = new WebView();
     WebEngine webEngine = webView.getEngine();
    
          
     public MyBrowser()
     {
         webEngine.load("http://127.0.0.1:8080");
         getChildren().add(webView);
     }
 }
}
