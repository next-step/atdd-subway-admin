package nextstep.subway.line.application;

import nextstep.subway.exception.ApiException;
import nextstep.subway.exception.ApiExceptionMessge;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static nextstep.subway.exception.ApiExceptionMessge.NOT_FOUND_LINE;

@Service
@Transactional
public class LineService {
    private LineRepository lineRepository;

    public LineService(LineRepository lineRepository) {
        this.lineRepository = lineRepository;
    }

    public LineResponse saveLine(LineRequest request) {
        Line persistLine = lineRepository.save(request.toLine());
        return LineResponse.of(persistLine);
    }

    @Transactional(readOnly = true)
    public List<LineResponse> getLines() {
        return convertLineResponse(lineRepository.findAll());
    }

    private List<LineResponse> convertLineResponse(List<Line> lines) {
        return lines.stream()
                    .map(LineResponse::of)
                    .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LineResponse getLine(final Long id) {
        return lineRepository.findById(id)
                             .map(LineResponse::of)
                             .orElseThrow(() -> new ApiException(NOT_FOUND_LINE));
    }
}
