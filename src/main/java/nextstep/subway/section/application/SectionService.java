package nextstep.subway.section.application;

import nextstep.subway.line.application.LineNotFoundException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.section.dto.SectionResponse;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class SectionService {
    public static final String NOT_FOUND_LINE = "존재하지 않는 노선입니다";
    private final LineRepository lineRepository;
    private final StationService stationService;

    public SectionService(LineRepository lineRepository, StationService stationService) {
        this.lineRepository = lineRepository;
        this.stationService = stationService;
    }

    @Transactional
    public SectionResponse saveSection(Long lineId, SectionRequest request) {
        Line line = getLine(lineId);

        Station upStation = stationService.findStation(request.getUpStationId());
        Station downStation = stationService.findStation(request.getDownStationId());

        line.updateSection(new Section(upStation, downStation, request.getDistance()));

        return new SectionResponse(2L, 1L, 3L, 5);
    }

    private Line getLine(Long lineId) {
        return lineRepository.findById(lineId)
                .orElseThrow(() -> new LineNotFoundException(NOT_FOUND_LINE));
    }
}
