package nextstep.subway.line.application;

import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import nextstep.subway.line.LineNotFoundException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class LineService {
    private final LineRepository lineRepository;

    public LineResponse saveLine(final LineRequest request) {
        Line persistLine = lineRepository.save(request.toEntity());
        return LineResponse.of(persistLine);
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findAllLines() {
        List<Line> Lines = lineRepository.findAll();

        return Lines.stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LineResponse findLine(final Long id) {
         Line line = lineRepository.findById(id)
                 .orElseThrow(() -> new LineNotFoundException());

        return LineResponse.of(line);
    }

    public void updateLine(final Long id, final LineRequest request) {
        Line line = lineRepository.findById(id)
                .orElseThrow(() -> new LineNotFoundException());

        line.update(request.toEntity());
    }

    public void deleteLineById(final Long id) {
        lineRepository.deleteById(id);
    }
}
