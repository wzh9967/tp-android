package com.tokenbank.activity;

import androidx.test.filters.LargeTest;
import androidx.test.runner.AndroidJUnit4;

import com.tokenbank.R;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(AndroidJUnit4.class)
@LargeTest
public class SplashActivityTest {

    @Test
    public void StartActivity(){
        onView(withId(R.id.tv_create_wallet)).perform(click());
        onView(withId(R.id.tv_import_wallet)).perform(click());
    }



}