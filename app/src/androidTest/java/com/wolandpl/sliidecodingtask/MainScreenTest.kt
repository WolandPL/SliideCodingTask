package com.wolandpl.sliidecodingtask

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.filter
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.longClick
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onChildren
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTouchInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.wolandpl.sliidecodingtask.data.model.ApiUser
import com.wolandpl.sliidecodingtask.ui.compose.ADD_USER_BUTTON_TEST_TAG
import com.wolandpl.sliidecodingtask.ui.compose.ADD_USER_CONFIRM_BUTTON_TEST_TAG
import com.wolandpl.sliidecodingtask.ui.compose.ADD_USER_DIALOG_TEST_TAG
import com.wolandpl.sliidecodingtask.ui.compose.ADD_USER_EMAIL_TEST_TAG
import com.wolandpl.sliidecodingtask.ui.compose.ADD_USER_NAME_TEST_TAG
import com.wolandpl.sliidecodingtask.ui.compose.DATA_COLUMN_TEST_TAG
import com.wolandpl.sliidecodingtask.ui.compose.DATA_ROW_TEST_TAG
import com.wolandpl.sliidecodingtask.ui.compose.DELETE_USER_CONFIRM_BUTTON_TEST_TAG
import com.wolandpl.sliidecodingtask.ui.compose.ERROR_DIALOG_TEST_TAG
import com.wolandpl.sliidecodingtask.ui.compose.LOADING_TEST_TAG
import com.wolandpl.sliidecodingtask.ui.compose.MainScreen
import com.wolandpl.sliidecodingtask.ui.theme.SliideCodingTaskTheme
import com.wolandpl.sliidecodingtask.utils.UsersApiServiceMock
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import javax.inject.Inject
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class MainScreenTest {

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Inject
    lateinit var usersApiService: UsersApiServiceMock

    @Before
    fun setup() {
        hiltRule.inject()
        composeTestRule.setContent {
            SliideCodingTaskTheme {
                MainScreen()
            }
        }
    }

    @Test
    fun loading_isDisplayed_atStartup() {
        composeTestRule.onNodeWithTag(LOADING_TEST_TAG)
            .assertIsDisplayed()
    }

    @Test
    fun userEntries_displayed() {
        waitForMainScreen()

        val dataRows = composeTestRule.onNodeWithTag(DATA_COLUMN_TEST_TAG)
            .onChildren().filter(hasTestTag(DATA_ROW_TEST_TAG))

        dataRows.assertCountEquals(TEST_USERS_COUNT)
    }

    @Test
    fun addUserDialog_isDisplayed() {
        waitForMainScreen()

        with(composeTestRule) {
            onNodeWithTag(ADD_USER_BUTTON_TEST_TAG)
                .performClick()

            onNodeWithTag(ADD_USER_DIALOG_TEST_TAG)
                .assertIsDisplayed()
        }
    }

    @Test
    fun addUserDialog_okButton_isDisabledUntilDataComplete() {
        waitForMainScreen()

        with(composeTestRule) {
            onNodeWithTag(ADD_USER_BUTTON_TEST_TAG)
                .performClick()

            onNodeWithTag(ADD_USER_DIALOG_TEST_TAG)
                .assertIsDisplayed()

            onNodeWithTag(ADD_USER_CONFIRM_BUTTON_TEST_TAG)
                .assertIsNotEnabled()

            onNodeWithTag(ADD_USER_NAME_TEST_TAG)
                .performTextInput("Dummy Name")

            onNodeWithTag(ADD_USER_EMAIL_TEST_TAG)
                .performTextInput("dummy@email.com")

            onNodeWithTag(ADD_USER_CONFIRM_BUTTON_TEST_TAG)
                .assertIsEnabled()
        }
    }

    @Test
    fun errorDialog_displayedOnApiError() {
        waitForMainScreen()

        with(composeTestRule) {
            onNodeWithTag(DATA_COLUMN_TEST_TAG)
                .onChildren().filter(hasTestTag(DATA_ROW_TEST_TAG))
                .onFirst()
                .performTouchInput { longClick() }

            onNodeWithTag(DELETE_USER_CONFIRM_BUTTON_TEST_TAG)
                .performClick()

            // API service mock always returns error for delete
            onNodeWithTag(ERROR_DIALOG_TEST_TAG)
                .assertIsDisplayed()
        }
    }

    private fun waitForMainScreen() {
        usersApiService.loadingFinished()

        composeTestRule.waitUntil {
            composeTestRule.onAllNodesWithTag(DATA_COLUMN_TEST_TAG)
                .fetchSemanticsNodes().size == 1
        }
    }

    companion object {
        private const val TEST_USERS_COUNT = 2

        val dummyUser1 = ApiUser(
            name = "name1",
            email = "email1"
        )

        val dummyUser2 = ApiUser(
            name = "name2",
            email = "email2"
        )
    }
}
