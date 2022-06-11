package nextstep.subway.line.dto;

public class LineUpdateRequest {
    private final String name;
    private final String color;

    public LineUpdateRequest(final String name, final String color) {
        this.name = name;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    @Override
    public String toString() {
        return "LineAddRequest{" +
                "name='" + name + '\'' +
                ", color='" + color + '\'' +
                '}';
    }
}
