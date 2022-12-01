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
        Station upStation = stationRepository.findById(lineCreateRequest.getUpStationId())
                .orElseThrow(IllegalArgumentException::new);
        Station downStation = stationRepository.findById(lineCreateRequest.getDownStationId())
                .orElseThrow(IllegalArgumentException::new);
        Line line = lineRepository.save(lineCreateRequest.toLine());
        LineStation lineStationUp = lineStationRepository.save(new LineStation(upStation, line));
        LineStation lineStationDown = lineStationRepository.save(new LineStation(downStation, line));
        line.setLineStations(new ArrayList<>(Arrays.asList(lineStationUp, lineStationDown)));
        return LineResponse.of(line);
    }

    public List<LineResponse> findAllLines() {
        return lineRepository.findAll()
                .stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
    }

    public LineResponse findById(long id) {
        return lineRepository.findById(id)
                .map(LineResponse::of)
                .orElseThrow(IllegalArgumentException::new);
    }

    @Transactional
    public void updateLine(long id, LineUpdateRequest lineUpdateRequest) {
        Line line = lineRepository.findById(id)
                .map(found -> lineUpdateRequest.toLine(found.getId()))
                .orElseThrow(IllegalArgumentException::new);
        lineRepository.save(line);
    }

    @Transactional
    public void removeById(long id) {
        Line line = lineRepository.findById(id).orElseThrow(IllegalArgumentException::new);
        lineRepository.deleteById(line.getId());
    }
}
