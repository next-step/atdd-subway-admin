package nextstep.subway.line.application;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.exception.LineNotFoundException;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationSearchRequest;

@Service
@Transactional
public class LineService {
    private final LineRepository lineRepository;
    private final StationService stationService;

    public LineService(final LineRepository lineRepository, final StationService stationService) {
        this.lineRepository = lineRepository;
        this.stationService = stationService;
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findAll() {
        return lineRepository.findAll().stream()
            .map(LineResponse::of)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LineResponse findOne(final Long id) {
        return LineResponse.of(findById(id));
    }

    public LineResponse saveLine(LineRequest request) {

        Map<Long, Station> stations = stationService
            .findAllById(new StationSearchRequest(request.getUpStationId(), request.getDownStationId()))
            .stream()
            .collect(Collectors.toMap(Station::getId, Function.identity()));

        Station upStation = stations.get(request.getUpStationId());
        Station downStation = stations.get(request.getDownStationId());

        Line line = lineRepository.save(request.toLine(upStation, downStation));

        return LineResponse.of(line);
    }

    public LineResponse updateLine(final Long id, final LineRequest lineRequest) {
        Line line = findById(id);

        line.update(lineRequest.toLine());

        return LineResponse.of(line);
    }

    public void deleteLine(final Long id) {
        lineRepository.deleteById(id);
    }

    private Line findById(final Long id) {
        return lineRepository.findById(id)
            .orElseThrow(() ->
                new LineNotFoundException(String.format("[id=%d] 요청한 지하철 노선 정보가 없습니다.", id)));
    }

}
