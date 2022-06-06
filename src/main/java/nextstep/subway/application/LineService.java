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
    private static final String ERROR_MESSAGE_STATION_NOT_FOUND = "해당 지하철 역이 존재하지 않습니다.";
    private static final String ERROR_MESSAGE_LINE_NOT_FOUND = "해당 노선은 존재하지 않습니다.";

    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    @Transactional
    public LineResponse saveLine(LineRequest lineRequest) {
        Section section = mapToSection(lineRequest.getUpStationId(), lineRequest.getDownStationId(), lineRequest.getDistance());
        Line save = lineRepository.save(Line.of(lineRequest.getName(), lineRequest.getColor(), section));

        return LineResponse.of(save);
    }

    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();

        return lines.stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
    }

    public LineResponse findLineById(Long id) {
        Line line = lineRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(ERROR_MESSAGE_LINE_NOT_FOUND));

        return LineResponse.of(line);
    }

    @Transactional
    public void updateLineInfo(Long id, LineRequest lineRequest) {
        Line line = lineRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(ERROR_MESSAGE_LINE_NOT_FOUND));

        line.updateLine(lineRequest.getName(), lineRequest.getColor());
    }

    @Transactional
    public void deleteLineById(Long id) {
        lineRepository.deleteById(id);
    }

    private Section mapToSection(Long upStationId, Long downStationId, int distance) {
        Station upStation = stationRepository.findById(upStationId)
                .orElseThrow(() -> new NoSuchElementException(ERROR_MESSAGE_STATION_NOT_FOUND));
        Station downStation = stationRepository.findById(downStationId)
                .orElseThrow(() -> new NoSuchElementException(ERROR_MESSAGE_STATION_NOT_FOUND));

        return Section.of(upStation, downStation, distance);
    }
}
