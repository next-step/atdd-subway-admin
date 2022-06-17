package nextstep.subway.application;

import nextstep.subway.domain.LineStation.LineStations;
import nextstep.subway.domain.common.Distance;
import nextstep.subway.domain.line.*;
import nextstep.subway.domain.section.Section;
import nextstep.subway.domain.station.Station;
import nextstep.subway.dto.line.LineRequest;
import nextstep.subway.dto.line.LineResponse;
import nextstep.subway.dto.line.SectionRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class LineService {

    private final LineRepository lineRepository;
    private final StationService stationService;
    private final LineStationService lineStationService;

    public LineService(LineRepository lineRepository, StationService stationService, LineStationService lineStationService) {
        this.lineRepository = lineRepository;
        this.stationService = stationService;
        this.lineStationService = lineStationService;
    }

    @Transactional
    public LineResponse saveLine(LineRequest lineRequest) {
        validateDuplicate(lineRequest.getName(), lineRequest.getColor());

        Line line = createLine(lineRequest);

        Line persistLine = lineRepository.save(line);

        return LineResponse.of(persistLine);
    }

    private Line createLine(LineRequest lineRequest) {
        Station upStation = stationService.getStationById(lineRequest.getUpStationId());
        Station downStation = stationService.getStationById(lineRequest.getDownStationId());

        return Line.create(
                LineName.of(lineRequest.getName()),
                LineColor.of(lineRequest.getColor()),
                Distance.of(lineRequest.getDistance()),
                upStation,
                downStation
        );
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

        line.modify(lineRequest.getName(), lineRequest.getColor());
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

        lineByName.ifPresent(line -> {
            throw new IllegalArgumentException("중복된 지하철 노선 이름입니다.");
        });
    }


    private void validateDuplicatedColor(String color) {
        Optional<Line> lineByColor = lineRepository.findByColor(LineColor.of(color));

        lineByColor.ifPresent(line -> {
            throw new IllegalArgumentException("중복된 지하철 노선 이름입니다.");
        });
    }

    @Transactional
    public void addSection(Long id, SectionRequest sectionRequest) {
        Line line = getLineById(id);

        List<Long> stationIds = Arrays.asList(sectionRequest.getUpStationId(), sectionRequest.getDownStationId());
        Map<Long, Station> stations = stationService.getStationsByIds(stationIds);

        Station upStation = stations.get(sectionRequest.getUpStationId());
        Station downStation = stations.get(sectionRequest.getDownStationId());

        Section section = Section.create(upStation, downStation, Distance.of(sectionRequest.getDistance()));

        line.addSection(section);
    }

    @Transactional
    public void removeSectionByStationId(Long lineId, Long stationId) {
        LineStations lineStations = lineStationService.getLineStationsByStationId(stationId);

        if (lineStations.isContainLine(lineId)) {
            throw new IllegalArgumentException("노선에 포함되지 않는 역입니다. lineId : " + lineId + ", stationId : " + stationId);
        }

        Lines lines = lineStations.getLines();

        lines.removeStation(stationId);
    }
}
