package de.tzerr.cli.demo.example;

public class Main {

  public static void main(String[] args) {
    var gameManager = new GameManager(args);

    //noinspection InfiniteLoopStatement
    while (true) {
      gameManager.render();
    }
  }
}
