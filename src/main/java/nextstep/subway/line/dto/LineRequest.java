package nextstep.subway.line.dto;

import nextstep.subway.line.domain.Line;

import java.util.Objects;

public class LineRequest {
    private static final LineRequest EMPYTY = new LineRequest("", "");
    private String name;
    private String color;

    public LineRequest() {
    }

    public LineRequest(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Line toLine() {
        return new Line(name, color);
    }

    public void changeValues(Line line) {
        String existingName = line.getName();
        String existingColor = line.getColor();

        if (!Objects.isNull(this.name)) {
            existingName = this.name;
        }

        if (!Objects.isNull(this.color)) {
            existingColor = this.color;
        }

        line.update(new Line(existingName, existingColor));
    }
}
