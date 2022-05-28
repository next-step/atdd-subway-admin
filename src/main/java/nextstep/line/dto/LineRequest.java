package nextstep.line.dto;

public class LineRequest {
    private String name;

    private LineRequest(String name) {
        this.name = name;
    }

    public static LineRequest of(String name) {
        return new LineRequest(name);
    }

    public String getName() {
        return name;
    }
}
