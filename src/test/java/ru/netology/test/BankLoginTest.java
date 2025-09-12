
package ru.netology.test;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.netology.data.DataHelper;
import ru.netology.data.SQLHelper;
import ru.netology.page.LoginPage;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ru.netology.data.SQLHelper.cleanAuthCodes;
import static ru.netology.data.SQLHelper.cleanDatabase;

public class BankLoginTest {
    private LoginPage loginPage;
    private static final String baseURL = System.getProperty("baseURL", "http://localhost:9999");

    @AfterEach
    public void tearDown() {
        cleanAuthCodes();
        cleanDatabase();
    }

    @BeforeEach
    public void setUp() {
        assertTrue(isServerAvailable(), "Сервер недоступен по адресу: " + baseURL);
        loginPage = open(baseURL, LoginPage.class);
    }

    private boolean isServerAvailable() {
        try {
            URL url = new URL(baseURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("HEAD");
            connection.setConnectTimeout(3000);
            connection.setReadTimeout(3000);
            int responseCode = connection.getResponseCode();
            return (responseCode >= 200 && responseCode < 400);
        } catch (IOException e) {
            return false;
        }
    }

    private void attemptLoginWithRandomUser() {
        var authInfo = DataHelper.generateRandomUser();
        loginPage.validLogin(authInfo);
    }

    @Test
    @DisplayName("Позитивный тест")
    void shouldSuccessfulLogin() {
        var authInfo = DataHelper.getAuthInfo();
        var verificationPage = loginPage.validLogin(authInfo);
        verificationPage.verifyVerificationPageVisibility();
        var verificationCode = SQLHelper.getVerificationCode();
        verificationPage.validVerify(verificationCode.getCode());
    }

    @Test
    @DisplayName("Неверно указано имя пользователя")
    void shouldErrorInvalidLogin() {
        var authInfo = DataHelper.generateRandomUser();
        loginPage.validLogin(authInfo);
        loginPage.verifyErrorNotification("Ошибка! Неверно указан логин или пароль");
    }

    @Test
    @DisplayName("Неверно указан код верификации")
    void shouldInvalidCodes() {
        var authInfo = DataHelper.getAuthInfo();
        var verificationPage = loginPage.validLogin(authInfo);
        verificationPage.verifyVerificationPageVisibility();
        var verificationCode = DataHelper.generateRandomVerificationCode();
        verificationPage.verify(verificationCode.getCode());
        verificationPage.verifyErrorNotification("Ошибка! Неверно указан код! Попробуйте ещё раз.");
    }

    @Test
    @DisplayName("Ввод неправильного пароля 3 раза")
    void shouldLockAfterThreeUnsuccessfulPasswords() {
        for (int i = 0; i < 3; i++) {
            attemptLoginWithRandomUser();
        }
        loginPage.verifyErrorNotification("Превышено максимальное количество попыток авторизации");
    }
}