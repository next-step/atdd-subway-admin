package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.ui.exception.DuplicateLineNameException;
import nextstep.subway.line.ui.exception.NotFoundLineException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class LineService {
    private LineRepository lineRepository;

    public LineService(LineRepository lineRepository) {
        this.lineRepository = lineRepository;
    }

    public LineResponse saveLine(LineRequest request) {
        if(lineRepository.existsByName(request.getName())){
            throw new DuplicateLineNameException(String.format("노선 이름이 이미 존재합니다.[%s]", request.getName()));
        }
        Line persistLine = lineRepository.save(request.toLine());
        return LineResponse.of(persistLine);
    }

    public List<LineResponse> findAllLines() {
        List<Line> findLines = lineRepository.findAll();

        List<LineResponse> responses = new ArrayList<>();
        for(Line line : findLines) {
            responses.add(LineResponse.of(line));
        }
        return responses;
    }

    public LineResponse findById(Long lineId) {
        Line line = lineRepository.findById(lineId).orElseThrow(
                () -> new NotFoundLineException(String.format("존재하지 않는 노선입니다.[lineId: %s]", lineId))
        );
        return LineResponse.of(line);
    }

    public void updateLine(Long lineId, LineRequest lineRequest) {
        Line line = lineRepository.findById(lineId).orElseThrow(
                () -> new NotFoundLineException(String.format("존재하지 않는 노선입니다.[lineId: %s]", lineId))
        );
        line.update(new Line(lineRequest.getName(), lineRequest.getColor()));
    }

    public void deleteLineById(Long lineId) {
        lineRepository.deleteById(lineId);
    }
}
