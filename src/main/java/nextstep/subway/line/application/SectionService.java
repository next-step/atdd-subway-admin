package nextstep.subway.line.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import nextstep.subway.common.exception.NotFoundException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.Section;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;

@Service
public class SectionService {
    private final StationRepository stationRepository;

    public SectionService(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    @Transactional
    public void saveSection(SectionRequest request, Line line) {
        Station upStation = findStationById(request.getUpStationId());
        Station downStation = findStationById(request.getDownStationId());
        Section section = request.toSection(upStation, downStation);
        section.changeLine(line);
    }

    private Station findStationById(Long stationId) {
        return stationRepository.findById(stationId)
            .orElseThrow(NotFoundException::new);
    }
}
