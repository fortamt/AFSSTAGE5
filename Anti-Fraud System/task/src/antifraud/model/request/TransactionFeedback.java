package antifraud.model.request;

import antifraud.model.Result;
import antifraud.model.validator.ValueOfEnum;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class TransactionFeedback {
    @NotNull
    Long transactionId;
    @NotEmpty
    @ValueOfEnum(enumClass = Result.class)
    String feedback;
}
