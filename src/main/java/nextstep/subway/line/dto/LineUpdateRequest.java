package nextstep.subway.line.dto;

public class LineUpdateRequest {
    private String name;
    private String color;

    private LineUpdateRequest() {
    }

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
