package nextstep.subway.application;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class LineService {

    private final StationService stationService;
    private final LineRepository lineRepository;

    public LineService(StationService stationService, LineRepository lineRepository) {
        this.stationService = stationService;
        this.lineRepository = lineRepository;
    }

    public LineResponse saveLine(LineRequest request) {
        Station upStation = stationService.findStation(request.getUpStationId());
        Station downStation = stationService.findStation(request.getDownStationId());

        Line saveLine = lineRepository.save(
                Line.of(request.getName(), request.getColor(), upStation, downStation, request.getDistance())
        );

        return LineResponse.of(saveLine);
    }

    public List<LineResponse> findAllLines() {
        List<Line> line = lineRepository.findAll();

        return line.stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
    }
}
