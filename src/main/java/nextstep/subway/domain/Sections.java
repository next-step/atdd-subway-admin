package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", cascade=CascadeType.ALL)
    private final List<Section> sections = new ArrayList<>();
    public Sections() {
    }

    public List<Section> getSections() {
        return sections;
    }

    public void addFisrtSection(Section section) {
        if (sections.size() == 0) {
            sections.add(section);
        }
    }

    public void addSection(Section section) {
        checkValidation(section);

        AddSectionType addType = determineAddSection(section);

        if (addType == AddSectionType.NEW_UP_STATION) {
            setNewUptation(section);
        }

        if (addType == AddSectionType.NEW_DOWN_STATION) {
            setDownStation(section);
        }

        if (addType == AddSectionType.NEW_STATION) {
            insertStation(section);
        }
    }

    private void checkValidation(Section section) {
        Optional<Section> registerdUpStation = this.sections.stream().filter(s -> s.getUpStation() == section.getUpStation()).findAny();
        Optional<Section> registedDownStation = this.sections.stream().filter(s -> s.getDownStation() == section.getDownStation()).findAny();

        Station firstStation = sections.get(0).getUpStation();
        Station lastStation = sections.get(Math.max((sections.size() - 1), 0)).getDownStation();

        if (registerdUpStation.isPresent() && registedDownStation.isPresent()) {
            throw new IllegalArgumentException("이미 등록된 구간입니다.");
        }

        if (!registerdUpStation.isPresent() &&
                !registedDownStation.isPresent() &&
                section.getDownStation() != firstStation &&
                section.getUpStation() != lastStation
        ) {
            throw new IllegalArgumentException("상행역과 하행역 모두 찾을 수 없습니다.");
        }
     }


    private void checkDistanceForNewStation(Section newSection, Section originSection) {
        if (originSection.getDistance() <= newSection.getDistance()) {
            throw new IllegalArgumentException("기존 역 사이 길이보다 크거나 같으면 새로운 구간을 등록할 수 없습니다.");
        }
    }

    private AddSectionType determineAddSection(Section section) {
        Station upStreamStation = sections.get(0).getUpStation();
        Station downStreamStation = sections.get(Math.max(sections.size() - 1, 0)).getDownStation();

        if (upStreamStation == section.getDownStation()) {
            return AddSectionType.NEW_UP_STATION;
        }

        if (downStreamStation == section.getUpStation()) {
            return AddSectionType.NEW_DOWN_STATION;
        }

        return AddSectionType.NEW_STATION;
    }

    private void setNewUptation(Section section) {
        int firstIndex = 0;
        this.sections.add(firstIndex, section);
    }

    private void setDownStation(Section section) {
        this.sections.add(section);
    }

    private void insertStation(Section section) {
        Section originSection = sections.stream().filter(s ->
          s.getUpStation() == section.getUpStation()
        ).findFirst().orElseThrow(() -> new IllegalArgumentException("구간을 등록하기 위한 역을 찾을 수 없습니다."));

        checkDistanceForNewStation(section, originSection);

        int originalSectionIndex = this.sections.indexOf(originSection);
        originSection.updateUpStationAndDistance(section.getUpStation(), originSection.getDistance() - section.getDistance());
        sections.set(originalSectionIndex,originSection);

        int addSectionIndex = Math.max((originalSectionIndex - 1), 0);
        sections.add(addSectionIndex , section);
    }
}
