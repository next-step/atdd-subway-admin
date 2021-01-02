package nextstep.subway.section.domain;

import nextstep.subway.station.dto.StationResponse;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", cascade = CascadeType.REMOVE)
    private List<Section> items = new ArrayList<>();

    public void add(Section section) {
        items.add(section);
    }

    public boolean remove(Section section) {
        return items.remove(section);
    }

    public List<StationResponse> toStationResponses() {
        List<Section> sortedSections = makeSortedSections();
        return makeStationResponses(sortedSections);
    }

    private List<Section> makeSortedSections() {
        return items.stream()
                .sorted(Comparator.comparing(Section::getSectionNumber))
                .collect(Collectors.toList());
    }

    private List<StationResponse> makeStationResponses(List<Section> sections) {
        List<StationResponse> stations = sections.stream()
                .map(Section::getUpStation)
                .map(StationResponse::of)
                .collect(Collectors.toList());

        int lastIdx = sections.size() - 1;
        Section lastSection = sections.get(lastIdx);
        stations.add(StationResponse.of(lastSection.getDownStation()));
        return stations;
    }
}
