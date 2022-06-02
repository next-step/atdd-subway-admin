package nextstep.subway.application;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

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

        Station firstStation = stationRepository.findById(request.getFirstStationId())
                                                .orElseThrow(() -> new NoSuchElementException("지하철 역이 존재하지 않습니다"));

        Station lastStation = stationRepository.findById(request.getLastStationId())
                                               .orElseThrow(() -> new NoSuchElementException("지하철 역이 존재하지 않습니다"));

        firstStation.update(newLine.getId());
        lastStation.update(newLine.getId());

        return LineResponse.of(newLine, Arrays.asList(firstStation, lastStation));
    }

    public List<LineResponse> getLines() {
        List<LineResponse> lines = new LinkedList<>();
        List<Station> stations = stationRepository.findAllByLineIdIsNotNull();
        for (Line line : lineRepository.findAll()) {
            lines.add(LineResponse.of(line, stations.stream()
                                                    .filter(station -> station.getLineId() != null
                                                            && station.getLineId().equals(line.getId()))
                                                    .collect(Collectors.toList())));
        }
        return lines;
    }

    public LineResponse getLineById(Long id) {
        Line line = lineRepository.findById(id)
                                  .orElseThrow(() -> new NoSuchElementException("지하철 노선이 존재하지 않습니다"));
        return LineResponse.of(line, stationRepository.findAllByLineId(line.getId()));
    }

    @Transactional
    public void deleteLineById(Long id) {
        lineRepository.deleteById(id);
    }

    @Transactional
    public void updateLineById(Long id, Line param) {
        Line line = lineRepository.findById(id)
                                  .orElseThrow(() -> new NoSuchElementException("지하철 노선이 존재하지 않습니다"));
        line.update(param);
    }
}
