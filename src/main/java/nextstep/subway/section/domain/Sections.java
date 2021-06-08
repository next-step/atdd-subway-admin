package nextstep.subway.section.domain;

import nextstep.subway.station.dto.StationResponse;

import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", fetch = FetchType.LAZY)
    private final List<Section> sections;

    public Sections() {
        this.sections = new ArrayList<>();
    }

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public void addSection(Section section) {
        this.sections.add(section);
    }

    public List<Section> getSection() {
        return this.sections;
    }

    public List<StationResponse> getStations() {
        List<StationResponse> stationResponses = new ArrayList<>();
        sections.stream()
                .forEach(
                        section -> stationResponses.add(StationResponse.of(section.getStation()))
                );
        return stationResponses;
    }
}
