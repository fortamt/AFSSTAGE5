package antifraud.controller;

import antifraud.model.Ip;
import antifraud.model.StolenCard;
import antifraud.model.request.TransactionRequest;
import antifraud.model.response.TransactionResultResponse;
import antifraud.service.TransactionService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/antifraud")
@AllArgsConstructor
public class TransactionController {
    TransactionService transactionService;

    @PostMapping("/transaction")
    @ResponseStatus(HttpStatus.OK)
    TransactionResultResponse transactionPost(@Valid @RequestBody TransactionRequest request) {
        return transactionService.process(request);
    }

    @PostMapping("/stolencard")
    @ResponseStatus(HttpStatus.OK)
    StolenCard addStolenCard(@Valid @RequestBody StolenCard stolenCard) {
        return transactionService.addStolenCard(stolenCard);
    }

    @DeleteMapping("/stolencard/{number}")
    @ResponseStatus(HttpStatus.OK)
    Map<String, String> deleteStolenCard(@PathVariable String number) {
        return transactionService.deleteStolenCard(number);
    }

    @GetMapping("/stolencard")
    @ResponseStatus(HttpStatus.OK)
    List<StolenCard> listStolenCards() {
        return transactionService.listStolenCards();
    }

    @PostMapping("/suspicious-ip")
    @ResponseStatus(HttpStatus.OK)
    Ip saveSuspiciousIp(@Valid @RequestBody Ip ip) {
        return transactionService.addSuspiciousIp(ip)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.CONFLICT));
    }

    @DeleteMapping("/suspicious-ip/{ip}")
    @ResponseStatus(HttpStatus.OK)
    Map<String, String> deleteSuspiciousIp(@PathVariable("ip") String ip) {
        if (transactionService.deleteSuspiciousIp(ip)) {
            return Map.of(
                    "status", "IP " + ip + " successfully removed!"
            );
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/suspicious-ip")
    @ResponseStatus(HttpStatus.OK)
    List<Ip> listSuspiciousAddresses() {
        return transactionService.listSuspiciousAddresses();
    }
}
