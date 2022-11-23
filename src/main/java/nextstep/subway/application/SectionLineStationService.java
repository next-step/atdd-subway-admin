package nextstep.subway.application;

import nextstep.subway.domain.*;
import nextstep.subway.dto.SectionRequest;
import nextstep.subway.dto.SectionResponse;
import nextstep.subway.dto.SectionsResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class SectionLineStationService {

    private SectionRepository sectionRepository;
    private LineService lineService;
    private StationService stationService;

    public SectionLineStationService(SectionRepository sectionRepository, LineService lineService,
                                     StationService stationService) {
        this.sectionRepository = sectionRepository;
        this.lineService = lineService;
        this.stationService = stationService;
    }

    public SectionsResponse addSection(Long lineId, SectionRequest sectionRequest) {
        Line line = lineService.getLineById(lineId);
        int distance = sectionRequest.getDistance();
        checkDistance(line, distance);

        Station upStation = stationService.getStationById(sectionRequest.getUpStationId());
        Station downStation = stationService.getStationById(sectionRequest.getDownStationId());

        Section newSection = new Section(line, upStation, downStation, distance);
        Station newStation = checkAndGetNewStation(line, upStation, downStation);

        List<Section> sections = new ArrayList<>();
        if(upStation.equals(newStation)) {
            sections.add(addNewUpStationAndGetSection(line, newStation, downStation, distance));
            sections.add(newSection);
        }
        if (downStation.equals(newStation)) {
            sections.add(newSection);
            sections.add(addNewDownStationAndGetSection(line, upStation, newStation, distance));
        }
        sectionRepository.save(newSection);
        return new SectionsResponse(sections.stream().map(SectionResponse::of).collect(Collectors.toList()));
    }

    private void checkDistance(Line line, int distance) {
        if(distance <= 0) {
            throw new IllegalArgumentException("");
        }
        if(line.compareToDistance(distance) <= 0) {
            throw new IllegalArgumentException("");
        }
    }

    private Station checkAndGetNewStation(Line line, Station upStation, Station downStation) {
        boolean isContainUpStation = line.isContainStation(upStation);
        boolean isContainDownStation = line.isContainStation(downStation);
        if(isContainUpStation && isContainDownStation) {
            throw new IllegalArgumentException(ErrorMessage.ALREADY_EXIST_SECTION.getMessage());
        }
        return isContainUpStation ? downStation : upStation;
    }

    private Section addNewUpStationAndGetSection(Line line, Station newStation, Station downStation, int distance) {
        Section section = sectionRepository.findByDownStation(downStation);
        sectionRepository.deleteById(section.getId());
        if(line.getSections().isFirst(downStation)) {
            return sectionRepository.save(new Section(line, null, newStation, 0));
        }
        return sectionRepository.save(new Section(line, section.getUpStation(), newStation,
                section.getDistance() - distance));
    }

    private Section addNewDownStationAndGetSection(Line line, Station upStation, Station newStation, int distance) {
        Section section = sectionRepository.findByUpStation(upStation);
        sectionRepository.deleteById(section.getId());
        if (line.getSections().isLast(upStation)) {
            return sectionRepository.save(new Section(line, newStation, null, 0));
        }
        return sectionRepository.save(new Section(line, newStation, section.getDownStation(),
                section.getDistance() - distance));

    }
}
