package antifraud.repository;

import antifraud.model.StolenCard;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StolenCardRepository extends JpaRepository<StolenCard, Long> {
    StolenCard findByNumber(String number);
    boolean existsByNumber(String number);
    StolenCard deleteStolenCardByNumber(String number);
}
