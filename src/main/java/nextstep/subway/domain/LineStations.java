package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import nextstep.subway.dto.SectionResponse;
import nextstep.subway.dto.StationResponse;

@Embeddable
public class LineStations {
    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LineStation> lineStations = new ArrayList<>();

    public LineStations() {
    }

    public LineStations(final List<LineStation> lineStations) {
        this.lineStations = lineStations;
    }

    public void add(final LineStation lineStation) {
        lineStations.add(lineStation);
    }

    public List<StationResponse> stations() {
        return lineStations
                .stream()
                .map(LineStation::getStation)
                .map(station -> StationResponse.of(station))
                .collect(Collectors.toList());
    }

    public Optional<LineStation> getByStation(final Station station) {
        return lineStations
                .stream()
                .filter(lineStation -> lineStation.getStation().equals(station))
                .findFirst();
    }

    public List<SectionResponse> sections() {
        final List<SectionResponse> sections = new ArrayList<>();
        if (!lineStations.isEmpty()) {
            lineStations.forEach(lineStation -> addSection(sections, lineStation));
        }
        return sections;
    }

    private void addSection(final List<SectionResponse> sections, final LineStation lineStation) {
        if (null != lineStation.getNext()) {
            sections.add(new SectionResponse(
                    lineStation.getLine().getName(),
                    lineStation.getStation().getName(),
                    lineStation.getNext().getName(),
                    lineStation.getDistanceToNext()));
        }
    }
}
