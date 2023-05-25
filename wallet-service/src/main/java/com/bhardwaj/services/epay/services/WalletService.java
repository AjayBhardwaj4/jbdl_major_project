package com.bhardwaj.services.epay.services;

import com.bhardwaj.services.epay.models.UserIdentifier;
import com.bhardwaj.services.epay.models.Wallet;
import com.bhardwaj.services.epay.models.WalletUpdateStatus;
import com.bhardwaj.services.epay.repositories.WalletRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import static com.bhardwaj.services.epay.constants.CommonConstants.*;

@Service
public class WalletService {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    WalletRepository walletRepository;

    @Autowired
    KafkaTemplate<String, String> kafkaTemplate;

    @KafkaListener(topics = {USER_CREATION_TOPIC}, groupId = LISTENER_GROUP) // Need to specify group for SDKConsumers
    public void createWaller(String msg) throws ParseException {
        JSONObject data = (JSONObject) new JSONParser().parse(msg);

        String phoneNumber = (String) data.get(USER_CREATION_TOPIC_PHONE_NUMBER);
        Long userId = Long.valueOf(data.get(USER_CREATION_TOPIC_USERID).toString());
        String identifierKey = (String) data.get(USER_CREATION_TOPIC_IDENTIFIER_KEY);
        String identifierValue = (String) data.get(USER_CREATION_TOPIC_IDENTIFIER_VALUE);

        Wallet wallet = Wallet.builder()
                .userId(userId)
                .phoneNumber(phoneNumber)
                .userIdentifier(UserIdentifier.valueOf(identifierKey))
                .identifierValue(identifierValue)
                .balance(10.0)
                .build();

        walletRepository.save(wallet);
    }

    @KafkaListener(topics = TRANSACTION_CREATION_TOPIC, groupId = LISTENER_GROUP)
    public void updateWalletForTxn(String msg) throws ParseException, JsonProcessingException {
        JSONObject data = (JSONObject) new JSONParser().parse(msg);

        String sender = (String) data.get(SENDER);
        String receiver = (String) data.get(RECEIVER);
        Double amount = (Double) data.get(AMOUNT);
        String transactionId = (String) data.get(TRANSACTION);

        Wallet senderWallet = walletRepository.findByPhoneNumber(sender);
        Wallet receiverWallet = walletRepository.findByPhoneNumber(receiver);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put(TRANSACTION, transactionId);
        jsonObject.put(SENDER, sender);
        jsonObject.put(RECEIVER, receiver);
        jsonObject.put(AMOUNT, amount);

        //escrow account || fail if receiverWaller == null
        if(senderWallet == null || receiverWallet == null || senderWallet.getBalance() < amount) {
            jsonObject.put(WALLET_UPDATE_STATUS, WalletUpdateStatus.FAILED);
            kafkaTemplate.send(WALLET_UPDATION_TOPIC, objectMapper.writeValueAsString(jsonObject));
            return;
        }

        walletRepository.updateWallet(receiver, amount);
        walletRepository.updateWallet(sender, 0 - amount);

        jsonObject.put(WALLET_UPDATE_STATUS, WalletUpdateStatus.SUCCESSFULL);
        kafkaTemplate.send(WALLET_UPDATION_TOPIC, objectMapper.writeValueAsString(jsonObject));
    }
}
