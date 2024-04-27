package com.example.service;

import com.example.entity.Message;
import com.example.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    // Post a new message
    public Message saveMessage(Message message) throws Exception {
        if (message.getMessageText() == null || message.getMessageText().trim().isEmpty()) {
            throw new Exception("Message text cannot be blank");
        }
        if (message.getPostedBy() == null) {
            throw new Exception("Message must have a valid user");
        }
        return messageRepository.save(message);
    }

    // Retrieve all messages
    public List<Message> getAllMessages() {
        return messageRepository.findAll();
    }

    // Retrieve messages by user ID
    public List<Message> getMessagesByUserId(Integer userId) {
        return messageRepository.findByPostedBy(userId);
    }

    // Retrieve a single message by ID
    public Optional<Message> getMessageById(Integer messageId) {
        return messageRepository.findById(messageId);
    }

    // Update a message
    public int updateMessage(Integer messageId, String newMessageText) throws Exception {
        Message message = messageRepository.findById(messageId)
            .orElseThrow(() -> new Exception("Message not found"));
    
        if (newMessageText == null || newMessageText.trim().isEmpty()) {
            throw new Exception("Message text cannot be blank");
        }
    
        message.setMessageText(newMessageText);
        messageRepository.save(message); // Assuming this is always successful
    
        return 1; // Indicating that one row was updated
    }
    

    // Delete a message
    public int deleteMessage(Integer messageId) {
        Optional<Message> message = messageRepository.findById(messageId);
        if (message.isPresent()) {
            messageRepository.delete(message.get());
            return 1;  // Assuming deletion was successful
        }
        return 0;  // No message found to delete
    }
    
}
