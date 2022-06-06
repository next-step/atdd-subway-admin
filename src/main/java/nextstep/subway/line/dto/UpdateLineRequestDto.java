package nextstep.subway.line.dto;

public class UpdateLineRequestDto {
    private String name;
    private String color;

    private UpdateLineRequestDto() {
    }

    public UpdateLineRequestDto(String name, String color) {
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
