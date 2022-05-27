package nextstep.subway.application;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;

import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class LineService {
    private static final String NO_EXIST_STATION_ERROR_MESSAGE = "해당 지하철 역이 존재하지 않습니다.";
    private static final String NO_EXIST_LINE_ERROR_MESSAGE = "해당 노선은 존재하지 않습니다.";

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
                .orElseThrow(() -> new NoSuchElementException(NO_EXIST_LINE_ERROR_MESSAGE));
        return LineResponse.of(line);
    }

    @Transactional
    public void updateLineInfo(Long id, LineRequest lineRequest) {
        Line line = lineRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(NO_EXIST_LINE_ERROR_MESSAGE));
        line.updateLine(lineRequest.getName(), lineRequest.getColor());
    }

    @Transactional
    public void deleteLineById(Long id) {
        lineRepository.deleteById(id);
    }

    private Section mapToSection(Long upStationId, Long downStationId, int distance) {
        Station upStation = stationRepository.findById(upStationId)
                .orElseThrow(() -> new NoSuchElementException(NO_EXIST_STATION_ERROR_MESSAGE));
        Station downStation = stationRepository.findById(downStationId)
                .orElseThrow(() -> new NoSuchElementException(NO_EXIST_STATION_ERROR_MESSAGE));
        return Section.of(upStation, downStation, distance);
    }
}
