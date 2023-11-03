package br.com.projetodifm.services;

public interface EmailService {

    void sendEmailVerification(String to, String body);
}
