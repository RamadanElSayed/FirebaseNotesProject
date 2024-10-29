package com.instant.firebasenotesproject.navigation

import HomeScreen
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.instant.firebasenotesproject.addnote.AddNoteScreen
import com.instant.firebasenotesproject.editnote.EditNoteScreen
import com.instant.firebasenotesproject.signin.LoginScreen
import com.instant.firebasenotesproject.signup.SignUpScreen
import com.instant.firebasenotesproject.splash.SplashScreen

// Define the MyApp composable with Column and padding
@Composable
fun AppNavHost(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    Column(
        modifier = modifier
            .fillMaxSize()
    ) {
        // NavHost for handling navigation between screens
        NavHost(
            navController = navController,
            startDestination = SplashRoute
        ) {

            // Splash Screen
            composable<SplashRoute> {
                SplashScreen(
                    onNavigateToLogin = {
                        navController.navigate(SignInRoute) {
                            popUpTo(SplashRoute) { inclusive = true }  // Remove splash from backstack
                        }
                    },
                    onNavigateToHome = { fullName, jobTitle ->
                        navController.navigate(route = HomeRoute(fullName, jobTitle)) {
                            popUpTo(SplashRoute) { inclusive = true }
                            popUpTo(SignUpRoute) { inclusive = true }
                            popUpTo(SignInRoute) { inclusive = true }
                        }
                    }
                )
            }

            // SignIn Screen
            composable<SignInRoute> {
                LoginScreen(
                    onNavigateToSignUp = { navController.navigate(SignUpRoute) },
                    onLoginSuccess = { fullName, jobTitle ->
                        navController.navigate(route = HomeRoute(fullName, jobTitle)) {
                            popUpTo(SignUpRoute) { inclusive = true }
                            popUpTo(SignInRoute) { inclusive = true }
                        }
                    }
                )
            }

            // SignUp Screen
            composable<SignUpRoute> {
                SignUpScreen(
                    onNavigateToLogin = { navController.popBackStack() },
                    onNavigateToHome = { fullName, jobTitle ->
                        navController.navigate(route = HomeRoute(fullName, jobTitle)) {
                            popUpTo(SignUpRoute) { inclusive = true }
                            popUpTo(SignInRoute) { inclusive = true }
                        }
                    }
                )
            }

            // Home Screen
            composable<HomeRoute> { backStackEntry ->
                val homeRoute: HomeRoute = backStackEntry.toRoute()
                HomeScreen(
                    fullName = homeRoute.fullName,
                    title = homeRoute.jobTitle,
                    onAddNote = {
                        // Navigate to AddNote screen, passing fullName and jobTitle
                        navController.navigate(
                            route = AddNoteRoute(
                                homeRoute.fullName,
                                homeRoute.jobTitle
                            )
                        )
                    },
                    onEditNote = { noteId ->
                        // Navigate to EditNote screen, passing noteId, fullName, and jobTitle
                        navController.navigate(
                            route = EditNoteRoute(
                                noteId,
                                homeRoute.fullName,
                                homeRoute.jobTitle
                            )
                        )
                    },
                    onLogout = {
                        navController.navigate(route=SignInRoute) {
                            popUpTo(SplashRoute) { inclusive = true }  // Correct usage of popUpTo
                        }
                    }
                )
            }


            // AddNote Screen
            composable<AddNoteRoute> { backStackEntry ->
                val addNoteRoute: AddNoteRoute = backStackEntry.toRoute()
                AddNoteScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // EditNote Screen
            composable<EditNoteRoute> { backStackEntry ->
                val editNoteRoute: EditNoteRoute = backStackEntry.toRoute()
                EditNoteScreen(
                    noteId = editNoteRoute.noteId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}