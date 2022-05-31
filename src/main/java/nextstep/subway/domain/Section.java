package nextstep.subway.domain;

import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class Section extends BaseEntity {
	private static final int MIN_DISTANCE = 0;
	private static final int INIT_SECTION_ORDER = 0;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "up_station_id")
	private Station upStation;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "down_station_id")
	private Station downStation;

	private int distance;

	private int sectionOrder;
	
	protected Section() {
	}
	
	public Section(Station upStation, Station downStation, int distance) {
		validation(upStation, downStation, distance);
		
		this.upStation = upStation;
		this.downStation = downStation;
		this.distance = distance;
		this.sectionOrder = INIT_SECTION_ORDER;
	}

	private void validation(Station upStation, Station downStation, int distance) {
		if(Objects.isNull(upStation) || Objects.isNull(downStation)) {
			throw new NullPointerException("역 정보가 없습니다.");
		}
		
		if(upStation.equals(downStation)) {
			throw new IllegalArgumentException("같은 역이 입력되었습니다.");
		}
		
		if(distance < MIN_DISTANCE) {
			throw new IllegalArgumentException("길이는 0이상이여야 합니다.");
		}
	}

	protected boolean upStationEquals(Station station) {
		return upStation.equals(station);
	}
	
	protected boolean downStationEquals(Station station) {
		return downStation.equals(station);
	}
	
	private boolean isBigDistance(int distance) {
		return this.distance > distance;
	}
	
	protected boolean isAddNextSection(Section section) {
		return upStationEquals(section.getUpStation()) && 
				!downStationEquals(section.getDownStation()) && 
				isBigDistance(section.getDistance());
	}

	public void addNextSection(Section section) {
		this.distance -= section.getDistance();
		this.upStation = section.downStation;
		this.sectionOrder += 1;
	}

	public void addCompleted(int index) {
		this.sectionOrder = index;
	}	

	public Long getId() {
		return id;
	}
	
	public Station getUpStation() {
		return upStation;
	}
	
	public Station getDownStation() {
		return downStation;
	}

	public int getDistance() {
		return distance;
	}

	public int getSectionOrder() {
		return sectionOrder;
	}
}
