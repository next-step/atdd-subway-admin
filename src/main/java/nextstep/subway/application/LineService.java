package nextstep.subway.application;

import java.util.List;
import java.util.stream.Collectors;
import javassist.NotFoundException;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class LineService {

    private LineRepository lineRepository;
    private StationRepository stationRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    @Transactional
    public LineResponse saveLine(LineRequest lineRequest) throws NotFoundException {
        if (isExistLine(lineRequest)) {
            throw new IllegalArgumentException("이미 존재하는 노선입니다");
        }
        Station upStation = stationRepository.findById(lineRequest.getUpStationId())
            .orElseThrow(() -> new NotFoundException("상행 역이 존재하지 않습니다."));
        Station downStation = stationRepository.findById(lineRequest.getDownStationId())
            .orElseThrow(() -> new NotFoundException("하행 역이 존재하지 않습니다."));

        Line line = new Line(lineRequest.getName(), lineRequest.getColor(), upStation, downStation,
            lineRequest.getDistance());
        lineRepository.save(line);
        return LineResponse.of(line);
    }

    public List<LineResponse> findAllLine() {
        List<Line> lines = lineRepository.findAll();
        return lines.stream().map(line -> LineResponse.of(line)).collect(Collectors.toList());
    }

    public LineResponse findLine(Long id) {
        Line line = lineRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("지하철 노선이 존재하지 않습니다."));
        return LineResponse.of(line);
    }

    @Transactional
    public void updateLine(Long id, LineRequest lineRequest) {
        Line line = lineRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("지하철 노선이 존재하지 않습니다."));
        line.update(lineRequest);
    }

    @Transactional
    public void deleteLine(Long id) {
        lineRepository.deleteById(id);
    }


    private boolean isExistLine(LineRequest lineRequest) {
        return lineRepository.findByName(lineRequest.getName()) != null;
    }
}
