package nextstep.subway.application;

import nextstep.subway.domain.*;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.SectionRequest;
import nextstep.subway.dto.UpdateLineRequest;
import nextstep.subway.application.exception.exception.NotFoundDataException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static nextstep.subway.application.exception.type.LineExceptionType.NOT_FOUND_LINE;

@Service
public class LineService {
    private final StationService stationService;
    private final LineRepository lineRepository;

    public LineService(StationService stationService, LineRepository lineRepository) {
        this.stationService = stationService;
        this.lineRepository = lineRepository;
    }

    @Transactional
    public LineResponse saveLine(LineRequest request) {
        Station upStation = stationService.findStation(request.getUpStationId());
        Station downStation = stationService.findStation(request.getDownStationId());
        Line saveLine = lineRepository.save(Line.of(request.getName(), request.getColor(), upStation, downStation, request.getDistance()));

        return LineResponse.of(saveLine);
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findAllLines() {
        List<Line> line = lineRepository.findAll();

        return line.stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LineResponse findLine(Long lineId) {
        Line line = findByLineId(lineId);
        return LineResponse.of(line);
    }

    private Line findByLineId(Long lineId) {
        return lineRepository.findById(lineId)
                .orElseThrow(() -> new NotFoundDataException(NOT_FOUND_LINE.getMessage()));
    }

    @Transactional
    public void updateLine(UpdateLineRequest request, Long lineId) {
        Line line = findByLineId(lineId);
        line.updateNameAndColor(request.getName(), request.getColor());
    }

    @Transactional
    public void deleteLine(Long lineId) {
        lineRepository.deleteById(lineId);
    }

    @Transactional
    public LineResponse addLineStation(Long lineId, SectionRequest sectionRequest) {
        Line line = findByLineId(lineId);

        Station upStation = stationService.findStation(sectionRequest.getUpStationId());
        Station downStation = stationService.findStation(sectionRequest.getDownStationId());
        line.addLineStation(LineStation.of(upStation, downStation, sectionRequest.getDistance()));

        return LineResponse.of(line);
    }
}
