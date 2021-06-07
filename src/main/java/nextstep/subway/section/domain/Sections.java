package nextstep.subway.section.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import nextstep.subway.section.dto.SectionResponse;
import nextstep.subway.station.domain.Station;

@Embeddable
public class Sections {

    public static final String AT_LEAST_ONE_SECTION_IS_REQUIRED = "1개 이상의 구간이 입력되어야 합니다.";

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "line", cascade = CascadeType.ALL)
    private List<Section> sections;

    public Sections() {
        sections = new ArrayList<>();
    }

    public Sections(List<Section> sections) {
        if (sections == null && sections.size() == 0) {
            throw new IllegalArgumentException(AT_LEAST_ONE_SECTION_IS_REQUIRED);
        }
        initSortSections(sections);
    }

    public List<Station> findSortedStations() {
        List<Station> stations = sections.stream()
            .map(Section::getUpStation)
            .collect(Collectors.toList());
        if (sections.size() > 0) {
            stations.add(getLast().getDownStation());
        }
        return stations;
    }

    public void add(Section section) {
        addInOrder(section);
    }

    public List<SectionResponse> toSectionResponses() {
        return sections.stream()
            .map(SectionResponse::of)
            .collect(Collectors.toList());
    }


    private void initSortSections(List<Section> sections) {
        this.sections = new ArrayList<>();
        for (Section section : sections) {
            addInOrder(section);
        }
    }

    private void addInOrder(Section section) {
        if (isBefore(section)) {
            addFirst(section);
        }
        if (isAfter(section)) {
            addLast(section);
        }
    }

    private void addFirst(Section section) {
        sections.add(0, section);
    }

    private void addLast(Section section) {
        sections.add(sections.size(), section);
    }

    private boolean isBefore(Section findSection) {
        if (sections.size() == 0) {
            return true;
        }
        return sections.stream()
            .filter(section -> section.isBefore(findSection))
            .count() > 0;
    }

    private boolean isAfter(Section findSection) {
        if (sections.size() == 0) {
            return true;
        }
        return sections.stream()
            .filter(section -> section.isAfter(findSection))
            .count() > 0;
    }

    private Section getLast() {
        return sections.get(sections.size() - 1);
    }
}
