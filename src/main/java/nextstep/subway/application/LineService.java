package nextstep.subway.application;

import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.EntityNotFoundException;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.StationResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class LineService {

    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    @Transactional
    public Long saveLine(LineRequest lineRequest) {
        setUpDownStation(lineRequest);
        Line persistLine = lineRepository.save(lineRequest.toLine());
        return persistLine.getId();
    }

    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();

        return lines.stream()
                .map(this::getLineResponse)
                .collect(Collectors.toList());
    }

    public LineResponse findById(Long id) {
        Line line = lineRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        return getLineResponse(line);
    }

    public LineResponse findByName(String name) {
        Line line = lineRepository.findByName(name).orElseThrow(EntityNotFoundException::new);
        return getLineResponse(line);
    }

    @Transactional
    public void updateLine(String name, LineRequest lineRequest) {
        Line line = lineRepository.findByName(name).orElseThrow(EntityNotFoundException::new);
        line.changeInformation(lineRequest.getName(), lineRequest.getColor());
    }

    @Transactional
    public void deleteLineById(Long id) {
        lineRepository.deleteById(id);
    }

    private LineResponse getLineResponse(Line line) {
        LineResponse lineResponse = LineResponse.of(line);
        lineResponse.setUpStation(StationResponse.of(line.getUpStation()));
        lineResponse.setDownStation(StationResponse.of(line.getDownStation()));
        return lineResponse;
    }

    private void setUpDownStation(LineRequest lineRequest) {
        lineRequest.setUpStation(
                stationRepository.findById(lineRequest.getUpStationId()).orElseThrow(EntityNotFoundException::new));
        lineRequest.setDownStation(
                stationRepository.findById(lineRequest.getDownStationId()).orElseThrow(EntityNotFoundException::new));
    }
}
