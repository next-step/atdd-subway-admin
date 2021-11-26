package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.domain.Section;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.exception.NotFoundException;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class LineService {
    private static final String EQUAL_SECTION_EXIST_EXCEPTION_STATEMENT = "같은 구간의 데이터가 존재합니다.";
    private static final String OLD_DISTANCE_IS_GRATER_EQUAL_THAN_NEW_DISTANCE_EXCEPTION_STATEMENT = "기존거리보다 새로운 입력거리가 깁니다.";
    private static final String ALREADY_UP_STATION_AND_DOWN_STATION_EXIST_EXCEPTION_STATEMENT = "상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없습니다.";
    private static final String CANNOT_ADD_WITH_NONE_OF_UP_STATION_AND_DOWN_STATION_EXIST_EXCEPTION_STATEMENT = "상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없습니다.";

    private static final String CANNOT_DELETE_SECTION_WITH_DATA_SIZE_LESS_EQUAL_THAN_ONE_EXCEPTION_STATEMENT = "구간 데이터가 하나 이하이므로 제거할 수 없습니다.";
    private static final String NOT_CANNOT_DELETE_SECTION_EXCEPTION_STATEMENT = "존재하지 않는 구간 데이터 입니다.";

    private static final String NOT_DEFINED_REQUEST_EXCEPTION_STATEMENT = "정의되지 않은 요청입니다.";

    private static final String LINE = "노선";
    private static final String STATION = "역";
    private static final String SECTION = "구간";

    private static final int SECTION_DATA_MIN_SIZE = 2;

    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findAll() {
        return lineRepository.findAll()
            .stream()
            .map(LineResponse::of)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LineResponse getLineResponseById(Long id) {
        Line line = getLineById(id);
        return LineResponse.of(line);
    }

    public void updateLineById(Long id, LineRequest lineRequest) {
        Line line = getLineById(id);
        line.update(lineRequest.toLine());
    }

    public void deleteLineById(Long id) {
        lineRepository.deleteById(id);
    }

    private Line getLineById(Long id) {
        return lineRepository.findById(id)
            .orElseThrow(() -> new NotFoundException(LINE));
    }

    public LineResponse saveLine(LineRequest request) {
        Station upStation = stationRepository.findById(request.getUpStationId())
            .orElseThrow(() -> new NotFoundException(STATION));
        Station downStation = stationRepository.findById(request.getDownStationId())
            .orElseThrow(() -> new NotFoundException(STATION));

        Optional<Line> optionalLine = lineRepository.findByName(request.getName());
        if (optionalLine.isPresent()) {
            validate(upStation, downStation, optionalLine.get());

            optionalLine.get().addSection(upStation, downStation, request.getDistance());
            return LineResponse.of(optionalLine.get());
        }

        Line persistLine = lineRepository.save(request.toLine());
        persistLine.addSection(upStation, downStation, request.getDistance());
        return LineResponse.of(persistLine);
    }

    public LineResponse saveSection(LineRequest request, Long id) {
        Station newUpStation = stationRepository.findById(request.getUpStationId())
            .orElseThrow(() -> new NotFoundException(SECTION));
        Station newDownStation = stationRepository.findById(request.getDownStationId())
            .orElseThrow(() -> new NotFoundException(SECTION));

        Line line = lineRepository.findById(id).orElseThrow(() -> new NotFoundException(LINE));
        validateSection(line, newUpStation, newDownStation);

        Optional<LineResponse> optionalLineResponse = insertSection(request, newUpStation, newDownStation, line);
        if (optionalLineResponse.isPresent()) {
            return optionalLineResponse.get();
        }

        throw new DataIntegrityViolationException(NOT_DEFINED_REQUEST_EXCEPTION_STATEMENT);
    }

    private Optional<LineResponse> insertSection(final LineRequest request, final Station newUpStation, final Station newDownStation, final Line line) {
        Optional<Section> optionalSection;

        // CASE #1-A - 역 사이에 새로운 역을 등록할 경우(상행역 일치)
        optionalSection = line.findSectionByUpStationId(newUpStation.getId());
        if (optionalSection.isPresent()) {
            return Optional.of(insertBetweenStation(request.getDistance(), newDownStation, line, optionalSection.get()));
        }

        // CASE #1-B - 역 사이에 새로운 역을 등록할 경우(하행역 일치)
        optionalSection = line.findSectionByDownStationId(newDownStation.getId());
        if (optionalSection.isPresent()) {
            return Optional.of(insertBetweenStation(optionalSection.get().getDistance() - request.getDistance()
                , newUpStation
                , line
                , optionalSection.get())
            );
        }

        // CASE #2 - 새로운 역을 상행 종점으로 등록할 경우
        optionalSection = line.findSectionByUpStationId(newDownStation.getId());
        if (optionalSection.isPresent()) {
            return Optional.of(insertUpStationEnd(request, newUpStation, newDownStation, line, optionalSection.get()));
        }

        // CASE #3 - 새로운 역을 하행 종점으로 등록할 경우
        optionalSection = line.findSectionByDownStationId(newUpStation.getId());
        if (optionalSection.isPresent()) {
            return Optional.of(insertDownStationEnd(request, newUpStation, newDownStation, line));
        }

        return Optional.empty();
    }

    private void validateSection(Line line, final Station newUpStation, final Station newDownStation) {
        List<Long> stationIds = line.getStationIds();
        if (stationIds.contains(newUpStation.getId()) && stationIds.contains(newDownStation.getId())) {
            throw new DataIntegrityViolationException(ALREADY_UP_STATION_AND_DOWN_STATION_EXIST_EXCEPTION_STATEMENT);
        }

        if (!stationIds.contains(newUpStation.getId()) && !stationIds.contains(newDownStation.getId())) {
            throw new DataIntegrityViolationException(CANNOT_ADD_WITH_NONE_OF_UP_STATION_AND_DOWN_STATION_EXIST_EXCEPTION_STATEMENT);
        }
    }

    private void validateDistance(int oldDistance, int newDistance) {
        if (oldDistance <= newDistance) {
            throw new DataIntegrityViolationException(OLD_DISTANCE_IS_GRATER_EQUAL_THAN_NEW_DISTANCE_EXCEPTION_STATEMENT);
        }
    }

    private LineResponse insertBetweenStation(final int newDistance, final Station newStation, final Line line, final Section originalSection) {
        validateDistance(originalSection.getDistance(), newDistance);
        line.getSections().remove(originalSection);
        line.addSection(originalSection.getUpStation(), newStation, newDistance);
        line.addSection(newStation, originalSection.getDownStation(), originalSection.getDistance() - newDistance);
        return LineResponse.of(line);
    }

    private LineResponse insertUpStationEnd(final LineRequest request, final Station newUpStation, final Station newDownStation, final Line line, Section section) {
        line.getSections().remove(section);
        line.addSection(newUpStation, newDownStation, request.getDistance());
        line.addSection(newDownStation, section.getDownStation(), section.getDistance());
        return LineResponse.of(line);
    }

    private LineResponse insertDownStationEnd(final LineRequest request, final Station newUpStation, final Station newDownStation, final Line line) {
        line.addSection(newUpStation, newDownStation, request.getDistance());
        return LineResponse.of(line);
    }

    public void deleteSectionById(Long lineId, Long stationId) {
        Line line = getLineById(lineId);

        if (line.getSections().size() < SECTION_DATA_MIN_SIZE) {
            throw new DataIntegrityViolationException(CANNOT_DELETE_SECTION_WITH_DATA_SIZE_LESS_EQUAL_THAN_ONE_EXCEPTION_STATEMENT);
        }

        // Case #1 상행역 끝
        Long endUpStationId = line.getEndUpStationId();
        if (stationId.equals(endUpStationId)) {
            Section targetSection = line.findSectionByUpStationId(stationId).get();
            line.getSections().remove(targetSection);
            return;
        }

        // Case #2 하행역 끝
        Long endDownStationId = line.getEndDownStationId();
        if (stationId.equals(endDownStationId)) {
            Section targetSection = line.findSectionByDownStationId(stationId).get();
            line.getSections().remove(targetSection);
            return;
        }

        // Case #3 중간역
        if (line.getStationIds().contains(stationId)) {
            Section targetSection = line.findSectionByUpStationId(stationId).get();
            Section upperSectionFromTarget = line.findSectionByDownStationId(stationId).get();
            line.getSections().remove(targetSection);
            line.getSections().remove(upperSectionFromTarget);
            line.addSection(
                upperSectionFromTarget.getUpStation(),
                targetSection.getDownStation(),
                upperSectionFromTarget.getDistance() + targetSection.getDistance()
            );
            return;
        }

        throw new DataIntegrityViolationException(NOT_CANNOT_DELETE_SECTION_EXCEPTION_STATEMENT);
    }

    private void validate(Station upStation, Station downStation, Line line) {
        if (line.getStations().containsAll(Arrays.asList(upStation, downStation))) {
            throw new DataIntegrityViolationException(EQUAL_SECTION_EXIST_EXCEPTION_STATEMENT);
        }
    }
}
