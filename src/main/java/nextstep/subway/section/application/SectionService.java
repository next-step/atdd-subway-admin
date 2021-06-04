package nextstep.subway.section.application;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.SectionRepository;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;

@Service
@Transactional
public class SectionService {
    private final SectionRepository sectionRepository;
    private final StationRepository stationRepository;

    public SectionService(SectionRepository sectionRepository, StationRepository stationRepository) {
        this.sectionRepository = sectionRepository;
        this.stationRepository = stationRepository;
    }

    public Section saveSection(SectionRequest request) {
        Section section = this.createSection(request);
        return sectionRepository.save(section);
    }

    private Section createSection(SectionRequest request) {
        Station upStation = this.findStationById(request.getUpStationId());
        Station downStation = this.findStationById(request.getDownStationId());
        return new Section(upStation, downStation, request.getDistance());
    }

    public Station findStationById(Long stationId) {
        return stationRepository.findById(stationId).orElseThrow(
            EntityNotFoundException::new);
    }
}
