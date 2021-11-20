package nextstep.subway.line.application;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import nextstep.subway.exception.BadRequestException;
import nextstep.subway.exception.NotFoundException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.domain.Section;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;

@Service
@Transactional
public class LineService {
    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    public LineService(final LineRepository lineRepository, final StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    public LineResponse saveLine(final LineRequest request) {
        validateDuplicatedName(request);
        final Line persistLine = lineRepository.save(request.toLine());

        addSection(request, persistLine);

        return LineResponse.of(persistLine);
    }

    private void addSection(final LineRequest request, final Line persistLine) {
        final Station upStation = getStationById(request.getUpStationId());
        final Station downStation = getStationById(request.getDownStationId());
        persistLine.addSection(new Section(upStation, downStation, request.getDistance()));
    }

    private Station getStationById(final Long stationId) {
        return stationRepository.findById(stationId).orElseThrow(() -> new NotFoundException("지하철역을 찾을 수 없습니다."));
    }

    public List<LineResponse> getAllLines() {
        final List<Line> lines = lineRepository.findAll();
        return LineResponse.of(lines);
    }

    public LineResponse getLine(final Long id) {
        final Line line = getLineById(id);
        return LineResponse.of(line);
    }

    public void updateLine(final LineRequest request, final Long id) {
        final Line line = getLineById(id);
        line.update(request.toLine());
    }

    public void deleteLine(final Long id) {
        lineRepository.deleteById(id);
    }

    private void validateDuplicatedName(final LineRequest request) {
        final boolean duplicated = lineRepository.findByName(request.getName()).isPresent();
        if (duplicated) {
            throw new BadRequestException("중복된 지하철 노선 이름은 사용할 수 없습니다.");
        }
    }

    private Line getLineById(final Long id) {
        return lineRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("지하철 노선을 찾을 수 없습니다."));
    }
}
