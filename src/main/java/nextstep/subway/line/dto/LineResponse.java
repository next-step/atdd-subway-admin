package nextstep.subway.line.dto;

import nextstep.subway.line.domain.Line;
import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class LineResponse {

    private Long id;

    private String name;

    private String color;

    private LocalDateTime createdDate;

    private LocalDateTime modifiedDate;

    private List<Station> stations = new ArrayList<>();

    public LineResponse() {
    }

    public LineResponse(Line line) {
        this.id = line.getId();
        this.name = line.getName();
        this.color = line.getColor();
        this.createdDate = line.getCreatedDate();
        this.modifiedDate = line.getModifiedDate();
        this.stations = line.getSections().stream().map(Section::getStation).collect(Collectors.toList());
    }

    public static LineResponse of(Line line) {
        stationsSort(line.getSections().stream().map(Section::getStation).collect(Collectors.toList()));
        return new LineResponse(line);
    }

    private static void stationsSort(List<Station> stations) {
        Collections.sort(stations, new Comparator<Station>() {
            @Override
            public int compare(Station o1, Station o2) {
                if(o1.getId() > o2.getId()){
                   return 1;
                }
                if(o1.getId() < o2.getId()){
                    return -1;
                }
                return 0;
            }
        });
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

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public LocalDateTime getModifiedDate() {
        return modifiedDate;
    }

    public List<Station> getStations() {
        return stations;
    }
}
