package by.yayauheny.exchangeratesbot.service.impl;

import by.yayauheny.exchangeratesbot.client.CbrClient;
import by.yayauheny.exchangeratesbot.exception.ServiceException;
import by.yayauheny.exchangeratesbot.service.ExchangeRatesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.StringReader;

@Service
public class ExchangeRatesServiceImpl implements ExchangeRatesService<String> {

    @Autowired
    private CbrClient client;

    private static final String USD_XPATH = "/ValCurs//Valute[@ID='R01235']/Value";
    private static final String EUR_XPATH = "/ValCurs//Valute[@ID='R01239']/Value";
    private static final String DATE_XPATH = "/ValCurs/@Date";

    @Override
    public String getUSDExchangeRate() throws ServiceException {
        String value = extractCurrencyValueFromXml(client.getCurrencyRatesXml(), USD_XPATH);
        return value;
    }

    @Override
    public String getEURExchangeRate() throws ServiceException {
        String value = extractCurrencyValueFromXml(client.getCurrencyRatesXml(), EUR_XPATH);
        return value;
    }

    @Override
    public String getBYNExchangeRate() {
        return null;
    }

    @Override
    public String getValueRateLocalDate() throws ServiceException {
        return extractCurrencyValueFromXml(client.getCurrencyRatesXml(), DATE_XPATH);
    }

    private static String extractCurrencyValueFromXml(String xml, String xpathExpression) throws ServiceException {
        var source = new InputSource(new StringReader(xml));
        try {
            var xpath = XPathFactory.newInstance().newXPath();
            var document = (Document) xpath.evaluate("/", source, XPathConstants.NODE);

            return xpath.evaluate(xpathExpression, document);
        } catch (XPathExpressionException e) {
            throw new ServiceException("Не удалось распарсить XML", e);
        }

    }
}
