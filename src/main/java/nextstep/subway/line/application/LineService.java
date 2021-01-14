package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.domain.Section;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.station.domain.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class LineService {
    private final LineRepository lineRepository;

    private final StationRepository stationRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    public LineResponse saveLine(LineRequest request) {
        Line persistLine = lineRepository.save(request.toLine());

        if (request.getUpStation() != null && request.getDownStation() != null) {
            //세션정보 입력
            Section section = new Section(request.getUpStation(), request.getDownStation(), request.getDistance());
            persistLine.addSection(section);
        }
        final Line result = lineRepository.save(persistLine);
        return LineResponse.of(result);
    }

    // 비즈니스 로직 도메인으로 이동 인자만 전달
    public LineResponse addSection(Long id, SectionRequest sectionRequest) {
        Line line = lineRepository.findById(id).orElseThrow(() -> new IllegalArgumentException());
        Section newSection = sectionRequest.toSection();
        line.addSection(newSection);
        return LineResponse.of(line);
    }


    public LineResponse saveSection(Long id, SectionRequest sectionRequest) {
        List<Section> newSection = new ArrayList<>();
        Line targetLine = lineRepository.findById(id).orElseThrow(() -> new IllegalArgumentException());

        //새로 생길 구간 추가
        if (targetLine.getSections().size() == 0) {
            targetLine.addSection(sectionRequest.toSection());
            return LineResponse.of(lineRepository.save(targetLine));
        }

        if (targetLine.getSections().size() >= 1) {
            extractSection(sectionRequest, newSection, targetLine);
        }

        targetLine.changeSection(newSection);
        final Line line = lineRepository.save(targetLine);
        return LineResponse.of(line);
    }

    private void extractSection(SectionRequest sectionRequest, List<Section> newSection, Line targetLine) {
        for (Section sectionValue: targetLine.getSections()) {
            int newDistance = 0;
            if (betweenStations(sectionRequest, newSection, sectionValue)) continue;
            if (baseUpStation(sectionRequest, newSection, sectionValue)) continue;
            if (baseDownStation(sectionRequest, newSection, sectionValue)) continue;
            newSection.add(sectionValue);
        }
    }

    private boolean baseDownStation(SectionRequest sectionRequest, List<Section> newSection, Section sectionValue) {
        int newDistance;//새로운 역의 상행을 기존 노선 하행 종점으로 등록
        if (sectionValue.getDownStation() == sectionRequest.getUpStation()) {
            newDistance = sectionRequest.getDistance();
            Section section1 = new Section(sectionValue.getUpStation(), sectionValue.getDownStation(), sectionValue.getDistance());
            Section section2 = new Section(sectionRequest.getUpStation(), sectionRequest.getDownStation(), newDistance);
            newSection.add(section1);
            newSection.add(section2);
            return true;
        }
        return false;
    }

    private boolean baseUpStation(SectionRequest sectionRequest, List<Section> newSection, Section sectionValue) {
        int newDistance;//새로운 역의 하행을 기존 노선 상행 종점으로 등록
        if (sectionValue.getUpStation() == sectionRequest.getDownStation()) {
            newDistance = sectionRequest.getDistance();
            Section section1 = new Section(sectionRequest.getUpStation(), sectionRequest.getDownStation(), newDistance);
            Section section2 = new Section(sectionValue.getUpStation(), sectionValue.getDownStation(), sectionValue.getDistance());
            newSection.add(section1);
            newSection.add(section2);
            return true;
        }
        return false;
    }

    private boolean betweenStations(SectionRequest sectionRequest, List<Section> newSection, Section sectionValue) {
        int newDistance;//역사이에 새로운 역을 등록 기존의 상행역과 같음
        if (sectionValue.getUpStation() == sectionRequest.getUpStation() && sectionValue.getDownStation() != sectionRequest.getDownStation() && sectionValue.getDistance() > sectionRequest.getDistance()) {
            newDistance = sectionValue.getDistance() - sectionRequest.getDistance();
            Section section1 = new Section(sectionValue.getUpStation(), sectionRequest.getDownStation(), sectionRequest.getDistance());
            Section section2 = new Section(sectionRequest.getDownStation(), sectionValue.getDownStation(), newDistance);
            newSection.add(section1);
            newSection.add(section2);
            return true;
        }

        //역사이에 새로운 역을 등록 기존의 하행역과 같음
        if (sectionValue.getDownStation() == sectionRequest.getDownStation() && sectionValue.getUpStation() != sectionRequest.getUpStation() && sectionValue.getDistance() > sectionRequest.getDistance()) {
            newDistance = sectionValue.getDistance() - sectionRequest.getDistance();
            Section section1 = new Section(sectionValue.getUpStation(), sectionRequest.getUpStation(), newDistance);
            Section section2 = new Section(sectionRequest.getUpStation(), sectionRequest.getDownStation(), sectionRequest.getDistance());
            newSection.add(section1);
            newSection.add(section2);
            return true;
        }
        return false;
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();

        return lines.stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
    }

    public LineResponse findById(Long id) {
        Line line = lineRepository.findById(id).orElseThrow(() -> new IllegalArgumentException());
        return LineResponse.of(line);
    }

    public LineResponse updateLineById(Long id, LineRequest request) {
        Line line = lineRepository.findById(id).orElseThrow(() -> new IllegalArgumentException());
        line.changeLine(request.getName(), request.getColor());
        Line persistLine = lineRepository.save(line);
        return LineResponse.of(persistLine);
    }

    public void deleteLineById(Long id) {
        lineRepository.deleteById(id);
    }
}
