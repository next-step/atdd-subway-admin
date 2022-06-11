package nextstep.subway.application;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class LineService {

    private LineRepository lineRepository;

    public LineService(LineRepository lineRepository) {
        this.lineRepository = lineRepository;
    }

    public LineResponse readLine(Long id) throws NotFoundException {
        Line line = lineRepository.findById(id).orElseThrow(NotFoundException::new);
        return LineResponse.from(line);
    }

    public List<LineResponse> readLines() {
        return lineRepository.findAll().stream()
//                .map(line -> LineResponse.from(line))
                .map(LineResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public LineResponse saveLine(LineRequest lineRequest) {
        Line persistLine = lineRepository.save(new Line(lineRequest.getName(), lineRequest.getColor()));
        return LineResponse.from(persistLine);
    }

    @Transactional
    public void deleteLine(Long id) {
        lineRepository.deleteById(id);
    }

    @Transactional
    public LineResponse updateLine(Long id, LineRequest lineRequest) throws NotFoundException {
        Line line = lineRepository.findById(id).orElseThrow(NotFoundException::new);
        Line updatedLine = line.update(lineRequest);
        return LineResponse.from(updatedLine);
    }
}
