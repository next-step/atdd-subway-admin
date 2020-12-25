package nextstep.subway.line.application;

import nextstep.subway.common.exception.NotExistsLineIdException;
import nextstep.subway.common.exception.NotExistsStationIdException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.domain.Section;
import nextstep.subway.line.domain.SectionRepository;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class SectionService {
    private LineRepository lineRepository;
    private SectionRepository sectionRepository;
    private StationRepository stationRepository;

    public SectionService(LineRepository lineRepository, SectionRepository sectionRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.sectionRepository = sectionRepository;
        this.stationRepository = stationRepository;
    }

    public void saveSection(SectionRequest request) {
        Line line = lineRepository.findById(request.getLineId())
                .orElseThrow(() -> new NotExistsLineIdException(request.getLineId()));
        Station upStation = stationRepository.findById(request.getUpStationId())
                .orElseThrow(() -> new NotExistsStationIdException(request.getUpStationId()));
        Station downStation = stationRepository.findById(request.getDownStationId())
                .orElseThrow(() -> new NotExistsStationIdException(request.getDownStationId()));
        sectionRepository.save(new Section(line, upStation, downStation, request.getDistance()));
    }

    public void deleteAllByLineId(Long lineId) {
        List<Section> sections = sectionRepository.findByLineId(lineId);
        sectionRepository.deleteAll(sections);
    }
}
