package nextstep.subway.dto.line;

public class UpdateLineRequest {
    private String name;
    private String color;

    protected UpdateLineRequest() {
    }

    public UpdateLineRequest(String name, String color) {
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
