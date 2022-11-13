package nextstep.subway.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javassist.NotFoundException;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.StationRequest;
import nextstep.subway.dto.StationResponse;
import nextstep.subway.value.ErrMsg;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional(readOnly = true)
public class LineService {
    private LineRepository lineRepository;

    public LineService(LineRepository lineRepository) {
        this.lineRepository = lineRepository;
    }

    @Transactional
    public LineResponse saveLine(LineRequest lineRequest) {
        Line persistLine = lineRepository.save(lineRequest.toLine());
        return LineResponse.of(persistLine);
    }

    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();

        return lines.stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteLineById(Long id) throws NotFoundException {
        Line line = findLineById(id);
        lineRepository.delete(line);
    }

    public LineResponse findLine(Long id) throws NotFoundException {
        return LineResponse.of(findLineById(id));
    }

    @Transactional
    public LineResponse updateLine(Long id, LineRequest lineRequest) throws NotFoundException {
        Line line =  findLineById(id);
        line.update(lineRequest);
        return LineResponse.of(line);
    }

    private Line findLineById(Long id) throws NotFoundException {
        return lineRepository.findById(id).orElseThrow(
                () -> new NotFoundException(id + ErrMsg.CANNOT_FIND_LINE)
        );
    }
}
