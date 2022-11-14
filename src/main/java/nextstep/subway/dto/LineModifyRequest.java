package nextstep.subway.dto;

public class LineModifyRequest {
    private String name;
    private String color;

    public LineModifyRequest() {
    }

    private LineModifyRequest(String newLineName, String newColor) {
        name = newLineName;
        color = newColor;
    }

    public static LineModifyRequest of(String newLineName, String newColor) {
        return new LineModifyRequest(newLineName,newColor);
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }
}
