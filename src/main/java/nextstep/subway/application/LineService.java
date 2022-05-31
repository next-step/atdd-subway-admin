package nextstep.subway.application;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
    public LineResponse saveLine(final LineRequest lineRequest) {
        final Line line = lineRequest.toLine()
                .upStationBy(stationRepository.findById(lineRequest.getUpStationId()).get())
                .downStationBy(stationRepository.findById(lineRequest.getDownStationId()).get());
        return LineResponse.of(lineRepository.save(line));
    }

    public List<LineResponse> findAllLine() {
        List<Line> lines = lineRepository.findAll();
        return lines.stream().map(LineResponse::of).collect(Collectors.toList());
    }

    public LineResponse findLine(final Long id) {
        final Line line = lineRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        return LineResponse.of(line);
    }

    @Transactional
    public void deleteLineById(final Long id) {
        lineRepository.deleteById(id);
    }

    @Transactional
    public void updateLine(final Long id, final LineRequest updateLine) {
        update(lineRepository.findById(id).get(), updateLine);
    }

    private void update(Line line, LineRequest updateLine) {
        if (Objects.nonNull(updateLine.getUpStationId()) && updateLine.getUpStationId() > 0) {
            line.setUpStation(stationRepository.findById(updateLine.getUpStationId()).get());
        }
        if (Objects.nonNull(updateLine.getDownStationId()) && updateLine.getDownStationId() > 0) {
            line.setDownStation(stationRepository.findById(updateLine.getDownStationId()).get());
        }
        line.setName(updateLine.getName());
        line.setColor(updateLine.getColor());
        line.setDistance(updateLine.getDistance());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LineService that = (LineService) o;
        return Objects.equals(lineRepository, that.lineRepository);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lineRepository);
    }
}
