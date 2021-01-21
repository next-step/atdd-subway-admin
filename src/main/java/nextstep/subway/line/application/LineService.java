package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional
public class LineService {
    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    public LineResponse saveLine(LineRequest request) {
        Map<Long, Station> stations = stationRepository
                .findAllByIdIn(request.getUpStationId(), request.getDownStationId())
                .collect(Collectors.toMap(Station::getId, Function.identity()));

        Station upStation = stations.get(request.getUpStationId());
        Station downStation = stations.get(request.getDownStationId());

        Line persistLine = lineRepository.save(request.toLine(upStation, downStation));
        return LineResponse.of(persistLine);
    }

    public List<LineResponse> findAll() {
        List<Line> lines = lineRepository.findAll();
        return lines.stream()
                    .map(LineResponse::of)
                    .collect(Collectors.toList());
    }

    public LineResponse findById(Long id) {
        Line line = lineRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("데이터가 존재하지 않습니다."));
        return LineResponse.of(line);
    }

    public LineResponse updateLine(long id, LineRequest lineRequest) {
        Line line = lineRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("데이터가 존재하지 않습니다."));
        line.update(new Line(lineRequest.getName(), lineRequest.getColor()));

        return LineResponse.of(line);
    }

    public void deleteLine(long id) {
        Line line = lineRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("데이터가 존재하지 않습니다."));
        lineRepository.delete(line);
    }

    public void saveSection(long lineId, SectionRequest request) {
        Line line = lineRepository.findById(lineId)
                .orElseThrow(() -> new NoSuchElementException("노선이 존재하지 않습니다."));

        Map<Long, Station> stations = stationRepository
                .findAllByIdIn(request.getUpStationId(), request.getDownStationId())
                .collect(Collectors.toMap(Station::getId, Function.identity()));

        Station upStation = stations.get(request.getUpStationId());
        Station downStation = stations.get(request.getDownStationId());
        line.addSection(upStation, downStation, request.getDistance());
    }
}
