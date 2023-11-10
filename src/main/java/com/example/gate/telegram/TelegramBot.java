package com.example.gate.telegram;

import com.example.data.data.*;
import com.example.data.database.Database;
import com.example.data.services.BikeService;
import com.example.data.services.CityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TelegramBot extends TelegramLongPollingBot implements AdminNotification {
    public static final int LANGUAGE = -1;
    public static final int MAIN = 0;
    public static final int ADMIN = 100;
    public static final int REPORT = 2;


    private final Database database;
    private final DialogText dialogText;
    private final DialogTextWithLocalization withLocalization;

    private final BikeService bikeService;
    private final CityService cityService;

    @Value("${rest-api.bikes}")
    private String bikeMapping;

    @Value("${rest-api.cities}")
    private String cityMapping;

    @Value("${telegram.password}")
    private String password;

    @Value("${telegram.botName}")
    private String botName;

    @Autowired
    public TelegramBot(@Value("${telegram.key}") String botToken, Database database, DialogText dialogText, DialogTextWithLocalization withLocalization, BikeService bikeService, CityService cityService) {
        super(botToken);
        this.database = database;
        this.dialogText = dialogText;
        this.withLocalization = withLocalization;
        this.bikeService = bikeService;
        this.cityService = cityService;
    }

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public void onUpdateReceived(Update update) {
        long chatId = update.getMessage().getChatId();
        User user = database.getUserInfo(chatId);

        if(user == null) {
            var newUser = new User();
            newUser.setId(0);
            newUser.setUserId(chatId);
            newUser.setDialogPart(TelegramBot.LANGUAGE);
            newUser.setAdmin(false);
            newUser.setLanguage(null);

            database.createNewUser(newUser);
            sendLanguageChoice(newUser);
        } else {
            switch (user.getDialogPart()) {
                case TelegramBot.LANGUAGE -> checkLanguageChoice(update, user);
                case TelegramBot.MAIN -> checkMainHello(update, user);
                case TelegramBot.ADMIN -> checkAdminPassword(update, user);
                case TelegramBot.REPORT -> checkReportInfo(update, user);
            }
        }
    }

    // LANGUAGE
    private void sendLanguageChoice(User user) {
        String text = dialogText.getText("LanguageHello");
        List<String> buttonsText = withLocalization.getAllTexts("LanguageHelloButtons");

        var keyboard = createKeyboard(buttonsText);
        var message = createMessage(user.getUserId(), text, keyboard);

        user.setDialogPart(TelegramBot.LANGUAGE);
        database.setDialogPart(user);

        try {
            sendMessage(message);
        } catch (TelegramApiException e) {
            database.deleteUser(user);
            log.error("Exception. ", e);
        }
    }

    private void checkLanguageChoice(Update update, User user) {
        String answer = update.getMessage().getText();

        if (withLocalization.getAllTexts("LanguageHelloButtons").contains(answer)) {
            var lang = withLocalization.getText("Languages", answer);
            user.setLanguage(lang);
            database.setUserLanguage(user);

            sendMainHello(user);
        } else {
            var lang = user.getLanguage();
            var text = withLocalization.getText("BadAnswer", "en");
            var message = createMessage(user.getUserId(), text, null);

            try {
                sendMessage(message);
                sendLanguageChoice(user);
            } catch (TelegramApiException e) {
                database.deleteUser(user);
                log.error("Exception. ", e);
            }
        }
    }

    // MAIN
    private void sendMainHello(User user) {
        var lang = user.getLanguage();
        var text = withLocalization.getText("MainHello", lang);

        var getBikesButtonText = withLocalization.getText("GetBikesButtons", lang);
        var getCitiesButtonText = withLocalization.getText("GetCitiesButtons", lang);
        var createApplicationButtonText = withLocalization.getText("CreateApplicationButtons", lang);
        var createReportButtonText = withLocalization.getText("CreateReportButtons", lang);
        var changeLanguageButtonText = withLocalization.getText("ChangeLanguageButtons", lang);

        List<String> buttonsText = Arrays.asList(getBikesButtonText, getCitiesButtonText, createApplicationButtonText, createReportButtonText, changeLanguageButtonText);

        var keyboard = createKeyboard(buttonsText);
        var message = createMessage(user.getUserId(), text, keyboard);

        user.setDialogPart(TelegramBot.MAIN);
        database.setDialogPart(user);

        try {
            sendMessage(message);
        } catch (TelegramApiException e) {
            database.deleteUser(user);
            log.error("Exception. ", e);
        }
    }

    private void checkMainHello(Update update, User user) {
        var answer = update.getMessage().getText();
        var lang = user.getLanguage();

        var getBikesButtonText = withLocalization.getText("GetBikesButtons", lang);
        var getCitiesButtonText = withLocalization.getText("GetCitiesButtons", lang);
        var createApplicationButtonText = withLocalization.getText("CreateApplicationButtons", lang);
        var createReportButtonText = withLocalization.getText("CreateReportButtons", lang);
        var changeLanguageButtonText = withLocalization.getText("ChangeLanguageButtons", lang);

        if(answer.equals(getBikesButtonText))
            sendBikesInfo(user);
        else if (answer.equalsIgnoreCase(getCitiesButtonText))
            sendCitiesInfo(user);
        else if (answer.equalsIgnoreCase(createApplicationButtonText))
            sendWhereCreateApplication(user);
        else if (answer.equalsIgnoreCase(createReportButtonText))
            sendReportInfo(user);
        else if (answer.equalsIgnoreCase(changeLanguageButtonText))
            sendLanguageChoice(user);
        else if (answer.equalsIgnoreCase("/admin"))
            sendAdminHello(user);
        else {
            var text = withLocalization.getText("BadAnswer", lang);
            var message = createMessage(user.getUserId(), text, null);

            try {
                sendMessage(message);
                sendMainHello(user);
            } catch (TelegramApiException e) {
                database.deleteUser(user);
                log.error("Exception. ", e);
            }
        }
    }

    // ADMIN
    private void sendAdminHello(User user) {
        var lang = user.getLanguage();
        var text = withLocalization.getText("AdminHello", lang);

        var message = createMessage(user.getUserId(), text, null);

        user.setDialogPart(TelegramBot.ADMIN);
        database.setDialogPart(user);

        try {
            sendMessage(message);
        } catch (TelegramApiException e) {
            database.deleteUser(user);
            log.error("Exception. ", e);
        }
    }

    private void checkAdminPassword(Update update, User user) {
        var answer = update.getMessage().getText();
        var lang = user.getLanguage();

        if(answer.equals(password)) {
            var text = withLocalization.getText("GoodAdminPassword", lang);
            var message = createMessage(user.getUserId(), text, null);

            database.addAdmin(user);

            try {
                sendMessage(message);
            } catch (TelegramApiException e) {
                database.deleteUser(user);
                log.error("Exception. ", e);
            }

            sendMainHello(user);
        } else {
            var text = withLocalization.getText("BadAdminPassword", lang);
            var message = createMessage(user.getUserId(), text, null);

            try {
                sendMessage(message);
            } catch (TelegramApiException e) {
                database.deleteUser(user);
                log.error("Exception. ", e);
            }

            sendMainHello(user);
        }
    }

    // BIKES
    private void sendBikesInfo(User user) {
        List<Bike> bikeList = bikeService.getBikeList();
        var lang = user.getLanguage();
        var text = bikeList.stream().map(b -> b.toString(lang)).collect(Collectors.joining());
        var message = createMessage(user.getUserId(), text, null);

        try {
            sendMessage(message);
        } catch (TelegramApiException e) {
            database.deleteUser(user);
            log.error("Exception. ", e);
        }

        sendMainHello(user);
    }

    // CITIES
    private void sendCitiesInfo(User user) {
        List<City> cityList = cityService.getCityList();
        var text = cityList.toString();
        var message = createMessage(user.getUserId(), text, null);

        try {
            sendMessage(message);
        } catch (TelegramApiException e) {
            database.deleteUser(user);
            log.error("Exception. ", e);
        }

        sendMainHello(user);
    }

    // APPLICATION
    private void sendWhereCreateApplication(User user) {
        var lang = user.getLanguage();
        var text = withLocalization.getText("CreateApplication", lang);
        var message = createMessage(user.getUserId(), text, null);

        try {
            sendMessage(message);
        } catch (TelegramApiException e) {
            database.deleteUser(user);
            log.error("Exception. ", e);
        }

        sendMainHello(user);
    }

    // REPORT
    private void sendReportInfo(User user) {
        var lang = user.getLanguage();
        var text = withLocalization.getText("CreateReport", lang);

        var message = createMessage(user.getUserId(), text, null);

        user.setDialogPart(TelegramBot.REPORT);
        database.setDialogPart(user);

        try {
            sendMessage(message);
        } catch (TelegramApiException e) {
            database.deleteUser(user);
            log.error("Exception. ", e);
        }
    }

    private void checkReportInfo(Update update, User user) {
        var answer = update.getMessage().getText();
        var lang = user.getLanguage();

        var textToAdmin = new StringBuilder();
        textToAdmin.append("Getting new report from user: @").append(update.getMessage().getChat().getUserName()).append("\n");
        textToAdmin.append("Report text:\n").append(answer);

        sendMessageToAllAdmins(textToAdmin.toString());



        var text = withLocalization.getText("GetReport", lang);
        var message = createMessage(user.getUserId(), text, null);

        try {
            sendMessage(message);
        } catch (TelegramApiException e) {
            database.deleteUser(user);
            log.error("Exception. ", e);
        }

        sendMainHello(user);

    }



    // MESSAGES
    private void sendMessage(SendMessage message) throws TelegramApiException {
        this.execute(message);
    }

    private SendMessage createMessage(long chatId, String text, List<KeyboardRow> keyboardRows) {
        SendMessage message = new SendMessage();

        message.enableHtml(true);
        message.enableMarkdown(true);
        message.disableWebPagePreview();
        message.setChatId(chatId);
        message.setText(text);

        if(keyboardRows != null) {
            var replyKeyboardMarkup = new ReplyKeyboardMarkup();
            replyKeyboardMarkup.setKeyboard(keyboardRows);
            replyKeyboardMarkup.setSelective(true);

            message.setReplyMarkup(replyKeyboardMarkup);
        } else {
            var keyboardMarkup = new ReplyKeyboardRemove();
            keyboardMarkup.setRemoveKeyboard(true);
            keyboardMarkup.setSelective(true);

            message.setReplyMarkup(keyboardMarkup);
        }

        return message;
    }

    private List<KeyboardRow> createKeyboard(List<String> buttons) {
        List<KeyboardRow> keyboard = new ArrayList<>();

        for(String button: buttons) {
            var row = new KeyboardRow(Collections.singleton(new KeyboardButton(button)));
            keyboard.add(row);
        }

        return keyboard;
    }

    // ADMIN NOTIFICATION
    @Override
    public void sendMessageToAllAdmins(String text) {
        List<User> adminList = database.getAdminList();

        for(User admin: adminList) {
            SendMessage message = createMessage(admin.getUserId(), text, null);

            try {
                sendMessage(message);
            } catch (TelegramApiException e) {
                log.error("Exception", e);
            }
        }
    }
}
