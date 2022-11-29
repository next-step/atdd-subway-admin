package nextstep.subway.application;

import nextstep.subway.domain.*;
import nextstep.subway.dto.LineCreateRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.LineUpdateRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class LineService {
    private final LineRepository lineRepository;
    private final LineStationRepository lineStationRepository;
    private final StationRepository stationRepository;

    public LineService(LineRepository lineRepository, LineStationRepository lineStationRepository,
                       StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.lineStationRepository = lineStationRepository;
        this.stationRepository = stationRepository;
    }

    @Transactional
    public LineResponse saveLine(LineCreateRequest lineCreateRequest) {
        Optional<Line> lineOptional = lineRepository.findByName(lineCreateRequest.getName());
        if (lineOptional.isPresent()) {
            return null;
        }
        Station upStation = stationRepository.findById(lineCreateRequest.getUpStationId())
                .orElseThrow(IllegalArgumentException::new);
        Station downStation = stationRepository.findById(lineCreateRequest.getDownStationId())
                .orElseThrow(IllegalArgumentException::new);
        Line line = lineRepository.save(
                new Line(null, lineCreateRequest.getName(), lineCreateRequest.getColor(),
                        lineCreateRequest.getDistance()));
        LineStation lineStationUp = lineStationRepository.save(new LineStation(upStation, line));
        LineStation lineStationDown = lineStationRepository.save(new LineStation(downStation, line));
        line.setLineStations(new ArrayList<>(Arrays.asList(lineStationUp, lineStationDown)));
        return new LineResponse(line);
    }

    public List<LineResponse> findAllLines() {
        return lineRepository.findAll()
                .stream()
                .map(LineResponse::new)
                .collect(Collectors.toList());
    }

    public LineResponse findById(long id) {
        return lineRepository.findById(id)
                .map(LineResponse::new)
                .orElseThrow(IllegalArgumentException::new);
    }

    @Transactional
    public Line updateLine(long id, LineUpdateRequest lineUpdateRequest) {
        Optional<Line> lineOptional = lineRepository.findByName(lineUpdateRequest.getName());
        if (lineOptional.isPresent()) {
            return null;
        }
        Line line = new Line(id, lineUpdateRequest.getName(), lineUpdateRequest.getColor());
        return lineRepository.save(line);
    }

    @Transactional
    public void removeById(long id) {
        Optional<Line> lineOptional = lineRepository.findById(id);
        if (!lineOptional.isPresent()) {
            return;
        }
        lineOptional.get()
                .getLineStations().stream()
                .map(LineStation::getId)
                .forEach(lineStationRepository::deleteById);
        lineRepository.deleteById(id);
    }
}
