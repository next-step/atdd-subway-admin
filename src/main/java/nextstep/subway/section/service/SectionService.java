package nextstep.subway.section.service;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.SectionRepository;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SectionService {
    private final StationService stationService;
    private final SectionRepository sectionRepository;

    public SectionService(StationService stationService, SectionRepository sectionRepository) {
        this.stationService = stationService;
        this.sectionRepository = sectionRepository;
    }

    public Section saveSection(long upStationId, long downStationId, int distance) {
        Station upStation = stationService.findStation(upStationId);
        Station downStation = stationService.findStation(downStationId);
        return sectionRepository.save(Section.of(upStation, downStation, distance));

    }

    public void saveSection(Line line, SectionRequest sectionRequest) {
        Station upStation = stationService.findStation(sectionRequest.getUpStationId());
        Station downStation = stationService.findStation(sectionRequest.getDownStationId());
        Section section = Section.of(upStation, downStation, sectionRequest.getDistance());
        line.addAdditionalSection(section);
        sectionRepository.save(section);
    }
}
