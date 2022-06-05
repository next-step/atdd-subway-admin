package nextstep.subway.dto.line;

public class PutLineRequest {
    private String name;
    private String color;

    protected PutLineRequest() {
    }

    public PutLineRequest(String name, String color) {
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
