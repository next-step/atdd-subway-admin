package nextstep.subway.application;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.SectionRequest;
import nextstep.subway.dto.UpdateLineRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LineService {

    private final LineRepository lineRepository;

    private final StationRepository stationRepository;

    public LineService(final LineRepository lineRepository, final StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    @Transactional
    public LineResponse saveLine(final LineRequest lineRequest) {
        Line line = lineRequest.toLine();
        final Station upStation = findStationById(lineRequest.getUpStationId());
        final Station downStation = findStationById(lineRequest.getDownStationId());
        final Section section = new Section(line, upStation, downStation, lineRequest.getDistance());
        line.addSection(section);
        lineRepository.save(line);
        return LineResponse.of(line);
    }

    public List<LineResponse> findAllLines() {
        return lineRepository.findAllFetchStation()
                .stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
    }

    public LineResponse findLine(final Long id) {
        return LineResponse.of(findLineById(id));
    }

    @Transactional
    public void updateLine(final Long id, final UpdateLineRequest updateLineRequest) {
        final Line line = findLineById(id);
        line.update(updateLineRequest.toLine());
    }

    private Line findLineById(final Long id) {
        return lineRepository.findFetchStationById(id)
                .orElseThrow(() -> new NoSuchElementException("지하철 노선을 찾을 수 없습니다."));
    }

    @Transactional
    public void deleteLine(final Long id) {
        lineRepository.deleteById(id);
    }

    @Transactional
    public void addSections(final Long id, final SectionRequest sectionRequest) {
        final Line line = findLineById(id);
        final Station upStation = findStationById(sectionRequest.getUpStationId());
        final Station downStation = findStationById(sectionRequest.getDownStationId());
        final Section section = sectionRequest.toSection(line, upStation, downStation);
        line.addSection(section);
    }

    private Station findStationById(final long id) {
        return stationRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("지하철역을 찾을 수 없습니다."));
    }

    @Transactional
    public void deleteStation(final Long id, final Long stationId) {
        final Line line = findLineById(id);
        final Station station = findStationById(stationId);
        line.deleteStation(station);
    }
}
