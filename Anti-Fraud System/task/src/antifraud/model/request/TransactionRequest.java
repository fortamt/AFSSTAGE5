package antifraud.model.request;

import antifraud.model.RegionCodes;
import antifraud.model.validator.CreditCardConstraint;
import antifraud.model.validator.ValueOfEnum;
import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    Long transactionId;
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
    @ValueOfEnum(enumClass = RegionCodes.class)
    String region;
    LocalDateTime date;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    String result;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    String feedback ="";
}
