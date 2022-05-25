package nextstep.subway.application;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.dto.LineCreateRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.LineUpdateRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class LineService {
    private final LineRepository lineRepository;

    private final StationRepository stationRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    @Transactional
    public LineResponse saveLine(LineCreateRequest lineCreateRequest) {
        Line persistLine = lineRepository.save(lineCreateRequest.toLine());
        addStations(persistLine, lineCreateRequest);
        return LineResponse.of(persistLine);
    }

    public LineResponse findLine(Long id) {
        Optional<Line> line = lineRepository.findById(id);
        return LineResponse.of(line.get());
    }

    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();

        return lines.stream()
                .map(line -> LineResponse.of(line))
                .collect(Collectors.toList());
    }

    @Transactional
    public LineResponse updateLine(Long id, LineUpdateRequest lineUpdateRequest) {
        Line line = lineRepository.findById(id).get();
        line.updateLine(lineUpdateRequest);
        return LineResponse.of(line);
    }

    @Transactional
    public void deleteLineById(Long id) {
        Optional<Line> line = lineRepository.findById(id);
        line.get().clearStations();
        lineRepository.deleteById(id);
    }

    private void addStations(Line persistLine, LineCreateRequest lineCreateRequest) {
        addUpLine(persistLine, lineCreateRequest);
        addDownLine(persistLine, lineCreateRequest);
    }

    private void addUpLine(Line line, LineCreateRequest lineCreateRequest) {
        if (lineCreateRequest.hasUpStationsId()) {
            Station station = getStation(lineCreateRequest.getUpStationId());
            line.setUpStation(station);
        }
    }

    private void addDownLine(Line line, LineCreateRequest lineCreateRequest) {
        if (lineCreateRequest.hasDownStationsId()) {
            Station station = getStation(lineCreateRequest.getDownStationId());
            line.setDownStation(station);
        }
    }

    private Station getStation(Long stationId) {
        Optional<Station> station = stationRepository.findById(stationId);
        station.orElseThrow(() -> new NoSuchElementException("id(" + stationId + ")에 해당하는 역이 없습니다."));
        return station.get();
    }
}
