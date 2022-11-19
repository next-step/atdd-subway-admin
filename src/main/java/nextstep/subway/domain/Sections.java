package nextstep.subway.domain;

import nextstep.subway.exception.ElementNotFoundException;
import nextstep.subway.exception.InvalidParameterException;

import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Embeddable
public class Sections {

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "line_id")
    private List<Section> sections = new ArrayList<>();

    public void add(Section section) {
        this.sections.add(section);
    }

    public void infixSection(Section infixSection) {
        Section needChangedStation = getNeedChangedStationStation(infixSection);

        if (needChangedStation == null) {
            add(infixSection);
            return;
        }
        checkValidationParameter(needChangedStation, infixSection);

        if (needChangedStation.isEqualsId(infixSection.getStation().getId())) {
            infixSection.resetStation(infixSection.getPreStation());
            infixSection.resetPreStation(needChangedStation.getPreStation());
        }
        needChangedStation.resetPreStation(infixSection.getStation());

        add(infixSection);
    }

    public List<Section> getList() {
        return sections;
    }

    public List<Section> getSortList() {
        List<Section> sortList = new ArrayList<>();

        Section upStation = getUpLineStation();
        sortList.add(upStation);

        for (int i = 1; i < sections.size(); i++) {
            Section finalUpStation = upStation;

            Section station = sections
                    .stream()
                    .filter(lineStation
                            -> lineStation.getPreStation() != null
                            && finalUpStation.isEqualsId(lineStation.getPreStation().getId()))
                    .findAny()
                    .orElseThrow(() -> new ElementNotFoundException());

            upStation = station;
            sortList.add(station);
        }

        return sortList;
    }

    private Section getNextStationByStationId(Long id) {
        return sections.stream()
                .filter(lineStation -> lineStation.getPreStation() != null)
                .filter(lineStation -> lineStation.getPreStation().getId().equals(id))
                .findAny().orElse(null);
    }

    private Section getUpLineStation() {
        return sections.stream()
                .filter(lineStations -> lineStations.getPreStation() == null)
                .findAny()
                .orElseThrow(() -> new ElementNotFoundException());
    }

    private Section getNeedChangedStationStation(Section infixSection) {
        Section existedStation = sections
                .stream()
                .filter(lineStation -> lineStation.isEqualsId(infixSection.getStation().getId()))
                .findAny()
                .orElse(sections
                        .stream()
                        .filter(lineStation -> lineStation.isEqualsId(infixSection.getPreStation().getId()))
                        .findAny()
                        .orElse(null));

        if (existedStation.isEqualsId(infixSection.getPreStation().getId())) {
            existedStation = getNextStationByStationId(existedStation.getStation().getId());
        }
        return existedStation;
    }

    private void checkValidationParameter(Section existedStation, Section infixSection) {
        if (existedStation.isEqualsId(infixSection.getPreStation().getId())) {
            throw new InvalidParameterException("상행선과 하행선을 모두 동일하게 등록할 수 없습니다.");
        }

        if (existedStation.isLessThanDistance(infixSection)) {
            throw new InvalidParameterException("기존의 역 사이보다 더 긴 길이의 역을 등록할 수 없습니다.");
        }
    }

}
