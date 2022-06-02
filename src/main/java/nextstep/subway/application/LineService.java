package nextstep.subway.application;

import nextstep.subway.domain.*;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class LineService {
    private final String NOT_FOUND_STATION_ERROR = "지하철역을 찾을 수 없습니다.";
    private final String NOT_FOUND_LINE_ERROR = "지하철 노선을 찾을 수 없습니다.";

    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    @Transactional
    public LineResponse saveLine(LineRequest lineRequest) {
        Section section = createSection(
                lineRequest.getDistance(),
                lineRequest.getUpStationId(),
                lineRequest.getDownStationId()
        );

        Line response = lineRepository.save(Line.of(lineRequest.getName(), lineRequest.getColor(), section));
        return LineResponse.of(response);
    }

    public LineResponse saveSections(Long id, LineRequest lineRequest) {
        Line line = findLineById(id);
        Section section = createSection(
                lineRequest.getDistance(),
                lineRequest.getUpStationId(),
                lineRequest.getDownStationId()
        );

        line.addSection(section);
        return LineResponse.of(line);
    }

    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();

        return lines.stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
    }

    public LineResponse findLine(Long id) {
        Line line = findLineById(id);
        return LineResponse.of(line);
    }

    @Transactional
    public void update(Long id, LineRequest lineRequest) {
        Line line = findLineById(id);
        line.update(lineRequest.getName(), lineRequest.getColor());
    }

    @Transactional
    public void delete(Long id) {
        Line line = findLineById(id);
        line.delete();
    }

    private Section createSection(int distance, Long upStationId, Long downStationId) {
        return Section.of(distance, findStation(upStationId), findStation(downStationId));
    }

    private Station findStation(Long stationId) {
        return stationRepository.findById(stationId)
                .orElseThrow(() -> new NoSuchElementException(NOT_FOUND_STATION_ERROR));
    }

    private Line findLineById(Long id) {
        return lineRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(NOT_FOUND_LINE_ERROR));
    }
}
