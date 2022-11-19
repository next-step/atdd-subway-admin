package nextstep.subway.dto.response;

import nextstep.subway.domain.line.Line;

import java.time.LocalTime;

public class LineReponse {
    private Long id;
    private String name;
    private String color;
    private LocalTime startTime;
    private LocalTime endTime;
    private String intervalTime;

    public static LineReponse of(Line line) {
        return new LineReponse(line.getId(), line.getName(), line.getColor(), line.getStartTime(), line.getEndTime(), line.getIntervalTime());
    }

    public LineReponse() {}

    public LineReponse(Long id, String name, String color, LocalTime startTime, LocalTime endTime, String intervalTime) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.startTime = startTime;
        this.endTime = endTime;
        this.intervalTime = intervalTime;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public String getIntervalTime() {
        return intervalTime;
    }
}
