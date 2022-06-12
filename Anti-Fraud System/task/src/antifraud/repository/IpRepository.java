package antifraud.repository;

import antifraud.model.Ip;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IpRepository extends JpaRepository<Ip, Long> {
    Optional<Ip> findByIp(String ip);
    boolean existsByIp(String ip);
    int deleteByIp(String ip);

}
