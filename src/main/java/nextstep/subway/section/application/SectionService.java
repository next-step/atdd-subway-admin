package nextstep.subway.section.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.SectionRepository;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import nextstep.subway.station.exception.StationNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SectionService {
    private final StationRepository stationRepository;
    private final SectionRepository sectionRepository;

    public SectionService(StationRepository stationRepository, SectionRepository sectionRepository) {
        this.stationRepository = stationRepository;
        this.sectionRepository = sectionRepository;
    }

    public Section create(Line line, SectionRequest request) {
        Station upStation = stationRepository.findById(request.getUpStationId())
                .orElseThrow(() -> new StationNotFoundException(request.getUpStationId()));
        Station downStation = stationRepository.findById(request.getDownStationId())
                .orElseThrow(() -> new StationNotFoundException(request.getUpStationId()));
        return sectionRepository.save(new Section(line, upStation, downStation, request.getDistance()));
    }
}
