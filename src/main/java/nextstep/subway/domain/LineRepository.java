package nextstep.subway.domain;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LineRepository extends JpaRepository<Line, Long> {
	@Override
	List<Line> findAll();

	@Modifying
	@Query(value = "UPDATE LINE l SET l.name = :name, l.color = :color WHERE l.id = :id", nativeQuery = true)
	int updateNameAndColor(@Param("name") String name, @Param("color") String color, @Param("id") Long id);
}
