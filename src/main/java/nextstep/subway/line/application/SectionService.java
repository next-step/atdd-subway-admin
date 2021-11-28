package nextstep.subway.line.application;

import static java.util.Arrays.*;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import nextstep.subway.common.exception.NoResultDataException;
import nextstep.subway.line.domain.Distance;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.domain.Section;
import nextstep.subway.line.domain.SectionType;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;

@Service
@Transactional
public class SectionService {

    private LineRepository lineRepository;
    private StationService stationService;

    public SectionService(StationService stationService, LineRepository lineRepository) {
        this.stationService = stationService;
        this.lineRepository = lineRepository;
    }

    public void addSection(Long lineId, SectionRequest newSectionRequest) {
        Line line = lineRepository.findById(lineId).orElseThrow(NoResultDataException::new);
        line.addSection(createSection(newSectionRequest, SectionType.MIDDLE));
    }

    public List<Section> createSection(SectionRequest sectionRequest) {
        Station station = stationService.findByIdThrow(sectionRequest.getUpStationId());
        Station downStation = stationService.findByIdThrow(sectionRequest.getDownStationId());
        Section firstStation = new Section(station, downStation, SectionType.FIRST, new Distance(sectionRequest.getDistance()));
        Section lastStation = new Section(downStation, null, SectionType.LAST, new Distance(0));
        return asList(firstStation, lastStation);
    }

    public Section createSection(SectionRequest newSectionRequest, SectionType sectionType) {
        Station station = stationService.findByIdThrow(newSectionRequest.getUpStationId());
        Station downStation = stationService.findByIdThrow(newSectionRequest.getDownStationId());
        return new Section(station, downStation, sectionType, new Distance(newSectionRequest.getDistance()));
    }
}
