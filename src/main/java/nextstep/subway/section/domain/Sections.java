package nextstep.subway.section.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

import nextstep.subway.exception.section.InvalidDistanceException;
import nextstep.subway.exception.section.NotFoundSectionException;
import nextstep.subway.exception.station.StationsAlreadyExistException;
import nextstep.subway.exception.station.StationsNoExistException;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationResponse;

@Embeddable
public class Sections {

    private static final String ALREADY_EXIST = "이미 구간이 등록된 역입니다.";
    private static final String NO_EXIST = "구간에 없는 역들입니다.";
    private static final String OVER_DISTANCE = "기존 구간의 거리보다 작아야 합니다.";

    @OneToMany(mappedBy = "line", cascade = {CascadeType.ALL})
    private List<Section> sections = new ArrayList<Section>();

    public Sections() {}

    private Sections(List<Section> sections) {
        this.sections = sections;
    }

    public static Sections of(List<Section> sections) {
        return new Sections(sections);
    }

    public int getDistanceWithStations(Station upStation, Station downStation) {
        return sections.stream()
            .filter(seciton -> seciton.sameUpStaion(upStation) && seciton.sameDownStaion(downStation)).findFirst()
            .orElseThrow(NotFoundSectionException::new).getDistance();
    }

    public List<StationResponse> toResponseList() {
        List<StationResponse> list = new ArrayList<>();

        if (sections == null) {
            return list;
        }

        Section nextSection = findStartStation();
        list.add(nextSection.upStationToReponse());
        while (nextSection != null) {
            list.add(nextSection.downStationToReponse());
            nextSection = findEndStatoin(nextSection);
        }

        return list;
    }

    private Section findEndStatoin(Section nextSection) {
        return sections.stream().filter(section -> section.isStartStation(nextSection))
            .findFirst().orElse(null);
    }

    private Section findStartStation() {
        return sections.stream().filter(section -> findUpStation(section) == null)
            .findFirst().orElseThrow(NotFoundSectionException::new);
    }

    private Section findUpStation(Section findSection) {
        return sections.stream()
            .filter(section -> section.isEndStation(findSection))
            .findFirst()
            .orElse(null);
    }

    public void add(Section section) {
        validateAlreadyOrNoExist(section);
        updateIfUpStaionMatch(section);
        updateIfDownStationMatch(section);
        sections.add(section);
    }

    private void updateIfDownStationMatch(Section compareSection) {
        sections.stream()
            .filter(compareSection::sameDownStaion)
            .findFirst()
            .ifPresent(findedSection -> {
                validateDistance(findedSection, compareSection);
                findedSection.updateDownStation(compareSection);
                findedSection.updateDistance(compareSection);
            });

    }

    private void updateIfUpStaionMatch(Section compareSection) {
        sections.stream()
            .filter(compareSection::sameUpStaion)
            .findFirst()
            .ifPresent(findedSection -> {
                validateDistance(findedSection, compareSection);
                findedSection.updateUpStation(compareSection);
                findedSection.updateDistance(compareSection);
            });
    }

    private void validateDistance(Section findSection, Section compareSection) {
        if (findSection.isOverDistance(compareSection)) {
            throw new InvalidDistanceException(OVER_DISTANCE);
        }
    }

    private void validateAlreadyOrNoExist(Section compareSection) {
        boolean hasUpStation = sections.stream().anyMatch(compareSection::hasUpStation);
        boolean hasDownStation = sections.stream().anyMatch(compareSection::hasDownStation);

        if (!hasUpStation && !hasDownStation) {
            throw new StationsNoExistException(NO_EXIST);
        }
        if (hasUpStation && hasDownStation) {
            throw new StationsAlreadyExistException(ALREADY_EXIST);
        }
    }

}
