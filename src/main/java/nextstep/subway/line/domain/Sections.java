package nextstep.subway.line.domain;

import nextstep.subway.line.exception.NotFoundUpAndDownStation;
import nextstep.subway.station.dto.StationResponse;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    protected Sections() {
    }

    public Sections(Section section) {
        sections.add(section);
    }

    public List<StationResponse> getStations() {
        return sections.stream()
                .flatMap(section -> section.getStations().stream())
                .map(station -> StationResponse.of(station))
                .distinct()
                .collect(Collectors.toList());
    }

    public void addSection(Section section) {
        if (isNotExistsUpAndDownStation(section)) {
            throw new NotFoundUpAndDownStation();
        }

        Section newSection = null;
        if(sections.stream().anyMatch(sec -> sec.equalsUpStation(section))) {
            Section savedSection = sections.stream().filter(sec -> sec.equalsUpStation(section)).findAny().get();
            newSection = savedSection.updateSectionEqualUpStation(section);


        } else if(sections.stream().anyMatch(sec -> sec.equalsDownStation(section))) {
            Section savedSection = sections.stream().filter(sec -> sec.equalsDownStation(section)).findAny().get();
            newSection = savedSection.updateSectionEqualDownStation(section);
        }


        sections.add(newSection);
    }

    private boolean isNotExistsUpAndDownStation(Section section) {
        return sections.stream()
                .allMatch(savedSection -> savedSection.isNotContainUnAndDownStation(section));
    }

}
