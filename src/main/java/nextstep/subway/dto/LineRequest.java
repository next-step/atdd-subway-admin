package nextstep.subway.dto;

public class LineRequest {
    private String name;
    private String color;

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    private LineRequest(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public static LineRequest of(String name, String color) {
        return new LineRequest(name, color);
    }
}
