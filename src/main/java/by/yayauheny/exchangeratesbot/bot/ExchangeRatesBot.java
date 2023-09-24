package by.yayauheny.exchangeratesbot.bot;

import by.yayauheny.exchangeratesbot.exception.ServiceException;
import by.yayauheny.exchangeratesbot.service.ExchangeRatesService;
import by.yayauheny.exchangeratesbot.util.DateTimeUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
@Log4j2
public class ExchangeRatesBot extends TelegramLongPollingBot {

    private static final String START = "/start";
    private static final String USD = "/usd";
    private static final String EUR = "/eur";
    private static final String HELP = "/help";
    @Autowired
    private ExchangeRatesService<String> exchangeRatesService;

    public ExchangeRatesBot(@Value("${bot.token}") String botToken) {
        super(botToken);
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return;
        }
        String message = update.getMessage().getText();
        Long chatId = update.getMessage().getChatId();
        switch (message) {
            case START -> startCommand(chatId);
            case USD -> usdCommand(chatId);
            case EUR -> eurCommand(chatId);
            case HELP -> helpCommand(chatId);
            default -> unknownCommand(chatId);
        }
    }

    private void unknownCommand(Long chatId) {
        String response = "Не удалось распознать команду";
        sendMessage(chatId, response);
    }

    private void eurCommand(Long chatId) {
        String response;
        try {
            String eurRate = exchangeRatesService.getEURExchangeRate();
            String valueRateDate = exchangeRatesService.getValueRateLocalDate();
            response = """
                    Дата: %s
                                        
                    Банк покупает евро по %s RUB 
                    """.formatted(valueRateDate,
                    eurRate);
        } catch (ServiceException e) {
            log.error("Cannot receive eur rate", e);
            response = "Не удалось получить текущий курс евро, попробуйте позже";
        }
        sendMessage(chatId, response);
    }

    @Override
    public String getBotUsername() {
        return "exchange_by_bot";
    }

    private void startCommand(Long chatId) {
        String startMessage = """
                 Добро пожаловать в конвертер валют!
                \s
                Здесь Вы сможете узнать официальные курсы валют на сегодня, установленные ЦБ РФ.
                \s
                Для получения актуального курса воспользуйтесь командами:
                /usd - курс доллара
                /eur - курс евро
                \s
                Дополнительные команды:
                /help - получение справки
                  """;
        sendMessage(chatId, startMessage);
    }

    private void usdCommand(Long chatId) {
        String response;
        try {
            String usdRate = exchangeRatesService.getUSDExchangeRate();
            String valueRateDate = exchangeRatesService.getValueRateLocalDate();
            response = """
                    Дата: %s
                                        
                    Банк покупает доллар по  %s RUB
                    """.formatted(valueRateDate,
                    usdRate);
        } catch (ServiceException e) {
            log.error("Cannot receive usd rate", e);
            response = "Не удалось получить текущий курс доллара, попробуйте позже";
        }
        sendMessage(chatId, response);
    }

    private void helpCommand(Long chatId) {
        var text = """
                Справочная информация по боту
                                
                Для получения текущих курсов валют воспользуйтесь командами:
                /usd - курс доллара
                /eur - курс евро
                """;
        sendMessage(chatId, text);
    }

    private void sendMessage(Long chatId, String message) {
        String chatIdStr = String.valueOf(chatId);
        SendMessage sendMessage = new SendMessage(chatIdStr, message);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error("Cannot send message", e);
        }

    }
}
