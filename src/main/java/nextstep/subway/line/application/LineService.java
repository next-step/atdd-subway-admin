package nextstep.subway.line.application;

import nextstep.subway.common.exceptions.NotFoundException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    public List<LineResponse> findAll() {
        return lineRepository.findAll().stream().map(line->LineResponse.of(line)).collect(Collectors.toList());
    }

    public Line findById(Long id) {
        return lineRepository.findById(id)
                .orElseThrow(()->new NotFoundException("invalid " + id));
    }

    public void updateLine(Long id, Line newLine) {
        Line line = lineRepository.findById(id)
                .orElseThrow(()-> new NotFoundException("invalid "+id));
        line.update(newLine);
    }

    public void deleteLine(Long id) {
        Line line = lineRepository.findById(id)
                .orElseThrow(()-> new NotFoundException("invalid "+id));
        lineRepository.delete(line);
    }
}
