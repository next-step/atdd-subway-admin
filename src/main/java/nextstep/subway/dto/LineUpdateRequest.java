package nextstep.subway.dto;

public class LineUpdateRequest {
    private final String name;
    private final String color;

    private LineUpdateRequest(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public static LineUpdateRequest of(String name, String color) {
        return new LineUpdateRequest(name, color);
    }

    public String getName() {
        return name;
    }
    public String getColor() {
        return color;
    }

}
