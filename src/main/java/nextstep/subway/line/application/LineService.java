package nextstep.subway.line.application;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import nextstep.subway.exception.LineDuplicateException;
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
        saveLineValidation(request);
        Line line = lineRepository.save(request.toLine());
        return LineResponse.of(line);
    }

    private void saveLineValidation(LineRequest request) {
        if(isDuplicate(request.getName())){
            throw new LineDuplicateException();
        }
    }

    private boolean isDuplicate(String name) {
        return lineRepository.findByName(name).isPresent();
    }

    @Transactional()
    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();

        return lines.stream()
            .map(LineResponse::of)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LineResponse findId(Long id) {
        return lineRepository.findById(id)
            .map(LineResponse::of)
            .orElseGet(LineResponse::new);
    }

    public LineResponse update(Long id, LineRequest lineRequest) {
        Line line = lineRepository.findById(id).orElseGet(Line::new);
        line.update(lineRequest.toLine());
        return LineResponse.of(line);
    }

    public void delete(Long id) {
        lineRepository.deleteById(id);
    }

}
