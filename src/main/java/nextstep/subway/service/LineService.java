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
        Line line = findById(id);
        return LineResponse.of(line);
    }

    @Transactional
    public void updateLine(Long id, LineRequest request) {
        Line line = findById(id);
        line.update(request.toLine());
    }

    @Transactional
    public void deleteLine(Long id) {
        Line line = findById(id);
        lineRepository.delete(line);
    }

    private Line findById(Long id) {
        return lineRepository.findById(id).orElseThrow(() -> new IllegalArgumentException());
    }

}
