package nextstep.subway.line.application;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;

@Service
public class LineService {
    private LineRepository lineRepository;

    public LineService(LineRepository lineRepository) {
        this.lineRepository = lineRepository;
    }

    @Transactional
    public LineResponse saveLine(LineRequest request) {
        Line persistLine = lineRepository.save(request.toLine());
        return LineResponse.from(persistLine);
    }

    @Transactional(readOnly = true)
    public List<LineResponse> getLines() {
        return lineRepository.findAll()
            .stream()
            .map(LineResponse::from)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LineResponse getLine(long id) {
        Line line = lineRepository.findById(id)
            .orElseThrow(NoSuchElementException::new);
        return LineResponse.from(line);
    }

    @Transactional
    public LineResponse updateLine(long id, LineRequest lineRequest) {
        Line line = lineRepository.findById(id)
            .orElseThrow(NoSuchElementException::new);
        line.update(lineRequest.toLine());
        lineRepository.save(line);

        return LineResponse.from(line);
    }

    @Transactional
    public void deleteLine(long id) {
        lineRepository.deleteById(id);
    }
}
