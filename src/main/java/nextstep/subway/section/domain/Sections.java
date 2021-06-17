package nextstep.subway.section.domain;

import nextstep.subway.common.exceptionAdvice.exception.RemoveSectionException;
import nextstep.subway.common.exceptionAdvice.exception.StationNotFoundException;
import nextstep.subway.common.exceptionAdvice.exception.StationOfLineNotFoundException;
import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<Section> sections;

    public Sections() {
        this.sections = new ArrayList<>();
    }

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public void addSection(Section newSection) {
        if (sections.size() != 0) {
            checkSectionValidation(newSection);
        }
        this.sections.add(newSection);
    }

    private void checkSectionValidation(Section newSection) {
        int sectionIdx = 0;
        Section originSection = sections.get(sectionIdx);
        if (isUpFinalSection(originSection, newSection)) return;
        boolean createUpSection = false;
        boolean createDownSection = false;
        while (!createUpSection && !createDownSection && sectionIdx < sections.size()) {
            originSection = sections.get(sectionIdx);
            createUpSection = checkUpSectionValidation(originSection, newSection);
            createDownSection = checkDownSectionValidation(originSection, newSection);
            sectionIdx++;
        }
        if (createUpSection || createDownSection) return;
        if (isDownFinalSection(originSection, newSection)) return;
        throw new IllegalArgumentException("상행역과 하행역 둘 중 하나라도 기존 구간에 존재해야 합니다.");
    }

    private boolean isUpFinalSection(Section originSection, Section newSection) {
        return originSection.isUpFinalSection(newSection);
    }

    private boolean isDownFinalSection(Section originSection, Section newSection) {
        return originSection.isDownFinalSection(newSection);
    }

    private boolean checkUpSectionValidation(Section originSection, Section newSection) {
        originSection.checkSameUpStationAndDownStation(newSection);
        if (originSection.isUpSection(newSection)) {
            return true;
        }
        return false;
    }

    private boolean checkDownSectionValidation(Section originSection, Section newSection) {
        if (originSection.isDownSection(newSection)) {
            return true;
        }
        return false;
    }

    public List<Section> getSection() {
        return this.sections;
    }

    public List<Station> getStationList() {
        List<Station> stationList = new ArrayList<>();
        Section upFinalSection = getUpFinalStation();
        stationList.add(upFinalSection.getUpStation());
        Station nextStation;
        Section nextSection = upFinalSection;
        while (nextSection != null) {
            nextStation = nextSection.getDownStation();
            stationList.add(nextStation);
            nextSection = getNextStation(nextStation);
        }
        return stationList;
    }

    private Section getUpFinalStation() {
        return sections.stream()
                .filter(it -> getDownStationOfUpStation(it.getUpStation()) == null)
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

    private Section getDownStationOfUpStation(Station upStation) {
        return sections.stream()
                .filter(it -> it.isSameDownStation(upStation))
                .findFirst()
                .orElse(null);
    }

    private Section getNextStation(Station downStation) {
        return sections.stream()
                .filter(it -> it.isSameUpStation(downStation))
                .findFirst()
                .orElse(null);
    }

    public void removeStation(Station removeStation) {
        isRemoveStationValidate(removeStation);
        Section upSection = getDownStationOfUpStation(removeStation);
        Section downSection = getNextStation(removeStation);
        if (upSection == null) {
            sections.remove(downSection);
            return;
        }
        if (downSection == null) {
            sections.remove(upSection);
            return;
        }
        upSection.changeSectionAndDistance(downSection);
        sections.remove(downSection);
    }

    private void isRemoveStationValidate(Station removeStation) {
        if (sections.size() < 2) {
            throw new RemoveSectionException();
        }
        if (hasRemoveStation(removeStation)) {
            throw new StationOfLineNotFoundException(removeStation.getId());
        }
    }

    private boolean hasRemoveStation(Station removeStation) {
        return sections.stream()
                .noneMatch(section -> section.isContains(removeStation));
    }
}
