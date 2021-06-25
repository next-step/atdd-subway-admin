package nextstep.subway.line.application;

import nextstep.subway.exception.DuplicateSectionException;
import nextstep.subway.exception.InvalidateDistanceException;
import nextstep.subway.exception.NotContainSectionException;
import nextstep.subway.exception.NotExistLineException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.LinesResponse;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class LineService {

    private final LineRepository lineRepository;
    private final StationService stationService;

    public LineService(LineRepository lineRepository, StationService stationService) {
        this.lineRepository = lineRepository;
        this.stationService = stationService;
    }

    public LineResponse save(LineRequest request) {
        Station upStation = stationService.findById(request.getUpStationId());
        Station downStation = stationService.findById(request.getDownStationId());

        Section section = new Section(upStation, downStation, request.getDistance());

        return LineResponse.of(lineRepository.save(request.toLine(section)));
    }

    // 기존에 있는 Section에 새로운 Section을 추가한다.
    public LinesResponse findAll() {
        List<Line> lines = lineRepository.findAll();
        return LinesResponse.of(lines);
    }

    public LineResponse findById(Long id) {
        Line line = findByIdOrThrow(id);

        return LineResponse.of(line);
    }

    public void modify(Long id, LineRequest request) {
        Line line = findByIdOrThrow(id);
        line.update(request.toLine());
    }

    public Line findByIdOrThrow(Long id) {
        return lineRepository.findById(id)
                .orElseThrow(() -> new NotExistLineException(id));
    }

    public void deleteById(Long id) {
        lineRepository.deleteById(id);
    }

    public void addSections(long lineId, SectionRequest request) {
        Line line = findByIdOrThrow(lineId);

        Station newUpStation = stationService.findById(request.getUpStationId());
        Station newDownStation = stationService.findById(request.getDownStationId());

        List<Section> sections = line.getSections();

        for (Section section : sections) {
            long downStationId = request.getDownStationId();
            long upStationId = request.getUpStationId();

            validateDuplicateSection(newUpStation, newDownStation, section);

            validateSectionDistance(request, downStationId, upStationId, section);

            addSectionWhenSameUpStation(request, newUpStation, newDownStation, sections, section);

            addSectionWhenSameAnotherPosition(request, newUpStation, newDownStation, sections, section);
        }

        //상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음
        throw new NotContainSectionException();
    }

    // 요청한 하행과 기존 상행이 일치하는 경우에는 요청한 상행 + 요청한 하행과 기존 상행 + 기존 하행으로 총 2개 section이 생긴다.
    // 기존 하행과 요청된 상행이 일치하는 경우 기존 상행과 기존 하행 + 요청된 상행 + 요청된 하행으로 추가한다.
    private void addSectionWhenSameAnotherPosition(SectionRequest request, Station newUpStation, Station newDownStation, List<Section> sections, Section section) {
        if (section.getUpStation().equals(newDownStation) || section.getDownStation().equals(newUpStation)) {
            sections.add(new Section(newUpStation, newDownStation, request.getDistance()));
        }
    }

    // 일차하는 역이 둘 다 상행인 경우 해당 상행과 요청한 하행 + 상행(요청한 하행역)과 기존 하행으로 이뤄진 2개의 section으로 구성된다.
    private void addSectionWhenSameUpStation(SectionRequest request, Station newUpStation, Station newDownStation, List<Section> sections, Section section) {
        if (section.getUpStation().equals(newUpStation)) {
            sections.remove(section);
            sections.add(new Section(newUpStation, newDownStation, request.getDistance()));
            sections.add(new Section(newDownStation, section.getDownStation(), section.getDistance() - request.getDistance()));
        }
    }

    private void validateSectionDistance(SectionRequest request, long downStationId, long upStationId, Section section) {
        if (section.getDownStation().getId() == downStationId || section.getUpStation().getId() == upStationId) {
            if (section.getDistance() <= request.getDistance()) {
                throw new InvalidateDistanceException();
            }
        }
    }

    private void validateDuplicateSection(Station newUpStation, Station newDownStation, Section section) {
        if (section.getDownStation().equals(newDownStation) && section.getUpStation().equals(newUpStation)) {
            throw new DuplicateSectionException();
        }
    }
}
