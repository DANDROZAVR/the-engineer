package engineer.utils;

public record Box(double left, double top, double width, double height) {
  public double right() {
    return left + width;
  }

  public double bottom() {
    return top + height;
  }
}
