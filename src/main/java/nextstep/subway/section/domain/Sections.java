package nextstep.subway.section.domain;

import nextstep.subway.station.dto.StationResponse;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", cascade = CascadeType.REMOVE)
    @OrderBy("sectionNumber")
    private List<Section> items = new ArrayList<>();

    public void add(Section section) {
        items.add(section);
    }

    public boolean remove(Section section) {
        return items.remove(section);
    }

    public List<StationResponse> toStationResponses() {
        List<StationResponse> stations = items.stream()
                .map(Section::getUpStation)
                .map(StationResponse::of)
                .collect(Collectors.toList());

        int lastIdx = items.size() - 1;
        Section lastSection = items.get(lastIdx);
        stations.add(StationResponse.of(lastSection.getDownStation()));
        return stations;
    }
}
