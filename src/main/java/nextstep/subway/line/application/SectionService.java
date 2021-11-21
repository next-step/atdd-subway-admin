package nextstep.subway.line.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import nextstep.subway.common.exception.NotFoundException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.domain.Section;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;

@Service
public class SectionService {
    private final StationRepository stationRepository;
    private final LineRepository lineRepository;

    public SectionService(StationRepository stationRepository,
        LineRepository lineRepository) {
        this.stationRepository = stationRepository;
        this.lineRepository = lineRepository;
    }

    @Transactional
    public void saveSection(SectionRequest request, Line line) {
        Station upStation = findStationById(request.getUpStationId());
        Station downStation = findStationById(request.getDownStationId());
        Section section = request.toSection(upStation, downStation);
        section.changeLine(line);
    }

    @Transactional
    public LineResponse addSection(SectionRequest request, Long lineId) {
        return null;
    }

    private Station findStationById(Long stationId) {
        return stationRepository.findById(stationId)
            .orElseThrow(NotFoundException::new);
    }
}
