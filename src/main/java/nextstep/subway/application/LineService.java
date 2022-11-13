package nextstep.subway.application;

import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.EntityNotFoundException;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class LineService {

    private final LineRepository lineRepository;

    public LineService(LineRepository lineRepository) {
        this.lineRepository = lineRepository;
    }


    @Transactional
    public Long saveLine(LineRequest lineRequest) {
        Line persistLine = lineRepository.save(lineRequest.toLine());
        return persistLine.getId();
    }

    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();

        return lines.stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
    }

    public LineResponse findById(Long id) {
        return LineResponse.of(lineRepository.findById(id).orElseThrow(EntityNotFoundException::new));
    }

    public LineResponse findByName(String name) {
        return LineResponse.of(lineRepository.findByName(name).orElseThrow(EntityNotFoundException::new));
    }

    @Transactional
    public void updateLine(String name, LineRequest lineRequest) {
        Line line = lineRepository.findByName(name).orElseThrow(EntityNotFoundException::new);
        line.changeInformation(lineRequest.getName(), lineRequest.getColor(), lineRequest.getDistance(),
                lineRequest.getUpStationId(), lineRequest.getDownStationId());
    }

    @Transactional
    public void deleteLineById(Long id) {
        lineRepository.deleteById(id);
    }
}
