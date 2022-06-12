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
//    List<TransactionRequest> findAllByDateAfter(LocalDateTime hourMinusOne);
//
//    @Query(nativeQuery = true, value="select * from transactions c where c.date between :start and :end")
//    List<TransactionRequest> getAllByTimeBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

//    List<TransactionRequest> findAllByNumberAndDateAfter(String number, LocalDateTime afterTime);
//    @Query("select m from TransactionRequest m where m.date >= :oneHourBefore")
//    List<TransactionRequest> findAllWithDateAfter(@Param("oneHourBefore") LocalDateTime oneHourBefore);
//
//    default List<TransactionRequest> getOneHourBefore() {
//        LocalDateTime oneHourBefore = LocalDateTime.now().minus(1, ChronoUnit.HOURS);
//        return findAllWithDateAfter(oneHourBefore);
//    }
}

