package nextstep.subway.line.application;

import nextstep.subway.common.exception.NotFoundException;
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
public class LineService {
    private LineRepository lineRepository;

    public LineService(LineRepository lineRepository) {
        this.lineRepository = lineRepository;
    }

    @Transactional(readOnly = true)
    public List<LineResponse> showAllLines() {
        return lineRepository.findAll().stream().map(LineResponse::of).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Line findByLineId(Long id) throws NotFoundException {
        return lineRepository.findById(id).orElseThrow(() -> new NotFoundException("데이터가 존재하지 않습니다."));
    }

    public LineResponse saveLine(LineRequest request) {
        Line persistLine = lineRepository.save(request.toLine());
        return LineResponse.of(persistLine);
    }

    public LineResponse findOne(Long id) {
        Line line = this.findByLineId(id);
        return LineResponse.of(line);
    }

    public LineResponse update(Long id, LineRequest request) {
        Line line = this.findByLineId(id);
        line.update(request.toLine());
        return LineResponse.of(line);
    }

    public void delete(Long id) {
        Line line = this.findByLineId(id);
        lineRepository.deleteById(line.getId());
    }
}
