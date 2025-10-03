package de.tzerr.cli.demo.example;

public class FinishedView implements View {

  @Override
  public Class<? extends View> render() {
    System.exit(0);

    throw new IllegalStateException("Should never be reached");
  }
}
