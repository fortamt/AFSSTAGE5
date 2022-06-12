package antifraud.model.response;

import antifraud.model.Result;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Getter
@Setter
public class TransactionResultResponse {
    private Result result;
    private Set<String> info = new HashSet<>();
    public TransactionResultResponse() {}

    public void addInfo(String s){
        info.add(s);
    }

    public String getInfo() {
        return String.join(", ", info);
    }

}
