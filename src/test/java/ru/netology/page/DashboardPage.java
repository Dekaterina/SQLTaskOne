package ru.netology.page;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;


public class DashboardPage {


    public DashboardPage() {
        SelenideElement heading = $("[data-test-id=dashboard]");
        heading.shouldHave(text("Личный кабинет")).shouldBe(visible);
    }
}