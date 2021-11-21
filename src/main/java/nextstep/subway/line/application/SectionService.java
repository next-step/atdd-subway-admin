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
        Line findLine = lineRepository.findByIdWithSections(lineId).orElseThrow(NotFoundException::new);
        findLine.getSections().addSection(request.toSection(
            findStationById(request.getUpStationId()), findStationById(request.getDownStationId())));
        return LineResponse.of(findLine,
            LineService.convertToStationResponse(findLine.getSections().createStations()));
    }

    private Station findStationById(Long stationId) {
        return stationRepository.findById(stationId)
            .orElseThrow(NotFoundException::new);
    }
}
