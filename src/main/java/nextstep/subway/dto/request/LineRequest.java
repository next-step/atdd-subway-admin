package nextstep.subway.dto.request;

import nextstep.subway.domain.line.Line;

import java.time.LocalTime;

public class LineRequest {
    private String name;
    private String color;
    private String startTime;
    private String endTime;
    private String intervalTime;

    public LineRequest() {}

    public LineRequest(String name, String color, String startTime, String endTime, String intervalTime) {
        this.name = name;
        this.color = color;
        this.startTime = startTime;
        this.endTime = endTime;
        this.intervalTime = intervalTime;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public String  getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getIntervalTime() {
        return intervalTime;
    }

    public Line toLine() {
        return new Line(name, color, LocalTime.parse(startTime), LocalTime.parse(endTime), intervalTime);
    }
}
