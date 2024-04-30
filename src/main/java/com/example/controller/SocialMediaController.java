package com.example.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.entity.Account;
import com.example.entity.Message;
import com.example.service.AccountService;
import com.example.service.MessageService;
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

            /*Using ObjectMapper and JsonNode allows the controller method to extract specific data from the incoming JSON request body (jsonBody). 
            In this case, it's extracting the new message text to be updated in the message identified by messageId. 
            Once the new message text is extracted, it can be passed to the messageService for further processing, 
            such as updating the message in the database. */
            /*Passing the raw JSON body to the service layer tightly couples the controller to the specific JSON structure of the request. 
            If the JSON structure changes in the future, I would need to update both the controller and the service layer, 
            leading to maintenance overhead and potential bugs. */
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
