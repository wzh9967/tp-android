package com.tokenbank.activity;

import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.tokenbank.R;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static java.util.EnumSet.allOf;
import static org.junit.Assert.*;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(AndroidJUnit4.class)
@LargeTest
public class SplashActivityTest {


    @Rule
    public ActivityTestRule<SplashActivity> mActivityRule = new ActivityTestRule(SplashActivity.class);

    @Test
    public void StartActivity(){
        onView(withId(R.id.tv_create_wallet)).perform(click());
        onView(withId(R.id.tv_import_wallet)).perform(click());
    }



}