package nextstep.subway.dto;

public class LineChange {
    private String name;
    private String color;

    public static LineChange of(LineRequest lineRequest) {
        LineChange lineChange = new LineChange();
        lineChange.name = lineRequest.getName();
        lineChange.color = lineRequest.getColor();
        return lineChange;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }
}
