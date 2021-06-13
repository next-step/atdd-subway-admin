package nextstep.subway.section.application;

import nextstep.subway.line.application.LineNotFoundException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class SectionService {
    private static final String NOT_EXIST_LINE = "존재하지 않는 노선: ";
    private final LineRepository lineRepository;
    private final StationService stationService;

    public SectionService(LineRepository lineRepository, StationService stationService) {
        this.lineRepository = lineRepository;
        this.stationService = stationService;
    }

    @Transactional
    public void saveSection(Long lineId, SectionRequest request) {
        Line line = getLine(lineId);
        Station upStation = stationService.findStation(request.getUpStationId());
        Station downStation = stationService.findStation(request.getDownStationId());
        line.addSection(new Section(upStation, downStation, request.getDistance()));
    }

    @Transactional
    public void deleteSection(Long lineId, Long stationId) {
        final Line line = lineRepository.findById(lineId)
                .orElseThrow(() -> new LineNotFoundException(NOT_EXIST_LINE + lineId));
        line.deleteStation(stationId);
    }

    private Line getLine(Long lineId) {
        return lineRepository.findById(lineId)
                .orElseThrow(() -> new LineNotFoundException(NOT_EXIST_LINE + lineId));
    }
}
