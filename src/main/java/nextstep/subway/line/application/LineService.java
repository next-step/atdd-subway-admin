package nextstep.subway.line.application;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public List<LineResponse> getLines() {
        return lineRepository.findAll()
            .stream().map(LineResponse::of)
            .collect(Collectors.toList());
    }

    public LineResponse getLine(Long id) {
        Line findLine = lineRepository.findById(id).orElseThrow(NoSuchElementException::new);

        return LineResponse.of(findLine);
    }

    public LineResponse updateLine(Long id, LineRequest lineRequest) {
        Line findLine = lineRepository.findById(id).orElseThrow(NoSuchElementException::new);
        findLine.update(
            new Line(lineRequest.getName(),
                lineRequest.getColor())
        );

        return LineResponse.of(findLine);
    }

    public void deleteLine(Long id) {
        lineRepository.deleteById(id);
    }
}
