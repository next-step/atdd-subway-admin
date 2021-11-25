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

    public void addSection(Section inputSection) {
        if (isNotExistsUpAndDownStation(inputSection)) {
            throw new NotFoundUpAndDownStation();
        }

        // 내부 구간 추가
        Section savedSection = this.sections.stream()
                .filter(section -> section.equalsUpStation(inputSection) || section.equalsDownStation(inputSection))
                .findAny()
                .get();
        List<Section> sections = savedSection.updateSection(inputSection);
        int index = this.sections.indexOf(savedSection);
        this.sections.remove(savedSection);
        this.sections.addAll(index, sections);

        // 외부 구간 추가

    }

    private boolean isNotExistsUpAndDownStation(Section section) {
        return sections.stream()
                .allMatch(savedSection -> savedSection.isNotContainUnAndDownStation(section));
    }

}
