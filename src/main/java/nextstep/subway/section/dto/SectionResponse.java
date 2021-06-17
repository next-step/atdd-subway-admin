package nextstep.subway.section.dto;

import nextstep.subway.line.domain.Line;
import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;

import java.time.LocalDateTime;

public class SectionResponse {
    private long id;
    private Station station;
    private Line line;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;

    public SectionResponse() {
    }

    public SectionResponse(long id, Station station, Line line, LocalDateTime createdDate, LocalDateTime modifiedDate) {
        this.id = id;
        this.station = station;
        this.line = line;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
    }

    public static SectionResponse of(Section section) {
        return new SectionResponse(section.getId(), section.getStation(), section.getLine(), section.getCreatedDate(),
                section.getModifiedDate());
    }

    public long getId() {
        return id;
    }

    public Station getStation() {
        return station;
    }

    public Line getLine() {
        return line;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public LocalDateTime getModifiedDate() {
        return modifiedDate;
    }
}
