package nextstep.subway.dto;

public class LineEditRequest {
    private String name;
    private String color;

    public LineEditRequest() {}
    public LineEditRequest(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }
}
