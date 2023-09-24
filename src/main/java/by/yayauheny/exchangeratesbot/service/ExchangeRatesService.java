package by.yayauheny.exchangeratesbot.service;

import by.yayauheny.exchangeratesbot.exception.ServiceException;

public interface ExchangeRatesService<T> {

    T getUSDExchangeRate() throws ServiceException;

    T getEURExchangeRate() throws ServiceException;

    T getBYNExchangeRate();

    T getValueRateLocalDate() throws ServiceException;
}
