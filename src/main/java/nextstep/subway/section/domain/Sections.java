package nextstep.subway.section.domain;

import nextstep.subway.station.dto.StationResponse;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Embeddable
public class Sections {
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "line_id")
    private final List<Section> sections = new ArrayList<>();

    public void addSection(Section section) {
        sections.add(section);
    }

    public List<StationResponse> convertToStationResponses() {
        Set<StationResponse> stations = new LinkedHashSet<>();
        sections.forEach(section -> {
            stations.add(StationResponse.of(section.getUpStation()));
            stations.add(StationResponse.of(section.getDownStation()));
        });
        return new ArrayList<>(stations);
    }
}
