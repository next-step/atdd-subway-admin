package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.domain.Section;
import nextstep.subway.line.domain.SectionRepository;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SectionService {
    private final SectionRepository sectionRepository;
    private final LineService lineService;
    private final StationService stationService;

    @Autowired
    public SectionService(SectionRepository sectionRepository, LineService lineService, StationService stationService) {
        this.sectionRepository = sectionRepository;
        this.lineService = lineService;
        this.stationService = stationService;
    }


    public void addSection(Long lineId, SectionRequest sectionRequest) {
        Line line = lineService.find(lineId);
        Station upStation = stationService.findStation(sectionRequest.getUpStationId());
        Station downStation = stationService.findStation(sectionRequest.getDownStationId());
        line.addSection(toSection(line, upStation, downStation, sectionRequest.getDistance()));
    }

    private Section toSection(Line line, Station upStaion, Station downStation, int distance) {
        return Section.builder()
                .line(line)
                .upStation(upStaion)
                .downStation(downStation)
                .distance(distance).build();
    }

    public void removeSection(Long lineId, Long stationId) {
        Line line = lineService.find(lineId);
        Station station = stationService.findStation(stationId);
        sectionRepository.delete(line.removeSection(station));
    }
}
