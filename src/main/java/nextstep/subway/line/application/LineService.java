package nextstep.subway.line.application;

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

    public LineResponse saveLine(LineRequest request) {
        Line persistLine = lineRepository.save(request.toLine());
        return LineResponse.of(persistLine);
    }

    public List<LineResponse> getLines(){
        List<Line> lines = lineRepository.findAll();
        return lines.stream().map(LineResponse::of).collect(Collectors.toList());
    }

    public LineResponse getLine(long id){
        Line line = lineRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 id의 노선을 찾을 수 없습니다."));
        return LineResponse.of(line);
    }

    public LineResponse updateLine(long id, LineRequest request){
        Line line = lineRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 id의 노선을 찾을 수 없습니다."));
        line.update(request.toLine());
        return LineResponse.of(line);
    }

    public void delete(long id){
        Line line = lineRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 id의 노선을 찾을 수 없습니다."));
        lineRepository.delete(line);
    }
}
