package antifraud.service;

import antifraud.model.Ip;
import antifraud.model.Result;
import antifraud.model.StolenCard;
import antifraud.model.User;
import antifraud.model.request.TransactionFeedback;
import antifraud.model.request.TransactionRequest;
import antifraud.model.response.TransactionResultResponse;
import antifraud.repository.IpRepository;
import antifraud.repository.StolenCardRepository;
import antifraud.repository.TransactionRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class TransactionService {

    private final String IPV4_REGEX = "(([0-1]?\\d{1,2}\\.)|(2[0-4]\\d\\.)|(25[0-5]\\.)){3}(([0-1]?\\d{1,2})|(2[0-4]\\d)|(25[0-5]))";
    private final Pattern IPV4_PATTERN = Pattern.compile(IPV4_REGEX);

    IpRepository ipRepository;
    StolenCardRepository stolenCardRepository;
    TransactionRepository transactionRepository;

    public TransactionResultResponse process(TransactionRequest request) {
        LocalDateTime localDateTime = request.getDate();
        TransactionResultResponse response = new TransactionResultResponse();
        transactionRepository.save(request);

        long regions = transactionRepository.findAllByNumberAndDateBetween(request.getNumber(), localDateTime.minusHours(1), localDateTime)
                .stream().map(TransactionRequest::getRegion).distinct().count();
        long ips = transactionRepository.findAllByNumberAndDateBetween(request.getNumber(), localDateTime.minusHours(1), localDateTime)
                .stream().map(TransactionRequest::getIp).distinct().count();

        if(stolenCardRepository.existsByNumber(request.getNumber())){
            response.setResult(Result.PROHIBITED);
            response.addInfo("card-number");
        }
        if(ipRepository.existsByIp(request.getIp())){
            response.setResult(Result.PROHIBITED);
            response.addInfo("ip");
        }
        if(regions == 3L){
            response.setResult(Result.MANUAL_PROCESSING);
            response.addInfo("region-correlation");
        } else if(regions > 3L){
            response.setResult(Result.PROHIBITED);
            response.addInfo("region-correlation");
        }
        if(ips == 3L){
            response.setResult(Result.MANUAL_PROCESSING);
            response.addInfo("ip-correlation");
        } else if(ips > 3L){
            response.setResult(Result.PROHIBITED);
            response.addInfo("ip-correlation");
        }
        if(request.getAmount() > 1500){
            response.setResult(Result.PROHIBITED);
            response.addInfo("amount");
        }

        if(response.getInfo().isEmpty()){
            if(request.getAmount() <= TransactionAmountChanger.ALLOWED){
                response.setResult(Result.ALLOWED);
                response.addInfo("none");
            } else if (request.getAmount() <= TransactionAmountChanger.MANUAL_PROCESSING) {
                response.setResult(Result.MANUAL_PROCESSING);
                response.addInfo("amount");
            } else if (request.getAmount() > TransactionAmountChanger.MANUAL_PROCESSING) {
                response.setResult(Result.PROHIBITED);
                response.addInfo("amount");
            }
        }
        request.setResult(response.getResult().name());
        transactionRepository.save(request);
        return response;
    }


    @Transactional
    public StolenCard addStolenCard(StolenCard stolenCard) {
        if (stolenCardRepository.existsByNumber(stolenCard.getNumber())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        } else {
            stolenCardRepository.save(stolenCard);
            return stolenCard;
        }
    }

    @Transactional
    public Map<String, String> deleteStolenCard(String number) {
        if (number.length() != 16) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        if (!stolenCardRepository.existsByNumber(number)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        } else {
            StolenCard stolenCard = stolenCardRepository.findByNumber(number);
            stolenCardRepository.delete(stolenCard);
            return Map.of("status", "Card " + stolenCard.getNumber() + " successfully removed!");
        }
    }

    @Transactional
    public List<StolenCard> listStolenCards() {
        return stolenCardRepository.findAll(
                Sort.sort(StolenCard.class).by(StolenCard::getId).ascending()
        );
    }

    public Optional<Ip> addSuspiciousIp(Ip ip) {
        if (ipRepository.existsByIp(ip.getIp())) {
            return Optional.empty();
        }
        return Optional.of(ipRepository.save(ip));
    }

    @Transactional
    public boolean deleteSuspiciousIp(String ip) {
        if (!isValidIPV4(ip)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        return ipRepository.deleteByIp(ip) == 1;
    }

    private boolean isValidIPV4(final String s) {
        return IPV4_PATTERN.matcher(s).matches();
    }

    public List<Ip> listSuspiciousAddresses() {
        return ipRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(Ip::getId))
                .collect(Collectors.toList());
    }

    public TransactionRequest feedbackProcess(TransactionFeedback feedback) {
        TransactionRequest transactionRequest;
        if(transactionRepository.existsByTransactionId(feedback.getTransactionId())){
            transactionRequest = transactionRepository.findByTransactionId(feedback.getTransactionId());
            if(!transactionRequest.getFeedback().isEmpty()){
                throw new ResponseStatusException(HttpStatus.CONFLICT);
            }
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        transactionRequest.setFeedback(feedback.getFeedback());
        TransactionAmountChanger.changeLimit(transactionRequest);
        transactionRepository.save(transactionRequest);
        return transactionRequest;
    }

    public List<TransactionRequest> history() {
        return transactionRepository.findAll(Sort.sort(TransactionRequest.class)
                        .by(TransactionRequest::getTransactionId)
                        .ascending());
    }

    public List<TransactionRequest> historyByCardNumber(String number) {
        if(!transactionRepository.existsByNumber(number)){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return transactionRepository.findAllByNumber(number);
    }
}