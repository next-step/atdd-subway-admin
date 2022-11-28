package nextstep.subway.application;

import nextstep.subway.domain.*;
import nextstep.subway.dto.LineCreateRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.LineUpdateRequest;
import nextstep.subway.dto.SectionCreateRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

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
    public LineResponse createLine(final LineCreateRequest request) {
        final Line line = lineRepository.save(request.toLine(
                findStation(request.getUpStationId()),
                findStation(request.getDownStationId())
        ));

        return LineResponse.of(line);
    }

    public List<LineResponse> findLines() {
        return lineRepository.findAll().stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
    }

    public LineResponse findLine(final Long id) {
        final Line line = findById(id);

        return LineResponse.of(line);
    }

    @Transactional
    public void updateLine(final Long id, final LineUpdateRequest request) {
        final Line line = findById(id);

        line.update(request.getName(), request.getColor());
    }

    @Transactional
    public void deleteLine(final Long id) {
        lineRepository.deleteById(id);
    }

    private Station findStation(final Long stationId) {
        return stationRepository.findById(stationId)
                .orElseThrow(() -> new IllegalArgumentException(String.format("존재하지 않는 역입니다. id=%d", stationId)));
    }

    private Line findById(final Long id) {
        return lineRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 노선이 존재하지 않습니다. id=" + id));
    }

    @Transactional
    public void addSection(final Long id, final SectionCreateRequest request) {
        final Line line = findById(id);
        final Station upStation = findStation(request.getUpStationId());
        final Station downStation = findStation(request.getDownStationId());

        Section section = request.toSection(upStation, downStation, line);
        line.addSection(section);
    }

    @Transactional
    public void deleteSection(final Long lineId, final Long stationId) {
        final Line line = findById(lineId);
        final Station station = findStation(stationId);

        line.removeStation(station);
    }
}
