package co.com.srdejo.visionarios.modules.events;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface EventRepository extends JpaRepository<Event, UUID> {

    List<Event> findAllByOrderByStartsAtAsc();

    List<Event> findByStartsAtAfterOrderByStartsAtAsc(Instant now);
}
