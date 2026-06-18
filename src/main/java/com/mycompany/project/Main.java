package com.mycompany.project;


public class Main {
    // public static void main(String[] args) {
    //     new LoginForm().setVisible(true);
    // }
  public static void main(String[] args) {
        Preloader preloader = new Preloader();

        preloader.showAndRun(() -> {
            new LoginForm().setVisible(true);
        });
    }

}

