package com.feedback.tests;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.Select;
import java.io.File;
import java.util.List;

public class FeedbackTest {

  static WebDriver driver;
  static int passed = 0, failed = 0;

  // Resolve form path relative to project root — works locally and in Jenkins
  static String formPath = "file:///" +
      new File("StudentFeedbackFormDevOps/index.html").getAbsolutePath().replace("\\", "/");

  public static void main(String[] args) {

    // ChromeOptions: run headless so it works in Jenkins (no display needed)
    ChromeOptions options = new ChromeOptions();
    options.addArguments("--headless=new");
    options.addArguments("--no-sandbox");
    options.addArguments("--disable-dev-shm-usage");

    // Selenium 4 Manager handles chromedriver automatically — no setProperty needed
    driver = new ChromeDriver(options);
    driver.manage().window().maximize();

    System.out.println("===== SELENIUM TEST REPORT =====");

    test1_FormOpens();
    test2_ValidDataSubmission();
    test3_EmptyFieldsShowErrors();
    test4_InvalidEmailFormat();
    test5_InvalidMobileNumber();
    test6_DropdownSelection();
    test7_ResetButtonClearsForm();
    test8_ShortFeedbackFails();
    test9_MissingGenderFails();

    System.out.println("================================");
    System.out.println("PASSED: " + passed + " | FAILED: " + failed);
    driver.quit();
  }

  // ----------- HELPER METHODS -----------

  static void openForm() {
    driver.get(formPath);
    sleep(500);
  }

  static void fillForm(String name, String email, String mobile,
      String dept, String gender, String feedback) {
    if (name != null)
      driver.findElement(By.id("name")).sendKeys(name);
    if (email != null)
      driver.findElement(By.id("email")).sendKeys(email);
    if (mobile != null)
      driver.findElement(By.id("mobile")).sendKeys(mobile);
    if (dept != null) {
      Select deptSelect = new Select(driver.findElement(By.id("department")));
      deptSelect.selectByValue(dept);
    }
    if (gender != null) {
      List<WebElement> radios = driver.findElements(By.name("gender"));
      for (WebElement r : radios) {
        if (r.getAttribute("value").equals(gender)) r.click();
      }
    }
    if (feedback != null)
      driver.findElement(By.id("feedback")).sendKeys(feedback);
  }

  static String getError(String id) {
    try {
      return driver.findElement(By.id(id)).getText();
    } catch (Exception e) { return ""; }
  }

  static void check(String testName, boolean condition) {
    if (condition) {
      System.out.println("[PASS] " + testName);
      passed++;
    } else {
      System.out.println("[FAIL] " + testName);
      failed++;
    }
  }

  static void sleep(long ms) {
    try { Thread.sleep(ms); } catch (InterruptedException e) {}
  }

  // ----------- TEST CASES -----------

  // TC1: Check that the form page loads
  static void test1_FormOpens() {
    openForm();
    String title = driver.getTitle();
    check("TC1 - Form page opens successfully",
      title.contains("Feedback") || title.contains("Student"));
  }

  // TC2: Fill valid data and submit — should show success alert
  static void test2_ValidDataSubmission() {
    openForm();
    fillForm("John Doe", "john@example.com", "9876543210",
      "CS", "Male",
      "This is my detailed feedback about the course it was great");
    driver.findElement(By.xpath("//button[@type='submit']")).click();
    sleep(500);
    try {
      String alertText = driver.switchTo().alert().getText();
      driver.switchTo().alert().accept();
      check("TC2 - Valid data submits successfully",
        alertText.contains("Form submitted successfully"));
    } catch (Exception e) {
      check("TC2 - Valid data submits successfully", false);
    }
  }

  // TC3: Submit completely empty form — all errors should appear
  static void test3_EmptyFieldsShowErrors() {
    openForm();
    driver.findElement(By.xpath("//button[@type='submit']")).click();
    sleep(300);
    boolean nameErr  = !getError("nameError").isEmpty();
    boolean emailErr = !getError("emailError").isEmpty();
    boolean mobileErr = !getError("mobileError").isEmpty();
    check("TC3 - Empty fields show error messages",
      nameErr && emailErr && mobileErr);
  }

  // TC4: Enter invalid email format
  static void test4_InvalidEmailFormat() {
    openForm();
    fillForm("Jane", "invalidemail", "9876543210",
      "IT", "Female",
      "Feedback about the course it was very informative");
    driver.findElement(By.xpath("//button[@type='submit']")).click();
    sleep(300);
    check("TC4 - Invalid email shows error",
      !getError("emailError").isEmpty());
  }

  // TC5: Enter invalid mobile number (letters or short number)
  static void test5_InvalidMobileNumber() {
    openForm();
    fillForm("Jane", "jane@test.com", "12345abc",
      "IT", "Female",
      "Feedback about the course it was very informative");
    driver.findElement(By.xpath("//button[@type='submit']")).click();
    sleep(300);
    check("TC5 - Invalid mobile shows error",
      !getError("mobileError").isEmpty());
  }

  // TC6: Check dropdown works and can select options
  static void test6_DropdownSelection() {
    openForm();
    Select dept = new Select(driver.findElement(By.id("department")));
    dept.selectByValue("ME");
    sleep(200);
    String selected = dept.getFirstSelectedOption().getAttribute("value");
    check("TC6 - Dropdown selects correct department",
      selected.equals("ME"));
  }

  // TC7: Check Reset button clears the form
  static void test7_ResetButtonClearsForm() {
    openForm();
    fillForm("Test User", "test@test.com", "9999999999",
      "CS", "Male", "Some feedback text");
    driver.findElement(By.xpath("//button[@type='reset']")).click();
    sleep(300);
    String nameValue = driver.findElement(By.id("name")).getAttribute("value");
    check("TC7 - Reset button clears all fields", nameValue.isEmpty());
  }

  // TC8: Feedback too short (less than 10 words)
  static void test8_ShortFeedbackFails() {
    openForm();
    fillForm("Alice", "alice@mail.com", "8888888888",
      "EC", "Female", "Good course");
    driver.findElement(By.xpath("//button[@type='submit']")).click();
    sleep(300);
    check("TC8 - Short feedback shows error",
      !getError("feedbackError").isEmpty());
  }

  // TC9: No gender selected — should show error
  static void test9_MissingGenderFails() {
    openForm();
    fillForm("Bob", "bob@mail.com", "7777777777",
      "CS", null,
      "This is detailed enough feedback with more than ten words");
    driver.findElement(By.xpath("//button[@type='submit']")).click();
    sleep(300);
    check("TC9 - Missing gender shows error",
      !getError("genderError").isEmpty());
  }

}
