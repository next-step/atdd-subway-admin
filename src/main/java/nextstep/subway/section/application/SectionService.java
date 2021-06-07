package nextstep.subway.section.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.station.domain.StationRepository;
import org.springframework.stereotype.Service;

// TODO 제거 예정
@Service
public class SectionService {
    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    public SectionService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    public Line addSection(Long lineId, SectionRequest request) {
//        final Line line = lineRepository.findById(lineId).orElseThrow(() -> new DataIntegrityViolationException("Not Found lineId" + lineId));
//        final Station downStation = stationRepository.findById(request.getDownStationId()).orElseThrow(() -> new DataIntegrityViolationException("Not Found stationId" + request.getDownStationId()));
//        final Station upStation = stationRepository.findById(request.getUpStationId()).orElseThrow(() -> new DataIntegrityViolationException("Not Found stationId" + request.getUpStationId()));
//
//        final Section section = new Section(upStation, downStation, request.getDistance());
//        line.addSection(section);

        return null;
    }
}
