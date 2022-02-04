package nextstep.subway.line.application;

import java.util.List;
import java.util.stream.Collectors;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.line.exception.LineNotFoundException;
import nextstep.subway.line.exception.NameDuplicateException;
import nextstep.subway.station.StationNotFoundException;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LineService {

    private LineRepository lineRepository;

    private StationRepository stationRepository;

    public LineService(LineRepository lineRepository,
        StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    public LineResponse saveLine(LineRequest request) {
        checkExistsByName(request.getName());
        Station upStation = station(request.getUpStationId());
        Station downStation = station(request.getDownStationId());
        Line persistLine = lineRepository.save(request.toLine(upStation, downStation));
        return LineResponse.of(persistLine);
    }

    private void checkExistsByName(String name) {
        if (lineRepository.existsByName(name)) {
            throw new NameDuplicateException("이미 존재하는 이름입니다. : " + name);
        }
    }

    private Station station(Long id) {
        return stationRepository.findById(id)
            .orElseThrow(() -> new StationNotFoundException(id + " : 존재하지 않는 역입니다."));
    }

    public LineResponse update(LineRequest request, Long id) {
        Line line = line(id);
        checkExistsByName(request.getName());
        line.update(request.toLine());
        return LineResponse.of(line);
    }

    public List<LineResponse> getLines() {
        List<Line> lines = lineRepository.findAll();
        return lines.stream()
            .map(LineResponse::of)
            .collect(Collectors.toList());
    }

    public LineResponse getLine(Long id) {
        Line line = line(id);
        return LineResponse.of(line);
    }

    public void delete(Long id) {
        Line line = line(id);
        lineRepository.delete(line);
    }

    private Line line(Long id) {
        return lineRepository.findById(id)
            .orElseThrow(() -> new LineNotFoundException(id + " : 존재하지 않는 라인입니다."));
    }

    public void addSection(SectionRequest sectionRequest, Long id) {

    }
}
