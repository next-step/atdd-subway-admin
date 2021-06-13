package nextstep.subway.line.application;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import nextstep.subway.section.domain.Sections;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.SectionRepository;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;

@Service
@Transactional
public class LineService {
    private LineRepository lineRepository;
    private StationRepository stationRepository;
    private SectionRepository sectionRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository, SectionRepository sectionRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
        this.sectionRepository = sectionRepository;
    }

    public LineResponse saveLine(LineRequest request) {
        try {
            return lineRepository.save(makeLine(request)).toLineResponse();
        } catch (DataIntegrityViolationException exception) {
            throw new DuplicateKeyException("노선 생성에 실패했습니다. 이미 존재하는 노선입니다.");
        }
    }

    public List<LineResponse> findAllLines() {
        return lineRepository.findAll()
                .stream()
                .map(Line::toLineResponse)
                .collect(Collectors.toList());
    }

    public LineResponse findLineById(long id) throws NoSuchElementException {
        return findLineByIdOrThrow(id, "노선이 존재하지 않습니다.").toLineResponse();
    }

    public void updateLine(long id, LineRequest updateLineRequest) {
        Line line = findLineByIdOrThrow(id, "수정 대상 노선이 존재하지 않습니다.");
        if (lineRepository.findByName(updateLineRequest.getName()).isPresent()) {
            throw new DuplicateKeyException("동일한 이름의 노선이 존재합니다.");
        }
        line.update(updateLineRequest.toLine());
    }

    public void deleteLineById(Long id) {
        Line line = findLineByIdOrThrow(id, "삭제 대상 노선이 존재하지 않습니다.");
        sectionRepository.deleteAllByLineId(id);
        lineRepository.delete(line);
    }

    private Line makeLine(LineRequest request) {
        Station upStation = this.stationRepository.findById(request.getUpStationId())
                .orElseThrow(() -> new NoSuchElementException("요청한 상행역은 등록되지 않은 역입니다. 역 ID : "
                        + request.getUpStationId()));
        Station downStation = this.stationRepository.findById(request.getDownStationId())
                .orElseThrow(() -> new NoSuchElementException("요청한 하행역은 등록되지 않은 역입니다. 역 ID : "
                        + request.getDownStationId()));
        return new Section(upStation, downStation, request.getDistance(), request.toLine()).getLine();
    }

    public LineResponse appendNewSectionToLine(long id, SectionRequest sectionRequest) {
        Line line = findLineByIdOrThrow(id, "조회된 노선이 없습니다.");
        Sections sections = line.createSections();
        Section section = makeSection(sectionRequest.getUpStationId(), sectionRequest.getDownStationId(),
                sectionRequest.getDistance(), line);
        sectionRepository.save(sections.addSection(section));
        return line.toLineResponse();
    }

    public void removeSectionByStationId(Long lineId, Long stationId) {
        List<Section> findSections = findSectionByLineIdWithValidate(lineId);
        Station station = findStationByIdOrThrow(stationId, "등록된 역이 아닙니다.");
        Section removedSection = new Sections(findSections).removeSectionByStation(station);
        try {
            sectionRepository.delete(removedSection);
        } catch (DataIntegrityViolationException exception) {
            throw new NoSuchElementException("구간 삭제 과정에서 에러가 발생했습니다.");
        }
    }

    private List<Section> findSectionByLineIdWithValidate(Long lineId) {
        List<Section> findSections = sectionRepository.findByLineId(lineId);
        validateResultSections(findSections);
        return findSections;
    }

    private void validateResultSections(List<Section> findSections) {
        if (findSections.isEmpty()) {
            throw new NoSuchElementException("노선이 존재하지 않습니다.");
        }
    }

    private Section makeSection(Long upStationId, Long downStationId, int distance, Line line) {
        Station upStation = findStationByIdOrThrow(upStationId, "요청한 상행역은 등록되지 않은 역입니다.");
        Station downStation = findStationByIdOrThrow(downStationId, "요청한 하행역은 등록되지 않은 역입니다.");
        return new Section(upStation, downStation, distance, line);
    }

    private Station findStationByIdOrThrow(Long stationId, String throwMessage) {
        return this.stationRepository.findById(stationId)
                .orElseThrow(() -> new NoSuchElementException(throwMessage + " 역 ID : " + stationId));
    }

    private Line findLineByIdOrThrow(Long id, String throwMessage) {
        return lineRepository.findById(id).orElseThrow(() -> new NoSuchElementException(throwMessage));
    }
}
