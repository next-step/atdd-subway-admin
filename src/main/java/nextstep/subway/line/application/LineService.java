package nextstep.subway.line.application;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;

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
    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();
        return lines.stream().map(LineResponse::of).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LineResponse findLine(Long id) {
        return lineRepository.findById(id).map(LineResponse::of).orElseThrow(EntityNotFoundException::new);
    }

    public void updateLine(LineRequest lineRequest, Long id) {
        Line findedLine = lineRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        findedLine.update(lineRequest.toLine());
    }

    public void deleteLine(Long id) {
        Line findedLine = lineRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        lineRepository.delete(findedLine);
    }
}
