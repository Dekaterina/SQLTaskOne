package ru.netology.test;

import org.junit.jupiter.api.*;
import ru.netology.data.DataHelper;
import ru.netology.data.SQLHelper;
import ru.netology.page.LoginPage;

import static com.codeborne.selenide.Selenide.open;
import static ru.netology.data.SQLHelper.cleanAuthCodes;
import static ru.netology.data.SQLHelper.cleanDatabase;

import io.github.bonigarcia.wdm.WebDriverManager;

class BankLoginTest {

    private LoginPage loginPage;

    @AfterEach
    void tearDown() {
        cleanAuthCodes();
    }

    @AfterAll
    static void tearDownAll() {
        cleanDatabase();
    }

    @BeforeEach
    void setUp() {
        WebDriverManager.chromedriver().setup();
        loginPage = open("http://localhost:9999", LoginPage.class);
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
        var authInfo = DataHelper.generateRandomUser();
        loginPage.validLogin(authInfo);
        loginPage.loginField.setValue("");
        loginPage.passwordField.setValue("");

        for (int i = 0; i < 2; i++) {
            authInfo = DataHelper.generateRandomUser();
            loginPage.validLogin(authInfo);
            loginPage.loginField.setValue("");
            loginPage.passwordField.setValue("");
        }

        loginPage.verifyErrorNotification("Превышено максимальное количество попыток авторизации");
    }
}
