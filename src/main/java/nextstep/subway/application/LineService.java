package nextstep.subway.application;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.StationResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class LineService {

    private LineRepository lineRepository;
    private static final String NO_SUCH_LINE_EXCEPTION = "해당 ID의 노선 정보가 없습니다.";

    public LineService(LineRepository lineRepository) {
        this.lineRepository = lineRepository;
    }

    @Transactional
    public LineResponse saveLine(LineRequest lineRequest) {
        Line persistLine = lineRepository.save(lineRequest.toLine());
        LineResponse lineResponse = LineResponse.of(persistLine);
        return lineResponse;
    }

    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();

        return lines.stream()
                .map(line -> LineResponse.of(line))
                .collect(Collectors.toList());
    }

    public LineResponse findLineById(Long id) {
        Line line = lineRepository
                .findById(id)
                .orElseThrow(() -> new IllegalArgumentException(NO_SUCH_LINE_EXCEPTION));
        return LineResponse.of(line);
    }

    @Transactional
    public void deleteLineById(Long id) {
        lineRepository
                .findById(id)
                .orElseThrow(() -> new IllegalArgumentException(NO_SUCH_LINE_EXCEPTION));
        lineRepository.deleteById(id);
    }

    @Transactional
    public LineResponse updateLine(Long id, LineRequest lineRequest) {
        Line line = lineRepository
                .findById(id)
                .orElseThrow(() -> new IllegalArgumentException(NO_SUCH_LINE_EXCEPTION));
        Line persistLine = line.of(lineRequest);

        return LineResponse.of(persistLine);
    }
}