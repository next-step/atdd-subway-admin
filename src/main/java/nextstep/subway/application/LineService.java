package nextstep.subway.application;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class LineService {
    private LineRepository lineRepository;

    private StationRepository stationRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    @Transactional
    public LineResponse saveLine(LineRequest lineRequest) {
        Line persistLine = lineRepository.save(lineRequest.toLine());
        LineResponse lineResponse = findStationInLine(persistLine);
        return lineResponse;
    }

    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();

        return lines.stream()
                .map(line -> findStationInLine(line))
                .collect(Collectors.toList());
    }

    public LineResponse findLineById(Long id) {
        return findStationInLine(lineRepository.findById(id).get());
    }

    @Transactional
    public void deleteLineById(Long id) {
        lineRepository.deleteById(id);
    }

    @Transactional
    public LineResponse updateLine(Long id, LineRequest lineRequest) {
        Line line = lineRepository.findById(id).get();

        line.setColor(lineRequest.getColor());
        line.setDistance(lineRequest.getDistance());
        line.setUpStationId(lineRequest.getUpStationId());
        line.setDownStationId(lineRequest.getDownStationId());

        Line persistLine = lineRepository.save(line);

        return findStationInLine(persistLine);
    }

    private LineResponse findStationInLine(Line line) {
        List<Station> stations = new ArrayList<>();
        stations.add(stationRepository.findById(line.getUpStationId()).get());
        stations.add(stationRepository.findById(line.getDownStationId()).get());
        return LineResponse.of(line).setStations(stations);
    }
}
