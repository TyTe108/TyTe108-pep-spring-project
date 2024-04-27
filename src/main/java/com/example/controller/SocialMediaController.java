package com.example.controller;

import com.example.entity.Account;
import com.example.entity.Message;
import com.example.service.AccountService;
import com.example.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.databind.JsonNode;  // Used for navigating and extracting data from JSON trees
import com.fasterxml.jackson.databind.ObjectMapper;  // Used for parsing JSON data and converting it to/from Java objects


@RestController
@RequestMapping("/")
public class SocialMediaController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private MessageService messageService;

    // Endpoint to register a new user
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody Account account) {
        try {
            Account registeredAccount = accountService.register(account);
            return ResponseEntity.ok(registeredAccount);
        } catch (Exception e) {
            if (e.getMessage().contains("Username already exists")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
            }
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Endpoint to login a user
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody Account account) {
        try {
            Account loggedInAccount = accountService.login(account.getUsername(), account.getPassword());
            return ResponseEntity.ok(loggedInAccount);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    // Endpoint to post a new message
    @PostMapping("/messages")
    public ResponseEntity<?> postMessage(@RequestBody Message message) {
        try {
            Message savedMessage = messageService.saveMessage(message);
            return ResponseEntity.ok(savedMessage);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Endpoint to get all messages
    @GetMapping("/messages")
    public ResponseEntity<List<Message>> getAllMessages() {
        List<Message> messages = messageService.getAllMessages();
        return ResponseEntity.ok(messages);
    }

    // Endpoint to get a message by ID
    @GetMapping("/messages/{messageId}")
    public ResponseEntity<?> getMessageById(@PathVariable Integer messageId) {
        Optional<Message> messageOptional = messageService.getMessageById(messageId);
        if (messageOptional.isPresent()) {
            return ResponseEntity.ok(messageOptional.get());
        } else {
            //return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Message not found");
            // To meet test expectations, return HTTP 200 with an empty body
            return ResponseEntity.ok().body("");  // Returning an empty string as the body
            /*Normally, HTTP 404 is appropriate as it correctly indicates that the requested resource was not found. I disagree with this implementation */
        }
    }

    // Endpoint to delete a message by ID
    @DeleteMapping("/messages/{messageId}")
    public ResponseEntity<?> deleteMessage(@PathVariable Integer messageId) {
        int rowsDeleted = messageService.deleteMessage(messageId);
        if (rowsDeleted == 0) {
            // Test expects a 200 OK even if nothing was deleted, with an empty body
            return ResponseEntity.ok().body("");
        } else {
            // When rows are deleted, return the count
            return ResponseEntity.ok(rowsDeleted);
        }
    }
    

    // Endpoint to update a message by ID
    @PatchMapping("/messages/{messageId}")
    public ResponseEntity<?> updateMessage(@PathVariable Integer messageId, @RequestBody String jsonBody) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(jsonBody);
            String newMessageText = jsonNode.get("messageText").asText();
    
            // Assuming the updateMessage method in the service layer returns the number of updated records
            int rowsUpdated = messageService.updateMessage(messageId, newMessageText);
            return ResponseEntity.ok(rowsUpdated);  // Returns the number of rows updated
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Endpoint to get all messages posted by a specific user
    @GetMapping("/accounts/{accountId}/messages")
    public ResponseEntity<List<Message>> getMessagesByUserId(@PathVariable Integer accountId) {
        List<Message> messages = messageService.getMessagesByUserId(accountId);
        return ResponseEntity.ok(messages);
    }
}
