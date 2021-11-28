package nextstep.subway.line.application;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
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
        checkLineNameIsUnique(request.getName());
        final Line persistLine = lineRepository.save(request.toLine());
        final List<Station> stations = getStations(
            request.getUpStationId(),
            request.getDownStationId()
        );
        final Station upStation = getStationById(stations, request.getUpStationId());
        final Station downStation = getStationById(stations, request.getDownStationId());
        final Section section = new Section(upStation, downStation, request.getDistance());
        persistLine.addSection(section);
        return LineResponse.of(persistLine);
    }

    private List<Station> getStations(final Long upStationId, final Long downStationId) {
        final List<Station> stations = stationRepository.findAllById(
            Arrays.asList(upStationId, downStationId)
        );
        if (stations.size() != 2) {
            throw new NoSuchElementException();
        }
        return stations;
    }

    private Station getStationById(final List<Station> stations, final Long id) {
        return stations.stream()
            .filter(s -> Objects.equals(s.getId(), id))
            .findFirst()
            .orElseThrow(NoSuchElementException::new);
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
    public void updateLine(final Long id, final LineRequest request) {
        final Line line = getLineById(id);
        if (!line.nameEquals(request.getName())) {
            checkLineNameIsUnique(request.getName());
        }
        line.changeName(request.getName());
        line.changeColor(request.getColor());
    }

    @Transactional
    public void deleteStationById(final Long id) {
        final Line line = getLineById(id);
        lineRepository.delete(line);
    }

    private Line getLineById(final Long id) {
        return lineRepository.findById(id)
            .orElseThrow(NoSuchElementException::new);
    }

    private void checkLineNameIsUnique(final String requestedName) {
        if (lineRepository.existsByName(requestedName)) {
            throw new IllegalArgumentException("이미 존재하는 지하철 노선 이름으로 지하철 노선을 생성할 수 없습니다.");
        }
    }
}
