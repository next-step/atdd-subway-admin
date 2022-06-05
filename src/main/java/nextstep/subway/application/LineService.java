package nextstep.subway.application;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

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

        Station upStation = stationRepository.findById(request.getUpStationId())
                                             .orElseThrow(() -> new NoSuchElementException("지하철 역이 존재하지 않습니다"));

        Station downStation = stationRepository.findById(request.getDownStationId())
                                               .orElseThrow(() -> new NoSuchElementException("지하철 역이 존재하지 않습니다"));

        upStation.setLine(newLine);
        downStation.setLine(newLine);

        return LineResponse.of(newLine, newLine.getStations());
    }

    public List<LineResponse> getLines() {
        List<LineResponse> lines = new LinkedList<>();
        for (Line line : lineRepository.findAll()) {
            lines.add(LineResponse.of(line, line.getStations()));
        }
        return lines;
    }

    public LineResponse getLineById(Long id) {
        Line line = lineRepository.findById(id)
                                  .orElseThrow(() -> new NoSuchElementException("지하철 노선이 존재하지 않습니다"));
        return LineResponse.of(line, line.getStations());
    }

    @Transactional
    public void deleteLineById(Long id) {
        List<Station> stations = stationRepository.findAllByLineId(id);
        for (Station station : stations) {
            station.setLine(null);
        }
        lineRepository.deleteById(id);
    }

    @Transactional
    public void updateLineById(Long id, Line param) {
        Line line = lineRepository.findById(id)
                                  .orElseThrow(() -> new NoSuchElementException("지하철 노선이 존재하지 않습니다"));
        line.setNameColor(param);
    }
}
