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
            throw new IllegalArgumentException();
        }
        Optional<Station> upStationOptional = stationRepository.findById(lineCreateRequest.getUpStationId());
        Optional<Station> downStationOptional = stationRepository.findById(lineCreateRequest.getDownStationId());
        if (!upStationOptional.isPresent() || !downStationOptional.isPresent()) {
            throw new IllegalArgumentException();
        }
        Line line = lineRepository.save(
                new Line(null, lineCreateRequest.getName(), lineCreateRequest.getColor(),
                        lineCreateRequest.getDistance()));
        Station upStation = upStationOptional.get();
        LineStation lineStationUp = lineStationRepository.save(new LineStation(upStation, line));
        Station downStation = downStationOptional.get();
        LineStation lineStationDown = lineStationRepository.save(new LineStation(downStation, line));
        line.setLineStations(new ArrayList<>(Arrays.asList(lineStationUp, lineStationDown)));
        line = lineRepository.save(line);
        return new LineResponse(line);
    }

    public List<LineResponse> findAllLines() {
        return lineRepository.findAll()
                .stream()
                .map(LineResponse::new)
                .collect(Collectors.toList());
    }

    public LineResponse findById(long lineId) {
        Optional<Line> lineOptional = lineRepository.findById(lineId);
        if (!lineOptional.isPresent()) {
            throw new IllegalArgumentException();
        }
        return new LineResponse(lineOptional.get());
    }

    @Transactional
    public void updateLine(long lineId, LineUpdateRequest lineUpdateRequest) {
        Optional<Line> lineOptional = lineRepository.findById(lineId);
        if (!lineOptional.isPresent()) {
            throw new IllegalArgumentException();
        }
        Line foundLine = lineOptional.get();
        Line saveLine = new Line(foundLine.getId(), lineUpdateRequest.getName(), lineUpdateRequest.getColor());
        lineRepository.save(saveLine);
    }

    @Transactional
    public void removeById(long lineId) {
        lineRepository.deleteById(lineId);
    }
}
