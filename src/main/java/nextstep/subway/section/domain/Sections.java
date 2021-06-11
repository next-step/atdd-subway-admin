package nextstep.subway.section.domain;

import nextstep.subway.section.application.SectionDuplicatedException;
import nextstep.subway.section.application.StationNotRegisterException;
import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Embeddable
public class Sections {
    private static final String SECTION_DUPLICATE = "이미 등록된 구간입니다.";
    private static final String NOT_REGISTERED_STATION = "주어진 역을 포함하지 않아 추가할 수 없습니다.";

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
    private List<Section> sections;

    public Sections() {
        sections = new LinkedList<>();
    }

    public void addSection(Section section) {
        if (sections.isEmpty()) {
            this.sections.add(section);
            return;
        }
        validate(section);
        LinkedList<Section> result = new LinkedList(sections);
        Section first = result.getFirst();
        if (isFirstSection(section, first)) {
            addFirstSection(section, result, first);
            return;
        }
        Section last = result.getLast();
        if (isLastSection(section, last)) {
            addLastSection(section, result, last);
            return;
        }
        addMiddleSection(section, result);
    }

    private void addMiddleSection(Section section, LinkedList<Section> result) {
        int foundIndex = 0;
        for (int i = 0; i < result.size(); i++) {
            if (isUpStationMatch(section, result, i)) {
                result.get(i).changeUpStation(section);
                foundIndex = i;
                break;
            }
            if (isDownStationMatch(section, result, i)) {
                result.get(i).changeDownStation(section);
                foundIndex = i;
                break;
            }
        }
        result.add(foundIndex, section);
        sections = new ArrayList<>(result);
    }

    private boolean isDownStationMatch(Section section, LinkedList<Section> result, int i) {
        return section.getDownStation().equals(result.get(i).getDownStation());
    }

    private boolean isUpStationMatch(Section section, LinkedList<Section> result, int i) {
        return section.getUpStation().equals(result.get(i).getUpStation());
    }

    private void addLastSection(Section section, LinkedList<Section> result, Section last) {
        last.changeDownStation(section);
        result.addLast(section);
        sections = new ArrayList<>(result);
    }

    private void addFirstSection(Section section, LinkedList<Section> result, Section first) {
        first.changeUpStation(section);
        result.addFirst(section);
        sections = new ArrayList<>(result);
    }

    private boolean isLastSection(Section section, Section last) {
        return last.getDownStation().equals(section.getUpStation()) || last.getDownStation().equals(section.getDownStation());
    }

    private boolean isFirstSection(Section section, Section first) {
        return first.getUpStation().equals(section.getUpStation()) || first.getUpStation().equals(section.getDownStation());
    }

    private void validate(Section section) {
        if (checkSectionDuplicated(section)) {
            throw new SectionDuplicatedException(SECTION_DUPLICATE);
        }
        if (checkSectionStationExisted(section)) {
            throw new StationNotRegisterException(NOT_REGISTERED_STATION);
        }
    }

    private boolean checkSectionDuplicated(Section section) {
        return sections.stream()
                .allMatch(section::containsAllStations);
    }

    private boolean checkSectionStationExisted(Section section) {
        return sections.stream()
                .allMatch(section::containsNoneStations);
    }

    public List<Station> getStations() {
        return sections.stream().
                flatMap(Section::getProcessStations)
                .distinct()
                .collect(toList());
    }
}
