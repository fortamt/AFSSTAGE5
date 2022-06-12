package antifraud.model.request;

import antifraud.model.validator.CreditCardConstraint;
import antifraud.model.validator.RegionCodesConstraint;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "transactions")
public class TransactionRequest {
    @Id
    @GeneratedValue
    @JsonIgnore
    Long id;
    @NotNull
    @Positive
    Long amount;
    @NotEmpty
    @Pattern(regexp = "^((\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])\\.){3}(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])$")
    String ip;
    @NotEmpty
    @CreditCardConstraint
    String number;
    @NotEmpty
    @RegionCodesConstraint
    String region;
    LocalDateTime date;
}
