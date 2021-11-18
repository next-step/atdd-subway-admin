package nextstep.subway.line.application;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.domain.Section;
import nextstep.subway.line.dto.LineCreateRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.LineUpdateRequest;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;

@Service
@Transactional(readOnly = true)
public class LineService {
    private static final String MESSAGE_ON_LINE_NOT_FOUND = "노선을 찾을 수 없습니다.";

    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    public LineService(
        LineRepository lineRepository,
        StationRepository stationRepository
    ) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    @Transactional
    public LineResponse saveLine(LineCreateRequest request) {
        Station upStation = stationRepository.findById(request.getUpStationId())
            .orElseThrow(() -> new NoSuchElementException("상행역을 찾을 수 없습니다."));
        Station downStation = stationRepository.findById(request.getDownStationId())
            .orElseThrow(() -> new NoSuchElementException("하행역을 찾을 수 없습니다."));

        Section section = Section.of(upStation, downStation, request.getDistance());
        Line line = lineRepository.save(Line.of(request.getName(), request.getColor(), section));
        return LineResponse.of(line);
    }

    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();

        return lines.stream()
            .map(LineResponse::of)
            .collect(Collectors.toList());
    }

    public LineResponse findLine(Long id) {
        Line line = lineRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException(MESSAGE_ON_LINE_NOT_FOUND));

        return LineResponse.of(line);
    }

    @Transactional
    public void updateLine(Long id, LineUpdateRequest request) {
        Line line = lineRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException(MESSAGE_ON_LINE_NOT_FOUND));

        line.update(request.getName(), request.getColor());
    }

    @Transactional
    public void deleteLineById(Long id) {
        Line line = lineRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException(MESSAGE_ON_LINE_NOT_FOUND));

        lineRepository.delete(line);
    }
}
