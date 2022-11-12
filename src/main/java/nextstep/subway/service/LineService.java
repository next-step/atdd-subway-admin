package nextstep.subway.service;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class LineService {

    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    public LineResponse create(LineRequest request) {
        // TODO: 라인 생성
        Station upStation = stationRepository.findById(request.getUpStationId()).orElseThrow(() -> new IllegalArgumentException());
        Station downStation = stationRepository.findById(request.getDownStationId()).orElseThrow(() -> new IllegalArgumentException());
        Line line = new Line(request.getName(), request.getColor(), upStation, downStation, request.getDistance());
        lineRepository.save(line);
        return LineResponse.of(line);
    }

    public List<LineResponse> findAllLines() {
        List<Line> stations = lineRepository.findAll();

        return stations.stream()
                .map(station -> LineResponse.of(station))
                .collect(Collectors.toList());
    }


    public LineResponse findLine(Long id) {
        Line line = lineRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 지하철노선 입니다."));
        return LineResponse.of(line);
    }

    public LineResponse updateLine(Long id, LineRequest request) {
        Line line = lineRepository.findById(id).orElseThrow(() -> new IllegalArgumentException());
        line.update(request.toLine());
        return LineResponse.of(line);
    }
}
