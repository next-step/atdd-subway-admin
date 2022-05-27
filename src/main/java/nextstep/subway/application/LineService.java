package nextstep.subway.application;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class LineService {
    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    @Transactional
    public LineResponse createLine(LineRequest request) {
        Line newLine = lineRepository.save(request.getLine());

        Station firstStation = stationRepository.save(request.getFirstStation());
        Station lastStation = stationRepository.save(request.getLastStation());

        return LineResponse.of(newLine, Arrays.asList(firstStation, lastStation));
    }

    public List<LineResponse> getLines() {
        List<LineResponse> lines = new LinkedList<>();
        for (Line line : lineRepository.findAll()) {
            lines.add(LineResponse.of(line, toStations()));
        }
        return lines;
    }

    public LineResponse getLine(Long id) {
        Optional<Line> line = lineRepository.findById(id);
        if (!line.isPresent()) {
            throw new NoSuchElementException("지하철 노선이 존재하지 않습니다");
        }
        return LineResponse.of(line.get(), toStations());
    }

    private static Long id = 1L;

    private List<Station> toStations() {
        return Arrays.asList(new Station(id, "지하철역_" +  id++), new Station(id, "새로운지하철역_" +  id++));
    }

    @Transactional
    public void deleteLineById(Long id) {
        lineRepository.deleteById(id);
    }
}
