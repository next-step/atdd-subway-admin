package nextstep.subway.dto;

public class UpdateLineRequest {

    private String name;
    private String color;

    private UpdateLineRequest() {

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
