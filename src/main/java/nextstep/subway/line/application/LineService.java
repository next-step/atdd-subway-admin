package nextstep.subway.line.application;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.domain.Section;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LineService {

    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    public LineService(final LineRepository lineRepository,
        final StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    @Transactional
    public LineResponse saveLine(final LineRequest request) {
        lineNameShouldBeUnique(request.getName());
        final Line persistLine = lineRepository.save(request.toLine());
        // stationsShouldExist(request.getUpStationId(), request.getDownStationId());
        // final Section section = new Section(
        //     request.getUpStationId(),
        //     request.getDownStationId(),
        //     request.getDistance()
        // );
        //
        final Station upStation = stationRepository.findById(request.getUpStationId())
            .orElseThrow(NoSuchElementException::new);
        final Station downStation = stationRepository.findById(request.getDownStationId())
            .orElseThrow(NoSuchElementException::new);
        //
        final Section section = new Section(upStation, downStation, request.getDistance());
        //
        persistLine.addSection(section);
        return LineResponse.of(persistLine);
    }

    private void lineNameShouldBeUnique(final String requestedName) {
        if (lineRepository.existsByName(requestedName)) {
            throw new IllegalArgumentException("이미 존재하는 지하철 노선 이름으로 지하철 노선을 생성할 수 없습니다.");
        }
    }

    private void stationsShouldExist(final Long upStationId, final Long downStationId) {
        stationShouldExist(upStationId);
        stationShouldExist(downStationId);
    }

    private void stationShouldExist(final Long stationId) {
        stationRepository.findById(stationId)
            .orElseThrow(() ->
                new NoSuchElementException("Station ID " + stationId + " does not exists.")
            );
    }

    public List<LineResponse> findLines() {
        final List<Line> lines = lineRepository.findAll();
        return lines.stream()
            .map(LineResponse::of)
            .collect(Collectors.toList());
    }

    public LineResponse findLineById(final Long id) {
        final Line line = getLineById(id);
        return LineResponse.of(line);
    }

    @Transactional
    public void updateLine(final Long id, final LineRequest lineRequest) {
        final Line line = getLineById(id);
        if (!lineRequest.getName().equals(line.getName())) {
            lineNameShouldBeUnique(lineRequest.getName());
        }
        line.changeName(lineRequest.getName());
        line.changeColor(lineRequest.getColor());
    }

    private Line getLineById(final Long id) {
        return lineRepository.findById(id)
            .orElseThrow(NoSuchElementException::new);
    }

    @Transactional
    public void deleteStationById(final Long id) {
        lineRepository.deleteById(id);
    }
}
