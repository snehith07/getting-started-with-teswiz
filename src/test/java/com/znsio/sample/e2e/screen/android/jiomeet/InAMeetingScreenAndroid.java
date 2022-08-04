package com.znsio.sample.e2e.screen.android.jiomeet;

import com.znsio.e2e.tools.Driver;
import com.znsio.e2e.tools.Visual;
import com.znsio.sample.e2e.exceptions.jiomeet.InAMeetingException;
import com.znsio.sample.e2e.screen.jiomeet.InAMeetingScreen;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import static com.znsio.e2e.tools.Wait.waitFor;

public class InAMeetingScreenAndroid
        extends InAMeetingScreen {
    private final Driver driver;
    private final Visual visually;
    private final String SCREEN_NAME = InAMeetingScreenAndroid.class.getSimpleName();
    private final Logger LOGGER = Logger.getLogger(SCREEN_NAME);
    private final By micStatusId = By.id("com.jio.rilconferences:id/mic_status_label");
    private final By meetingId = By.id("com.jio.rilconferences:id/caller_number");
    private final By meetingPasswordId = By.id("com.jio.rilconferences:id/caller_password");
    private final By byTopHeaderControlsPanelId = By.id("videoTopLayout1");

    public InAMeetingScreenAndroid(Driver driver, Visual visually) {
        this.driver = driver;
        this.visually = visually;
    }

    @Override
    public boolean isMeetingStarted() {
        try {
            enableInMeetingControls("isMeetingStarted");
            driver.waitTillElementIsPresent(micStatusId);
            return true;
        } catch(Exception e) {
            return false;
        }
    }

    private boolean areInMeetingControlsDisplayed() {
        return driver.isElementPresent(byTopHeaderControlsPanelId);
    }

    @Override
    public String getMeetingId() {
        enableInMeetingControls("getMeetingId");
        return driver.waitTillElementIsPresent(meetingId)
                     .getText()
                     .replace("-", "");
    }

    private void enableInMeetingControls(String calledFrom) {
        LOGGER.info(String.format("enableInMeetingControls: Called from: '%s'%n", calledFrom));
        boolean isTopHeaderDisplayed = areInMeetingControlsDisplayed();
        LOGGER.info(String.format("enableInMeetingControls: Called from: '%s': headers displayed?: '%s'", calledFrom, isTopHeaderDisplayed));
        int retryAttempt = 0;
        if(!isTopHeaderDisplayed) {
            do {
                int seconds = 1;
                LOGGER.info(String.format("enableInMeetingControls: Called from: '%s', ': headers not displayed. Wait for '%d' sec and try again", calledFrom, seconds));
                waitFor(seconds);
                retryAttempt++;
                driver.tapOnMiddleOfScreen();
                isTopHeaderDisplayed = areInMeetingControlsDisplayed();
                LOGGER.info(String.format("enableInMeetingControls: Called from: '%s': retryAttempt: '%d' : are headers displayed now: '%s'", calledFrom, retryAttempt,
                                          isTopHeaderDisplayed));
            } while(!isTopHeaderDisplayed && retryAttempt < 8);
            if(!isTopHeaderDisplayed) {
                throw new InAMeetingException("Unable to see In Meeting Controls called from '" + calledFrom + "' in " + retryAttempt + " retry attempts");
            }
        }
    }

    @Override
    public String getMeetingPassword() {
        enableInMeetingControls("getMeetingPassword");
        return driver.waitTillElementIsPresent(meetingPasswordId)
                     .getText()
                     .replace("Password: ", "");
    }

    @Override
    public InAMeetingScreen unmute() {
        enableInMeetingControls("unmute");
        visually.checkWindow(SCREEN_NAME, "mic should be muted");
        WebElement micStatus = driver.waitTillElementIsPresent(micStatusId);
        LOGGER.info("unmute- current mic status: " + micStatus.getText());
        if(micStatus.getText()
                    .equals("Mute")) {
            throw new InAMeetingException("Mic is already unmuted");
        } else {
            micStatus.click();
        }
        return this;
    }

    @Override
    public InAMeetingScreen mute() {
        enableInMeetingControls("mute");
        visually.checkWindow(SCREEN_NAME, "mic should be unmuted");
        WebElement micStatus = driver.waitTillElementIsPresent(micStatusId);
        LOGGER.info("mute- current mic status: " + micStatus.getText());
        if(micStatus.getText()
                    .equals("Unmute")) {
            throw new InAMeetingException("Mic is already muted");
        } else {
            micStatus.click();
        }
        return this;
    }
}