package nextstep.subway.line;

public enum TestLine {
    SHINBUNDANG("신분당선"), NUMBER_2("2호선");

    private String name;

    TestLine(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
