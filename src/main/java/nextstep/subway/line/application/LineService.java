package nextstep.subway.line.application;

import nextstep.subway.common.exception.NotFoundException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.domain.Lines;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class LineService {
    private LineRepository lineRepository;

    public LineService(LineRepository lineRepository) {
        this.lineRepository = lineRepository;
    }

    @Transactional(readOnly = true)
    public List<LineResponse> showAllLines() {
        return Lines.of(lineRepository.findAll()).toDto();
    }

    @Transactional(readOnly = true)
    public Line findByLineId(Long id) throws NotFoundException {
        return lineRepository.findById(id).orElseThrow(() -> new NotFoundException("데이터가 존재하지 않습니다."));
    }

    public LineResponse saveLine(LineRequest request) {
        final Line line = lineRepository.save(request.toLine());
        return line.toDto();
    }

    public LineResponse findOne(Long id) {
        final Line line = findByLineId(id);
        return line.toDto();
    }

    public LineResponse update(Long id, LineRequest request) {
        final Line line = findByLineId(id);
        line.update(request.toLine());
        return line.toDto();
    }

    public void delete(Long id) {
        Line line = findByLineId(id);
        lineRepository.deleteById(line.getId());
    }
}
