package nextstep.subway.section.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

import org.springframework.util.CollectionUtils;

import nextstep.subway.exception.section.InvalidDistanceException;
import nextstep.subway.exception.section.NotFoundSectionException;
import nextstep.subway.exception.section.NotPossibleRemoveException;
import nextstep.subway.exception.station.StationsAlreadyExistException;
import nextstep.subway.exception.station.StationsNoExistException;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationResponse;

@Embeddable
public class Sections {

    private static final String ALREADY_EXIST = "이미 구간이 등록된 역입니다.";
    private static final String NO_EXIST = "구간에 없는 역들입니다.";
    private static final String OVER_DISTANCE = "기존 구간의 거리보다 작아야 합니다.";
    private static final String CHECK_SIZE = "구간이 1개일 경우 삭제가 불가능합니다.";
    private static final String EMPTY_SECION = "구간이 존재하지 않습니다.";

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
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

        Section nextSection = findStartSection();
        list.add(nextSection.upStationToReponse());
        while (nextSection != null) {
            list.add(nextSection.downStationToReponse());
            nextSection = findNextSection(nextSection);
        }

        return list;
    }

    public void add(Section section) {
        validateAlreadyOrNoExist(section);
        updateIfUpStaionMatch(section);
        updateIfDownStationMatch(section);
        sections.add(section);
    }

    public void remove(Station station) {
        validationRemove(station);
        Section beforeSection = findDownStationSection(station);
        Section afterSection = findUpStationSection(station);
        Long lastStationId = findLastSection().downStationId();
        Long firstStaionId = findStartSection().upStaionId();

        if (station.getId().equals(lastStationId)) {
            sections.remove(beforeSection);
            return;
        }

        if (station.getId().equals(firstStaionId)) {
            sections.remove(afterSection);
            return;
        }

        beforeSection.updateDownStationToDown(afterSection);
        beforeSection.connectDistance(afterSection);
        sections.remove(afterSection);
    }

    private Section findDownStationSection(Station station) {
        return sections.stream().filter(section -> section.sameDownStaion(station)).findFirst().orElse(Section.EMPTY);
    }

    private Section findUpStationSection(Station station) {
        return sections.stream().filter(section -> section.sameUpStaion(station)).findFirst().orElse(Section.EMPTY);
    }

    private void validationRemove(Station station) {
        List<Long> stationIdList = getStationIdList();

        if (CollectionUtils.isEmpty(sections) || !stationIdList.contains(station.getId())) {
            throw new NotPossibleRemoveException(EMPTY_SECION);
        }

        if (sections.size() == 1) {
            throw new NotPossibleRemoveException(CHECK_SIZE);
        }

    }

    private List<Long> getStationIdList() {
        return sections.stream()
            .map(section -> Stream.of(section.upStaionId(), section.downStationId())).flatMap(Stream::distinct)
            .collect(Collectors.toList());
    }

    private Section findNextSection(Section beforSection) {
        return sections.stream().filter(section -> section.isUpStationWithDown(beforSection))
            .findFirst().orElse(null);
    }

    private Section findStartSection() {
        return sections.stream().filter(section -> findSectionIsAnotherDownStation(section) == null)
            .findFirst().orElseThrow(NotFoundSectionException::new);
    }

    private Section findLastSection() {
        return sections.stream().filter(section -> findSectionIsAnotherUpStation(section) == null)
            .findFirst().orElseThrow(NotFoundSectionException::new);
    }

    private Section findSectionIsAnotherUpStation(Section beforeSection) {
        return sections.stream()
            .filter(section -> section.isUpStationWithDown(beforeSection))
            .findFirst()
            .orElse(null);
    }

    private Section findSectionIsAnotherDownStation(Section beforeSection) {
        return sections.stream()
            .filter(section -> section.isDownStationWithUp(beforeSection))
            .findFirst()
            .orElse(null);
    }

    private void updateIfDownStationMatch(Section compareSection) {
        sections.stream()
            .filter(compareSection::sameDownStaion)
            .findFirst()
            .ifPresent(findedSection -> {
                validateDistance(findedSection, compareSection);
                findedSection.updateDownStation(compareSection);
                findedSection.updateMinusDistance(compareSection);
            });

    }

    private void updateIfUpStaionMatch(Section compareSection) {
        sections.stream()
            .filter(compareSection::sameUpStaion)
            .findFirst()
            .ifPresent(findedSection -> {
                validateDistance(findedSection, compareSection);
                findedSection.updateUpStation(compareSection);
                findedSection.updateMinusDistance(compareSection);
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
