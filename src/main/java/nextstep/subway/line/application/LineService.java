package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.domain.LineStation;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;

import java.util.List;

import javax.persistence.NoResultException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LineService {
    private LineRepository lineRepository;

    public LineService(LineRepository lineRepository) {
        this.lineRepository = lineRepository;
    }

    public LineResponse saveLine(Line line, LineStation... lineStations) {
        for (LineStation lineStation : lineStations) {
            line.addLineStation(lineStation);
        }
        Line persistLine = lineRepository.save(line);
        return LineResponse.of(persistLine);
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findAllLines() {
        return LineResponse.valueOfList(lineRepository.findAll());
    }

    @Transactional(readOnly = true)
    public LineResponse findLineById(Long id) {
        return LineResponse.of(lineRepository.findLineByFetchJoin(id));
    }

    @Transactional(readOnly = true)
    public Line findLineAndLineStations(Long id) {
        return lineRepository.findLineByFetchJoin(id);
    }

    @Transactional(readOnly = true)
    public Line findLine(Long id) {
        return lineRepository.findById(id)
                .orElseThrow(() -> new NoResultException(id + "엔티티가 존재하지 않습니다"));
    }

    public void updateLine(Long id, LineRequest lineRequest) {
        Line findLine = findLine(id);
        findLine.update(lineRequest.toLine());
    }

    public void removeLine(Long id) {
        lineRepository.deleteById(id);
    }
}
