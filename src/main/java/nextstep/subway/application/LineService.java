package nextstep.subway.application;

import nextstep.subway.domain.line.Line;
import nextstep.subway.domain.line.LineColor;
import nextstep.subway.domain.line.LineName;
import nextstep.subway.domain.line.LineRepository;
import nextstep.subway.domain.section.Section;
import nextstep.subway.domain.station.Station;
import nextstep.subway.domain.station.StationRepository;
import nextstep.subway.dto.line.LineRequest;
import nextstep.subway.dto.line.LineResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
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
    public LineResponse saveLine(LineRequest lineRequest) {
        validateDuplicate(lineRequest.getName(), lineRequest.getColor());

        Line line = createLine(lineRequest);

        Line persistLine = lineRepository.save(line);

        return LineResponse.of(persistLine);
    }

    private Line createLine(LineRequest lineRequest) {
        Line line = lineRequest.toLine();
        Station upStation = getStationById(lineRequest.getUpStationId());
        Station downStation = getStationById(lineRequest.getDownStationId());

        Section section = Section.create(upStation, downStation, lineRequest.getDistance());

        line.addSection(section);
        line.setTerminus(upStation, downStation);
        return line;
    }

    private Station getStationById(Long stationId) {
        return stationRepository.findById(stationId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 지하철역입니다. stationId : " + stationId));
    }

    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();

        return lines.stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
    }

    public LineResponse findLineById(Long id) {
        Line line = getLineById(id);

        return LineResponse.of(line);
    }

    @Transactional
    public void modifyLine(Long lineId, LineRequest lineRequest) {
        Line line = getLineById(lineId);

        if (!Objects.equals(line.getName(), lineRequest.getName())) {
            validateDuplicatedName(lineRequest.getName());
        }

        if (!Objects.equals(line.getColor(), lineRequest.getColor())) {
            validateDuplicatedColor(lineRequest.getColor());
        }

        line.modifyNameAndColor(lineRequest.getName(), lineRequest.getColor());
    }

    private Line getLineById(Long lineId) {
        return lineRepository.findById(lineId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 지하철 노선입니다. lineId : " + lineId));
    }

    @Transactional
    public void deleteLineById(Long lineId) {
        lineRepository.deleteById(lineId);
    }


    private void validateDuplicate(String name, String color) {
        validateDuplicatedName(name);
        validateDuplicatedColor(color);
    }

    private void validateDuplicatedName(String name) {
        Optional<Line> lineByName = lineRepository.findByName(LineName.of(name));

        if (lineByName.isPresent()) {
            throw new IllegalArgumentException("중복된 지하철 노선 이름입니다.");
        }
    }


    private void validateDuplicatedColor(String color) {
        Optional<Line> lineByColor = lineRepository.findByColor(LineColor.of(color));

        if (lineByColor.isPresent()) {
            throw new IllegalArgumentException("중복된 지하철 노선 색갈입니다.");
        }
    }
}
