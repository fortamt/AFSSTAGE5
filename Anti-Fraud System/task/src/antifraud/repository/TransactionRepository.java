package antifraud.repository;

import antifraud.model.request.TransactionRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;
import java.util.List;

public interface TransactionRepository extends JpaRepository<TransactionRequest, Long> {

    List<TransactionRequest> findAllByNumberAndDateBetween(@NotEmpty String number, LocalDateTime start, LocalDateTime end);
    boolean existsByTransactionId(Long id);
    boolean existsByNumber(String id);
    TransactionRequest findByTransactionId(Long id);
    List<TransactionRequest> findAllByNumber(String number);
}

